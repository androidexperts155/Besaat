package com.deepak.besaat.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class GetFairPerMilePojo {
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("fair")
    @Expose
    private var fair: Double? = null
    @SerializedName("service_offer")
    @Expose
    private var serviceOffer: String? = null

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

    fun getFair(): Double? {
        return fair
    }

    fun setFair(fair: Double?) {
        this.fair = fair
    }

    fun getServiceOffer(): String? {
        return serviceOffer
    }

    fun setServiceOffer(serviceOffer: String?) {
        this.serviceOffer = serviceOffer
    }
}