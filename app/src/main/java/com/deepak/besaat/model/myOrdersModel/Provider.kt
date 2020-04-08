package com.deepak.besaat.model.myOrdersModel

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.deepak.besaat.R
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import java.io.Serializable

class Provider {
        @SerializedName("id")
        @Expose
        var id: Int? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("image")
        @Expose
        var image: String? = null
        @SerializedName("distance")
        @Expose
        var distance: Double? = null
        @SerializedName("rating")
        var rating: String? = null

        fun getDistanceString(): String {
                return if (distance != null)
                        String.format("%.2f km away", distance)
                else
                        "N/A"
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

    }