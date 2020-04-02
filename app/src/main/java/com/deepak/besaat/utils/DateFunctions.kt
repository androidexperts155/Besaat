package com.deepak.besaat.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateFunctions {
    fun tweleveHoursFormat(date: String): String? {
        val inputPattern = "HH:mm"
        val outputPattern = "hh:mm a"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var newDate: Date? = null
        var str: String? = null

        try {
            newDate = inputFormat.parse(date)
            str = outputFormat.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }


    fun getTimeStamp(time: String): Long {
        val df = SimpleDateFormat("yyyy-MM-dd")
         var d:Date ?=null
        if(!time.equals("")) {
             d = df.parse(time)
        }
        val cal = Calendar.getInstance()
        if(d!=null)
        cal.time = d
        return cal.timeInMillis
    }

    fun isValidTime(arrivalDate: String,departureDate:String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a")

        var strDate:Date ? = null
        if(!arrivalDate.equals("")) {
            strDate = sdf.parse(arrivalDate)
        }
        var departureDatee :Date ?=null
         if(departureDate.equals("")){
              departureDatee = sdf.parse(departureDate)


         }
     /*   val departureDate = sdf.parse(departureDate)*/
             if(strDate!=null && departureDatee!=null) {
                 if (strDate.time > departureDatee.time) {
                     return true
                 } else {
                     return false
                 }
             }else{

                 return false
             }

    }
}