package com.deepak.besaat.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.deepak.besaat.R
import com.google.android.material.snackbar.Snackbar
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CommonFunctions {
    var mDialogProgress: AlertDialog? = null

    fun showProgressBar(activity: Activity, message: String) {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setCancelable(false)
        val view = LayoutInflater.from(activity).inflate(R.layout.view_progress_dialog, null)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = message
        alertDialog.setView(view)

        mDialogProgress = alertDialog.create()
        mDialogProgress!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (mDialogProgress != null) {
            if (mDialogProgress!!.isShowing) {
                mDialogProgress?.dismiss()
            }
            if (!mDialogProgress!!.isShowing && !activity.isFinishing) {
                mDialogProgress?.show()
            }
        }

        val lp = WindowManager.LayoutParams()
        mDialogProgress!!.window.also {
            it!!.setGravity(Gravity.CENTER)
            lp.copyFrom(it.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            it.attributes = lp
        }
    }

    fun hideProgressBar() {
        if (mDialogProgress != null) {
            mDialogProgress!!.dismiss()
        }
    }

    fun showFeedbackMessage(view: View, message: String) {
        val snakbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snakbar.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorPrimary))
        snakbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            .maxLines = 5 //Get reference of snackbar textview
        if (snakbar.isShown) {
            snakbar.dismiss()
        }
        snakbar.show()
    }

    fun loadCircularImage(context: Context, imageUrl: String, circularImageView: CircleImageView) {
        var requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.ic_user_placeholder)
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl)
            .into(circularImageView)
    }

    fun getFormattedTimeOrDate(data: String, patternFrom: String, patternTo: String): String {
        var d: Date? = null
        val sdf = SimpleDateFormat(patternFrom, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        try {
            d = sdf.parse(data)
        } catch (ex: ParseException) {
            Log.e("exp", "" + ex.message)
        }
        sdf.timeZone = TimeZone.getDefault()
        sdf.applyPattern(patternTo)
        return "" + sdf.format(d)
    }

    fun getFormattedTimeOrDateSend(data: String, patternFrom: String, patternTo: String): String {
        var d: Date? = null
        val sdf = SimpleDateFormat(patternFrom, Locale.ENGLISH)
        try {
            d = sdf.parse(data)
        } catch (ex: ParseException) {
            Log.e("exp", "" + ex.message)
        }
        sdf.applyPattern(patternTo)
        return "" + sdf.format(d)
    }

    fun getCurrentDateTime(): String {
        val c = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        sdf.format(c)
        return sdf.format(c)
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            //            Bitmap resizedBitmap = Bitmap.createScaledBitmap(myBitmap, 300, 300, false);
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun KMToMiles(distance:Double):Double
    {
        var mi=0.621371
        return distance*mi
    }
}