package com.deepak.besaat.view.fragments.Past


import android.app.AlertDialog
import android.content.Intent
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
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.DividerItemDecoration
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.fragments.MyVisitFragment.VisitListAdaptor
import com.deepak.besaat.viewModel.PastDetailsViewModel
import kotlinx.android.synthetic.main.fragment_past.*
import org.koin.android.ext.android.inject
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PastFragment : Fragment(), iFragmentCommunicator {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var departureDate: String
    lateinit var departureCountry: String
    lateinit var arrivalDate: String
    lateinit var arrivalCountry: String
    lateinit var imageFile: File
    lateinit var tvNoTripText: TextView
    var tripPastList: MutableList<upcomingTripModel> = ArrayList()
    var recyclerView: RecyclerView? = null
    val sharedPref: SharedPref by inject()
    val viewModel: PastDetailsViewModel by inject()
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
        var view = inflater.inflate(R.layout.fragment_past, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewStoreListingAdapter)
        tvNoTripText = view.findViewById(R.id.tvNoTripText)
        var adaptor = VisitListAdaptor(activity!!, tripPastList, 2)
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

        if (tripPastList.isEmpty()) {
            tvNoTripText.visibility = View.VISIBLE
        } else {
            tvNoTripText.visibility = View.GONE
        }


        //viewModel.AddNewTrip(Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),departureCountry,departureDate,arrivalDate,arrivalCountry,imageFile)
        // viewModel.pastTrip(Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),sharedPref.getLong(Constants.USER_ID))
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(pastList: ArrayList<upcomingTripModel>): PastFragment {
            var fragment: PastFragment = PastFragment()
            fragment.tripPastList = pastList
            /// fragment.toolBar=toolBar
            return fragment
        }
    }

    var iFragmentCommunicatorMain: iFragmentCommunicator? = null
    public fun attachIFragmentCommunicatorMain(iFragmentCommunicatorMain: iFragmentCommunicator) {
        this.iFragmentCommunicatorMain = iFragmentCommunicatorMain
    }

    override fun passData(position: Int, status: Int, value: String) {
        if (iFragmentCommunicatorMain != null) {
            iFragmentCommunicatorMain?.passData(position, status, value)
        }
//        if (status == 1 && value == "past") {
//            showAlert()
//        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Alert")
        builder.setMessage("Do you want to delete this trip?")
        builder.setPositiveButton("Delete") { dialog, which ->
            dialog.cancel()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.setCancelable(false)
        builder.show()
    }
}
