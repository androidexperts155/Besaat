package com.deepak.besaat.view.activities.phoneSignUp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityPhoneSignUpBinding
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.verifyCode.CodeVerificationActivity
import com.deepak.besaat.viewModel.PhoneSignUpViewModel
import kotlinx.android.synthetic.main.activity_phone_sign_up.*
import org.koin.android.ext.android.inject
import android.telephony.TelephonyManager
import android.util.Log
import com.deepak.besaat.Interfaces.LastLocationValues
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.utils.getCOuntryCode
import com.facebook.FacebookSdk
import java.io.IOException


class PhoneSignUpActivity : BaseActivity() {

    lateinit var binding: ActivityPhoneSignUpBinding
    val phoneSignUpViewModel: PhoneSignUpViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    private val REQUEST_READ_PHONE_STATE = 100
     var phone_number =""
    val sharedPref: SharedPref? by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityPhoneSignUpBinding>(
            this,
            R.layout.activity_phone_sign_up
        )
        binding.phoneSignUpModel = phoneSignUpViewModel
        customToolBarWithBack(this, toolBar)


        phoneSignUpViewModel!!.phoneCode = "+" + binding!!.ccp!!.selectedCountryCode
        textViewPhoneCode.setText("+" + binding!!.ccp!!.selectedCountryCode)
        binding!!.ccp!!.setOnCountryChangeListener {
            phoneSignUpViewModel.phoneCode = "+" + binding!!.ccp!!.selectedCountryCode
            textViewPhoneCode.setText("+" + binding!!.ccp!!.selectedCountryCode)
        }



        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_READ_PHONE_STATE)
        } else {


            val tMgr = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if(getSimState(tMgr.getSimState())==true){
                phone_number = tMgr.line1Number

                Log.e("phoneNo",""+phone_number)
            }


        }


        initObserver()

        if(!super.checkPermissions()){
            super.startLocationPermissionRequest()
        }else{


            super.getLastLocation(object: LastLocationValues {


                override fun LocationValues(lat: Double, long: Double,latString:String,longString:String) {
                    runOnUiThread(Runnable {

                        sharedPref?.setString(Constants.latitude,latString)
                        sharedPref?.setString(Constants.longitude,longString)
                        var countryName =getCurrentCountry(lat,long)
                        if (countryName!=null)
                        {
                            var countryCode= getCOuntryCode.getCountryCodeByCountryName(countryName?.toLowerCase())
                            Log.e("CountryName","country name on phone sign up "+countryName+"  "+lat+"  "+long)
                            binding!!.ccp!!.setCountryForNameCode(countryCode)

                        }
                    })
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
    }


    fun  getCurrentCountry(lat: Double,long: Double): String? {
        var country_name:String? = null
        val lm = FacebookSdk.getApplicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val geocoder = Geocoder(FacebookSdk.getApplicationContext())

        for (provider in lm.getAllProviders())
        {
            // val location = lm.getLastKnownLocation(provider)
            //   if (location != null)
            //   {
            try
            {
                var addresses:MutableList<Address>?=null

                if(!sharedPref?.getString(Constants.latitude).equals("") && !sharedPref?.getString(Constants.longitude).equals("")) {
                    addresses = geocoder.getFromLocation(
                        lat,
                        long,
                        1
                    )
                }
                if (addresses != null && addresses.size > 0)
                {
                    country_name = addresses.get(0).getCountryName()
                    break
                }
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
            //}
        }

        return country_name
    }

//    override fun onPause() {
//        super.onPause()
//        if (broadcastReceiver != null)
//            unregisterReceiver(broadcastReceiver)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        val intentFilter = IntentFilter()
//        intentFilter.addAction(Constants.MESSAGE_CODE)
//        registerReceiver(broadcastReceiver, intentFilter)
//    }

//    internal var broadcastReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//
//            val action = intent.action
//
//            if (action != null)
//
//                if (action.equals(Constants.MESSAGE_CODE, ignoreCase = true)) {
//
//                    var msg = intent.getStringExtra("")
//
//                } else {
//
//                }
//
//        }
//    }


    fun initObserver() {
        phoneSignUpViewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.showProgressBar(
                    this@PhoneSignUpActivity,
                    getString(R.string.loading)
                )
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        phoneSignUpViewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

        phoneSignUpViewModel.onSuccessfullySendOtp.observe(this, Observer {
            if (it.exist == "1") {
                it.user!!.image?.let {
                    AllLocalHandler.userDetail.imageUrl =it
                }
                AllLocalHandler.userDetail.name = it.user!!.name!!
                AllLocalHandler.userDetail.token = it.access_token!!
                AllLocalHandler.userDetail.role = it.user!!.role!!


                sharedPref?.setLong(Constants.USER_ID, it.user!!.id!!)
            }
            startActivity(
                Intent(this@PhoneSignUpActivity, CodeVerificationActivity::class.java)
                    .putExtra("otp", it.otp.toString())
                    .putExtra("phoneNumber", phoneSignUpViewModel.phone.get())
                    .putExtra("phoneCode", phoneSignUpViewModel.phoneCode)
                    .putExtra("from", "phoneSignUp")
                    .putExtra("isExist", it.exist)
                    .putExtra("currentphoneNumber",phone_number)
            )
        })
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        if( requestCode==REQUEST_READ_PHONE_STATE){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TODO
                val tMgr = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                if(getSimState(tMgr.getSimState())==true){
                    phone_number = tMgr.line1Number
                    Log.e("phoneNo",""+phone_number)
                }


            }

            else  {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    REQUEST_READ_PHONE_STATE)
            }
        }
    }


    fun getSimState(simStatus: Int):Boolean{

        when (simStatus) {
            TelephonyManager.SIM_STATE_ABSENT -> {
                return false
            }
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> {
                return false
            }
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> {
                return false
            }
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> {
                return false
            }
            TelephonyManager.SIM_STATE_READY -> {
                return true
            }
            TelephonyManager.SIM_STATE_UNKNOWN -> {
                return false
            }
        }
        return true
    }

}
