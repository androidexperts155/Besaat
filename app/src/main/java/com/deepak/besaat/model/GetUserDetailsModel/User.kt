package com.deepak.besaat.model.GetUserDetailsModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class User {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("address")
    @Expose
    var address: Any? = null
    @SerializedName("latitude")
    @Expose
    var latitude: Any? = null
    @SerializedName("logitude")
    @Expose
    var logitude: Any? = null
    @SerializedName("distance")
    @Expose
    var distance: Double? = null

    @SerializedName("experience_years")
    @Expose
    var experienceYears: String? = null

    @SerializedName("services")
    @Expose
    var services: String? = null

}