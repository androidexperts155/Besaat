package com.deepak.besaat.model.providerSignupModel

import com.google.gson.annotations.SerializedName

class ProviderSignUpResponse {
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null


    /*@SerializedName("user_name")
    var user_name:String?=null

    @SerializedName("user_image")
    var user_image:String?=null

    @SerializedName("access_token")
    var access_token:String?=null*/
}
