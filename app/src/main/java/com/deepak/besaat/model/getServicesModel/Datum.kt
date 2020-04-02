package com.deepak.besaat.model.getServicesModel

import com.google.gson.annotations.SerializedName

class Datum {
    @SerializedName("created_at")
    var createdAt: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("id")
    var id: Long? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("status")
    var status: String? = null
    @SerializedName("updated_at")
    var updatedAt: String? = null
    @SerializedName("map_image")
    var map_image: String? = ""
    var isChecked=false


}
