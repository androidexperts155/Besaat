package com.deepak.besaat.view.activities.newRequestCourierOverSeas

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.deepak.besaat.R
import com.deepak.besaat.model.GetFairPerMilePojo
import com.deepak.besaat.model.GetNewServiceRequest.NewServiceRequestPojo
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.DateFunctions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.paymentDetailsStore.PaymentDetailsStoreActivity
import com.deepak.besaat.viewModel.NewRequestCourierViewModel
import com.example.possibility.hr.utils.FilesFunctions
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.gson.Gson
import androidx.lifecycle.Observer
import com.deepak.besaat.databinding.ActivityNewRequestCourierOverseasBinding
import kotlinx.android.synthetic.main.activity_new_request_courier_overseas.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class NewRequestCourierOverseasActivity : BaseActivity() {
    lateinit var binding: ActivityNewRequestCourierOverseasBinding
    val viewModel: NewRequestCourierViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val dateFunctions: DateFunctions by inject()
    val sharedPref: SharedPref by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_new_request_courier_overseas)
        init()
        initObserver()
    }

    fun init() {
        viewModel.name.set(intent.getStringExtra("name"))
        viewModel.providerID.set(intent.getStringExtra("id"))
        viewModel.fair.set(0.0)
        viewModel.pickUpCountry.set(intent.getStringExtra("pickupCountry"))
        viewModel.dropCountry.set(intent.getStringExtra("dropCountry"))

        ccpPickup?.setCountryForNameCode(intent.getStringExtra("pickupCountry"))
        ccpDrop?.setCountryForNameCode(intent.getStringExtra("dropCountry"))

        if (intent.getStringExtra("distance") == "" || intent.getStringExtra("distance") == null)
            viewModel.distance.set("0.0")
        else
            viewModel.distance.set(intent.getStringExtra("distance"))

        if (intent.getStringExtra("providerLat") != null && intent.getStringExtra("providerLat") != "") {
            viewModel.dropLatitude.set(intent.getStringExtra("providerLat"))
            viewModel.dropLogitude.set(intent.getStringExtra("providerLng"))
            viewModel.dropAddress.set(intent.getStringExtra("providerAddress"))
            if (intent.getStringExtra("providerAddress") != null) {
                viewModel.dropAddress.set(intent.getStringExtra("providerAddress"))
            } else {
                viewModel.dropAddress.set("N/A")
                getCompleteAddressString(
                    viewModel.dropLatitude.get()!!.toDouble(),
                    viewModel.dropLogitude.get()!!.toDouble()
                )
            }
        }

        viewModel.deliveryType.set(intent.getStringExtra("deliveryType"))

        binding.viewModelNewRequest = viewModel
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = getString(R.string.new_request)
        viewModel.requestType.set("4") // 4 for Overseas Courier

        if (getCurrentCountryCodeString(
                sharedPref.getString(Constants.latitude).toDouble(),
                sharedPref.getString(Constants.longitude).toDouble()
            ) == viewModel.pickUpCountry.get()!!
        ) {
            viewModel.pickUpAddress.set(sharedPref.getString(Constants.ADDRESS))
            viewModel.pickUpLatitude.set(sharedPref.getString(Constants.latitude))
            viewModel.pickUpLogitude.set(sharedPref.getString(Constants.longitude))
        }
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
                viewModel.dropAddress.set(strAdd)
            }
        } catch (e: Exception) {
            Log.w("CurrentAddress", "Canont get Address!")
        }
        return strAdd
    }

    private fun getCurrentCountryCodeString(LATITUDE: Double, LONGITUDE: Double): String {
        var currentCountryCode = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                currentCountryCode = returnedAddress.countryCode
            } else {
                Log.e("CurrentCC", "Cannot get CountryCode!")
                currentCountryCode =
                    "IN"  // to make fool to code and if this function will not work
            }
        } catch (e: Exception) {
            Log.e("CurrentCC", "Cannot get CountryCode!")
            currentCountryCode = "IN"// to make fool to code and if this function will not work
        }
        return currentCountryCode
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
            if (getCurrentCountryCodeString(
                    sharedPref.getString(Constants.latitude).toDouble(),
                    sharedPref.getString(Constants.longitude).toDouble()
                ) == viewModel.pickUpCountry.get()!!
            ) {
                if (sharedPref.getString(Constants.latitude) == viewModel.pickUpLatitude.get() &&
                    sharedPref.getString(Constants.longitude) == viewModel.pickUpLogitude.get()
                ) {
                    initPlaceAutoCompleteActivity(
                        this,
                        Constants.REQUEST_LOCATION,
                        viewModel.pickUpCountry.get()!!
                    )
                } else {
                    ivChangeLocation.setImageResource(R.drawable.ic_location)
                    viewModel.pickUpLatitude.set(sharedPref.getString(Constants.latitude))
                    viewModel.pickUpLogitude.set(sharedPref.getString(Constants.longitude))
                    viewModel.pickUpAddress.set(sharedPref.getString(Constants.ADDRESS))

                    if (viewModel.dropLatitude.get() != null && viewModel.dropLatitude.get() != "") {
                        viewModel.distance.set(
                            getDistance(
                                LatLng(
                                    viewModel.pickUpLatitude.get()!!.toDouble(),
                                    viewModel.pickUpLogitude.get()!!.toDouble()
                                ),
                                LatLng(
                                    viewModel.dropLatitude.get()!!.toDouble(),
                                    viewModel.dropLogitude.get()!!.toDouble()
                                )
                            ).toString()
                        )
                        if (viewModel.distance.get()!!.toFloat() < 1.0f) {
                            viewModel.fair.set(
                                String.format(
                                    "%.2f",
                                    (viewModel.fairPerDistanceUnit.get()!!.toDouble() * 1.0f)
                                ).toDouble()
                            )
                        } else {
                            viewModel.fair.set(
                                String.format(
                                    "%.2f",
                                    (viewModel.fairPerDistanceUnit.get()!!.toDouble() * viewModel.distance.get()!!.toDouble())
                                ).toDouble()
                            )
                        }
//                        viewModel.fair.set(
//                            String.format(
//                                "%.2f",
//                                viewModel.fairPerDistanceUnit.get()!!.toDouble() * viewModel.distance.get()!!.toDouble()
//                            ).toDouble()
//                        )
                    }
                }
            } else {
                initPlaceAutoCompleteActivity(
                    this,
                    Constants.REQUEST_LOCATION,
                    viewModel.pickUpCountry.get()!!
                )
            }

        })

        viewModel.changeDropLocationClick.observe(this, Observer {
            initPlaceAutoCompleteActivity(
                this,
                Constants.REQUEST_DROP_LOCATION,
                viewModel.dropCountry.get()!!
            )
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
                    this@NewRequestCourierOverseasActivity,
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

                    if (viewModel.distance.get()!!.toFloat() < 1.0f) {
                        viewModel.fair.set(
                            String.format(
                                "%.2f",
                                (viewModel.fairPerDistanceUnit.get()!!.toDouble() * 1.0f)
                            ).toDouble()
                        )
                    } else {
                        viewModel.fair.set(
                            String.format(
                                "%.2f",
                                (viewModel.fairPerDistanceUnit.get()!!.toDouble() * viewModel.distance.get()!!.toDouble())
                            ).toDouble()
                        )
                    }

//                    viewModel.fair.set(
//                        String.format(
//                            "%.2f",
//                            (pojo.getFair()!! * viewModel.distance.get()!!.toDouble())
//                        ).toDouble()
//                    )
                } else {
                    viewModel.fairPerDistanceUnit.set(0.0)
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
        var int =
            Intent(this@NewRequestCourierOverseasActivity, PaymentDetailsStoreActivity::class.java)

        int.putExtra("pickAddress", viewModel.pickUpAddress.get())
        int.putExtra("pickLat", viewModel.pickUpLatitude.get())
        int.putExtra("pickLong", viewModel.pickUpLogitude.get())

        if (data.getSocial()?.pickupLatitude == null) {
            data.getSocial()?.pickupLatitude = viewModel.pickUpLatitude.get()
            data.getSocial()?.pickupLongitude = viewModel.pickUpLogitude.get()
            data.getSocial()?.pickupAddress = viewModel.pickUpAddress.get()
        }

        int.putExtra("dropAddress", viewModel.dropAddress.get())
        int.putExtra("dropLat", viewModel.dropLatitude.get())
        int.putExtra("dropLong", viewModel.dropLogitude.get())

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
            this@NewRequestCourierOverseasActivity, date, myCalendar
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
            this@NewRequestCourierOverseasActivity,
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
                viewModel.pickUpLatitude.set(place?.latLng?.latitude?.toString())
                viewModel.pickUpLogitude.set(place?.latLng?.longitude?.toString())
                viewModel.pickUpAddress.set(place.address)

                if (viewModel.dropLatitude.get() != null && viewModel.dropLatitude.get() != "") {
                    viewModel.distance.set(
                        getDistance(
                            place?.latLng!!,
                            LatLng(
                                viewModel.dropLatitude.get()!!.toDouble(),
                                viewModel.dropLogitude.get()!!.toDouble()
                            )
                        ).toString()
                    )
                    if (viewModel.distance.get()!!.toFloat() < 1.0f) {
                        viewModel.fair.set(
                            String.format(
                                "%.2f",
                                (viewModel.fairPerDistanceUnit.get()!!.toDouble() * 1.0f)
                            ).toDouble()
                        )
                    } else {
                        viewModel.fair.set(
                            String.format(
                                "%.2f",
                                (viewModel.fairPerDistanceUnit.get()!!.toDouble() * viewModel.distance.get()!!.toDouble())
                            ).toDouble()
                        )
                    }

//                    viewModel.fair.set(
//                        String.format(
//                            "%.2f",
//                            viewModel.fairPerDistanceUnit.get()!!.toDouble() * viewModel.distance.get()!!.toDouble()
//                        ).toDouble()
//                    )
                }

                if (sharedPref.getString(Constants.latitude) == viewModel.pickUpLatitude.get() &&
                    sharedPref.getString(Constants.longitude) == viewModel.pickUpLogitude.get()
                ) {
                    ivChangeLocation.setImageResource(R.drawable.ic_location)
                } else {
                    ivChangeLocation.setImageResource(R.drawable.location_disabled)
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        } else if (requestCode == Constants.REQUEST_DROP_LOCATION && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                viewModel.dropLatitude.set(place.latLng?.latitude?.toString())
                viewModel.dropLogitude.set(place.latLng?.longitude?.toString())
                viewModel.dropAddress.set(place.address)

                if (viewModel.pickUpLatitude.get() != null && viewModel.pickUpLatitude.get() != "") {
                    viewModel.distance.set(
                        getDistance(
                            place.latLng!!,
                            LatLng(
                                viewModel.pickUpLatitude.get()!!.toDouble(),
                                viewModel.pickUpLogitude.get()!!.toDouble()
                            )
                        ).toString()
                    )

                    if (viewModel.distance.get()!!.toFloat() < 1.0f) {
                        viewModel.fair.set(
                            String.format(
                                "%.2f",
                                (viewModel.fairPerDistanceUnit.get()!!.toDouble() * 1.0f)
                            ).toDouble()
                        )
                    } else {
                        viewModel.fair.set(
                            String.format(
                                "%.2f",
                                (viewModel.fairPerDistanceUnit.get()!!.toDouble() * viewModel.distance.get()!!.toDouble())
                            ).toDouble()
                        )
                    }
//                    viewModel.fair.set(
//                        String.format(
//                            "%.2f",
//                            viewModel.fairPerDistanceUnit.get()!!.toDouble() * viewModel.distance.get()!!.toDouble()
//                        ).toDouble()
//                    )
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
