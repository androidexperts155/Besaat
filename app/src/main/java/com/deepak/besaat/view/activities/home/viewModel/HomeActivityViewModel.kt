package com.deepak.besaat.view.activities.home.viewModel

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

class HomeActivityViewModel(
    repository: Repository,
    sharedPref: SharedPref
) :
    BaseViewModel() {
    val repository = repository
    var onSuccessfullyUpdateLocation: MutableLiveData<String> =MutableLiveData()
    val sharedPref = sharedPref
    var lat: ObservableField<String> = ObservableField()
    var lng: ObservableField<String> = ObservableField()
    var address: ObservableField<String> = ObservableField()

    fun updateAddress() {
        Coroutines.backThread {
            try {
                val response =
                    repository.updateAddress(
                        Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                        address.get(), lat.get(), lng.get()
                    )
                Coroutines.mainThread {
                    progressBar.value = false
                    onSuccessfullyUpdateLocation.value = response.string()
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