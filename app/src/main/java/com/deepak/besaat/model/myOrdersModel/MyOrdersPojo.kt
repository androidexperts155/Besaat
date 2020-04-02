package com.deepak.besaat.model.myOrdersModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyOrdersPojo {
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("requests")
    @Expose
    private var requests: List<Request?>? = null

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

    fun getRequests(): List<Request?>? {
        return requests
    }

    fun setRequests(requests: List<Request?>?) {
        this.requests = requests
    }
}