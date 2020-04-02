package com.deepak.besaat.model.myOrdersModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class RequestOfferAcceptPojo {
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("request")
    @Expose
    private var request: Request? = null

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

    fun getRequest(): Request? {
        return request
    }

    fun setRequest(request: Request?) {
        this.request = request
    }
}