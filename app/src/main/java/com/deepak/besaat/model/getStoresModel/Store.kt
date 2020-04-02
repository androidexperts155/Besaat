package com.deepak.besaat.model.getStoresModel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Store :Serializable{

    @SerializedName("created_at")
    var createdAt: Any? = null
    @SerializedName("description")
    var description: Any? = null
    @SerializedName("id")
    var id: Long? = null
    @SerializedName("image")
    var image: String? = null
    @SerializedName("map_image")
    var map_image: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("status")
    var code: String? = null
    @SerializedName("code")
    var status: String? = null
    @SerializedName("updated_at")
    var updatedAt: String? = null

    var checked :Boolean=false

}
