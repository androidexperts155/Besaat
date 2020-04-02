package com.deepak.besaat.model.chatModel

import android.os.Handler
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Chat {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("message_from")
    @Expose
    var messageFrom: Int? = null
    @SerializedName("message_to")
    @Expose
    var messageTo: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("media_type")
    @Expose
    var mediaType: String? = null
    @SerializedName("media_runtime")
    @Expose
    var mediaRuntime: String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null
    @SerializedName("longitude")
    @Expose
    var longitude: String? = null
    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("media")
    @Expose
    var media: String? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
    @SerializedName("from_user_image")
    @Expose
    var fromUserImage: String? = null
    @SerializedName("to_user_image")
    @Expose
    var toUserImage: String? = null

    var isPlaying:Boolean=false
    var playPosition:Int=0

    var handler: Handler=Handler()
}