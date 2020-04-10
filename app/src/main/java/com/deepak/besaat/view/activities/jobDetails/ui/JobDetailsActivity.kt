package com.deepak.besaat.view.activities.jobDetails.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityJobDetailsBinding
import com.deepak.besaat.model.cancellationReasons.CancellationReasonsPojo
import com.deepak.besaat.model.myOrdersModel.Request
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.cancelOrderRequest.ui.CancelOrderRequestActivity
import com.deepak.besaat.view.activities.chat.ui.ChatActivity
import com.deepak.besaat.viewModel.JobDetailsViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_job_details.*
import org.koin.android.ext.android.inject
import java.util.*

class JobDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityJobDetailsBinding
    var request: Request? = null
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()
    val viewModel: JobDetailsViewModel by inject()
    var jobType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityJobDetailsBinding>(
            this,
            R.layout.activity_job_details
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
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = getString(R.string.job_details)

        if (intent.getSerializableExtra("data") != null) {
            request = intent.getSerializableExtra("data") as Request?
            if (request != null) {
                binding.orderItem = request
                viewModel.receiverID.set(request?.getProviderId().toString())
                viewModel.requestID.set(request?.getId().toString())
                setOrderDateTime()
            }
        }

        jobType = intent.getStringExtra("jobType")

        if (jobType == Constants.REQUEST_TYPE_SERVICE) {
//            llTime.visibility = View.GONE
//            llDate.visibility = View.GONE

        }
    }

    private fun setOrderDateTime() {
        if (request?.getServiceTimeFrom() != null) {
            tvValue2.text = request?.getOrderServiceDateOnlyFormatted()
            tvValue3.text = request?.getOrderServiceTimeOnlyFormatted()
        } else if (request?.getDeliverTimeFrom() != null) {
            tvValue2.text = request?.getOrderDeliveryDateOnlyFormatted()
            tvValue3.text = request?.getOrderDeliveryTimeOnlyFormatted()
        } else {
            tvValue2.text = request?.getOrderDateOnlyFormatted()
            tvValue3.text = request?.getOrderTimeOnlyFormatted()
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

        viewModel.changeStatusClickObserver.observe(this, Observer {
            if (it) {
                showChangeStatusDialog()
            }
        })

        viewModel.makeOfferClickObserver.observe(this, Observer {
            if (it) {
                showMakeOfferDialog()
            }
        })

        viewModel.acceptClickObserver.observe(this, Observer {
            if (it) {
                //accept  Api
                viewModel.acceptOrRejectOrMakeOfferJob(
                    request?.getId().toString(),
                    request?.getCharges().toString(),
                    Constants.JOB_ACCEPT
                )
            }
        })

        viewModel.rejectClickObserver.observe(this, Observer {
            if (it) {
                // rejectAPi
                viewModel.acceptOrRejectOrMakeOfferJob(
                    request?.getId().toString(),
                    request?.getCharges().toString(),
                    Constants.JOB_REJECT
                )
            }
        })

        viewModel.chatClickObserver.observe(this, Observer {
            if (it) {
                // chat activity
                var intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("id", request?.getUser()?.id.toString())
                intent.putExtra("name", request?.getUser()?.name)
                intent.putExtra("image", request?.getUser()?.image)
                intent.putExtra("from", "job")
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

        viewModel.getResponse.observe(this, Observer {
            Log.e("response", "" + it)
            var pojo = Gson().fromJson(it.toString(), CancellationReasonsPojo::class.java)
            if (pojo.status != null && pojo.status!!) {
                if (pojo.request != null) {
                    intent.putExtra("data", pojo.request)
                    request = pojo.request
                    binding.orderItem = request
                    setOrderDateTime()
//                    viewModel.getRequestDetails()
                }
            }
            if (pojo.message != null && (pojo.message!!.contains(
                    "Job reject",
                    true
                ) || pojo.message!!.contains("job accept", true))
            ) {
                var intent = Intent()
                intent.putExtra("data", request)
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
        })

        viewModel.getRequestResponse.observe(this, Observer {
            Log.e("requestResponse", "" + it)
            var pojo = Gson().fromJson(it.toString(), CancellationReasonsPojo::class.java)
            if (pojo.status != null && pojo.status!!) {
                if (pojo.request != null) {
                    intent.putExtra("data", pojo.request)
                    request = pojo.request
                    binding.orderItem = request
                    setOrderDateTime()
                }
            }
        })

        viewModel.getRequestDetails()
    }

    private fun showChangeStatusDialog() {
        val commonDialog = Dialog(this)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_change_status, null, false)
        var tvSubmit = vv.findViewById<TextView>(R.id.tvSubmit)
        var rlRoot = vv.findViewById<ScrollView>(R.id.rlRoot)
        var radioGroup = vv.findViewById<RadioGroup>(R.id.radioGroup)
        var radio_pickup = vv.findViewById<RadioButton>(R.id.radio_pickup)
        var radio_delay = vv.findViewById<RadioButton>(R.id.radio_delay)
        var radio_dropped = vv.findViewById<RadioButton>(R.id.radio_dropped)
        var radio_start = vv.findViewById<RadioButton>(R.id.radio_start)
        var radio_complete = vv.findViewById<RadioButton>(R.id.radio_complete)
        var status: String? = null

        if (jobType == Constants.REQUEST_TYPE_SERVICE) {
            radio_pickup.visibility = View.GONE
            radio_delay.visibility = View.GONE
            radio_dropped.visibility = View.GONE
            if (request?.getWorkingStatus() != null) {
                if (request?.getWorkingStatus() == "0") {
                    radio_start.isEnabled = false
                }
                if (request?.getWorkingStatus() == "1") {
                    radio_complete.isEnabled = false
                }
            }
        } else {
            radio_start.visibility = View.GONE
            radio_complete.visibility = View.GONE
            if (request?.getWorkingStatus() != null) {
                if (request?.getWorkingStatus() == "0") {
                    radio_pickup.isEnabled = false
                }
                if (request?.getWorkingStatus() == "1") {
                    radio_delay.isEnabled = false
                }
                if (request?.getWorkingStatus() == "2") {
                    radio_dropped.isEnabled = false
                }
            }
        }

        radioGroup.setOnCheckedChangeListener { group, viewID ->
            when (viewID) {
                R.id.radio_pickup -> {
                    status = Constants.CHANGE_STATUS_PICKUP
                }
                R.id.radio_delay -> {
                    status = Constants.CHANGE_STATUS_DELAYED
                }
                R.id.radio_dropped -> {
                    status = Constants.CHANGE_STATUS_DROPPED
                }
                R.id.radio_start -> {
                    status = Constants.CHANGE_STATUS_START
                }
                R.id.radio_complete -> {
                    status = Constants.CHANGE_STATUS_COMPLETE
                }
            }
        }

        tvSubmit.setOnClickListener {
            if (status != null) {
                viewModel.changeOrderStatus(jobType!!, status!!)
                commonDialog.dismiss()
            } else {
                commonFunctions.showFeedbackMessage(
                    rlRoot,
                    getString(R.string.select_status_of_job)
                )
            }
        }

        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        commonDialog.window!!.setGravity(Gravity.BOTTOM)
        commonDialog.setCancelable(true)
        commonDialog.show()
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

    private fun showMakeOfferDialog() {
        val commonDialog = Dialog(this)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_make_offer, null, false)
        var tvSubmit = vv.findViewById<TextView>(R.id.tvSubmit)
        var tvOfferDetails = vv.findViewById<TextView>(R.id.tvOfferDetails)
        var ivCancel = vv.findViewById<ImageView>(R.id.ivCancel)
        var edOfferPrice = vv.findViewById<EditText>(R.id.edOfferPrice)
        var rlRoot = vv.findViewById<RelativeLayout>(R.id.rlRoot)

        tvOfferDetails.text = String.format(
            getString(R.string.offer_price_details),
            request?.getChargesInCurrency(), request?.getChargesInCurrency()
        )

        ivCancel.setOnClickListener {
            commonDialog.dismiss()
        }

        tvSubmit.setOnClickListener {
            if (edOfferPrice.text.toString().trim() != "") {
                if (edOfferPrice.text.toString().trim().toFloat() > (request?.getCharges()?.toFloat()!! + request?.getCharges()?.toFloat()!! * 0.2f)) {
                    commonFunctions.showFeedbackMessage(
                        rlRoot,
                        String.format(
                            getString(R.string.please_enter_your_offer_price_less_than),
                            request?.getChargesInCurrency()
                        )

                    )
                } else if (edOfferPrice.text.toString().trim().toFloat() < (request?.getCharges()?.toFloat()!! - request?.getCharges()?.toFloat()!! * 0.1f)) {
                    commonFunctions.showFeedbackMessage(
                        rlRoot,
                        String.format(
                            getString(R.string.please_enter_your_offer_price_greater_than),
                            request?.getChargesInCurrency()
                        )
                    )
                } else {
                    viewModel.acceptOrRejectOrMakeOfferJob(
                        request?.getId().toString(),
                        edOfferPrice.text.toString().trim(),
                        Constants.JOB_ACCEPT
                    )
                    commonDialog.dismiss()
                }
            } else {
                commonFunctions.showFeedbackMessage(
                    rlRoot,
                    getString(R.string.please_enter_your_offer_price)
                )
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CANCEL_REQ && resultCode == Activity.RESULT_OK) {
            if (data?.getSerializableExtra("data") != null) {
                request = data.getSerializableExtra("data") as Request
                binding.orderItem = request
            }
        }
    }
}
