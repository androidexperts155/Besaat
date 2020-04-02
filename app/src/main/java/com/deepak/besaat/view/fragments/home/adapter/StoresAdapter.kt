package com.deepak.besaat.view.fragments.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.AdapterStoresBinding
import com.deepak.besaat.model.getStoresModel.Store
import com.deepak.besaat.viewModel.HomeFragmentViewModel

class StoresAdapter(viewModel: HomeFragmentViewModel) : ListAdapter<Store,StoresAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    var viewModel: HomeFragmentViewModel = viewModel

    lateinit var binding:AdapterStoresBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate<AdapterStoresBinding>(inflater, R.layout.adapter_stores, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.binding.storeDetail=getItem(position)
        holder.binding.viewModel=viewModel
       //holder.binding.storeDetail=getItem(position)
    }


    class ViewHolder(binding: AdapterStoresBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding:AdapterStoresBinding
        init {
            this.binding=binding
        }
    }
    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(
            oldItem: Store,
            newItem: Store
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Store,
            newItem: Store
        ): Boolean {
            return oldItem.equals(newItem)
        }
    }
}