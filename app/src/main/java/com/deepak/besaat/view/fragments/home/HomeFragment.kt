package com.deepak.besaat.view.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepak.besaat.R
import com.deepak.besaat.databinding.AaaaDemoBinding

import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.fragments.BaseFragment
import com.deepak.besaat.view.fragments.home.adapter.StoresAdapter
import com.deepak.besaat.viewModel.HomeFragmentViewModel
import kotlinx.android.synthetic.main.aaaa_demo.*

import org.koin.android.ext.android.inject

class HomeFragment : BaseFragment() {
    lateinit var binding: AaaaDemoBinding
    val viewModel: HomeFragmentViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val sharedPref: SharedPref by inject()

    lateinit var adapter: StoresAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<AaaaDemoBinding>(inflater, R.layout.aaaa_demo, container, false)
        binding.aaFragmentViewModel = viewModel
        viewModel.getStores(Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN))
        BaseActivity.textViewTitle.visibility = View.GONE
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
    }


    fun initObserver() {
        viewModel.progressBar.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it) {
                commonFunctions.showProgressBar(activity!!, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.feedBackMessage.observe(viewLifecycleOwner, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

        viewModel.successfullyGetStore.observe(viewLifecycleOwner, Observer {
            adapter!!.submitList(it.stores)
        })
    }


    private fun setAdapter() {
        adapter = StoresAdapter(viewModel)
        binding.recyclerViewStore.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewStore.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
//        BaseActivity.appBarLayout.background=null
        // removeObserver()

    }

    fun removeObserver() {
        viewModel.progressBar.removeObservers(this@HomeFragment)
        viewModel.feedBackMessage.removeObservers(this@HomeFragment)
    }
}