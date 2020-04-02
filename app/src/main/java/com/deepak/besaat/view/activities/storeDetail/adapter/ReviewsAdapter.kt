package com.deepak.besaat.view.activities.storeDetail.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.deepak.besaat.R
import com.deepak.besaat.model.reviewDetails.reviewDetails
import de.hdodenhof.circleimageview.CircleImageView

class ReviewsAdapter(activity: Activity, reviewList: MutableList<reviewDetails>) :
    RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {
    var activity = activity
    var reviewList = reviewList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_reviews, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textComment.text = reviewList.get(position).textComment
        Log.e("profilephoto", "profile photourl  is " + reviewList[position].profilePhotoUrl)
        if (reviewList[position].profilePhotoUrl != null)
            Glide.with(activity)
                .load(reviewList[position].profilePhotoUrl)
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
                .transform(CircleCrop()) //4
                .into(holder.circularImage)
    }

    fun reviewList(reviewList: MutableList<reviewDetails>) {
        this.reviewList = reviewList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circularImage = itemView.findViewById<CircleImageView>(R.id.circleImageView22)
        var textComment = itemView.findViewById<TextView>(R.id.textView30)
    }
}