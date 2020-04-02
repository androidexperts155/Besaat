package com.deepak.besaat.view.activities.walkThrough.adapter

import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.deepak.besaat.R


class WalkThroughAdapter(
    items: Array<String>,
    icons: TypedArray
) : PagerAdapter() {
    var icons: TypedArray
    var items: Array<String>

    init {
        this.icons = icons
        this.items = items
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view =
            LayoutInflater.from(container.context).inflate(R.layout.adapter_walk_through, null)
        var textViewWelcome = view.findViewById<TextView>(R.id.textViewWelcome)
        var textViewWelcomeTitle = view.findViewById<TextView>(R.id.textViewWelcomeTitle)
        var imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(icons.getResourceId(position, -1))
        if(position==0){
            textViewWelcome.visibility=View.VISIBLE
        }else{
            textViewWelcome.visibility=View.GONE
        }
        textViewWelcomeTitle.setText(items.get(position))
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` == view;
    }

    override fun getCount(): Int {
        return 4
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}