package com.deepak.besaat.view.activities.newRequestService

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityNewRequestServiceBinding
import com.deepak.besaat.model.GetFairPerMilePojo
import com.deepak.besaat.model.GetNewServiceRequest.NewServiceRequestPojo
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.DateFunctions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.paymentDetailsStore.PaymentDetailsStoreActivity
import com.deepak.besaat.viewModel.NewRequestServiceViewModel
import com.example.possibility.hr.utils.FilesFunctions
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.gson.Gson
import io.nlopez.smartlocation.OnReverseGeocodingListener
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_new_request_service.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class NewRequestService : BaseActivity() {
    lateinit var binding: ActivityNewRequestServiceBinding
    val viewModel: NewRequestServiceViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val dateFunctions: DateFunctions by inject()
    val sharedPref: SharedPref by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_request_service)

        init()
        initObserver()
    }

    fun init() {
        viewModel.name.set(intent.getStringExtra("name"))
        viewModel.providerID.set(intent.getStringExtra("id"))
        if (intent.getStringExtra("distance") == "")
            viewModel.distance.set("0.0")
        else
            viewModel.distance.set(intent.getStringExtra("distance"))

        viewModel.latitudeProvider.set(intent.getStringExtra("providerLat"))
        viewModel.logitudeProvider.set(intent.getStringExtra("providerLng"))

        if (intent.getStringExtra("providerLat") != null && intent.getStringExtra("providerLat") != "") {
            if (intent.getStringExtra("providerAddress") != null) {
                viewModel.addressProvider.set(intent.getStringExtra("providerAddress"))
            } else {
                viewModel.addressProvider.set("N/A")
                getCompleteAddressString(
                    viewModel.latitudeProvider.get()!!.toDouble(),
                    viewModel.logitudeProvider.get()!!.toDouble()
                )
            }
        }
        viewModel.fair.set(0.0)

        binding.viewModelNewRequestService = viewModel
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = getString(R.string.new_request)
        viewModel.requestType.set("2") // 2 for service
        viewModel.address.set(sharedPref.getString(Constants.ADDRESS))
        viewModel.latitude.set(sharedPref.getString(Constants.latitude))
        viewModel.logitude.set(sharedPref.getString(Constants.longitude))
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")

                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                viewModel.addressProvider.set(strAdd)
            }
        } catch (e: Exception) {
            Log.w("CurrentAddress", "Canont get Address!")
        }
        return strAdd
    }

    fun initObserver() {
        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.hideProgressBar()
                commonFunctions.showProgressBar(this, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.feedBackMessage.observe(this, Observer {
            if (it != null)
                commonFunctions.showFeedbackMessage(rootLayout, it)
            else
                commonFunctions.showFeedbackMessage(rootLayout, "Something wrong.")
        })

        viewModel.changeLocationClick.observe(this, Observer {
            if (sharedPref.getString(Constants.latitude) == viewModel.latitude.get() &&
                sharedPref.getString(Constants.longitude) == viewModel.logitude.get()
            ) {
                initPlaceAutoCompleteActivity(this, Constants.REQUEST_LOCATION,"")
            } else {
                ivChangeLocation.setImageResource(R.drawable.ic_location)
                viewModel.latitude.set(sharedPref.getString(Constants.latitude))
                viewModel.logitude.set(sharedPref.getString(Constants.longitude))
                viewModel.address.set(sharedPref.getString(Constants.ADDRESS))


                viewModel.distance.set(
                    getDistance(
                        LatLng(
                            viewModel.latitudeProvider.get()!!.toDouble(),
                            viewModel.logitudeProvider.get()!!.toDouble()
                        ),
                        LatLng(
                            viewModel.latitude.get()!!.toDouble(),
                            viewModel.logitude.get()!!.toDouble()
                        )
                    ).toString()
                )
                viewModel.fair.set(
                    String.format(
                        "%.2f",
                        viewModel.fairPerDistanceUnit.get()!!.toDouble() * viewModel.distance.get()!!.toDouble()
                    ).toDouble()
                )
            }
        })

        viewModel.onTimeClick.observe(this, Observer {
            if (it)
                timePicker()
        })

        viewModel.onDateClick.observe(this, Observer {
            if (it)
                datePickerDialog()
        })

//        viewModel.confirmClick.observe(this, Observer {
//            if (it)
//                viewModel.executeCreateRequest()
//        })

        viewModel.onImageClick.observe(this, Observer {
            val permission = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (checkPermission(permission) > 0) {
                ActivityCompat.requestPermissions(
                    this@NewRequestService,
                    arrayOf(permission[0], permission[1]),
                    Constants.REQUEST_PERMISSION
                )
            } else {
                dialogForCameraGallery()
            }
        })

        viewModel.successfullyCreateRequest.observe(this, Observer {
            if (it != null) {
                var pojo = Gson().fromJson(it, NewServiceRequestPojo::class.java)
                if (pojo.getStatus() != null && pojo.getStatus()!!) {
                    commonFunctions.showFeedbackMessage(rootLayout, pojo.getMessage()!!)
                    intentToNextPaymentScreen(pojo)
                } else if (pojo.getMessage() != null) {
                    commonFunctions.showFeedbackMessage(rootLayout, pojo.getMessage()!!)
                } else {
                    commonFunctions.showFeedbackMessage(rootLayout, it.toString())
                }
            } else {
                commonFunctions.showFeedbackMessage(rootLayout, "Something wrong.")
            }
        })

        viewModel.fairJsonResponse.observe(this, Observer {
            Log.e("fair", "" + it)
            var pojo = Gson().fromJson(it.toString(), GetFairPerMilePojo::class.java)
            if (pojo.getStatus() != null && pojo.getStatus()!!) {
                if (pojo.getFair() != null) {
                    viewModel.fairPerDistanceUnit.set(pojo.getFair())
                    viewModel.fair.set(
                        String.format(
                            "%.2f",
                            (pojo.getFair()!! * viewModel.distance.get()!!.toDouble())
                        ).toDouble()
                    )
                } else {
                    viewModel.fair.set(0.0)
                }
//                commonFunctions.showFeedbackMessage(
//                    rootLayout,
//                    "Estimated charges " + viewModel.fair.get()
//                )
            }
        })

        viewModel.getFairPerMile()
    }

    private fun intentToNextPaymentScreen(data: NewServiceRequestPojo) {
        var int = Intent(this@NewRequestService, PaymentDetailsStoreActivity::class.java)
        int.putExtra("pickAddress", viewModel.addressProvider.get())
        int.putExtra("pickLat", viewModel.latitudeProvider.get())
        int.putExtra("pickLong", viewModel.logitudeProvider.get())

        if (data.getSocial()?.pickupLatitude == null) {
            data.getSocial()?.pickupLatitude = viewModel.latitudeProvider.get()
            data.getSocial()?.pickupLongitude = viewModel.logitudeProvider.get()
            data.getSocial()?.pickupAddress = viewModel.addressProvider.get()
        }

        int.putExtra("dropAddress", viewModel.address.get())
        int.putExtra("dropLat", viewModel.latitude.get())
        int.putExtra("dropLong", viewModel.logitude.get())

        int.putExtra("orderinfo", viewModel.serviceInfo.get())
        int.putExtra("specialnote", viewModel.specialNote.get())
        int.putExtra("fair", viewModel.fairPerDistanceUnit.get().toString())
        int.putExtra("fairAmount", viewModel.fair.get().toString())
        int.putExtra("orderId", data.getSocial()?.id)

        int.putExtra("from", "service")
        int.putExtra("data", data.getSocial())
        //int.putExtra("radiusValue",radiusValue)
        startActivity(int)
    }

    private fun datePickerDialog() {
        val myCalendar = Calendar.getInstance()
        val date = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

            private fun updateLabel() {
                val myFormat = "yyyy-MM-dd" // to show on screen
                var sdf = SimpleDateFormat(myFormat, Locale.US)
                viewModel.serviceDateShow.set("" + sdf.format(myCalendar.time))

                val myFormat2 = "yyyy-MM-dd" // to send on server
                sdf = SimpleDateFormat(myFormat2, Locale.US)
                viewModel.serviceDate.set("" + sdf.format(myCalendar.time))
            }
        }
        val datePickerDialog = DatePickerDialog(
            this@NewRequestService, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
    }


    private fun timePicker() {
        var mcurrentTime = Calendar.getInstance()
        var hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        var minute = mcurrentTime.get(Calendar.MINUTE)

        var mTimePicker = TimePickerDialog(
            this@NewRequestService,
            TimePickerDialog.OnTimeSetListener { p0, selectedHour, minute ->
                var newHours = ""
                var newMinutes = ""
                if (selectedHour < 10) {
                    newHours = "0$selectedHour"
                } else {
                    newHours = selectedHour.toString()
                }
                if (minute < 10) {
                    newMinutes = "0$minute"
                } else {
                    newMinutes = minute.toString()
                }
                viewModel.serviceTime.set(dateFunctions.tweleveHoursFormat("$newHours:$newMinutes"))
                viewModel.serviceTimeShow.set(dateFunctions.tweleveHoursFormat("$newHours:$newMinutes"))
            },
            hour,
            minute,
            false
        )
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_LOCATION && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                viewModel.latitude.set(place?.latLng?.latitude?.toString())
                viewModel.logitude.set(place?.latLng?.longitude?.toString())
                viewModel.address.set(place.address)

                viewModel.distance.set(
                    getDistance(
                        place.latLng!!,
                        LatLng(
                            viewModel.latitudeProvider.get()!!.toDouble(),
                            viewModel.logitudeProvider.get()!!.toDouble()
                        )
                    ).toString()
                )

                viewModel.fair.set(
                    String.format(
                        "%.2f",
                        viewModel.fairPerDistanceUnit.get()!!.toDouble() * viewModel.distance.get()!!.toDouble()
                    ).toDouble()
                )


                if (sharedPref.getString(Constants.latitude) == viewModel.latitude.get() &&
                    sharedPref.getString(Constants.longitude) == viewModel.logitude.get()
                ) {
                    ivChangeLocation.setImageResource(R.drawable.ic_location)
                } else {
                    ivChangeLocation.setImageResource(R.drawable.location_disabled)
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        } else if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.CAMERA_INTENT -> {
                    if (mImageFile != null) {
                        val bitmap = BitmapFactory.decodeFile(mImageFile!!.getPath())
                        var newBitMap =
                            FilesFunctions.changeImageOrientation(mImageFile!!.path, bitmap)
                        mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                        viewModel.file = mImageFile
                        imageViewLicense.setImageBitmap(newBitMap)
                        AllLocalHandler.providerSignUpDetailToServer.licenseImage = mImageFile
                    } else {
                        if (data != null) {
                            val thumbnail = data!!.extras!!.get("data") as Bitmap
//                        circularImageView!!.setImageBitmap(thumbnail)
                            mImageFile = FilesFunctions.createFileFromBitMap(thumbnail)
                            viewModel.file = mImageFile
                            AllLocalHandler.providerSignUpDetailToServer.licenseImage = mImageFile
                            var resizedBitMap = FilesFunctions.getResizedBitmap(thumbnail, 200, 200)
                            imageViewLicense.setImageBitmap(resizedBitMap)
                        } else {
                            commonFunctions.showFeedbackMessage(
                                rootLayout!!,
                                "Camera not supported. Please pic image from gallery."
                            )
                        }
                    }
                }

                Constants.GALLERY_INTENT -> {
                    var path = FilesFunctions.getPathFromData(this!!, data!!)
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(this!!.getContentResolver(), data!!.data);
                    var newBitMap = FilesFunctions.changeImageOrientation(path, bitmap)
                    mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                    viewModel.file = mImageFile
                    imageViewLicense.setImageBitmap(newBitMap)
                    AllLocalHandler.providerSignUpDetailToServer.licenseImage = mImageFile
                }
            }
        }
    }
}
