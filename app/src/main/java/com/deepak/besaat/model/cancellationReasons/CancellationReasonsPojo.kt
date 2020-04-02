package com.deepak.besaat.model.cancellationReasons

import com.deepak.besaat.model.myOrdersModel.Request
import com.google.gson.annotations.SerializedName

class CancellationReasonsPojo {

    @SerializedName("reasons")
    var reasons: MutableList<Reason>? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null
    @SerializedName("request")
    var request: Request? = null
}
