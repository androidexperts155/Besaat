package com.deepak.besaat.model.getServiceProviderModel

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.deepak.besaat.R
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import com.squareup.picasso.Picasso
import java.io.Serializable


class Datum : Serializable,ClusterItem {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("email_verified_at")
    @Expose
    var emailVerifiedAt: String? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
    @SerializedName("phone")
    @Expose
    var phone: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: Double = 0.0
    @SerializedName("logitude")
    @Expose
    var logitude: Double = 0.0
    @SerializedName("license_num")
    @Expose
    var licenseNum: String? = null
    @SerializedName("license_image")
    @Expose
    var licenseImage: String? = null
    @SerializedName("facebook_id")
    @Expose
    var facebookId: String? = null
    @SerializedName("google_id")
    @Expose
    var googleId: String? = null
    @SerializedName("instagram_id")
    @Expose
    var instagramId: String? = null
    @SerializedName("experience_years")
    @Expose
    var experienceYears: String? = null
    @SerializedName("rate_per_hour")
    @Expose
    var ratePerHour: String? = null
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
    @SerializedName("device_type")
    @Expose
    var deviceType: String? = null
    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = null
    @SerializedName("language")
    @Expose
    var language: String? = null
    @SerializedName("role")
    @Expose
    var role: String? = null
    @SerializedName("provider_role")
    @Expose
    var providerRole: String? = null
    @SerializedName("courier_status")
    @Expose
    var courierStatus: String? = null
    @SerializedName("is_applied_for_provider")
    @Expose
    var isAppliedForProvider: String? = null
    @SerializedName("approved")
    @Expose
    var approved: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("services")
    @Expose
    var services: String? = null
    @SerializedName("distance")
    @Expose
    var distance: Double? = null
    @SerializedName("rating")
    @Expose
    var rating: String? = null

    fun getDistanceString(): String {
        return if (distance != null)
            String.format("%.2f km", distance)
        else
            "N/A"
    }

    fun getRatePerHourString(): String {
        return if (ratePerHour != null)
            String.format("$%s/hr", ratePerHour)
        else
            "$0/hr"
    }

    fun getExpString(): String {
        return if (experienceYears != null) {
//            if (experienceYears == "1") {
//                String.format("%s+ Year's Exp", experienceYears)
//            } else {
                String.format("%s+ Years' Exp", experienceYears)
//            }
        } else
            "0 Year's Exp"
    }

    fun getRatingFloat(): Float? {
        return if (rating != null && rating != "") {
            rating!!.toFloat()
        } else {
            0.0f
        }
    }

    fun getRatingString(): String? {
        return if (rating != null && rating != "") {
            String.format("%.2f", rating!!.toFloat())
        } else {
            rating
        }
    }

    var bitmapIcon: Bitmap? = null  // for map markers

    companion object DataBindingAdapter : Serializable {
        @BindingAdapter("bind:image_url")
        @JvmStatic
        fun loadImage(imageView: ImageView, image_url: String) {
//            if (image_url.isNotEmpty())
            Picasso.get().load(image_url).fit()
                .placeholder(R.drawable.bd_blue_drawable)
                .error(R.drawable.icn_no_image)
                .into(imageView)
        }
    }

    override fun getSnippet(): String {
        return name!!
    }

    override fun getTitle(): String {
        return name!!
    }

    override fun getPosition(): LatLng {
        var latLng = LatLng(latitude, logitude)
        return latLng
    }

}