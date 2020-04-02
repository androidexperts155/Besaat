package com.deepak.besaat.viewModel

import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import java.lang.Exception

class MyOrdersOffersViewModel(
    val validations: Validations,
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var successfullyGetOffers: MutableLiveData<String> = MutableLiveData()
    var getResponse: MutableLiveData<String> = MutableLiveData()
    var requestID: MutableLiveData<String> = MutableLiveData()

    fun getReqOffers(url:String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getReqOffers(
                    url,
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    requestID.value!!
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetOffers.value = response.toString()
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

    fun acceptOffer(offerID:String,typeID:String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.acceptOffer(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    offerID, typeID
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

}