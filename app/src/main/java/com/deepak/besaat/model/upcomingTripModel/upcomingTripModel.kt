package com.deepak.besaat.model.upcomingTripModel

import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

class upcomingTripModel : Serializable {

    @SerializedName("departure_country")
    var departureCountry: String? = null
    @SerializedName("arrival_country")
    var arrivalCountry: String? = ""
    @SerializedName("created_at")
    var createdDate: String? = null

    @SerializedName("updated_at")
    var updatedDate: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("image")
    var imageUrl: String? = null
    var departureCountryFlag: Int = 0
    var departurecountryname: String = ""
    var arrivalCountryFlag = 0
    var arrivalContryname = ""
    var isPast: Boolean = false
}