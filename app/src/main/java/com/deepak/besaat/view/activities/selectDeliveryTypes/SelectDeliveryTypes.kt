package com.deepak.besaat.view.activities.selectDeliveryTypes

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivitySelectDeliveryTypesBinding
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.viewModel.DeliveryTypesViewModel
import kotlinx.android.synthetic.main.activity_select_delivery_types.*
import org.koin.android.ext.android.inject


class SelectDeliveryTypes : BaseActivity() {

    lateinit var binding: ActivitySelectDeliveryTypesBinding
    val viewModel: DeliveryTypesViewModel by inject()
    val commonFunctions:CommonFunctions by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivitySelectDeliveryTypesBinding>(
            this,
            R.layout.activity_select_delivery_types
        )
        binding.deliveryTypesViewModel = viewModel
        customToolBarWithBack(this, toolBar)
        initObserver()


    }



    fun initObserver() {
        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

    }
}
