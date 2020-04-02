package com.deepak.besaat.view.activities.cancelOrderRequest.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ItemCancellationReasonBinding
import com.deepak.besaat.model.cancellationReasons.Reason

class ReasonsListAdapter(
    var activity: Activity,
    var reasonsList: MutableList<Reason>
) :
    ListAdapter<Reason, ReasonsListAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: ItemCancellationReasonBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)

        binding = DataBindingUtil.inflate<ItemCancellationReasonBinding>(
            inflater,
            R.layout.item_cancellation_reason,
            parent,
            false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.reason = getItem(position)

        holder.binding.radioDelType.setOnClickListener {
            itemClick.onReasonSelected(position, "")
        }

    }

    class ViewHolder(binding: ItemCancellationReasonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: ItemCancellationReasonBinding = binding
    }

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<Reason>() {
        override fun areItemsTheSame(oldItem: Reason, newItem: Reason): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reason, newItem: Reason): Boolean {
            return oldItem.equals(newItem)
        }
    }

    interface OnReasonSelectClick {
        fun onReasonSelected(position: Int, name: String)
    }

    lateinit var itemClick: OnReasonSelectClick

    fun onClickListener(onItemClick: OnReasonSelectClick) {
        this.itemClick = onItemClick
    }
}