package com.deepak.besaat.model.buyerSignUpModel

import com.google.gson.annotations.SerializedName

class BuyerSignUpResponse {

    @SerializedName("access_token")
    var accessToken: String? = null
    @SerializedName("data")
    var data: Data? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null
    @SerializedName("token_type")
    var tokenType: String? = null

}
