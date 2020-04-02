package com.deepak.besaat.model.chatModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class ChatHistoryPojo {
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("chats")
    @Expose
    private var chats: List<Chat?>? = null

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

    fun getChats(): List<Chat?>? {
        return chats
    }

    fun setChats(chats: List<Chat?>?) {
        this.chats = chats
    }
}