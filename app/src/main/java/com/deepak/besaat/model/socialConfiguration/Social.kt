package com.deepak.besaat.model.socialConfiguration

import com.google.gson.annotations.SerializedName

class Social {
    @SerializedName("created_at")
    var createdAt: Any? = null
    @SerializedName("fb_app_id")
    var fbAppId: String? = null
    @SerializedName("id")
    var id: Long? = null
    @SerializedName("insta_client_id")
    var instaClientId: String? = null
    @SerializedName("insta_redirect_url")
    var instaRedirectUrl: String? = null
    @SerializedName("updated_at")
    var updatedAt: String? = null

}
