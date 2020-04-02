package com.deepak.besaat.model.ModelSourceDeatils

import android.graphics.Bitmap
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem

@Entity(tableName = "stores")
class SourceDetails : ClusterItem {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "place_id")
    @SerializedName("place_id")
    var placeId: String? = null

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    var createdAt: String? = null

    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description: String? = null

    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Long? = null

    @ColumnInfo(name = "icon")
    @SerializedName("icon")
    var icon: String? = null

    @ColumnInfo(name = "image")
    @SerializedName("image")
    var image: String? = null

    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name: String? = null

    @ColumnInfo(name = "status")
    @SerializedName("status")
    var status: String? = null

    @ColumnInfo(name = "updated_at")
    @SerializedName("updated_at")
    var updatedAt: String? = null

    @ColumnInfo(name = "lat")
    @SerializedName("lat")
    var latitute: Double = 0.0

    @ColumnInfo(name = "lng")
    @SerializedName("lng")
    var longitute: Double = 0.0

    @ColumnInfo(name = "vicinity")
    @SerializedName("vicinity")
    var locAddress: String? = null

    @ColumnInfo(name = "rating")
    @SerializedName("rating")
    var rating: Float? = 0.0f

    @ColumnInfo(name = "photo_reference")
    @SerializedName("photo_reference")
    var photoRefrence: String? = null

    @ColumnInfo(name = "height")
    @SerializedName("height")
    var heightt: Int? = null

    @ColumnInfo(name = "width")
    @SerializedName("width")
    var widthh: Int? = null

    @ColumnInfo(name = "user_ratings_total")
    @SerializedName("user_ratings_total")
    var userRatingsTotal: Int? = 0

    @ColumnInfo(name = "radius")
    var radius: Double = 0.0

    @ColumnInfo(name = "category")
    var category: String = ""

//    var bitmapIcon: Bitmap? = null

    override fun getSnippet(): String {
        return name!!
    }

    override fun getTitle(): String {
        return name!!
    }

    override fun getPosition(): LatLng {
        var latLng = LatLng(latitute, longitute)
        return latLng
    }
}
