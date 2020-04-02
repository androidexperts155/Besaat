package com.deepak.besaat.view.activities.storeNearByListing.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.AdapterStoreNearbyCategoryBinding
import com.deepak.besaat.model.getStoresModel.Store
import com.deepak.besaat.view.activities.storeNearByListing.interfaces.IStoreCategory

class StoresNearbyCategoryListAdapter(var activity: Activity, var storeCategoryList: MutableList<Store>) :
    ListAdapter<Store, StoresNearbyCategoryListAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: AdapterStoreNearbyCategoryBinding
    lateinit var itemList: OnItemClick2
    var iStoreCategory: IStoreCategory? = null
    fun attachCategoryClicks(iStoreCategory: IStoreCategory) {
        this.iStoreCategory = iStoreCategory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate<AdapterStoreNearbyCategoryBinding>(
            inflater,
            R.layout.adapter_store_nearby_category,
            parent,
            false
        )
        /*var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_services_category, null)*/
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.storeCategory = getItem(position)
        holder.binding.relativeStore.setOnClickListener {
            if (iStoreCategory != null) {
                iStoreCategory?.onStoreCategoryClick(position)
            }
        }

        if (getItem(position).checked) {
            holder.binding.relativeStore.setBackgroundResource(R.drawable.bg_light_bluelight_all_side_four_dp)
            holder.binding.textStoreCat.setTextColor(activity.resources.getColor(R.color.white))
        } else {
            holder.binding.relativeStore.setBackgroundResource(R.drawable.bg_light_white_all_side_four_dp)
            holder.binding.textStoreCat.setTextColor(activity.resources.getColor(R.color.categoryColor))
        }
    }


    class ViewHolder(binding: AdapterStoreNearbyCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: AdapterStoreNearbyCategoryBinding

        init {
            this.binding = binding
        }
    }

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
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