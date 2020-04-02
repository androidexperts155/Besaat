package com.deepak.besaat.viewModel

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.model.buyerSignUpModel.BuyerSignUpResponse
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.selectDeliveryTypes.SelectDeliveryTypes
import com.deepak.besaat.view.activities.verifyCode.CodeVerificationActivity
import java.io.File

class CreateProfileViewModel(
    validations: Validations,
    frequentFunctions: FrequentFunctions,
    respository: Repository,
    sharedPref: SharedPref
) :
    BaseViewModel() {
    var validations = validations
    var frequentFunctions = frequentFunctions
    var repository = respository

    var accountType = 0 //   0 Default,1 for buyer , 2 for Provider


    var imageClick: MutableLiveData<Boolean> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()
    var email: MutableLiveData<String> = MutableLiveData()
    var phone: MutableLiveData<String> = MutableLiveData()
    var onSuccessfullySignUp: MutableLiveData<BuyerSignUpResponse> = MutableLiveData()
    var file: File? = null
    var selectedCountryCode = ""
    var from = ""
    val sharedPref = sharedPref

    fun imageClick(view: View) {
        imageClick.value = true
    }

    fun onContinueClick(view: View) {
        frequentFunctions.hideKeyBoard(view.context, view)
        if (AllLocalHandler.userDetail.imageUrl == "" && file == null) {
            feedBackMessage.value = view.context.getString(R.string.empty_profile_image)
        }
//        else if (AllLocalHandler.userDetail.isDefaultProfileImage && file == null) {
//            feedBackMessage.value = view.context.getString(R.string.empty_profile_image)
//        }
// uncomment this if you don't want to use default placeholder of facebook
        else if (validations.isFieldEmpty(name.value)) {
            feedBackMessage.value = view.context.resources.getString(R.string.empty_name)
        } else if (!validations.isMinLength2(name.value.toString())) {
            feedBackMessage.value = view.context.resources.getString(R.string.min_length_name)
        } else if (validations.isFieldEmpty(email.value?.trim())) {
            feedBackMessage.value = view.context.resources.getString(R.string.empty_email)
        } else if (validations.isInvalidEmail(email.value?.trim())) {
            feedBackMessage.value = view.context.resources.getString(R.string.invalid_email)
        } else if (validations.isFieldEmpty(phone.value)) {
            feedBackMessage.value = view.context.resources.getString(R.string.empty_phone)
        } else if (!validations.isValidMobile(phone.value.toString())) {
            feedBackMessage.value = view.context.resources.getString(R.string.enter_valid_phone)
        } else if (accountType == 0) {
            feedBackMessage.value = view.context.getString(R.string.empty_account_type)
        } else {
            var imageUrl = ""
            if (file == null) {
                imageUrl = AllLocalHandler.userDetail.imageUrl
            }
            if (accountType == 1) {
                progressBar.value = true
                Coroutines.backThread {
                    try {
                        var response = repository.buyerSignUp(
                            name.value!!,
                            email.value!!.trim(),
                            "+" + selectedCountryCode + "-" + phone.value!!,
                            AllLocalHandler.userDetail.instaId,
                            AllLocalHandler.userDetail.googleId,
                            AllLocalHandler.userDetail.fbId,
                            imageUrl,
                            file
                        )
                        Coroutines.mainThread {
                            progressBar.value = false
                            onSuccessfullySignUp.value = response
                        }
                    } catch (e: ApiException) {
                        Coroutines.mainThread {
                            progressBar.value = false
                            feedBackMessage.value = e.message
                        }


                    } catch (e: NoInternetException) {
                        Coroutines.mainThread {
                            progressBar.value = false
                            feedBackMessage.value = e.message
                        }
                    }
                }
            } else {
                AllLocalHandler.userDetail.name = name.value!!
                AllLocalHandler.userDetail.email = email.value!!.trim()
                AllLocalHandler.userDetail.phone = phone.value!!
                AllLocalHandler.userDetail.phoneCode = selectedCountryCode!!
                if (from.equals("CodeVerification")) {
                    hitSendOtpApi(view, "1")
                } else {
                    hitSendOtpApi(view, "0")
                }
            }
        }
    }

    private fun hitSendOtpApi(view: View, isVerified: String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                var authorization = Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN)
                val response =
                    repository.sendSignOtp(
                        authorization,
                        phone.value!!,
                        "+" + selectedCountryCode,
                        email.value!!.trim(),
                        isVerified
                    )
                Coroutines.mainThread {
                    progressBar.value = false
                    if (response.status!!) {
                        if (from.equals("CodeVerification")) {
                            view.context.startActivity(
                                Intent(
                                    view.context,
                                    SelectDeliveryTypes::class.java
                                )
                            )
                        } else {
                            view.context.startActivity(
                                Intent(view.context, CodeVerificationActivity::class.java)
                                    .putExtra("otp", response.otp.toString())
                                    .putExtra("phoneNumber", phone.value!!)
                                    .putExtra("phoneCode", selectedCountryCode)
                                    .putExtra("from", "createProfile")
                                    .putExtra("isExist", "0")
                            )
                        }
                    } else {
                        feedBackMessage.value = response.message
                    }
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

    fun radioGroupOnCheckedChange(radioGroup: RadioGroup, id: Int) {
        Log.e("radio group", radioGroup.id.toString())
        when (radioGroup.checkedRadioButtonId) {
            R.id.radioButtonBuyer -> {
                accountType = 1
            }
            R.id.radioButtonProvider -> {
                accountType = 2
            }
        }
    }
}