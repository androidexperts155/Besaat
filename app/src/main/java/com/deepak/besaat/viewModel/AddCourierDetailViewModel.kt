package com.deepak.besaat.viewModel

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.R
import com.deepak.besaat.model.providerSignupModel.ProviderSignUpResponse
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.network.AllLocalHandler.Companion.providerSignUpDetailToServer
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.addCourierDetail.AddCourierDetailActivity
import com.deepak.besaat.view.activities.paymentInformation.PaymentInformationActivity
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddCourierDetailViewModel(validations: Validations, repository: Repository) :
    BaseViewModel() {
    val validations = validations
    val repository = repository
    var departureDateClick: MutableLiveData<Boolean> = MutableLiveData()
    var departureTimeClick: MutableLiveData<Boolean> = MutableLiveData()
    var arrivalDateClick: MutableLiveData<Boolean> = MutableLiveData()
    var arrivalTimeClick: MutableLiveData<Boolean> = MutableLiveData()
    var onImageClick: MutableLiveData<Boolean> = MutableLiveData()

    var departureDate: ObservableField<String> = ObservableField()
    var departureTime: ObservableField<String> = ObservableField()

    var arrivalDate: ObservableField<String> = ObservableField()
    var arrivalTime: ObservableField<String> = ObservableField()

    var licenseNumber: ObservableField<String> = ObservableField()
    var localChecked: ObservableField<Boolean> = ObservableField()
    var overseasChecked: ObservableField<Boolean> = ObservableField()
    var bothChecked: ObservableField<Boolean> = ObservableField()
    var licenseDetailViewVisibility: ObservableField<Boolean> = ObservableField()
    var coutryViewVisibility: ObservableField<Boolean> = ObservableField()

    var onSuccessfullySignUp: MutableLiveData<ProviderSignUpResponse> = MutableLiveData()
    var departureCountryName = ""
    var arrivalCountryName = ""
    var courierStatus = 0    //1-> locale, 2-> overseas , 3->both

    fun departureDateClick(view: View) {
        departureDateClick.value = true
    }


    var file: File? = null
    fun departureTimeClick(view: View) {
        departureTimeClick.value = true
    }


    fun arrivalDateClick(view: View) {
        if (validations.isFieldEmpty(departureDate.get()) && validations.isFieldEmpty(departureTime.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_departure_date_time)
        } else {
            arrivalDateClick.value = true
        }
    }

    fun arrivalTimeClick(view: View) {
        if (validations.isFieldEmpty(departureDate.get()) && validations.isFieldEmpty(departureTime.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_departure_date_time)
        } else if (validations.isFieldEmpty(arrivalDate.get())) {
            feedBackMessage.value = view.context.getString(R.string.empty_arrival_date)
        } else {
            arrivalTimeClick.value = true
        }
    }

    fun onLocalClick(view: View) {
        coutryViewVisibility.set(false)
//        if (localChecked.get()!!) {
//            licenseDetailViewVisibility.set(false)
//            localChecked.set(false)
//        } else {
        licenseDetailViewVisibility.set(true)
        localChecked.set(true)
        bothChecked.set(false)
        overseasChecked.set(false)
        courierStatus = 1
//        }
    }

    fun onOverSeasClick(view: View) {
        licenseDetailViewVisibility.set(false)
//        if (overseasChecked.get()!!) {
//            overseasChecked.set(false)
//            coutryViewVisibility.set(false)
//        } else {
        coutryViewVisibility.set(true)
        overseasChecked.set(true)
        bothChecked.set(false)
        localChecked.set(false)
        courierStatus = 2
//        }
    }


    fun onBothClick(view: View) {
        if (bothChecked.get()!!) {
            coutryViewVisibility.set(false)
            licenseDetailViewVisibility.set(false)
            localChecked.set(false)
            overseasChecked.set(false)
            bothChecked.set(false)
        } else {
            localChecked.set(false)
            overseasChecked.set(false)
            coutryViewVisibility.set(true)
            licenseDetailViewVisibility.set(true)
            localChecked.set(true)
            overseasChecked.set(true)
            bothChecked.set(true)
            courierStatus = 3
        }
    }

    fun onImageClick(view: View) {
        onImageClick.value = false
    }


    fun continueClick(view: View) {
        providerSignUpDetailToServer.name = AllLocalHandler.userDetail.name
        providerSignUpDetailToServer.email = AllLocalHandler.userDetail.email
        providerSignUpDetailToServer.phone =
            "+" + AllLocalHandler.userDetail.phoneCode + "-" + AllLocalHandler.userDetail.phone
        providerSignUpDetailToServer.facebookId = AllLocalHandler.userDetail.fbId
        providerSignUpDetailToServer.googleId = AllLocalHandler.userDetail.googleId
        providerSignUpDetailToServer.instagramId = AllLocalHandler.userDetail.instaId


        providerSignUpDetailToServer.departureCountry = departureCountryName
        if (validations.isFieldEmpty(departureDate.get()) && validations.isFieldEmpty(departureTime.get())) {
            providerSignUpDetailToServer.departureDate = ""

        } else {
            providerSignUpDetailToServer.departureDate =
                CommonFunctions().getFormattedTimeOrDate(
                    departureDate.get() + " " + departureTime.get(),
                    Constants.Pattern_YYYY_MM_DD_HH_MM_A,
                    Constants.Pattern_YYYY_MM_DD_HH_MM_SS_A
                )
//                departureDate.get() + " " + departureTime.get()
        }

        providerSignUpDetailToServer.arrivalCountry = arrivalCountryName
        if (validations.isFieldEmpty(arrivalDate.get()) && validations.isFieldEmpty(arrivalTime.get())) {
            providerSignUpDetailToServer.arrivalDate = ""
        } else {
            providerSignUpDetailToServer.arrivalDate =
                CommonFunctions().getFormattedTimeOrDateSend(
                    arrivalDate.get() + " " + arrivalTime.get(),
                    Constants.Pattern_YYYY_MM_DD_HH_MM_A,
                    Constants.Pattern_YYYY_MM_DD_HH_MM_SS_A
                )
//                arrivalDate.get() + " " + arrivalTime.get()
        }


        if (!localChecked.get()!! && !bothChecked.get()!! && !overseasChecked.get()!!) {
            feedBackMessage.value = view.context.getString(R.string.empty_courier_delivery_typr)
        } else if (localChecked.get()!! || bothChecked.get()!!) {
            if (validations.isFieldEmpty(licenseNumber.get())) {
                feedBackMessage.value = view.context.getString(R.string.empty_licence_number)
            } else if (!validations.isMinLength6(licenseNumber.get().toString())) {
                feedBackMessage.value = view.context.getString(R.string.min_length_licence)
            } else if (file == null) {
                feedBackMessage.value = view.context.getString(R.string.empty_licence_image)
            } else {
                if (!setDuration()) {
                    intentTOoNextSecreen(view)
                } else {
                    feedBackMessage.value =
                        view.context.getString(R.string.arrival_time_must_greater_than_departure)
                }
            }
        } else {
            if (!setDuration()) {
                intentTOoNextSecreen(view)
            } else {
                feedBackMessage.value =
                    view.context.getString(R.string.arrival_time_must_greater_than_departure)
            }
        }
    }

    fun setDuration(): Boolean {
        var b = false
        var start = providerSignUpDetailToServer.departureDate
        var end = providerSignUpDetailToServer.arrivalDate
        var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd h:mm a");
        Log.e("Time", "start and end time " + start + " " + end)
        try {
            var dateStart = dateFormat.parse(start);
            var dateEnd = dateFormat.parse(end);
            if (dateStart.after(dateEnd)) {
                b = true;//If start date is before end date
                Log.e("durationhours", "date start before  " + b)
            } else if (dateStart.equals(dateEnd)) {
                b = true;//If two dates are equal
                Log.e("durationhours", "date start equal " + b)
            } else {

                b = false
                Log.e("durationhours", "date start false  " + b)

            }

            var difference: String = printDifference(dateStart, dateEnd)
            if (difference.contains("-")) {
                difference = "0"
            }


            //actualDuration = difference.split("Hours")[0]
            //  Log.e("durationhours", "Actual Duration hours of time " + actualDuration+" total hours   is  ")
        } catch (e: ParseException) {

        }




        Log.e("durationhours", "duration validation  " + b)
        return b


    }

    private fun printDifference(dateStart: Date, dateEnd: Date): String {
        var mTotalHourSecond = "0"
        var different = dateEnd.getTime() - dateStart.getTime()
        //val differenceprevious = (dateStart.getTime() - previousStartDatelist.get(0).getTime()) as Int

        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli

        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli
        Log.e("elaspeddate", "elapsed dates are " + elapsedDays)
        var totalDays = elapsedDays.toInt()
        Log.e("elaspeddate", "elapsed dates are " + elapsedDays)
        val mTotalHour = (elapsedDays * 24) + elapsedHours
        var totalHours = mTotalHour.toInt()
        //   totalHour=mTotalHour.toInt()
        val mTotalHourString = (mTotalHour).toString()
        val mTotalSecondString = (elapsedMinutes).toString()
        if (mTotalSecondString.equals("0", ignoreCase = true)) {
            mTotalHourSecond = mTotalHourString + " Hours"
        } else {
            mTotalHourSecond = mTotalHourString + " Hours " + mTotalSecondString + " Minutes"
        }
        return mTotalHourSecond


    }


    private fun intentTOoNextSecreen(view: View) {
        providerSignUpDetailToServer.courierStatus = courierStatus.toString()
        if (licenseNumber.get() == null) {
            providerSignUpDetailToServer.licenseNum = ""
        } else {
            providerSignUpDetailToServer.licenseNum = licenseNumber.get()
        }
        if (AllLocalHandler.userDetail.image == null) {
            providerSignUpDetailToServer.image_url = AllLocalHandler.userDetail.imageUrl!!
        } else {
            providerSignUpDetailToServer.image = AllLocalHandler.userDetail.image
        }
        view.context.startActivity(Intent(view.context, PaymentInformationActivity::class.java))
    }
}