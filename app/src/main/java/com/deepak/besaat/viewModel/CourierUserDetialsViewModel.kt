package com.deepak.besaat.viewModel

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import org.json.JSONObject
import java.lang.Exception

class CourierUserDetailsViewModel(
    val validations: Validations,
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var successfullyGetUserDetails: MutableLiveData<JSONObject> = MutableLiveData()

    var onRequestClick: MutableLiveData<Boolean> = MutableLiveData()
    var onChatClick: MutableLiveData<Boolean> = MutableLiveData()

    var latitude: ObservableField<String> = ObservableField()
    var longitude: ObservableField<String> = ObservableField()

    var providerLatitude: ObservableField<String> = ObservableField()
    var providerLongitude: ObservableField<String> = ObservableField()
    var providerAddress: ObservableField<String> = ObservableField()

    var deliveryType: ObservableField<String> = ObservableField()
    var from: ObservableField<String> = ObservableField()
    var name: ObservableField<String> = ObservableField()
    var distance: ObservableField<String> = ObservableField()
    var services: ObservableField<String> = ObservableField()
    var courierStatus: ObservableField<String> = ObservableField()
    var experienceYears: ObservableField<String> = ObservableField()
    var image: ObservableField<String> = ObservableField()
    var id: ObservableField<String> = ObservableField()

    fun onRequestClickFun(view: View) {
        onRequestClick.value = true
    }

    fun onChatClickFun(view: View) {
        onChatClick.value = true
    }

    fun getUserDetails() {
        var value = Coroutines.backThread {
            var jsonObject: JSONObject? = null
            try {
                val response = repository.courierUserDetails(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    id.get(),latitude.get(),longitude.get()
                )
                jsonObject = JSONObject(response.string())
                Coroutines.mainThread {
                    successfullyGetUserDetails.value = jsonObject
                }
            } catch (e: Exception) {
                feedBackMessage.postValue(e.message!!)
                Log.e("exp_uer_details", "" + e.message)
            }
        }
    }
}