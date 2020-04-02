package com.deepak.besaat.model.courierGuysModel

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.deepak.besaat.R
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.FlagCountry
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import java.io.Serializable

class Trips {
    @SerializedName("id")
    @Expose
    private var id: Int? = null
    @SerializedName("user_id")
    @Expose
    private var userId: Int? = null
    @SerializedName("departure_country")
    @Expose
    private var departureCountry: String? = null
    @SerializedName("arrival_country")
    @Expose
    private var arrivalCountry: String? = null
    @SerializedName("departure_date")
    @Expose
    private var departureDate: String? = null
    @SerializedName("arrival_date")
    @Expose
    private var arrivalDate: String? = null
    @SerializedName("image")
    @Expose
    private var image: String? = null
    @SerializedName("status")
    @Expose
    private var status: String? = null
    @SerializedName("status_reason")
    @Expose
    private var statusReason: String? = null

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getUserId(): Int? {
        return userId
    }

    fun setUserId(userId: Int?) {
        this.userId = userId
    }

    fun getDepartureCountry(): String? {
        return departureCountry
    }

    fun setDepartureCountry(departureCountry: String?) {
        this.departureCountry = departureCountry
    }

    fun getArrivalCountry(): String? {
        return arrivalCountry
    }

    fun setArrivalCountry(arrivalCountry: String?) {
        this.arrivalCountry = arrivalCountry
    }

    fun getDepartureDate(): String? {
        return CommonFunctions().getFormattedTimeOrDate(
            departureDate!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_DD_MMM_YYYY_HH_MM
        )
    }

    fun setDepartureDate(departureDate: String?) {
        this.departureDate = departureDate
    }

    fun getArrivalDate(): String? {
        return CommonFunctions().getFormattedTimeOrDate(
            arrivalDate!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_DD_MMM_YYYY_HH_MM
        )
    }

    fun setArrivalDate(arrivalDate: String?) {
        this.arrivalDate = arrivalDate
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getStatusReason(): String? {
        return statusReason
    }

    fun setStatusReason(statusReason: String?) {
        this.statusReason = statusReason
    }


    companion object DataBindingAdapter : Serializable {
        @BindingAdapter("bind:flag_url")
        @JvmStatic
        fun loadFlagImage(imageView: ImageView, flag_url: String) {
//            if (image_url.isNotEmpty())

            Picasso.get().load(FlagCountry.getFlagDrawableResId(flag_url.toLowerCase())).fit()
                .placeholder(R.drawable.bd_blue_drawable)
                .error(R.drawable.bg_white_stroke_gray_tl_br_bl_round_20)
                .into(imageView)
        }
    }
}