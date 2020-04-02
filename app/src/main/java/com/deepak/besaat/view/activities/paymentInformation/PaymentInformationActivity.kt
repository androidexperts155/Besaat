package com.deepak.besaat.view.activities.paymentInformation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityPaymentInformationBinding
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.signup.SignupActivity
import com.deepak.besaat.viewModel.PaymentInformationViewModel
import kotlinx.android.synthetic.main.activity_payment_information.*
import kotlinx.android.synthetic.main.activity_payment_information.rootLayout
import org.koin.android.ext.android.inject

class PaymentInformationActivity : BaseActivity() {
    lateinit var binding: ActivityPaymentInformationBinding
    val viewModel: PaymentInformationViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val sharedPref: SharedPref by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityPaymentInformationBinding>(
            this,
            R.layout.activity_payment_information
        )
        customToolBarWithBack(this, toolBar)
        binding.viewModel = viewModel
        textViewTitleName.text = getString(R.string.payment_information)
        initObserver()
    }

    fun initObserver() {
        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.showProgressBar(this@PaymentInformationActivity, getString(R.string.loading)
                )
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.onSuccessfullySignUp.observe(this, Observer {
            viewModel.onSuccessfullySignUp.observe(this, Observer {
                AllLocalHandler.userDetail.phone = ""
                AllLocalHandler.userDetail.name = ""
                AllLocalHandler.userDetail.imageUrl = ""
                AllLocalHandler.userDetail.email = ""
                AllLocalHandler.userDetail.fbId = ""
                AllLocalHandler.userDetail.googleId = ""
                AllLocalHandler.userDetail.instaId = ""
                AllLocalHandler.userDetail.image = null
                alertDialog(it.message!!)
            })
        })
    }

    private fun alertDialog(message: String) {
        val builder = AlertDialog.Builder(this!!)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
            startActivity(
                Intent(
                    this@PaymentInformationActivity,
                    SignupActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            dialogInterface.dismiss()
        }
        builder.show()
    }
}

