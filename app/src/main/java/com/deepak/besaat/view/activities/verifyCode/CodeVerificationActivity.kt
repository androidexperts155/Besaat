package com.deepak.besaat.view.activities.verifyCode

import android.os.Bundle
import com.deepak.besaat.R
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import kotlinx.android.synthetic.main.activity_code_verification.*
import android.view.KeyEvent
import android.view.View
import com.deepak.besaat.view.activities.BaseActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.deepak.besaat.databinding.ActivityCodeVerificationBinding
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.viewModel.CodeVerificationViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import org.koin.android.ext.android.inject

class CodeVerificationActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    lateinit var binding: ActivityCodeVerificationBinding
    val viewModel:CodeVerificationViewModel by inject()
    val commonFunctions:CommonFunctions by inject()

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityCodeVerificationBinding>(
            this,
            R.layout.activity_code_verification
        )
        binding.codeVerificatonViewModel=viewModel
        setUpData()

        customizeEditText()
        customToolBarWithBack(this, toolBar)
       /* startSMSListener()*/
        /*GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .enableAutoManage(this, this)
            .addApi(Auth.CREDENTIALS_API)
            .build()*/
        /*  linearLayoutSubmit.setOnClickListener {
              startActivity(Intent(this@CodeVerificationActivity,CreateProfileActivity::class.java))
          }*/
        initObserver()
    }

    fun setUpData(){
        viewModel.otp=intent.getStringExtra("otp")
        viewModel.phoneNumber=intent.getStringExtra("phoneNumber")
        viewModel.phoneCode=intent.getStringExtra("phoneCode")
        viewModel.from=intent.getStringExtra("from")
        viewModel.isExist=intent.getStringExtra("isExist")

    }

    fun initObserver() {
        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.showProgressBar(this@CodeVerificationActivity, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

    }

    private fun customizeEditText() {
        editTextOne.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (editTextOne.text.length > 0)
                    editTextTwo.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}

        })
        editTextTwo.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (editTextTwo.text.toString().length > 0) {
                    editTextThree.requestFocus()
                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
            }
        })

        editTextThree.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (editTextThree.text.length > 0) {
                    editTextFour.requestFocus()
                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}

        })
        editTextFour.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}

        })


        editTextTwo.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(p0: View?, keyCode: Int, p2: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editTextTwo.getText().length === 0)
                        editTextOne.requestFocus()
                }
                return false

            }

        })
        editTextThree.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editTextThree.getText().length == 0)
                        editTextTwo.requestFocus()
                }
                return false
            }
        })
        editTextFour.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editTextFour.getText().length == 0)
                        editTextThree.requestFocus()
                }
                return false
            }
        })
    }


    fun startSMSListener() {
        val mClient = SmsRetriever.getClient(this)
        val mTask = mClient.startSmsRetriever()
        mTask.addOnSuccessListener {
            Log.e("success", "true")
        }
        mTask.addOnFailureListener {
            Log.e("fail", "true")
        }
    }

}
