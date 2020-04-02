package com.deepak.besaat.network

import com.deepak.besaat.utils.ApiException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.lang.StringBuilder

open class SafeApiRequest {
    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val error = response.errorBody()!!.string()
            val messageString= StringBuilder()
            error?.let {
                try {
                    var message = JSONObject(it).get("message")
                    messageString.append(message)
                } catch (e: JSONException) {
                    messageString.append("Error code: ${response.code()}")
                    e.printStackTrace()
                }
            }
            throw ApiException(messageString.toString())
        }
    }
}