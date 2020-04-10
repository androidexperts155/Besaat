package com.deepak.besaat.model.getUserProfile

import com.deepak.besaat.model.socialLoginModel.User
import com.google.gson.annotations.SerializedName

class UserProfileResponse {
    @SerializedName("data")
    var data: User? = null
//    var data: Data? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null

}
