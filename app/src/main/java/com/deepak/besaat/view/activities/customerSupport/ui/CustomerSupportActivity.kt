package com.deepak.besaat.view.activities.customerSupport.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityCustomerSupportBinding
import com.deepak.besaat.model.upcomingTripModel.upcomingTripModel
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.DateFunctions
import com.deepak.besaat.utils.FlagCountry
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.customerSupport.viewModel.CustomerSupportViewModel
import kotlinx.android.synthetic.main.activity_customer_support.*
import org.koin.android.ext.android.inject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CustomerSupportActivity : BaseActivity() {
    lateinit var binding: ActivityCustomerSupportBinding
    val viewModel: CustomerSupportViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val sharedPref: SharedPref by inject()
    var tripData: upcomingTripModel? = null
    var from: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_support)
        initDataBinding()
        initObserver()
        init()
        click()
    }

    fun init() {
        tripData = intent.getSerializableExtra("data") as upcomingTripModel?
        from = intent.getStringExtra("from")
        if (tripData != null) {
            viewModel.id.set(tripData?.id.toString())
            if (from != null)
                viewModel.type.set(from)

            flagDeparture.setImageResource(
                FlagCountry.getFlagDrawableResId(
                    tripData?.departureCountry?.toLowerCase()
                )
            )
            flagArrival.setImageResource(FlagCountry.getFlagDrawableResId(tripData?.arrivalCountry?.toLowerCase()))
            textDeparture.text = tripData?.departureCountry
            textArrival.text = tripData?.arrivalCountry
            tripId.text = tripData?.id.toString()

            if (tripData?.imageUrl != null && !tripData?.imageUrl.equals("")) {
                imageBottom.visibility = View.VISIBLE
                Glide.with(this)
                    .load(tripData?.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            imageBottom.visibility = View.GONE
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
                    .into(imageBottom)
            } else {
                imageBottom.visibility = View.GONE
            }

            ////////////////// date time/////////////////////
            var dateCreator: List<String>? = null
            var dateCreator2: List<String>? = null

            var str = tripData?.updatedDate
            var str1 = tripData?.createdDate
            //   var str2=tripList.get(0).arrivalCountryl
            dateCreator = str?.split(("\\s+").toRegex())
            dateCreator2 = str1?.split(("\\s+").toRegex())

            var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd");
            var date: Date? = null
            var date2: Date? = null
            try {
                date = dateFormat.parse(dateCreator2?.get(0))
                date2 = dateFormat.parse(dateCreator?.get(0))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            var dateFormat2: DateFormat = SimpleDateFormat("MMM d, yyyy")
            var dateArrival = dateFormat2.format(date)
            var dateDeparture = dateFormat2.format(date2)
            arrivalDate.text = dateArrival
            arrivalTime.text = dateCreator2?.get(1)
            departureDate.text = dateDeparture
            departureTime.text = dateCreator?.get(1)
        }
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_customer_support
        )

        binding.customerSuppertModel = viewModel
    }

    private fun click() {
        ivClose.setOnClickListener {
            onBackPressed()
        }

        textViewEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            val aEmailList = arrayOf("abc@gmail.com")
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList)
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello")
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
            emailIntent.type = "plain/text"
            startActivity(Intent.createChooser(emailIntent, "Send your email in:"))
        }

        textViewCall.setOnClickListener {
            checkPermission()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun initObserver() {
        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

        viewModel.progressBar.observe(this, Observer {
            if (it) {
                commonFunctions.showProgressBar(
                    this@CustomerSupportActivity,
                    getString(R.string.loading)
                )
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.onSuccessfullySubmit.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it.message!!)
            if (it.status!!) {
                onBackPressed()
            }
        })
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42)
            }
        } else {
            // Permission has already been granted
            callPhone()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 42) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone()
            } else {
                // permission denied, boo! Disable the
                // functionality
            }
            return
        }
    }

    private fun callPhone(){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "(899) 079-0707"))
        startActivity(intent)
    }
}
