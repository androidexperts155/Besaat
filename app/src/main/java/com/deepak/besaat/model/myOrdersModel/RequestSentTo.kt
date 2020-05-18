package com.deepak.besaat.model.myOrdersModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestSentTo : Serializable {
    @SerializedName("id")
    @Expose
    private var id: Int? = null
    @SerializedName("provider_status")
    @Expose
    private var providerStatus: Int? = null
    @SerializedName("customer_status")
    @Expose
    private var customerStatus: Int? = null
    @SerializedName("offer")
    @Expose
    private var offer: String? = null
    @SerializedName("image")
    @Expose
    private var receiverImage: String? = null
    @SerializedName("name")
    @Expose
    private var receiverName: String? = null
    @SerializedName("rating")
    @Expose
    private var receiverRating: String? = null
    @SerializedName("rate_per_hour")
    @Expose
    private var ratePerHour: String? = null
    @SerializedName("completed_jobs")
    @Expose
    private var completedJobs: String? = null

    fun getReceiverId(): Int? {
        return id
    }

    fun setReceiverId(id: Int?) {
        this.id = id
    }

    fun getProviderStatus(): Int? {
        return providerStatus
    }

    fun setProviderStatus(providerStatus: Int?) {
        this.providerStatus = providerStatus
    }

    fun getCustomerStatus(): Int? {
        return customerStatus
    }

    fun setCustomerStatus(customerStatus: Int?) {
        this.customerStatus = customerStatus
    }

    fun getOffer(): String? {
        return offer
    }

    fun setOffer(offer: String?) {
        this.offer = offer
    }

    fun getReceiverImage(): String? {
        return receiverImage
    }

    fun setReceiverImage(receiverImage: String?) {
        this.receiverImage = receiverImage
    }

    fun getReceiverName(): String? {
        return receiverName
    }

    fun setReceiverName(receiverName: String?) {
        this.receiverName = receiverName
    }

    fun getCompletedJobs(): String? {
        return completedJobs
    }

    fun setCompletedJobs(completedJobs: String?) {
        this.completedJobs = completedJobs
    }

    fun getRatePerHours(): String? {
        return ratePerHour
    }

    fun setRatePerHours(ratePerHour: String?) {
        this.ratePerHour = ratePerHour
    }

    fun getReceiverRating(): String? {
        return receiverRating
    }

    fun setReceiverRating(receiverRating: String?) {
        this.receiverRating = receiverRating
    }


    fun getRatingFloat(): Float? {
        return if (receiverRating != null) {
            receiverRating!!.toFloat()
        } else {
            0.0f
        }
    }

    fun getRatingString(): String? {
        return if (receiverRating != null) {
            String.format("$%.2f", receiverRating!!.toFloat())
        } else {
            receiverRating
        }
    }

    fun getOfferInCurrency(): String? {
        if (offer != null && offer != "") {
            if (offer!!.toFloat() <= 0.0f) {
                return "N/A"
            } else {
                return String.format("$%.2f", offer!!.toFloat())
            }
        } else
            return "N/A"
    }
}