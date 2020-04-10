package com.deepak.besaat.viewModel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import java.lang.Exception


class RequestStatusViewModel(
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var getRateResponse: MutableLiveData<String> = MutableLiveData()
    var receiverID: ObservableField<String> = ObservableField()
    var requestID: ObservableField<String> = ObservableField()
    var wantToCancelClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var viewDirectionClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var chatClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var reorderClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var rateServiceProviderClickObserver: MutableLiveData<Boolean> = MutableLiveData()

    fun rateServiceProviderClick(view: View) {
        rateServiceProviderClickObserver.value = true
    }

    fun wantToCancelClick(view: View) {
        wantToCancelClickObserver.value = true
    }

    fun chatClick(view: View) {
        chatClickObserver.value = true
    }

    fun reorderClick(view: View) {
        reorderClickObserver.value = true
    }


    fun viewDirectionClick(view: View) {
        viewDirectionClickObserver.value = true
    }

    fun rateUser(rating:String, receiverID:String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.rateUser(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    requestID.get(), receiverID , rating
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    getRateResponse.value = response.toString()
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
            } catch (e: Exception) {
                Coroutines.mainThread {
                    progressBar.value = false
                    feedBackMessage.value = e.message
                }
            }
        }
    }

    fun getRequestDetails() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getRequestDetails(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    requestID.get(),Constants.DETAILS_REQUEST
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    getRateResponse.value = response.toString()
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
            } catch (e: Exception) {
                Coroutines.mainThread {
                    progressBar.value = false
                    feedBackMessage.value = e.message
                }
            }
        }
    }
}