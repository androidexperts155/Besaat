package com.deepak.besaat.model.getUserProfile

import com.google.gson.annotations.SerializedName

class UserProfileResponse {
    @SerializedName("data")
    var data: Data? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null

}
