package com.deepak.besaat.view.fragments.serviceListingFragment.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.view.activities.deliveryPersonDetail.DeliveryPersonDetail
import com.deepak.besaat.view.activities.newRequestService.NewRequestService

class ServiceListAdapter(activity: Activity) : RecyclerView.Adapter<ServiceListAdapter.ViewHolder>() {
    var activity=activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.adapter_service_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                  holder.textViewRequest.setOnClickListener {
                      activity.startActivity(Intent(activity,NewRequestService::class.java))
                  }


        holder.itemView.setOnClickListener {
            activity.startActivity(Intent(activity,DeliveryPersonDetail::class.java))
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
           var textViewRequest=itemView.findViewById<TextView>(R.id.textViewRequest)
    }
}