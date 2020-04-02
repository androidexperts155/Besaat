package com.deepak.besaat.model.socialLoginModel

import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.Nullable
import java.io.Serializable

class User : Serializable {

    @SerializedName("address")
    var address: Any? = null
    @SerializedName("approved")
    var approved: Any? = null
    @SerializedName("courier_status")
    var courierStatus: Any? = null
    @SerializedName("created_at")
    var createdAt: String? = null
    @SerializedName("device_token")
    var deviceToken: Any? = null
    @SerializedName("device_type")
    var deviceType: Any? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("email_verified_at")
    var emailVerifiedAt: Any? = null
    @SerializedName("facebook_id")
    var facebookId: String? = null
    @SerializedName("google_id")
    var googleId: Any? = null
    @SerializedName("id")
    var id: Long? = null
    @Nullable
    @SerializedName("image")
    var image: String? = null
    @SerializedName("instagram_id")
    var instagramId: Any? = null
    @SerializedName("language")
    var language: String? = null
    @SerializedName("latitude")
    var latitude: String? = null
    @SerializedName("license_image")
    var licenseImage: Any? = null
    @SerializedName("license_num")
    var licenseNum: Any? = null
    @SerializedName("logitude")
    var logitude: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("phone")
    var phone: String? = null
    @SerializedName("provider_role")
    var providerRole: Any? = null
    @SerializedName("role")
    var role: String? = null
    @SerializedName("status")
    var status: String? = null
    @SerializedName("updated_at")
    var updatedAt: String? = null
    @SerializedName("rating")
    var rating: String? = null
    @SerializedName("rate_per_hour")
    var ratePerHour: String? = null
    @SerializedName("offer")
    var offer: String? = null

    fun getRatingFloat(): Float? {
        return if (rating != null) {
            rating!!.toFloat()
        } else {
            0.0f
        }
    }

    fun getOfferInCurrency(): String? {
        if (offer != null) {
            return String.format("$%.2f", offer!!.toFloat())
        } else
            return offer
    }
}
