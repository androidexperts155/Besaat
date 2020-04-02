package com.deepak.besaat.viewModel

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.deepak.besaat.R
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.view.activities.addCourierDetail.AddCourierDetailActivity
import com.deepak.besaat.view.activities.addServiceDetail.AddServicesDetailActvity

class DeliveryTypesViewModel : BaseViewModel() {

    var serviceChecked = false
    var courierChecked = false


    fun onCourierCheckedChanged(isCheck: Boolean) {
        courierChecked = isCheck
    }

    fun onServiceCheckedChanged(isCheck: Boolean) {
        serviceChecked = isCheck
    }

    fun continueClick(view: View) {
        if (serviceChecked && courierChecked) {
            view.context.startActivity(Intent(view.context, AddServicesDetailActvity::class.java).putExtra("bothDeliveryTypes",true))
        } else if (serviceChecked) {
            view.context.startActivity(Intent(view.context, AddServicesDetailActvity::class.java).putExtra("bothDeliveryTypes",false))
        } else if (courierChecked) {
            AllLocalHandler.providerSignUpDetailToServer.experienceYears=""
            AllLocalHandler.providerSignUpDetailToServer.availableDays=""
            AllLocalHandler.providerSignUpDetailToServer.serviceIds=""
            AllLocalHandler.providerSignUpDetailToServer.otherService=""
            AllLocalHandler.providerSignUpDetailToServer.serviceTimeFrom=""
            AllLocalHandler.providerSignUpDetailToServer.serviceTimeTo=""
            AllLocalHandler.providerSignUpDetailToServer.providerRole="2"
            view.context.startActivity(Intent(view.context, AddCourierDetailActivity::class.java))
        } else {
            feedBackMessage.value = view.context.getString(R.string.empty_delivery_type)
        }
    }
}