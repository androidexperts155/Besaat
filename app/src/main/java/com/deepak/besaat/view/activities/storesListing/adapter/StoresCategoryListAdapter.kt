package com.deepak.besaat.view.activities.storesListing.adapter

import android.app.Activity
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.model.getStoresModel.Store

class StoresCategoryListAdapter(
    activity: Activity,
    storeList: MutableList<Store>
) :
    RecyclerView.Adapter<StoresCategoryListAdapter.ViewHolder>() {
    var storeList = storeList
    var activity = activity
    var value: String = ""

    var postionClick: Int = 0
    private var mLastClickTime = 0.toLong()
    lateinit var onItemClick: OnItemClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_stores_list_category, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("response", "on bind view holder in store list " + storeList.get(position).name)
        /*   if(getname!=null) {
               getPosition(getname)
           }*/
        checkedFalse()
        holder.txtOut.text = storeList.get(position).name
        holder.cardOut.background =
            activity.resources.getDrawable(R.drawable.bg_light_white_all_side_four_dp)
        // holder.cardOut.setBackgroundColor(activity.resources.getColor(R.color.colorWhiteAlpha))
        // holder.txtOut.setTextColor(activity.resources.getColor(R.color.colorDarkGray))

        if (postionClick == position) {
            //holder.cardOut.setBackgroundColor(activity.resources.getColor(R.color.colorAccent))
            //  holder.txtOut.setTextColor(activity.resources.getColor(R.color.colorWhiteAlpha))
            holder.cardOut.background =
                activity.resources.getDrawable(R.drawable.bg_light_bluelight_all_side_four_dp)
            holder.txtOut.setTextColor(activity.resources.getColor(R.color.white))
            //holder.cardOut.setBackgroundColor(activity.resources.getColor(R.color.colorWhiteAlpha))
            // holder.txtOut.setTextColor(activity.resources.getColor(R.color.colorDarkGray))
        } else {
            // holder.cardOut.setBackgroundColor(activity.resources.getColor(R.color.colorAccent))
            // holder.txtOut.setTextColor(activity.resources.getColor(R.color.colorWhiteAlpha))
            holder.cardOut.setBackgroundColor(activity.resources.getColor(R.color.colorWhiteAlpha))
            holder.txtOut.setTextColor(activity.resources.getColor(R.color.categoryColor))
            holder.cardOut.background =
                activity.resources.getDrawable(R.drawable.bg_light_white_all_side_four_dp)
        }

        if (!storeList[position].checked) {
            holder.cardOut.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 2000) {
                    mLastClickTime = SystemClock.elapsedRealtime()
                    storeList[position].checked = true
                    onItemClick.onPosClck(position, storeList[position].name.toString(), value)
                }
            }
        }
    }

    private fun checkedFalse() {
        for (item in storeList) {
            item.checked = false
        }
    }

    private fun getPosition(getname: String) {
        for ((i, item) in storeList.withIndex()) {
            if (item.name.toString() == getname) {
                notifyAdapter(i)
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtOut: TextView = itemView.findViewById(R.id.txtOut)
        var cardOut: CardView = itemView.findViewById(R.id.cardOut)
    }

    interface OnItemClick {
        fun onPosClck(position: Int, name: String, value: String)
    }

    fun onClickListner(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }

    fun notifyAdapter(position: Int) {
        postionClick = position
        notifyDataSetChanged()
    }
}