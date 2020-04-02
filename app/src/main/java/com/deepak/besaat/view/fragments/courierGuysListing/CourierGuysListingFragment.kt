package com.deepak.besaat.view.fragments.courierGuysListing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepak.besaat.R
import com.deepak.besaat.utils.DividerItemDecoration
import com.deepak.besaat.view.fragments.BaseFragment
import com.deepak.besaat.view.fragments.courierGuysListing.adapter.CourierGuyzAdapter
import kotlinx.android.synthetic.main.activity_courier_guys_listing.*

class CourierGuysListingFragment:BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_courier_guys_listing,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
    }


    fun setUpAdapter(){
        var adapter= CourierGuyzAdapter(activity!!)
        recyclerViewCourierGuysListing.layoutManager= LinearLayoutManager(activity)
        recyclerViewCourierGuysListing.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(activity!!,R.drawable.divider)))
        recyclerViewCourierGuysListing.adapter=adapter
    }
}