package com.deepak.besaat.view.activities.storeNearByListing.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.Interfaces.imageIconClick
import com.deepak.besaat.Interfaces.searchTextChange
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityStoreNearByListingBinding
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.model.StoresNearbyModel.GetStoresNearByModel
import com.deepak.besaat.model.getStoresModel.Store
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.FragmentFucntions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.storeNearByListing.adapters.StoresNearbyCategoryListAdapter
import com.deepak.besaat.view.activities.storeNearByListing.adapters.StoresNearbyListAdapter
import com.deepak.besaat.view.activities.storeNearByListing.dbViewModel.DBViewModel
import com.deepak.besaat.view.activities.storeNearByListing.fragments.StoresNearByListMapFragment
import com.deepak.besaat.view.activities.storeNearByListing.interfaces.IStoreCategory
import com.deepak.besaat.viewModel.StoresNearByListingViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_store_near_by_listing.*
import org.koin.android.ext.android.inject
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class StoreNearByListingActivity : BaseActivity(), IStoreCategory {

    lateinit var binding: ActivityStoreNearByListingBinding
    private val fragmentFucntions: FragmentFucntions by inject()
    val sharedPref: SharedPref by inject()
    val viewModel: StoresNearByListingViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    var viewModelDB: DBViewModel? = null

    var markerIcon = ""
    var catName = "All"
    var defaultDistance = "15"
    var distanceMin = 1
    var allCategory: String? = ""

    private var isLoading = false
    private var recyclerViewState: Parcelable? = null

    lateinit var adapter: StoresNearbyCategoryListAdapter
    lateinit var storesAdapter: StoresNearbyListAdapter
    var storeCategoriesList: MutableList<Store> = ArrayList()
    var storeList: MutableList<SourceDetails> = ArrayList()
    var storeListFilter: MutableList<SourceDetails> = ArrayList()
    var storesNearByListMapFragment = StoresNearByListMapFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityStoreNearByListingBinding>(
            this,
            R.layout.activity_store_near_by_listing
        )
//        setContentView(R.layout.activity_store_near_by_listing)
        init()
    }

    fun init() {
        binding.viewModelStoreNearByListing = viewModel
        viewModelDB = ViewModelProviders.of(this).get(DBViewModel::class.java)
        firstTimeSetup()
        setAdapterStoreCategories()
        setAdapterStoresListing()
        initObserver()
        viewModel.getStores()
//        viewModelDB!!.getAllDataFromDB()

        customToolBarWithSearch(this, toolBar, object : searchTextChange {
            override fun TextChange(value: String) {
                viewModel.search.set(value)
                filterList()
                // call api when no result from existing result or filter
            }
        }, object : imageIconClick {
            override fun onClick(boolean: Boolean) {
                if (boolean) {
                    showFilterDialog()
                }
            }
        })
    }

    private fun filterList() {
        storeListFilter.clear()
//        if (viewModel.search.get() != null && viewModel.search.get() != "")
        for (i in 0 until storeList.size) {
            if (storeList[i].name!!.contains(viewModel.search.get()!!, true)) {

                if (viewModel.rating.get() != null && viewModel.rating.get() != "All") {
                    if (storeList[i].rating!! <= viewModel.rating.get()!!.toFloat() && storeList[i].rating!! > (viewModel.rating.get()!!.toFloat() - 1.0f)) {
                        if (storeList[i].radius <= viewModel.radius.get()!!.toInt()) {
                            storeListFilter.add(storeList[i])
                        }
                    } else if (1.0f == viewModel.rating.get()!!.toFloat() && storeList[i].rating!! == (viewModel.rating.get()!!.toFloat() - 1.0f)) {
                        if (storeList[i].radius <= viewModel.radius.get()!!.toInt()) {
                            storeListFilter.add(storeList[i])
                        }
                    }
                } else {
                    if (storeList[i].radius <= viewModel.radius.get()!!.toInt()) {
                        storeListFilter.add(storeList[i])
                    }
                }
            }
        }

        /// sorting and order
        if (viewModel.sort.get() == Constants.SORT_RATING) {
            if (viewModel.order.get() == Constants.ORDER_LOWEST) {
                storeListFilter.sortBy { it.rating }
            } else {
                storeListFilter.sortByDescending { it.rating }
            }
        } else {
            if (viewModel.order.get() == Constants.ORDER_LOWEST) {
                storeListFilter.sortBy { it.radius }
            } else {
                storeListFilter.sortByDescending { it.radius }
            }
        }

//        else {
//            storeListFilter.addAll(storeList)
//        }
        if (storeListFilter.isEmpty()) {
            tv_no_data_found.text =
                resources.getString(R.string.no_store_availed_please_change_filter_value_to_list_the_store)
            rlNoDataFound.visibility = View.VISIBLE
        } else {
            rlNoDataFound.visibility = View.GONE
        }

        setAdapterStoresListing()
//        storesNearByListMapFragment=StoresNearByListMapFragment()
        storesNearByListMapFragment.setDetails(
            storeListFilter as java.util.ArrayList<SourceDetails>?,
            sharedPref,
            catName,
            markerIcon,
            viewModel.latitude.get(), viewModel.longitude.get()
        )
        fragmentFucntions.replaceFragment(this, storesNearByListMapFragment, R.id.map)
    }

    private fun firstTimeSetup() {
        // default values
        viewModel.selectedStoreCat.set(allCategory)
        viewModel.selectedStoreCatID.set(0)
        viewModel.radius.set(defaultDistance)
        viewModel.rating.set("All")
        viewModel.search.set("")
        viewModel.sort.set(Constants.SORT_DISTANCE)
        viewModel.order.set(Constants.ORDER_LOWEST)
        if (sharedPref.getString(Constants.latitude) == "") {
            viewModel.latitude.set("0.0")
            viewModel.longitude.set("0.0")
        } else {
            viewModel.latitude.set(sharedPref.getString(Constants.latitude))
            viewModel.longitude.set(sharedPref.getString(Constants.longitude))
        }

        viewModel.pageToken.set("")
        viewModel.google_key.set(resources.getString(R.string.google_api))

        // getIntentValuesFromHomeFragment
        if (intent.getSerializableExtra("storeCat") != null) {
            var storeCat: Store = intent.getSerializableExtra("storeCat") as Store
            viewModel.selectedStoreCat.set(storeCat.status)
            viewModel.selectedStoreCatID.set(storeCat.id?.toInt())
            markerIcon = storeCat.map_image!!
            catName = storeCat.name!!
            storeCat.checked = true

            for (i in 0 until storeCategoriesList.size) {
                storeCategoriesList[i].checked = false
                if (storeCategoriesList[i].id == storeCat.id) {
                    storeCategoriesList[i] = storeCat
                    recyclerViewStoreCategoriesListing.layoutManager?.smoothScrollToPosition(
                        recyclerViewStoreCategoriesListing,
                        RecyclerView.State(),
                        i
                    )
                }
            }

            if (storeCategoriesList.isNotEmpty()) {
                adapter.submitList(storeCategoriesList)
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun initObserver() {
        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.showProgressBar(this, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.successfullyGetStoreCategories.observe(this, Observer {
            storeCategoriesList.clear()
            storeCategoriesList = it.stores!!

            allCategory = ""
            for (i in 0 until storeCategoriesList.size) {
                if (i != storeCategoriesList.size - 1)
                    allCategory = allCategory + "" + storeCategoriesList[i].status + "|"
                else
                    allCategory = allCategory + "" + storeCategoriesList[i].status
            }

            var data = Store()
            data.name = "All"
            data.status = allCategory
            data.id = 0
            data.checked = true
            storeCategoriesList.add(0, data)
            adapter.submitList(it.stores)

            // default value for first time
            firstTimeSetup()
            //////////////////////////

            viewModel.getStoresListing()
        })

        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
            tv_no_data_found.text = it
            if (storeListFilter.isEmpty()) {
                rlNoDataFound.visibility = View.VISIBLE
            } else {
                rlNoDataFound.visibility = View.GONE
            }
        })

        viewModel.successfullyGetStoresList.observe(this, Observer {
            Log.e("data", "" + it.toString())
            isLoading = false
            var pojo = Gson().fromJson(it.toString(), GetStoresNearByModel::class.java)

            if (pojo.getStatus() != null && pojo.getStatus() == "OK") {
                for (i in 0 until pojo.getResults()?.size!!) {
                    var sourceDetail = SourceDetails()
                    sourceDetail.icon = pojo.getResults()?.get(i)?.icon
                    sourceDetail.name = pojo.getResults()?.get(i)?.name
                    sourceDetail.locAddress = pojo.getResults()?.get(i)?.formattedAddress
                    if (pojo.getResults()?.get(i)?.formattedAddress == null)
                        sourceDetail.locAddress = pojo.getResults()?.get(i)?.vicinity

                    if (pojo.getResults()?.get(i)?.rating != null) {
                        sourceDetail.rating = pojo.getResults()?.get(i)?.rating
                    } else {
                        sourceDetail.rating = 0.0f
                    }
                    sourceDetail.category = viewModel.selectedStoreCatID.get().toString()
                    sourceDetail.placeId = pojo.getResults()?.get(i)?.placeId
                    sourceDetail.userRatingsTotal = pojo.getResults()?.get(i)?.userRatingsTotal

                    sourceDetail.latitute = pojo.getResults()?.get(i)?.geometry?.location?.lat!!
                    sourceDetail.longitute =
                        pojo.getResults()?.get(i)?.geometry?.location?.lng!!

                    if (pojo.getResults()?.get(i)?.photos != null && pojo.getResults()?.get(i)?.photos!!.isNotEmpty()) {
                        sourceDetail.photoRefrence =
                            pojo.getResults()?.get(i)?.photos!![0].photoReference
                        sourceDetail.heightt = pojo.getResults()?.get(i)?.photos!![0].height
                        sourceDetail.widthh = pojo.getResults()?.get(i)?.photos!![0].width
                    }

                    sourceDetail.radius = calculationByDistance(
                        LatLng(
                            viewModel.latitude.get()!!.toDouble(),
                            viewModel.longitude.get()!!.toDouble()
                        ),
                        LatLng(sourceDetail.latitute, sourceDetail.longitute)
                    )
                    storeList.add(sourceDetail)
//                        viewModelDB!!.insertData(sourceDetail)   // to store data in database(ROom)
                }

                if (pojo.getNextPageToken() != null)
                    viewModel.pageToken.set(pojo.getNextPageToken())
                else
                    viewModel.pageToken.set("")

                filterList()

                if (storeListFilter.isEmpty()) {
                    rlNoDataFound.visibility = View.VISIBLE
                } else {
                    rlNoDataFound.visibility = View.GONE
                }

                // to load all google places 3 pages data 20 per page .... max result can be 60 result as per client requirements
                if (pojo.getNextPageToken() != null) {
                    viewModel.progressBar.postValue(true)
                    Handler().postDelayed(
                        {
                            if (pojo.getNextPageToken() != null) {
                                viewModel.getStoresListing()
                            }
                        },
                        3000
                    )
                } else {
                    viewModel.progressBar.postValue(false)
                }
            } else if (pojo.getStatus() != null) {
                commonFunctions.showFeedbackMessage(rootLayout, pojo.getStatus()!!)
                if (storeListFilter.isEmpty()) {
                    tv_no_data_found.text =
                        resources.getString(R.string.no_store_availed_please_change_filter_value_to_list_the_store)
                    rlNoDataFound.visibility = View.VISIBLE
                } else {
                    rlNoDataFound.visibility = View.GONE
                }
            } else {
                if (storeListFilter.isEmpty()) {
                    tv_no_data_found.text =
                        resources.getString(R.string.no_store_availed_please_change_filter_value_to_list_the_store)
                    rlNoDataFound.visibility = View.VISIBLE
                } else {
                    rlNoDataFound.visibility = View.GONE
                }
                commonFunctions.showFeedbackMessage(rootLayout, getString(R.string.server_error))
            }
        })

        viewModel.listMapIocnClick.observe(this, Observer {
            if (it) {
                if (recyclerViewStoresNearByListingAdapter.visibility == View.VISIBLE) {
                    imageViewMapList.setImageResource(R.drawable.ic_list)
                    map_relative_layout.visibility = View.VISIBLE
                    recyclerViewStoresNearByListingAdapter.visibility = View.GONE
                } else {
                    imageViewMapList.setImageResource(R.drawable.ic_map)
                    map_relative_layout.visibility = View.GONE
                    recyclerViewStoresNearByListingAdapter.visibility = View.VISIBLE
                    if (storeListFilter.isEmpty()) {
                        rlNoDataFound.visibility = View.VISIBLE
                    } else {
                        rlNoDataFound.visibility = View.GONE
                    }
                }
            }
        })

        viewModel.changeLocationClick.observe(this, Observer {
            if (sharedPref.getString(Constants.latitude) == viewModel.latitude.get() &&
                sharedPref.getString(Constants.longitude) == viewModel.longitude.get()
            ) {
                initPlaceAutoCompleteActivity(this, Constants.REQUEST_LOCATION, "")
            } else {
                imageView4.setImageResource(R.drawable.ic_location)
                viewModel.latitude.set(sharedPref.getString(Constants.latitude))
                viewModel.longitude.set(sharedPref.getString(Constants.longitude))
                viewModel.pageToken.set("")
                storeList.clear()
                storeListFilter.clear()
                viewModel.getStoresListing()
//                getProviders(url)
            }
        })

        viewModelDB!!.notifyGetAllDataFromDB.observe(this, Observer {
            if (it) {
                Toast.makeText(this, "dataDB", Toast.LENGTH_SHORT).show()
            }
        })
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
        var selectedRating = "All"
        var selectedSorting: String = viewModel.sort.get()!!
        var selectedOrder: String = viewModel.order.get()!!

        if (viewModel.radius.get() != defaultDistance) {
            tvReset.visibility = View.VISIBLE
        } else {
            tvReset.visibility = View.GONE
        }


        radioGroup.setOnCheckedChangeListener { group, viewID ->
            if (viewID == R.id.radia_id11) {
//                viewModel.rating.set("All")
                selectedRating = "All"
            }
            if (viewID == R.id.radia_id1) {
//                viewModel.rating.set("5")
                selectedRating = "5"
            }
            if (viewID == R.id.radia_id2) {
//                viewModel.rating.set("4")
                selectedRating = "4"
            }
            if (viewID == R.id.radia_id3) {
//                viewModel.rating.set("3")
                selectedRating = "3"
            }
            if (viewID == R.id.radia_id4) {
//                viewModel.rating.set("2")
                selectedRating = "2"
            }
            if (viewID == R.id.radia_id5) {
//                viewModel.rating.set("1")
                selectedRating = "1"
            }
        }

        radioGroupSorting.setOnCheckedChangeListener { group, viewID ->
            if (viewID == R.id.radio_by_rating) {
//                viewModel.sort.set(Constants.SORT_RATING)
                selectedSorting = Constants.SORT_RATING
            }
            if (viewID == R.id.radio_by_distance) {
//                viewModel.sort.set(Constants.SORT_DISTANCE)
                selectedSorting = Constants.SORT_DISTANCE
            }
        }

        radioGroupOrder.setOnCheckedChangeListener { group, viewID ->
            if (viewID == R.id.radio_highest) {
//                viewModel.order.set(Constants.ORDER_HIGHEST)
                selectedOrder = Constants.ORDER_HIGHEST
            }
            if (viewID == R.id.radio_lowest) {
//                viewModel.order.set(Constants.ORDER_LOWEST)
                selectedOrder = Constants.ORDER_LOWEST
            }
        }

        tvApply.setOnClickListener {
            viewModel.radius.set(selectedDistance)
            viewModel.rating.set(selectedRating)
            viewModel.sort.set(selectedSorting)
            viewModel.order.set(selectedOrder)
            commonDialog.dismiss()
            filterList()
//            viewModel.getStoresListing()
        }

        ivClose.setOnClickListener {
            commonDialog.dismiss()
        }

        tvReset.setOnClickListener {
            viewModel.radius.set(defaultDistance)
            viewModel.rating.set("All")
            viewModel.sort.set(Constants.SORT_DISTANCE)
            viewModel.order.set(Constants.ORDER_LOWEST)
            radioGroup.check(R.id.radia_id11)
            selectedDistance = viewModel.radius.get()!!
            tvDistance.text = String.format("%s km", viewModel.radius.get())
            seekBarDistance?.progress = selectedDistance.toInt()
            tvReset.visibility = View.GONE
            filterList()
//            commonDialog.dismiss()
//            viewModel.getStoresListing()
        }
        selectedDistance = viewModel.radius.get()!!
        tvDistance.text = String.format("%s km", viewModel.radius.get())
        seekBarDistance?.progress = selectedDistance.toInt()

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

        when (viewModel.sort.get()) {
            Constants.SORT_DISTANCE -> {
                radioGroupSorting.check(R.id.radio_by_distance)
            }
            Constants.SORT_RATING -> {
                radioGroupSorting.check(R.id.radio_by_rating)
            }
        }

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

        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        commonDialog.setCancelable(false)
        commonDialog.show()
    }

    private fun setAdapterStoreCategories() {
        adapter = StoresNearbyCategoryListAdapter(this, storeCategoriesList)
        binding.recyclerViewStoreCategoriesListing.adapter = adapter
        adapter.attachCategoryClicks(this)
    }

    private fun setAdapterStoresListing() {
        storesAdapter = StoresNearbyListAdapter(
            this,
            storeListFilter,
            viewModel.latitude.get(),
            viewModel.longitude.get()
        )
        binding.recyclerViewStoresNearByListingAdapter.adapter = storesAdapter
        storesAdapter.notifyDataSetChanged()

//        binding.recyclerViewStoresNearByListingAdapter!!.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if ((recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == storeListFilter.size - 1 && viewModel.pageToken.get() != "") {
//                    if (!isLoading) {
//                        viewModel.getStoresListing()
//                        recyclerViewState =
//                            binding.recyclerViewStoresNearByListingAdapter!!.layoutManager!!.onSaveInstanceState()
//                        isLoading = true
//                    }
//                } else if ((recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == storeListFilter.size - 1 && catName == "All") {
//                    if (!isLoading) {
//                        if (storeCategoriesList.isNotEmpty() && viewModel.selectedStoreCatID.get() == storeCategoriesList[storeCategoriesList.size - 1].id!!.toInt()) {
//                            if (viewModel.pageToken.get() == "") {
//                                viewModel.selectedStoreCatID.set(storeCategoriesList[storeCategoriesList.size - 1].id!!.toInt())
//                            }
//                        }
//                        viewModel.getStoresListing()
//                        recyclerViewState =
//                            binding.recyclerViewStoresNearByListingAdapter!!.layoutManager!!.onSaveInstanceState()
//                        isLoading = true
//                    }
//                }
//            }
//        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_LOCATION && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                viewModel.latitude.set(place.latLng?.latitude?.toString())
                viewModel.longitude.set(place.latLng?.longitude?.toString())

                if (sharedPref.getString(Constants.latitude) == viewModel.latitude.get() &&
                    sharedPref.getString(Constants.longitude) == viewModel.longitude.get()
                ) {
                    imageView4.setImageResource(R.drawable.ic_location)
                } else {
                    viewModel.pageToken.set("")
                    storeList.clear()
                    storeListFilter.clear()
                    viewModel.getStoresListing()
                    imageView4.setImageResource(R.drawable.location_disabled)
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("place", "Cancel")
            }
        }
    }

    override fun onStoreCategoryClick(position: Int) {
        if (viewModel.selectedStoreCatID.get() != storeCategoriesList[position].id?.toInt()) {
            for (i in 0 until storeCategoriesList.size) {
                storeCategoriesList[i].checked = false
            }
            storeCategoriesList[position].checked = true
            markerIcon = if (storeCategoriesList[position].map_image != null)
                storeCategoriesList[position].map_image!!
            else
                ""
            catName = storeCategoriesList[position].name!!
            adapter.submitList(storeCategoriesList)
            adapter.notifyDataSetChanged()

            viewModel.selectedStoreCatID.set(storeCategoriesList[position].id?.toInt())
            viewModel.selectedStoreCat.set(storeCategoriesList[position].status)
            viewModel.pageToken.set("")
            storeList.clear()
            storeListFilter.clear()
            storesAdapter.notifyDataSetChanged()
            viewModel.getStoresListing()
        }
    }

    fun calculationByDistance(StartP: LatLng, EndP: LatLng): Double {
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
