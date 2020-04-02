package com.deepak.besaat.view.fragments.AddTrip


import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager

import com.deepak.besaat.R
import com.deepak.besaat.model.addTripModel
import com.deepak.besaat.model.upcomingTripModel.upcomingTripModel
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.fragments.MyVisitFragment.MyVisitFragment
import com.deepak.besaat.viewModel.PastDetailsViewModel
import com.example.possibility.hr.utils.FilesFunctions
import com.facebook.FacebookSdk.getApplicationContext

import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddTripFragment : Fragment(), View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null
    var rootLayout: ScrollView? = null
    var departureDate: LinearLayout? = null
    var depatureTime: LinearLayout? = null
    var arrivalDate: LinearLayout? = null
    var arrivalTime: LinearLayout? = null
    var departureDateText: TextView? = null
    var departureTimeText: TextView? = null
    var arrivalDateText: TextView? = null
    var arrivalTimeText: TextView? = null
    var textViewContinue: TextView? = null
    var arrivalTimee: String? = null
    var departureTimee: String? = null
    var actualDuration: String = " "
    var mImageFile: File? = null
    var filee: File? = null
    //var imageAddPhoto:ImageView ?= null
    var departureDateString: String = ""
    var arrivalDateString: String = ""
    var imageAddPhoto: ImageView? = null
    var dialog: AlertDialog? = null
    var countryCodePickerDeparture: CountryCodePicker? = null
    var countryCodePickerArrival: CountryCodePicker? = null
    val viewModel: PastDetailsViewModel by inject()
    var fm: FragmentManager? = null
    val dateFunctions: DateFunctions by inject()
    val commonFunctions: CommonFunctions by inject()
    val fragmentFucntion: FragmentFucntions by inject()
    val sharedPref: SharedPref by inject()
    var tripList: MutableList<addTripModel> = ArrayList()
    var textcurrent11: TextView? = null
    var checkClick: Boolean = false
    var totalHour: Int = 0
    var tripData: upcomingTripModel? = null

    var countryArrivalSelected: String? = ""
    var countryDepartureSelected: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_trip, container, false)
        rootLayout = view.findViewById(R.id.rootLayout)
        departureDate = view.findViewById(R.id.departureDate)  // LinearLayout
        depatureTime = view.findViewById(R.id.departureTime)
        arrivalDate = view.findViewById(R.id.arrivalDate)    //LinearLayout
        arrivalTime = view.findViewById(R.id.arrivalTime)
        departureDateText = view.findViewById(R.id.departureDateText)
        departureTimeText = view.findViewById(R.id.departureDateTime)
        arrivalDateText = view.findViewById(R.id.arrivalDateText)
        arrivalTimeText = view.findViewById(R.id.arrivalTimeText)
        countryCodePickerDeparture = view.findViewById(R.id.ccpDeparture)
        countryCodePickerArrival = view.findViewById(R.id.ccpArrival)
        textViewContinue = view.findViewById(R.id.textViewContinue)
        imageAddPhoto = view.findViewById(R.id.add_photo)
        textcurrent11 = view.findViewById(R.id.textCurrentAddress11)
        // HomeActivity.textTop.text="Add My Trip"
        BaseActivity.textViewTitle.text = "Add New Trip"
        BaseActivity.textViewTitle.textSize = 20f
        val permission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (checkPermission(permission) > 0) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(permission[0], permission[1]),
                Constants.REQUEST_PERMISSION
            )
        } else {
            createFile()
        }


        BaseActivity.imageViewMenu.setImageResource(R.drawable.ic_close_black_24dp)


        //  VisitActivity.imageViewTopLeft.setImageResource(R.drawable.ic_close_black_24dp)
        // VisitActivity.textTop.text="Add New Trip"
        /*  activity?.resources?.getColor(R.color.colorWhitishBlue)?.let {
              BaseActivity.topRelative.setBackgroundColor(it)
          }*/
        var countryname = context?.resources?.configuration?.locale?.displayCountry
        Log.e("CountryName", "Country name is " + getCurrentCountry())
        var countryName = getCurrentCountry()
        var countryCode = ""
        if (countryName != null && countryName != "")
            countryCode = getCOuntryCode.getCountryCodeByCountryName(countryName?.toLowerCase())
        countryCodePickerDeparture?.setCountryForNameCode(countryCode)
        countryCodePickerArrival?.setCountryForNameCode(countryCode)
        //String country = .locale.getDisplayCountry()
        countryDepartureSelected = countryCodePickerDeparture?.selectedCountryNameCode
        countryArrivalSelected = countryCodePickerArrival?.selectedCountryNameCode

        countryCodePickerDeparture?.setOnCountryChangeListener {
            countryDepartureSelected = countryCodePickerDeparture?.selectedCountryNameCode
        }
        countryCodePickerArrival?.setOnCountryChangeListener {
            countryArrivalSelected = countryCodePickerArrival?.selectedCountryNameCode
        }
        /*add_photo.setOnClickListener(object:View.OnClickListener{
            override fun onClick(view: View?) {
                val permission = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (checkPermission(permission) > 0) {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(permission[0], permission[1]),
                        Constants.REQUEST_PERMISSION
                    )
                } else {
               //  VisitActivity.super.dialogForCameraGallery()
                }
            }
        })*/
        imageAddPhoto?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val permission = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (checkPermission(permission) > 0) {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(permission[0], permission[1]),
                        Constants.REQUEST_PERMISSION
                    )
                } else {
                    dialogForCameraGallery()
                }
            }
        })
        textViewContinue?.setOnClickListener {
            if (validation()) {
                var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                var date: Date? = null
                try {
                    date = dateFormat.parse(departureDateText?.text.toString())
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                var date2: Date? = null
                try {
                    date2 = dateFormat.parse(arrivalDateText?.text.toString());
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                Log.e("AddTrip", "Add Trip on Date  $date  $date2")
                var departureDate = ""
                var arrivalDate = ""
                if (date != null)
                    departureDate = dateFormat.format(date)
                if (date2 != null)
                    arrivalDate = dateFormat.format(date2)
                Log.e(
                    "ImageFile",
                    "image file on text continue   " + AllLocalHandler.providerSignUpDetailToServer.requestImage + "   " + filee
                )

                if (!setDuration()) {
                    Log.e(
                        "TotalDuration",
                        "total duration is " + totalHour + "   " + (totalHour > 1)
                    )
                    if (totalHour >= 1) {
                        if (!checkClick) {
                            checkClick = true
                            if (tripData == null) {
                                viewModel.AddNewTrip(
                                    Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                                    countryDepartureSelected,
                                    "$departureDate $departureTimee",
                                    "$arrivalDate $arrivalTimee",
                                    countryArrivalSelected,
                                    filee
                                )
                            } else {
                                commonFunctions.showFeedbackMessage(
                                    rootLayout!!,
                                    getString(R.string.edit_in_process)
                                )
                                viewModel.editTrip(
                                    Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                                    tripData?.id.toString(),
                                    countryDepartureSelected,
                                    "$departureDate $departureTimee",
                                    arrivalDate + " " + arrivalTimee,
                                    countryArrivalSelected,
                                    filee
                                )
                            }
                        }
                    } else {
                        commonFunctions.showFeedbackMessage(
                            rootLayout!!,
                            getString(R.string.total_duraton_should_be_greater_than_1_hour)
                        )
                    }
                } else {
                    commonFunctions.showFeedbackMessage(
                        rootLayout!!,
                        "Departure time should not be greater than Arrival time"
                    )
                }
            }
        }
        Log.e(
            "CountryPicker",
            "Country code picker $countryDepartureSelected  $countryArrivalSelected"
        )
        departureDate?.setOnClickListener {
            arrivalTimeText?.text = ""
            arrivalDateText?.text = ""
            departureTimeText?.text = ""
            // viewModel.arrivalTime.set("")
            // viewModel.arrivalDate.set("")
            datePickerDialog(System.currentTimeMillis() - 1000, "Departure")
        }
        depatureTime?.setOnClickListener {
            if ((departureDateText?.text.toString() == "")) {
                commonFunctions.showFeedbackMessage(
                    rootLayout!!,
                    getString(R.string.empty_departure_date)
                )
            } else {
                arrivalTimeText?.text = ""
                timePickerDialog("Departure")
            }
        }

        arrivalDate?.setOnClickListener {

            if ((departureDateText?.text.toString() == "") || (departureTimeText?.text.toString() == "")) {
                commonFunctions.showFeedbackMessage(
                    rootLayout!!,
                    getString(R.string.empty_departure_date_time)
                )
            } else {
                datePickerDialog(
                    dateFunctions.getTimeStamp(departureDateText?.text.toString()),
                    "Arrival"
                )
            }
        }
        arrivalTime?.setOnClickListener {
            if ((departureDateText?.text.toString() == "") || (departureTimeText?.text.toString() == "")) {
                commonFunctions.showFeedbackMessage(
                    rootLayout!!,
                    getString(R.string.empty_departure_date_time)
                )
            } else {
                if ((arrivalDateText?.text.toString() == "")) {
                    commonFunctions.showFeedbackMessage(
                        rootLayout!!,
                        getString(R.string.empty_arrival_date)
                    )
                } else {
                    timePickerDialog("Arrival")
                }
            }
        }
        BaseActivity.imageViewMenu.setOnClickListener {
            //                var myVisitFragment = MyVisitFragment.newInstance(tripList)
            fragmentFucntion.popFragment(activity as AppCompatActivity)
            /* var count: Int? =fm?.getBackStackEntryCount()?.toInt()
                         if(fm!=null) {
                             while (fm?.getBackStackEntryCount()?.toInt()!! > 0) {
                                 fm?.popBackStackImmediate();
                             }
                         }*/
        }
        initViewModel()
        // Inflate the layout for this fragment
        return view
    }

    private fun showDialogToSelectArrivalTime(title: String, message: String) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
            dialogInterface.dismiss()
            if (title.contains("Arrival"))
                timePickerDialog("Arrival")
            else
                timePickerDialog("Departure")
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun createFile() {
        filee =
            File(activity?.getExternalFilesDir(null)?.getAbsolutePath() + "/" + File.separator + "test.txt")
        filee?.createNewFile()
        val data1 = byteArrayOf(1, 1, 0, 0)
        if (filee?.exists()!!) {
            val fo = FileOutputStream(filee)
            fo.write(data1)
            fo.close()
            println("file created: " + filee)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        if (arguments != null) {
            tripData = arguments!!.getSerializable("data") as upcomingTripModel?

            if (tripData != null) {
                BaseActivity.textViewTitle.text = "Edit Trip"
                BaseActivity.textViewTitle.textSize = 20f
//                departureDateText?.text=tripData?.updatedDate
//                arrivalDateText?.text=tripData?.createdDate

                var depDate: Date? = null
                var arvDate: Date? = null
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                try {
                    depDate = sdf.parse(tripData?.updatedDate)
                    arvDate = sdf.parse(tripData?.createdDate)
                } catch (ex: ParseException) {
                    Log.e("exp", "" + ex.message)
                }
                sdf.applyPattern("yyyy-MM-dd")
                departureDateText?.text = "" + sdf.format(depDate)
                arrivalDateText?.text = "" + sdf.format(arvDate)

                sdf.applyPattern("hh:mm a")

                departureTimeText?.text = sdf.format(depDate)
                arrivalTimeText?.text = sdf.format(arvDate)

                var departureDateTime = tripData?.updatedDate?.split(("\\s+").toRegex())
                var arrivalDateTime = tripData?.createdDate?.split(("\\s+").toRegex())

                departureTimee = departureDateTime?.get(1)
                arrivalTimee = arrivalDateTime?.get(1)

                countryCodePickerDeparture?.setCountryForNameCode(tripData?.departureCountry)
                countryCodePickerArrival?.setCountryForNameCode(tripData?.arrivalCountry)

                countryDepartureSelected = tripData?.departureCountry
                countryArrivalSelected = tripData?.arrivalCountry


                if (tripData?.imageUrl != null && tripData?.imageUrl != "") {
                    Picasso.get()
                        .load(tripData?.imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(imageAddPhoto)

                    textcurrent11?.text = "Image"
                }
            }
        }
    }


    private fun initViewModel() {
        viewModel.jsonResponse.observe(this, androidx.lifecycle.Observer {
            Log.e("TripResponse", "trip response is  " + it)
            textViewContinue?.isEnabled = true
            checkClick = false
            var Object = it?.optJSONObject("data")
            if (Object != null) {
                var userID = Object?.getLong("user_id")
                var arrivalCountry = Object?.getString("arrival_country")
                var departureCountry = Object?.getString("departure_country")
                var imageUrl = Object?.getString("image")
                // var status=Object?.getString("status")
                var addTip = addTripModel()
                addTip.arrivalCountryl = arrivalCountry.toString()
                addTip.departureCountry = departureCountry.toString()
                addTip.userID = userID.toString()
                addTip.imageUrl = imageUrl.toString()
                // addTip.status=status.toString()
                tripList.add(addTip)
            }
            //  CountryUtil(activity).setTitle("").build()

            if (it?.getBoolean("status") == true) {
                var myVisitFragment = MyVisitFragment.newInstance(tripList)
                fragmentFucntion.replaceFragment(activity, myVisitFragment, R.id.container)
            }
        })

        viewModel.editJsonResponse.observe(this, androidx.lifecycle.Observer {
            Log.e("TripResponse", "trip response is  " + it)
            textViewContinue?.isEnabled = true
            checkClick = false
            var Object = it?.optJSONObject("data")
            if (Object != null) {
                var userID = Object?.getLong("user_id")
                var arrivalCountry = Object?.getString("arrival_country")
                var departureCountry = Object?.getString("departure_country")
                var imageUrl = Object?.getString("image")
                // var status=Object?.getString("status")
                var addTip = addTripModel()
                addTip.arrivalCountryl = arrivalCountry.toString()
                addTip.departureCountry = departureCountry.toString()
                addTip.userID = userID.toString()
                addTip.imageUrl = imageUrl.toString()
                // addTip.status=status.toString()
                tripList.add(addTip)
            }
            //  CountryUtil(activity).setTitle("").build()

            if (it?.getBoolean("status") == true) {
                var myVisitFragment = MyVisitFragment.newInstance(tripList)
                fragmentFucntion.replaceFragment(activity, myVisitFragment, R.id.container)
            }
        })


    }

    private fun validation(): Boolean {
        val paramBool: Boolean

        if (departureDateText?.text.toString() == "") {
            paramBool = false
            commonFunctions.showFeedbackMessage(rootLayout!!, "please fill departure date")
            return paramBool
        }
        if (departureTimeText?.text.toString() == "") {
            paramBool = false
            commonFunctions.showFeedbackMessage(rootLayout!!, "please fill departure time")
            return paramBool
        }
        if (arrivalTimeText?.text.toString() == "") {
            paramBool = false
            commonFunctions.showFeedbackMessage(rootLayout!!, "please fill arrival time")
            return paramBool
        }
        if (arrivalDateText?.text.toString() == "") {
            paramBool = false
            commonFunctions.showFeedbackMessage(rootLayout!!, "please fill arrival date")
            return paramBool
        }

        /*  if(!radioButtonAutoTransmission1.isChecked || !radioButtonManualTransmission1.isChecked){
              paramBool=false
              Toast.makeText(this,"please select any transmision ",Toast.LENGTH_LONG).show()
              return paramBool

          }*/

        paramBool = true
        return paramBool
    }

    fun checkPermission(permission: Array<String>): Int {
        var permissionNeeded = 0
        if (Build.VERSION.SDK_INT >= 23) {
            for (i in permission.indices) {
                val result = ContextCompat.checkSelfPermission(activity!!, permission[i])
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionNeeded++
                }
            }
        }/*else{

            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.setData(Uri.parse("package:" + context?.getPackageName()))
            activity?.startActivityForResult(intent, 132);
        }*/
        return permissionNeeded
    }

    fun dialogForCameraGallery() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val view = LayoutInflater.from(activity).inflate(R.layout.popup_camera_gallery, null)
        val mTextViewGallery = view.findViewById<TextView>(R.id.textViewGallery)
        val mTextViewTakePic = view.findViewById<TextView>(R.id.textViewTakePic)
        val mTextViewCancel = view.findViewById<TextView>(R.id.textViewCancel)
        mTextViewGallery.setOnClickListener(this)
        mTextViewTakePic.setOnClickListener(this)
        mTextViewCancel.setOnClickListener(this)
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.show()
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun datePickerDialog(minimumMilliSec: Long, type: String) {
        var newCalendar = Calendar.getInstance()
        var datePicker = activity?.let {
            DatePickerDialog(
                it,
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
                        departureDateText?.text =
                            year.toString() + "-" + monthString + "-" + dayString
                        //   departureDateString=  dateFormat.format(departureDateText?.text.toString())
                        //viewModel.departureDate.set(year.toString() + "-" + monthString + "-" + dayString)
                        //viewModel.departureDate.set(year.toString() + "-" + monthString + "-" + dayString)
                    } else {
                        arrivalDateText?.text =
                            year.toString() + "-" + monthString + "-" + dayString
                        arrivalTimeText?.text = ""
                        //  arrivalDateString= dateFormat.format(arrivalDateText?.text.toString())
                        //viewModel.arrivalDate.set(year.toString() + "-" + monthString + "-" + dayString)
                    }
                },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH)
            )
        }
        datePicker?.datePicker?.minDate = minimumMilliSec
        datePicker?.show()
    }

    private fun timePickerDialog(type: String) {
        var mcurrentTime = Calendar.getInstance()
        var hours = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        var minutes = mcurrentTime.get(Calendar.MINUTE)

        var mTimePicker = TimePickerDialog(
            activity,
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


                var selectedDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                selectedDateFormat.timeZone = TimeZone.getDefault()
                var selectedDate: Date? = null
                try {
                    if (type == "Departure")
                        selectedDate =
                            selectedDateFormat.parse(departureDateText?.text.toString() + " " + newHours + ":" + newMinutes)
                    else
                        selectedDate =
                            selectedDateFormat.parse(arrivalDateText?.text.toString() + " " + newHours + ":" + newMinutes)

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
                    departureTimeText?.text =
                        dateFunctions.tweleveHoursFormat("$newHours:$newMinutes")
                    var dateFormat: DateFormat = SimpleDateFormat("h:mm a");
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    var date: Date? = null
                    try {
                        date = dateFormat.parse(departureTimeText?.text.toString())
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    var dateFormat2: DateFormat = SimpleDateFormat("HH:mm:ss")
                    dateFormat2.timeZone = TimeZone.getTimeZone("UTC")
                    //DateFormat f2 = new SimpleDateFormat("hh:mm:ss")
                    departureTimee = dateFormat2.format(date)// "12:18am"
                } else {
                    arrivalTimeText?.text =
                        dateFunctions.tweleveHoursFormat(newHours + ":" + newMinutes)

                    var dateFormat: DateFormat = SimpleDateFormat("h:mm a");
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC");
                    var date: Date? = null
                    try {
                        date = dateFormat.parse(arrivalTimeText?.text.toString());
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    var dateFormat2: DateFormat = SimpleDateFormat("HH:mm:ss");
                    dateFormat2.timeZone = TimeZone.getTimeZone("UTC")
                    //DateFormat f2 = new SimpleDateFormat("hh:mm:ss");
                    arrivalTimee = dateFormat2.format(date)
                    if (setDuration()) {
                        showDialogToSelectArrivalTime(
                            "Arrival Time",
                            getString(R.string.arrival_time_must_greater_than_departure)
                        )
                        arrivalTimeText?.text = ""
                    } else {
                        if (totalHour < 1) {
                            showDialogToSelectArrivalTime(
                                "Arrival Time",
                                getString(R.string.total_duraton_should_be_greater_than_1_hour)
                            )
                            arrivalTimeText?.text = ""
                        } else if (totalHour >= 240) {
                            showDialogToSelectArrivalTime(
                                "Arrival Time",
                                "You can't select your trip more than 10 days."
                            )
                            arrivalTimeText?.text = ""
                        }
                    }
                }
            },
            hours,
            minutes,
            false
        )
        mTimePicker.setTitle("Select Time");
        mTimePicker.show()
    }

    private fun setDuration(): Boolean {
        var b = false
        var start = departureDateText?.text.toString() + " " + departureTimeText?.text.toString()
        var end = arrivalDateText?.text.toString() + " " + arrivalTimeText?.text.toString()
        var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd h:mm a");
        Log.e("Time", "start and end time " + start + " " + end)
        try {
            var dateStart = dateFormat.parse(start)
            var dateEnd = dateFormat.parse(end)
            if (dateStart.after(dateEnd)) {
                b = true//If start date is before end date
                Log.e("durationhours", "date start before  " + b)
            } else if (dateStart == dateEnd) {
                b = true//If two dates are equal
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
                "Actual Duration hours of time " + actualDuration + " total hours   is  " + totalHour
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

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.textViewGallery -> {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, Constants.GALLERY_INTENT)
                dialog!!.dismiss()
            }

            R.id.textViewTakePic -> {
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    mImageFile = FilesFunctions.createImageFile()
                    cameraIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(
                            activity!!,
                            activity!!.packageName + ".provider",
                            mImageFile!!
                        )
                    )
                    startActivityForResult(cameraIntent, Constants.CAMERA_INTENT)
                } else {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, Constants.CAMERA_INTENT)
                }
                dialog!!.dismiss()
            }
            R.id.textViewCancel -> {
                dialog!!.dismiss()
            }
        }
    }

    private fun getCurrentCountry(): String? {
        var country_name: String? = null
        val lm =
            getApplicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val geocoder = Geocoder(getApplicationContext())

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
                        sharedPref.getString(Constants.latitude).toDouble(),
                        sharedPref.getString(Constants.longitude).toDouble(),
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
        }
        return country_name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GALLERY_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    //   imageViewLicense.setImageBitmap(data?.extras?.get("data") as Bitmap)
                    //  add_photo.setImageURI(data?.data)
                    // Log.e("Data","data is  "+data?.data.toString() +"    "+data?.data.toString().reversed())
                    var path = FilesFunctions.getPathFromData(activity!!, data!!)
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(
                            activity!!.getContentResolver(),
                            data!!.data
                        );
                    var newBitMap = FilesFunctions.changeImageOrientation(path, bitmap)
                    mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                    filee = mImageFile as File
                    //viewModel.file = mImageFile
                    if (newBitMap != null) {
                        imageAddPhoto?.setImageBitmap(newBitMap)
                    }
                    textcurrent11?.text =
                        bitmap.toString().substring(bitmap.toString().indexOf("@"))
                    Log.e("imageFile", "image fiel is ")
                    AllLocalHandler.providerSignUpDetailToServer.requestImage = mImageFile
                    Log.e(
                        "ImageFile",
                        "image fiel is " + AllLocalHandler.providerSignUpDetailToServer.requestImage + "   " + filee
                    )
                }
            }
        }
        if (requestCode == Constants.CAMERA_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (mImageFile != null) {
                    val bitmap = BitmapFactory.decodeFile(mImageFile?.getPath())
                    var path: String? = mImageFile?.path
                    var newBitMap: Bitmap? = null
                    if (path != null) {
                        var newBitMap = FilesFunctions.changeImageOrientation(path!!, bitmap)
                        mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                        filee = mImageFile as File
                        //   viewModel.file = mImageFile
                        imageAddPhoto?.setImageBitmap(newBitMap)
                        textcurrent11?.text =
                            bitmap.toString().substring(bitmap.toString().indexOf("@") + 2)
                        AllLocalHandler.providerSignUpDetailToServer.requestImage = mImageFile
                    }
                } else {
                    if (data != null) {
                        val thumbnail = data!!.extras!!.get("data") as Bitmap
                        mImageFile = FilesFunctions.createFileFromBitMap(thumbnail)
                        filee = mImageFile as File
                        AllLocalHandler.providerSignUpDetailToServer.requestImage = mImageFile
                        var resizedBitMap = FilesFunctions.getResizedBitmap(thumbnail, 200, 200)
                        imageAddPhoto?.setImageBitmap(resizedBitMap)
                        textcurrent11?.text =
                            resizedBitMap.toString()
                                .substring(resizedBitMap.toString().indexOf("@") + 2)
                    } else {
                        commonFunctions.showFeedbackMessage(
                            rootLayout!!,
                            "Camera not supported. Please pic image from gallery."
                        )
                    }
                }
            }
        }
        if (requestCode == 132) {
            if (resultCode == Activity.RESULT_OK) {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseActivity.appBarLayout.background = null
    }


    companion object {
        @JvmStatic
        fun newInstance(): AddTripFragment {
            var fragment: AddTripFragment = AddTripFragment()
            // fragment.toolBar=toolBar
            return fragment
        }
    }
}
