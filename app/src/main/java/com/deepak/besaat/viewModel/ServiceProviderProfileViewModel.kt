package com.deepak.besaat.viewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.model.getUserProfile.UserProfileResponse
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.ApiException
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.utils.NoInternetException
import okhttp3.ResponseBody

class ServiceProviderProfileViewModel(repository: Repository) : BaseViewModel() {
    var repository = repository
    var name: ObservableField<String> = ObservableField()
    var email: ObservableField<String> = ObservableField()
    var phone: ObservableField<String> = ObservableField()
    var successfullyGetUserProfile: MutableLiveData<UserProfileResponse> = MutableLiveData()

    fun getUserProfile(authorization: String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.userProfile(authorization)
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetUserProfile.value = response
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