package com.deepak.besaat.view.fragments.serviceProviderProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.FragmentCustomerProfileBinding
import com.deepak.besaat.databinding.FragmentServiceProviderProfileBinding
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.fragments.BaseFragment
import com.deepak.besaat.viewModel.ServiceProviderProfileViewModel
import kotlinx.android.synthetic.main.fragment_service_provider_profile.*

import org.koin.android.ext.android.inject

class ServiceProviderProfile : BaseFragment() {
    lateinit var binding: FragmentServiceProviderProfileBinding
    val viewModel: ServiceProviderProfileViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val sharedPref: SharedPref by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentServiceProviderProfileBinding>(
            inflater,
            R.layout.fragment_service_provider_profile,
            container,
            false
        )
        BaseActivity.textViewTitle.visibility = View.VISIBLE
        BaseActivity.textViewTitle.setText(getString(R.string.my_profile))
        activity?.resources?.getColor(R.color.colorAccent)?.let {
            HomeActivity.homeRelative.setBackgroundColor(
                it
            )
        }
        binding.serviceProviderModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserProfile(Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN))
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
           sharedPref.setLong(Constants. USER_ID,it.data!!.id!!)
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
            binding.userDetail= it
        })
    }

}