package com.deepak.besaat.model.socialLoginModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.Nullable
import java.io.Serializable


class User : Serializable {
    var isChecked: Boolean = false
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

    ////////////////////////////////////////

    @SerializedName("experience_years")
    @Expose
    var experienceYears: String? = null
    @SerializedName("available_days")
    @Expose
    var availableDays: String? = null
    @SerializedName("service_time_from")
    @Expose
    var serviceTimeFrom: String? = null
    @SerializedName("service_time_to")
    @Expose
    var serviceTimeTo: String? = null
    @SerializedName("departure_country")
    @Expose
    var departureCountry: String? = null
    @SerializedName("departure_date")
    @Expose
    var departureDate: String? = null
    @SerializedName("arrival_country")
    @Expose
    var arrivalCountry: String? = null
    @SerializedName("arrival_date")
    @Expose
    var arrivalDate: String? = null
    @SerializedName("otp")
    @Expose
    var otp: String? = null
    @SerializedName("is_verified")
    @Expose
    var isVerified: String? = null
    @SerializedName("is_applied_for_provider")
    @Expose
    var isAppliedForProvider: String? = null
    @SerializedName("distance")
    @Expose
    var distance: String? = null

    ////////////////////////////////////////

    fun getDistanceString(): String {
        return if (distance != null)
            String.format("%.2f km", distance!!.toFloat())
        else
            "N/A"
    }

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
