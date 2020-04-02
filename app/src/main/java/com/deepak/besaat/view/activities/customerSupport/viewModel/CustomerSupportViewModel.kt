package com.deepak.besaat.view.activities.customerSupport.viewModel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.customerSupport.model.CustomerSupportModel
import com.deepak.besaat.viewModel.BaseViewModel
import com.google.gson.Gson
import org.koin.android.ext.android.inject

class CustomerSupportViewModel(
    repository: Repository,
    frequentFunctions: FrequentFunctions,
    validations: Validations, sharedPref: SharedPref
) :
    BaseViewModel() {
    val frequentFunctions = frequentFunctions
    val repository = repository
    val validations = validations
    var onSuccessfullySubmit: MutableLiveData<CustomerSupportModel> =MutableLiveData() // here at the place of Any you can specify Model class
    val sharedPref = sharedPref
    var type: ObservableField<String> = ObservableField()
    var issue: ObservableField<String> = ObservableField()
    var id: ObservableField<String> = ObservableField()

    fun onContinueClick(view: View) {
        frequentFunctions.hideKeyBoard(view.context, view)
        if (validations.isFieldEmpty(issue.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_issue)
        } else if (validations.isFieldEmpty(type.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_support_type)
        } else if (validations.isFieldEmpty(id.get())) {
            feedBackMessage.value = view.context.getString(R.string.select_id)
        } else {
            submitIssueForEditOrDeleteVisit()
        }
    }

    private fun submitIssueForEditOrDeleteVisit() {
        progressBar.value = true
        var typeID: String = "3" // change when there will be multiple types
        if (type.get().equals("trip", true))
            typeID = "3"


        Coroutines.backThread {
            try {
                val response =
                    repository.addSupportRequest(
                        Constants.BEARER + " " + sharedPref.getString(
                            Constants.TOKEN
                        ), typeID, id.get(), issue.get()
                    )
                Coroutines.mainThread {
                    progressBar.value = false
                    onSuccessfullySubmit.value = Gson().fromJson(response.toString(),CustomerSupportModel::class.java)
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