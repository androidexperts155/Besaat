package com.deepak.besaat.view.activities.courierGuysListing

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.Interfaces.imageIconClick
import com.deepak.besaat.Interfaces.searchTextChange
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityCourierGuysListingBinding
import com.deepak.besaat.model.courierGuysModel.Courier
import com.deepak.besaat.model.courierGuysModel.GetDeliveryTypesPojo
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.courierGuysListing.adapter.CourierGuyzListAdapter
import com.deepak.besaat.view.activities.courierGuysListing.adapter.CourierGuyzLocalListAdapter
import com.deepak.besaat.view.activities.courierGuysListing.adapter.CourierGuyzOverseasListAdapter
import com.deepak.besaat.view.activities.courierGuysListing.adapter.DeliveryTypesAdapter
import com.deepak.besaat.view.activities.courierGuysListing.fragment.CourierGuysListMapFragment
import com.deepak.besaat.view.activities.courierGuysListing.fragment.CourierGuysOverseasListMapFragment
import com.deepak.besaat.viewModel.CourierGuysListingActivityViewModel
import com.facebook.FacebookSdk
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import kotlinx.android.synthetic.main.activity_courier_guys_listing.*
import kotlinx.android.synthetic.main.activity_select_language.*
import org.koin.android.ext.android.inject
import java.io.IOException

class CourierGuysListing : BaseActivity(), DeliveryTypesAdapter.OnDeliveryTypeSelectClick {
    lateinit var binding: ActivityCourierGuysListingBinding
    val fragmentFucntions: FragmentFucntions by inject()
    val viewModel: CourierGuysListingActivityViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val sharedPref: SharedPref by inject()

    lateinit var adapter: CourierGuyzOverseasListAdapter
    lateinit var adapterLocal: CourierGuyzLocalListAdapter
    lateinit var deliveryTypesAdapter: DeliveryTypesAdapter

    var courierGuysListMapFragment = CourierGuysListMapFragment()
    var courierGuysOverseasListMapFragment = CourierGuysOverseasListMapFragment()

    var courierGuysList: MutableList<Courier> = ArrayList()
    var deliveryTypesList: MutableList<GetDeliveryTypesPojo.Datum> = ArrayList()

    val url: String = "get-couriers"
    var markerIcon = ""
    var defaultRadius = "15"
    var distanceMin = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityCourierGuysListingBinding>(
            this,
            R.layout.activity_courier_guys_listing
        )

