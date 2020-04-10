package com.deepak.besaat.view.activities.signup

import android.content.Context
import android.os.Bundle
import com.deepak.besaat.R
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.databinding.ActivitySignupBinding
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.createprofile.CreateProfileActivity
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.viewModel.SignUpViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_signup.*
import org.koin.android.ext.android.inject
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import com.deepak.besaat.view.activities.termsAndConditions.Terms_and_privacy
import android.text.SpannableString
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.deepak.besaat.Interfaces.LastLocationValues
import com.google.android.gms.auth.api.Auth
import com.google.gson.Gson
import java.io.IOException


class SignupActivity : BaseActivity() {
    val frequentFunctions: FrequentFunctions by inject()
    val signUpViewModel: SignUpViewModel by inject()
    val commonFunctions: CommonFunctions by inject()

    var callbackManager: CallbackManager? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var binding: ActivitySignupBinding
    val sharedPref: SharedPref by inject()
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivitySignupBinding>(this, R.layout.activity_signup)
        binding.signUpViewModel = signUpViewModel
        signUpViewModel.socialConfiguration()
        initObserver()
        setUpGoogleClient()
        makeLinks(
            textViewTermsAndPrivacyPolicy,
            arrayOf(resources.getString(R.string.terms_and_privacy)),
            arrayOf(privacyPolicyClick)
        )

        FacebookSdk.sdkInitialize(this)
        callbackManager = CallbackManager.Factory.create()
        signUpViewModel.callbackManager = callbackManager
        // customizeString()
        //FacebookSdk.setApplicationId(it.social!!.fbAppId)
        // FacebookSdk.sdkInitialize(this)
        // callbackManager = CallbackManager.Factory.create()
        //signUpViewModel.callbackManager = callbackManager
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
//                        var countryName = getCurrentCountry(lat, long)
//                        var countryCode =getCOuntryCode.getCountryCodeByCountryName(countryName?.toLowerCase())
                        //  binding!!.ccp!!.setCountryForNameCode("ar")
                    })
                }
            })
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
        }
    }


    /*try {
          val info =getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES)
          for (signature in info.signatures) {
              val md = MessageDigest.getInstance("SHA")
              md!!.update(signature.toByteArray())
              val hashKey = String(Base64.encode(md.digest(), 0))
              Log.i("keyhash", "printHashKey() Hash Key: $hashKey")
          }
      } catch (e: NoSuchAlgorithmException) {
          Log.e("keyhas", "printHashKey()", e)
      } catch (e: Exception) {
          Log.e("keyhash", "printHashKey()", e)
      }*/

    private fun setUpGoogleClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signUpViewModel.mGoogleSignInClient = mGoogleSignInClient
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
        signUpViewModel.googleButtonClick.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    if (frequentFunctions.isGooglePlayServicesAvailable(this@SignupActivity)) {

                        Log.e("Signin", "sign in function is ")
                        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
                        startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN)
                    }
                }
            }
        })

        signUpViewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.showProgressBar(this@SignupActivity, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        signUpViewModel.feedBackMessage.observe(this, Observer {
            // commonFunctions.showFeedbackMessage(rootLayout, it)
            alertDialogWithOKButton2("Alert", it)
        })

        signUpViewModel.onSuccessfullyGetSocialConfiguration.observe(this, Observer {
            FacebookSdk.setApplicationId(it.social!!.fbAppId);

        })




        signUpViewModel.successfullyLogin.observe(this, Observer {
            if (it.status!!) {
                sharedPref.setLong(Constants.USER_ID, it.user!!.id!!)
                sharedPref.setString(Constants.USER_NAME, it.user!!.name!!)
                sharedPref.setString(Constants.TOKEN, it.accessToken!!)
                it.user!!.image?.let {
                    sharedPref.setString(Constants.USER_IMAGE, it)
                }
                sharedPref.setString(Constants.ROLE, it.user!!.role!!)
                var int = Intent(this@SignupActivity, HomeActivity::class.java)
                int.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(int)
                finishAffinity()
            } else {
                var int = Intent(this@SignupActivity, CreateProfileActivity::class.java)
                int.putExtra("from", "signUp")
                int.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int)
                finishAffinity()
            }
        })
    }


    fun makeLinks(textView: TextView, links: Array<String>, clickableSpans: Array<ClickableSpan>) {
        val spannableString = SpannableString(textView.text)

        for (i in links.indices) {
            val clickableSpan = clickableSpans[i]
            val link = links[i]

            val startIndexOfLink = textView.text.indexOf(link)

            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    val privacyPolicyClick = object : ClickableSpan() {
        override fun onClick(p0: View) {
            var intent: Intent = Intent(this@SignupActivity, Terms_and_privacy::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP;
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.GOOGLE_SIGN_IN) {
            Log.e("Signin", "sign in funcion result is ")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.e("Signin", "sign in funcion result is " + task)
            handleSignInResult(task)
        } else {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            var name = ""
            var email = ""
            var photoUrl = ""
            val account = completedTask.getResult(ApiException::class.java)
            Log.e("Signin", "sign in funcion result is account " + account)
            account!!.displayName?.let {
                name = it
            }
            account!!.email?.let {
                email = it
            }
            account!!.photoUrl?.let {
                photoUrl = it.toString()
            }

            signUpViewModel.socialLogin(name, email, photoUrl, "", "", account.id!!, "",false)
//            signUpViewModel.socialLogin(name, "vk1@yopmail.com", photoUrl, "", "", "1478512369", "")
        } catch (e: ApiException) {
            e.printStackTrace()
            Log.w("signInResult:failed cod", e.statusCode.toString())
        }
    }
}
