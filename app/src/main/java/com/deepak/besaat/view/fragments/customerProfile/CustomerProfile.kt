package com.deepak.besaat.view.fragments.customerProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepak.besaat.R
import com.deepak.besaat.databinding.FragmentCustomerProfileBinding
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.fragments.BaseFragment
import com.deepak.besaat.view.fragments.customerProfile.adapter.PaymentMethodsAdapter
import com.deepak.besaat.viewModel.CustomerProfileViewModell
import kotlinx.android.synthetic.main.fragment_customer_profile.*
import org.koin.android.ext.android.inject

class CustomerProfile : BaseFragment() {
    val viewModel: CustomerProfileViewModell by inject()
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()

    lateinit var binding: FragmentCustomerProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCustomerProfileBinding>(
            inflater,
            R.layout.fragment_customer_profile,
            container,
            false
        )
        activity?.resources?.getColor(R.color.colorAccent)?.let {
            HomeActivity.homeRelative.setBackgroundColor(
                it
            )
        }
        BaseActivity.textViewTitle.visibility = View.VISIBLE
        BaseActivity.textViewTitle.setText(getString(R.string.my_profile))
        BaseActivity.textViewTitle.textSize = 20f
        binding.customerProfileViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        viewModel.getUserProfile(Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN))
    }


    private fun setAdapter() {
        var adapter = PaymentMethodsAdapter()
        recyclerViewPaymentMethods.layoutManager = LinearLayoutManager(activity!!)
        recyclerViewPaymentMethods.adapter = adapter
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

        viewModel.successfullyGetUserProfile.observe(viewLifecycleOwner, Observer {


            sharedPref.setLong(Constants.USER_ID, it.data!!.id!!)

            // Log.e("Response","share prefrence "+it.data!!.id+"       "+sharedPref.getLong(Constants. USER_ID))

            viewModel.name.set(it.data!!.name!!)
            viewModel.email.set(it.data!!.email!!)
            viewModel.phone.set(
                it.data!!.phone!!.substring(
                    it.data!!.phone!!.indexOf("-") + 1,
                    it.data!!.phone!!.length
                )
            )
            ccp2.setCountryForPhoneCode(
                it.data!!.phone!!.substring(
                    it!!.data!!.phone!!.indexOf("+") + 1,
                    it.data!!.phone!!.indexOf("-")
                ).toInt()
            )
            binding.userDetail = it
        })
    }

}