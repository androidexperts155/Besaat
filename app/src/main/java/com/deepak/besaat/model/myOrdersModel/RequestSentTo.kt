package com.deepak.besaat.model.myOrdersModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestSentTo : Serializable {
    @SerializedName("receiver_id")
    @Expose
    private var receiverId: Int? = null
    @SerializedName("provider_status")
    @Expose
    private var providerStatus: Int? = null
    @SerializedName("customer_status")
    @Expose
    private var customerStatus: Int? = null
    @SerializedName("offer")
    @Expose
    private var offer: Int? = null
    @SerializedName("receiver_image")
    @Expose
    private var receiverImage: String? = null
    @SerializedName("receiver_name")
    @Expose
    private var receiverName: String? = null
    @SerializedName("receiver_rating")
    @Expose
    private var receiverRating: String? = null

    fun getReceiverId(): Int? {
        return receiverId
    }

    fun setReceiverId(receiverId: Int?) {
        this.receiverId = receiverId
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

    fun getOffer(): Int? {
        return offer
    }

    fun setOffer(offer: Int?) {
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
}