package com.deepak.besaat.utils

import java.io.IOException

class ApiException(message:String):IOException(message)
class NoInternetException(message: String):IOException(message){


    override val message: String?
        get() = super.message
}

