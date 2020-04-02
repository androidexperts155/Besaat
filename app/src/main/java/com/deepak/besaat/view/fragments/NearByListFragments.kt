package com.deepak.besaat.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.Interfaces.Selected

import com.deepak.besaat.R
import com.deepak.besaat.model.providerSignUpDetailToServer.personAddedModel
import com.deepak.besaat.utils.DividerItemDecoration
import com.deepak.besaat.view.activities.newRequestStore.adapter.DriverAdapter
import com.deepak.besaat.view.fragments.MyVisitFragment.VisitListAdaptor

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NearByListFragments.newInstance] factory method to
 * create an instance of this fragment.
 */
class NearByListFragments : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var recyclerView: RecyclerView?=null
  lateinit var  selected :Selected
    var storeList: MutableList<personAddedModel>?=null
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
        var view=inflater.inflate(R.layout.fragment_near_by_list_fragments, container, false)
        recyclerView=view.findViewById(R.id.recyclerViewStoreListingAdapter)
        var  adaptor= storeList?.let { DriverAdapter(it) }
       adaptor?.setInerface(object:Selected{
           override fun selected(sucess: Boolean) {
               selected.selected(sucess)
           }
       })
        recyclerView?.layoutManager= LinearLayoutManager(activity!!)
        recyclerView?.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(activity!!,R.drawable.divider)))
        // recyclerViewStoreListingAdapter.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(activity!!,R.drawable.divider)))
        recyclerView?.adapter=adaptor
        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NearByListFragments.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun   newInstance(storeList: MutableList<personAddedModel>?,selected :Selected): NearByListFragments{
            var fragment : NearByListFragments = NearByListFragments()
            fragment.storeList=storeList
            fragment.selected=selected
            // fragment.toolBar=toolBar
            return fragment
        }
    }
}
