package com.deepak.besaat.view.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.deepak.besaat.Interfaces.LastLocationValues
import com.deepak.besaat.Interfaces.imageIconClick
import com.deepak.besaat.Interfaces.searchTextChange
import com.deepak.besaat.R
import com.deepak.besaat.utils.Constants

import com.deepak.besaat.view.activities.addCourierDetail.AddCourierDetailActivity
import com.deepak.besaat.view.activities.addServiceDetail.AddServicesDetailActvity
import com.deepak.besaat.view.activities.cancelOrderRequest.ui.CancelOrderRequestActivity
import com.deepak.besaat.view.activities.chat.ui.ChatActivity
import com.deepak.besaat.view.activities.courierGuysListing.CourierGuysListing
import com.deepak.besaat.view.activities.createprofile.CreateProfileActivity
import com.deepak.besaat.view.activities.deliveryPersonDetail.DeliveryPersonDetail
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.activities.jobDetails.ui.JobDetailsActivity
import com.deepak.besaat.view.activities.newRequestCourier.NewRequestCourier
import com.deepak.besaat.view.activities.newRequestCourierOverSeas.NewRequestCourierOverseasActivity
import com.deepak.besaat.view.activities.newRequestService.NewRequestService
import com.deepak.besaat.view.activities.newRequestStore.NewRequestStore
import com.deepak.besaat.view.activities.paymentDetailsStore.PaymentDetailsStoreActivity
import com.deepak.besaat.view.activities.paymentInformation.PaymentInformationActivity
import com.deepak.besaat.view.activities.requestOffers.ui.RequestOffersActivity
import com.deepak.besaat.view.activities.requestStatus.ui.RequestStatusActivity
import com.deepak.besaat.view.activities.selectCourierType.SelectCourierType
import com.deepak.besaat.view.activities.servicesListing.ServicesListingActivity
import com.deepak.besaat.view.activities.storeDetail.StoreDetailActivity
import com.deepak.besaat.view.activities.storesListing.StoreListingActivity
import com.example.possibility.hr.utils.FilesFunctions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

import java.io.File
import java.text.DecimalFormat
import java.util.*

open class BaseActivity : AppCompatActivity(), View.OnClickListener {
    var dialog: AlertDialog? = null
    var mImageFile: File? = null
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    private var lastLocationValue: LastLocationValues? = null
    private var mLatitudeLabel: String = ""
    private var mLongitudeLabel: String = ""

    companion object {
        lateinit var textViewTitleName: TextView
        lateinit var topRelative: RelativeLayout

        lateinit var imageViewMenu: ImageView
        lateinit var textViewTitle: TextView
        lateinit var searchEdit: EditText
        lateinit var imageViewbackArrow: ImageView
        lateinit var appBarLayout: RelativeLayout
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }

    override fun onStart() {
        super.onStart()
        /* if (!checkPermissions()) {
             startLocationPermissionRequest()
         } else {
             getLastLocation(lastLocationValue)
         }*/
    }

    public fun getLastLocation(lastLocationValue: LastLocationValues?) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result

                    mLatitudeLabel = (mLastLocation)!!.latitude.toString()
                    mLongitudeLabel = (mLastLocation)!!.longitude.toString()

