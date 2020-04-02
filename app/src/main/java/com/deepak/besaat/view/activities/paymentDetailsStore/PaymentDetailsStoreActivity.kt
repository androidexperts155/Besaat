package com.deepak.besaat.view.activities.paymentDetailsStore

import android.os.Bundle
import android.util.Log

import com.deepak.besaat.R
import com.deepak.besaat.utils.FragmentFucntions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.fragments.PaymentDetails
import com.google.android.gms.maps.model.LatLng
import org.koin.android.ext.android.inject
import java.text.DecimalFormat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.deepak.besaat.model.GetNewServiceRequest.NewServiceRequestPojo
import com.google.android.material.appbar.AppBarLayout

class PaymentDetailsStoreActivity : BaseActivity() {
    val fragmentFucntions: FragmentFucntions by inject()
    val sharedPref: SharedPref by inject()

    companion object {
        lateinit var appBarLayout: AppBarLayout
        lateinit var tooConstraiin: ConstraintLayout
    }

    //  var toolbar :Toolbar ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_details_store2)
        var toolbar: Toolbar = findViewById<Toolbar>(R.id.toolBar)
        appBarLayout = findViewById<AppBarLayout>(R.id.appBarLayout)
        tooConstraiin = findViewById<ConstraintLayout>(R.id.topConstrainn)
        super.customToolBarWithBack(this, toolbar)
        //   super.textViewTitleName.setText(getString(R.string.payment_method))
        var from: String = intent.getStringExtra("from") ?: ""
        var data: NewServiceRequestPojo.Social =
            intent.getSerializableExtra("data") as NewServiceRequestPojo.Social

        var pickUp = data.pickupAddress
        var drop = data.dropAddress
        var orderInfo = data.orderInfo
        var specialInfo = data.specialNote
        var pickLat = data.pickupLatitude
        var pickLong = data.pickupLongitude
        var droplat = data.dropLatitude
        var dropLong = data.dropLongitude
        var fair = intent.getStringExtra("fair") ?: ""
        var fairAmount: String = intent.getStringExtra("fairAmount") ?: ""
        var orderId = data.id


        var radiusValue = CalculationByDistance(
            LatLng(pickLat!!.toDouble(), pickLong!!.toDouble()),
            LatLng(droplat!!.toDouble(), dropLong!!.toDouble())
        )
        var paymentDetails = PaymentDetails.newInstance(
            pickUp!!,
            drop!!,
            orderInfo!!,
            specialInfo!!,
            radiusValue,
            fair,
            fairAmount,
            orderId.toString()
        )
        fragmentFucntions.replaceFragment(this, paymentDetails, R.id.frameContainer)
    }

    private fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Any {
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
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        Log.e("Radius Value", "radius valur return " + Radius * c)
        return Radius * c
    }
}
