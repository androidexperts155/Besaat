package com.deepak.besaat.view.activities.customerSupport.model

import com.google.gson.annotations.SerializedName

class CustomerSupportModel {
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null
}