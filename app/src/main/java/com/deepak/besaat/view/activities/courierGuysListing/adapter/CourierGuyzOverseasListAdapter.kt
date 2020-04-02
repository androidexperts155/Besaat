package com.deepak.besaat.view.activities.courierGuysListing.adapter

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
import com.deepak.besaat.databinding.AdapterCourierGuyzBinding
import com.deepak.besaat.databinding.AdapterCourierGuyzLocalBinding
import com.deepak.besaat.model.courierGuysModel.Courier
import com.deepak.besaat.view.activities.chat.ui.ChatActivity
import com.deepak.besaat.view.activities.deliveryPersonDetail.DeliveryPersonDetail
import com.deepak.besaat.view.activities.newRequestCourier.NewRequestCourier
import com.deepak.besaat.view.activities.newRequestCourierOverSeas.NewRequestCourierOverseasActivity

class CourierGuyzOverseasListAdapter(
    var activity: Activity,
    var courierServiceProviderList: MutableList<Courier>,
    var deliveryType: String
) :
    ListAdapter<Courier, CourierGuyzOverseasListAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: AdapterCourierGuyzBinding
    lateinit var itemList: OnItemClick2
    private var mLastClickTime = 0.toLong()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)

            binding = DataBindingUtil.inflate<AdapterCourierGuyzBinding>(
                inflater,
                R.layout.adapter_courier_guyz,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    private fun lastClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.courierGuy = getItem(position)

        holder.binding.rootLayout.setOnClickListener {
            lastClick()
            var intent = Intent(activity, DeliveryPersonDetail::class.java)
//            intent.putExtra("data", getItem(position))
            intent.putExtra("name", getItem(position).name)
            intent.putExtra("distance", getItem(position).distance)
            intent.putExtra("providerLat", getItem(position).latitude)
            intent.putExtra("providerLng", getItem(position).logitude)
            intent.putExtra("providerAddress", getItem(position).address)
            intent.putExtra("pickupCountry", getItem(position).trips?.getDepartureCountry())
            intent.putExtra("dropCountry", getItem(position).trips?.getArrivalCountry())
            intent.putExtra("image", getItem(position).image)
            intent.putExtra("courierStatus", getItem(position).courierStatus)
            intent.putExtra("id", getItem(position).id.toString())
            intent.putExtra("from", "CourierOverSeas")
            intent.putExtra("deliveryType", deliveryType)
            activity.startActivity(intent)
        }

        holder.binding.textViewRequest.setOnClickListener {
            lastClick()
            var intent = Intent(activity, NewRequestCourierOverseasActivity::class.java)
//            intent.putExtra("data", getItem(position))
            intent.putExtra("id", getItem(position).id.toString())
            intent.putExtra("name", getItem(position).name)
            intent.putExtra("distance", getItem(position).distance)
            intent.putExtra("providerLat", getItem(position).latitude)
            intent.putExtra("providerLng", getItem(position).logitude)
            intent.putExtra("providerAddress", getItem(position).address)
            intent.putExtra("pickupCountry", getItem(position).trips?.getDepartureCountry())
            intent.putExtra("dropCountry", getItem(position).trips?.getArrivalCountry())
            intent.putExtra("deliveryType", deliveryType)
            activity.startActivity(intent)
        }

        holder.binding.ivChat.setOnClickListener {
            lastClick()
            var intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("id", getItem(position).id.toString())
            intent.putExtra("name", getItem(position).name)
            intent.putExtra("image", getItem(position).image)
            intent.putExtra("from", "CourierOverSeas")
            activity.startActivity(intent)
        }
    }

    class ViewHolder(binding: AdapterCourierGuyzBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: AdapterCourierGuyzBinding = binding
    }

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<Courier>() {
        override fun areItemsTheSame(oldItem: Courier, newItem: Courier): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Courier, newItem: Courier): Boolean {
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