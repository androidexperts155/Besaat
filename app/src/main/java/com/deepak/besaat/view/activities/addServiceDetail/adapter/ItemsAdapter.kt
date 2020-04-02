package com.deepak.besaat.view.activities.addServiceDetail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R


class ItemsAdapter(context: Context) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    var experienceList = context.resources.getStringArray(R.array.experienceList)

    lateinit var handler: ItemClickHandler
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_items, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return experienceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewItemName.setText(experienceList.get(position))
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                handler.OnItemClick(position, experienceList.get(position))
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewItemName: TextView

        init {
            textViewItemName = itemView.findViewById(R.id.textViewItemName)
        }
    }

    fun OnItemClickListener(handler: ItemClickHandler) {
        this.handler = handler
    }

    interface ItemClickHandler {
        fun OnItemClick(position: Int, name: String)
    }

}