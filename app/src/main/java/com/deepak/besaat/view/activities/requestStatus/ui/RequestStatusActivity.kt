package com.deepak.besaat.view.activities.requestStatus.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityRequestStatusBinding
import com.deepak.besaat.model.cancellationReasons.CancellationReasonsPojo
import com.deepak.besaat.model.myOrdersModel.Request
import com.deepak.besaat.model.myOrdersModel.RequestSentTo
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.DividerItemDecoration
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.cancelOrderRequest.ui.CancelOrderRequestActivity
import com.deepak.besaat.view.activities.chat.ui.ChatActivity
import com.deepak.besaat.view.activities.courierGuysListing.CourierGuysListing
import com.deepak.besaat.view.activities.newRequestStore.NewRequestStore
import com.deepak.besaat.view.activities.requestOffers.ui.RequestOffersActivity
import com.deepak.besaat.view.activities.requestStatus.adapter.RequestSentToServiceProviderListAdapter
import com.deepak.besaat.view.activities.servicesListing.ServicesListingActivity
import com.deepak.besaat.view.activities.storeNearByListing.activities.StoreNearByListingActivity
import com.deepak.besaat.viewModel.RequestStatusViewModel
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_request_status.*
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt


class RequestStatusActivity : BaseActivity() {
    lateinit var binding: ActivityRequestStatusBinding
    var request: Request? = null
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()
    val viewModel: RequestStatusViewModel by inject()
    lateinit var serviceProviderAdapter: RequestSentToServiceProviderListAdapter
    var serviceProviderList: MutableList<RequestSentTo> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityRequestStatusBinding>(
            this,
            R.layout.activity_request_status
        )
        init()
        initObserver()
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        var intent = Intent()
        intent.putExtra("data", request)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun init() {
        binding.viewModel = viewModel
        binding.currentUserID = sharedPref.getLong(Constants.USER_ID).toInt()
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = getString(R.string.request_status)

        if (intent.getSerializableExtra("data") != null) {
            request = intent.getSerializableExtra("data") as Request?
            if (request != null) {
                binding.orderItem = request
                cancellationBannerStatusText()
                setOrderDateTime()
                viewModel.receiverID.set(request?.getProviderId().toString())
                viewModel.requestID.set(request?.getId().toString())
                setAdapterServiceProviders()
                viewModel.getRequestDetails()
            }
        }

//        tvReqSentTo.setOnClickListener {
//            val intent = Intent(this, RequestOffersActivity::class.java)
//            intent.putExtra("data", request)
//            startActivityForResult(intent, Constants.SELECT_OFFER_REQ)
//        }
    }

    private fun setOrderDateTime() {
        if (request?.getServiceTimeFrom() != null) {
            llDeliveryDate.visibility = View.VISIBLE
            tvValue5.text = request?.getOrderServiceDateTimeFormatted()
        } else if (request?.getDeliverTimeFrom() != null) {
            llDeliveryDate.visibility = View.VISIBLE
            tvValue5.text = request?.getOrderDeliveryDateTimeFormatted()
        } else {
            llDeliveryDate.visibility = View.GONE
            tvValue5.text = request?.getOrderDateTimeFormatted()
        }
    }

    fun initObserver() {
        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.hideProgressBar()
                commonFunctions.showProgressBar(this, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.wantToCancelClickObserver.observe(this, Observer {
            if (it) {
                showWantToCancelDialog()
            }
        })

        viewModel.reorderClickObserver.observe(this, Observer {
            if (it) {
                var intent: Intent = when {
                    request?.getRequestType() == Constants.REQUEST_TYPE_STORE -> {
                        Intent(this, NewRequestStore::class.java)
                    }
                    request?.getRequestType() == Constants.REQUEST_TYPE_SERVICE -> {
                        Intent(this, ServicesListingActivity::class.java)
                    }
                    else -> {
                        Intent(this, CourierGuysListing::class.java)
                    }
                }

                if (request?.getRequestType() == Constants.REQUEST_TYPE_STORE) {

                    intent.putExtra("latitute", request?.getPickupLatitude()!!.toDouble())
                    intent.putExtra("longitute", request?.getPickupLongitude()!!.toDouble())
                    intent.putExtra("location", request?.getPickupAddress())
                    intent.putExtra("title", request?.getName())
                    intent.putExtra("info", request?.getOrderInfo())
                    intent.putExtra("note", request?.getSpecialNote())
                    intent.putExtra("from", "")
                }

                intent.putExtra("data", request)
                intent.putExtra("from", Constants.ORDER_STATUS_REQ_REORDER)
                startActivity(intent)
            }
        })

        viewModel.viewDirectionClickObserver.observe(this, Observer {
            if (it) {
                val intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "geo:" + request?.getPickupLatitude() + "," + request?.getPickupLongitude() + "?q=" + request?.getDropLatitude() +
                                "," + request?.getDropLongitude() + "(" + request?.getUser()?.name + ")"
                    )
                )
                startActivity(intent)
            }
        })

        viewModel.chatClickObserver.observe(this, Observer {
            if (it) {
                var intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("id", request?.getProvider()?.id.toString())
                intent.putExtra("name", request?.getProvider()?.name)
                intent.putExtra("image", request?.getProvider()?.image)
                intent.putExtra("from", "request")
                startActivity(intent)
            }
        })

        viewModel.rateServiceProviderClickObserver.observe(this, Observer {
            if (it) {
                showRatingDialog()
            }
        })

