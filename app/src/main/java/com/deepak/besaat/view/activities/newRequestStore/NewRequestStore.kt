package com.deepak.besaat.view.activities.newRequestStore


import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityNewRequestStoreBinding
import com.deepak.besaat.view.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_new_request_store.*
import android.content.Intent

import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*
import com.google.android.libraries.places.widget.AutocompleteActivity
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.deepak.besaat.viewModel.NewRequestStoreViewModel
import org.koin.android.ext.android.inject
import android.location.Geocoder
import android.os.SystemClock
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.deepak.besaat.Interfaces.Selected
import com.deepak.besaat.model.CreateRequestResponse.NearByServicesProviderListPojo
import com.deepak.besaat.model.GetNewServiceRequest.NewServiceRequestPojo
import com.deepak.besaat.model.providerSignUpDetailToServer.personAddedModel
import com.deepak.besaat.model.socialLoginModel.User
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.newRequestStore.adapter.ServicesProvidersListAdapter
import com.deepak.besaat.view.activities.newRequestStore.interfaces.IServiceProviderClickListener
import com.deepak.besaat.view.activities.paymentDetailsStore.PaymentDetailsStoreActivity
import com.deepak.besaat.view.fragments.NearByListFragments
import com.example.possibility.hr.utils.FilesFunctions
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_new_request_store.relativeLayout6
import kotlinx.android.synthetic.main.activity_new_request_store.textViewContinue
import kotlinx.android.synthetic.main.activity_new_request_store.toolBar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.text.DecimalFormat


class NewRequestStore : BaseActivity(), IServiceProviderClickListener {
    lateinit var binding: ActivityNewRequestStoreBinding
    val viewModel: NewRequestStoreViewModel by inject()
    val sharedPref: SharedPref by inject()
    var lat: Double? = null
    var long: Double? = null
    var address: String? = null
    var dadress: String? = null
    var radiusValue: Any? = null
    var fairAmount: Double? = null
    var fair: String? = null
    var type: Int = 1
    lateinit var file: File
    var storeName: String? = "Store"
    val fragmentFucntion: FragmentFucntions by inject()
    private var mLastClickTime = 0.toLong()

    var storeList: MutableList<personAddedModel>? = null
    var selectedProviderIDs: MutableList<Long>? = ArrayList()
    var servicesProviderList: MutableList<User>? = ArrayList()
    lateinit var serviceProviderAdapter: ServicesProvidersListAdapter
    val commonFunctions: CommonFunctions by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityNewRequestStoreBinding>(
            this,
            R.layout.activity_new_request_store
        )

        binding.viewModelNewRequest = viewModel
        binding.recyclerViewVisibility = false
        customToolBarWithBack(this, toolBar)
        setAdapterServiceProviders()

        file = File("xyz.txt")

        var from: String = intent.getStringExtra("from") ?: ""
        storeName = intent.getStringExtra("title") ?: "Store"

//        edTitle.setText(title)

        lat = intent.getDoubleExtra("latitute", 0.0)
        long = intent.getDoubleExtra("longitute", 0.0)
        if (from != "MapFrag") {
            address = intent.getStringExtra("location") ?: ""
        }

        textViewTitleName.text = getString(R.string.new_request)

        editTextOrderInformation.setOnTouchListener { p0, p1 ->
            scrollView.requestDisallowInterceptTouchEvent(true);
            false
        }

