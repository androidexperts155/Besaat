package com.deepak.besaat.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.deepak.besaat.R
import com.deepak.besaat.view.fragments.MapFragment.MapFragment


class FragmentFucntions {
    var fm: FragmentManager? = null
    var ft: FragmentTransaction? = null

    fun replaceFragment(activity: FragmentActivity?, fragment: Fragment, id: Int) {
        fm = activity?.getSupportFragmentManager()
        ft = fm!!.beginTransaction()
        ft!!.replace(id, fragment)
        ft!!.commit()
    }

    fun replaceFragment2(activity: AppCompatActivity, fragment: Fragment, id: Int) {
        fm = activity.getSupportFragmentManager()
        ft = fm!!.beginTransaction()
        ft!!.replace(id, fragment)
        ft!!.addToBackStack(null)
        ft!!.commit()
    }

    fun removeFragment(activity: AppCompatActivity, id: Int) {
        fm = activity.getSupportFragmentManager()
        var count: Int? = fm?.backStackEntryCount
        if (fm != null) {
            while (fm?.getBackStackEntryCount()!! > 0) {
                fm?.popBackStackImmediate();
            }
        }


    }

    fun removeAllFragments(activity: AppCompatActivity) {
        var fragmentManager: FragmentManager = activity.supportFragmentManager
        while (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
    }

    fun addFragment(activity: AppCompatActivity, fragment: Fragment) {
        var backstackName = fragment.javaClass.name
        fm = activity.getSupportFragmentManager()
        ft = fm!!.beginTransaction()
        ft!!.add(R.id.container, fragment)
        ft!!.addToBackStack(backstackName)
        ft!!.commit()
    }

    fun popFragment(activity: AppCompatActivity) {
        fm = activity.supportFragmentManager
        fm!!.popBackStack()
    }
}