        init()
//        fragmentFucntions.replaceFragment(this, CourierGuysListingFragment(), R.id.listOrMapContainerLocation)
        initObserver()
        viewModel.getDeliveryTypes()
    }

    fun init() {
        imageViewMapList.visibility = View.GONE
        binding.viewModelCourierGuyzListing = viewModel
        initFirstTimeDefaultValue()
        customToolBarWithSearch(this, toolBar, object : searchTextChange {
            override fun TextChange(value: String) {
                viewModel.search.set(value)
                if (clDeliveryType.visibility == View.GONE)
                    getCourierGuysList()
            }
        }, object : imageIconClick {
            override fun onClick(boolean: Boolean) {
                if (boolean) {
                    showFilterDialog()
                }
            }
        })
        deliveryTypesAdapter = DeliveryTypesAdapter(this, deliveryTypesList)
        deliveryTypesAdapter.onClickListner(this)
    }

    private fun showFilterDialog() {
        val commonDialog = Dialog(this)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.layout_popup, null, false)
        var ivClose = vv.findViewById<ImageView>(R.id.imageCross)
        var tvApply = vv.findViewById<TextView>(R.id.textViewContinue2)
        var tvReset = vv.findViewById<TextView>(R.id.textreset)
        var tvDistance = vv.findViewById<TextView>(R.id.seekbardetails)
        var seekBarDistance = vv.findViewById<SeekBar>(R.id.simpleSeekBar)
        var radioGroup = vv.findViewById<RadioGroup>(R.id.groupradio)
        var radioGroupSorting = vv.findViewById<RadioGroup>(R.id.radio_group_sorting)
        var radioGroupOrder = vv.findViewById<RadioGroup>(R.id.radio_group_order)

        var selectedDistance: String = defaultRadius

        if (viewModel.radius.get() != defaultRadius) {
            tvReset.visibility = View.VISIBLE
        } else {
            tvReset.visibility = View.GONE
        }


        radioGroup.setOnCheckedChangeListener { group, viewID ->
            if (viewID == R.id.radia_id11) {
                viewModel.rating.set("All")
            }
            if (viewID == R.id.radia_id1) {
                viewModel.rating.set("5")
            }
            if (viewID == R.id.radia_id2) {
                viewModel.rating.set("4")
            }
            if (viewID == R.id.radia_id3) {
                viewModel.rating.set("3")
            }
            if (viewID == R.id.radia_id4) {
                viewModel.rating.set("2")
            }
            if (viewID == R.id.radia_id5) {
                viewModel.rating.set("1")
            }
        }

        radioGroupSorting.setOnCheckedChangeListener { group, viewID ->
            if (viewID == R.id.radio_by_rating) {
                viewModel.sort_by.set(Constants.SORT_RATING)
            }
            if (viewID == R.id.radio_by_distance) {
                viewModel.sort_by.set(Constants.SORT_DISTANCE)
            }
        }

        radioGroupOrder.setOnCheckedChangeListener { group, viewID ->
            if (viewID == R.id.radio_highest) {
                viewModel.order.set(Constants.ORDER_HIGHEST)
            }
            if (viewID == R.id.radio_lowest) {
                viewModel.order.set(Constants.ORDER_LOWEST)
            }
        }

        tvApply.setOnClickListener {
            viewModel.radius.set(selectedDistance)
            commonDialog.dismiss()
            getCourierGuysList()
        }

        ivClose.setOnClickListener {
            commonDialog.dismiss()
        }

        tvReset.setOnClickListener {
            viewModel.radius.set(defaultRadius)
            viewModel.rating.set("All")
            radioGroup.check(R.id.radia_id11)
            selectedDistance = viewModel.radius.get()!!
            tvDistance.text = String.format("%s km", viewModel.radius.get())
            seekBarDistance?.progress = selectedDistance.toInt()
            tvReset.visibility = View.GONE
//            commonDialog.dismiss()
            getCourierGuysList()
        }
        selectedDistance = viewModel.radius.get()!!
        tvDistance.text = String.format("%s km", viewModel.radius.get())
        seekBarDistance?.progress = selectedDistance.toInt()

        seekBarDistance?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                viewModel.radius.set(p1.toString())
                selectedDistance = p1.toString()
                if (p1 < distanceMin) {
                    selectedDistance = "1"
//                    viewModel.radius.set("1")
                }
                tvDistance.text = String.format("%s km", selectedDistance)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        when (viewModel.rating.get()) {
            "All" -> {
                radioGroup.check(R.id.radia_id11)
            }
            "5" -> {
                radioGroup.check(R.id.radia_id1)
                tvReset.visibility = View.VISIBLE
            }
            "4" -> {
                radioGroup.check(R.id.radia_id2)
                tvReset.visibility = View.VISIBLE
            }
            "3" -> {
                radioGroup.check(R.id.radia_id3)
                tvReset.visibility = View.VISIBLE
            }
            "2" -> {
                radioGroup.check(R.id.radia_id4)
                tvReset.visibility = View.VISIBLE
            }
            "1" -> {
                radioGroup.check(R.id.radia_id5)
                tvReset.visibility = View.VISIBLE
            }
        }

        when (viewModel.order.get()) {
            Constants.ORDER_LOWEST -> {
                radioGroupOrder.check(R.id.radio_lowest)
            }
            Constants.ORDER_HIGHEST -> {
                radioGroupOrder.check(R.id.radio_highest)
            }
        }

        when (viewModel.sort_by.get()) {
            Constants.SORT_DISTANCE -> {
                radioGroupSorting.check(R.id.radio_by_distance)
            }
            Constants.SORT_RATING -> {
                radioGroupSorting.check(R.id.radio_by_rating)
            }
        }

        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        commonDialog.setCancelable(false)
        commonDialog.show()
    }

    private fun getCourierGuysList() {
        courierGuysList.clear()
        viewModel.getCourierGuys(url)
    }

    private fun initFirstTimeDefaultValue() {
        viewModel.courierStatus.set("1")// 1-> local, 2-> overseas
        viewModel.latitude.set(sharedPref.getString(Constants.latitude))
        viewModel.longitude.set(sharedPref.getString(Constants.longitude))
        viewModel.search.set("")
        viewModel.fromCountry.set("")
        viewModel.toCountry.set("")

        viewModel.sort_by.set(Constants.SORT_DISTANCE) //sort_by(0=>distance, 1=>rating)
        viewModel.order.set(Constants.ORDER_LOWEST)  //order(0 => highest, 1=> lowest)
        viewModel.radius.set(defaultRadius)


        var countryName = getCurrentCountry()
        var countryCode = ""
        if (countryName != null && countryName != "")
            countryCode = getCOuntryCode.getCountryCodeByCountryName(countryName?.toLowerCase())
        ccpArrival?.setCountryForNameCode(countryCode)
        ccpDeparture?.setCountryForNameCode(countryCode)


        viewModel.fromCountry.set(ccpDeparture?.selectedCountryNameCode)
        viewModel.toCountry.set(ccpArrival?.selectedCountryNameCode)
    }

    private fun setUpAdapter() {
        adapter =
            CourierGuyzOverseasListAdapter(this, courierGuysList, viewModel.deliveryType.get()!!)
        recyclerViewCourierGuysListing.layoutManager = LinearLayoutManager(this)
        recyclerViewCourierGuysListing.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider
                )
            )
        )
        recyclerViewCourierGuysListing.adapter = adapter
    }

    private fun setUpAdapterLocal() {
        adapterLocal =
            CourierGuyzLocalListAdapter(this, courierGuysList, viewModel.deliveryType.get()!!)
        recyclerViewCourierGuysListing.layoutManager = LinearLayoutManager(this)
        recyclerViewCourierGuysListing.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider
                )
            )
        )
        recyclerViewCourierGuysListing.adapter = adapterLocal
    }

    fun initObserver() {

        ccpDeparture?.setOnCountryChangeListener {
            viewModel.fromCountry.set(ccpDeparture?.selectedCountryNameCode)
        }

        ccpArrival?.setOnCountryChangeListener {
            viewModel.toCountry.set(ccpArrival?.selectedCountryNameCode)
        }

        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.showProgressBar(this, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

        viewModel.courierStatusObserver.observe(this, Observer {
            if (it)
                if (viewModel.courierStatus.get() == "1")
                    llOverseas.visibility = View.GONE
                else
                    llOverseas.visibility = View.VISIBLE
        })

        viewModel.deliveryTypeClick.observe(this, Observer {
            if (it) {
                showDeliveryTypePopup()
            }
        })

        viewModel.listMapIocnClick.observe(this, Observer {
            if (it) {
                if (recyclerViewCourierGuysListing.visibility == View.VISIBLE) {
                    imageViewMapList.setImageResource(R.drawable.ic_list)
                    map_relative_layout.visibility = View.VISIBLE
                    recyclerViewCourierGuysListing.visibility = View.GONE
                } else {
                    imageViewMapList.setImageResource(R.drawable.ic_map)
                    map_relative_layout.visibility = View.GONE
                    recyclerViewCourierGuysListing.visibility = View.VISIBLE

                    if (courierGuysList.isEmpty()) {
                        if (map_relative_layout.visibility == View.GONE) {
                            rlNoDataFound.visibility = View.VISIBLE
                        }
                    } else {
                        rlNoDataFound.visibility = View.GONE
                    }
                }
            }
        })

        viewModel.continueClickObserver.observe(this, Observer {
            if (it) {
                imageViewMapList.visibility = View.VISIBLE
                clDeliveryType.visibility = View.GONE
                map_relative_layout.visibility = View.GONE
                recyclerViewCourierGuysListing.visibility = View.VISIBLE
                if (viewModel.courierStatus.get() != null && viewModel.courierStatus.get() == "1")
                    setUpAdapterLocal()
                else
                    setUpAdapter()

                getCourierGuysList()
            }
        })

        viewModel.changeLocationClick.observe(this, Observer {
            if (it) {
                if (sharedPref.getString(Constants.latitude) == viewModel.latitude.get() &&
                    sharedPref.getString(Constants.longitude) == viewModel.longitude.get()
                ) {
                    initPlaceAutoCompleteActivity(this, Constants.REQUEST_LOCATION, "")
                } else {
                    imageView4.setImageResource(R.drawable.ic_location)
                    viewModel.latitude.set(sharedPref.getString(Constants.latitude))
                    viewModel.longitude.set(sharedPref.getString(Constants.longitude))
//                    if (recyclerViewCourierGuysListing.visibility == View.VISIBLE) // add map condition also
                    if (clDeliveryType.visibility == View.GONE)
                        getCourierGuysList()
                }
            }
        })

        viewModel.successfullyGetDeliveryTypes.observe(this, Observer {
            if (it.getStatus() != null && it.getStatus()!!) {
                if (it.getMapIcon() != null) {
                    markerIcon = it.getMapIcon()!!
                }
                deliveryTypesList.clear()
                deliveryTypesList.addAll(it.getData() as MutableList<GetDeliveryTypesPojo.Datum>)
//                commonFunctions.showFeedbackMessage(rootLayout, it.getMessage()!!)
                deliveryTypesAdapter.submitList(deliveryTypesList)
                deliveryTypesAdapter.notifyDataSetChanged()
            } else {
                commonFunctions.showFeedbackMessage(rootLayout, "Error")
            }
        })


        viewModel.successfullyGetCourierGuys.observe(this, Observer {
            if (it.getStatus() != null && it.getStatus()!!) {
//                commonFunctions.showFeedbackMessage(rootLayout, it.getMessage()!!)
                courierGuysList.clear()
                courierGuysList.addAll(it.getCouriers() as MutableList<Courier>)

                if (viewModel.courierStatus.get() == "1") {
                    adapterLocal.submitList(courierGuysList)
                    adapterLocal.notifyDataSetChanged()
                } else {
                    adapter.submitList(courierGuysList)
                    adapter.notifyDataSetChanged()
                }

                if (viewModel.courierStatus.get() == "1") {
                    courierGuysListMapFragment = CourierGuysListMapFragment()
                    courierGuysListMapFragment.setDetails(
                        courierGuysList as java.util.ArrayList<com.deepak.besaat.model.courierGuysModel.Courier>?,
                        sharedPref,
                        "all",
                        markerIcon,
                        viewModel.deliveryType.get(),
                        viewModel.latitude.get(), viewModel.longitude.get()
                    )

                    fragmentFucntions.replaceFragment(this, courierGuysListMapFragment, R.id.map)
                } else {
                    courierGuysOverseasListMapFragment = CourierGuysOverseasListMapFragment()
                    courierGuysOverseasListMapFragment.setDetails(
                        courierGuysList as java.util.ArrayList<com.deepak.besaat.model.courierGuysModel.Courier>?,
                        sharedPref,
                        "all",
                        markerIcon,
                        viewModel.deliveryType.get(),
                        viewModel.latitude.get(), viewModel.longitude.get()
                    )
                    fragmentFucntions.replaceFragment(
                        this,
                        courierGuysOverseasListMapFragment,
                        R.id.map
                    )
                }


                if (courierGuysList.isEmpty()) {
                    if (map_relative_layout.visibility == View.GONE) {
                        rlNoDataFound.visibility = View.VISIBLE
                    }
                } else {
                    rlNoDataFound.visibility = View.GONE
                }

            } else {
                if (it.getMessage() != null) {
                    commonFunctions.showFeedbackMessage(rootLayout, it.getMessage()!!)
                    if (it.getMessage() == "Couriers Not Found!") {
                        courierGuysList.clear()
                        if (viewModel.courierStatus.get() == "1") {
                            adapterLocal.submitList(courierGuysList)
                            adapterLocal.notifyDataSetChanged()
                        } else {
                            adapter.submitList(courierGuysList)
                            adapter.notifyDataSetChanged()
                        }

                        if (viewModel.courierStatus.get() == "1") {
                            courierGuysListMapFragment = CourierGuysListMapFragment()
                            courierGuysListMapFragment.setDetails(
                                courierGuysList as java.util.ArrayList<com.deepak.besaat.model.courierGuysModel.Courier>?,
                                sharedPref,
                                "all",
                                markerIcon,
                                viewModel.deliveryType.get(),
                                viewModel.latitude.get(), viewModel.longitude.get()
                            )
                            fragmentFucntions.replaceFragment(
                                this,
                                courierGuysListMapFragment,
                                R.id.map
                            )
                        } else {
                            courierGuysOverseasListMapFragment =
                                CourierGuysOverseasListMapFragment()
                            courierGuysOverseasListMapFragment.setDetails(
                                courierGuysList as java.util.ArrayList<com.deepak.besaat.model.courierGuysModel.Courier>?,
                                sharedPref,
                                "all",
                                markerIcon,
                                viewModel.deliveryType.get(),
                                viewModel.latitude.get(), viewModel.longitude.get()
                            )
                            fragmentFucntions.replaceFragment(
                                this,
                                courierGuysOverseasListMapFragment,
                                R.id.map
                            )
                        }

                        if (map_relative_layout.visibility == View.GONE) {
                            rlNoDataFound.visibility = View.VISIBLE
                        }

                        if (courierGuysList.isEmpty()) {
                            rlNoDataFound.visibility = View.VISIBLE
                        } else {
                            rlNoDataFound.visibility = View.GONE
                        }
                    }
                } else {
                    commonFunctions.showFeedbackMessage(rootLayout, "Server Error ")
                }
            }
        })
    }

    override fun onBackPressed() {
        if (clDeliveryType.visibility == View.GONE) {
            clDeliveryType.visibility = View.VISIBLE
            imageViewMapList.visibility = View.GONE
            recyclerViewCourierGuysListing.visibility = View.GONE
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_LOCATION && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                viewModel.latitude.set(place?.latLng?.latitude?.toString())
                viewModel.longitude.set(place?.latLng?.longitude?.toString())

                if (sharedPref.getString(Constants.latitude) == viewModel.latitude.get() &&
                    sharedPref.getString(Constants.longitude) == viewModel.longitude.get()
                ) {
                    imageView4.setImageResource(R.drawable.ic_location)
                } else {
                    imageView4.setImageResource(R.drawable.location_disabled)
//                    if (recyclerViewCourierGuysListing.visibility == View.VISIBLE) // add map condition also
                    if (clDeliveryType.visibility == View.GONE)
                        getCourierGuysList()
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    override fun onPosClick(position: Int, name: String) {
        for (i in 0 until deliveryTypesList.size) {
            deliveryTypesList[i].check = false
        }
        deliveryTypesList[position].check = true
        viewModel.deliveryTypeName.set(deliveryTypesList[position].name)
        viewModel.deliveryType.set(deliveryTypesList[position].id.toString())
        deliveryTypesAdapter.submitList(deliveryTypesList)
        deliveryTypesAdapter.notifyDataSetChanged()
    }

    private fun showDeliveryTypePopup() {
        val commonDialog = Dialog(this)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_delivery_types, null)
        val rvDeliveryTypes = vv.findViewById<RecyclerView>(R.id.rvDeliveryTypes)
        rvDeliveryTypes.adapter = deliveryTypesAdapter
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


    private fun getCurrentCountry(): String? {
        var country_name: String? = null
        val lm =
            FacebookSdk.getApplicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val geocoder = Geocoder(FacebookSdk.getApplicationContext())

        for (provider in lm.getAllProviders()) {
            // val location = lm.getLastKnownLocation(provider)
            //   if (location != null)
            //   {
            try {
                var addresses: MutableList<Address>? = null

                if (!sharedPref.getString(Constants.latitude).equals("") && !sharedPref.getString(
                        Constants.longitude
                    ).equals("")
                ) {
                    addresses = geocoder.getFromLocation(
                        sharedPref.getString(Constants.latitude).toDouble(),
                        sharedPref.getString(Constants.longitude).toDouble(),
                        1
                    )
                }
                if (addresses != null && addresses.size > 0) {
                    country_name = addresses.get(0).getCountryName()
                    break
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return country_name
    }
}
