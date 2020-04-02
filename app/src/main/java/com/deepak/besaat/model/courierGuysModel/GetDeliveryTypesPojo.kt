package com.deepak.besaat.model.courierGuysModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetDeliveryTypesPojo {
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("map_icon")
    @Expose
    private var map_icon: String? = null

    @SerializedName("data")
    @Expose
    private var data: List<Datum?>? = null

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }


    fun getMapIcon(): String? {
        return map_icon
    }

    fun setMapIcon(mapIcon: String?) {
        this.map_icon = mapIcon
    }

    fun getData(): List<Datum?>? {
        return data
    }

    fun setData(data: List<Datum?>?) {
        this.data = data
    }

    class Datum {
        @SerializedName("id")
        @Expose
        var id: Int? = null
        @SerializedName("name")
        @Expose
        var name: String? = null

        var check: Boolean? = false
    }
}