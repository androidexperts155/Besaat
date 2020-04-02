package com.deepak.besaat.model.socialConfiguration

import com.google.gson.annotations.SerializedName

class SocialConfiguration {

    @SerializedName("message")
    var message: String? = null
    @SerializedName("social")
    var social: Social? = null
    @SerializedName("status")
    var status: Boolean? = null

}
