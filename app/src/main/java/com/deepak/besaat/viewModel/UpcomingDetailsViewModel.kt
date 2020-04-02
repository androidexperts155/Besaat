package com.deepak.besaat.viewModel

import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.ApiException
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.utils.NoInternetException
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.customerSupport.model.CustomerSupportModel
import com.google.gson.JsonElement
import org.json.JSONObject
import java.io.File

class UpcomingDetailsViewModel(respository: Repository) : BaseViewModel() {

    var respository: Repository
    var jsonResponse: MutableLiveData<JSONObject> = MutableLiveData()
    var upcomigTripResponse: MutableLiveData<JSONObject> = MutableLiveData()
    var deleteTripResponse: MutableLiveData<CustomerSupportModel> = MutableLiveData()
    var pastTripResponse: MutableLiveData<JSONObject> = MutableLiveData()

    init {
        this.respository = respository
    }

    fun AddNewTrip(
        authorization: String,
        departureCountry: String,
        departureDate: String,
        arrivalDate: String,
        arrivalCountry: String,
        imagee: File
    ) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = respository.addTrip(
                    authorization,
                    departureCountry,
                    departureDate,
                    arrivalDate,
                    arrivalCountry,
                    imagee
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    jsonResponse.value = JSONObject(response.toString())

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

    fun upcomigTrip(authorization: String, id: Long?) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = respository.upcomigTrips(authorization, id)
                Coroutines.mainThread {
                    progressBar.value = false
                    upcomigTripResponse.value = JSONObject(response.string())

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

    fun deleteTrip(authorization: String, id: String?) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = respository.deleteTrips(authorization, id)
                Coroutines.mainThread {
                    progressBar.value = false
                    deleteTripResponse.value = response
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

    fun pastTrip(authorization: String, id: Long?) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = respository.pastTrips(authorization, id)
                Coroutines.mainThread {
                    progressBar.value = false
                    pastTripResponse.value = JSONObject(response.string())

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