package com.deepak.besaat.view.activities.selectCourierType

import android.content.Intent
import android.os.Bundle
import com.deepak.besaat.R
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.courierGuysListing.CourierGuysListing
import kotlinx.android.synthetic.main.activity_select_courier_type.*

class SelectCourierType : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_courier_type)
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = getString(R.string.choose_courier_type)

        textViewContinue.setOnClickListener {
            startActivity(Intent(this@SelectCourierType,CourierGuysListing::class.java))
        }
    }
}
