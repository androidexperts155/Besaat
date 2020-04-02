package com.deepak.besaat.view.fragments.customerProfile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R

class PaymentMethodsAdapter: RecyclerView.Adapter<PaymentMethodsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.adapter_payments_method,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }
}