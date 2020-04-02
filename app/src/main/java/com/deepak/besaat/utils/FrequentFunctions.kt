package com.deepak.besaat.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager

import androidx.core.content.ContextCompat.getSystemService
import android.net.wifi.WifiManager
import android.content.pm.PackageManager
import android.R.attr.versionName
import android.app.AppComponentFactory
import android.content.pm.PackageInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import java.util.*
import android.text.Spannable
import android.text.style.StyleSpan
import androidx.core.content.res.ResourcesCompat
import android.graphics.Typeface
import android.text.SpannableString
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability


class FrequentFunctions(context: Application) {
var context:Application
    init {

        this.context=context
    }

    fun hideKeyBoard(activity: Context, view: View) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun changeLaguage(laguage:String,context: Context){
        val locale = Locale(laguage)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics())
    }

    fun getVersionNumber(activity: Activity):String{
        var version=""
        try {
            val pInfo = context.packageManager.getPackageInfo(activity.getPackageName(), 0)
            version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return version

    }


    fun changeStatusBarColor(activity: AppCompatActivity,color:Int){
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }


    fun setCustomFontTypeSpan(context: Context, source: String, startIndex: Int, endIndex: Int, font: Int): SpannableString {
        val spannableString = SpannableString(source)
        val typeface = ResourcesCompat.getFont(context, font)
        spannableString.setSpan(StyleSpan(typeface!!.style), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(activity)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show()
            }
            return false
        }
        return true
    }
}