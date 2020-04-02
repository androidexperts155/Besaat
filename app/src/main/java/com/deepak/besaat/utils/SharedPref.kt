package com.deepak.besaat.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Application) {
    private val sharedpreferences: SharedPreferences
    private val sharedpreferences2: SharedPreferences

    init {
        sharedpreferences = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
        sharedpreferences2 = context.getSharedPreferences(Constants.Language, Context.MODE_PRIVATE)
    }


    fun setString(name: String, value: String) {
        sharedpreferences.edit().putString(name, value).commit()
    }
    fun getString(name: String): String {
        return sharedpreferences.getString(name,"")!!
    }
    fun getLong(name:String): Long {
        return sharedpreferences.getLong(name,0)
    }
    fun setLong(name:String,long:Long){
        sharedpreferences.edit().putLong(name, long).commit()
    }



    fun getStringLanguage(name: String): String {
        return sharedpreferences2.getString(name,"")!!
    }

    fun setStringLanugage(name: String, value: String) {
        sharedpreferences2.edit().putString(name, value).commit()
    }

    fun getWalkThrough(name: String): Boolean {
        return sharedpreferences2.getBoolean(name,false)!!
    }

    fun setWalkThrough(name: String, value: Boolean) {
        sharedpreferences2.edit().putBoolean(name, value).commit()
    }


    fun clear() {
        sharedpreferences.edit().clear().commit()
    }
}