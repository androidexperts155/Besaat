package com.deepak.besaat.model.myOrdersModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Offer {
    @SerializedName("id")
    @Expose
    private var id: Int? = null
    @SerializedName("receiver_id")
    @Expose
    private var receiverId: Int? = null
    @SerializedName("type_id")
    @Expose
    private var typeId: Int? = null
    @SerializedName("offer")
    @Expose
    private var offer: Int? = null
    @SerializedName("provider")
    @Expose
    private var provider: Provider? = null

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getReceiverId(): Int? {
        return receiverId
    }

    fun setReceiverId(receiverId: Int?) {
        this.receiverId = receiverId
    }

    fun getTypeId(): Int? {
        return typeId
    }

    fun setTypeId(typeId: Int?) {
        this.typeId = typeId
    }

    fun getOffer(): Int? {
        return offer
    }

    fun setOffer(offer: Int?) {
        this.offer = offer
    }

    fun getOfferString(): String {
        return if (offer != null)
            String.format("$%d", offer)
        else
            "N/A"
    }

    fun getProvider(): Provider? {
        return provider
    }

    fun setProvider(provider: Provider?) {
        this.provider = provider
    }
}