        viewModel.getRateResponse.observe(this, Observer {
            Log.e("rateResponse", "" + it)
            var pojo = Gson().fromJson(it.toString(), CancellationReasonsPojo::class.java)
            if (pojo.status != null && pojo.status!!) {
                if (pojo.request != null) {
                    intent.putExtra("data", pojo.request)
                    request = pojo.request
                    binding.orderItem = request
                    cancellationBannerStatusText()
                    setOrderDateTime()

                    serviceProviderList.clear()
                    if (request?.getRequestSentTo() != null) {
                        serviceProviderList.addAll(request?.getRequestSentTo() as MutableList<RequestSentTo>)
                        serviceProviderAdapter.submitList(serviceProviderList)
                        serviceProviderAdapter.notifyDataSetChanged()
                    }

                    if (serviceProviderList.size > 0 && request?.getProviderId() == null) {
                        tvReqSentTo.visibility = View.VISIBLE
                    } else {
                        tvReqSentTo.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun cancellationBannerStatusText() {
        if (sharedPref.getLong(Constants.USER_ID).toInt() == request?.getCancelledById()) {
            tvStatusCanceledBanner.text = resources.getString(R.string.cancelled_by_you)
        } else {
            tvStatusCanceledBanner.text =
                resources.getString(R.string.cancelled_by_provider)
        }
    }

    private fun setAdapterServiceProviders() {
        serviceProviderAdapter = RequestSentToServiceProviderListAdapter(this, serviceProviderList)
        binding.rvRequestSendTo.adapter = serviceProviderAdapter

        binding.rvRequestSendTo.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider
                )
            )
        )
    }

    private fun showWantToCancelDialog() {
        val commonDialog = Dialog(this)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_cancel, null, false)
        var tvNo = vv.findViewById<TextView>(R.id.tvNo)
        var tvYes = vv.findViewById<TextView>(R.id.tvYes)
        var ivCancel = vv.findViewById<ImageView>(R.id.ivCancel)
        var rlRoot = vv.findViewById<RelativeLayout>(R.id.rlRoot)

        ivCancel.setOnClickListener {
            commonDialog.dismiss()
        }

        tvYes.setOnClickListener {
            val intent = Intent(this, CancelOrderRequestActivity::class.java)
            intent.putExtra("data", request)
            startActivityForResult(intent, Constants.CANCEL_REQ)
            commonDialog.dismiss()
        }

        tvNo.setOnClickListener {
            commonDialog.dismiss()
        }

        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        commonDialog.setCancelable(false)
        commonDialog.show()
    }

    private fun showRatingDialog() {
        val commonDialog = Dialog(this)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_delivered, null, false)
        var llSuccessBanner = vv.findViewById<LinearLayout>(R.id.llSuccessBanner)
        var llCancelledBanner = vv.findViewById<LinearLayout>(R.id.llCancelledBanner)
        var tvSubmit = vv.findViewById<TextView>(R.id.tvSubmit)
        var tvName = vv.findViewById<TextView>(R.id.tvName)
        var tvPrice = vv.findViewById<TextView>(R.id.tvPrice)
        var tvUserName = vv.findViewById<TextView>(R.id.tvUserName)
        var tvRatingStatus = vv.findViewById<TextView>(R.id.tvRatingStatus)
        var circleImageView = vv.findViewById<CircleImageView>(R.id.circleImageView)
        var ratingBar = vv.findViewById<RatingBar>(R.id.ratingBar)
        var rlRoot = vv.findViewById<RelativeLayout>(R.id.rlRoot)
//        bg_light_pink_tl_bl_br_round_30
        tvUserName.text = request?.getProvider()!!.name
        tvName.text = request?.getTitle()

        tvPrice.text = request?.getChargesInCurrency()
        if (request?.getProvider() != null && request?.getProvider()?.getOfferInCurrency() != null)
            tvPrice.text = request?.getProvider()?.getOfferInCurrency()

        Picasso.get()
            .load(request?.getProvider()!!.image)
            .placeholder(R.drawable.ic_user_placeholder)
            .error(R.drawable.icn_no_image)
            .into(circleImageView)

        if (request?.getRequestStatus() == "5") {
            llSuccessBanner.visibility = View.VISIBLE
            tvSubmit.setBackgroundResource(R.drawable.bg_bluelight_tl_tr_br_round_30)
        } else {
            llCancelledBanner.visibility = View.VISIBLE
            tvSubmit.setBackgroundResource(R.drawable.bg_light_pink_tl_bl_br_round_30)
        }

        tvSubmit.setOnClickListener {
            if (ratingBar.rating.roundToInt() != 0) {
                viewModel.rateUser(ratingBar.rating.toString(), request?.getProviderId().toString())
                commonDialog.dismiss()
            } else {
                commonFunctions.showFeedbackMessage(
                    rlRoot,
                    getString(R.string.rate_user_validation)
                )
            }
        }

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, b ->
            if (rating <= 1.0f) {
                tvRatingStatus.text = getString(R.string.poor)
            } else if (rating <= 2.0f) {
                tvRatingStatus.text = getString(R.string.average)
            } else if (rating <= 3.0f) {
                tvRatingStatus.text = getString(R.string.above_average)
            } else if (rating <= 4.0f) {
                tvRatingStatus.text = getString(R.string.good)
            } else if (rating <= 5.0f) {
                tvRatingStatus.text = getString(R.string.excellent)
            }
        }

        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        commonDialog.setCancelable(true)
        commonDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CANCEL_REQ && resultCode == Activity.RESULT_OK) {
            if (data?.getSerializableExtra("data") != null) {
                request = data.getSerializableExtra("data") as Request
                binding.orderItem = request
                setOrderDateTime()
                cancellationBannerStatusText()
            }
        }
    }
}
