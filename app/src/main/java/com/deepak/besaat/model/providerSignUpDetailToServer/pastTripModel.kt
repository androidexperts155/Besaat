package com.deepak.besaat.model.providerSignUpDetailToServer

import com.google.gson.annotations.SerializedName
import java.io.File

class pastTripModel{

    @SerializedName("departure_country")
    var departureCountry: String? = null
    @SerializedName("arrival_country")
    var arrivalCountry :String?=""
    @SerializedName("created_at")
    var createdDate: String? = null

    @SerializedName("updated_at")
    var updatedDate: String? = null



    @SerializedName("image")
    var imageUrl: String? = null







}