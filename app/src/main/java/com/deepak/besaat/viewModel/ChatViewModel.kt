package com.deepak.besaat.viewModel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import java.io.File
import java.lang.Exception

class ChatViewModel(
    val validations: Validations,
    val repository: Repository,
    val sharedPreferences: SharedPref
) : BaseViewModel() {
    var successfullyUploadImage: MutableLiveData<String> = MutableLiveData()
    var successfullyGetHistory: MutableLiveData<String> = MutableLiveData()
    var attachmentClick: MutableLiveData<Boolean> = MutableLiveData()
    var onSendClick: MutableLiveData<Boolean> = MutableLiveData()
    var onImageClick: MutableLiveData<Boolean> = MutableLiveData()

    var onLocationClick: MutableLiveData<Boolean> = MutableLiveData()
    var onDocClick: MutableLiveData<Boolean> = MutableLiveData()
    var onMicClick: MutableLiveData<Boolean> = MutableLiveData()

    var pickUpLatitude: ObservableField<String> = ObservableField()
    var pickUpLogitude: ObservableField<String> = ObservableField()
    var pickUpAddress: ObservableField<String> = ObservableField()
    var mediaType: ObservableField<String> = ObservableField()
    var uploadedFileName: ObservableField<String> = ObservableField()
    var mediaRuntime: ObservableField<String> = ObservableField()
    var message: ObservableField<String> = ObservableField()

    var title: ObservableField<String> = ObservableField()
    var name: ObservableField<String> = ObservableField()
    var receiverId: ObservableField<String> = ObservableField()
    var receiverImage: ObservableField<String> = ObservableField()
    var file: File? = null

    fun onAttachmentClickFun(view: View) {
        attachmentClick.value = true
    }

    fun onLocationClickFun(view: View) {
        onLocationClick.value = true
    }

    fun onMicClickFun(view: View) {
        onMicClick.value = true
    }

    fun onDocClickFun(view: View) {
        onDocClick.value = true
    }

    fun onImageClick(view: View) {
        onImageClick.value = true
    }

    fun onSendClickfun(view: View) {
        onSendClick.value = true
    }

    fun uploadFile() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                if (file == null) {
                    feedBackMessage.value = "Please select file"
                }
                val response = repository.uploadFile(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    file!!
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyUploadImage.value = response.toString()
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

    fun getChatHistory() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getChatHistory(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN),
                    receiverId.get()
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetHistory.value = response.toString()
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