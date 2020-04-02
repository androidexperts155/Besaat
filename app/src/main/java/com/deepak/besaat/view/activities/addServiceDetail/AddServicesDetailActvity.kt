package com.deepak.besaat.view.activities.addServiceDetail

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityAddServicesDetailActvityBinding
import com.deepak.besaat.model.getServicesModel.Datum
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.addServiceDetail.adapter.ItemsAdapter
import com.deepak.besaat.view.activities.addServiceDetail.adapter.ServicesAdapter
import com.deepak.besaat.viewModel.AddServiceDetailViewModel
import kotlinx.android.synthetic.main.activity_add_services_detail_actvity.*
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.collections.ArrayList

class AddServicesDetailActvity : BaseActivity(), ServicesAdapter.ItemClickListener,
    ItemsAdapter.ItemClickHandler {

    val viewModel: AddServiceDetailViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val dateFunctions: DateFunctions by inject()
    val sharedPref: SharedPref by inject()
    lateinit var binding: ActivityAddServicesDetailActvityBinding
    var mPopupWindow: PopupWindow? = null
    var adapter: ServicesAdapter? = null

    var servicesList: MutableList<Datum> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityAddServicesDetailActvityBinding>(
            this,
            R.layout.activity_add_services_detail_actvity
        )
        binding.addServiceDetailViewModel = viewModel
        customToolBarWithBack(this, toolBar)
        viewModel.bothDeliveryTypes = intent.getBooleanExtra("bothDeliveryTypes", false)
        BaseActivity.textViewTitleName.setText(getString(R.string.add_service_detail))
        viewModel.getServices()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
    }

    fun initObserver() {
        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(rootLayout, it)
        })

        viewModel.progressBar.observe(this, Observer {
            if (it) {
                commonFunctions.showProgressBar(
                    this@AddServicesDetailActvity,
                    getString(R.string.loading)
                )
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.experienceClick.observe(this, Observer {
            if (it.second) {
                showPopUpExperienceWindow(it.first)
            }
        })

        viewModel.fromTimeClick.observe(this, Observer {
            timePickerDialog("From")
        })

        viewModel.toTimeClick.observe(this, Observer {
            timePickerDialog("To")
        })
        viewModel.successfullyGetServices.observe(this, Observer {
            servicesList.clear()
            servicesList = it.data!!
            var data = Datum()
            var data1 = Datum()
            data1.name = "All"
            data1.isChecked = false
            data.name = "Other"
            data.isChecked = false
            servicesList.add(data1)
            servicesList.add(data)
            setAdapter()
        })

    }

    private fun setAdapter() {
        adapter = ServicesAdapter(servicesList)
        adapter?.itemClickHandler(this)
//        recyclerViewServices.addItemDecoration(
//            DividerItemDecoration(
//                ContextCompat.getDrawable(
//                    this@AddServicesDetailActvity,
//                    R.drawable.divider
//                )
//            )
//        )
        binding.recyclerViewServices.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewServices.adapter = adapter
    }

    override fun onCheckBoxClick(isChekced: Boolean, datum: Datum, position: Int) {
        if (servicesList[position].name?.toLowerCase()!! == "all") {
            if (isChekced) {
                viewModel.isOtherVisible = true
                editTextOther.visibility = View.VISIBLE
                viewModel.selectedServicesList.clear()
                for (i in 0 until servicesList.size - 2) {
                    viewModel.selectedServicesList.add(servicesList[i])
                }
            } else {
                viewModel.isOtherVisible = false
                editTextOther.visibility = View.GONE
                viewModel.selectedServicesList.clear()
            }
        }
        servicesList[position].isChecked = isChekced
        if (servicesList[position].name?.toLowerCase()!!.contains("other")) {
            if (isChekced) {
                viewModel.selectedServicesList.add(datum)
                viewModel.isOtherVisible = true
                editTextOther.visibility = View.VISIBLE
            } else {
                for (i in 0 until servicesList.size) {
                    if (servicesList[i].name == "All") {
                        servicesList[i].isChecked = isChekced
                    }
                }
                viewModel.selectedServicesList.remove(datum)
                viewModel.isOtherVisible = false
                editTextOther.visibility = View.GONE
            }
        } else {
            if (isChekced) {
                viewModel.selectedServicesList.add(datum)
            } else {
                for (i in 0 until servicesList.size) {
                    if (servicesList[i].name == "All") {
                        servicesList[i].isChecked = isChekced
                    }
                }
                viewModel.selectedServicesList.remove(datum)
            }
        }
        setAdapter()
    }

    override fun OnItemClick(position: Int, providerExperience: String) {
        mPopupWindow!!.dismiss()
        viewModel.providerExperience.set(providerExperience)
    }

    fun showPopUpExperienceWindow(view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        var adapter = ItemsAdapter(this@AddServicesDetailActvity)
        adapter.OnItemClickListener(this)
        val customView = LayoutInflater.from(this@AddServicesDetailActvity)
            .inflate(R.layout.view_pop_window, null)
        val recylcerView = customView.findViewById<RecyclerView>(R.id.recyclerView)
        recylcerView.setLayoutManager(LinearLayoutManager(this@AddServicesDetailActvity))
        recylcerView.setAdapter(adapter)

        mPopupWindow = PopupWindow(
            customView,
            view.getWidth(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mPopupWindow!!.setOutsideTouchable(true)

        val displayMetrics = getResources().displayMetrics
        val height = displayMetrics.heightPixels * 2 / 3
        if (location[1] > height) {
            mPopupWindow!!.showAsDropDown(view, 0, -320);
        } else {
            mPopupWindow!!.showAsDropDown(view, 0, 0)
        }
    }


    fun timePickerDialog(type: String) {
        var mcurrentTime = Calendar.getInstance();
        var hour = mcurrentTime!!.get(Calendar.HOUR_OF_DAY);
        var minute = mcurrentTime!!.get(Calendar.MINUTE);

        var mTimePicker = TimePickerDialog(
            this@AddServicesDetailActvity,
            object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(p0: TimePicker?, selectedHour: Int, minute: Int) {

                    var newHours = ""
                    var newMinutes = ""
                    if (selectedHour < 10) {
                        newHours = "0" + selectedHour.toString()
                    } else {
                        newHours = selectedHour.toString()
                    }
                    if (minute < 10) {
                        newMinutes = "0" + minute.toString()
                    } else {
                        newMinutes = minute.toString()
                    }
                    if (type.equals("From")) {
                        viewModel.from.set(dateFunctions.tweleveHoursFormat(newHours + ":" + newMinutes))
                    } else {
                        viewModel.to.set(dateFunctions.tweleveHoursFormat(newHours + ":" + newMinutes))
                    }
                }
            },
            hour,
            minute,
            false
        );
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
}


