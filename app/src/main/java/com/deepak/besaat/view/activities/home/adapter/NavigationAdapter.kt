package com.deepak.besaat.view.activities.home.adapter

import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R

class NavigationAdapter(navigationsItems: Array<String>, icons: TypedArray) : RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {
    private var drawerListener: FragmentDrawerListener? = null
    var navigationsItems:Array<String>
    var icons:TypedArray
    init {
        this.navigationsItems=navigationsItems
        this.icons=icons
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.adapter_navigaton,null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return navigationsItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageViewIcon.setImageResource(icons.getResourceId(position, -1))
        holder.textViewItemName.setText(navigationsItems[position])
        holder.lnrCells.setOnClickListener(View.OnClickListener {
            drawerListener?.onDrawerItemSelected(position)
        })


    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
           var imageViewIcon=itemView.findViewById<ImageView>(R.id.imageViewIcon)
           var textViewItemName=itemView.findViewById<TextView>(R.id.textViewItemName)
        var lnrCells =itemView.findViewById<LinearLayout>(R.id.lnrCells)

    }

    fun setDrawerListener(listener: FragmentDrawerListener) {
        this.drawerListener = listener
    }

    interface FragmentDrawerListener {
        fun onDrawerItemSelected( position: Int)
    }
}