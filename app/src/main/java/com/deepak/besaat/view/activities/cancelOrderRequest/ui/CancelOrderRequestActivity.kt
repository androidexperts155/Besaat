package com.deepak.besaat.view.activities.cancelOrderRequest.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityCancelOrderRequestBinding
import com.deepak.besaat.model.cancellationReasons.CancellationReasonsPojo
import com.deepak.besaat.model.cancellationReasons.Reason
import com.deepak.besaat.model.myOrdersModel.Request
import com.deepak.besaat.network.NetworkConstants
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.cancelOrderRequest.adapters.ReasonsListAdapter
import com.deepak.besaat.viewModel.CancelOrderRequestViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cancel_order_request.*
import org.koin.android.ext.android.inject

class CancelOrderRequestActivity : BaseActivity(), ReasonsListAdapter.OnReasonSelectClick {
    lateinit var binding: ActivityCancelOrderRequestBinding
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()
    val viewModel: CancelOrderRequestViewModel by inject()
    var request: Request? = null
    private var reasonsList: MutableList<Reason> = ArrayList()
    lateinit var reasonsListAdapter: ReasonsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_order_request)
        init()
        initObserver()
    }

    fun init() {
        binding.viewModel = viewModel
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = getString(R.string.reason_for_cancellation)
        viewModel.yesChecked.set(false)
        if (intent.getSerializableExtra("data") != null) {
            request = intent.getSerializableExtra("data") as Request?
            if (request != null) {
                viewModel.requestID.set(request!!.getId().toString())
                binding.orderItem = request
            }
        }

        viewModel.getCancellationReasons()

        reasonsListAdapter = ReasonsListAdapter(this, reasonsList)
        reasonsListAdapter.onClickListener(this)
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

        viewModel.selectReasonClickObserver.observe(this, Observer {
            if (it) {
                showCancellationReasonsPopup()
            }
        })

        viewModel.successfullyGetCancellationReasons.observe(this, Observer {
            Log.e("cancellationReasons", "" + it)
            var pojo = Gson().fromJson(it.toString(), CancellationReasonsPojo::class.java)
            if (pojo.status != null && pojo.status!!) {
                if (pojo.reasons != null) {
                    reasonsList.addAll(pojo.reasons!!)
                    reasonsListAdapter.submitList(reasonsList)
                    reasonsListAdapter.notifyDataSetChanged()
                }
            }
        })

        viewModel.getResponse.observe(this, Observer {
            Log.e("cancelDone", "" + it)
            var pojo = Gson().fromJson(it.toString(), CancellationReasonsPojo::class.java)
            if (pojo.status != null && pojo.status!!) {
                var intent = Intent()
                intent.putExtra("data", pojo.request)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })
    }

    private fun showCancellationReasonsPopup() {
        val commonDialog = Dialog(this)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_cancellation_reasons, null, false)
        val rvDeliveryTypes = vv.findViewById<RecyclerView>(R.id.rvReasons)
        rvDeliveryTypes.adapter = reasonsListAdapter
        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        commonDialog.setCancelable(true)
//        commonDialog.window!!.attributes.windowAnimations = R.style.DialogTheme
        commonDialog.show()
    }

    override fun onReasonSelected(position: Int, name: String) {
        for (i in 0 until reasonsList.size) {
            reasonsList[i].check = false
        }
        reasonsList[position].check = true
        viewModel.reason.set(reasonsList[position].reason)
        viewModel.reasonID.set(reasonsList[position].id.toString())
        reasonsListAdapter.submitList(reasonsList)
        reasonsListAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
