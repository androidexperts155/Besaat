package com.deepak.besaat.view.activities.servicesListing.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.AdapterServicesCategoryBinding
import com.deepak.besaat.model.getServicesModel.Datum
import com.deepak.besaat.view.activities.servicesListing.interfaces.IServiceCategory

class ServicesCategoryListAdapter(var activity: Activity, var servicesList: MutableList<Datum>) :
    ListAdapter<Datum, ServicesCategoryListAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: AdapterServicesCategoryBinding
    lateinit var itemList: OnItemClick2
    var iServiceCategory: IServiceCategory? = null
    fun attachCategoryClicks(iServiceCategory: IServiceCategory) {
        this.iServiceCategory = iServiceCategory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate<AdapterServicesCategoryBinding>(
            inflater,
            R.layout.adapter_services_category,
            parent,
            false
        )
        /*var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_services_category, null)*/
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.serviceDetail = getItem(position)
        holder.binding.relativeServices.setOnClickListener {
            if (iServiceCategory != null) {
                iServiceCategory?.onServiceCategoryClick(position)
            }
        }

        if (getItem(position).isChecked) {
            holder.binding.relativeServices.setBackgroundResource(R.drawable.bg_brown_all_side_four_dp)
            holder.binding.textServices.setTextColor(activity.resources.getColor(R.color.white))
        } else {
            holder.binding.relativeServices.setBackgroundResource(R.drawable.bg_light_white_all_side_four_dp)
            holder.binding.textServices.setTextColor(activity.resources.getColor(R.color.colorLightBlack))
        }
    }


    class ViewHolder(binding: AdapterServicesCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: AdapterServicesCategoryBinding

        init {
            this.binding = binding
        }
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