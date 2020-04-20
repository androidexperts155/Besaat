package com.deepak.besaat.view.activities.messagesList.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.deepak.besaat.R
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.FragmentFucntions
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.viewModel.MessageListViewModel
import org.koin.android.ext.android.inject

class MessagesListActivity : AppCompatActivity() {
    val fragmentFucntions: FragmentFucntions by inject()
    val viewModel: MessageListViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val sharedPref: SharedPref by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_list)
        initObserver()
    }

    fun initObserver() {

        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.showProgressBar(this, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.getResponse.observe(this, Observer {
            Log.e("data", "" + it)
        })

//        viewModel.feedBackMessage.observe(this, Observer {
//            commonFunctions.showFeedbackMessage(rootLayout, it)
//        })
    }
}
