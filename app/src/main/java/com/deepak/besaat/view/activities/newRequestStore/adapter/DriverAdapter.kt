package com.deepak.besaat.view.activities.newRequestStore.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.deepak.besaat.Interfaces.Selected
import com.deepak.besaat.R
import com.deepak.besaat.model.providerSignUpDetailToServer.personAddedModel
import de.hdodenhof.circleimageview.CircleImageView

class DriverAdapter(storeList: MutableList<personAddedModel>) :
    RecyclerView.Adapter<DriverAdapter.ViewHolder>() {

    var storeList = storeList
    var clicked: Boolean = false
    lateinit var selectedd: Selected
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_driver, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    fun setInerface(selected: Selected) {
        selectedd = selected


    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textName.text = storeList.get(position).name
        holder.textDistance.text = storeList.get(position).distance?.substring(0, 4) + " km"

        holder.textViewRequest.setBackgroundResource(R.drawable.rounded_drawable_borders)
        holder.textViewRequest.setTextColor(context.resources.getColor(R.color.buttonselctedColor))
        holder.textViewRequest.text = "Select"
        holder.textViewRequest.setPadding(40, 10, 40, 10)

        if (storeList.get(position).checked) {

            holder.textViewRequest.setBackgroundResource(R.drawable.bg_blue_rounded)
            holder.textViewRequest.setTextColor(context.resources.getColor(R.color.white))
            holder.textViewRequest.text = "Selected"
            holder.textViewRequest.setPadding(25, 10, 25, 10)
        } else {

            holder.textViewRequest.setBackgroundResource(R.drawable.rounded_drawable_borders)
            holder.textViewRequest.setTextColor(context.resources.getColor(R.color.buttonselctedColor))
            holder.textViewRequest.text = "Select"
            holder.textViewRequest.setPadding(40, 10, 40, 10)

        }

        holder.textViewRequest.setOnClickListener(object : View.OnClickListener {


            override fun onClick(view: View?) {
                if (!clicked) {
                    clicked = true
                    storeList.get(position).checked = true
                    selectedd.selected(storeList[position].checked)
                    holder.textViewRequest.setBackgroundResource(R.drawable.bg_blue_rounded)
                    holder.textViewRequest.setTextColor(context.resources.getColor(R.color.white))
                    holder.textViewRequest.text = "Selected"
                    holder.textViewRequest.setPadding(25, 11, 25, 11)
                } else {
                    clicked = false
                    storeList.get(position).checked = false
                    selectedd.selected(storeList[position].checked)
                    holder.textViewRequest.setBackgroundResource(R.drawable.rounded_drawable_borders)
                    holder.textViewRequest.setTextColor(context.resources.getColor(R.color.buttonselctedColor))
                    holder.textViewRequest.text = "Select"
                    holder.textViewRequest.setPadding(40, 11, 40, 11)
                }
            }
        })
        Glide.with(context)
            .load(storeList?.get(position).imageUrl)
            .placeholder(R.drawable.placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // holder.progress.visibility=View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    //   holder.progress.visibility=View.VISIBLE
                    return false
                }


            })
            .diskCacheStrategy(DiskCacheStrategy.DATA)

            .into(holder.circleImageView)
    }

    fun selectedButton(holder: ViewHolder, position: Int) {
        for (item in storeList) {
            item.checked = false
        }
        storeList.get(position).checked = true
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circleImageView = itemView.findViewById<CircleImageView>(R.id.circleImageView)
        var textName = itemView.findViewById<TextView>(R.id.textView19)
        var textOrder = itemView.findViewById<TextView>(R.id.ordertext)
        var textDistance = itemView.findViewById<TextView>(R.id.distanceText)
        var textViewRequest = itemView.findViewById<TextView>(R.id.textViewRequest)
    }
}