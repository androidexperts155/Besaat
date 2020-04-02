package com.deepak.besaat.model.courierGuysModel

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.deepak.besaat.R
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import com.squareup.picasso.Picasso


class Courier() : Parcelable, ClusterItem {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null
    @SerializedName("logitude")
    @Expose
    var logitude: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("courier_status")
    @Expose
    var courierStatus: String? = null
    @SerializedName("distance")
    @Expose
    var distance: String? = null
    @SerializedName("experience_years")
    @Expose
    var experienceYears: String? = null
    @SerializedName("provider_role")
    @Expose
    var providerRole: String? = null
    @SerializedName("trips")
    @Expose
    var trips: Trips? = null


    var bitmapIcon: Bitmap? = null  // for map markers


    fun getDistanceString(): String {
        return if (distance != null && distance != "") {
            String.format("%.2f km", distance!!.toDouble())
        } else
            "0.0 km"
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        name = parcel.readString()
        address = parcel.readString()
        latitude = parcel.readString()
        logitude = parcel.readString()
        image = parcel.readString()
        distance = parcel.readString()
        experienceYears = parcel.readString()
        providerRole = parcel.readString()
        trips = parcel.readValue(Trips::class.java.classLoader) as Trips?
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(latitude)
        parcel.writeString(logitude)
        parcel.writeString(image)
        parcel.writeValue(distance)
        parcel.writeValue(providerRole)
        parcel.writeValue(experienceYears)
        parcel.writeValue(trips)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Courier> {
        override fun createFromParcel(parcel: Parcel): Courier {
            return Courier(parcel)
        }

        override fun newArray(size: Int): Array<Courier?> {
            return arrayOfNulls(size)
        }

        @BindingAdapter("bind:image_url")
        @JvmStatic
        fun loadImage(imageView: ImageView, image_url: String) {
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
        var latLng = LatLng(0.0, 0.0)
        if (latitude != null && latitude != "") {
            latLng = LatLng(latitude!!.toDouble(), logitude!!.toDouble())
        }

        return latLng
    }
}