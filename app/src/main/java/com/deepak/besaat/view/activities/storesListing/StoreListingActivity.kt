package com.deepak.besaat.view.activities.storesListing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.Interfaces.callMoreApi
import com.deepak.besaat.Interfaces.imageIconClick
import com.deepak.besaat.Interfaces.searchTextChange
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityStoreListingBinding
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.model.getStoresModel.Store
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.storeDetail.Dialog.StoreFilterDialog
import com.deepak.besaat.view.activities.storesListing.adapter.StoresCategoryListAdapter
import com.deepak.besaat.view.activities.storesListing.adapter.StoresCategoryListAdapter.OnItemClick
import com.deepak.besaat.view.fragments.storeListingFragment.StoreListingFragment
import com.deepak.besaat.view.fragments.storeMapFragment.StoreMapFragment
import com.deepak.besaat.viewModel.StoreListingActivityViewModel
import kotlinx.android.synthetic.main.activity_store_listing.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.lang.Exception

import android.widget.*
import com.deepak.besaat.utils.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_store_listing.toolBar
import kotlinx.android.synthetic.main.layout_popup.*
import kotlinx.android.synthetic.main.rating_items.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class StoreListingActivity : BaseActivity(), OnItemClick {
    var name: String = ""
    var rating: String = ""
    var valu: String = ""
    var pageToken: String = ""
    var mapChecked: Boolean? = null
    var count: Int = 0
    var clearText: String = ""
    var countInside: Int = 0
    var loadMore: Boolean = false
    var clickBoolean: Boolean = false
    var clickBool: Boolean? = false
    var mapView: Boolean = true
    var ratingString: String = ""
    var distanceValue: String = ""
    var seekBarValue: String = "15000"
    var popupClick: LinearLayout? = null
    var dilaogLinear: LinearLayout? = null
    var seekBar: SeekBar? = null
    var lati: String? = null
    var longi: String? = null
    var url: String? = null
    var noDataFound: Boolean = false
    var noDataString: String = ""
    var ImageCrossed: ImageView? = null
    var textReset: TextView? = null
    private var locationDisabled: Boolean = false
    private var serviceFirstTimeExecution: Boolean = false
    var allCategory: String? = ""
    var markerIcon: String? = ""


    override fun onPosClck(position: Int, name: String, value: String) {

        if (this.name != name) {
            this.name = name
            if (name.toLowerCase() == "all" || name == "") {
                markerIcon = ""
            } else {
                markerIcon = servicesList[position].map_image
            }

            Log.e("response", "response of click " + name)
            if (mapChecked == false) {
                Log.e(
                    "ShareSetting",
                    "shar prefrence string  on map true " + sharedPref.getString(Constants.latChanged) + "   " + sharedPref.getString(
                        Constants.latChanged
                    )
                )
                Handler().postDelayed(Runnable {
                    pageToken = ""
                    if (serviceFirstTimeExecution == false) {
                        serviceFirstTimeExecution = true
                        viewModel.getStoreDetailsAnother(
                            sharedPref,
                            name,
                            resources.getString(R.string.google_api),
                            seekBarValue,
                            true,
                            rating,
                            pageToken,
                            valu,
                            sharedPref.getString(Constants.latChanged),
                            sharedPref.getString(Constants.longChanged),
                            allCategory
                        )
                    }
                }, 500)

            } else {
                Log.e(
                    "ShareSetting",
                    "shar prefrence string  on map false  " + sharedPref.getString(Constants.latChanged) + "   " + sharedPref.getString(
                        Constants.latChanged
                    )
                )
                Handler().postDelayed(Runnable {
                    pageToken = ""
                    if (!serviceFirstTimeExecution) {
                        serviceFirstTimeExecution = true
                        viewModel.getStoreDetails(
                            sharedPref,
                            name,
                            resources.getString(R.string.google_api),
                            seekBarValue,
                            true,
                            rating,
                            pageToken,
                            value,
                            sharedPref.getString(Constants.latChanged),
                            sharedPref.getString(Constants.longChanged),
                            allCategory
                        )
                    }
                }, 500)
            }
            adapter.notifyAdapter(position)
        }
        //BaseActivity.searchEdit.text= Editable.Factory.getInstance().newEditable(clearText)
    }

    val fragmentFucntions: FragmentFucntions by inject()
    val viewModel: StoreListingActivityViewModel by inject()
    lateinit var binding: ActivityStoreListingBinding
    var jsonArray: JSONArray = JSONArray()
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()
    lateinit var adapter: StoresCategoryListAdapter
    var storeListFragment: StoreListingFragment? = null
    var storeMapFragment: StoreMapFragment? = null
    var storeFilterDialog: StoreFilterDialog? = null
    var servicesList: MutableList<Store> = ArrayList()
    var storeList: MutableList<SourceDetails> = ArrayList()
    var storeListFiltered: ArrayList<SourceDetails> = ArrayList()
    //  lateinit var getname:String
    val frequentFunctions: FrequentFunctions by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityStoreListingBinding>(
            this,
            R.layout.activity_store_listing,
            null
        )

        binding.viewModelStoreListing = viewModel

        viewModel.getStores(Constants.BEARER + " " + sharedPref.getString(Constants.TOKEN))

        filterPopUp()

        customToolBarWithSearch(this, toolBar, object : searchTextChange {
            override fun TextChange(value: String) {
                valu = value.toLowerCase()
                storeListFiltered = ArrayList()
                for (item in storeList) {
                    if (item.name.toString().toLowerCase().contains(value.toLowerCase())) {
                        storeListFiltered.add(item)
                    }
                }
                storeListFragment?.notifydatasetAndSetList(storeListFiltered)

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                if (currentFragment is StoreMapFragment) {
                    var storeMapFragment: StoreMapFragment = StoreMapFragment.newInstance(
                        storeList,
                        valu, name, markerIcon!!
                    )
                    imageViewMapList.setImageResource(R.drawable.ic_list)
                    fragmentFucntions.replaceFragment(
                        this@StoreListingActivity,
                        storeMapFragment,
                        R.id.listOrMapContainer
                    )
                }
            }
        }, object : imageIconClick {
            override fun onClick(boolean: Boolean) {
                if (boolean) {
                    if (clickBool == false) {
                        clickBool = true
                        dilaogLinear?.visibility = View.VISIBLE
                    } else {
                        clickBool = false
                        dilaogLinear?.visibility = View.GONE
                    }
                }
            }
        })

        initObserver()
        fragmentFucntions.replaceFragment(this, StoreListingFragment(), R.id.listOrMapContainer)
    }

    private fun filterPopUp() {
        dilaogLinear = findViewById(R.id.frameDialog)
        var radioGroup = findViewById<RadioGroup>(R.id.groupradio)
        var textViewContinue = findViewById<TextView>(R.id.textViewContinue)
        seekBar = findViewById<SeekBar>(R.id.simpleSeekBar)
        textReset = findViewById<TextView>(R.id.textreset)
        ImageCrossed = findViewById<ImageView>(R.id.imageCross)

        seekBar?.progress = 15
        seekbardetails.text = "15" + " km"
        seekBarValue = "15000"

        ImageCrossed?.setOnClickListener {
            Log.e("clcikVisiblity", "clcikvisiblity is  $clickBool")
            clickBool = false
            dilaogLinear?.visibility = View.GONE
        }

        textReset?.setOnClickListener {
            radioGroup.check(R.id.radia_id11)
            rating = ""
            seekBar?.progress = 15
            seekbardetails.text = "15" + " km"
            seekBarValue = "15" + "000"
            //                clickBool=false
            //                dilaogLinear?.visibility=View.GONE

            if (mapChecked == false) {
                Log.e(
                    "ShareSetting",
                    "shar prefrence string  on map true " + sharedPref.getString(Constants.latChanged) + "   " + sharedPref.getString(
                        Constants.latChanged
                    )
                )
                Handler().postDelayed(Runnable {
                    pageToken = ""
                    if (serviceFirstTimeExecution == false) {
                        serviceFirstTimeExecution = true
                        viewModel.getStoreDetailsAnother(
                            sharedPref,
                            name,
                            resources.getString(R.string.google_api),
                            seekBarValue,
                            true,
                            rating,
                            pageToken,
                            valu,
                            sharedPref.getString(Constants.latChanged),
                            sharedPref.getString(Constants.longChanged),
                            allCategory
                        )
                    }
                }, 200)

            } else {
                Log.e(
                    "ShareSetting",
                    "shar prefrence string  on map false  " + sharedPref.getString(Constants.latChanged) + "   " + sharedPref.getString(
                        Constants.latChanged
                    )
                )
                Handler().postDelayed(Runnable {
                    pageToken = ""
                    if (serviceFirstTimeExecution == false) {
                        serviceFirstTimeExecution = true
                        viewModel.getStoreDetails(
                            sharedPref,
                            name,
                            resources.getString(R.string.google_api),
                            seekBarValue,
                            true,
                            rating,
                            pageToken,
                            valu,
                            sharedPref.getString(Constants.latChanged),
                            sharedPref.getString(Constants.longChanged),
                            allCategory
                        )
                    }


                }, 200)


            }
        }

        sharedPref.setString(Constants.latChanged, "")
        sharedPref.setString(Constants.longChanged, "")

        radioGroup.setOnCheckedChangeListener { group, viewID ->
            if (viewID == R.id.radia_id11) {
                // value="5000"
                rating = ""
                // passValue?.value("5000")
            }
            if (viewID == R.id.radia_id1) {
                // value="5000"
                rating = "5"
                // passValue?.value("5000")
            }
            if (viewID == R.id.radia_id2) {
                // value="4000"
                rating = "4"
                //  passValue?.value("4000")
            }

            if (viewID == R.id.radia_id3) {
                // value="3000"
                rating = "3"
                //passValue?.value("3000")
            }
            if (viewID == R.id.radia_id4) {
                //value="2000"
                rating = "2"
                //  passValue?.value("2000")
            }
            if (viewID == R.id.radia_id5) {
                // value="1000"
                rating = "1"
                // passValue?.value("1000")
            }
        }

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                seekbardetails.text = "$p1 km"
                seekBarValue = p1.toString() + "00"
                var MIN = 1
                if (p1 < MIN) {
                    seekbardetails.text = "1 km"
                    seekBarValue = "1" + "000"
                    // value.setText(" Time Interval (" + seektime + " sec)");
                } else {
                    seekbardetails.text = "$p1 km"
                    seekBarValue = p1.toString() + "000"
                }
                //value.setText(" Time Interval (" + seektime + " sec)");
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        textViewContinue2.setOnClickListener {
            clickBool = false
            dilaogLinear?.visibility = View.GONE
            if (mapChecked == false) {
                Handler().postDelayed(Runnable {
                    pageToken = ""
                    if (serviceFirstTimeExecution == false) {
                        serviceFirstTimeExecution = true
                        viewModel.getStoreDetailsAnother(
                            sharedPref,
                            name,
                            resources.getString(R.string.google_api),
                            seekBarValue,
                            true,
                            rating,
                            pageToken,
                            valu,
                            sharedPref.getString(Constants.latChanged),
                            sharedPref.getString(Constants.longChanged),
                            allCategory
                        )
                    }
                }, 200)
            } else {
                Handler().postDelayed(Runnable {
                    pageToken = ""
                    if (serviceFirstTimeExecution == false) {
                        serviceFirstTimeExecution = true
                        viewModel.getStoreDetails(
                            sharedPref,
                            name,
                            resources.getString(R.string.google_api),
                            seekBarValue,
                            true,
                            rating,
                            pageToken,
                            valu,
                            sharedPref.getString(Constants.latChanged),
                            sharedPref.getString(Constants.longChanged),
                            allCategory
                        )
                    }
                }, 200)
            }
        }
    }

    fun initObserver() {
        Places.initialize(applicationContext, getString(R.string.google_api_key))
        var AUTOCOMPLETE_REQUEST_CODE = 5;
        var fields = Arrays.asList(
            Place.Field.LAT_LNG, Place.Field.NAME,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ADDRESS
        )

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
            if (it) {
                commonFunctions.showProgressBar(this, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }

        })

        viewModel.feedBackMessage.observe(this, Observer {
            commonFunctions.showFeedbackMessage(contrainTop, it)
        })

        viewModel.successfullyGetStore.observe(this, Observer {
            servicesList.clear()
            var data = Store()

            data.name = "All"
            servicesList = it.stores!!
            allCategory = ""
            for (i in 0 until servicesList.size) {
                if (i != servicesList.size - 1)
                    allCategory = allCategory + "" + servicesList[i].status + "|"
                else
                    allCategory = allCategory + "" + servicesList[i].status
            }
            servicesList.add(0, data)

            setAdapter()
            val getname = intent.getStringExtra("storedetail")
                ?: "all"
            if (getname != "") {
                for ((i, item) in servicesList.withIndex()) {
                    if (item.name.toString() == getname) {
                        binding.recyclerViewStoreListing.scrollToPosition(i)
                        adapter.notifyAdapter(i)
                    }
                }

                Log.e("StoreClick", "get name model  " + getname)
                Handler().postDelayed(Runnable {
                    if (frequentFunctions.isGooglePlayServicesAvailable(this@StoreListingActivity)) {
                        viewModel.getStoreDetails(
                            sharedPref,
                            getname,
                            resources.getString(R.string.google_api),
                            seekBarValue,
                            true,
                            rating,
                            pageToken,
                            valu,
                            lati,
                            longi,
                            allCategory
                        )
                    }
                }, 100)
            } else {
                Log.e("StoreClick", "get name model  else case " + getname)
            }
            adapter.onClickListner(this)
            //

            /*  if(getname!=null){
                  Log.e("storedetail"," store details name inside not nullrRRRRRRRRRRRR "+getname)
                  for((i,item) in servicesList.withIndex()){
                      Log.e("storedetail"," store details name is  "+item.name.toString().equals(getname))
                      if(item.name.toString().equals(getname)){
                          adapter.notifyAdapter(i)
                          viewModel.getStoreDetails(
                              sharedPref,
                              getname!!,
                              resources.getString(R.string.google_api),
                              "5000",
                              true,
                              rating
                          )
                      }
                      //adapter.notifyAdapter()
                  }
              }*/
        })
        viewModel.listMapIconClickChange.observe(this, Observer {
            Log.e("MapIcon", "Current context is " + it)
            if (it) {
                Log.e(
                    "MapIconChanged",
                    "map icon chnaged   on location disabed true" + it + " location disabled check   " + locationDisabled
                )
                imageView4.setImageResource(R.drawable.ic_location)

                if (mapChecked == true) {
                    mapChecked = true
                    Log.e(
                        "ShareSetting",
                        "shar prefrence string  on map true " + sharedPref.getString(Constants.latChanged) + "   " + sharedPref.getString(
                            Constants.latChanged
                        )
                    )
                    Handler().postDelayed(Runnable {
                        pageToken = ""
                        viewModel.getStoreDetailsAnother(
                            sharedPref,
                            name,
                            resources.getString(R.string.google_api),
                            seekBarValue,
                            true,
                            rating,
                            pageToken,
                            valu,
                            sharedPref.getString(Constants.latChanged),
                            sharedPref.getString(Constants.longChanged),
                            allCategory
                        )
                    }, 500)

                } else {
                    Log.e(
                        "ShareSetting",
                        "shar prefrence string  on map false  " + sharedPref.getString(Constants.latChanged) + "   " + sharedPref.getString(
                            Constants.latChanged
                        )
                    )
                    mapChecked = true
                    Handler().postDelayed(Runnable {
                        pageToken = ""
                        viewModel.getStoreDetails(
                            sharedPref,
                            name,
                            resources.getString(R.string.google_api),
                            seekBarValue,
                            true,
                            rating,
                            pageToken,
                            valu,
                            sharedPref.getString(Constants.latChanged),
                            sharedPref.getString(Constants.longChanged),
                            allCategory
                        )


                    }, 500)
                }
                //   imageViewMapList.setImageResource(R.drawable.ic_map)
                /*  viewModel.getStoreDetails(
                  sharedPref,
                  name.toString(),
                  resources.getString(R.string.google_api),
                  "1000",
                  false,
                  rating,
                  pageToken,
                  valu
              )*/
                //  imageView4.setImageResource(R.drawable.ic_location)
                Log.e("MapIcon", "Current context is True ")
            } else {
                //  imageView4.setImageResource(R.drawable.location_disabled)

                // imageViewMapList.setImageResource(R.drawable.ic_list)
                /* viewModel.getStoreDetails(
                  sharedPref,
                  name.toString(),
                  resources.getString(R.string.google_api),
                  "1000",
                  true,
                  rating,
                  pageToken,
                  valu
              )*/
                Log.e(
                    "MapIconChanged",
                    "map icon chnaged   on location disabled  false" + it + " location disabled check   " + locationDisabled
                )

                locationDisabled = false
                mapChecked = false
                imageView4.setImageResource(R.drawable.location_disabled)
                sharedPref.setString(Constants.latChanged, "")
                sharedPref.setString(Constants.longChanged, "")
                var intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
                Log.e("MapIcon", "Current context is False ")
            }
        })
        /*  viewModel.stingResponse.observe(this, Observer {
              handleResponse(it)
          })*/
        viewModel.jsonResponse21.observe(this, Observer {
            handleJsonResponse2(it)
        })
        viewModel.jsonResponse22.observe(this, Observer {
            handleResponse22(it)
        })
        viewModel.jsonResponse.observe(this, Observer {
            handleJsonResponse(it)
        })

        viewModel.feedBackMessage.observe(this, Observer {
            handleResponse(it)
        })

        viewModel.listMapIocnClick.observe(this, Observer {
            Log.e("MapIcon", "list map icon click " + it)
            if (it) {
                if (mapChecked == false) {
                    // Toast.makeText(this,"Please enable location ",Toast.LENGTH_LONG).show()
                    val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                    if (!mapView) {
                        mapView = true
                        imageViewMapList.setImageResource(R.drawable.ic_map)
                        fragmentFucntions.replaceFragment(
                            this,
                            storeListFragment!!,
                            R.id.listOrMapContainer
                        )
                    } else {
                        mapView = false
                        var storeMapFragment: StoreMapFragment = StoreMapFragment.newInstance(
                            storeList,
                            valu, name, markerIcon!!
                        )
                        imageViewMapList.setImageResource(R.drawable.ic_list)
                        fragmentFucntions.replaceFragment(
                            this,
                            storeMapFragment,
                            R.id.listOrMapContainer
                        )
                    }
                } else {
                    val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                    if (!mapView) {
                        mapView = true
                        storeListFragment =
                            StoreListingFragment.newInstance(
                                storeList,
                                viewModel.repository,
                                pageToken,
                                object : callMoreApi {
                                    override fun more(sucess: Boolean, name: String) {
                                        if (sucess) {
                                            if (valu.equals("")) {
                                                viewModel.getStoreDetailsMore(
                                                    sharedPref,
                                                    name,
                                                    resources.getString(R.string.google_api),
                                                    seekBarValue,
                                                    true,
                                                    rating, pageToken,
                                                    allCategory
                                                )
                                            }
                                        }
                                    }
                                },
                                name,
                                loadMore, lati, longi, valu, noDataString
                            )
                        Log.e("MapIcon", "list map icon click is true ")
                        imageViewMapList.setImageResource(R.drawable.ic_map)
                        fragmentFucntions.replaceFragment(
                            this,
                            storeListFragment!!,
                            R.id.listOrMapContainer
                        )
                    } else {
                        mapView = false
                        var storeMapFragment: StoreMapFragment = StoreMapFragment.newInstance(
                            storeList,
                            valu, name,
                            markerIcon!!
                        )
                        Log.e("MapIcon", "list map icon click is false ")
                        imageViewMapList.setImageResource(R.drawable.ic_list)
                        fragmentFucntions.replaceFragment(
                            this,
                            storeMapFragment,
                            R.id.listOrMapContainer
                        )
                    }
                    Log.e("MapIcon", "list map icon click is ture" + it)
                }
                /*  val currentFragment = supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                  if (currentFragment !is StoreListingFragment) {
                      imageViewMapList.setImageResource(R.drawable.ic_map)
                      fragmentFucntions.replaceFragment(this, storeListFragment!!, R.id.listOrMapContainer)
                  } else {
                      imageViewMapList.setImageResource(R.drawable.ic_list)
                      fragmentFucntions.replaceFragment(this, storeListFragment!!, R.id.listOrMapContainer)
                  }*/
            } else {
                Log.e("MapIcon", "list map icon click is false " + it)
            }
        })
    }

    private fun handleResponse22(it: JSONObject?) {
        Log.e("response", "response on main thread " + it)
        //var jsonObject = JSONObject(it)
        //  servicesList.clear()
        storeList = ArrayList()
        pageToken = ""
        try {
            //   storeList = ArrayList()
            try {
                jsonArray = it?.getJSONArray("results")!!
                Log.e("responseArray", "Json array response on first try resposne 22 " + jsonArray)
                for (item: JSONObject in jsonArray) {
                    Log.e("response", "item object  " + item)
                    try {
                        var sourceDetail = SourceDetails()
                        sourceDetail.icon = item.optString("icon")
                        sourceDetail.name = item?.getString("name")
                        sourceDetail.locAddress = item?.getString("vicinity")
                        sourceDetail.rating = item?.getDouble("rating").toFloat()
                        sourceDetail.placeId = item?.getString("place_id")
                        sourceDetail.userRatingsTotal = item?.optInt("user_ratings_total")
                        var jsonObjectGeometry = JSONObject(item?.getString("geometry"))
                        var jsonObjectLocation: JSONObject =
                            jsonObjectGeometry.get("location") as JSONObject
                        sourceDetail.latitute = jsonObjectLocation.getDouble("lat")
                        sourceDetail.longitute = jsonObjectLocation.getDouble("lng")
                        // var jsonPhotoArrayy=JSONArray(item?.getString(  "photos"))
                        try {
                            var hasPhoto = item?.has("photos") ?: false
                            var isNull = it?.isNull("photos") ?: false
                            var jsonPhotoArray = item?.getJSONArray("photos")
                            if (jsonPhotoArray != null) {
                                var jsonPhotoObject = jsonPhotoArray.getJSONObject(0)
                                var photoRefrence = jsonPhotoObject.getString("photo_reference")
                                var height = jsonPhotoObject.getInt("height")
                                var width = jsonPhotoObject.getInt("width")
                                sourceDetail.photoRefrence = photoRefrence
                                sourceDetail.heightt = height
                                sourceDetail.widthh = width
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        Log.e("response", "store name " + sourceDetail.name)
                        Log.e("response", "location lat " + sourceDetail.latitute)
                        Log.e("response", "location longi " + sourceDetail.longitute)
                        // store.id=item?.getString("id").toLong()
                        // store.status=item?.getString("")
                        if (rating.equals("")) {
                            storeList.add(sourceDetail)
                        } else {
                            if (item?.getDouble("rating").toFloat() <= rating.toFloat() && item?.getDouble(
                                    "rating"
                                ).toFloat() > (rating.toFloat() - 1)
                            ) {
                                storeList.add(sourceDetail)
                            }
                        }
                    } catch (e: JSONException) {
                        Log.e("response", "json Exception  " + e)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()

            }

            var tempList = storeList.sortedWith(compareBy { it.rating })
            storeList.clear()
            storeList.addAll(tempList)

            var hasToken = it?.has("next_page_token") ?: false
            var isNull = it?.isNull("next_page_token") ?: false
            Log.e(
                "responseArray",
                "PageToken  on  outside first try not has token response 22" + pageToken
            )
            if (hasToken) {

                pageToken = it?.getString("next_page_token").toString()
                Log.e("responseArray", "PageToken  on first try  has token response 22" + pageToken)
            } else {
                pageToken = ""
                Log.e(
                    "responseArray",
                    "PageToken  on first try not has token response 22" + pageToken
                )
            }

            Log.e("responseArray", "PageToken  on first try  resposne 222   " + pageToken)

            if (!storeList.isEmpty()) {
                for (item in storeList) {
                    if (item.name.toString().toLowerCase().contains(valu.toLowerCase())) {
                        //valu=valu.toLowerCase()
                        storeListFiltered = ArrayList<SourceDetails>()
                        storeListFiltered.add(item)
                        //    storeListFragment?.notifydatasetAndSetList(storeListFiltered)
                        storeListFragment = StoreListingFragment.newInstance(
                            storeListFiltered,
                            viewModel.repository,
                            pageToken,
                            object : callMoreApi {
                                override fun more(sucess: Boolean, name: String) {
                                    if (sucess) {
                                        if (valu.equals("")) {
                                            viewModel.getStoreDetailsMore(
                                                sharedPref,
                                                name,
                                                resources.getString(R.string.google_api),
                                                seekBarValue,
                                                true,
                                                rating, pageToken,
                                                allCategory
                                            )
                                        }
                                    }
                                }
                            },
                            name,
                            loadMore,
                            lati,
                            longi,
                            valu, noDataString
                        )

                    } else {
                        //  Toast.makeText(this@StoreListingActivity,"no item found ",Toast.LENGTH_LONG).show()
                        //storeListFragment?.notifydatasetAndSetList(storeListFiltered)

                        storeListFragment = StoreListingFragment.newInstance(
                            storeList,
                            viewModel.repository,
                            pageToken,
                            object : callMoreApi {
                                override fun more(sucess: Boolean, name: String) {
                                    if (sucess) {

                                        if (valu.equals("")) {
                                            viewModel.getStoreDetailsMore(
                                                sharedPref,
                                                name,
                                                resources.getString(R.string.google_api),
                                                seekBarValue,
                                                true,
                                                rating, pageToken,
                                                allCategory
                                            )
                                        }
                                    }
                                }
                            },
                            name,
                            loadMore,
                            lati,
                            longi,
                            valu, noDataString
                        )
                    }
                }
            }
            if (mapChecked == false) {
                fragmentFucntions.replaceFragment(
                    this,
                    storeListFragment!!,
                    R.id.listOrMapContainer
                )
            } else {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                if (currentFragment !is StoreListingFragment) {
                    imageViewMapList.setImageResource(R.drawable.ic_map)
                    fragmentFucntions.replaceFragment(
                        this,
                        storeListFragment!!,
                        R.id.listOrMapContainer
                    )
                } else {
                    var storeMapFragment: StoreMapFragment = StoreMapFragment.newInstance(
                        storeList,
                        valu, name,
                        markerIcon!!
                    )
                    imageViewMapList.setImageResource(R.drawable.ic_list)
                    fragmentFucntions.replaceFragment(
                        this,
                        storeListFragment!!,
                        R.id.listOrMapContainer
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("responseArray", "PageToken  on second try  resposne 22  " + pageToken)
        }
    }

    private fun handleJsonResponse2(it: JSONObject?) {
        Log.e("response", "response on main thread " + it)
        //var jsonObject = JSONObject(it)
        //  servicesList.clear()
        var storeList2: MutableList<SourceDetails> = ArrayList()
        pageToken = ""
        var maxDistance: Double = 20.00
        try {
            jsonArray = it?.getJSONArray("results")!!
            for (item: JSONObject in jsonArray) {
                var sourceDetail = SourceDetails()
                sourceDetail.icon = item.optString("icon")
                if (item.has("name"))
                    sourceDetail.name = item.getString("name")
                if (item.has("vicinity"))
                    sourceDetail.locAddress = item.getString("vicinity")
                if (item.has("rating")) {
                    sourceDetail.rating = item.getDouble("rating").toFloat()
                }
                if (item.has("place_id"))
                    sourceDetail.placeId = item.getString("place_id")

                sourceDetail.userRatingsTotal = item.optInt("user_ratings_total")

                var jsonObjectGeometry = JSONObject(item.getString("geometry"))
                var jsonObjectLocation: JSONObject =
                    jsonObjectGeometry.get("location") as JSONObject
                if (jsonObjectLocation.has("lat") && jsonObjectLocation.has("lng")) {
                    sourceDetail.latitute = jsonObjectLocation.getDouble("lat")
                    sourceDetail.longitute = jsonObjectLocation.getDouble("lng")

                    if (mapChecked == false) {
                        sourceDetail.radius = CalculationByDistance(
                            LatLng(
                                sharedPref.getString(Constants.latChanged).toDouble(),
                                sharedPref.getString(Constants.longChanged).toDouble()
                            ),
                            LatLng(
                                sourceDetail.latitute,
                                sourceDetail.longitute
                            )
                        )
                    } else {
                        sourceDetail.radius = CalculationByDistance(
                            LatLng(
                                sharedPref.getString(Constants.latitude).toDouble(),
                                sharedPref.getString(Constants.longitude).toDouble()
                            ),
                            LatLng(
                                sourceDetail.latitute,
                                sourceDetail.longitute
                            )
                        )
                    }
                }

                try {
                    var hasPhoto = item.has("photos") ?: false
                    var isNull = it.isNull("photos") ?: false
                    if (hasPhoto) {
                        var jsonPhotoArray = item?.getJSONArray("photos")
                        Log.e("response", "json photo arrya" + jsonPhotoArray)
                        if (jsonPhotoArray != null) {
                            var jsonPhotoObject = jsonPhotoArray.getJSONObject(0)
                            var photoRefrence: String? = null
                            if (jsonPhotoObject.has("photo_reference"))
                                photoRefrence = jsonPhotoObject.getString("photo_reference")
                            Log.e("response", "phot resonse " + photoRefrence)
                            var height: Int? = 50
                            if (jsonPhotoObject.has("height"))
                                height = jsonPhotoObject.getInt("height")
                            var width: Int? = 50
                            if (jsonPhotoObject.has("width"))
                                width = jsonPhotoObject.getInt("width")
                            sourceDetail.photoRefrence = photoRefrence
                            sourceDetail.heightt = height
                            sourceDetail.widthh = width
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (!rating.equals("")) {
                    if (item.has("rating")) {
                        if (item.getDouble("rating").toFloat() <= rating.toFloat() && item.getDouble(
                                "rating"
                            ).toFloat() > (rating.toFloat() - 1.0f)
                        ) {
                            if (sourceDetail.radius <= maxDistance) {
                                storeList.add(sourceDetail)
                                storeListFiltered.add(sourceDetail)
                            }
                        }
                    }
                } else if (sourceDetail.radius <= maxDistance) {
                    storeList.add(sourceDetail)
                    storeListFiltered.add(sourceDetail)
                }
            }
            var hasToken = it?.has("next_page_token") ?: false
            var isNull = it?.isNull("next_page_token")
            Log.e(
                "responseArray",
                "PageToken  on first try  outside not has token response 2" + pageToken
            )
            if (hasToken) {
                pageToken = it?.getString("next_page_token").toString()
                Log.e("responseArray", "PageToken  on first try has token response 2" + pageToken)
            } else {
                pageToken = ""
                Log.e(
                    "responseArray",
                    "PageToken  on first try not has token response 2" + pageToken
                )
            }
            Log.e("responseArray", "PageToken  on first try  resposne 2   " + pageToken)

            var tempList = storeList.sortedWith(compareBy { it.rating })
            storeList.clear()
            storeList.addAll(tempList)

            storeList2.addAll(storeList)
            storeListFragment = StoreListingFragment.newInstance(
                storeList2,
                viewModel.repository,
                pageToken,
                object : callMoreApi {
                    override fun more(sucess: Boolean, name: String) {
                        if (sucess) {
                            if (valu.equals("")) {
                                viewModel.getStoreDetailsMore(
                                    sharedPref,
                                    name,
                                    resources.getString(R.string.google_api),
                                    seekBarValue,
                                    true,
                                    rating, pageToken,
                                    allCategory
                                )
                            }
                        }
                    }
                },
                name,
                loadMore,
                lati,
                longi, valu, noDataString
            )

            storeMapFragment = null
            storeMapFragment = StoreMapFragment.newInstance(storeList, valu, name, markerIcon!!)

            if (mapChecked == false) {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                if (mapView) {
                    imageViewMapList.setImageResource(R.drawable.ic_map)
                    fragmentFucntions.replaceFragment(
                        this,
                        storeListFragment!!,
                        R.id.listOrMapContainer
                    )
                } else {

                    imageViewMapList.setImageResource(R.drawable.ic_list)
                    fragmentFucntions.replaceFragment(
                        this,
                        storeMapFragment!!,
                        R.id.listOrMapContainer
                    )

                }

            } else {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                if (mapView) {
                    imageViewMapList.setImageResource(R.drawable.ic_map)
                    fragmentFucntions.replaceFragment(
                        this, storeListFragment!!, R.id.listOrMapContainer
                    )
                } else {


                    imageViewMapList.setImageResource(R.drawable.ic_list)
                    fragmentFucntions.replaceFragment(
                        this,
                        storeMapFragment!!,
                        R.id.listOrMapContainer
                    )
                }
            }

            // storeListFragment?.notifyDataSetListAndPageToken(storeList2,pageToken)
            /*   if(mapChecked == false){
                   fragmentFucntions.replaceFragment(
                       this,
                       storeListFragment!!,
                       R.id.listOrMapContainer
                   )

               }else {
                   val currentFragment = supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                   if (currentFragment !is StoreListingFragment) {
                      // imageViewMapList.setImageResource(R.drawable.ic_map)
                       fragmentFucntions.replaceFragment(
                           this,
                           storeListFragment!!,
                           R.id.listOrMapContainer
                       )
                   } else {
                       var storeMapFragment:StoreMapFragment=StoreMapFragment.newInstance(
                           storeList,
                           valu
                       )
                      // imageViewMapList.setImageResource(R.drawable.ic_list)
                       fragmentFucntions.replaceFragment(
                           this,
                           storeListFragment!!,
                           R.id.listOrMapContainer
                       )
                   }
               }*/


        } catch (e: Exception) {
            // storeList = ArrayList()
            e.printStackTrace()
            Log.e("responseArray", "PageToken  on second try  resposne 2   " + pageToken)
        }
    }

    private fun handleJsonResponse(it: JSONObject?) {
        Log.e("response", "response on main thread " + it)
        Log.e("Check", "response on main thread " + it)
        //var jsonObject = JSONObject(it)
        //  servicesList.clear()
        pageToken = ""
        var maxDistance: Double = 20.00
        storeList = ArrayList()
        storeListFiltered = ArrayList()
        try {
            if (it != null) {
                serviceFirstTimeExecution = false
                jsonArray = it?.getJSONArray("results")!!
                Log.e("responseArray", "Json array is on first try " + jsonArray)
                if (jsonArray?.length() > 0) {

                    noDataString = ""
                    for (item: JSONObject in jsonArray) {
                        Log.e("response", "item object  " + item)

                        var sourceDetail = SourceDetails()
                        sourceDetail.icon = item.optString("icon")
                        if (item?.has("name"))
                            sourceDetail.name = item?.getString("name")
                        if (item?.has("vicinity"))
                            sourceDetail.locAddress = item?.getString("vicinity")
                        if (item?.has("rating")) {
                            sourceDetail.rating = item?.getDouble("rating").toFloat()
                        }
                        if (item?.has("place_id"))
                            sourceDetail.placeId = item?.getString("place_id")

                        sourceDetail.userRatingsTotal = item.optInt("user_ratings_total")

                        var jsonObjectGeometry = JSONObject(item?.getString("geometry"))

                        var jsonObjectLocation: JSONObject =
                            jsonObjectGeometry.get("location") as JSONObject
                        if (jsonObjectLocation.has("lat") && jsonObjectLocation.has("lng")) {
                            sourceDetail.latitute = jsonObjectLocation.getDouble("lat")
                            sourceDetail.longitute = jsonObjectLocation.getDouble("lng")

                            if (mapChecked == false) {
                                sourceDetail.radius = CalculationByDistance(
                                    LatLng(
                                        sharedPref.getString(Constants.latChanged).toDouble(),
                                        sharedPref.getString(Constants.longChanged).toDouble()
                                    ),
                                    LatLng(
                                        sourceDetail.latitute,
                                        sourceDetail.longitute
                                    )
                                )


                            } else {
                                sourceDetail.radius = CalculationByDistance(
                                    LatLng(
                                        sharedPref.getString(Constants.latitude).toDouble(),
                                        sharedPref.getString(Constants.longitude).toDouble()
                                    ),
                                    LatLng(
                                        sourceDetail.latitute,
                                        sourceDetail.longitute
                                    )
                                )
                            }
                        }
                        //  if(jsonObjectLocation.has("lng"))
                        // var jsonPhotoArrayy=JSONArray(item?.getString(  "photos"))
                        try {
                            var hasPhoto = item?.has("photos") ?: false
                            var isNull = it?.isNull("photos") ?: false
                            Log.e("hasphoto", "has photo variable " + hasPhoto)
                            if (hasPhoto) {
                                var jsonPhotoArray = item?.getJSONArray("photos")
                                if (jsonPhotoArray != null) {
                                    var jsonPhotoObject = jsonPhotoArray.getJSONObject(0)
                                    var photoRefrence =
                                        jsonPhotoObject.getString("photo_reference")
                                    var height = jsonPhotoObject.getInt("height")
                                    var width = jsonPhotoObject.getInt("width")
                                    sourceDetail.photoRefrence = photoRefrence
                                    sourceDetail.heightt = height
                                    sourceDetail.widthh = width
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        Log.e("radiusValue", "radius value is  " + sourceDetail.radius)
                        if (!rating.equals("")) {
                            if (item.has("rating")) {
                                if (item.getDouble("rating").toFloat() <= rating.toFloat() && item.getDouble(
                                        "rating"
                                    ).toFloat() > (rating.toFloat() - 1.0f)
                                ) {
                                    if (sourceDetail.radius <= maxDistance) {
                                        storeList.add(sourceDetail)
                                        storeListFiltered.add(sourceDetail)
                                    }
                                }
                            }
                        } else if (sourceDetail.radius <= maxDistance) {
                            storeList.add(sourceDetail)
                            storeListFiltered.add(sourceDetail)
                        }
                    }
                } else {

                    if (mapView) {
                        noDataString = "true"
                        //storeListFragment =StoreListingFragment.newInstance("No Data Found")

                    } else {
                        /// 12-02-2020
//                        alertDialogWithOKButton2(
//                            getString(R.string.alert),
//                            getString(R.string.no_data_found)
//                        )
                    }
                }
            } else {
                storeList = ArrayList()
                storeListFiltered = ArrayList()
            }
            var hasToken = it?.has("next_page_token") ?: false
            var isNull = it?.isNull("next_page_token") ?: false
            Log.e("responseArray", "PageToken  on first try outside has token " + pageToken)
            if (hasToken) {
                pageToken = it?.getString("next_page_token").toString()
                Log.e("responseArray", "PageToken  on first try has token " + pageToken)
            } else {
                pageToken = ""
                Log.e("responseArray", "PageToken  on first try noir has token " + pageToken)
            }
            Log.e("responseArray", "PageToken  on first try " + pageToken)

            //storeList.sortBy { it. }

            // 23- 12- 19 vk
            var tempList = storeList.sortedWith(compareBy { it.rating })
            storeList.clear()
            storeList.addAll(tempList)

            storeListFiltered.clear()
            storeListFiltered.addAll(tempList)
            ////////////

            storeListFragment = StoreListingFragment.newInstance(
                storeList,
                viewModel.repository,
                pageToken,
                object : callMoreApi {
                    override fun more(sucess: Boolean, name: String) {
                        if (sucess) {
                            if (valu.equals("")) {
                                viewModel.getStoreDetailsMore(
                                    sharedPref,
                                    name,
                                    resources.getString(R.string.google_api),
                                    seekBarValue,
                                    true,
                                    rating, pageToken,
                                    allCategory
                                )
                            }
                        }
                    }
                },
                name,
                loadMore,
                lati,
                longi, valu, noDataString
            )

            storeMapFragment = null
            storeMapFragment = StoreMapFragment.newInstance(storeList, valu, name, markerIcon!!)



            if (mapChecked == false) {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                if (mapView) {
                    imageViewMapList.setImageResource(R.drawable.ic_map)
                    fragmentFucntions.replaceFragment(
                        this,
                        storeListFragment!!,
                        R.id.listOrMapContainer
                    )
                } else {

                    imageViewMapList.setImageResource(R.drawable.ic_list)
                    fragmentFucntions.replaceFragment(
                        this,
                        storeMapFragment!!,
                        R.id.listOrMapContainer
                    )

                }

            } else {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.listOrMapContainer)
                if (mapView) {
                    imageViewMapList.setImageResource(R.drawable.ic_map)
                    fragmentFucntions.replaceFragment(
                        this, storeListFragment!!, R.id.listOrMapContainer
                    )
                } else {


                    imageViewMapList.setImageResource(R.drawable.ic_list)
                    fragmentFucntions.replaceFragment(
                        this,
                        storeMapFragment!!,
                        R.id.listOrMapContainer
                    )
                }
            }


        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e("responseArray", "PageToken  on secon try " + pageToken)
        }
    }


    private fun handleResponse(it: String?) {
        Log.e("response", "response on main thread " + it)

        // Log.e("response","json response on main thread"+jsonObject)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("resultCode", "result cod is  " + resultCode + "  " + requestCode)
        if (requestCode === 5) {
            if (resultCode === Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                var longitude = place.latLng!!.longitude
                var latitude = place.latLng!!.latitude
                place.address
                Log.e("longitude", longitude.toString())
                Log.e("latitude", latitude.toString())
                lati = latitude.toString()
                longi = longitude.toString()
                sharedPref.setString(Constants.latChanged, latitude.toString())
                sharedPref.setString(Constants.longChanged, longitude.toString())
                mapChecked == true
                // viewModel.listMapIconClickChange.value=true
                imageView4.setImageResource(R.drawable.location_disabled)
                locationDisabled = true
                Log.e(
                    "MapIconChanged",
                    "view model changed on location getting  " + locationDisabled
                )

                Log.e(
                    "ShareSetting",
                    "shar prefrence string " + sharedPref.getString(Constants.latChanged) + "   " + sharedPref.getString(
                        Constants.latChanged
                    )
                )
                Log.e("longitude", "logitute global " + lati)
                Log.e("latitude", longi)
                pageToken = ""
                viewModel.getStoreDetailsAnother(
                    sharedPref,
                    name,
                    resources.getString(R.string.google_api),
                    seekBarValue,
                    true,
                    rating, pageToken, valu, latitude.toString(), longitude.toString(),
                    allCategory
                )

            } else if (resultCode === AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                val `in` = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                `in`.hideSoftInputFromWindow(getCurrentFocus()?.getApplicationWindowToken(), 0)
            } else if (resultCode === Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.e("MapIconChanged", "view model on location cancel  " + locationDisabled)
                mapChecked = false
                viewModel.listMapIconClickChange.value = true
                imageView4.setImageResource(R.drawable.ic_location)
                val `in` = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                `in`.hideSoftInputFromWindow(getCurrentFocus()?.getApplicationWindowToken(), 0)
                // viewModel.listMapIconClickChange.value=false
            }
        }
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")

                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("CurrentAddress", strReturnedAddress.toString())
            } else {
                Log.w("CurrentAddress", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("CurrentAddress", "Canont get Address!")
        }

        return strAdd
    }

    fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371// radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        Log.e("Radius Value", "radius valur return " + Radius * c)
        return Radius * c
    }

    private fun setAdapter() {
        val getname = intent.getStringExtra("storedetail") ?: " "
        adapter = StoresCategoryListAdapter(this, servicesList)
        binding.recyclerViewStoreListing.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) as RecyclerView.LayoutManager?
        binding.recyclerViewStoreListing.adapter = adapter
    }
}

operator fun JSONArray?.iterator(): Iterator<JSONObject> =
    (0 until this!!.length()).asSequence().map { get(it) as JSONObject }.iterator()
