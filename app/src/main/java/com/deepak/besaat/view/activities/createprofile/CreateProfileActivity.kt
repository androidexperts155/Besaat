package com.deepak.besaat.view.activities.createprofile

import android.Manifest
import android.app.Activity
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
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.Interfaces.LastLocationValues
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityCreateProfileBinding
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.utils.getCOuntryCode
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.viewModel.CreateProfileViewModel
import com.example.possibility.hr.utils.FilesFunctions
import com.facebook.FacebookSdk
import kotlinx.android.synthetic.main.activity_create_profile.*
import org.koin.android.ext.android.inject
import java.io.IOException


class CreateProfileActivity : BaseActivity() {
    val commonFunction: CommonFunctions by inject()
    val createProfileViewModel: CreateProfileViewModel by inject()
    val sharedPref: SharedPref by inject()
    lateinit var binding: ActivityCreateProfileBinding
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityCreateProfileBinding>(
            this,
            R.layout.activity_create_profile
        )

        setData()

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
                        sharedPref.setString(Constants.latitude, latString)
                        sharedPref.setString(Constants.longitude, longString)
                        var countryName = getCurrentCountry(lat, long)
                        if (countryName != null) {
                            var countryCode =
                                getCOuntryCode.getCountryCodeByCountryName(countryName?.toLowerCase())
                            Log.e(
                                "CountryName",
                                "country name on creare profile $countryName  $lat   $long"
                            )
                            binding!!.ccp2!!.setCountryForNameCode(countryCode)

                            if (createProfileViewModel.from.equals("CodeVerification")) {
                                ccp2.setCountryForPhoneCode(
                                    AllLocalHandler.userDetail.phoneCode!!.substring(
                                        1
                                    ).toInt()
                                )
                            }
                        }
                    })
                }
            })
        }
        binding.createProfileModel = createProfileViewModel
        initObserver()
    }

    override fun onStart() {
        super.onStart()
//        if (!super.checkPermissions()) {
//            super.startLocationPermissionRequest()
//        } else {
//            super.getLastLocation(object : LastLocationValues {
//                override fun LocationValues(
//                    lat: Double,
//                    long: Double,
//                    latString: String,
//                    longString: String
//                ) {
//                    runOnUiThread(Runnable {
//                        sharedPref.setString(Constants.latitude, latString)
//                        sharedPref.setString(Constants.longitude, longString)
//                        var countryName = getCurrentCountry(lat, long)
//                        var countryCode =
//                            getCOuntryCode.getCountryCodeByCountryName(countryName?.toLowerCase())
//                        Log.e(
//                            "CountryName",
//                            "country name on creare profile " + countryName + "  " + lat + "   " + long
//                        )
//                        binding!!.ccp2!!.setCountryForNameCode(countryCode)
//                    })
//                }
//            })
//        }
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


    fun initObserver() {
        createProfileViewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunction.showProgressBar(
                    this@CreateProfileActivity,
                    getString(R.string.loading)
                )
            } else {
                commonFunction.hideProgressBar()
            }
        })

        createProfileViewModel.feedBackMessage.observe(this, Observer {
            commonFunction.showFeedbackMessage(rootLayout, it)
        })



        createProfileViewModel.onSuccessfullySignUp.observe(this, Observer {
            sharedPref.setString(Constants.USER_NAME, it.data!!.name!!)
            sharedPref.setLong(Constants.USER_ID, it.data!!.id!!)
            sharedPref.setString(Constants.TOKEN, it.accessToken!!)
            sharedPref.setString(Constants.USER_IMAGE, it.data!!.image!!)
            sharedPref.setString(Constants.ROLE, it.data!!.role!!)
            startActivity(Intent(this@CreateProfileActivity, HomeActivity::class.java))
            finishAffinity()
        })

        createProfileViewModel.imageClick.observe(this, Observer {
            if (it) {
                val permission = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (checkPermission(permission) > 0) {
                    ActivityCompat.requestPermissions(
                        this@CreateProfileActivity,
                        arrayOf(permission[0], permission[1]),
                        Constants.REQUEST_PERMISSION
                    )
                } else {
                    dialogForCameraGallery()
                }
            }
        })
    }

    fun setData() {
        createProfileViewModel.from = intent.getStringExtra("from")
        customToolBarWithBack(this, toolBar)
        /* super.getLastLocation(object:LastLocationValues{

             override fun LocationValues(lat: String, long: String) {

             }
         })*/
        // BaseActivity.getLastLocation
        textViewTitleName.text = getString(R.string.create_profile)

        createProfileViewModel!!.selectedCountryCode = binding!!.ccp2!!.selectedCountryCode
        binding!!.ccp2!!.setOnCountryChangeListener {
            createProfileViewModel.selectedCountryCode = binding!!.ccp2!!.selectedCountryCode
        }

        if (createProfileViewModel.from.equals("CodeVerification")) {
            editTextPhoneNumber.isEnabled = false
            ccp2.isClickable = false
            ccp2.setCountryForPhoneCode(AllLocalHandler.userDetail.phoneCode!!.substring(1).toInt())
        }
        createProfileViewModel.name.value = AllLocalHandler.userDetail.name
        createProfileViewModel.email.value = AllLocalHandler.userDetail.email
        createProfileViewModel.phone.value = AllLocalHandler.userDetail.phone
        if (AllLocalHandler.userDetail.imageUrl != "") {
            commonFunction.loadCircularImage(
                this@CreateProfileActivity,
                AllLocalHandler.userDetail.imageUrl,
                circularImageView
            )
        }
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
        } else if (requestCode == Constants.REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialogForCameraGallery()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.CAMERA_INTENT -> {
                    if (mImageFile != null) {
                        val bitmap = BitmapFactory.decodeFile(mImageFile?.path)
                        var newBitMap =
                            FilesFunctions.changeImageOrientation(mImageFile!!.path, bitmap)
                        mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                        createProfileViewModel.file = mImageFile
                        AllLocalHandler.userDetail.image = mImageFile

                        var resizedBitMap = FilesFunctions.getResizedBitmap(newBitMap, 200, 200)
                        circularImageView.setImageBitmap(resizedBitMap)
                    } else {
                        if (data != null) {
                            val thumbnail = data!!.extras!!.get("data") as Bitmap
//                        circularImageView!!.setImageBitmap(thumbnail)
                            mImageFile = FilesFunctions.createFileFromBitMap(thumbnail)
                            createProfileViewModel.file = mImageFile
                            AllLocalHandler.userDetail.image = mImageFile
                            var resizedBitMap = FilesFunctions.getResizedBitmap(thumbnail, 200, 200)
                            circularImageView.setImageBitmap(resizedBitMap)
                        } else {
                            commonFunction.showFeedbackMessage(
                                rootLayout,
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

                    createProfileViewModel.file = mImageFile
                    AllLocalHandler.userDetail.image = mImageFile

                    var resizedBitMap = FilesFunctions.getResizedBitmap(newBitMap, 200, 200)
                    circularImageView.setImageBitmap(resizedBitMap)
                }
            }
        }
    }
}
