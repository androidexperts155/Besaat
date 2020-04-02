package com.deepak.besaat.viewModel

import android.content.Intent
import android.view.View
import android.widget.CheckBox
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.model.getServicesModel.Datum
import com.deepak.besaat.model.getServicesModel.GetServicesResponse
import com.deepak.besaat.model.providerSignupModel.ProviderSignUpResponse
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.network.AllLocalHandler.Companion.providerSignUpDetailToServer
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.ApiException
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.utils.NoInternetException
import com.deepak.besaat.utils.Validations
import com.deepak.besaat.view.activities.addCourierDetail.AddCourierDetailActivity
import com.deepak.besaat.view.activities.paymentInformation.PaymentInformationActivity

class AddServiceDetailViewModel(repository: Repository, validations: Validations) :
    BaseViewModel() {
    var repository = repository
    var validations = validations
    var successfullyGetServices: MutableLiveData<GetServicesResponse> = MutableLiveData()
    var experienceClick: MutableLiveData<Pair<View, Boolean>> = MutableLiveData()
    var fromTimeClick: MutableLiveData<Boolean> = MutableLiveData()
    var toTimeClick: MutableLiveData<Boolean> = MutableLiveData()
    var providerExperience: ObservableField<String> = ObservableField()
    var from: ObservableField<String> = ObservableField()
    var to: ObservableField<String> = ObservableField()
    var otherService: ObservableField<String> = ObservableField()
    var ratePerHour: ObservableField<String> = ObservableField()
    var availableDaysList: MutableList<String> = ArrayList();
    var selectedServicesList: MutableList<Datum> = ArrayList()
    var isOtherVisible = false
    var bothDeliveryTypes = false

    fun getServices() {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread {
            try {
                val response = repository.getServices()
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetServices.value = response
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

    fun experienceClick(view: View) {
        experienceClick.value = Pair(view, true)
    }

    fun fromTimeClick(view: View) {
        fromTimeClick.value = true
    }

    fun toTimeClick(view: View) {
        toTimeClick.value = true
    }

    fun onMonCheckedChange(view: View, isCheck: Boolean) {
        if (isCheck) {
            availableDaysList.add((view as CheckBox).text.toString())
        } else {
            availableDaysList.remove((view as CheckBox).text.toString())
        }
    }

    fun onTueCheckedChange(view: View, isCheck: Boolean) {
        if (isCheck) {
            availableDaysList.add((view as CheckBox).text.toString())
        } else {
            availableDaysList.remove((view as CheckBox).text.toString())
        }
    }

    fun onWedCheckedChange(view: View, isCheck: Boolean) {
        if (isCheck) {
            availableDaysList.add((view as CheckBox).text.toString())
        } else {
            availableDaysList.remove((view as CheckBox).text.toString())
        }
    }

    fun onThuCheckedChange(view: View, isCheck: Boolean) {
        if (isCheck) {
            availableDaysList.add((view as CheckBox).text.toString())
        } else {
            availableDaysList.remove((view as CheckBox).text.toString())
        }
    }

    fun onFriCheckedChange(view: View, isCheck: Boolean) {
        if (isCheck) {
            availableDaysList.add((view as CheckBox).text.toString())
        } else {
            availableDaysList.remove((view as CheckBox).text.toString())
        }
    }

    fun onSatCheckedChange(view: View, isCheck: Boolean) {
        if (isCheck) {
            availableDaysList.add((view as CheckBox).text.toString())
        } else {
            availableDaysList.remove((view as CheckBox).text.toString())
        }
    }

    fun onSunCheckedChange(view: View, isCheck: Boolean) {
        if (isCheck) {
            availableDaysList.add((view as CheckBox).text.toString())
        } else {
            availableDaysList.remove((view as CheckBox).text.toString())
        }
    }

    fun continueClick(view: View) {
        if (selectedServicesList.size == 0) {
            feedBackMessage.value = view.context.getString(R.string.empty_service_type)
        } else if (isOtherVisible && validations.isFieldEmpty(otherService.get()?.trim())) {
            feedBackMessage.value = view.context.getString(R.string.empty_other_service)
        } else if (isOtherVisible && isDuplicateOtherService()) {
            feedBackMessage.value = String.format(
                view.context.getString(R.string.duplicate_other_service),
                otherService.get()?.trim()
            )
        } else if (validations.isFieldEmpty(providerExperience.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_provider_experience)
        } else if (validations.isFieldEmpty(ratePerHour.get())) {
            feedBackMessage.value = view.context.getString(R.string.please_enter_rate_per_hour)
        } else if (availableDaysList.size == 0) {
            feedBackMessage.value = view.context.getString(R.string.empty_available_days)
        } else if (validations.isFieldEmpty(from.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_timings)
        } else if (validations.isFieldEmpty(to.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_timings)
        } else {
//            if (isOtherVisible && validations.isFieldEmpty(otherService.get()?.trim())) {
//                feedBackMessage.value = view.context.getString(R.string.empty_other_service)
//            } else if (isOtherVisible && isDuplicateOtherService()) {
//                feedBackMessage.value = String.format(
//                    view.context.getString(R.string.duplicate_other_service),
//                    otherService.get()?.trim()
//                )
//            } else {
            var commaSepratedAvailableDays = ""
            var commaSepratedService = ""

            for (i in availableDaysList.indices) {
                if (i == 0) {
                    commaSepratedAvailableDays = availableDaysList.get(i)
                } else {
                    commaSepratedAvailableDays =
                        commaSepratedAvailableDays + "," + availableDaysList.get(i)
                }

            }
            for (i in selectedServicesList.indices) {
                if (selectedServicesList[i].id != null) {
                    if (commaSepratedService == "") {
                        commaSepratedService = selectedServicesList[i].id!!.toString()
                    } else {
                        commaSepratedService =
                            commaSepratedService + "," + selectedServicesList[i].id.toString()
                    }
                }
            }
            providerSignUpDetailToServer.name = AllLocalHandler.userDetail.name
            providerSignUpDetailToServer.email = AllLocalHandler.userDetail.email
            providerSignUpDetailToServer.phone =
                "+" + AllLocalHandler.userDetail.phoneCode + "-" + AllLocalHandler.userDetail.phone
            providerSignUpDetailToServer.facebookId = AllLocalHandler.userDetail.fbId
            providerSignUpDetailToServer.googleId = AllLocalHandler.userDetail.googleId
            providerSignUpDetailToServer.instagramId = AllLocalHandler.userDetail.instaId
            providerSignUpDetailToServer.experienceYears = providerExperience.get()
            providerSignUpDetailToServer.availableDays = commaSepratedAvailableDays
            providerSignUpDetailToServer.serviceIds = commaSepratedService
            if (otherService.get() == null) {
                providerSignUpDetailToServer.otherService = ""
            } else {
                providerSignUpDetailToServer.otherService = otherService.get()
            }

            if (ratePerHour.get() == null) {
                providerSignUpDetailToServer.ratePerHour = ""
            } else {
                providerSignUpDetailToServer.ratePerHour = ratePerHour.get()
            }

            providerSignUpDetailToServer.serviceTimeFrom = from.get()
            providerSignUpDetailToServer.serviceTimeTo = to.get()



            providerSignUpDetailToServer.courierStatus = ""
            providerSignUpDetailToServer.licenseNum = ""
            providerSignUpDetailToServer.departureCountry = ""
            providerSignUpDetailToServer.departureDate = ""
            providerSignUpDetailToServer.arrivalCountry = ""
            providerSignUpDetailToServer.arrivalDate = ""
            if (AllLocalHandler.userDetail.image == null) {
                providerSignUpDetailToServer.image_url = AllLocalHandler.userDetail.imageUrl!!
            } else {
                providerSignUpDetailToServer.image = AllLocalHandler.userDetail.image
            }
            if (bothDeliveryTypes) {
                providerSignUpDetailToServer.providerRole = "3"
                view.context.startActivity(
                    Intent(
                        view.context,
                        AddCourierDetailActivity::class.java
                    )
                )
            } else {
                providerSignUpDetailToServer.providerRole = "1"
                view.context.startActivity(
                    Intent(
                        view.context,
                        PaymentInformationActivity::class.java
                    )
                )
                //providerSignUp()
            }
//            }
        }
    }

    private fun isDuplicateOtherService(): Boolean {
        var isContain = false
        for (i in 0 until successfullyGetServices.value?.data!!.size) {
            if (successfullyGetServices.value?.data!![i].name.equals(
                    otherService.get()?.trim(),
                    true
                )
            ) {
                isContain = true
                break
            }
        }
        return isContain
    }
}