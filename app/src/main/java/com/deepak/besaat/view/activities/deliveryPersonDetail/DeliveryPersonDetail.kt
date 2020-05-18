package com.deepak.besaat.view.activities.deliveryPersonDetail

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityDeliveryPersonDetailBinding
import com.deepak.besaat.model.GetUserDetailsModel.GetUserDetailsPojo
import com.deepak.besaat.view.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_delivery_person_detail.*
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.chat.ui.ChatActivity
import com.deepak.besaat.view.activities.newRequestCourier.NewRequestCourier
import com.deepak.besaat.view.activities.newRequestCourierOverSeas.NewRequestCourierOverseasActivity
import com.deepak.besaat.view.activities.newRequestService.NewRequestService
import com.deepak.besaat.viewModel.CourierUserDetailsViewModel
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject

class DeliveryPersonDetail : BaseActivity() {
    lateinit var binding: ActivityDeliveryPersonDetailBinding
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()
    val viewModel: CourierUserDetailsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_person_detail)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_person_detail)
        init()
    }

    private fun init() {
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = getString(R.string.profile)
        binding.viewModel = viewModel

        if (intent.getStringExtra("from") != null) {
            viewModel.from.set(intent.getStringExtra("from"))
        }

        viewModel.availableDays.set(intent.getStringExtra("availableDays"))
        viewModel.serviceTimeFrom.set(intent.getStringExtra("serviceTimeFrom"))
        viewModel.serviceTimeTo.set(intent.getStringExtra("serviceTimeTo"))

        viewModel.name.set(intent.getStringExtra("name"))
        viewModel.distance.set(intent.getStringExtra("distance"))
        viewModel.image.set(intent.getStringExtra("image"))
        viewModel.id.set(intent.getStringExtra("id"))
        viewModel.deliveryType.set(intent.getStringExtra("deliveryType"))

        viewModel.rating.set("0.0")// default
        viewModel.ratingBar.set(0.0f)// default
        viewModel.completedJobs.set("0")// default

        viewModel.providerLatitude.set(intent.getStringExtra("providerLat"))
        viewModel.providerLongitude.set(intent.getStringExtra("providerLng"))
        viewModel.providerAddress.set(intent.getStringExtra("providerAddress"))

        viewModel.latitude.set(sharedPref.getString(Constants.latitude))
        viewModel.longitude.set(sharedPref.getString(Constants.longitude))

        Picasso.get().load(viewModel.image.get()).fit()
            .placeholder(R.drawable.bd_blue_drawable)
            .error(R.drawable.icn_no_image)
            .into(circularImageView)

        initObserver()
        viewModel.getUserDetails()
//        binding.profileData.name = intent.getStringExtra("name")

    }

    fun initObserver() {
        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

        viewModel.successfullyGetUserDetails.observe(this, Observer {
            var pojo = Gson().fromJson(it.toString(), GetUserDetailsPojo::class.java)
            if (pojo.getStatus() != null && pojo.getStatus()!!) {
                commonFunctions.showFeedbackMessage(rootLayout, pojo.getMessage()!!)
                viewModel.services.set(pojo.getUser()?.services)
                viewModel.experienceYears.set(pojo.getUser()?.experienceYears)
                viewModel.rating.set(pojo.getUser()?.getRatingString())
                viewModel.ratingBar.set(pojo.getUser()?.getRatingFloat())
                viewModel.completedJobs.set(pojo.getUser()?.completedJobs)

                if (viewModel.from.get() != null && viewModel.from.get().toString().contains("service")) {
                    viewModel.services.set(pojo.getUser()?.services)
                    viewModel.services.set(viewModel.services.get()!!.replace(",", "/"))
                } else {
                    viewModel.courierStatus.set(intent.getStringExtra("courierStatus"))
                    if (viewModel.courierStatus.get() != null) {
                        if (viewModel.courierStatus.get() == "1") {
                            viewModel.services.set(Constants.COURIER_1)
                        } else if (viewModel.courierStatus.get() == "2") {
                            viewModel.services.set(Constants.COURIER_2)
                        } else if (viewModel.courierStatus.get() == "3") {
                            viewModel.services.set(Constants.COURIER_3)
                        }
                    }
                }

            } else {
                commonFunctions.showFeedbackMessage(rootLayout, "Something went wrong!")
            }
        })

        viewModel.onRequestClick.observe(this, Observer {
            if (it) {
                var nextIntent: Intent
                if (viewModel.from.get() != null && viewModel.from.get().toString().contains("service")) {
                    nextIntent = Intent(this, NewRequestService::class.java)
                    nextIntent.putExtra("availableDays", viewModel.availableDays.get())
                    nextIntent.putExtra("serviceTimeFrom", viewModel.serviceTimeFrom.get())
                    nextIntent.putExtra("serviceTimeTo", viewModel.serviceTimeTo.get())
                } else if (viewModel.from.get() != null && viewModel.from.get().toString().contains(
                        "CourierOverSeas"
                    )
                ) {
                    nextIntent = Intent(this, NewRequestCourierOverseasActivity::class.java)
                    nextIntent.putExtra("pickupCountry", intent.getStringExtra("pickupCountry"))
                    nextIntent.putExtra("dropCountry", intent.getStringExtra("dropCountry"))
                } else {
                    nextIntent = Intent(this, NewRequestCourier::class.java)
                }
                nextIntent.putExtra("id", viewModel.id.get())
                nextIntent.putExtra("name", viewModel.name.get())
                nextIntent.putExtra("distance", viewModel.distance.get())
                nextIntent.putExtra("providerLat", viewModel.providerLatitude.get())
                nextIntent.putExtra("providerLng", viewModel.providerLongitude.get())
                nextIntent.putExtra("providerAddress", viewModel.providerAddress.get())
                nextIntent.putExtra("deliveryType", viewModel.deliveryType.get())

                startActivity(nextIntent)
            }
        })

        viewModel.onChatClick.observe(this, Observer {
            var intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("id", viewModel.id.get().toString())
            intent.putExtra("name", viewModel.name.get())
            intent.putExtra("image", viewModel.image.get())
            intent.putExtra("from", viewModel.from.get())
            startActivity(intent)
        })
    }
//    private fun setAdapter() {
//        var adapter = PreviousOverSeasTripAdapter()
//        recyclerViewPreviousOverSeasAdapter.layoutManager = LinearLayoutManager(this)
//        recyclerViewPreviousOverSeasAdapter.adapter = adapter
//    }
}
