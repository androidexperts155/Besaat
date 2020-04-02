package com.deepak.besaat.view.fragments.MyVisitFragment

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.deepak.besaat.Interfaces.iFragmentCommunicator
import com.deepak.besaat.R
import com.deepak.besaat.model.GetFlag
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.model.upcomingTripModel.upcomingTripModel
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.FlagCountry
import com.deepak.besaat.utils.FragmentFucntions
import com.deepak.besaat.view.activities.customerSupport.ui.CustomerSupportActivity
import com.deepak.besaat.view.fragments.AddTrip.AddTripFragment
import com.rilixtech.widget.countrycodepicker.Country
import com.rilixtech.widget.countrycodepicker.CountryUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class VisitListAdaptor(
    activity: FragmentActivity,
    storeList: MutableList<upcomingTripModel>, type: Int
) : Adapter<VisitListAdaptor.ViewHolder>() {
    var activity = activity
    var type = type
    var storeList = storeList
    lateinit var context: Context
    var iFragmentCommunicator: iFragmentCommunicator? = null

    fun attachIFragmentCommunicator(iFragmentCommunicator: iFragmentCommunicator) {
        this.iFragmentCommunicator = iFragmentCommunicator
    }

    fun setList(storeList: MutableList<upcomingTripModel>, type: Int) {
        this.type = type
        this.storeList = storeList
    }

    // type 1=upcoming, 2=past

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.added_trip_layout, parent, false)
        context = parent.context
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return storeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("TripResponse", "on bind view holder ")

        var storeid = storeList[position].id

        holder.flagDeparturePending.setImageResource(
            FlagCountry.getFlagDrawableResId(
                storeList?.get(
                    position
                ).departureCountry?.toLowerCase()
            )
        )
        holder.flagArrivalPending.setImageResource(
            FlagCountry.getFlagDrawableResId(
                storeList?.get(
                    position
                ).arrivalCountry?.toLowerCase()
            )
        )
        holder.textdeparturePending.text = storeList.get(position).departureCountry
        holder.textarrivalpending.text = storeList.get(position).arrivalCountry

        holder.flagDeparture.setImageResource(
            FlagCountry.getFlagDrawableResId(
                storeList?.get(
                    position
                ).departureCountry?.toLowerCase()
            )
        )
        holder.flagArrival.setImageResource(FlagCountry.getFlagDrawableResId(storeList?.get(position).arrivalCountry?.toLowerCase()))
        holder.textdeparture.text = storeList.get(position).departureCountry
        holder.textArrival.text = storeList.get(position).arrivalCountry

