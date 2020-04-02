package com.deepak.besaat.view.activities.requestOffers.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityRequestOffersBinding
import com.deepak.besaat.model.myOrdersModel.Offer
import com.deepak.besaat.model.myOrdersModel.Request
import com.deepak.besaat.model.myOrdersModel.RequestOfferAcceptPojo
import com.deepak.besaat.model.myOrdersModel.RequestOffersPojo
import com.deepak.besaat.network.NetworkConstants
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.DividerItemDecoration
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.requestOffers.adapter.RequestOffersListAdapter
import com.deepak.besaat.view.activities.requestOffers.interfaces.IOfferClick
import com.deepak.besaat.viewModel.MyOrdersOffersViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_offers.*
import org.koin.android.ext.android.inject

class RequestOffersActivity : BaseActivity(), IOfferClick {

    lateinit var binding: ActivityRequestOffersBinding
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()
    val viewModel: MyOrdersOffersViewModel by inject()
    private var offersList: MutableList<Offer> = ArrayList()
    var request: Request? = null

    private lateinit var requestOffersAdapter: RequestOffersListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_offers)
        setAdapter()
        init()
        initObserver()
    }

    fun init() {
        binding.viewModel = viewModel
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = String.format(getString(R.string.offers), 0)

        if (intent.getSerializableExtra("data") != null) {
            request = intent.getSerializableExtra("data") as Request?
            if (request != null) {
                viewModel.requestID.value = request!!.getId().toString()
                viewModel.getReqOffers(NetworkConstants.MY_OFFERS)
            }
        }
    }

    fun setAdapter() {
        requestOffersAdapter = RequestOffersListAdapter(this, offersList)
        binding.rvOffersList.adapter = requestOffersAdapter
        requestOffersAdapter.attachOffersClicks(this)

        binding.rvOffersList.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider
                )
            )
        )
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

        viewModel.getResponse.observe(this, Observer {
            Log.e("acceptData", "" + it)
            var pojo = Gson().fromJson(it.toString(), RequestOfferAcceptPojo::class.java)
            if (pojo.getStatus() != null && pojo.getStatus()!!) {
                if (pojo.getRequest() != null) {
                    var intent = Intent()
                    intent.putExtra("data", pojo.getRequest())
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        })

        viewModel.successfullyGetOffers.observe(this, Observer {
            Log.e("offerData", "" + it)
            var pojo = Gson().fromJson(it.toString(), RequestOffersPojo::class.java)
            if (pojo.getStatus() != null && pojo.getStatus()!!) {
                if (pojo.getOffers() != null) {
                    textViewTitleName.text =
                        String.format(getString(R.string.offers), pojo.getOffers()!!.size)
                    offersList.addAll(pojo.getOffers() as MutableList<Offer>)
                }
            } else if (pojo.getMessage() != null) {
                commonFunctions.showFeedbackMessage(rootLayout, "" + pojo.getMessage())
            } else {
                commonFunctions.showFeedbackMessage(rootLayout, "Something went wrong!")
            }
            requestOffersAdapter.submitList(offersList)
            requestOffersAdapter.notifyDataSetChanged()

            if (offersList.isEmpty()) {
                tv_no_data_found.visibility = View.VISIBLE
            } else {
                tv_no_data_found.visibility = View.GONE
            }
        })
    }

    override fun onOfferAcceptClick(position: Int) {
        viewModel.acceptOffer(
            offersList[position].getId().toString(),
            offersList[position].getTypeId().toString()
        )
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
