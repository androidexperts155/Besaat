package com.deepak.besaat.model.cancellationReasons

import com.google.gson.annotations.SerializedName

class Reason {
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("reason")
    var reason: String? = null
    var check: Boolean? = false
}
