package com.deepak.besaat.view.activities.servicesListing.adapter

import android.app.Activity
import android.content.Intent
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.AdapterServiceListBinding
import com.deepak.besaat.model.getServiceProviderModel.Datum
import com.deepak.besaat.view.activities.chat.ui.ChatActivity
import com.deepak.besaat.view.activities.deliveryPersonDetail.DeliveryPersonDetail
import com.deepak.besaat.view.activities.newRequestService.NewRequestService

class ServiceProviderListAdapter(
    var activity: Activity,
    var serviceProviderList: MutableList<Datum>
) :
    ListAdapter<Datum, ServiceProviderListAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: AdapterServiceListBinding
    lateinit var itemList: OnItemClick2
    private var mLastClickTime = 0.toLong()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate<AdapterServiceListBinding>(
            inflater,
            R.layout.adapter_service_list,
            parent,
            false
        )
        /*var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_services_category, null)*/
        return ViewHolder(binding)
    }

    private fun lastClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.serviceProvider = getItem(position)

        holder.binding.rootLayout.setOnClickListener {
            lastClick()
            var intent = Intent(activity, DeliveryPersonDetail::class.java)
//            intent.putExtra("data", getItem(position))
            intent.putExtra("name", getItem(position).name)
            intent.putExtra("distance", getItem(position).distance.toString())
            intent.putExtra("providerLat", getItem(position).latitude.toString())
            intent.putExtra("providerLng", getItem(position).logitude.toString())
            intent.putExtra("providerAddress", getItem(position).address)
            intent.putExtra("image", getItem(position).image)
            intent.putExtra("id", getItem(position).id.toString())
            intent.putExtra("from", "service")
            intent.putExtra("deliveryType", "4")// dummy
            activity.startActivity(intent)
        }

        holder.binding.textViewRequest.setOnClickListener {
            lastClick()
            var intent = Intent(activity, NewRequestService::class.java)
            intent.putExtra("id", getItem(position).id.toString())
            intent.putExtra("name", getItem(position).name)
            intent.putExtra("distance", getItem(position).distance.toString())
            intent.putExtra("providerLat", getItem(position).latitude.toString())
            intent.putExtra("providerLng", getItem(position).logitude.toString())
            intent.putExtra("providerAddress", getItem(position).address)
            intent.putExtra("availableDays", getItem(position).availableDays)
            intent.putExtra("serviceTimeFrom", getItem(position).serviceTimeFrom)
            intent.putExtra("serviceTimeTo", getItem(position).serviceTimeTo)
            activity.startActivity(intent)
        }

        holder.binding.ivChat.setOnClickListener {
            lastClick()
            var intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("id", getItem(position).id.toString())
            intent.putExtra("name", getItem(position).name)
            intent.putExtra("image", getItem(position).image)
            intent.putExtra("from", "service")
            activity.startActivity(intent)
        }
    }

    class ViewHolder(binding: AdapterServiceListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: AdapterServiceListBinding = binding
    }

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<Datum>() {
        override fun areItemsTheSame(oldItem: Datum, newItem: Datum): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Datum, newItem: Datum): Boolean {
            return oldItem.equals(newItem)
        }
    }

    interface OnItemClick2 {
        fun onPosClck(position: Int, name: String, value: String)
    }

    fun onClickListner(onItemClick: OnItemClick2) {
        this.itemList = onItemClick
    }
}