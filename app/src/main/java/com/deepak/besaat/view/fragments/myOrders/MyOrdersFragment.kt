package com.deepak.besaat.view.fragments.myOrders


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.FragmentMyOrdersBinding
import com.deepak.besaat.model.myOrdersModel.JobStatusChangePojo
import com.deepak.besaat.model.myOrdersModel.MyOrdersPojo
import com.deepak.besaat.model.myOrdersModel.Request
import com.deepak.besaat.network.NetworkConstants
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.cancelOrderRequest.ui.CancelOrderRequestActivity
import com.deepak.besaat.view.activities.jobDetails.ui.JobDetailsActivity
import com.deepak.besaat.view.activities.requestOffers.ui.RequestOffersActivity
import com.deepak.besaat.view.activities.requestStatus.ui.RequestStatusActivity
import com.deepak.besaat.view.fragments.BaseFragment
import com.deepak.besaat.view.fragments.myOrders.adapters.OrdersListAdapter
import com.deepak.besaat.view.fragments.myOrders.interfaces.IOrderItemClick
import com.deepak.besaat.viewModel.MyOrdersViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_my_orders.*
import org.koin.android.ext.android.inject

class MyOrdersFragment : BaseFragment(), IOrderItemClick {
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()
    private var ordersList: MutableList<Request> = ArrayList()
    val viewModel: MyOrdersViewModel by inject()
    lateinit var binding: FragmentMyOrdersBinding
    lateinit var adapter: OrdersListAdapter
    var clickedPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentMyOrdersBinding>(
            inflater,
            R.layout.fragment_my_orders,
            container,
            false
        )
        binding.viewModel = viewModel
        return binding.root
//        return inflater.inflate(R.layout.fragment_my_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initObserver()
    }

    fun init() {
        var role = sharedPref.getString(Constants.ROLE)
        if (role == Constants.ROLE_BUYER) {
            radioButtonMyJobs.visibility = View.GONE
        }
        viewModel.status.value = Constants.FILTER_ORDER_STATUS_ALL
        viewModel.requestType.value = Constants.REQUEST_TYPE_STORE
        viewModel.orderType.value = NetworkConstants.MY_REQUESTS
        viewModel.getOrders()
        setAdapter()
    }

    private fun setAdapter() {
        adapter = OrdersListAdapter(
            activity!!,
            viewModel.orderType.value!!,
            viewModel.requestType.value!!,
            sharedPref.getLong(Constants.USER_ID).toInt()
        )
        binding.recyclerViewOrders.adapter = adapter
        adapter.attachOrderItemClick(this)
        adapter.submitList(ordersList)
    }

    fun initObserver() {
        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.hideProgressBar()
                commonFunctions.showProgressBar(activity!!, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.feedBackMessage.observe(this, Observer {
            if (it != null)
                commonFunctions.showFeedbackMessage(rootLayout, it)
            else
                commonFunctions.showFeedbackMessage(rootLayout, "Something wrong.")
        })

        viewModel.filterClick.observe(this, Observer {
            if (it) {
                showFilterDialog()
            }
        })

        viewModel.successfullyGetOrders.observe(this, Observer {
            ordersList.clear()
            var pojo = Gson().fromJson(it, MyOrdersPojo::class.java)
            if (pojo.getStatus() != null && pojo.getStatus()!!) {
                ordersList.addAll(pojo.getRequests() as MutableList<Request>)
            }
            setAdapter()
        })

        viewModel.getResponse.observe(this, Observer {
            Log.e("response", "" + it)
            val pojo = Gson().fromJson(it, JobStatusChangePojo::class.java)
            if (pojo.getStatus() != null && pojo.getStatus()!!) {
                commonFunctions.showFeedbackMessage(rootLayout, pojo.getMessage()!!)
                if (pojo.getRequest() != null) {
                    ordersList[clickedPosition] = pojo.getRequest()!!
                }

                if (pojo.getMessage() != null && pojo.getMessage()!!.contains("Job reject", true)) {
                    ordersList.removeAt(clickedPosition)
                } else if (pojo.getMessage() != null && pojo.getMessage()!!.contains(
                        "job accept",
                        true
                    )
                ) {
                    ordersList.removeAt(clickedPosition)
                }

                adapter.submitList(ordersList)
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.orderType.observe(this, Observer {
            Log.e("OrderType", "" + viewModel.orderType.value)
        })

        viewModel.requestType.observe(this, Observer {
            Log.e("RequestType", "" + viewModel.requestType.value)
            when (viewModel.requestType.value) {
                Constants.REQUEST_TYPE_STORE -> {
                    rootLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.lightadeblue
                        )
                    )
                    activity!!.topBarExtra.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.lightadeblue
                        )
                    )
                    activity!!.toolBar1.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorDarkSkyBlue
                        )
                    )
                    activity!!.llTitleBarCorner.setBackgroundDrawable(resources.getDrawable(R.drawable.bd_voilet_bl_round_90))

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val window = activity!!.window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.statusBarColor = this.resources.getColor(R.color.colorDarkSkyBlue)
                    }
                }

                Constants.REQUEST_TYPE_SERVICE -> {
                    rootLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorYellow
                        )
                    )

                    activity!!.toolBar1.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorYellowDark
                        )
                    )
                    activity!!.topBarExtra.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorYellow
                        )
                    )
                    activity!!.llTitleBarCorner.setBackgroundDrawable(resources.getDrawable(R.drawable.bd_yellow_dark_bl_round_90))

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val window = activity!!.window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.statusBarColor = this.resources.getColor(R.color.colorYellowDark)
                    }
                }

                Constants.REQUEST_TYPE_COURIER -> {
                    rootLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorBlueO2
                        )
                    )

                    activity!!.topBarExtra.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorBlueO2
                        )
                    )
                    activity!!.toolBar1.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorBlueO
                        )
                    )
                    activity!!.llTitleBarCorner.setBackgroundDrawable(resources.getDrawable(R.drawable.bd_voilet_light_bl_round_90))

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val window = activity!!.window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.statusBarColor = this.resources.getColor(R.color.colorBlueO)
                    }
                }
            }
        })
    }

    private fun showFilterDialog() {
        val commonDialog = Dialog(activity!!)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_order_filter, null, false)
        var ivClose = vv.findViewById<ImageView>(R.id.imageCross2)
        var tvApply = vv.findViewById<TextView>(R.id.textViewContinue2)
        var tvReset = vv.findViewById<TextView>(R.id.textreset2)
        var radioGroup = vv.findViewById<RadioGroup>(R.id.radioGroup)

        radioGroup.setOnCheckedChangeListener { group, viewID ->
            when (viewID) {
                R.id.radio_all -> {
                    viewModel.status.value = Constants.FILTER_ORDER_STATUS_ALL
                }
                R.id.radio_in_progress -> {
                    viewModel.status.value = Constants.FILTER_ORDER_STATUS_IN_PROGRESS
                }
                R.id.radio_cancelled -> {
                    viewModel.status.value = Constants.FILTER_ORDER_STATUS_CANCELLED
                }
                R.id.radio_rejected -> {
                    viewModel.status.value = Constants.FILTER_ORDER_STATUS_REJECT
                }
                R.id.radio_delivered -> {
                    viewModel.status.value = Constants.FILTER_ORDER_STATUS_COMPLETED
                }
            }
        }

        tvApply.setOnClickListener {
            commonDialog.dismiss()
            viewModel.getOrders()
        }

        ivClose.setOnClickListener {
            commonDialog.dismiss()
        }

        tvReset.setOnClickListener {
            viewModel.status.value = Constants.FILTER_ORDER_STATUS_ALL
            radioGroup.check(R.id.radio_all)
            tvReset.visibility = View.GONE
//            commonDialog.dismiss()
            viewModel.getOrders()
        }

        when (viewModel.status.value) {
            Constants.FILTER_ORDER_STATUS_ALL -> {
                radioGroup.check(R.id.radio_all)
                tvReset.visibility = View.GONE
            }
            Constants.FILTER_ORDER_STATUS_IN_PROGRESS -> {
                radioGroup.check(R.id.radio_in_progress)
                tvReset.visibility = View.VISIBLE
            }
            Constants.FILTER_ORDER_STATUS_CANCELLED -> {
                radioGroup.check(R.id.radio_cancelled)
                tvReset.visibility = View.VISIBLE
            }
            Constants.FILTER_ORDER_STATUS_REJECT -> {
                radioGroup.check(R.id.radio_rejected)
                tvReset.visibility = View.VISIBLE
            }
            Constants.FILTER_ORDER_STATUS_COMPLETED -> {
                radioGroup.check(R.id.radio_delivered)
                tvReset.visibility = View.VISIBLE
            }
        }

        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.window!!.setGravity(Gravity.BOTTOM)
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        commonDialog.setCancelable(false)
        commonDialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MyOrdersFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.SELECT_OFFER_REQ && resultCode == Activity.RESULT_OK) {
            if (data?.getSerializableExtra("data") != null) {
                var request: Request = data.getSerializableExtra("data") as Request
                ordersList.set(clickedPosition, request)
                adapter.submitList(ordersList)
                adapter.notifyDataSetChanged()
            }
        }

        if (requestCode == Constants.CANCEL_REQ && resultCode == Activity.RESULT_OK) {
            if (data?.getSerializableExtra("data") != null) {
                var request: Request = data.getSerializableExtra("data") as Request
                ordersList[clickedPosition] = request
                adapter.submitList(ordersList)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onOrderItemClick(position: Int, type: Int, purpose: String, status: String) {
        clickedPosition = position
//        if (type == adapter.VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_DELIVERY && purpose == Constants.FILTER_ORDER_STATUS_VIEW && ordersList[position].getProvider() == null && ordersList[position].getRequestStatus() == Constants.ORDER_STATUS_PLACED) {
//            val intent = Intent(activity, RequestOffersActivity::class.java)
//            intent.putExtra("data", ordersList[position])
//            startActivityForResult(intent, Constants.SELECT_OFFER_REQ)
//        } else
        if (type == adapter.VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_DELIVERY && purpose == Constants.FILTER_ORDER_STATUS_VIEW) {
            if (ordersList[position].getRequestStatus() == "2") {
                val intent = Intent(activity, RequestOffersActivity::class.java)
                intent.putExtra("data", ordersList[position])
                startActivityForResult(intent, Constants.SELECT_OFFER_REQ)
            } else {
                val intent = Intent(activity, RequestStatusActivity::class.java)
                intent.putExtra("data", ordersList[position])
                startActivityForResult(intent, Constants.VIEW_REQ)
            }

        } else if (type == adapter.VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_SERVICE && purpose == Constants.FILTER_ORDER_STATUS_VIEW) {
            if (ordersList[position].getRequestStatus() == "2") {
                val intent = Intent(activity, RequestOffersActivity::class.java)
                intent.putExtra("data", ordersList[position])
                startActivityForResult(intent, Constants.SELECT_OFFER_REQ)
            } else {
                val intent = Intent(activity, RequestStatusActivity::class.java)
                intent.putExtra("data", ordersList[position])
                startActivityForResult(intent, Constants.VIEW_REQ)
            }
        } else if (type == adapter.VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_COURIER && purpose == Constants.FILTER_ORDER_STATUS_VIEW) {
            if (ordersList[position].getRequestStatus() == "2") {
                val intent = Intent(activity, RequestOffersActivity::class.java)
                intent.putExtra("data", ordersList[position])
                startActivityForResult(intent, Constants.SELECT_OFFER_REQ)
            } else {
                val intent = Intent(activity, RequestStatusActivity::class.java)
                intent.putExtra("data", ordersList[position])
                startActivityForResult(intent, Constants.VIEW_REQ)
            }
        } else if (type == adapter.VIEW_TYPE_JOB_SUB_VIEW_TYPE_DELIVERY && purpose == Constants.FILTER_ORDER_STATUS_VIEW) {
            val intent = Intent(activity, JobDetailsActivity::class.java)
            intent.putExtra("data", ordersList[position])
            intent.putExtra("jobType", Constants.REQUEST_TYPE_STORE)
            startActivityForResult(intent, Constants.VIEW_REQ)
        } else if ((type == adapter.VIEW_TYPE_JOB_SUB_VIEW_TYPE_SERVICE) && purpose == Constants.FILTER_ORDER_STATUS_VIEW) {
            val intent = Intent(activity, JobDetailsActivity::class.java)
            intent.putExtra("data", ordersList[position])
            intent.putExtra("jobType", Constants.REQUEST_TYPE_SERVICE)
            startActivityForResult(intent, Constants.VIEW_REQ)
        } else if ((type == adapter.VIEW_TYPE_JOB_SUB_VIEW_TYPE_COURIER) && purpose == Constants.FILTER_ORDER_STATUS_VIEW) {
            val intent = Intent(activity, JobDetailsActivity::class.java)
            intent.putExtra("data", ordersList[position])
            intent.putExtra("jobType", Constants.REQUEST_TYPE_COURIER)
            startActivityForResult(intent, Constants.VIEW_REQ)
        } else if (purpose == Constants.ORDER_STATUS_ACCEPT || purpose == Constants.ORDER_STATUS_REJECT) {
            viewModel.acceptOrRejectJob(
                ordersList[position].getId().toString(),
                ordersList[position].getCharges().toString(),
                status
            )
        } else if (purpose == Constants.ORDER_STATUS_REQ_CANCLLATION) {
            viewModel.acceptOrRejectCancelRequest(
                ordersList[position].getId().toString(),
                status
            )
        } else if (purpose == Constants.ORDER_STATUS_CANCELLED) {
            val intent = Intent(activity, CancelOrderRequestActivity::class.java)
            intent.putExtra("data", ordersList[position])
            startActivityForResult(intent, Constants.CANCEL_REQ)
        } else if (purpose == Constants.FILTER_ORDER_STATUS_REQ_VIEW_OFFERS) {
            val intent = Intent(activity, RequestOffersActivity::class.java)
            intent.putExtra("data", ordersList[position])
            startActivityForResult(intent, Constants.SELECT_OFFER_REQ)
        }
    }
}
