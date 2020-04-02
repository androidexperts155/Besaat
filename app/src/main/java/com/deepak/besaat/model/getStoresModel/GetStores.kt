package com.deepak.besaat.model.getStoresModel

import com.google.gson.annotations.SerializedName

class GetStores {

    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null
    @SerializedName("data")
    var stores: MutableList<Store>? = null

}
