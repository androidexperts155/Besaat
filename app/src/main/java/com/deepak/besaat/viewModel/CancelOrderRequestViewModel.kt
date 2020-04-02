package com.deepak.besaat.viewModel

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import com.google.gson.JsonElement
import java.lang.Exception


class CancelOrderRequestViewModel(
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var successfullyGetCancellationReasons: MutableLiveData<JsonElement> = MutableLiveData()
    var getResponse: MutableLiveData<String> = MutableLiveData()

    var remarks: ObservableField<String> = ObservableField()
    var reason: ObservableField<String> = ObservableField()
    var reasonID: ObservableField<String> = ObservableField()
    var requestID: ObservableField<String> = ObservableField()
    var yesChecked: ObservableField<Boolean> = ObservableField()
    var changeLocationClick: MutableLiveData<Boolean> = MutableLiveData()
    var selectReasonClickObserver: MutableLiveData<Boolean> = MutableLiveData()

    fun selectReasonClick(view: View) {
        selectReasonClickObserver.value = true
    }

    fun continueClick(view: View) {
        // hit api for cancellation
        if (reasonID.get() == null)
            feedBackMessage.value = "Please select at least one reason to cancel."
        else if (remarks.get() == null || remarks.get().toString().trim() == "")
            feedBackMessage.value = "Please enter your remarks."
        else if (!yesChecked.get()!!)
            feedBackMessage.value = "Please check the terms checkbox."
        else
            cancelOrder()
    }


    fun getCancellationReasons() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getCancellationReasons(
                    Constants.BEARER + " " + sharedPreferences.getString(
                        Constants.TOKEN
                    )
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetCancellationReasons.value = response
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

    fun cancelOrder() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.cancelOrder(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    requestID.get(), reasonID.get(), remarks.get()
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

    fun onCheckedChange(button: CompoundButton?, check: Boolean) {
        yesChecked.set(check)
    }
}