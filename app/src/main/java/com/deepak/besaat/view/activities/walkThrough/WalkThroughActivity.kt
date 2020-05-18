package com.deepak.besaat.view.activities.walkThrough

import android.content.Intent
import android.content.res.TypedArray
import android.os.Bundle
import com.deepak.besaat.R
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.createprofile.CreateProfileActivity
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.activities.signup.SignupActivity

import com.deepak.besaat.view.activities.walkThrough.adapter.WalkThroughAdapter
import kotlinx.android.synthetic.main.activity_walk_through.*
import org.koin.android.ext.android.inject

class WalkThroughActivity : BaseActivity() {
    lateinit var icons: TypedArray
    lateinit var items: Array<String>
    val sharedPreferences:SharedPref by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_through)
        setUpAdapter()

        linearLayoutGetStarted.setOnClickListener {
            sharedPreferences.setWalkThrough(Constants.WALK_THROUGH,true)
            startActivity(Intent(this@WalkThroughActivity, SignupActivity::class.java))
            finish()
        }
    }

    //<editor-fold desc="view pager adapter set here">
    fun setUpAdapter(){
        items = resources.getStringArray(R.array.welcome_screens_titles)
        icons = resources.obtainTypedArray(R.array.welcome_screens_images)
        var adapter=WalkThroughAdapter(items,icons)
        viewPager.adapter=adapter
        indicator.setViewPager(viewPager)
    }
    //</editor-fold>
}
