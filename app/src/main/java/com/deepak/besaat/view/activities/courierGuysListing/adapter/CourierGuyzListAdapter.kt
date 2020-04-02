package com.deepak.besaat.view.activities.courierGuysListing.adapter

import android.app.Activity
import android.content.Intent
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

class CourierGuyzListAdapter(
    var activity: Activity,
    var courierServiceProviderList: MutableList<Courier>,
    var courierStatus: String
) :
    ListAdapter<Courier, CourierGuyzListAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: AdapterCourierGuyzBinding
    lateinit var itemList: OnItemClick2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
            binding = DataBindingUtil.inflate<AdapterCourierGuyzBinding>(
                inflater,
                R.layout.adapter_courier_guyz,
                parent,
                false
            )

        /*var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_services_category, null)*/
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.courierGuy = getItem(position)

        holder.binding.rootLayout.setOnClickListener {
            var intent = Intent(activity, DeliveryPersonDetail::class.java)
//            intent.putExtra("data", getItem(position))
            intent.putExtra("name", getItem(position).name)
            activity.startActivity(intent)
        }

        holder.binding.textViewRequest.setOnClickListener {
            var intent = Intent(activity, NewRequestCourier::class.java)
//            intent.putExtra("data", getItem(position))
            intent.putExtra("id", getItem(position).id.toString())
            intent.putExtra("name", getItem(position).name)
            intent.putExtra("distance", getItem(position).distance)
            intent.putExtra("providerLat", getItem(position).latitude)
            intent.putExtra("providerLng", getItem(position).logitude)
            intent.putExtra("providerAddress", getItem(position).address)
            activity.startActivity(intent)
        }

        holder.binding.ivChat.setOnClickListener {
            var intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("id", getItem(position).id.toString())
            intent.putExtra("name", getItem(position).name)
            intent.putExtra("image", getItem(position).image)
            intent.putExtra("from", "courier")
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