package com.deepak.besaat.view.activities.requestOffers.adapter

import android.app.Activity
import android.content.Intent
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.AdapterRequestOffersBinding
import com.deepak.besaat.databinding.AdapterServiceListBinding
import com.deepak.besaat.model.getServiceProviderModel.Datum
import com.deepak.besaat.model.myOrdersModel.Offer
import com.deepak.besaat.model.myOrdersModel.RequestOffersPojo
import com.deepak.besaat.view.activities.chat.ui.ChatActivity
import com.deepak.besaat.view.activities.deliveryPersonDetail.DeliveryPersonDetail
import com.deepak.besaat.view.activities.newRequestService.NewRequestService
import com.deepak.besaat.view.activities.requestOffers.interfaces.IOfferClick

class RequestOffersListAdapter(
    var activity: Activity,
    var offersList: MutableList<Offer>
) :
    ListAdapter<Offer, RequestOffersListAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: AdapterRequestOffersBinding
    lateinit var itemList: OnItemClick2
    private var mLastClickTime = 0.toLong()

    var iOfferClick: IOfferClick? = null
    fun attachOffersClicks(iOfferClick: IOfferClick) {
        this.iOfferClick = iOfferClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate<AdapterRequestOffersBinding>(
            inflater,
            R.layout.adapter_request_offers,
            parent,
            false
        )
        /*var view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_services_category, null)*/
        return ViewHolder(binding)
    }

    private fun lastClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.offer = getItem(position)

        holder.binding.rootLayout.setOnClickListener {
            lastClick()
//            var intent = Intent(activity, DeliveryPersonDetail::class.java)
//            intent.putExtra("name", getItem(position).name)
//            intent.putExtra("distance", getItem(position).distance.toString())
//            activity.startActivity(intent)
        }

        holder.binding.tvAccept.setOnClickListener {
            lastClick()
            if (iOfferClick != null) {
                iOfferClick?.onOfferAcceptClick(position)
            }
        }
    }

    class ViewHolder(var binding: AdapterRequestOffersBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<Offer>() {
        override fun areItemsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem.equals(newItem)
        }
    }

    interface OnItemClick2 {
        fun onPosClck(position: Int, name: String, value: String)
    }

    fun onClickListner(onItemClick: OnItemClick2) {
        this.itemList = onItemClick
    }
}