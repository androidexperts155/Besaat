package com.deepak.besaat.viewModel

import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.ApiException
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.utils.NoInternetException
import org.json.JSONObject
import java.io.File

class PastDetailsViewModel(respository: Repository) : BaseViewModel() {

    var respository: Repository
    var jsonResponse: MutableLiveData<JSONObject> = MutableLiveData()
    var editJsonResponse: MutableLiveData<JSONObject> = MutableLiveData()


    init {
        this.respository = respository

    }

    fun AddNewTrip(
        authorization: String,
        departureCountry: String?,
        departureDate: String,
        arrivalDate: String,
        arrivalCountry: String?,
        imagee: File?
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
                    jsonResponse.value = JSONObject(response.string())

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

    fun editTrip(
        authorization: String,
        tripId: String?,
        departureCountry: String?,
        departureDate: String,
        arrivalDate: String,
        arrivalCountry: String?,
        imagee: File?
    ) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = respository.editTrip(
                    authorization,
                    tripId,
                    departureCountry,
                    departureDate,
                    arrivalDate,
                    arrivalCountry,
                    imagee
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    editJsonResponse.value = JSONObject(response.string())
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

    fun pastTrip(authorization: String, id: Long) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = respository.pastTrips(authorization, id)
                Coroutines.mainThread {
                    progressBar.value = false
                    jsonResponse.value = JSONObject(response.string())

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