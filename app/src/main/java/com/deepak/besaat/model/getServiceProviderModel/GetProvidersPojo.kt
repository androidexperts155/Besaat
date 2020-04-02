package com.deepak.besaat.model.getServiceProviderModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetProvidersPojo {
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("data")
    @Expose
    private var data: List<Datum?>? = null

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getData(): List<Datum?>? {
        return data
    }

    fun setData(data: List<Datum?>?) {
        this.data = data
    }

}