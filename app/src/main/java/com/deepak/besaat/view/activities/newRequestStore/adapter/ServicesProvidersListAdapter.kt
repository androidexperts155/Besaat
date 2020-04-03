package com.deepak.besaat.view.activities.newRequestStore.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.AdapterManualServiceProviderBinding
import com.deepak.besaat.model.socialLoginModel.User
import com.deepak.besaat.view.activities.newRequestStore.interfaces.IServiceProviderClickListener

class ServicesProvidersListAdapter(var activity: Activity, var servicesProvidersList: MutableList<User>) :
    ListAdapter<User, ServicesProvidersListAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: AdapterManualServiceProviderBinding
    lateinit var itemList: OnItemClick2
    var iServiceProviderClickListener: IServiceProviderClickListener? = null
    fun attachServiceProviderClicks(iServiceProviderClickListener: IServiceProviderClickListener) {
        this.iServiceProviderClickListener = iServiceProviderClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate<AdapterManualServiceProviderBinding>(
            inflater,
            R.layout.adapter_manual_service_provider,
            parent,
            false
        )
        /*var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_services_category, null)*/
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.serviceProviderDetail = getItem(position)
        holder.binding.textViewSelect.setOnClickListener {
            if (iServiceProviderClickListener != null) {
                iServiceProviderClickListener?.onServiceProviderClick(position)
            }
        }
        if (getItem(position).isChecked) {
            holder.binding.textViewSelect.setBackgroundResource(R.drawable.bg_blue_rounded)
            holder.binding.textViewSelect.setTextColor(activity.resources.getColor(R.color.white))
            holder.binding.textViewSelect.text = activity.getString(R.string.selected)
            holder.binding.textViewSelect.setPadding(25, 10, 25, 10)
        } else {
            holder.binding.textViewSelect.setBackgroundResource(R.drawable.rounded_drawable_borders)
            holder.binding.textViewSelect.setTextColor(activity.resources.getColor(R.color.buttonselctedColor))
            holder.binding.textViewSelect.text = activity.getString(R.string.select)
            holder.binding.textViewSelect.setPadding(40, 10, 40, 10)
        }
    }


    class ViewHolder(var binding: AdapterManualServiceProviderBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
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