package com.deepak.besaat.view.activities.selectLanguage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.deepak.besaat.R
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.FrequentFunctions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.walkThrough.WalkThroughActivity
import kotlinx.android.synthetic.main.activity_select_language.*
import org.koin.android.ext.android.inject

class SelectLanguageActivity : AppCompatActivity(),View.OnClickListener {


    val frequentFunctions: FrequentFunctions by inject()
    val sharedPref:SharedPref by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_language)
        relativeLayoutEnglish.setOnClickListener(this)
        relativeLayoutUrdu.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
              when(view!!.id){
                  R.id.relativeLayoutEnglish->{
                      sharedPref.setStringLanugage(Constants.USER_Language,"en")
                      frequentFunctions.changeLaguage(sharedPref.getStringLanguage(Constants.USER_Language),this)
                      startActivity(Intent(this@SelectLanguageActivity, WalkThroughActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                  }

                  R.id.relativeLayoutUrdu->{
                      sharedPref.setStringLanugage(Constants.USER_Language,"ar")
                      frequentFunctions.changeLaguage(sharedPref.getStringLanguage(Constants.USER_Language),this)
                      startActivity(Intent(this@SelectLanguageActivity, WalkThroughActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                  }
              }
    }
}
