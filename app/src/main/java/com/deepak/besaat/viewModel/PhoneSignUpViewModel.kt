package com.deepak.besaat.viewModel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deepak.besaat.R
import com.deepak.besaat.model.sendOtpModel.SendOtpResponse
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.phoneSignUp.PhoneSignUpActivity

class PhoneSignUpViewModel(
    activity: Application,
    repository: Repository,
    sharedPreferences: SharedPref,
    validations: Validations, frequentFunctions: FrequentFunctions
) :
    BaseViewModel() {
    val activity = activity
    val repository = repository
    val sharedPref = sharedPreferences
    val validations = validations
    val frequentFunctions = frequentFunctions
    var onSuccessfullySendOtp: MutableLiveData<SendOtpResponse> = MutableLiveData()
    var phoneCode = ""
    var phone: ObservableField<String> = ObservableField()
    var multipleClick:Boolean=false

    fun continueClick(view: View) {
        frequentFunctions.hideKeyBoard(view.context, view)
        if (validations.isFieldEmpty(phone.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_phone)
        } else if (!validations.isValidMobile(phone.get().toString())) {
            feedBackMessage.value = view.context.getString(R.string.enter_valid_phone)
        } else {
            if(multipleClick==false) {
                multipleClick=true
                hitSendOtpApi()
            }
        }
    }

    private fun hitSendOtpApi() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                var authorization = Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN)
                val response = repository.sendOtp(authorization, phone.get()!!, phoneCode)
                Coroutines.mainThread {
                    progressBar.value = false
                    onSuccessfullySendOtp.value = response
                    multipleClick=false

                    val intent = Intent()
                    intent.action = Constants.MESSAGE_CODE
                    intent.putExtra(Constants.MESSAGE_CODE_STRING, response.otp)
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    activity.sendBroadcast(intent)
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