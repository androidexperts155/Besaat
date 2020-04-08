package com.deepak.besaat.model.GetUserDetailsModel

import com.deepak.besaat.model.socialLoginModel.User
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class GetUserDetailsPojo {
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("user")
    @Expose
    private var user: User? = null

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getUser(): User? {
        return user
    }

    fun setUser(user: User?) {
        this.user = user
    }

}