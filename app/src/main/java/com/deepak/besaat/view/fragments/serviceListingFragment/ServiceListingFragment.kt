package com.deepak.besaat.view.fragments.serviceListingFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepak.besaat.R
//import com.deepak.besaat.utils.DividerItemDecoration
import com.deepak.besaat.view.fragments.BaseFragment
import com.deepak.besaat.view.fragments.serviceListingFragment.adapter.ServiceListAdapter
import kotlinx.android.synthetic.main.fragment_service_listing.*

class ServiceListingFragment:BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view=inflater.inflate(R.layout.fragment_service_listing,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
    }


    private fun setUpAdapter(){
        var adapter= ServiceListAdapter(activity!!)
        recyclerViewServiceListingAdapter.layoutManager= LinearLayoutManager(activity)
      //  recyclerViewServiceListingAdapter.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(activity!!,R.drawable.divider)))
        recyclerViewServiceListingAdapter.adapter=adapter
    }
}