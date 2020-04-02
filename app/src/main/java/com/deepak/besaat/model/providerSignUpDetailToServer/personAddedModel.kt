package com.deepak.besaat.model.providerSignUpDetailToServer

import com.deepak.besaat.model.getUserProfile.Data
import com.google.gson.annotations.SerializedName

class personAddedModel{

    @SerializedName("name")
    var name:String? = null
    @SerializedName("distance")
    var distance: String? = null
    @SerializedName("image")
    var imageUrl:String? = null

    var checked:Boolean=false







}