package com.deepak.besaat.model.courierGuysModel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class GetCourierGuysPojo {
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("couriers")
    @Expose
    private var couriers: List<Courier?>? = null

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

    fun getCouriers(): List<Courier?>? {
        return couriers
    }

    fun setCouriers(couriers: List<Courier?>?) {
        this.couriers = couriers
    }
}