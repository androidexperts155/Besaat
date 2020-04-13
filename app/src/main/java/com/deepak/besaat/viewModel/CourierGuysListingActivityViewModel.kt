package com.deepak.besaat.viewModel

import android.util.Log
import android.view.View
import android.widget.RadioGroup
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.model.courierGuysModel.GetCourierGuysPojo
import com.deepak.besaat.model.courierGuysModel.GetDeliveryTypesPojo
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*

class CourierGuysListingActivityViewModel(
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var successfullyGetDeliveryTypes: MutableLiveData<GetDeliveryTypesPojo> = MutableLiveData()
    var successfullyGetCourierGuys: MutableLiveData<GetCourierGuysPojo> = MutableLiveData()

    var courierStatus: ObservableField<String> = ObservableField()
    var latitude: ObservableField<String> = ObservableField()
    var longitude: ObservableField<String> = ObservableField()
    var fromCountry: ObservableField<String> = ObservableField()
    var toCountry: ObservableField<String> = ObservableField()
    var search: ObservableField<String> = ObservableField()
    var sort_by: ObservableField<String> = ObservableField()
    var order: ObservableField<String> = ObservableField()
    var radius: ObservableField<String> = ObservableField()
    var rating: ObservableField<String> = ObservableField()
    var deliveryType: ObservableField<String> = ObservableField()
    var deliveryTypeName: ObservableField<String> = ObservableField()

    var courierStatusObserver: MutableLiveData<Boolean> = MutableLiveData()

    var deliveryTypeClick: MutableLiveData<Boolean> = MutableLiveData()
    var listMapIocnClick: MutableLiveData<Boolean> = MutableLiveData()
    var continueClickObserver: MutableLiveData<Boolean> = MutableLiveData()
    var changeLocationClick: MutableLiveData<Boolean> = MutableLiveData()

    fun radioGroupOnCheckedChange(radioGroup: RadioGroup, id: Int) {
        Log.e("radio group", radioGroup.id.toString())
        when (radioGroup.checkedRadioButtonId) {
            R.id.radioButtonLocal -> {
                courierStatus.set("1")
            }
            R.id.radioButtonOverseas -> {
                courierStatus.set("2")
            }
        }
        courierStatusObserver.value = true
    }

    fun deliveryTypeClickFun(view: View) {
        deliveryTypeClick.value = true
    }

    fun listorMapIconClick(view: View) {
        listMapIocnClick.value = true
    }

    fun continueClick(view: View) {
        if (deliveryType.get() != null && deliveryType.get() != "")
            continueClickObserver.value = true
        else {
            continueClickObserver.value = false
            feedBackMessage.value = "Please select delivery type"
        }
    }

    fun changeLocationClickFun(view: View) {
        changeLocationClick.value = true
    }

    fun getDeliveryTypes() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getDeliveryTypes(
                    Constants.BEARER + " " + sharedPreferences.getString(
                        Constants.TOKEN
                    )
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetDeliveryTypes.value = response
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

    fun getCourierGuys(url: String) {
        Coroutines.mainThread {
            feedBackMessage.value = "Loading..."
//            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                var response:GetCourierGuysPojo
                if (courierStatus.get()=="1")
                {
                    response =repository.getCourierGuys(
                        url,
                        Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                        sort_by.get()!!, order.get()!!,
                        courierStatus.get()!!, latitude.get()!!,
                        longitude.get()!!, fromCountry.get()!!,
                        toCountry.get()!!, search.get()!!, radius.get()!!,rating.get()!!
                    )
                }
                else{
                    response =repository.getCourierGuysOverSeas(
                        url,
                        Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                        sort_by.get()!!, order.get()!!,
                        courierStatus.get()!!, latitude.get()!!,
                        longitude.get()!!, fromCountry.get()!!,
                        toCountry.get()!!, search.get()!!,rating.get()!!
                    )
                }

//                val response =repository.getCourierGuys(
//                    url,
//                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
//                    courierStatus.get()!!, latitude.get()!!,
//                    longitude.get()!!, fromCountry.get()!!,
//                    toCountry.get()!!, search.get()!!, radius.get()!!
//                )


                Coroutines.mainThread {
                    //                    progressBar.value = false
                    successfullyGetCourierGuys.value = response
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
            } catch (e: Exception) {
                Coroutines.mainThread {
                    //                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            }
        }
    }

}