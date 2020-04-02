package com.deepak.besaat.viewModel

import android.util.Log
import android.view.View
import android.widget.RadioGroup
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.network.NetworkConstants
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import java.lang.Exception

class MyOrdersViewModel(
    val validations: Validations,
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var successfullyGetOrders: MutableLiveData<String> = MutableLiveData()
    var getResponse: MutableLiveData<String> = MutableLiveData()
    var requestType: MutableLiveData<String> = MutableLiveData()
    var status: MutableLiveData<String> = MutableLiveData()
    var orderType: MutableLiveData<String> = MutableLiveData()
    var filterClick: MutableLiveData<Boolean> = MutableLiveData()

    fun filterOnClick(view: View) {
        filterClick.value = true
    }

    fun getOrders() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getOrders(
                    orderType.value!!,
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    requestType.value!!, status.value!!
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetOrders.value = response.toString()
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

    fun acceptOrRejectJob(requestID:String,offer:String,status:String) {
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

    fun acceptOrRejectCancelRequest(requestID:String,status:String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.acceptOrRejectCancelRequest(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    requestID,status
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

    fun radioGroupOnCheckedChange(radioGroup: RadioGroup, id: Int) {
        when (radioGroup.checkedRadioButtonId) {
            R.id.radioButtonMyRequests -> {
                orderType.value = NetworkConstants.MY_REQUESTS
                getOrders()
            }
            R.id.radioButtonMyJobs -> {
                orderType.value = (NetworkConstants.MY_JOBS)
                getOrders()
            }
            R.id.radioButtonDelivery -> {
                requestType.value = (Constants.REQUEST_TYPE_STORE)
                getOrders()
            }
            R.id.radioButtonServices -> {
                requestType.value = (Constants.REQUEST_TYPE_SERVICE)
                getOrders()
            }
            R.id.radioButtonCourier -> {
                requestType.value = (Constants.REQUEST_TYPE_COURIER)
                getOrders()
            }
        }
    }
}