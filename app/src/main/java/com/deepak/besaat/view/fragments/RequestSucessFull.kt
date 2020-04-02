package com.deepak.besaat.view.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat.finishAffinity

import com.deepak.besaat.R
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.activities.paymentDetailsStore.PaymentDetailsStoreActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RequestSucessFull : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var fairAmount:String?=null
    var orderIdd:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View=inflater.inflate(R.layout.fragment_request_sucess_full, container, false)
        var paymentOrder=view.findViewById<TextView>(R.id.orderPayment)
        var textBackToHome=view.findViewById<TextView>(R.id.textViewContinue)
        var orderId=view.findViewById<TextView>(R.id.orderId)
        textBackToHome.setOnClickListener(object:View.OnClickListener{
            override fun onClick(view: View?) {
                startActivity(Intent(activity, HomeActivity::class.java))
                activity?.let { finishAffinity(it) }
            }
        })
        activity?.resources?.getColor(R.color.BlueLight)?.let {
            PaymentDetailsStoreActivity.tooConstraiin.setBackgroundColor(
                it
            )
        }
        paymentOrder.text=fairAmount+" "
        orderId.text= orderIdd
        BaseActivity.textViewTitleName.setText(getString(R.string.confirmation))
        BaseActivity.imageViewbackArrow.visibility=View.GONE
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(fairAmount: String, param2: String) :RequestSucessFull

        {
         var fragment =RequestSucessFull()
            fragment.fairAmount = fairAmount
            fragment.orderIdd=param2
            return fragment
        }
           /* RequestSucessFull().apply {
               // arguments = Bundle().apply {
                 //   putString(ARG_PARAM1, param1)
                  //  putString(ARG_PARAM2, param2)
                }
            }*/
    }
}
