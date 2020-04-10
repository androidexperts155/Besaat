package com.deepak.besaat.view.activities.storeNearByListing.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.deepak.besaat.R
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView
import com.google.android.gms.maps.model.LatLng

import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.deepak.besaat.view.activities.newRequestStore.NewRequestStore
import com.deepak.besaat.view.activities.storeDetail.StoreDetailActivity
import java.text.DecimalFormat


class StoresNearbyListAdapter(
    activity: Activity,
    sourceDetails: MutableList<SourceDetails>,
    latitude: String?,
    longitude: String?
) : RecyclerView.Adapter<StoresNearbyListAdapter.ViewHolder>() {
    var activity = activity
    var latitude = latitude
    var longitude = longitude

    var storeList = sourceDetails
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_stores_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    fun addList(sourceDetails: MutableList<SourceDetails>) {
        this.storeList = sourceDetails
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewRequest.setOnClickListener {
            var int: Intent? = Intent(activity, NewRequestStore::class.java)
            int?.putExtra("latitute", storeList[position].latitute!!)
            int?.putExtra("longitute", storeList[position].longitute!!)
            int?.putExtra("location", storeList[position].locAddress)
            int?.putExtra("title", storeList[position].name)
            int?.putExtra("from", "")
            int?.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(int)
        }


        /*  holder.itemView.setOnClickListener {
              activity.startActivity(Intent(activity,StoreDetailActivity::class.java))
          }*/
        holder.textViewName.text = storeList[position].name
        holder.textViewAddress.text = storeList[position].locAddress
        if (storeList.get(position).rating != null) {
            holder.ratingBar.rating = storeList[position].rating as Float
            holder.textReviewCount.text = storeList[position].rating.toString()
        } else {
            holder.ratingBar.rating = 0.0f
            holder.textReviewCount.text = "0.0"
        }
        var radiusValue = 0.0

        if (latitude.equals("") && longitude.equals("")) {
            radiusValue = calculationByDistance(
                LatLng(latitude!!.toDouble(), longitude!!.toDouble()),
                LatLng(storeList[position].latitute, storeList[position].longitute)
            )
        } else {
            radiusValue = calculationByDistance(
                LatLng(latitude!!.toDouble(), longitude!!.toDouble()),
                LatLng(storeList.get(position).latitute, storeList.get(position).longitute)
            )
        }

        if (radiusValue.toString().length > 4) {
            holder.textRadius.text = radiusValue.toString().substring(0, 5) + " km"
        } else if (radiusValue.toString().length > 3) {
            holder.textRadius.text = radiusValue.toString().substring(0, 4) + " km"
        } else if (radiusValue.toString().length > 2) {
            holder.textRadius.text = radiusValue.toString().substring(0, 3) + " km"
        } else if (radiusValue.toString().length > 1) {

            holder.textRadius.text = radiusValue.toString().substring(0, 2) + " km"
        } else if (radiusValue.toString().isNotEmpty()) {

            holder.textRadius.text = radiusValue.toString().substring(0, 1) + " km"
        }

        var profilePicUrl =
            Constants.GET_PROFILE_URL + storeList.get(position).photoRefrence + "&sensor=false&maxheight=" + storeList.get(
                position
            ).heightt + "&maxwidth=" + storeList.get(position).widthh + "&key=" + activity.resources.getString(
                R.string.google_api
            )

        Glide.with(activity)
            .load(profilePicUrl)
            .placeholder(R.drawable.icn_no_image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.circularImage.setImageResource(R.drawable.icn_no_image)
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

        holder.topContrain.setOnClickListener { view ->
            var int: Intent? = Intent(view?.context, StoreDetailActivity::class.java)
            int?.putExtra("title", storeList.get(position).name)
            int?.putExtra("location", storeList.get(position).locAddress)
            int?.putExtra("imageurl", profilePicUrl)

            int?.putExtra("rating", storeList.get(position).rating!!)
            int?.putExtra("no_rating_users", storeList.get(position).userRatingsTotal!!)
            int?.putExtra("latitute", storeList.get(position).latitute!!)
            int?.putExtra("longitute", storeList.get(position).longitute!!)
            int?.putExtra("placeid", storeList.get(position).placeId)
            int?.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            view?.context?.startActivity(int)
        }

    }

    fun calculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371// radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        return Radius * c
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewRequest = itemView.findViewById<TextView>(R.id.textViewRequest)
        var textViewName = itemView.findViewById<TextView>(R.id.textView19);
        var textViewAddress = itemView.findViewById<TextView>(R.id.textView20)
        var ratingBar = itemView.findViewById<RatingBar>(R.id.ratingBar);
        var textReviewCount = itemView.findViewById<TextView>(R.id.reviewcountText)
        var textRadius = itemView.findViewById<TextView>(R.id.radiusInKm)
        var circularImage = itemView.findViewById<CircleImageView>(R.id.circleImageView)
        var topContrain = itemView.findViewById<ConstraintLayout>(R.id.topConstrain)
    }
}