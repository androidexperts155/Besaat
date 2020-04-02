package com.deepak.besaat.model.socialLoginModel

import com.google.gson.annotations.SerializedName

class SocialLoginResponse {

    @SerializedName("access_token")
    var accessToken: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null
    @SerializedName("token_type")
    var tokenType: String? = null
    @SerializedName("user")
    var user: User? = null
}
