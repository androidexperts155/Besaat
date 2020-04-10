package com.deepak.besaat.viewModel

import android.content.Intent
import android.view.View
import androidx.databinding.ObservableField
import com.deepak.besaat.R
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.createprofile.CreateProfileActivity
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.activities.selectDeliveryTypes.SelectDeliveryTypes
import com.google.gson.Gson

class CodeVerificationViewModel(
    validations: Validations,
    repository: Repository,
    frequentFunctions: FrequentFunctions,
    sharedPref: SharedPref
) : BaseViewModel() {
    val repository = repository
    val validations = validations
    val frequentFunctions = frequentFunctions
    val sharedPref = sharedPref

    var otp = ""
    var phoneNumber = ""
    var phoneCode = ""
    var from = ""
    var isExist = ""
    var currentPhoneNo=""
    var firstEditText: ObservableField<String> = ObservableField()
    var secondEditText: ObservableField<String> = ObservableField()
    var thirdEditText: ObservableField<String> = ObservableField()
    var fourthEditText: ObservableField<String> = ObservableField()
    var clickedOnce:Boolean =false

    fun submitClick(view: View) {
        frequentFunctions.hideKeyBoard(view.context, view)
        if (validations.isFieldEmpty(firstEditText.get()) || validations.isFieldEmpty(secondEditText.get()) || validations.isFieldEmpty(
                thirdEditText.get()
            ) || validations.isFieldEmpty(fourthEditText.get())
        ) {
            feedBackMessage.value = view.context.getString(R.string.empty_otp)
        } else if (otp == firstEditText.get() + secondEditText.get() + thirdEditText.get() + fourthEditText.get()) {
            AllLocalHandler.userDetail.phone = phoneNumber
            AllLocalHandler.userDetail.phoneCode = phoneCode
            if (from == "createProfile") {
                view.context.startActivity(
                    Intent(
                        view.context,
                        SelectDeliveryTypes::class.java
                    )
                )
            } else {
                if(isExist == "1"){
                    sharedPref.setString(Constants.USER_IMAGE, AllLocalHandler.userDetail.imageUrl )
                    sharedPref.setString(Constants.USER_NAME, AllLocalHandler.userDetail.name!!)
                    sharedPref.setString(Constants.TOKEN, AllLocalHandler.userDetail.token)
                    sharedPref.setString(Constants.ROLE, AllLocalHandler.userDetail.role)
                    view.context.startActivity(Intent(view.context,HomeActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }else{
                    view.context.startActivity(
                        Intent(
                            view.context,
                            CreateProfileActivity::class.java
                        ).putExtra("from", "CodeVerification")
                    )
                }
            }
        } else {
            feedBackMessage.value = view.context.getString(R.string.otp_no_valid)
        }
    }

    fun resendCode(view: View) {
        if(clickedOnce==false) {
            clickedOnce=true
            hitSendOtpApi(view)
        }
    }


    private fun hitSendOtpApi(view: View) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                var authorization = Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN)
                val response = repository.sendOtp(authorization, phoneNumber,"+"+phoneCode )
                Coroutines.mainThread {
                    clickedOnce=false
                    progressBar.value = false
                    otp = response.otp.toString()
                    feedBackMessage.value = view.context.getString(R.string.code_resend)
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