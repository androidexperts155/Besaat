package com.deepak.besaat.model.userDetailModel

import com.google.gson.annotations.SerializedName
import java.io.File

class UserDetail {
    @SerializedName("email")
    var email: String? = null
    @SerializedName("imageUrl")
    var imageUrl=""
    @SerializedName("name")
    var name: String? = null

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("phoneCode")
    var phoneCode: String? = null
    @SerializedName("instaId")
    var instaId=""

    @SerializedName("googleId")
    var googleId=""

    @SerializedName("fbId")
    var fbId=""

    var image:File?=null

    var token=""

    var role=""
    var isDefaultProfileImage=false
}