                    lastLocationValue?.LocationValues(
                        (mLastLocation)!!.latitude,
                        (mLastLocation)!!.longitude,
                        mLatitudeLabel,
                        mLongitudeLabel
                    )
                    //sharedPref.setString(Constants.latitude,mLatitudeLabel)
                    // sharedPref.setString(Constants.longitude,mLongitudeLabel)


                } else {
                    Log.w("error", "getLastLocation:exception", task.exception)
                    // showMessage(getString(R.string.no_location_detected))
                }
            }
    }

    fun startLocationPermissionRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this@BaseActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        } else {

            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsProviderEnabled: Boolean
            val isNetworkProviderEnabled: Boolean
            isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkProviderEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsProviderEnabled && !isNetworkProviderEnabled) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Location Permission")
                builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.")
                builder.setPositiveButton(
                    android.R.string.yes,
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface: DialogInterface, i: Int) {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                    })
                builder.setNegativeButton(android.R.string.no, null)
                builder.show()

            }
        }
    }

    fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    fun customToolBarWithBack(context: AppCompatActivity, toolbar: Toolbar) {
        context.setSupportActionBar(toolbar)
        context.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        context.supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        val view = LayoutInflater.from(context).inflate(R.layout.view_tool_bar_with_back_icon, null)
        textViewTitleName = view.findViewById<TextView>(R.id.textViewTitleName)
        imageViewbackArrow = view.findViewById<ImageView>(R.id.imageViewbackArrow)
        context.supportActionBar!!.customView = view
        if (context is JobDetailsActivity || context is CancelOrderRequestActivity || context is RequestStatusActivity || context is RequestOffersActivity || context is CreateProfileActivity || context is AddServicesDetailActvity || context is AddCourierDetailActivity || context is PaymentInformationActivity || context is NewRequestStore || context is StoreDetailActivity || context is NewRequestService || context is DeliveryPersonDetail || context is SelectCourierType || context is NewRequestCourier || context is NewRequestCourierOverseasActivity || context is PaymentDetailsStoreActivity || context is ChatActivity) {
            textViewTitleName.visibility = View.VISIBLE
        } else {
            textViewTitleName.visibility = View.GONE
        }

        imageViewbackArrow.setOnClickListener {
            if (context is JobDetailsActivity || context is RequestStatusActivity || context is RequestOffersActivity || context is CancelOrderRequestActivity) {
                context.onBackPressed()
            } else {
                finish()
            }
        }
    }

    public fun customToolBarWithMenu(context: AppCompatActivity, toolbar: Toolbar) {
        context.setSupportActionBar(toolbar)
        context.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        context.supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        val view = LayoutInflater.from(context).inflate(R.layout.view_tool_bar_with_menu, null)
        appBarLayout = view.findViewById(R.id.appBarLayout)
        textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)
        imageViewMenu = view.findViewById<ImageView>(R.id.imageViewMenu)
        topRelative = view.findViewById<RelativeLayout>(R.id.topRelative)

        context.supportActionBar!!.customView = view
    }

    fun customToolBarWithSearch(
        context: AppCompatActivity,
        toolbar: Toolbar,
        param: searchTextChange,
        param1: imageIconClick
    ) {
        context.setSupportActionBar(toolbar)
        context.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        context.supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        val view =
            LayoutInflater.from(context).inflate(R.layout.view_tool_bar_with_menu_search, null)
        var appBarLayout = view.findViewById<LinearLayout>(R.id.appBarLayout)
        var imageViewFilter = view.findViewById<ImageView>(R.id.imageViewFilter)
        searchEdit = view.findViewById(R.id.searchEdit)
        imageViewFilter.setOnClickListener { param1.onClick(true) }

        searchEdit.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                //tvSample.setText("Text in EditText : "+s)
                param.TextChange(s.toString())

            }
        })
        /*   imageViewFilter.setOnClickListener(object :View.OnClickListener{
               override fun onClick(view: View?) {
                   var   Tool :ToolTip=ToolTip()
                   Tool
                   .withContentView(LayoutInflater.from(this@BaseActivity).inflate(R.layout.search_pop_up_layout, null))

                   .withAnimationType(ToolTip.AnimationType.NONE);
                   activity_main_tooltipframelayout.showToolTipForView(Tool,view);


               }
           })*/
        if (context is ServicesListingActivity) {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorYellow))
            imageViewFilter.background =
                ContextCompat.getDrawable(context, R.drawable.bg_light_black_tr_br_four_dp_round)
        } else if (context is CourierGuysListing) {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhitishBlue))
            imageViewFilter.background =
                ContextCompat.getDrawable(context, R.drawable.bg_light_blue_tr_br_four_dp_round)
        }

        var imageViewBackArrow = view.findViewById<ImageView>(R.id.imageViewBackArrow)
        imageViewBackArrow.setOnClickListener {
            if (context is StoreListingActivity) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else if (context is CourierGuysListing) {
                context.onBackPressed()
            } else {
                finish()
            }
        }

        context.supportActionBar!!.customView = view
    }

    fun checkPermission(permission: Array<String>): Int {
        var permissionNeeded = 0
        if (Build.VERSION.SDK_INT >= 23) {
            for (i in permission.indices) {
                val result = ContextCompat.checkSelfPermission(this!!, permission[i])
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionNeeded++
                }
            }
        }
        return permissionNeeded
    }


    fun dialogForCameraGallery() {
        val dialogBuilder = AlertDialog.Builder(this!!)
        val view = LayoutInflater.from(this).inflate(R.layout.popup_camera_gallery, null)
        val mTextViewGallery = view.findViewById<TextView>(R.id.textViewGallery)
        val mTextViewTakePic = view.findViewById<TextView>(R.id.textViewTakePic)
        val mTextViewCancel = view.findViewById<TextView>(R.id.textViewCancel)
        mTextViewGallery.setOnClickListener(this)
        mTextViewTakePic.setOnClickListener(this)
        mTextViewCancel.setOnClickListener(this)
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.show()
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
                            this!!,
                            this!!.packageName + ".provider",
                            mImageFile!!
                        )
                    )
                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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

    //<editor-fold desc="permission dialog to show user that this permission is necessary">
    fun alertDialogWithOKButton(title: String, message: String) {
        val builder = AlertDialog.Builder(this!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", this!!.packageName, null)
            intent.data = uri
            startActivityForResult(intent, Constants.REQUEST_PERMISSION_SETTING)
            dialogInterface.dismiss()
        }
        builder.show()
    }

    fun alertDialogWithOKButton2(title: String, message: String) {
        val builder = AlertDialog.Builder(this!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->

            dialogInterface.dismiss()
        }
        builder.show()
    }

    fun alertDialogOKButtonLocation(title: String, message: String) {
        Places.initialize(applicationContext, getString(R.string.google_api_key))
        var AUTOCOMPLETE_REQUEST_CODE = 5;
        var fields = Arrays.asList(
            Place.Field.LAT_LNG, Place.Field.NAME,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ADDRESS
        );

        val builder = AlertDialog.Builder(this!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->

            var intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }
        builder.show()

    }
    //</editor-fold>


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_PERMISSION -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        dialogForCameraGallery()
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        alertDialogWithOKButton(
                            getString(R.string.permission),
                            getString(R.string.storagePermission)
                        )
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            val showRationale =
                                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            val showRationale2 =
                                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                            if (!showRationale) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storagePermission)
                                )
                            } else if (!showRationale2) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storagePermission)
                                )
                            }
                        }
                    }
                }
            }

            Constants.REQUEST_AUDIO_PERMISSION -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        // Audio Granted
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        alertDialogWithOKButton(
                            getString(R.string.permission),
                            getString(R.string.audio_permission)
                        )
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            val showRationale =
                                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            val showRationale2 =
                                shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                            if (!showRationale) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.audio_permission)
                                )
                            } else if (!showRationale2) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.audio_permission)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun initPlaceAutoCompleteActivity(activity: Activity, REQ: Int, country: String) {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key))
        }
        val fields: List<Place.Field?>
        fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ADDRESS
        )
        if (country != "") {
            var intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            ).setCountry(country).build(this)
            activity.startActivityForResult(intent, REQ)
        } else {
            var intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            ).build(this)
            activity.startActivityForResult(intent, REQ)
        }
    }

    fun getDistance(StartP: LatLng, EndP: LatLng): Any {
        val Radius = 6371// radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
//        Log.i("Radius Value", "$valueResult   KM  $kmInDec Meter   $meterInDec")
//        Log.e("Radius Value", "radius value return " + Radius * c)
        return Radius * c
    }

}