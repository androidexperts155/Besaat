package com.deepak.besaat.model.GetNewServiceRequest

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NewServiceRequestPojo : Serializable {
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("social")
    @Expose
    private var social: Social? = null

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

    fun getSocial(): Social? {
        return social
    }

    fun setSocial(social: Social?) {
        this.social = social
    }

    class Social : Serializable {
        @SerializedName("user_id")
        @Expose
        var userId: Int? = null
        @SerializedName("title")
        @Expose
        var title: String? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("request_type")
        @Expose
        var requestType: String? = null
        @SerializedName("service_time_from")
        @Expose
        var serviceTimeFrom: String? = null
        @SerializedName("service_time_to")
        @Expose
        var serviceTimeTo: Any? = null
        @SerializedName("image")
        @Expose
        var image: String? = null
        @SerializedName("drop_address")
        @Expose
        var dropAddress: String? = null
        @SerializedName("drop_latitude")
        @Expose
        var dropLatitude: String? = null
        @SerializedName("drop_longitude")
        @Expose
        var dropLongitude: String? = null
        @SerializedName("order_info")
        @Expose
        var orderInfo: String? = null
        @SerializedName("special_note")
        @Expose
        var specialNote: String? = null
        @SerializedName("app_commission")
        @Expose
        var appCommission: Double? = null
        @SerializedName("delivery_fee")
        @Expose
        var deliveryFee: Double? = null
        @SerializedName("charges")
        @Expose
        var charges: String? = null
        @SerializedName("request_status")
        @Expose
        var requestStatus: String? = null
        @SerializedName("payment_method")
        @Expose
        var paymentMethod: String? = null
        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null
        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null
        @SerializedName("id")
        @Expose
        var id: Int? = null


        @SerializedName("pickup_address")
        @Expose
        var pickupAddress: String? = null
        @SerializedName("pickup_latitude")
        @Expose
        var pickupLatitude: String? = null
        @SerializedName("pickup_longitude")
        @Expose
        var pickupLongitude: String? = null
    }
}