        editTextSpecialNotes.setOnTouchListener { p0, p1 ->
            scrollView.requestDisallowInterceptTouchEvent(true)
            false
        }
        init()
        add_photo.setOnClickListener {
            val permission = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (checkPermission(permission) > 0) {
                ActivityCompat.requestPermissions(
                    this@NewRequestStore,
                    arrayOf(permission[0], permission[1]),
                    Constants.REQUEST_PERMISSION
                )
            } else {
                dialogForCameraGallery()
            }
        }
        var completeAddress = ""
        if (sharedPref.getString(Constants.latChanged) == "" && sharedPref.getString(Constants.longChanged) == "") {
            completeAddress = getCompleteAddressString(
                sharedPref.getString(Constants.latitude).toDouble(),
                sharedPref.getString(Constants.longitude).toDouble()
            )
        } else {
            completeAddress = getCompleteAddressString(
                sharedPref.getString(Constants.latChanged).toDouble(),
                sharedPref.getString(Constants.longChanged).toDouble()
            )
        }
        Log.e("Address", completeAddress)
        dropAddress.text = completeAddress
        dadress = getCompleteAddressString(lat!!, long!!)
        if (from == "MapFrag") {
            dadress = getCompleteAddressString(lat!!, long!!)
            // dropAddress.text = dadress
            textCurrentAddress.text = dadress
        } else {
            // dropAddress.text = address

            textCurrentAddress.text = address
        }
        if (sharedPref.getString(Constants.latChanged).equals("") && sharedPref.getString(Constants.longChanged).equals(
                ""
            )
        ) {
            radiusValue = CalculationByDistance(
                LatLng(
                    sharedPref.getString(Constants.latitude).toDouble(),
                    sharedPref.getString(Constants.longitude).toDouble()
                ), LatLng(lat!!.toDouble(), long!!.toDouble())
            )
        } else {
            radiusValue = CalculationByDistance(
                LatLng(
                    sharedPref.getString(Constants.latChanged).toDouble(),
                    sharedPref.getString(Constants.longChanged).toDouble()
                ), LatLng(lat!!.toDouble(), long!!.toDouble())
            )
        }

        if (lat != null || lat != 0.0) {
            viewModel.executenearbyproviders(
                Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                lat.toString(),
                long.toString()
            )
        }

//        setAdapter()

        Places.initialize(applicationContext, getString(R.string.google_api_key))
        /* val placesClient = Places.createClient(this)*/
        var AUTOCOMPLETE_REQUEST_CODE = 5
        var fields = listOf(
            Place.Field.LAT_LNG,
            Place.Field.NAME,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ADDRESS
        )


        linearLayout11.setOnClickListener {
            var intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
        var authorization =
            Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN)

        viewModel.executeFairApi(authorization)

        textViewContinue.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()

