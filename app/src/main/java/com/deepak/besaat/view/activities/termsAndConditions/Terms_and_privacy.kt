package com.deepak.besaat.view.activities.termsAndConditions

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.deepak.besaat.R
import kotlinx.android.synthetic.main.activity_terms_conditions.*

class Terms_and_privacy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_conditions)

        textViewTitleName.setText(getString(R.string.terms_and_privacy))

        imageViewbackArrow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                finish()
            }
        })
    }

    fun customToolBarWithBack(context: AppCompatActivity, toolbar: Toolbar) {
        context.setSupportActionBar(toolbar)
        context.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        context.supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        val view = LayoutInflater.from(context).inflate(R.layout.view_tool_bar_with_back_icon, null)
        var textViewTitleName = view.findViewById<TextView>(R.id.textViewTitleName)


        var imageViewbackArrow = view.findViewById<ImageView>(R.id.imageViewbackArrow)
        context.supportActionBar!!.customView = view

        textViewTitleName.visibility ==View.VISIBLE

    }


}
