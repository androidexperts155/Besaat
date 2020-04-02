package com.deepak.besaat.viewModel

import android.app.Activity
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.model.getUserProfile.UserProfileResponse
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.ApiException
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.utils.NoInternetException
import org.json.JSONObject


class CustomerProfileViewModell(activity: Activity, respository: Repository):BaseViewModel() {
    var repository = respository
    var activity = activity

    var name: ObservableField<String> = ObservableField()
    var email: ObservableField<String> = ObservableField()
    var phone: ObservableField<String> = ObservableField()
    var jsonResponse:MutableLiveData<JSONObject> = MutableLiveData()
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
    fun getReviews(placeid:String){
        Coroutines.backThread {
            var url:String
            try {
                url = Constants.GET_PLACE_DETAILS +placeid +"&fields=review"+"&key="+"AIzaSyAtCrvV9feuiIbDmO4XRhHqLocy_6rWZaI"
                val response = repository.getPlaceReview(url)
                var jsonObject = JSONObject(response.string())
                Coroutines.mainThread {
                    progressBar.value = false
                    jsonResponse.value=jsonObject
                    Log.e("StoreProfile","store profile view model "+response.toString())
                   // successfullyGetUserProfile.value = response
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
