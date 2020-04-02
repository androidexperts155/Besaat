package com.deepak.besaat.viewModel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import java.lang.Exception


class JobDetailsViewModel(
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var getResponse: MutableLiveData<String> = MutableLiveData()
    var getRequestResponse: MutableLiveData<String> = MutableLiveData()
    var receiverID: ObservableField<String> = ObservableField()
    var requestID: ObservableField<String> = ObservableField()
    var wantToCancelClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var changeStatusClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var makeOfferClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var acceptClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var rejectClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var viewDirectionClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var chatClickObserver: MutableLiveData<Boolean> = MutableLiveData()

    fun changeStatusOnClick(view: View) {
        changeStatusClickObserver.value = true
    }

    fun wantToCancelClick(view: View) {
        wantToCancelClickObserver.value = true
    }

    fun makeOfferClick(view: View) {
        makeOfferClickObserver.value = true
    }

    fun acceptClick(view: View) {
        acceptClickObserver.value = true
    }

    fun rejectClick(view: View) {
        rejectClickObserver.value = true
    }

    fun viewDirectionClick(view: View) {
        viewDirectionClickObserver.value = true
    }

    fun chatClick(view: View) {
        chatClickObserver.value = true
    }


    fun changeOrderStatus(reqType: String,status: String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.changeOrderStatus(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    requestID.get(), reqType, status
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    getResponse.value = response.toString()
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

    fun acceptOrRejectOrMakeOfferJob(requestID:String,offer:String,status:String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.acceptOrRejectJob(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    requestID,offer, status
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    getResponse.value = response.toString()
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
                    requestID.get(),Constants.DETAILS_JOB
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    getRequestResponse.value = response.toString()
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