//        if (storeList?.get(position).status == "1") {
//            holder.linearActive.visibility = View.GONE
//            holder.linearPending.visibility = View.VISIBLE
//        } else {
//            holder.linearPending.visibility = View.GONE
//            holder.linearActive.visibility = View.VISIBLE
//        }
//        if (storeList?.get(position).status == "2") {
//            holder.rlinearActiveBlue.visibility = View.VISIBLE
//            holder.rlinaerRedDenied.visibility = View.GONE
//            if (storeList?.get(position).isPast) {
//                holder.textActiveStatus.text = "Completed"
//            } else {
//                holder.textActiveStatus.text = "Active"
//            }
//        } else {
//            holder.rlinearActiveBlue.visibility = View.GONE
//            holder.rlinaerRedDenied.visibility = View.VISIBLE
//        }
        ///////////////////////////////////
        if (storeList?.get(position).status == "1") {
            holder.linearActive.visibility = View.GONE
            holder.linearPending.visibility = View.VISIBLE
            holder.tvStatus.text = "Pending"
            holder.llEditDelete.visibility = View.VISIBLE
            holder.llStatus.setBackgroundResource(R.drawable.bd_yellow_drawable)
        } else if (storeList?.get(position).status == "2") {
            holder.linearActive.visibility = View.GONE
            holder.linearPending.visibility = View.VISIBLE
            holder.llStatus.setBackgroundResource(R.drawable.bd_blue_drawable)
            if (storeList?.get(position).isPast) {
                holder.tvStatus.text = "Completed"
                holder.llEditDelete.visibility = View.GONE
            } else {
                holder.tvStatus.text = "Active"
                holder.llEditDelete.visibility = View.VISIBLE
            }
        } else if (storeList?.get(position).status == "3") {
            holder.linearActive.visibility = View.VISIBLE
            holder.linearPending.visibility = View.GONE
            holder.rlinearActiveBlue.visibility = View.GONE
            holder.rlinaerRedDenied.visibility = View.VISIBLE
            holder.llEditDelete.visibility = View.GONE
        }

        ///////////////////////////////////

        holder.tripId.text = storeid.toString()

        holder.textViewArrivalDate.text = CommonFunctions().getFormattedTimeOrDate(
            storeList[position].createdDate!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_MMM_D_YYYY
        )

        holder.textViewArrivalTime.text = CommonFunctions().getFormattedTimeOrDate(
            storeList[position].createdDate!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_HH_MM
        )

        holder.textViewDepartureDate.text = CommonFunctions().getFormattedTimeOrDate(
            storeList[position].updatedDate!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_MMM_D_YYYY
        )

        holder.textViewDepartureTime.text = CommonFunctions().getFormattedTimeOrDate(
            storeList[position].updatedDate!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_HH_MM
        )

        if (!storeList.get(position).imageUrl.equals("")) {
            holder.imageBottm.visibility = View.VISIBLE
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
                        holder.imageBottm.visibility = View.GONE
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

                .into(holder.imageBottm)
        } else {
            holder.imageBottm.visibility = View.GONE
        }

        holder.ivEdit.setOnClickListener {
            if (storeList?.get(position).status == "1") {
                var addTripFragment = AddTripFragment.newInstance()
                var bundle = Bundle()
                bundle.putSerializable("data", storeList[position])
                addTripFragment.arguments = bundle
                var fragFunc = FragmentFucntions()
                fragFunc.replaceFragment2(
                    context as AppCompatActivity,
                    addTripFragment,
                    R.id.container
                )
            } else {
                var intent = Intent(context, CustomerSupportActivity::class.java)
                intent.putExtra("from", "trip")
                intent.putExtra("purpose", "edit")
                intent.putExtra("data", storeList[position])
                context.startActivity(intent)
            }
        }

        holder.ivDelete.setOnClickListener {
            if (storeList?.get(position).status == "1") {
                if (iFragmentCommunicator != null) {
                    if (type == 2)
                        iFragmentCommunicator?.passData(position, 1, "past")
                    else
                        iFragmentCommunicator?.passData(position, 1, "up")
                }
            } else {
                var intent = Intent(context, CustomerSupportActivity::class.java)
                intent.putExtra("from", "trip")
                intent.putExtra("purpose", "delete")
                intent.putExtra("data", storeList[position])
                context.startActivity(intent)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivEdit = itemView.findViewById<ImageView>(R.id.ivEdit)
        var ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)
        var flagDeparture = itemView.findViewById<ImageView>(R.id.flagarrival)
        var flagArrival = itemView.findViewById<ImageView>(R.id.flagdeparture)
        var textViewArrivalDate = itemView.findViewById<TextView>(R.id.arrivalDate)
        var textViewDepartureTime = itemView.findViewById<TextView>(R.id.departureTime);
        var textViewDepartureDate = itemView.findViewById<TextView>(R.id.departureDate)
        var textViewArrivalTime = itemView.findViewById<TextView>(R.id.arrivalTime)
        var tripId = itemView.findViewById<TextView>(R.id.tripid)
        var tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        var linearPending = itemView.findViewById<RelativeLayout>(R.id.linearPending)
        var linearActive = itemView.findViewById<RelativeLayout>(R.id.relativeActive)
        var imageBottm = itemView.findViewById<ImageView>(R.id.imageBottom)
        var flagDeparturePending = itemView.findViewById<ImageView>(R.id.flagdeparturepending)
        var flagArrivalPending = itemView.findViewById<ImageView>(R.id.flagarrivalpending)
        var rlinearActiveBlue = itemView.findViewById<LinearLayout>(R.id.activeText)
        var rlinaerRedDenied = itemView.findViewById<LinearLayout>(R.id.deniewZText)
        var llEditDelete = itemView.findViewById<LinearLayout>(R.id.llEditDelete)
        var llStatus = itemView.findViewById<LinearLayout>(R.id.llStatus)
        var activeStatus = itemView.findViewById<TextView>(R.id.textActiveStatus)
        var textdeparture = itemView.findViewById<TextView>(R.id.textDeparture)
        var textArrival = itemView.findViewById<TextView>(R.id.textArrival)
        var textdeparturePending = itemView.findViewById<TextView>(R.id.textDeparturePending)
        var textarrivalpending = itemView.findViewById<TextView>(R.id.textArrivalPending)
        var textActiveStatus = itemView.findViewById<TextView>(R.id.textActiveStatus)
    }

}