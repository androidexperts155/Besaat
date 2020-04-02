package com.deepak.besaat.view.fragments.Upcoming


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.Interfaces.iFragmentCommunicator

import com.deepak.besaat.R
import com.deepak.besaat.model.upcomingTripModel.upcomingTripModel
import com.deepak.besaat.utils.DividerItemDecoration
import com.deepak.besaat.utils.FragmentFucntions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.fragments.MyVisitFragment.VisitListAdaptor
import com.deepak.besaat.viewModel.UpcomingDetailsViewModel
import com.jdev.countryutil.Country
import org.koin.android.ext.android.inject
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UpcomigFragment : Fragment(), iFragmentCommunicator {
    private var param1: String? = null
    private var param2: String? = null
    var departureDate: String? = null
    var departureTime: String? = null
    var arrivalDate: String? = null
    var arrivalTime: String? = null
    var imageFile: File? = null
    lateinit var tvNoTripText: TextView
    var tripUpcomigList: MutableList<upcomingTripModel> = ArrayList()

    val fragmentFucntion: FragmentFucntions by inject()
    var recyclerView: RecyclerView? = null
    val sharedPref: SharedPref by inject()
    val viewModel: UpcomingDetailsViewModel by inject()

    var iFragmentCommunicatorMain: iFragmentCommunicator? = null
    public fun attachIFragmentCommunicatorMain(iFragmentCommunicatorMain: iFragmentCommunicator) {
        this.iFragmentCommunicatorMain = iFragmentCommunicatorMain
    }

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
        var view = inflater.inflate(R.layout.fragment_upcomig, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewStoreListingAdapter)
        tvNoTripText = view.findViewById(R.id.tvNoTripText)
        var adaptor = VisitListAdaptor(activity!!, tripUpcomigList, 1)
        adaptor.attachIFragmentCommunicator(this)

        recyclerView?.layoutManager = LinearLayoutManager(activity!!)
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.divider
                )
            )
        )
        // recyclerViewStoreListingAdapter.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(activity!!,R.drawable.divider)))
        recyclerView?.adapter = adaptor
        if (tripUpcomigList.isEmpty()) {
            tvNoTripText.visibility = View.VISIBLE
        } else {
            tvNoTripText.visibility = View.GONE
        }
        //viewModel.upcomigTrip(Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),281)
        // viewModel.AddNewTrip(Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN))
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(tripUpcomigList: ArrayList<upcomingTripModel>): UpcomigFragment {
            var fragment: UpcomigFragment = UpcomigFragment()
            fragment.tripUpcomigList = tripUpcomigList
            /// fragment.toolBar=toolBar

            return fragment

        }
    }

    override fun passData(position: Int, status: Int, value: String) {
        if (iFragmentCommunicatorMain != null) {
            iFragmentCommunicatorMain?.passData(position, status, value)
        }
    }
}
