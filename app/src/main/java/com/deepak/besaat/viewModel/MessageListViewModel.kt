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

class MessageListViewModel(
    val validations: Validations,
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var getResponse: MutableLiveData<String> = MutableLiveData()
    var filterClick: MutableLiveData<Boolean> = MutableLiveData()

    fun filterOnClick(view: View) {
        filterClick.value = true
    }


    fun getOrderChatList() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getOrderChatList(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN)
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