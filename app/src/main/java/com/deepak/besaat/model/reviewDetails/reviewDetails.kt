package com.deepak.besaat.model.reviewDetails

import com.google.gson.annotations.SerializedName

class reviewDetails {

    @SerializedName("author_name")
    var author:String?=null
    @SerializedName("profile_photo_url")
    var profilePhotoUrl:String?=null
    @SerializedName("text")
    var textComment:String?=null
    @SerializedName("rating")
    var rating:Int?=0

}
