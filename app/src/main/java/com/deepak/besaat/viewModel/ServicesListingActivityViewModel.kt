package com.deepak.besaat.viewModel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.model.getServiceProviderModel.GetProvidersPojo
import com.deepak.besaat.model.getServicesModel.GetServicesResponse
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*

class ServicesListingActivityViewModel(
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var successfullyGetServices: MutableLiveData<GetServicesResponse> = MutableLiveData()
    var successfullyGetServiceProviders: MutableLiveData<GetProvidersPojo> = MutableLiveData()
    var listMapIocnClick: MutableLiveData<Boolean> = MutableLiveData()
    var changeLocationClick: MutableLiveData<Boolean> = MutableLiveData()
    var sort: ObservableField<String> = ObservableField()
    var order: ObservableField<String> = ObservableField()
    var search: ObservableField<String> = ObservableField()
    var service_id: ObservableField<String> = ObservableField()
    var latitude: ObservableField<String> = ObservableField()
    var logitude: ObservableField<String> = ObservableField()
    var radius: ObservableField<String> = ObservableField()
    var rating: ObservableField<String> = ObservableField()

    fun changeLocationClickFun(view: View) {
        changeLocationClick.value = true
    }

    fun listorMapIconClick(view: View) {
        listMapIocnClick.value = true
    }

    fun getServices() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getAllServices()
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetServices.value = response
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

    fun getServiceProviders(url: String) {
        Coroutines.mainThread {
            feedBackMessage.value = "Loading..."
//            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getServiceProviders(
                    url,
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    sort.get()!!,order.get()!!,
                    search.get()!!,
                    service_id.get()!!, latitude.get()!!,
                    logitude.get()!!, radius.get()!!, rating.get()!!
                )
                Coroutines.mainThread {
//                    progressBar.value = false
                    successfullyGetServiceProviders.value = response
                }
            } catch (e: ApiException) {
                Coroutines.mainThread {
//                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            } catch (e: NoInternetException) {
                Coroutines.mainThread {
//                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            }
        }
    }

}