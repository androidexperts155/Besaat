package com.deepak.besaat.model.buyerSignUpModel

import com.google.gson.annotations.SerializedName

class Data {

    @SerializedName("created_at")
    var createdAt: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("id")
    var id: Long? = null
    @SerializedName("image")
    var image: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("phone")
    var phone: String? = null
    @SerializedName("role")
    var role: String? = null
    @SerializedName("updated_at")
    var updatedAt: String? = null

}
