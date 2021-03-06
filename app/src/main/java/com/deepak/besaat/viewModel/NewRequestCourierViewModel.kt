package com.deepak.besaat.viewModel

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class NewRequestCourierViewModel(
    val validations: Validations,
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var successfullyCreateRequest: MutableLiveData<String> = MutableLiveData()
    var fairJsonResponse: MutableLiveData<JSONObject> = MutableLiveData()
    var changeLocationClick: MutableLiveData<Boolean> = MutableLiveData()
    var changeDropLocationClick: MutableLiveData<Boolean> = MutableLiveData()
    var onDateClick: MutableLiveData<Boolean> = MutableLiveData()
    var onTimeClick: MutableLiveData<Boolean> = MutableLiveData()
    var onImageClick: MutableLiveData<Boolean> = MutableLiveData()

    var pickUpLatitude: ObservableField<String> = ObservableField()
    var pickUpLogitude: ObservableField<String> = ObservableField()
    var pickUpAddress: ObservableField<String> = ObservableField()
    var pickUpCountry: ObservableField<String> = ObservableField()

    var dropLogitude: ObservableField<String> = ObservableField()
    var dropLatitude: ObservableField<String> = ObservableField()
    var dropAddress: ObservableField<String> = ObservableField()
    var dropCountry: ObservableField<String> = ObservableField()


    var title: ObservableField<String> = ObservableField()
    var serviceInfo: ObservableField<String> = ObservableField()
    var specialNote: ObservableField<String> = ObservableField()
    var serviceDate: ObservableField<String> = ObservableField()
    var serviceDateShow: ObservableField<String> = ObservableField()
    var serviceTime: ObservableField<String> = ObservableField()
    var serviceTimeShow: ObservableField<String> = ObservableField()
    var requestType: ObservableField<String> = ObservableField()
    var deliveryType: ObservableField<String> = ObservableField()
    var paymentMethod: ObservableField<String> = ObservableField()
    var name: ObservableField<String> = ObservableField()
    var providerID: ObservableField<String> = ObservableField()
    var distance: ObservableField<String> = ObservableField()
    var fair: ObservableField<Double> = ObservableField()
    var fairPerDistanceUnit: ObservableField<Double> = ObservableField()
    var file: File? = null

    fun changeLocationClickFun(view: View) {
        changeLocationClick.value = true
    }

    fun changeDropLocationClickFun(view: View) {
        changeDropLocationClick.value = true
    }

    fun onDateSelect(view: View) {
        onDateClick.value = true
    }

    fun onTimeSelect(view: View) {
        onTimeClick.value = true
    }

    fun onImageClick(view: View) {
        onImageClick.value = false
    }

    fun executeCreateRequest(view: View) {
        if (validations.isFieldEmpty(title.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_title)
        } else if (validations.isFieldEmpty(pickUpAddress.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_pickup_location)
        } else if (validations.isFieldEmpty(dropAddress.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_drop_location)
        } else if (validations.isFieldEmpty(serviceDate.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_delivery_date)
        } else if (validations.isFieldEmpty(serviceTime.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_delivery_time)
        } else if (validations.isFieldEmpty(serviceInfo.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_delivery_info)
        } else if (validations.isFieldEmpty(specialNote.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_note)
        } else {
            paymentMethod.set("1") // 1 for cash
            var serviceDateTimeFrom = CommonFunctions().getFormattedTimeOrDate(
                serviceDate.get() + " " + serviceTime.get(),
                Constants.Pattern_YYYY_MM_DD_HH_MM_A,
                Constants.Pattern_YYYY_MM_DD_HH_MM_SS_A
            )

            Coroutines.mainThread {
                progressBar.value = true
            }
            Coroutines.backThread {
                try {
                    if (file == null) {
                        file = File("xyz.txt")
                    }
                    val response = repository.createCourierRequest(
                        Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                        providerID.get()!!,
                        name.get()!!,
                        title.get()!!,
                        pickUpAddress.get()!!,
                        pickUpLatitude.get()!!,
                        pickUpLogitude.get()!!,
                        dropAddress.get()!!,
                        dropLatitude.get()!!,
                        dropLogitude.get()!!,
                        serviceInfo.get()!!,
                        specialNote.get()!!,
                        fair.get().toString(),
                        paymentMethod.get()!!,
                        requestType.get()!!,
                        serviceDateTimeFrom,
                        deliveryType.get()!!,
                        file!!
                    )
                    Coroutines.mainThread {
                        progressBar.value = false
                        successfullyCreateRequest.value = response.toString()
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

    fun getFairPerMile() {
        // currently return fair in km
        var value = Coroutines.backThread {
            var jsonObject: JSONObject? = null
            try {
                val response = repository.getFair(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN)
                )
                jsonObject = JSONObject(response.string())
                Coroutines.mainThread {
                    fairJsonResponse.value = jsonObject
                }
            } catch (e: JSONException) {
                Log.e("expNewReq", "" + e.message)
            }
        }
    }

}