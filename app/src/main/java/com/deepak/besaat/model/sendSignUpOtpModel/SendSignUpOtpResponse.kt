package com.deepak.besaat.model.sendSignUpOtpModel

import com.google.gson.annotations.SerializedName

class SendSignUpOtpResponse {

    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("otp")
    var otp:Int?=null
}
