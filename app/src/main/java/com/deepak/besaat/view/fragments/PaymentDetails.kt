package com.deepak.besaat.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.deepak.besaat.R
import com.deepak.besaat.utils.FragmentFucntions
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.paymentDetailsStore.PaymentDetailsStoreActivity
import org.koin.android.ext.android.inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PaymentDetails : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var pickUp: String? = null
    var drop: String? = null
    var orderInfo: String? = null
    var specialInfo: String? = null
    var checkBool: Boolean = true
    var radiusValue: Any? = null
    var fair: String? = null
    var fairAmount: String? = null
    var orderId: String = ""
    val fragmentFucntions: FragmentFucntions by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_payment_details, container, false)
        var textView = view.findViewById<TextView>(R.id.textViewContinue)
        var imageView = view.findViewById<ImageView>(R.id.cash_image_check)
        var textRequest = view.findViewById<TextView>(R.id.textView455)
        var textPickUp = view.findViewById<TextView>(R.id.textView466)
        var textDrop = view.findViewById<TextView>(R.id.textView488)
        var distance = view.findViewById<TextView>(R.id.distance)
        var fairMoney = view.findViewById<TextView>(R.id.fareMoney)
        var fareText = view.findViewById<TextView>(R.id.fairAmount)
        //PaymentDetailsStoreActivity.appBarLayout.setBackgroundColor( android:background="@android:color/transparent")

        activity?.resources?.getColor(R.color.colorDarkSkyBlue)?.let {
            PaymentDetailsStoreActivity.tooConstraiin.setBackgroundColor(
                it
            )
        }

        //  var textPickUp=view.findViewById<TextView>(R.id.textView47)
        // var textDelivery=view.findViewById<TextView>(R.id.textView49)
        var distanceCovered = view.findViewById<TextView>(R.id.distance)



        textRequest.text = orderInfo.toString()
        textPickUp.text = pickUp.toString()
        textDrop.text = drop.toString()
        distance.text = radiusValue.toString().substring(0, 3) + " km"
        fairMoney.text = "$ $fair "

        if (fairAmount!=null)
        fareText.text = String.format("$ %.2f",fairAmount?.toDouble())
//        if (fairAmount.toString().length > 3) {
//            fareText.text = "$ " + fairAmount.toString().substring(0, 3) + " "

//        }

        textView.setOnClickListener {
            if (checkBool) {
                var requestSucessFull =
                    RequestSucessFull.newInstance(fareText.text.toString(), orderId)
                fragmentFucntions.replaceFragment(
                    activity,
                    requestSucessFull,
                    R.id.frameContainer
                )
            } else {
                Toast.makeText(activity, "Please Enable a payment Method", Toast.LENGTH_LONG)
                    .show()
            }
        }
        imageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (!checkBool) {
                    checkBool = true
                    imageView.setImageResource(R.drawable.checked_circle)
                } else {
                    checkBool = false
                    imageView.setImageResource(R.drawable.unchecked_circle)
                }
            }
        })
        BaseActivity.textViewTitleName.setText(getString(R.string.payment_method))
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(
            pickUp: String,
            drop: String,
            orderInfo: String,
            specialInfo: String,
            radiusValue: Any,
            fair: String,
            fairAmount: String,
            orderId: String
        ): PaymentDetails {
            var fragment = PaymentDetails()
            fragment.pickUp = pickUp
            fragment.drop = drop
            fragment.orderInfo = orderInfo
            fragment.specialInfo = specialInfo
            fragment.radiusValue = radiusValue
            fragment.fair = fair
            fragment.fairAmount = fairAmount
            fragment.orderId = orderId
            return fragment
        }
    }
}
