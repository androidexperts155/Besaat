package com.deepak.besaat.model.myOrdersModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class RequestOffersPojo {
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("offers")
    @Expose
    private var offers: List<Offer?>? = null

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

    fun getOffers(): List<Offer?>? {
        return offers
    }

    fun setOffers(offers: List<Offer?>?) {
        this.offers = offers
    }
}