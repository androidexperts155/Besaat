package com.deepak.besaat.view.activities.addServiceDetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


import com.deepak.besaat.model.getServicesModel.Datum
import android.content.ClipData.Item
import com.deepak.besaat.R


class ServicesAdapter(servicesList: MutableList<Datum>) :

    RecyclerView.Adapter<ServicesAdapter.ViewHolder>() {
    private var isAllChecked: Boolean = false
    var servicesList = servicesList
    lateinit var listener: ItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_services, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return servicesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewName.text = servicesList[position].name
        holder.checkBox.isChecked = servicesList[position].isChecked
//        holder.checkBox.isChecked = isAllChecked
        holder.checkBox.setOnCheckedChangeListener { compoundButton, b ->
            if (servicesList[position].name?.toLowerCase()!!.contains("all")) {
                if (b) {
                    setAllChecked(true)
                } else {
                    setAllChecked(false)
                }
            }

            listener.onCheckBoxClick(b, servicesList[position], position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewName: TextView
        var checkBox: CheckBox

        init {
            textViewName = itemView.findViewById(R.id.textViewName)
            checkBox = itemView.findViewById(R.id.checkbox)
        }

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onClickListener)
        }
    }

    interface ItemClickListener {
        fun onCheckBoxClick(
            isChekced: Boolean,
            get: Datum,
            position: Int
        )
    }

    fun itemClickHandler(listener: ItemClickListener) {
        this.listener = listener
    }

    private fun setAllChecked(isAllChecked: Boolean) {
        this.isAllChecked = isAllChecked
        for (i in 0 until servicesList.size) {
            servicesList[i].isChecked = isAllChecked
        }
        notifyDataSetChanged()
    }
}