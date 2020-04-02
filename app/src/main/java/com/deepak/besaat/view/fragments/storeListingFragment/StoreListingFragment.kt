package com.deepak.besaat.view.fragments.storeListingFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepak.besaat.Interfaces.callMoreApi
import com.deepak.besaat.R
import com.deepak.besaat.databinding.FragmentStoreListingBinding
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.DividerItemDecoration
import com.deepak.besaat.utils.SharedPref
//import com.deepak.besaat.utils.DividerItemDecoration
import com.deepak.besaat.view.fragments.BaseFragment
import com.deepak.besaat.view.fragments.storeListingFragment.adapter.PaginationListener
import com.deepak.besaat.view.fragments.storeListingFragment.adapter.StoresListAdapter
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_store_listing.*
import org.koin.android.ext.android.inject
import java.text.DecimalFormat

class StoreListingFragment : BaseFragment() {
    lateinit var binding: FragmentStoreListingBinding
    lateinit var moreApi: callMoreApi
    var sourceDetails: MutableList<SourceDetails>? = ArrayList()
    var repository: Repository? = null
    var pageToken: String = ""
    var value: String = ""
    var name: String = ""
    var count: Int = 0
    var lati: String? = null
    var longi: String? = null
    var loadMore: Boolean = false
    val sharedPref: SharedPref by inject()
    var stringNoData: String = ""
    var relativeText: RelativeLayout? = null
    var noData: Boolean = false
    var noDataString: String = ""
    //var adaptor:StoreListDetialsAdaptor?=null

    var adapter: StoresListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentStoreListingBinding>(
            inflater,
            R.layout.fragment_store_listing,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        relativeText = view.findViewById(R.id.relativeText)
        if (noDataString == "") {
            relativeText?.visibility = View.GONE
            recyclerViewStoreListingAdapter.visibility = View.VISIBLE
        } else {
            relativeText?.visibility = View.VISIBLE
            recyclerViewStoreListingAdapter.visibility = View.GONE
        }

        if (this.sourceDetails!!.size>0) {
            relativeText?.visibility = View.GONE
            recyclerViewStoreListingAdapter.visibility = View.VISIBLE
        } else {
            relativeText?.visibility = View.VISIBLE
            recyclerViewStoreListingAdapter.visibility = View.GONE
        }

        setUpAdapter(this.sourceDetails!!)
    }

    /*  fun setList(servicesList: MutableList<SourceDetails>) {
          Log.e("response","response of list "+servicesList)
          var storeList=servicesList
          Log.e("response","response of list second "+storeList)
         // this.sourceDetails=servicesList
        //  adapter!!.addList(sourceDetails!!)
         // adapter!!.notifyDataSetChanged()

         setUpAdapter(storeList)
      }*/
    /* fun notifyAdaptor(){
         Log.e("response","adaptor has been notifired ")
         adaptor?.notifyDataSetChanged()
     }*/
    fun notifydatasetAndSetList(servicesList: MutableList<SourceDetails>) {
        adapter?.addList(servicesList)
        adapter?.notifyDataSetChanged()

        if (servicesList.size>0) {
            relativeText?.visibility = View.GONE
            recyclerViewStoreListingAdapter?.visibility = View.VISIBLE
        } else {
            relativeText?.visibility = View.VISIBLE
            recyclerViewStoreListingAdapter?.visibility = View.GONE
        }
    }

    fun notifyDataSetListAndPageToken(servicesList: MutableList<SourceDetails>, pageToken: String) {
        adapter?.addList(servicesList)
        adapter?.notifyDataSetChanged()
        this.pageToken = pageToken
    }

    fun onScrollMore() {
        // recyclerViewStoreListingAdapter.on
    }

    private fun setUpAdapter(servicesList: MutableList<SourceDetails>) {
        Log.e("response", "adaptor is  1st " + servicesList + " activitity  " + activity)
        Log.e(
            "response",
            "adaptor is  list  " + repository + " share prefrence  " + sharedPref + " value " + value
        )
        if (value == "") {
            //servicesList.sortBy { CalculationByDistance(LatLng()) }
            servicesList.sortByDescending { it.rating }
//            servicesList.reverse()
            adapter = StoresListAdapter(activity!!, servicesList, sharedPref, lati, longi)
        } else {
            var storeListFiltered: MutableList<SourceDetails> = ArrayList()
            // storeListFiltered.addAll(servicesList)
            // storeListFiltered.retainAll{ it.name.toString().toLowerCase().contains(value.toLowerCase()) }
            for (item in servicesList) {
                if (item.name.toString().toLowerCase().contains(value.toLowerCase())) {
                    storeListFiltered.add(item)
                }
            }
            storeListFiltered.sortedByDescending { it.rating }
//            storeListFiltered.reverse()
            // storeListFiltered.retainAll{ it.name.toString().toLowerCase().contains(value.toLowerCase()) }
            adapter = StoresListAdapter(activity!!, storeListFiltered, sharedPref, lati, longi)

            if (storeListFiltered.size>0) {
                relativeText?.visibility = View.GONE
                recyclerViewStoreListingAdapter.visibility = View.VISIBLE
            } else {
                relativeText?.visibility = View.VISIBLE
                recyclerViewStoreListingAdapter.visibility = View.GONE
            }
        }

        Log.e("response", "adaptor is 2nd   " + adapter)
        //var adaptor=StoreListDetialsAdaptor(activity!!,sourceDet
        // ails!!)
        //adaptor= sourceDetails?.let { StoreListDetialsAdaptor(activity!!, it) }
        recyclerViewStoreListingAdapter.layoutManager = LinearLayoutManager(activity)
        recyclerViewStoreListingAdapter.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.divider
                )
            )
        )
        // recyclerViewStoreListingAdapter.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(activity!!,R.drawable.divider)))
        recyclerViewStoreListingAdapter.adapter = adapter
        recyclerViewStoreListingAdapter.addOnScrollListener(object : PaginationListener(
            recyclerViewStoreListingAdapter.layoutManager as LinearLayoutManager
        ) {
            override fun isLastPage(): Boolean {
                if (value != "") {
                    return true
                }
                return pageToken == ""
                //return loadMore
            }

            override fun isLoading(): Boolean {
                if (value != "") {
                    return true
                }
                return pageToken == ""
                // return loadMore
            }

            override fun loadMoreItems() {
                Log.e("LoadMore", "load more being called " + pageToken.equals(""))
                pageToken = ""
                moreApi.more(true, name)
            }
        })
    }

    fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Double {
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

    companion object {
        @JvmStatic
        fun newInstance(
            sourceDetails: MutableList<SourceDetails>?,
            repository: Repository,
            pageToken: String,
            moreApi: callMoreApi,
            name: String,
            count: Boolean,
            lati: String?,
            longi: String?,
            valu: String,
            noData: String
        ): StoreListingFragment {
            var fragment = StoreListingFragment()
            fragment.sourceDetails = sourceDetails
            fragment.repository = repository
            fragment.pageToken = pageToken
            fragment.moreApi = moreApi
            fragment.name = name
            fragment.loadMore = count
            fragment.lati = lati
            fragment.longi = longi
            fragment.value = valu
            fragment.noDataString = noData
            return fragment
        }

        @JvmStatic
        fun newInstance(string: String): StoreListingFragment {

            var fragment: StoreListingFragment = StoreListingFragment()
            fragment.stringNoData = string
            return fragment

        }

        /* BlankFragment().apply {
             arguments = Bundle().apply {
                 putString(ARG_PARAM1, param1)
                 putString(ARG_PARAM2, param2)
             }
         }*/
    }
}