package com.deepak.besaat.view.activities.addCourierDetail

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.Interfaces.LastLocationValues
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityAddCourierDetailBinding
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.viewModel.AddCourierDetailViewModel
import com.example.possibility.hr.utils.FilesFunctions
import com.facebook.FacebookSdk
import kotlinx.android.synthetic.main.activity_add_courier_detail.*
import kotlinx.android.synthetic.main.activity_select_delivery_types.toolBar
import org.koin.android.ext.android.inject
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddCourierDetailActivity : BaseActivity() {
    lateinit var binding: ActivityAddCourierDetailBinding
    val viewModel: AddCourierDetailViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val dateFunctions: DateFunctions by inject()
    val sharedPref: SharedPref by inject()
    var actualDuration: String = " "
    var totalHour: Int = 0
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_courier_detail)
        binding = DataBindingUtil.setContentView<ActivityAddCourierDetailBinding>(
            this,
            R.layout.activity_add_courier_detail
        )
        binding.addCourierDetailViewModel = viewModel
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = getString(R.string.add_courier_detail)
        initObserver()

        viewModel.localChecked.set(false)
        viewModel.overseasChecked.set(false)
        viewModel.bothChecked.set(false)

        viewModel.departureCountryName = binding.ccpDeparture!!.selectedCountryNameCode
        binding.ccpDeparture!!.setOnCountryChangeListener {
            viewModel.departureCountryName = binding.ccpDeparture!!.selectedCountryNameCode
        }

        viewModel.arrivalCountryName = binding.ccpArrival!!.selectedCountryNameCode
        binding.ccpArrival!!.setOnCountryChangeListener {
            viewModel.arrivalCountryName = binding.ccpArrival!!.selectedCountryNameCode
        }
    }

    fun initObserver() {
        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

        viewModel.progressBar.observe(this, Observer {
            if (it) {
                commonFunctions.showProgressBar(
                    this@AddCourierDetailActivity,
                    getString(R.string.loading)
                )
            } else {
                commonFunctions.hideProgressBar()
            }
        })
        viewModel.departureDateClick.observe(this, Observer {
            viewModel.arrivalTime.set("")
            viewModel.arrivalDate.set("")
            datePickerDialog(System.currentTimeMillis() - 1000, "Departure")
        })

        viewModel.departureTimeClick.observe(this, Observer {
            if (viewModel.departureDate.get() == null || (viewModel.departureDate.get() == "")) {
                commonFunctions.showFeedbackMessage(
                    rootLayout!!,
                    getString(R.string.empty_departure_date)
                )
            } else {
                viewModel.arrivalTime.set("")
                timePickerDialog("Departure")
            }
        })

        viewModel.arrivalDateClick.observe(this, Observer {
            datePickerDialog(dateFunctions.getTimeStamp(viewModel.departureDate.get()!!), "Arrival")
        })

        viewModel.arrivalTimeClick.observe(this, Observer {
            timePickerDialog("Arrival")
        })


        viewModel.onImageClick.observe(this, Observer {
            val permission = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (checkPermission(permission) > 0) {
                ActivityCompat.requestPermissions(
                    this@AddCourierDetailActivity,
                    arrayOf(permission[0], permission[1]),
                    Constants.REQUEST_PERMISSION
                )
            } else {
                dialogForCameraGallery()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (!super.checkPermissions()) {
            super.startLocationPermissionRequest()
        } else {
            super.getLastLocation(object : LastLocationValues {
                override fun LocationValues(
                    lat: Double,
                    long: Double,
                    latString: String,
                    longString: String
                ) {
                    runOnUiThread(Runnable {

                        sharedPref?.setString(Constants.latitude, latString)
                        sharedPref?.setString(Constants.longitude, longString)
                        var countryName = getCurrentCountry(lat, long)
                        if (countryName != null) {
                            var countryCode =
                                getCOuntryCode.getCountryCodeByCountryName(countryName?.toLowerCase())
                            Log.e("CountryName", "country name on singup cactivity " + countryName)
                            //  binding!!.ccp!!.setCountryForNameCode("ar")
                            binding!!.ccpDeparture!!.setCountryForNameCode(countryCode)
                            binding!!.ccpArrival!!.setCountryForNameCode(countryCode)

                        }
                    })
                }
            })
        }
    }

    fun getCurrentCountry(lat: Double, long: Double): String? {
        var country_name: String? = null
        val lm =
            FacebookSdk.getApplicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val geocoder = Geocoder(FacebookSdk.getApplicationContext())

        for (provider in lm.getAllProviders()) {
            // val location = lm.getLastKnownLocation(provider)
            //   if (location != null)
            //   {
            try {
                var addresses: MutableList<Address>? = null

                if (!sharedPref.getString(Constants.latitude).equals("") && !sharedPref.getString(
                        Constants.longitude
                    ).equals("")
                ) {
                    addresses = geocoder.getFromLocation(
                        lat,
                        long,
                        1
                    )
                }
                if (addresses != null && addresses.size > 0) {
                    country_name = addresses.get(0).getCountryName()
                    break
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //}
        }

        return country_name
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i("ocationupdate", "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                // getLastLocation()
            } else {
                super.startLocationPermissionRequest()
            }
        }
    }


    private fun datePickerDialog(minimumMilliSec: Long, type: String) {
        var newCalendar = Calendar.getInstance()
        var datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var monthString = ""
                var dayString = ""
                if (monthOfYear < 9) {
                    monthString = "0" + (monthOfYear + 1)
                } else {
                    monthString = (monthOfYear + 1).toString()
                }
                if (dayOfMonth < 10) {
                    dayString = "0" + dayOfMonth
                } else {
                    dayString = dayOfMonth.toString()
                }

                if (type == "Departure") {
                    viewModel.departureDate.set("$year-$monthString-$dayString")
                } else {
                    viewModel.arrivalTime.set("")
                    viewModel.arrivalDate.set("$year-$monthString-$dayString")
                }
            },
            newCalendar.get(Calendar.YEAR),
            newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = minimumMilliSec
        datePicker.show()
    }


    private fun timePickerDialog(type: String) {
        var mcurrentTime = Calendar.getInstance()
        var hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        var minute = mcurrentTime.get(Calendar.MINUTE)

        var mTimePicker = TimePickerDialog(
            this@AddCourierDetailActivity,
            TimePickerDialog.OnTimeSetListener { p0, selectedHour, minute ->
                //                var newHours = ""
//                var newMinutes = ""
//                if (selectedHour < 10) {
//                    newHours = "0" + selectedHour.toString()
//                } else {
//                    newHours = selectedHour.toString()
//                }
//                if (minute < 10) {
//                    newMinutes = "0" + minute.toString()
//                } else {
//                    newMinutes = minute.toString()
//                }
//                if (type == "Departure") {
//                    viewModel.departureTime.set(dateFunctions.tweleveHoursFormat(newHours + ":" + newMinutes))
//                } else {
//                    var arrivalDate =
//                        viewModel.arrivalDate.get()!! + " " + dateFunctions.tweleveHoursFormat(
//                            newHours + ":" + newMinutes
//                        )
//                    var departureDate =
//                        viewModel.departureDate.get()!! + " " + viewModel.departureTime.get()
//                    viewModel.arrivalTime.set(dateFunctions.tweleveHoursFormat(newHours + ":" + newMinutes))
//                    // if (dateFunctions.isValidTime(arrivalDate, departureDate)) {
//                    if (setDuration()) {
//                        showDialogToSelectArrivalTime(
//                            "Arrival Time",
//                            getString(R.string.arrival_time_must_greater_than_departure)
//                        )
//                        viewModel.arrivalTime.set("")
//                    } else {
//                        if (totalHour < 1) {
//                            showDialogToSelectArrivalTime(
//                                "Arrival Time",
//                                getString(R.string.total_duraton_should_be_greater_than_1_hour)
//                            )
//                            viewModel.arrivalTime.set("")
//                        } else if (totalHour >= 240) {
//                            showDialogToSelectArrivalTime(
//                                "Arrival Time",
//                                "You can't select your trip more than 10 days."
//                            )
//                            viewModel.arrivalTime.set("")
//                        }
//                    }
//                }
                ////////////////////////////////////////////////////////

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


                var selectedDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                selectedDateFormat.timeZone = TimeZone.getDefault()
                var selectedDate: Date? = null
                try {
                    if (type == "Departure")
                        selectedDate =
                            selectedDateFormat.parse(viewModel.departureDate.get() + " " + newHours + ":" + newMinutes)
                    else
                        selectedDate =
                            selectedDateFormat.parse(viewModel.arrivalDate.get() + " " + newHours + ":" + newMinutes)

                    var mCurrentTime = Calendar.getInstance()
                    mCurrentTime.timeInMillis = System.currentTimeMillis()
                    var selectTime = Calendar.getInstance()
                    selectTime.timeInMillis = selectedDate.time

                    if (mCurrentTime.after(selectTime)) {
                        if (type == "Departure")
                            showDialogToSelectArrivalTime(
                                "Departure Time",
                                getString(R.string.past_time)
                            )
                        else
                            showDialogToSelectArrivalTime(
                                "Arrival Time",
                                getString(R.string.past_time)
                            )
                        return@OnTimeSetListener
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (type == "Departure") {
                    viewModel.departureTime.set(dateFunctions.tweleveHoursFormat("$newHours:$newMinutes"))
                    var dateFormat: DateFormat = SimpleDateFormat("h:mm a");
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    var date: Date? = null
                    try {
                        date = dateFormat.parse(viewModel.departureTime.get().toString())
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    var dateFormat2: DateFormat = SimpleDateFormat("HH:mm:ss")
                    dateFormat2.timeZone = TimeZone.getTimeZone("UTC")
                    //DateFormat f2 = new SimpleDateFormat("hh:mm:ss")
//                    departureTimee = dateFormat2.format(date)// "12:18am"
                } else {
                    viewModel.arrivalTime.set(dateFunctions.tweleveHoursFormat("$newHours:$newMinutes"))

                    var dateFormat: DateFormat = SimpleDateFormat("h:mm a");
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC");
                    var date: Date? = null
                    try {
                        date = dateFormat.parse(viewModel.arrivalTime.get().toString())
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    var dateFormat2: DateFormat = SimpleDateFormat("HH:mm:ss");
                    dateFormat2.timeZone = TimeZone.getTimeZone("UTC")
                    //DateFormat f2 = new SimpleDateFormat("hh:mm:ss");
//                    arrivalTimee = dateFormat2.format(date)
                    if (setDuration()) {
                        showDialogToSelectArrivalTime(
                            "Arrival Time",
                            getString(R.string.arrival_time_must_greater_than_departure)
                        )
                        viewModel.arrivalTime.set("")
                    } else {
                        if (totalHour < 1) {
                            showDialogToSelectArrivalTime(
                                "Arrival Time",
                                getString(R.string.total_duraton_should_be_greater_than_1_hour)
                            )
                            viewModel.arrivalTime.set("")
                        } else if (totalHour >= 240) {
                            showDialogToSelectArrivalTime(
                                "Arrival Time",
                                "You can't select your trip more than 10 days."
                            )
                            viewModel.arrivalTime.set("")
                        }
                    }
                }

                /////////////////////////////////////////////////////////
            },
            hour,
            minute,
            false
        )
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    private fun showDialogToSelectArrivalTime(title: String, message: String) {
        val builder = AlertDialog.Builder(this!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
            dialogInterface.dismiss()
            if (title.contains("Arrival"))
                timePickerDialog("Arrival")
            else
                timePickerDialog("Departure")
//            timePickerDialog("Arrival")
        }
        builder.setCancelable(false)
        builder.show()
    }

    fun setDuration(): Boolean {
        var b = false
        var start = viewModel.departureDate.get()!! + " " + viewModel.departureTime.get()
        var end = viewModel.arrivalDate.get()!! + " " + viewModel.arrivalTime.get()
        var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd h:mm a");
        Log.e("Time", "start and end time " + start + " " + end)
        try {
            var dateStart = dateFormat.parse(start);
            var dateEnd = dateFormat.parse(end);
            if (dateStart.after(dateEnd)) {
                b = true;//If start date is before end date
                Log.e("durationhours", "date start before  " + b)
            } else if (dateStart.equals(dateEnd)) {
                b = true;//If two dates are equal
                Log.e("durationhours", "date start equal " + b)
            } else {
                b = false
                Log.e("durationhours", "date start false  " + b)
            }

            var difference: String = printDifference(dateStart, dateEnd)
            if (difference.contains("-")) {
                difference = "0"
            }
            actualDuration = difference.split("Hours")[0]
            Log.e(
                "durationhours",
                "Actual Duration hours of time " + actualDuration + " total hours   is  "
            )
        } catch (e: ParseException) {

        }




        Log.e("durationhours", "duration validation  " + b)
        return b


    }

    private fun printDifference(dateStart: Date, dateEnd: Date): String {
        var mTotalHourSecond = "0"
        var different = dateEnd.getTime() - dateStart.getTime()
        //val differenceprevious = (dateStart.getTime() - previousStartDatelist.get(0).getTime()) as Int

        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli

        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli
        Log.e("elaspeddate", "elapsed dates are " + elapsedDays)
        var totalDays = elapsedDays.toInt()
        Log.e("elaspeddate", "elapsed dates are " + elapsedDays)
        val mTotalHour = (elapsedDays * 24) + elapsedHours
        var totalHours = mTotalHour.toInt()
        totalHour = mTotalHour.toInt()
        val mTotalHourString = (mTotalHour).toString()
        val mTotalSecondString = (elapsedMinutes).toString()
        if (mTotalSecondString.equals("0", ignoreCase = true)) {
            mTotalHourSecond = mTotalHourString + " Hours"
        } else {
            mTotalHourSecond = mTotalHourString + " Hours " + mTotalSecondString + " Minutes"
        }
        return mTotalHourSecond


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
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
