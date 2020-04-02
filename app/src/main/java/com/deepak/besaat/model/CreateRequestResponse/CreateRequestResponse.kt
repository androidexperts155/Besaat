package com.deepak.besaat.model.CreateRequestResponse

import com.deepak.besaat.model.buyerSignUpModel.Data
import com.google.gson.annotations.SerializedName


class CreateRequestResponse {

    @SerializedName("message")
    var message: String? = null


    @SerializedName("status")
    var status: Boolean? = null
    @SerializedName("social")
    var socila: Social? = null


    class Social{



    }

}