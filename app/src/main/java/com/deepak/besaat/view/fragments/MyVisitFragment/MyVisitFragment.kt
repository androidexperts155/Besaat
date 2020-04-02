package com.deepak.besaat.view.fragments.MyVisitFragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.Interfaces.CloseOpenDrawerInterface
import com.deepak.besaat.Interfaces.InterfaceBackAlert
import com.deepak.besaat.Interfaces.iFragmentCommunicator

import com.deepak.besaat.R
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.model.addTripModel
import com.deepak.besaat.model.upcomingTripModel.upcomingTripModel
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.customerSupport.model.CustomerSupportModel
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.fragments.AddTrip.AddTripFragment
import com.deepak.besaat.view.fragments.Past.PastFragment
import com.deepak.besaat.view.fragments.Upcoming.UpcomigFragment
import com.deepak.besaat.viewModel.UpcomingDetailsViewModel
import kotlinx.android.synthetic.main.fragment_my_visit.*

import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyVisitFragment : Fragment(), iFragmentCommunicator {
    private var param1: String? = null
    private var param2: String? = null
    val fragmentFucntion: FragmentFucntions by inject()
    val viewModel: UpcomingDetailsViewModel by inject()
    val commonFunctions: CommonFunctions by inject()
    val sharedPref: SharedPref by inject()
    var addText: TextView? = null
    var cardOne: CardView? = null
    var cardTwo: CardView? = null
    var textCardOne: TextView? = null
    var textCardTwo: TextView? = null
    var recyclerView: RecyclerView? = null
    var imageViewFilter: ImageView? = null
    var RelativePopUp: RelativeLayout? = null
    var RelativePopUp2: RelativeLayout? = null
    var adapter: VisitListAdaptor? = null
    var frameLayout: FrameLayout? = null
    var relativeclick: RelativeLayout? = null
    var storeList: MutableList<SourceDetails> = ArrayList()
    var tripList: MutableList<addTripModel> = ArrayList()
    var dialogSelectUpcoming: MutableList<String> = ArrayList()
    var dialogSelectPast: MutableList<String> = ArrayList()
    var tripUpcomigList: MutableList<upcomingTripModel> = ArrayList()
    var tripPastList: MutableList<upcomingTripModel> = ArrayList()
    var clickBool: Boolean? = false
    var cardClick: Boolean? = false
    var pastList: Boolean? = false
    var upcomigList: Boolean? = false
    var radioGroup: RadioGroup? = null
    var radioGroup2: RadioGroup? = null
    var radioButton: RadioButton? = null
    var dialogSelect: String? = null
    var dialogSelectPastString: String = "0"
    var textContinue: TextView? = null
    var textContinue2: TextView? = null
    var linearCointainer: LinearLayout? = null
    var linearClick: LinearLayout? = null
    var interfaceBackAlert: InterfaceBackAlert? = null
    var imageCross1: ImageView? = null
    var imageCross2: ImageView? = null
    var toolBar: Toolbar? = null
    var dialogInt: Int? = 0
    var textNoTrip: TextView? = null
    var adaptor: VisitListAdaptor? = null
    var arrayStringDeparture: List<String>? = null
    var arrayStringArrival: List<String>? = null
    var textReset1: TextView? = null
    var textReset2: TextView? = null

    var clickedPosition: Int = -1
    var clickedFromList: String = ""

    lateinit var closeOpenDrawerInterface: CloseOpenDrawerInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        closeOpenDrawerInterface = context as CloseOpenDrawerInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_my_visit, container, false)
        addText = view.findViewById(R.id.textView39)
        cardOne = view.findViewById(R.id.cardOut)
        cardTwo = view.findViewById(R.id.cardOut2)
        textCardOne = view.findViewById(R.id.txtOut)
        textCardTwo = view.findViewById(R.id.txtOut2)
        imageViewFilter = view.findViewById(R.id.imageViewFilter)
        RelativePopUp = view.findViewById(R.id.popuplayout)
        RelativePopUp2 = view.findViewById(R.id.popuplayout2)
        radioGroup = view.findViewById(R.id.groupradio)
        radioGroup2 = view.findViewById(R.id.groupradio2)
        textContinue = view.findViewById(R.id.textViewContinue)
        textContinue2 = view.findViewById(R.id.textViewContinue2)
        relativeclick = view.findViewById(R.id.relativeClick)
        linearClick = view.findViewById(R.id.linearClick)
        textNoTrip = view.findViewById(R.id.tripText)
        radioButton = view.findViewById(R.id.radia_id1)
        imageCross1 = view.findViewById(R.id.imageCross1)
        imageCross2 = view.findViewById(R.id.imageCross2)
        textReset1 = view.findViewById(R.id.textreset1)
        textReset2 = view.findViewById(R.id.textreset2)
        dialogSelect = "0"

        RelativePopUp?.visibility = View.GONE
        RelativePopUp2?.visibility = View.GONE
        textReset1?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                radioGroup?.check(R.id.radia_id111)
                dialogSelect = "0"

            }
        })
        textReset2?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                radioGroup2?.check(R.id.radia_id2111)
                dialogSelectPastString = "0"
            }
        })
        textContinue?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                HomeActivity.bottomNaigationLinear.visibility = View.VISIBLE
                clickBool = false

                RelativePopUp?.visibility = View.GONE
                // if(cardClick ==false){

                if (!tripList.isEmpty()) {
                    if (tripList.get(0).userID != null) {
                        var strindId = tripList.get(0).userID
                        viewModel.upcomigTrip(
                            Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                            strindId?.toLong()
                        )
                    } else {
                        viewModel.upcomigTrip(
                            Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                            sharedPref.getLong(Constants.USER_ID)
                        )
                    }
                } else {
                    viewModel.upcomigTrip(
                        Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                        sharedPref.getLong(Constants.USER_ID)
                    )
                }

                /*}else{

                    if(!tripList.isEmpty()) {
                        if (tripList.get(0).userID != null) {
                            var strindId= tripList.get(0).userID
                            viewModel.pastTrip(
                                Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN), strindId?.toLong())

                        } else {

                            viewModel.pastTrip(
                                Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                                sharedPref.getLong(Constants.USER_ID)
                            )
                        }
                    }else{
                        viewModel.pastTrip(
                            Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                            sharedPref.getLong(Constants.USER_ID)
                        )
                    }


                }*/
            }
        })
        imageCross1?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                HomeActivity.bottomNaigationLinear.visibility = View.VISIBLE
                clickBool = false

                RelativePopUp?.visibility = View.GONE

            }
        })
        imageCross2?.setOnClickListener(object : View.OnClickListener {

            override fun onClick(p0: View?) {
                clickBool = false
                HomeActivity.bottomNaigationLinear.visibility = View.VISIBLE
                RelativePopUp2?.visibility = View.GONE
            }
        })

        textContinue2?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                clickBool = false
                HomeActivity.bottomNaigationLinear.visibility = View.VISIBLE
                RelativePopUp2?.visibility = View.GONE
                if (!tripList.isEmpty()) {
                    if (tripList.get(0).userID != null) {
                        var strindId = tripList.get(0).userID
                        viewModel.pastTrip(
                            Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                            strindId?.toLong()
                        )

                    } else {

                        viewModel.pastTrip(
                            Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                            sharedPref.getLong(Constants.USER_ID)
                        )
                    }
                } else {
                    viewModel.pastTrip(
                        Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                        sharedPref.getLong(Constants.USER_ID)
                    )
                }
            }
        })
        BaseActivity.imageViewMenu.setImageResource(R.drawable.ic_menu_2)

        BaseActivity.imageViewMenu.setOnClickListener(object : View.OnClickListener {

            override fun onClick(p0: View?) {
                // HomeActivity.closeOrOpenDrawer()

                closeOpenDrawerInterface.Sucess(true)
            }
        })
        radioGroup?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {

            override fun onCheckedChanged(group: RadioGroup?, viewID: Int) {
                if (viewID == R.id.radia_id111) {

                    dialogSelect = "0"
                    dialogInt = 0


                }
                if (viewID == R.id.radia_id11) {

                    dialogInt = 1

                    dialogSelect = "1"
                    dialogInt = 0


                }
                if (viewID == R.id.radia_id1) {


                    dialogSelect = "2"
                    dialogInt = 0


                    dialogInt = 2


                }

                if (viewID == R.id.radia_id2) {


                    dialogSelect = "3"
                    dialogInt = 0

                    dialogInt = 3


                }

            }
        })


        radioGroup2?.setOnCheckedChangeListener { group, viewID ->
            if (viewID == R.id.radia_id2111) {
                dialogSelectPastString = "0"
            }
            if (viewID == R.id.radia_id211) {
                dialogInt = 1
                dialogSelectPastString = "1"
            }
            if (viewID == R.id.radia_id21) {
                dialogSelectPastString = "2"
                dialogInt = 2
            }

            if (viewID == R.id.radia_id22) {
                dialogSelectPastString = "3"
                dialogInt = 3
            }
        }
        //  radioGroup.setOnCheckedChangeListener(object:View.O)

        //   linearCointainer=view.findViewById(R.id.)
        //   recyclerView=view.findViewById(R.id.recyclerViewAdapter)

        frameLayout = view.findViewById(R.id.frameClickablity)
        // BaseActivity.appBarLayout.background=resources?.getDrawable(R.drawable.bg_voilet_bl_round_40)
        //HomeActivity.textTop.text="My Visits"
        BaseActivity.textViewTitle.text = "My Visits"
        BaseActivity.textViewTitle.textSize = 20f


        //  BaseActivity.topRelative.background=null

        //   VisitActivity.imageViewTopLeft.setImageResource(R.drawable.ic_menu)
        // VisitActivity.textTop.text="My Visits"


        /*  if(cardClick ==false){
               adaptor=VisitListAdaptor(activity!!,tripPastList,1)
          }else{
              adaptor=VisitListAdaptor(activity!!,tripUpcomigList,2)


          }*/

        /* recyclerView?.layoutManager= LinearLayoutManager(activity)
         recyclerView?.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(activity!!,R.drawable.divider)))
         recyclerView?.adapter=adapter*/
        if (!tripList.isEmpty()) {
            var str = tripList.get(0).departureCountry
            var str2 = tripList.get(0).arrivalCountryl
            arrayStringDeparture = str?.split(("\\s+").toRegex())
            arrayStringArrival = str2?.split(("\\s+").toRegex())

            if (arrayStringDeparture?.size == 1) {


            } else {


            }
            if (arrayStringDeparture?.size == 1) {


            } else {


            }

        }

        if (!tripList.isEmpty()) {
            if (tripList.get(0).userID != null) {
                var strindId = tripList.get(0).userID
                viewModel.upcomigTrip(
                    Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                    strindId?.toLong()
                )

            } else {

                viewModel.upcomigTrip(
                    Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                    sharedPref.getLong(Constants.USER_ID)
                )
            }
        } else {
            viewModel.upcomigTrip(
                Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                sharedPref.getLong(Constants.USER_ID)
            )
        }
        // adapter=  VisitListAdaptor(activity!!,storeList!!)
        addText?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                var addTripFragment = AddTripFragment.newInstance()
                fragmentFucntion.replaceFragment2(
                    context as AppCompatActivity,
                    addTripFragment,
                    R.id.container
                )
            }
        })
        radioButton?.text = "Active"
        cardOne?.background = activity?.resources?.getDrawable(R.drawable.bg_black_all_side_four_dp)
        activity?.resources?.getColor(R.color.white)?.let { textCardOne?.setTextColor(it) }
        //cardOne?.background=activity?.resources?.getDrawable(R.drawable.bg_white_all_side_four_dp_round)
        cardTwo?.background =
            activity?.resources?.getDrawable(R.drawable.bg_light_white_all_side_four_dp)
        activity?.resources?.getColor(R.color.quantum_black_text)?.let {
            textCardTwo?.setTextColor(
                it
            )
        }

        cardOne?.setOnClickListener {
            if (activity?.supportFragmentManager?.fragments?.last() is PastFragment) {
                commonFunctions.showFeedbackMessage(container2, "Loading...")
                cardClick = false
                cardOne?.background =
                    activity?.resources?.getDrawable(R.drawable.bg_black_all_side_four_dp)
                activity?.resources?.getColor(R.color.white)?.let { textCardOne?.setTextColor(it) }

                cardTwo?.background =
                    activity?.resources?.getDrawable(R.drawable.bg_light_white_all_side_four_dp)
                activity?.resources?.getColor(R.color.quantum_black_text)?.let {
                    textCardTwo?.setTextColor(
                        it
                    )
                }

                if (tripList.isNotEmpty()) {
                    if (tripList[0].userID != null) {
                        var strindId = tripList[0].userID
                        viewModel.upcomigTrip(
                            Constants.BEARER + " " + sharedPref.getString(
                                Constants.TOKEN
                            ), strindId?.toLong()
                        )
                    } else {
                        viewModel.upcomigTrip(
                            Constants.BEARER + " " + sharedPref.getString(
                                Constants.TOKEN
                            ), sharedPref.getLong(Constants.USER_ID)
                        )
                    }
                } else {
                    tripUpcomigList.clear()
                    var upcoming =
                        UpcomigFragment.newInstance(tripUpcomigList as ArrayList<upcomingTripModel>)
                    fragmentFucntion.replaceFragment(activity, upcoming, R.id.container2)
                    upcoming.attachIFragmentCommunicatorMain(this)
                    viewModel.upcomigTrip(
                        Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                        sharedPref.getLong(Constants.USER_ID)
                    )
                }
            }
            //fragmentFucntion.removeFragment(context as AppCompatActivity, AddTripFragment, R.id.cont)
        }
        cardTwo?.setOnClickListener {
            if (activity?.supportFragmentManager?.fragments?.last() is UpcomigFragment) {
                commonFunctions.showFeedbackMessage(container2, "Loading..")
                cardClick = true
                // radioButton?.setText("Completed")
                cardOne?.background =
                    activity?.resources?.getDrawable(R.drawable.bg_light_white_all_side_four_dp)
                activity?.resources?.getColor(R.color.quantum_black_text)?.let {
                    textCardOne?.setTextColor(
                        it
                    )
                }

                cardTwo?.background =
                    activity?.resources?.getDrawable(R.drawable.bg_black_all_side_four_dp)
                activity?.resources?.getColor(R.color.white)?.let { textCardTwo?.setTextColor(it) }

                if (tripList.isNotEmpty()) {
                    if (tripList[0].userID != null) {
                        var stringId = tripList[0].userID
                        // viewModel.upcomigTrip(Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),tripList.get(0).userID)
                        viewModel.pastTrip(
                            Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                            stringId?.toLong()
                        )

                    } else {
                        viewModel.pastTrip(
                            Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                            sharedPref.getLong(Constants.USER_ID)
                        )
                    }
                } else {
                    var past =
                        PastFragment.newInstance(tripPastList as ArrayList<upcomingTripModel>)
                    fragmentFucntion.replaceFragment(activity, past, R.id.container2)
                    past.attachIFragmentCommunicatorMain(this)

                    viewModel.pastTrip(
                        Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                        sharedPref.getLong(Constants.USER_ID)
                    )
                }
            }
        }

        BaseActivity.textViewTitle.text = "My Visits"
        activity?.resources?.getColor(R.color.white)?.let {
            BaseActivity.textViewTitle.setTextColor(it)
        }
        /*VisitActivity.imageViewTopLeft.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                fragmentFucntion.removeFragment(activity as AppCompatActivity,R.id.frameContainer)
            }
        })*/

        linearClick?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (clickBool == false) {
                    Log.e("clcikVisiblity", "clcikvisiblity is  " + clickBool)
                    clickBool = true
                    HomeActivity.bottomNaigationLinear.visibility = View.GONE
                    if (cardClick == false) {
                        RelativePopUp?.visibility = View.VISIBLE
                    } else {
                        RelativePopUp2?.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("clcikVisiblity", "clcikvisiblity is  " + clickBool)
                    HomeActivity.bottomNaigationLinear.visibility = View.VISIBLE
                    clickBool = false
                    if (cardClick == false) {
                        RelativePopUp?.visibility = View.GONE
                    } else {
                        RelativePopUp2?.visibility = View.GONE
                    }
                }
            }
        })
        /*  imageViewFilter?.setOnClickListener(object :View.OnClickListener{


              override fun onClick(view: View?) {
                 if(clickBool==false){
                     Log.e("clcikVisiblity","clcikvisiblity is  "+clickBool)
                     clickBool=true
                     linearPopUp?.visibility=View.VISIBLE
                 }else{
                     Log.e("clcikVisiblity","clcikvisiblity is  "+clickBool)
                     clickBool=false
                     linearPopUp?.visibility=View.GONE
                 }
              }
          })*/
        frameLayout?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                clickBool = false
                RelativePopUp?.visibility = View.GONE
            }
        })




        intiObservers()
        // Inflate the layout for this fragment
        return view
    }

    private fun intiObservers() {
        viewModel.pastTripResponse.observe(this, androidx.lifecycle.Observer {


            handleResponsePast(it)

        })
        viewModel.upcomigTripResponse.observe(this, androidx.lifecycle.Observer {
            handleResponsePresent(it)
        })
        viewModel.feedBackMessage.observe(viewLifecycleOwner, Observer {
            commonFunctions.showFeedbackMessage(frameClickablity, it)
        })


        viewModel.deleteTripResponse.observe(this, androidx.lifecycle.Observer {
            handleResponseDeleteTrip(it)
        })

        viewModel.progressBar.observe(this, Observer<Boolean> {
            /* val currentFragment = supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
             if (currentFragment !is StoreListingFragment) {
                 storeListFragment=StoreListingFragment.newInstance(storeList,viewModel.repository)
                 Log.e("MapIcon","list map icon click is true ")
                 imageViewMapList.setImageResource(R.drawable.ic_map)
                 fragmentFucntions.replaceFragment(this, storeListFragment!!, R.id.listOrMapContainer)
             } else {
                 var storeMapFragment:StoreMapFragment=StoreMapFragment.newInstance(storeList)
                 Log.e("MapIcon","list map icon click is false ")
                 imageViewMapList.setImageResource(R.drawable.ic_list)
                 fragmentFucntions.replaceFragment(this, storeMapFragment, R.id.listOrMapContainer)
             }*/
            //  fragmentFucntions.removeAllFragments(this)
//            if (it) {
//                commonFunctions.showProgressBar(activity!!, getString(R.string.loading))
//            } else {
//                commonFunctions.hideProgressBar()
//            }

        })
    }

    private fun handleResponseDeleteTrip(it: CustomerSupportModel) {
        if (it.status!!) {
            if (clickedFromList == "up") {
                tripUpcomigList.removeAt(clickedPosition)
                var upcoming =
                    UpcomigFragment.newInstance(tripUpcomigList as ArrayList<upcomingTripModel>)
                fragmentFucntion.replaceFragment(activity, upcoming, R.id.container2)
                upcoming.attachIFragmentCommunicatorMain(this)
            } else {
                tripPastList.removeAt(clickedPosition)
                var past = PastFragment.newInstance(tripPastList as ArrayList<upcomingTripModel>)
                fragmentFucntion.replaceFragment(activity, past, R.id.container2)
                past.attachIFragmentCommunicatorMain(this)
            }

        }
        commonFunctions.showFeedbackMessage(container2, it.message!!)
        Log.e("Delete", "Delete")
        // Notify Adapter
    }

    private fun handleResponsePresent(it: JSONObject?) {
        Log.e("TripResponse", "response of trip is " + it)

        tripUpcomigList = ArrayList()
        //   try {


        var jsonArray = it?.getJSONArray("data")!!
        if (jsonArray.length() > 0) {
            textNoTrip?.visibility = View.GONE
            Log.e("TripResponse", "response of trip is json array " + jsonArray)
            for (item: JSONObject in jsonArray) {
                Log.e("TripResponse", "response of trip item is  " + item)
                var tripmodel = upcomingTripModel()
                tripmodel.id = item?.getLong("id")
                tripmodel.departureCountry = item?.getString("departure_country")
                tripmodel.status = item?.getString("status")

                tripmodel.arrivalCountry = item?.getString("arrival_country")
                tripmodel.createdDate = item?.getString("arrival_date")
                tripmodel.updatedDate = item?.getString("departure_date")
                tripmodel.imageUrl = item?.getString("image")
                tripmodel.isPast = false
                Log.e(
                    "TripResponse",
                    "dates are " + tripmodel.createdDate + "       " + tripmodel.updatedDate
                )
                if (!dialogSelect.equals("0")) {
                    if (tripmodel.status.toString().equals(dialogSelect)) {

                        tripUpcomigList.add(tripmodel)
                    }
                } else {
                    tripUpcomigList.add(tripmodel)
                }
            }
        } else {
            textNoTrip?.visibility = View.VISIBLE
            //   interfaceBackAlert?.Sucess(true)


        }

        /* activity?.runOnUiThread { Runnable {
             adaptor?.setList(tripUpcomigList,1)
             adaptor?.notifyDataSetChanged()  } }*/
        var upcoming = UpcomigFragment.newInstance(tripUpcomigList as ArrayList<upcomingTripModel>)
        fragmentFucntion.replaceFragment(activity, upcoming, R.id.container2)
        upcoming.attachIFragmentCommunicatorMain(this)
        /*  adaptor=VisitListAdaptor(activity!!,tripUpcomigList,2)
              adaptor?.setList(tripUpcomigList,1)
              adaptor?.notifyDataSetChanged()*/
        //  }catch(e:Exception){

        //  e.printStackTrace()
        //}
    }

    fun setAdaptor() {
        Log.e("TripResponse", "adator been set ")
        adaptor = VisitListAdaptor(activity!!, tripUpcomigList, 2)

        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.divider
                )
            )
        )
        // recyclerViewStoreListingAdapter.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(activity!!,R.drawable.divider)))
        recyclerView?.adapter = adapter
        //recyclerView?.layoutManager= LinearLayoutManager(activity)
        // recyclerView?.addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(activity!!,R.drawable.divider)))
        // recyclerView?.adapter=adapter

    }

    fun setAdaptor2() {
        Log.e("TripResponse", "adator been set 2 ")
        adaptor = VisitListAdaptor(activity!!, tripPastList, 2)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.divider
                )
            )
        )
        recyclerView?.adapter = adapter

    }

    private fun handleResponsePast(it: JSONObject?) {
        Log.e("TripResponse", "response of trip is " + it)
        RelativePopUp2?.visibility = View.GONE
        tripPastList = ArrayList()
        //  try {
        var jsonArray = it?.getJSONArray("data")!!
        if (jsonArray.length() > 0) {
            textNoTrip?.visibility = View.GONE
            for (item: JSONObject in jsonArray) {
                var tripmodel = upcomingTripModel()
                tripmodel.id = item.getLong("id")
                tripmodel.departureCountry = item.getString("departure_country")
                tripmodel.arrivalCountry = item.getString("arrival_country")
                tripmodel.createdDate = item.getString("arrival_date")
                tripmodel.updatedDate = item.getString("departure_date")
                tripmodel.imageUrl = item.getString("image")
                tripmodel.status = item.getString("status")
                tripmodel.isPast = true

                if (!dialogSelectPastString.equals("0")) {
                    if (tripmodel.status.toString().equals(dialogSelectPastString)) {
                        tripPastList.add(tripmodel)
                    }
                } else {
                    tripPastList.add(tripmodel)
                }
            }
        } else {
            textNoTrip?.visibility = View.VISIBLE
        }
        var past = PastFragment.newInstance(tripPastList as ArrayList<upcomingTripModel>)
        fragmentFucntion.replaceFragment(activity, past, R.id.container2)
        past.attachIFragmentCommunicatorMain(this)
        //  setAdaptor()
        //  }catch(e:Exception){
        //  e.printStackTrace()

        //  }
    }

    override fun onDestroy() {
        super.onDestroy()
//        BaseActivity.appBarLayout.background=null

    }


    companion object {
        @JvmStatic
        fun newInstance(tripList: MutableList<addTripModel>): MyVisitFragment {
            var fragment: MyVisitFragment = MyVisitFragment()
            fragment.tripList = tripList
            /// fragment.toolBar=toolBar

            return fragment

        }

        fun newInstance2(
            interfaceBackAlert: InterfaceBackAlert
        ): MyVisitFragment {

            var fragment: MyVisitFragment = MyVisitFragment()
            fragment.interfaceBackAlert = interfaceBackAlert
            return fragment

        }


    }

    private operator fun JSONArray?.iterator(): Iterator<JSONObject> =
        (0 until this!!.length()).asSequence().map { get(it) as JSONObject }.iterator()

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
    }

    override fun passData(position: Int, status: Int, value: String) {
        if (status == 1)
            showAlert(position, status, value)
    }

    private fun showAlert(position: Int, status: Int, value: String) {
        val commonDialog = Dialog(activity!!)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_alert, null, false)
        val tvDelete = vv.findViewById<TextView>(R.id.tvYes)
        val tvCancel = vv.findViewById<TextView>(R.id.tvNo)

        tvDelete.setOnClickListener {
            commonDialog.dismiss()
            clickedPosition = position
            clickedFromList = value

            if (value == "up") {
                if (position <= tripUpcomigList.size)
                    viewModel.deleteTrip(
                        Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                        tripUpcomigList[position].id.toString()
                    )
            } else {
                if (position <= tripPastList.size)
                    viewModel.deleteTrip(
                        Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN),
                        tripPastList[position].id.toString()
                    )
            }
        }

        tvCancel.setOnClickListener {
            commonDialog.dismiss()
        }

        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        commonDialog.setCancelable(false)
//        commonDialog.window!!.attributes.windowAnimations = R.style.DialogTheme
        commonDialog.show()
    }
}
