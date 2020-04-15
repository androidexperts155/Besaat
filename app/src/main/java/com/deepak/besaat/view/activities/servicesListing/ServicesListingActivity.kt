package com.deepak.besaat.view.activities.servicesListing

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.Interfaces.imageIconClick
import com.deepak.besaat.Interfaces.searchTextChange
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityServicesListingBinding
import com.deepak.besaat.model.getServicesModel.Datum
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.servicesListing.adapter.ServiceProviderListAdapter
import com.deepak.besaat.view.activities.servicesListing.adapter.ServicesCategoryListAdapter
import com.deepak.besaat.view.activities.servicesListing.fragment.ServiceProvidersMapFragment
import com.deepak.besaat.view.activities.servicesListing.interfaces.IServiceCategory
import com.deepak.besaat.viewModel.ServicesListingActivityViewModel
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_select_language.*
import kotlinx.android.synthetic.main.activity_services_listing.*
import org.json.JSONObject
import org.koin.android.ext.android.inject

class ServicesListingActivity : BaseActivity(), IServiceCategory {
    lateinit var binding: ActivityServicesListingBinding
    val fragmentFucntions: FragmentFucntions by inject()
    val sharedPref: SharedPref by inject()
    val viewModel: ServicesListingActivityViewModel by inject()
    var servicesList: MutableList<Datum> = ArrayList()
    var serviceMapFragment = ServiceProvidersMapFragment()
    var serviceProviderList: MutableList<com.deepak.besaat.model.getServiceProviderModel.Datum> =
        ArrayList()
    val commonFunctions: CommonFunctions by inject()
    val url: String = "service-providers"
    var markerIcon = ""
    var catName = "All"
    var defaultDistance = "15"
    var distanceMin = 1

    lateinit var adapter: ServicesCategoryListAdapter
    lateinit var serviceProviderAdapter: ServiceProviderListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityServicesListingBinding>(
            this,
            R.layout.activity_services_listing
        )
        setAdapterServiceCategories()
        setAdapterServiceProviders()
        customToolBarWithSearch(this, toolBar, object : searchTextChange {
            override fun TextChange(value: String) {
                viewModel.search.set(value)
                getProviders(url)
            }
        }, object : imageIconClick {
            override fun onClick(boolean: Boolean) {
                if (boolean) {
                    showFilterDialog()
                }
            }
        })
        //binding.viewModelServiceListing=viewModel
        binding.viewModelServiceListing = viewModel
        firstTimeSetUp()
        initObserver()
        viewModel.getServices()

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

        var selectedDistance: String = defaultDistance

        if (viewModel.radius.get() != defaultDistance) {
            tvReset.visibility = View.VISIBLE
        } else {
            tvReset.visibility = View.GONE
        }


