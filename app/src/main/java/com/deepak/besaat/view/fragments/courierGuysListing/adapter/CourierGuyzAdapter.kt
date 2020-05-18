package com.deepak.besaat.view.fragments.courierGuysListing.adapter

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
import com.deepak.besaat.view.activities.newRequestCourier.NewRequestCourier

class CourierGuyzAdapter(activity: Activity) : RecyclerView.Adapter<CourierGuyzAdapter.ViewHolder>() {
    var activity=activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_courier_guyz, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewRequest.setOnClickListener {
            activity.startActivity(Intent(activity,NewRequestCourier::class.java))
        }

        holder.itemView.setOnClickListener {
            activity.startActivity(Intent(activity,DeliveryPersonDetail::class.java))
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewRequest = itemView.findViewById<TextView>(R.id.textViewRequest)
    }
}