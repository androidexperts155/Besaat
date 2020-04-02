package com.deepak.besaat.model.sendOtpModel

import com.deepak.besaat.model.socialLoginModel.User
import com.google.gson.annotations.SerializedName

class SendOtpResponse {
    @SerializedName("message")
    var message: String? = null
    @SerializedName("otp")
    var otp: Long? = null
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("exist")
    var exist=""


    @SerializedName("user")
    var user: User? = null

    @SerializedName("access_token")
    var access_token:String?=null

}
