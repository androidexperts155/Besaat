package com.deepak.besaat.model.getServicesModel

import com.google.gson.annotations.SerializedName

class GetServicesResponse {

    @SerializedName("data")
    var data: MutableList<Datum>? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null

}
