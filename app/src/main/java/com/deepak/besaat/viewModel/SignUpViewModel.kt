package com.deepak.besaat.viewModel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import androidx.lifecycle.ViewModel
import com.deepak.besaat.view.activities.phoneSignUp.PhoneSignUpActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import org.json.JSONObject
import java.util.*
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.view.activities.signup.AuthenticationDialog
import com.deepak.besaat.view.activities.signup.AuthenticationListener
import android.webkit.CookieSyncManager
import android.webkit.ValueCallback
import com.deepak.besaat.model.socialConfiguration.SocialConfiguration
import com.deepak.besaat.model.socialLoginModel.SocialLoginResponse
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.network.NetworkConnectionInterceptor
import com.deepak.besaat.utils.ApiException
import com.deepak.besaat.utils.NoInternetException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException


class SignUpViewModel(respository: Repository) : BaseViewModel(), AuthenticationListener {

    var instaClientId = ""
    var instaRedirectUrl = ""

    var callbackManager: CallbackManager? = null
    var mGoogleSignInClient: GoogleSignInClient? = null

    var googleButtonClick: MutableLiveData<Boolean> = MutableLiveData()
    var successfullyLogin: MutableLiveData<SocialLoginResponse> = MutableLiveData()
    var onSuccessfullyGetSocialConfiguration: MutableLiveData<SocialConfiguration> =
        MutableLiveData()


    lateinit var authenticationDialog: AuthenticationDialog
    var respository: Repository

    init {
        this.respository = respository
    }

    fun signUpPhoneNumberClick(view: View) {
        AllLocalHandler.userDetail.email = ""
        AllLocalHandler.userDetail.phone = ""
        AllLocalHandler.userDetail.imageUrl = ""
        AllLocalHandler.userDetail.fbId = ""
        AllLocalHandler.userDetail.googleId = ""
        AllLocalHandler.userDetail.instaId = ""
        AllLocalHandler.userDetail.image = null
        var int = Intent(view.context, PhoneSignUpActivity::class.java)
        int.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        view.context.startActivity(int)
    }

    fun fbClick(view: View) {
        var loginmanager = LoginManager.getInstance()
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null) {
            loginmanager.logOut()
        }
        loginmanager.logInWithReadPermissions(
            view.context as Activity,
            Arrays.asList("public_profile", "email")
        )
        Log.e("facebookCall", "Callback manager  $callbackManager")

        loginmanager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                var request = GraphRequest.newMeRequest(
                    result.accessToken,
                    object : GraphRequest.GraphJSONObjectCallback {
                        override fun onCompleted(jsonObject: JSONObject, response: GraphResponse?) {
                            var email: String
                            val id: String
                            val name: String
                            val first_name: String
                            val last_name: String
                            email = jsonObject.getString("email")
                            id = jsonObject.getString("id")

                            name = jsonObject.getString("name")
                            first_name = jsonObject.getString("first_name")
                            last_name = jsonObject.getString("last_name")
                            val picture = jsonObject.getJSONObject("picture")
                            val data = picture.getJSONObject    ("data")
                            val profile_img_url = data.getString("url")
                            var isDefaultProfileImage:Boolean =true
                            isDefaultProfileImage=data.optBoolean("is_silhouette")
//                            socialLogin(name, email, profile_img_url, "", "", "", id)
                            socialLogin(name, email, profile_img_url, "", "", "", ""+id,isDefaultProfileImage)
                        }
                    })
                val parameters = Bundle()
                parameters.putString(
                    "fields",
                    "id,name,email,first_name,last_name,picture.width(300).height(300)"
                )
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                Log.e("cancel", "done")
            }

            override fun onError(error: FacebookException?) {
                Log.e("error", error!!.message)
            }

        })
    }

    fun gmailClick(view: View) {
        mGoogleSignInClient!!.signOut()
        googleButtonClick.value = true
    }

    fun instagramClick(view: View) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies { }
        authenticationDialog =
            AuthenticationDialog(view.context, this, instaClientId, instaRedirectUrl)
        authenticationDialog.setCancelable(true)
        authenticationDialog.show()
    }

    override fun onTokenReceived(auth_token: String?) {
        Coroutines.backThread({
            try {
                var response =
                    respository.getInstagramLoginDetail(Constants.GET_INSTA_USER_INFO + auth_token)
                var jsonObject = JSONObject(response.string())
                Log.e("response", jsonObject.toString())
                val jsonData = jsonObject.getJSONObject("data")
                var userName = jsonData.getString("username")
                var userId = jsonData.getString("id")
                var profilePic = jsonData.getString("profile_picture")
                Log.e("UserName", userName)
                Log.e("UserId", userId)
                Log.e("UserProfilePic", profilePic)
                socialLogin(userName, "", profilePic, "", userId, "", "",false
                )
            } catch (e: ApiException) {
                Coroutines.mainThread {
                    feedBackMessage.value = e.message
                }
            } catch (e: NoInternetException) {
                Coroutines.mainThread {
                    feedBackMessage.value = e.message
                }
            }
        })
    }

    fun socialLogin(
        name: String,
        email: String,
        image: String,
        phone: String,
        instagramId: String,
        googleId: String,
        fbId: String,
        isDefaultImage:Boolean
    ) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response =
                    respository.socialLogin(name, email, image, phone, instagramId, googleId, fbId)
                AllLocalHandler.userDetail.email = email
                AllLocalHandler.userDetail.name = name
                AllLocalHandler.userDetail.imageUrl = image
                AllLocalHandler.userDetail.instaId = instagramId
                AllLocalHandler.userDetail.googleId = googleId
                AllLocalHandler.userDetail.fbId = fbId
                AllLocalHandler.userDetail.phone = ""
                AllLocalHandler.userDetail.isDefaultProfileImage = isDefaultImage
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyLogin.value = response
                }
            } catch (e: ApiException) {
                Coroutines.mainThread {
                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            } catch (e: NoInternetException) {
                Coroutines.mainThread {
                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            }
        }
    }


    fun socialConfiguration() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = respository.socialConfiguration()
                Coroutines.mainThread {
                    progressBar.value = false
                    onSuccessfullyGetSocialConfiguration.value = response
                    instaClientId = response.social!!.instaClientId!!
                    instaRedirectUrl = response.social!!.instaRedirectUrl!!
                }
            } catch (e: ApiException) {
                Coroutines.mainThread {
                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            } catch (e: NoInternetException) {
                Coroutines.mainThread {
                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            }
        }
    }
}