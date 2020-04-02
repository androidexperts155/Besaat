package com.deepak.besaat.viewModel

import android.util.Log
import android.widget.RadioGroup
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.ApiException
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.utils.NoInternetException
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class NewRequestStoreViewModel(respository: Repository) : BaseViewModel() {
    var accountType: MutableLiveData<Int> = MutableLiveData()
    var createJsonResponse: MutableLiveData<JSONObject> = MutableLiveData()
    var fairJsonResponse: MutableLiveData<JSONObject> = MutableLiveData()
    var nearbyProviderResponse: MutableLiveData<JSONObject> = MutableLiveData()
    var respository: Repository

    init {
        this.respository = respository
    }

    fun radioGroupOnCheckedChange(radioGroup: RadioGroup, id: Int) {
        Log.e("radio group", radioGroup.id.toString())
        when (radioGroup.checkedRadioButtonId) {
            R.id.radioButtonAutoTransmission1 -> {
                accountType.value = 1
            }
            R.id.radioButtonManualTransmission1 -> {
                accountType.value = 2
            }
        }
    }

    fun executeFairApi(authorization: String) {
        var value = Coroutines.backThread {
            var jsonObject: JSONObject? = null
            try {
                val response = respository.getFair(authorization)
                Log.e("RequestResponse", "response of get fair " + response)
                jsonObject = JSONObject(response.string());
                Coroutines.mainThread {
                    fairJsonResponse.value = jsonObject
                }
            } catch (e: JSONException) {
                Log.e("expNewReq", "" + e.message)
            }
        }
    }

    fun executenearbyproviders(authorization: String, lat: String, long: String) {
        Coroutines.backThread {
            var jsonObject: JSONObject? = null
            try {
                val response = respository.nearByProvider(authorization, lat, long)
                Log.e("RequestResponse", "request response is " + response)
                jsonObject = JSONObject(response.string());
                Log.e("RequestResponse", "response on view moodel " + jsonObject.toString())
                Coroutines.mainThread {
                    progressBar.value = false
                    nearbyProviderResponse.value = jsonObject
                }
            } catch (e: ApiException) {
                Coroutines.mainThread {
                    progressBar.value = false
                    nearbyProviderResponse.value = jsonObject
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

    fun executeCreateRequest(
        aurhorization: String,
        name: String,
        title: String,
        pickUpAddress: String,
        pickUpLatitute: String,
        pickUpLongitute: String,
        dropAddress: String,
        dropLatitute: String,
        dropLongitute: String,
        orderinfo: String,
        specialNote: String,
        charges: String,
        paymentMethod: String,
        requestType: String,
        requestImage: File,
        driverSelectionType: String
    ) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            var jsonObject: JSONObject? = null
            try {
                val response = respository.createRequest(
                    aurhorization,
                    name,
                    title,
                    pickUpAddress,
                    pickUpLatitute,
                    pickUpLongitute,
                    dropAddress,
                    dropLatitute,
                    dropLongitute,
                    orderinfo,
                    specialNote,
                    charges,
                    paymentMethod,
                    requestType,
                    requestImage,
                    driverSelectionType
                )
                Log.e("RequestResponse", "request response is " + response)
                jsonObject = JSONObject(response.string());
                Log.e("RequestResponse", "response on view moodel " + jsonObject.toString())
                Coroutines.mainThread {
                    progressBar.value = false
                    createJsonResponse.value = jsonObject
                }
            } catch (e: ApiException) {
                Coroutines.mainThread {
                    progressBar.value = false
                    createJsonResponse.value = jsonObject
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