package com.deepak.besaat.view.activities.courierGuysListing.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.AdapterCourierGuyzBinding
import com.deepak.besaat.databinding.ItemDeliveyTypeBinding
import com.deepak.besaat.model.courierGuysModel.GetDeliveryTypesPojo.Datum

class DeliveryTypesAdapter(
    var activity: Activity,
    var courierServiceProviderList: MutableList<Datum>
) :
    ListAdapter<Datum, DeliveryTypesAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: ItemDeliveyTypeBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)

        binding = DataBindingUtil.inflate<ItemDeliveyTypeBinding>(
            inflater,
            R.layout.item_delivey_type,
            parent,
            false
        )

        /*var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_services_category, null)*/
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.deliveryType = getItem(position)

        holder.binding.radioDelType.setOnClickListener {
            itemClick.onPosClick(position, "")
        }

    }

    class ViewHolder(binding: ItemDeliveyTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: ItemDeliveyTypeBinding = binding
    }

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<Datum>() {
        override fun areItemsTheSame(oldItem: Datum, newItem: Datum): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Datum, newItem: Datum): Boolean {
            return oldItem.equals(newItem)
        }
    }

    interface OnDeliveryTypeSelectClick {
        fun onPosClick(position: Int, name: String)
    }

    lateinit var itemClick: OnDeliveryTypeSelectClick

    fun onClickListner(onItemClick: OnDeliveryTypeSelectClick) {
        this.itemClick = onItemClick
    }
}