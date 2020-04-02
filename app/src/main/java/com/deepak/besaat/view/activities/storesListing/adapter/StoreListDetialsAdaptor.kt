package com.deepak.besaat.view.activities.storesListing.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails

class StoreListDetialsAdaptor(activity: Activity, storeList: MutableList<SourceDetails>) : RecyclerView.Adapter<StoreListDetialsAdaptor.ViewHolder>() {

    var storeList = storeList
    var activity = activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_stores_list, null)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
      return  storeList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewName.text=storeList.get(position).name
    }

    fun addList(sourceDetails: MutableList<SourceDetails>) {
        this.storeList=sourceDetails
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //var textViewRequest=itemView.findViewById<TextView>(R.id.textViewRequest)
        var textViewName=itemView.findViewById<TextView>(R.id.textView19);
    }

}