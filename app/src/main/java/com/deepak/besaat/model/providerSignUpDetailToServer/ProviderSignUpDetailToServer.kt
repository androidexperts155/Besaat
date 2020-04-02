package com.deepak.besaat.model.providerSignUpDetailToServer

import com.google.gson.annotations.SerializedName
import java.io.File

class ProviderSignUpDetailToServer {

    @SerializedName("arrival_country")
    var arrivalCountry: String? = null
    @SerializedName("arrival_date")
    var arrivalDate: String? = null
    @SerializedName("available_days")
    var availableDays: String? = null
    @SerializedName("courier_status")
    var courierStatus: String? = null
    @SerializedName("departure_country")
    var departureCountry: String? = null
    @SerializedName("departure_date")
    var departureDate: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("experience_years")
    var experienceYears: String? = null
    @SerializedName("facebook_id")
    var facebookId: String? = null
    @SerializedName("google_id")
    var googleId: String? = null
    @SerializedName("instagram_id")
    var instagramId: String? = null
    @SerializedName("license_num")
    var licenseNum: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("other_service")
    var otherService: String? = null
    @SerializedName("rate_per_hour")
    var ratePerHour: String? = null
    @SerializedName("phone")
    var phone: String? = null
    @SerializedName("provider_role")
    var providerRole: String? = null
    @SerializedName("service_ids")
    var serviceIds: String? = null
    @SerializedName("service_time_from")
    var serviceTimeFrom: String? = null
    @SerializedName("service_time_to")
    var serviceTimeTo: String? = null

    @SerializedName("image_url")
    var image_url=""

    var licenseImage: File?=null
    var image: File?=null


    var requestImage : File ?=null


    var account_holder_name=""
    var account_number=""
    var ifsc_code=""

}
