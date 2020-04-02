package com.deepak.besaat.utils

import android.text.TextUtils
import android.util.Patterns
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.util.regex.Pattern

class Validations {

    fun isFieldEmpty(value: String?): Boolean {
        return TextUtils.isEmpty(value?.trim())
    }


    fun isInvalidEmail(value:String?):Boolean{
        return !Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }

    fun isValidMobile(phone: String): Boolean {
        var check = false
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            // if (phone.length() < 10 || phone.length() > 14) {
            if (phone.length < 6) {
                check = false

            } else {
                check = true
            }
        } else {
            check = false
        }
        return check
    }

    fun isMinLength2(char: String): Boolean {
        var check = false

            if (char.length < 2) {
                check = false

            } else {
                check = true
            }
        return check
    }


    fun isMinLength6(char: String): Boolean {
        var check = false

        if (char.length < 6) {
            check = false

        } else {
            check = true
        }
        return check
    }

    fun isMinLength10(char: String): Boolean {
        var check = false

        if (char.length < 10) {
            check = false

        } else {
            check = true
        }
        return check
    }


}