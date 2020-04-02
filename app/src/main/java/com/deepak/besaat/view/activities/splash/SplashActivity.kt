package com.deepak.besaat.view.activities.splash

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.deepak.besaat.R
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.utils.FrequentFunctions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.createprofile.CreateProfileActivity
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.activities.selectLanguage.SelectLanguageActivity
import com.deepak.besaat.view.activities.signup.SignupActivity
import com.deepak.besaat.view.activities.walkThrough.WalkThroughActivity
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import com.google.android.gms.common.util.IOUtils.toByteArray
import android.content.pm.PackageManager.GET_SIGNATURES
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : BaseActivity() {
    val sharedPref:SharedPref by inject()
    val frequentFunctions:FrequentFunctions by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        intentToNextScreen()
        AlertDialog.Builder(this)

        printHashKey(this)
    }

    fun intentToNextScreen(){
        Coroutines.backThread{
            delay(2000)
             var language=sharedPref.getStringLanguage(Constants.USER_Language)
             var walkThrough=sharedPref.getWalkThrough(Constants.WALK_THROUGH)
             if(language.equals("")){
                 startActivity(Intent(this@SplashActivity,SelectLanguageActivity::class.java))
                 finishAffinity()
             }else if(!walkThrough){
                 startActivity(Intent(this@SplashActivity,WalkThroughActivity::class.java))
                 finishAffinity()
             }else{
                 if(sharedPref.getString(Constants.TOKEN).equals("")){
                     frequentFunctions.changeLaguage(sharedPref.getStringLanguage(Constants.USER_Language),this@SplashActivity)
                     startActivity(Intent(this@SplashActivity,SignupActivity::class.java))
                     finishAffinity()
                 }else{
                     frequentFunctions.changeLaguage(sharedPref.getStringLanguage(Constants.USER_Language),this@SplashActivity)
                     startActivity(Intent(this@SplashActivity,HomeActivity::class.java))
                     finishAffinity()
                 }
             }
        }
    }

    fun printHashKey(pContext: Context) {
        try {
            val info = pContext.getPackageManager()
                .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.e("", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {

        } catch (e: Exception) {
        }
    }
}
