package com.deepak.besaat.view.activities.requestStatus.adapter

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
import com.deepak.besaat.databinding.AdapterRequestSentToBinding
import com.deepak.besaat.model.myOrdersModel.RequestSentTo

class RequestSentToServiceProviderListAdapter(
    var activity: Activity,
    var serviceProviderList: MutableList<RequestSentTo>
) :
    ListAdapter<RequestSentTo, RequestSentToServiceProviderListAdapter.ViewHolder>(
        StoreDetailDiffCallback()
    ) {
    lateinit var binding: AdapterRequestSentToBinding
    lateinit var itemList: OnItemClick2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate<AdapterRequestSentToBinding>(
            inflater,
            R.layout.adapter_request_sent_to,
            parent,
            false
        )
        /*var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_services_category, null)*/
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.serviceProvider = getItem(position)
    }

    class ViewHolder(binding: AdapterRequestSentToBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: AdapterRequestSentToBinding = binding
    }

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<RequestSentTo>() {
        override fun areItemsTheSame(oldItem: RequestSentTo, newItem: RequestSentTo): Boolean {
            return oldItem.getReceiverId() == newItem.getReceiverId()
        }

        override fun areContentsTheSame(oldItem: RequestSentTo, newItem: RequestSentTo): Boolean {
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