        radioGroup.setOnCheckedChangeListener { group, viewID ->
            if (viewID == R.id.radia_id11) {
                viewModel.rating.set("0")
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
                viewModel.sort.set(Constants.SORT_RATING)
            }
            if (viewID == R.id.radio_by_distance) {
                viewModel.sort.set(Constants.SORT_DISTANCE)
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
            getProviders(url)
        }

        ivClose.setOnClickListener {
            commonDialog.dismiss()
        }

        tvReset.setOnClickListener {
            viewModel.radius.set(defaultDistance)
            viewModel.rating.set("0")
            radioGroup.check(R.id.radia_id11)
            selectedDistance = viewModel.radius.get()!!
            tvDistance.text = String.format("%s km", viewModel.radius.get())
            seekBarDistance?.progress = selectedDistance.toInt()
            tvReset.visibility = View.GONE
//            commonDialog.dismiss()
            getProviders(url)
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
            "0" -> {
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

        when (viewModel.sort.get()) {
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

    private fun firstTimeSetUp()
    {
        viewModel.service_id.set("0")
        viewModel.radius.set(defaultDistance)
        viewModel.rating.set("0")
        viewModel.search.set("")
        viewModel.sort.set(Constants.SORT_DISTANCE)
        viewModel.order.set(Constants.ORDER_LOWEST)
        viewModel.latitude.set(sharedPref.getString(Constants.latitude))
        viewModel.logitude.set(sharedPref.getString(Constants.longitude))
    }

    fun initObserver() {
        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.showProgressBar(this, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.successfullyGetServices.observe(this, Observer {
            servicesList.clear()
            servicesList = it.data!!
            var data = Datum()
            data.name = "All"
            data.id = 0
            data.isChecked = true
            servicesList.add(0, data)
            adapter.submitList(it.data)
            getProviders(url)
        })

        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)

            if (it == "Providers Not Found.") {
                serviceProviderList.clear()
                serviceProviderAdapter.submitList(serviceProviderList)
                serviceProviderAdapter.notifyDataSetChanged()

                serviceMapFragment = ServiceProvidersMapFragment()
                serviceMapFragment.setDetails(
                    serviceProviderList as java.util.ArrayList<com.deepak.besaat.model.getServiceProviderModel.Datum>?,
                    sharedPref,
                    catName,
                    markerIcon,
                    viewModel.latitude.get(), viewModel.logitude.get()
                )
                fragmentFucntions.replaceFragment(this, serviceMapFragment, R.id.map)

                if (map_relative_layout.visibility == View.GONE) {
                    rlNoDataFound.visibility = View.VISIBLE
                }
            }
        })

        viewModel.successfullyGetServiceProviders.observe(this, Observer {
            if (it.getStatus() != null && it.getStatus()!!) {
                serviceProviderList.clear()
                serviceProviderList.addAll(it.getData() as MutableList<com.deepak.besaat.model.getServiceProviderModel.Datum>)
                serviceProviderAdapter.submitList(it.getData())
                serviceProviderAdapter.notifyDataSetChanged()

                if (serviceProviderList.isEmpty()) {
                    rlNoDataFound.visibility = View.VISIBLE
                } else {
                    rlNoDataFound.visibility = View.GONE
                }

            } else if (it.getMessage() != null)
                commonFunctions.showFeedbackMessage(rootLayout, it.getMessage()!!)
            else
                commonFunctions.showFeedbackMessage(rootLayout, getString(R.string.server_error))

            serviceMapFragment = ServiceProvidersMapFragment()
            serviceMapFragment.setDetails(
                serviceProviderList as java.util.ArrayList<com.deepak.besaat.model.getServiceProviderModel.Datum>?,
                sharedPref,
                catName,
                markerIcon,
                viewModel.latitude.get(), viewModel.logitude.get()
            )
            fragmentFucntions.replaceFragment(this, serviceMapFragment, R.id.map)
        })

        viewModel.listMapIocnClick.observe(this, Observer {
            if (it) {
                if (recyclerViewServiceProviderListingAdapter.visibility == View.VISIBLE) {
                    imageViewMapList.setImageResource(R.drawable.ic_list)
                    map_relative_layout.visibility = View.VISIBLE
                    recyclerViewServiceProviderListingAdapter.visibility = View.GONE
                } else {
                    imageViewMapList.setImageResource(R.drawable.ic_map)
                    map_relative_layout.visibility = View.GONE
                    recyclerViewServiceProviderListingAdapter.visibility = View.VISIBLE
                    if (serviceProviderList.isEmpty()) {
                        rlNoDataFound.visibility = View.VISIBLE
                    } else {
                        rlNoDataFound.visibility = View.GONE
                    }
                }
            }
        })

        viewModel.changeLocationClick.observe(this, Observer {
            if (sharedPref.getString(Constants.latitude) == viewModel.latitude.get() &&
                sharedPref.getString(Constants.longitude) == viewModel.logitude.get()
            ) {
                initPlaceAutoCompleteActivity(this, Constants.REQUEST_LOCATION,"")
            } else {
                imageView4.setImageResource(R.drawable.ic_location)
                viewModel.latitude.set(sharedPref.getString(Constants.latitude))
                viewModel.logitude.set(sharedPref.getString(Constants.longitude))
                getProviders(url)
            }
        })
    }

    private fun setAdapterServiceCategories() {
        adapter = ServicesCategoryListAdapter(this, servicesList)
        binding.recyclerViewSListing.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.recyclerViewSListing.adapter = adapter
        adapter.attachCategoryClicks(this)
    }

    private fun setAdapterServiceProviders() {
        serviceProviderAdapter = ServiceProviderListAdapter(this, serviceProviderList)
        binding.recyclerViewServiceProviderListingAdapter.adapter = serviceProviderAdapter

        binding.recyclerViewServiceProviderListingAdapter.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider
                )
            )
        )

        serviceMapFragment.setDetails(
            serviceProviderList as java.util.ArrayList<com.deepak.besaat.model.getServiceProviderModel.Datum>?,
            sharedPref,
            catName,
            markerIcon,
            viewModel.latitude.get(), viewModel.logitude.get()
        )
    }

    private fun getProviders(url: String) {
        viewModel.getServiceProviders(url)
    }

    override fun onServiceCategoryClick(position: Int) {
        if (viewModel.service_id.get() != servicesList[position].id.toString()) {
            for (i in 0 until servicesList.size) {
                servicesList[i].isChecked = false
            }
            servicesList[position].isChecked = true
            markerIcon = if (servicesList[position].map_image != null)
                servicesList[position].map_image!!
            else
                ""
            catName = servicesList[position].name!!
            adapter.submitList(servicesList)
            adapter.notifyDataSetChanged()

            viewModel.service_id.set(servicesList[position].id.toString())
            getProviders(url)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_LOCATION && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                viewModel.latitude.set(place?.latLng?.latitude?.toString())
                viewModel.logitude.set(place?.latLng?.longitude?.toString())

                if (sharedPref.getString(Constants.latitude) == viewModel.latitude.get() &&
                    sharedPref.getString(Constants.longitude) == viewModel.logitude.get()
                ) {
                    imageView4.setImageResource(R.drawable.ic_location)
                } else {
                    getProviders(url)
                    imageView4.setImageResource(R.drawable.location_disabled)
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }
}
