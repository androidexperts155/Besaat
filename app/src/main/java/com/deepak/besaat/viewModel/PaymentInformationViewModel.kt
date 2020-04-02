package com.deepak.besaat.viewModel

import android.database.Observable
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.model.providerSignupModel.ProviderSignUpResponse
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*

class PaymentInformationViewModel(repository: Repository, frequentFunctions: FrequentFunctions,validations: Validations) :
    BaseViewModel() {
    val frequentFunctions = frequentFunctions
    val repository = repository
    val validations=validations
    var onSuccessfullySignUp: MutableLiveData<ProviderSignUpResponse> = MutableLiveData()

    var name: ObservableField<String> = ObservableField()
    var accountNumber: ObservableField<String> = ObservableField()
    var ifscCode: ObservableField<String> = ObservableField()

    fun onContinueClick(view: View) {
        frequentFunctions.hideKeyBoard(view.context, view)
        if(validations.isFieldEmpty(name.get())){
            feedBackMessage.value=view.context.getString(R.string.empty_name)
        }
       else if(!validations.isMinLength2(name.get().toString())){
            feedBackMessage.value=view.context.getString(R.string.min_length_name)
        }
        else if(validations.isFieldEmpty(accountNumber.get())){
            feedBackMessage.value=view.context.getString(R.string.empty_account_number)
        }

        else if (!validations.isMinLength10(accountNumber.get().toString())) {
            feedBackMessage.value = view.context.resources.getString(R.string.min_length_account_nm)
        }
        else if(validations.isFieldEmpty(ifscCode.get())){
            feedBackMessage.value=view.context.getString(R.string.empty_ifsc_code)
        }
        else if (!validations.isMinLength6(ifscCode.get().toString())) {
            feedBackMessage.value = view.context.resources.getString(R.string.min_length_ifsc)
        }
        else{
            AllLocalHandler.providerSignUpDetailToServer.account_holder_name=name.get()!!
            AllLocalHandler.providerSignUpDetailToServer.account_number=accountNumber.get()!!
            AllLocalHandler.providerSignUpDetailToServer.ifsc_code=ifscCode.get()!!
            providerSignUp()
        }
    }
    private fun providerSignUp() {
        progressBar.value = true
        Coroutines.backThread {
            try {
                val response =
                    repository.providerSignUp(AllLocalHandler.providerSignUpDetailToServer)
                Coroutines.mainThread {
                    progressBar.value = false
                    onSuccessfullySignUp.value = response
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