package com.deepak.besaat.model.CreateRequestResponse

import com.deepak.besaat.model.socialLoginModel.User
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class NearByServicesProviderListPojo {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: List<User>? = null
}