                var authorization =
                    Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN)



                if (validation()) {

                    var providerID = selectedProviderIDs.toString().removeSurrounding("[", "]")
                    Log.e("providerIds", providerID)

                    viewModel.executeCreateRequest(
                        authorization,
                        storeName!!,
                        edTitle.text.toString().trim(),
                        textCurrentAddress.text.toString(),
                        lat.toString(),
                        long.toString(),
                        completeAddress,
                        sharedPref.getString(Constants.latitude),
                        sharedPref.getString(Constants.longitude),
                        editTextOrderInformation.text.toString(),
                        editTextSpecialNotes.text.toString(),
                        fairAmount.toString(),
                        "1",
                        "1",  // 1 for storeRequest
                        file,
                        providerID,
                        if (type == 1) {
                            Constants.DRIVER_AUTO
                        } else {
                            Constants.DRIVER_MANUAL
                        }
                    )
                }
            }
        })

        relativeLayout6.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, 20)
                }
            }
        })

        /*radioGroup21.setOnCheckedChangeListener(object:RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, p1: Int) {
                Log.e("newRequest","new request is "+p1+"   "+group.toString())
                if(p1==R.id.radioButtonAutoTransmission1){


                }
                if(p1 ==R.id.radioButtonManualTransmission1){


                }
            }
        })*/
    }

    override fun onResume() {
        super.onResume()
        commonFunctions.hideProgressBar()
    }

    private fun setAdapterServiceProviders() {
        serviceProviderAdapter = ServicesProvidersListAdapter(this, servicesProviderList!!)
        serviceProviderAdapter.attachServiceProviderClicks(this)
        binding.rvServiceProviders.adapter = serviceProviderAdapter

        binding.rvServiceProviders.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider
                )
            )
        )
    }

    private fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Any {
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
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        Log.e("Radius Value", "radius valur return " + Radius * c)
        return Radius * c
    }

    private fun validation(): Boolean {
        val paramBool: Boolean
        selectedProviderIDs!!.clear()
        if (edTitle.text.toString().trim() == "") {
            paramBool = false
            commonFunctions.showFeedbackMessage(
                scrollView,
                "please fill request title"
            )
            return paramBool
        }
        if (textCurrentAddress.text.toString() == "") {
            paramBool = false
            commonFunctions.showFeedbackMessage(
                scrollView,
                "please fill current address"
            )
            return paramBool
        }
        if (dropAddress.text.toString() == "") {
            paramBool = false
            commonFunctions.showFeedbackMessage(
                scrollView,
                "please fill drop address "
            )
            return paramBool
        }
        if (editTextOrderInformation.text.toString() == "") {
            paramBool = false
            commonFunctions.showFeedbackMessage(
                scrollView,
                "please fill order information"
            )
            return paramBool
        }
        if (editTextSpecialNotes.text.toString() == "") {
            paramBool = false
            commonFunctions.showFeedbackMessage(
                scrollView,
                "please fill special note "
            )
            return paramBool
        }
        if (type == 2) {
            var selected = false
            for (i in 0 until servicesProviderList!!.size) {
                if (servicesProviderList!![i].isChecked) {
                    selected = true
                    selectedProviderIDs!!.add(servicesProviderList!![i].id!!)
                }
            }

            if (!selected) {
                paramBool = false
                if (servicesProviderList == null) {
                    commonFunctions.showFeedbackMessage(
                        scrollView,
                        "No nearby provider,\n you can choose auto transmission"
                    )
                } else if (servicesProviderList != null && servicesProviderList!!.isEmpty()) {
                    commonFunctions.showFeedbackMessage(
                        scrollView,
                        "No nearby provider,\n you can choose auto transmission"
                    )
                } else {
                    commonFunctions.showFeedbackMessage(
                        scrollView,
                        "please select at least one provider"
                    )
                }
                return paramBool
            }
        }
        /*  if(!radioButtonAutoTransmission1.isChecked || !radioButtonManualTransmission1.isChecked){
              paramBool=false
              Toast.makeText(this,"please select any transmision ",Toast.LENGTH_LONG).show()
              return paramBool

          }*/

        paramBool = true
        return paramBool
    }

    fun init() {
        viewModel.accountType.observe(this, androidx.lifecycle.Observer {
            if (it == 1) {
                type = 1
                Log.e("newRequest", "account type is 1st")
                binding.recyclerViewVisibility = false
            } else {
                type = 2
                Log.e("newRequest", "account type is 2nd")
                // Toast.makeText(this@NewRequestStore,"manual transmission is in progress ",Toast.LENGTH_LONG).show()
                binding.recyclerViewVisibility = true
//                setAdapter()


                if (servicesProviderList != null) {
                    if (servicesProviderList!!.isEmpty())
                        commonFunctions.showFeedbackMessage(
                            scrollView,
                            "No nearby provider,\n you can choose auto transmission"
                        )
                } else {
                    commonFunctions.showFeedbackMessage(
                        scrollView,
                        "No nearby provider,\n you can choose auto transmission"
                    )
                }
            }
        })

        viewModel.progressBar.observe(this, androidx.lifecycle.Observer {
            if (it) {
                commonFunctions.showProgressBar(this, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })
        viewModel.createJsonResponse.observe(this, androidx.lifecycle.Observer {
            handleResponse(it)
        })

        viewModel.fairJsonResponse.observe(this, androidx.lifecycle.Observer {
            handleFairResponse(it)
        })
        viewModel.nearbyProviderResponse.observe(this, androidx.lifecycle.Observer {
            handleNearbyResponse(it)
        })

        viewModel.feedBackMessage.observe(this, androidx.lifecycle.Observer {
            commonFunctions.showFeedbackMessage(scrollView, it)
        })
    }

    private fun handleNearbyResponse(it: JSONObject?) {
        Log.e("NearByResponse", "near by response $it")
        storeList = ArrayList()
        servicesProviderList?.clear()
        try {
            var pojo = Gson().fromJson(it.toString(), NearByServicesProviderListPojo::class.java)
            if (pojo.data != null)
                servicesProviderList?.addAll(pojo.data!!)

            serviceProviderAdapter.submitList(servicesProviderList)
            serviceProviderAdapter.notifyDataSetChanged()
//            if (it?.has("data") == true) {
//                val jsonArray = it?.getJSONArray("data")!!
//                for (item: JSONObject in jsonArray) {
//                    Log.e("response", "item object  $item")
//                    try {
//                        var personAddedModel = personAddedModel()
//                        personAddedModel.name = item?.getString("name")
//                        personAddedModel.distance = item?.getString("distance")
//                        personAddedModel.imageUrl = item?.getString("image")
//                        // store.status=item?.getString("")
//                        (storeList as ArrayList<personAddedModel>).add(personAddedModel)
//                    } catch (e: JSONException) {
//                        Log.e("response", "json Exception  $e")
//                    }
//                }
//            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun handleFairResponse(it: JSONObject?) {
        Log.e("RequestResponse", "fair response is " + it)
        try {
            fair = it?.getString("fair")
            Log.e("RequestResponse", "fair is " + fair)
            fairAmount = (fair?.toDouble())?.times((radiusValue.toString().toDouble()))
            Log.e("RequestResponse", "fair calculation is  " + fairAmount)
            if (fairAmount.toString().length > 3) {
                fairText.text = "$" + fairAmount.toString().substring(0, 4) + " "
            } else if (fairAmount.toString().length > 2) {
                fairText.text = "$" + fairAmount.toString().substring(0, 3) + " "
            } else if (fairAmount.toString().length > 1) {
                fairText.text = "$" + fairAmount.toString().substring(0, 2) + " "
            } else {
                fairText.text = "$" + "0.0" + " "
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun handleResponse(it: JSONObject?) {
        Log.e("createRequest", "create request response  " + it.toString())
        if (it != null) {
            var pojo = Gson().fromJson(it.toString(), NewServiceRequestPojo::class.java)
            var messageString: String? = it?.getString("message")
            var jsonObject = it?.getJSONObject("social")
            var pickupaddress = jsonObject?.getString("pickup_address")
            var dropadress = jsonObject?.getString("drop_address")
            var orderinfo = jsonObject?.getString("order_info")
            var specialnote = jsonObject?.getString("special_note")
            var pickupLatitude = jsonObject?.getString("pickup_latitude")
            var pickupLongitude = jsonObject?.getString("pickup_longitude")
            var dropLatitude = jsonObject?.getString("drop_latitude")
            var dropLongitude = jsonObject?.getString("drop_longitude")
            var orderId = jsonObject?.getString("id")
            if (messageString != null) {
                commonFunctions.showFeedbackMessage(
                    scrollView,
                    messageString!!
                )
            }

            var int = Intent(this@NewRequestStore, PaymentDetailsStoreActivity::class.java)
            int.putExtra("pickAddress", pickupaddress)
            int.putExtra("dropAddress", dropadress)
            int.putExtra("orderinfo", orderinfo)
            int.putExtra("specialnote", specialnote)
            int.putExtra("pickLat", pickupLatitude)
            int.putExtra("pickLong", pickupLongitude)
            int.putExtra("dropLat", dropLatitude)
            int.putExtra("dropLong", dropLongitude)
            int.putExtra("fair", fair)
            int.putExtra("fairAmount", fairAmount.toString())
            int.putExtra("orderId", orderId)
            int.putExtra("orderId", orderId)

            int.putExtra("from", "store")
            int.putExtra("data", pojo.getSocial())
            //int.putExtra("radiusValue",radiusValue)
            startActivity(int)
        } else {
            commonFunctions.showFeedbackMessage(
                scrollView,
                "Server error"
            )
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
                Log.w("CurrentAddress", strReturnedAddress.toString())
            } else {
                Log.w("CurrentAddress", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("CurrentAddress", "Canont get Address!")
        }
        return strAdd
    }

//    private fun setAdapter() {
//        var nearByListFragments = NearByListFragments.newInstance(storeList, object : Selected {
//            override fun selected(sucess: Boolean) {
//                selectBool = sucess
//            }
//        })
//        fragmentFucntion.replaceFragment(this, nearByListFragments, R.id.linearContainer)
//        /*var adapter= storeList?.let { DriverAdapter(it) }
//        recyclerViewDriver.layoutManager=LinearLayoutManager(this)
//        recyclerViewDriver.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(this@NewRequestStore,R.drawable.divider)))
//        recyclerViewDriver.adapter=adapter*/
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 5) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    var longitude = place.latLng!!.longitude
                    var latitude = place.latLng!!.latitude
                    Log.e("longitude", longitude.toString())
                    Log.e("latitude", latitude.toString())

                    var completeAddress = getCompleteAddressString(latitude, longitude)
                    Log.e("Address", completeAddress)
                    dropAddress.text = completeAddress
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode == Constants.GALLERY_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                //   imageViewLicense.setImageBitmap(data?.extras?.get("data") as Bitmap)
                //  add_photo.setImageURI(data?.data)
                // Log.e("Data","data is  "+data?.data.toString() +"    "+data?.data.toString().reversed())
                var path = FilesFunctions.getPathFromData(this!!, data!!)
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this!!.getContentResolver(), data!!.data);
                var newBitMap = FilesFunctions.changeImageOrientation(path, bitmap)
                mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                file = mImageFile as File
                //viewModel.file = mImageFile
                add_photo.setImageBitmap(newBitMap)
                textCurrentAddress11.text =
                    bitmap.toString().substring(bitmap.toString().indexOf("@"))

                AllLocalHandler.providerSignUpDetailToServer.requestImage = mImageFile

            }
        }
        if (requestCode == Constants.CAMERA_INTENT && resultCode == Activity.RESULT_OK) {
            if (mImageFile != null) {
                val bitmap = BitmapFactory.decodeFile(mImageFile!!.getPath())
                var newBitMap = FilesFunctions.changeImageOrientation(mImageFile!!.path, bitmap)
                mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                file = mImageFile as File

                //   viewModel.file = mImageFile
                add_photo.setImageBitmap(newBitMap)
                textCurrentAddress11.text =
                    bitmap.toString().substring(bitmap.toString().indexOf("@") + 2)
                AllLocalHandler.providerSignUpDetailToServer.requestImage = mImageFile
            } else {
                if (data != null) {
                    val thumbnail = data!!.extras!!.get("data") as Bitmap
//                        circularImageView!!.setImageBitmap(thumbnail)
                    mImageFile = FilesFunctions.createFileFromBitMap(thumbnail)
                    file = mImageFile as File
                    AllLocalHandler.providerSignUpDetailToServer.requestImage = mImageFile
                    var resizedBitMap = FilesFunctions.getResizedBitmap(thumbnail, 200, 200)
                    add_photo.setImageBitmap(resizedBitMap)
                } else {
                    commonFunctions.showFeedbackMessage(
                        rootLayout!!,
                        "Camera not supported. Please pic image from gallery."
                    )
                }
            }
        }
    }

    operator fun JSONArray?.iterator(): Iterator<JSONObject> =
        (0 until this!!.length()).asSequence().map { get(it) as JSONObject }.iterator()

    override fun onServiceProviderClick(position: Int) {
        servicesProviderList!![position].isChecked = !servicesProviderList!![position].isChecked
        serviceProviderAdapter.submitList(servicesProviderList)
        serviceProviderAdapter.notifyDataSetChanged()
    }
}
