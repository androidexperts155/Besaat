package com.deepak.besaat.view.activities.home

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.deepak.besaat.R
import com.deepak.besaat.view.activities.BaseActivity

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepak.besaat.view.activities.home.adapter.NavigationAdapter
import kotlinx.android.synthetic.main.activity_home.*
import android.graphics.drawable.GradientDrawable
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.signup.SignupActivity
import com.deepak.besaat.view.fragments.customerProfile.CustomerProfile
import com.deepak.besaat.view.fragments.home.HomeFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import kotlinx.android.synthetic.main.view_tool_bar_with_menu.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.koin.android.ext.android.inject
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.deepak.besaat.Interfaces.CloseOpenDrawerInterface
import com.deepak.besaat.Interfaces.InterfaceBackAlert
import com.deepak.besaat.view.activities.home.viewModel.HomeActivityViewModel
import com.deepak.besaat.view.fragments.InprogressFragment
import com.deepak.besaat.view.fragments.MyVisitFragment.MyVisitFragment
import com.deepak.besaat.view.fragments.myOrders.MyOrdersFragment
import com.deepak.besaat.view.fragments.serviceProviderProfile.ServiceProviderProfile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.OnReverseGeocodingListener
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider

class HomeActivity : BaseActivity(), KeyboardVisibilityEventListener,
    NavigationAdapter.FragmentDrawerListener, CloseOpenDrawerInterface, OnLocationUpdatedListener {
    override fun Sucess(bool: Boolean) {
        if (bool) {
            closeOrOpenDrawer()
        }
    }

    val viewModel: HomeActivityViewModel by inject()

    private val LOCATION_PERMISSION_ID = 1001
    private var provider: LocationGooglePlayServicesProvider? = null
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null

    private var mLatitudeLabel: String = ""
    private var mLongitudeLabel: String = ""

    private var visitBool: Boolean = false

    lateinit var content: RelativeLayout
    lateinit var mainLayoutShadow: RelativeLayout
    val frequentFunctions: FrequentFunctions by inject()
    val fragmentFucntion: FragmentFucntions by inject()
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()
    var language = ""
    var userType = ""
    lateinit var bottomNavigation: MeowBottomNavigation

    companion object {
        lateinit var toolBar: Toolbar
        lateinit var textTop: TextView
        lateinit var homeRelative: RelativeLayout
        lateinit var topBarExtraLay: LinearLayout
        lateinit var bottomNaigationLinear: LinearLayout
    }


    override fun onDrawerItemSelected(position: Int) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        when (position) {
            0 -> {
                visitBool = false
                appBarLayout1.background = null
                bottomNaigationLinear.visibility = View.VISIBLE
                if (userType == "2") {
                    if (currentFragment !is CustomerProfile) {
                        fragmentFucntion.replaceFragment(this, CustomerProfile(), R.id.container)
                    }
                } else {
                    if (currentFragment !is ServiceProviderProfile) {
                        fragmentFucntion.replaceFragment(
                            this,
                            ServiceProviderProfile(),
                            R.id.container
                        )
                    }
                }
                changeToolBarColor(Constants.MENU_ME)
                BaseActivity.textViewTitle.visibility = View.VISIBLE
                topBarExtraLay.visibility = View.GONE
                bottomNavigation.show(4, true)
                textViewHome.setTextColor(
                    ContextCompat.getColor(
                        this@HomeActivity,
                        android.R.color.black
                    )
                )
                textViewOrders.setTextColor(
                    ContextCompat.getColor(
                        this@HomeActivity,
                        android.R.color.black
                    )
                )
                textViewNotification.setTextColor(
                    ContextCompat.getColor(
                        this@HomeActivity,
                        android.R.color.black
                    )
                )
                textViewMe.setTextColor(
                    ContextCompat.getColor(
                        this@HomeActivity,
                        R.color.colorPrimary
                    )
                )
            }
            1 -> {
                visitBool = false
                appBarLayout1.background = null
                bottomNaigationLinear.visibility = View.VISIBLE
                fragmentFucntion.replaceFragment(this, InprogressFragment(), R.id.container)
                BaseActivity.textViewTitle.visibility = View.GONE
                topBarExtraLay.visibility = View.GONE
                changeToolBarColor(Constants.MENU_WALLET)
            }
            2 -> {
                visitBool = false
                appBarLayout1.background = null
                bottomNaigationLinear.visibility = View.VISIBLE
                fragmentFucntion.replaceFragment(this, InprogressFragment(), R.id.container)
                BaseActivity.textViewTitle.visibility = View.GONE
                topBarExtraLay.visibility = View.GONE
                changeToolBarColor(Constants.MENU_CARDS)
            }

            3 -> {
                /*var myVisitFragment=MyVisitFragment.newInstance(toolBar)
                fragmentFucntion.replaceFragment(this, myVisitFragment, R.id.container)
                BaseActivity.textViewTitle.visibility = View.VISIBLE
                BaseActivity.topRelative.visibility=View.VISIBLE*/
                if (sharedPref.getString(Constants.ROLE) != "2") {
                    visitBool = true

                    var visitfragment: MyVisitFragment =
                        MyVisitFragment.newInstance2(object : InterfaceBackAlert {

                            override fun Sucess(bool: Boolean) {
                                if (bool) {
                                    alertDialogWithOKButton2(
                                        getString(R.string.alert),
                                        getString(R.string.no_data_found)
                                    )
                                }
                            }
                        })
                    fragmentFucntion.replaceFragment(
                        this,
                        visitfragment,
                        R.id.container
                    )
                    changeToolBarColor(Constants.MENU_VISITS)
                    BaseActivity.textViewTitle.visibility = View.VISIBLE
                    topBarExtraLay.visibility = View.VISIBLE
                    llTitleBarCorner.setBackgroundDrawable(resources.getDrawable(R.drawable.bd_voilet_bl_round_90))
                    bottomNavigation.show(4, true)
                    textViewHome.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewOrders.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewNotification.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewMe.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.colorPrimary
                        )
                    )
                } else {
                    visitBool = false
                    appBarLayout1.background = null
                    bottomNaigationLinear.visibility = View.VISIBLE
                    fragmentFucntion.replaceFragment(this, InprogressFragment(), R.id.container)
                    BaseActivity.textViewTitle.visibility = View.GONE
                    topBarExtraLay.visibility = View.GONE
                }

                /*startActivity(Intent(this@HomeActivity, VisitActivity::class.java))
                finishAffinity()*/
                // BaseActivity.topRelative.background=resources?.getColor(R.color.white)
                //  toolBar?.background=resources?.getDrawable(R.drawable.bg_voilet_bl_round_40)
            }

            4 -> {
                visitBool = false
                appBarLayout1.background = null
                bottomNaigationLinear.visibility = View.VISIBLE
                fragmentFucntion.replaceFragment(this, InprogressFragment(), R.id.container)
                BaseActivity.textViewTitle.visibility = View.GONE
                topBarExtraLay.visibility = View.GONE
                changeToolBarColor(Constants.MENU_SETTINGS)
            }

            else -> {
                visitBool = false
                appBarLayout1.background = null
                bottomNaigationLinear.visibility = View.VISIBLE
                fragmentFucntion.replaceFragment(this, InprogressFragment(), R.id.container)
                BaseActivity.textViewTitle.visibility = View.GONE
                topBarExtraLay.visibility = View.GONE
            }
        }
        closeOrOpenDrawer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        content = findViewById(R.id.content)
        mainLayoutShadow = findViewById(R.id.content2)
        toolBar = findViewById<Toolbar>(R.id.toolBar1)
        textTop = findViewById<TextView>(R.id.textHome)
        homeRelative = findViewById<RelativeLayout>(R.id.content)
        topBarExtraLay = findViewById<LinearLayout>(R.id.topBarExtra)
        bottomNaigationLinear = findViewById<LinearLayout>(R.id.bottomNavigationlayout)
        bottomNaigationLinear.visibility = View.VISIBLE
        frequentFunctions.changeStatusBarColor(
            this@HomeActivity,
            ContextCompat.getColor(this@HomeActivity, android.R.color.transparent)
        )
        language = sharedPref.getStringLanguage(Constants.USER_Language)
        drawerSlide()
        setNavigationAdapter()
        setUpBottomNavigationBar()
        customToolBarWithMenu(this, toolBar)
        initObserverFun()
        fragmentFucntion.replaceFragment(this, HomeFragment(), R.id.container)
        menuClickListener()
        logoutClick()
        setData()
        topBarExtraLay.visibility = View.GONE

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //KeyboardVisibilityEvent.setEventListener(this,this)
    }

    private fun initObserverFun() {
        viewModel.onSuccessfullyUpdateLocation.observe(this, Observer {
            Log.e("LocationUpdated", it)
        })
    }

    public override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if (!checkPermissionss()) {
            startLocationPermissionRequests()
        } else {
            getLastLocation()
            if (mLatitudeLabel == "")
                startLocation()
        }
    }

    override fun onStop() {
        super.onStop()
        stopLocation()
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        if (isOpen) {
            bottomNavigationlayout.visibility = View.GONE
        } else {
            bottomNavigationlayout.visibility = View.VISIBLE
        }
    }

    fun setData() {
        commonFunctions.loadCircularImage(
            this,
            sharedPref.getString(Constants.USER_IMAGE),
            circularImageView
        )
        textViewUserName.setText(sharedPref.getString(Constants.USER_NAME))
        userType = sharedPref.getString(Constants.ROLE)

    }

    private fun logoutClick() {
        linearLayoutLogout.setOnClickListener {
            closeOrOpenDrawer()
            var alertDialog = AlertDialog.Builder(this).also {
                it.setTitle(getString(R.string.logout))
                it.setMessage(getString(R.string.are_you_sure_to_log_out))
                it.setPositiveButton(
                    getString(R.string.yes),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.dismiss()
                            sharedPref.clear()
                            var int = Intent(this@HomeActivity, SignupActivity::class.java)
                            int.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(int)
                            finish()
                        }
                    })
                it.setNegativeButton(
                    getString(R.string.no),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.dismiss()
                        }
                    })
            }
            alertDialog.show()
        }
    }

    fun drawerSlide() {
        var scaleFactor = 4f
        var cornerRadiusFactorTop = 55f
        var cornerRadiusFactorBottom = 100f
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        val actionBarDrawerToggle =
            object : ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)
                    val slideX = drawerView.width * slideOffset
                    if (language == "ar") {
                        content.translationX = -slideX
                    } else {
                        content.translationX = slideX
                    }
                    content.scaleX = 1 - (slideOffset / scaleFactor)
                    content.scaleY = 1 - (slideOffset / scaleFactor)

                    var scaleFactor2 = 19f
                    val slideX2 = drawerView.width + 20 * slideOffset
                    if (language == "ar") {
                        content2.translationX = -slideX2
                    } else {
                        content2.translationX = slideX2
                    }
                    content2.scaleX = 1 - (slideOffset / scaleFactor2)
                    content2.scaleY = 1 - (slideOffset / scaleFactor2 * 6)


                    val mainLayoutBackground = GradientDrawable()
                    if (!visitBool) {
                        //  visitBool=true
                        mainLayoutBackground.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorAccent
                            )
                        )
                    } else {
                        // visitBool=false
                        mainLayoutBackground.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorDarkSkyBlue
                            )
                        )
                    }
                    mainLayoutBackground.setCornerRadii(
                        floatArrayOf(
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset
                        )
                    )
                    content.background = mainLayoutBackground


                    val shadow = GradientDrawable()
                    shadow.setColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.colorDarkGray
                        )
                    )
                    shadow.setCornerRadii(
                        floatArrayOf(
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset
                        )
                    )
                    mainLayoutShadow.background = shadow


                    val toolBarGradeint = GradientDrawable()
                    if (!visitBool) {
                        toolBarGradeint.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorAccent
                            )
                        )
                    } else {

                        toolBarGradeint.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorDarkSkyBlue
                            )
                        )

                    }

                    toolBarGradeint.cornerRadii = floatArrayOf(
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset,
                        cornerRadiusFactorTop * slideOffset

                    )


                    appBarLayout1.background = toolBarGradeint


                    val bottomBarGradient = GradientDrawable()
                    bottomBarGradient.setColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.white
                        )
                    )

                    if (language == "ar") {
                        bottomBarGradient.setCornerRadii(
                            floatArrayOf(
                                0f,
                                0f,
                                0f,
                                0f,
                                cornerRadiusFactorBottom * slideOffset,
                                cornerRadiusFactorBottom * slideOffset,
                                0f,
                                0f
                            )
                        )
                    } else {
                        bottomBarGradient.setCornerRadii(
                            floatArrayOf(
                                0f,
                                0f,
                                0f,
                                0f,
                                0f,
                                0f,
                                cornerRadiusFactorBottom * slideOffset,
                                cornerRadiusFactorBottom * slideOffset
                            )
                        )
                    }
                    bottomLayout.background = bottomBarGradient

                }
            }

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
    }


    fun setNavigationAdapter() {
        Log.e("UserType", "user type is   " + sharedPref.getString(Constants.ROLE))
        var type = sharedPref.getString(Constants.ROLE)
        var navigationItems: Array<String>
        if (type == "2") {
            navigationItems = resources.getStringArray(R.array.navigation_items_buyer)

        } else {
            navigationItems = resources.getStringArray(R.array.navigation_items)


        }
        //  var navigationsItems = resources.getStringArray(R.array.navigation_items)
        var icons = resources.obtainTypedArray(R.array.navigation_icons)
        var adapter = NavigationAdapter(navigationItems, icons)
        recyclerViewNavigation.layoutManager = LinearLayoutManager(this)
        recyclerViewNavigation.adapter = adapter
        adapter.setDrawerListener(this)
    }

    fun setUpBottomNavigationBar() {
        bottomNavigation = findViewById<MeowBottomNavigation>(R.id.meowBottom)
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_orders))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_notification))
        bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.ic_user))
        bottomNavigation.show(1, true)



        bottomNavigation.setOnClickMenuListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
            when (it.id) {

                1 -> {
                    visitBool = false
                    topBarExtraLay.visibility = View.GONE
                    //bottomNaigationLinear.visibility=View.VISIBLE
                    if (!visitBool) {
                        BaseActivity.appBarLayout.background = null
                        BaseActivity.topRelative.background = null
                        textTop?.visibility = View.GONE
                        BaseActivity.textViewTitle.visibility = View.VISIBLE

                    } else {
                        textTop?.visibility = View.VISIBLE
                        BaseActivity.textViewTitle.visibility = View.GONE

                    }
                    val mainLayoutBackground = GradientDrawable()
                    if (!visitBool) {
                        //  visitBool=true
                        mainLayoutBackground.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorAccent
                            )
                        )
                    } else {
                        // visitBool=false
                        mainLayoutBackground.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorDarkSkyBlue
                            )
                        )
                    }
                    content.background = mainLayoutBackground
                    val toolBarGradeint = GradientDrawable()
                    if (!visitBool) {
                        toolBarGradeint.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorAccent
                            )
                        )
                    } else {
                        toolBarGradeint.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorDarkSkyBlue
                            )
                        )

                    }
                    appBarLayout1.background = toolBarGradeint
                    textViewHome.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.colorPrimary
                        )
                    )
                    textViewOrders.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewNotification.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewMe.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    BaseActivity.imageViewMenu.setImageResource(R.drawable.ic_menu_2)
                    imageViewMenu.setOnClickListener {
                        closeOrOpenDrawer()
                    }
                    if (currentFragment !is HomeFragment) {
                        fragmentFucntion.replaceFragment(this, HomeFragment(), R.id.container)
                    }
                    changeToolBarColor(Constants.MENU_HOME)
                }

                2 -> {
                    visitBool = false
                    topBarExtraLay.visibility = View.VISIBLE
                    llTitleBarCorner.setBackgroundDrawable(resources.getDrawable(R.drawable.bd_voilet_light_bl_round_90))
                    //  bottomNaigationLinear.visibility=View.VISIBLE
                    if (!visitBool) {
                        BaseActivity.appBarLayout.background = null
                        BaseActivity.topRelative.background = null
                        textTop.visibility = View.GONE
                        BaseActivity.textViewTitle.visibility = View.VISIBLE
                        BaseActivity.textViewTitle.text = "Orders"
                    } else {
                        textTop.visibility = View.VISIBLE

                        BaseActivity.textViewTitle.visibility = View.GONE
                    }

                    val mainLayoutBackground = GradientDrawable()
                    content.background = mainLayoutBackground
                    val toolBarGradeint = GradientDrawable()
                    toolBarGradeint.setColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.colorBlueO
                        )
                    )
                    appBarLayout1.background = toolBarGradeint
                    mainLayoutBackground.setColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.colorBlueO
                        )
                    )
                    content.background = mainLayoutBackground
                    textViewHome.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewOrders.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.colorPrimary
                        )
                    )
                    textViewNotification.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewMe.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    BaseActivity.imageViewMenu.setImageResource(R.drawable.ic_menu_2)
                    imageViewMenu.setOnClickListener {
                        closeOrOpenDrawer()
                    }
                    if (currentFragment !is MyOrdersFragment) {
                        fragmentFucntion.replaceFragment(this, MyOrdersFragment(), R.id.container)
                    }
                }

                3 -> {
                    visitBool = false
                    topBarExtraLay.visibility = View.GONE
                    //  bottomNaigationLinear.visibility=View.VISIBLE

                    if (!visitBool) {
                        BaseActivity.appBarLayout.background = null
                        BaseActivity.topRelative.background = null
                        textTop?.visibility = View.GONE
                        BaseActivity.textViewTitle.visibility = View.VISIBLE

                    } else {
                        textTop?.visibility = View.VISIBLE
                        BaseActivity.textViewTitle.visibility = View.GONE

                    }

                    val mainLayoutBackground = GradientDrawable()
                    if (!visitBool) {
                        //  visitBool=true
                        mainLayoutBackground.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorAccent
                            )
                        )
                    } else {
                        // visitBool=false
                        mainLayoutBackground.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorDarkSkyBlue
                            )
                        )
                    }
                    content.background = mainLayoutBackground
                    val toolBarGradeint = GradientDrawable()
                    if (!visitBool) {
                        toolBarGradeint.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorAccent
                            )
                        )
                    } else {
                        toolBarGradeint.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorDarkSkyBlue
                            )
                        )

                    }
                    appBarLayout1.background = toolBarGradeint
                    content.background = mainLayoutBackground
                    textViewHome.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewOrders.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewNotification.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.colorPrimary
                        )
                    )
                    textViewMe.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    BaseActivity.imageViewMenu.setImageResource(R.drawable.ic_menu_2)
                    imageViewMenu.setOnClickListener {
                        closeOrOpenDrawer()
                    }
                    if (currentFragment !is InprogressFragment) {
                        fragmentFucntion.replaceFragment(this, InprogressFragment(), R.id.container)
                    }
                    changeToolBarColor(Constants.MENU_NOTIFICATION)

                    BaseActivity.textViewTitle.visibility = View.GONE
                }
                4 -> {
                    visitBool = false
                    topBarExtraLay.visibility = View.GONE
                    //bottomNaigationLinear.visibility=View.VISIBLE
                    if (!visitBool) {
                        BaseActivity.appBarLayout.background = null
                        BaseActivity.topRelative.background = null
                        textTop?.visibility = View.GONE
                        BaseActivity.textViewTitle.visibility = View.VISIBLE

                    } else {
                        textTop?.visibility = View.VISIBLE
                        BaseActivity.textViewTitle.visibility = View.GONE

                    }
                    val mainLayoutBackground = GradientDrawable()
                    if (!visitBool) {
                        //  visitBool=true
                        mainLayoutBackground.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorAccent
                            )
                        )
                    } else {
                        // visitBool=false
                        mainLayoutBackground.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorDarkSkyBlue
                            )
                        )
                    }

                    content.background = mainLayoutBackground
                    val toolBarGradeint = GradientDrawable()
                    if (!visitBool) {
                        toolBarGradeint.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorAccent
                            )
                        )
                    } else {
                        toolBarGradeint.setColor(
                            ContextCompat.getColor(
                                this@HomeActivity,
                                R.color.colorDarkSkyBlue
                            )
                        )

                    }
                    appBarLayout1.background = toolBarGradeint
                    textViewHome.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewOrders.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewNotification.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            android.R.color.black
                        )
                    )
                    textViewMe.setTextColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.colorPrimary
                        )
                    )
                    BaseActivity.imageViewMenu.setImageResource(R.drawable.ic_menu_2)
                    imageViewMenu.setOnClickListener {
                        closeOrOpenDrawer()
                    }

                    if (userType == "2") {
                        if (currentFragment !is CustomerProfile) {
                            fragmentFucntion.replaceFragment(
                                this,
                                CustomerProfile(),
                                R.id.container
                            )
                        }
                    } else {
                        if (currentFragment !is ServiceProviderProfile) {
                            fragmentFucntion.replaceFragment(
                                this,
                                ServiceProviderProfile(),
                                R.id.container
                            )
                        }
                    }
                    changeToolBarColor(Constants.MENU_ME)
                }
            }
        }
    }

    private fun menuClickListener() {
        if (!visitBool) {
            BaseActivity.appBarLayout.background = null
            BaseActivity.topRelative.background = null

            textTop?.visibility = View.GONE
            BaseActivity.textViewTitle.visibility = View.VISIBLE

        } else {
            appBarLayout1.background = null
            textTop?.visibility = View.VISIBLE
            BaseActivity.textViewTitle.visibility = View.GONE

        }

        imageViewMenu.setOnClickListener {
            closeOrOpenDrawer()
        }
    }

    fun closeOrOpenDrawer() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun checkPermissionss(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequests() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this@HomeActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        } else {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsProviderEnabled: Boolean
            val isNetworkProviderEnabled: Boolean
            isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkProviderEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsProviderEnabled && !isNetworkProviderEnabled) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Location Permission")
                builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.")
                builder.setPositiveButton(
                    android.R.string.yes,
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface: DialogInterface, i: Int) {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                    })
                builder.setNegativeButton(android.R.string.no, null)
                builder.show()

            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i("ocationupdate", "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                // getLastLocation()
            } else {
                startLocationPermissionRequest()
            }
        }
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocation()
    }

    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result

                    mLatitudeLabel = (mLastLocation)!!.latitude.toString()
                    mLongitudeLabel = (mLastLocation)!!.longitude.toString()

                    sharedPref.setString(Constants.latitude, mLatitudeLabel)
                    sharedPref.setString(Constants.longitude, mLongitudeLabel)

                    updateLocation(mLatitudeLabel, mLongitudeLabel, mLastLocation!!)
                } else {
                    Log.w("error", "getLastLocation:exception", task.exception)
                    // showMessage(getString(R.string.no_location_detected))
                }
            }
    }

    private fun updateLocation(latitude: String, longitude: String, location: Location) {
        var address = ""
        val listener = OnReverseGeocodingListener { original, results ->
            if (results.size > 0) {
                try {
                    address = results[0].getAddressLine(0)
                    Log.e("location", "" + results[0].getAddressLine(0))
                } catch (e: Exception) {
                    address = results[0].locality
                    Log.e("location", "" + results[0].locality)
                }

                sharedPref.setString(Constants.ADDRESS, address)
                sharedPref.setString(Constants.COUNTRY_CODE, results[0].countryCode)

                viewModel.lat.set(latitude)
                viewModel.lng.set(longitude)
                viewModel.address.set(address)
                viewModel.updateAddress()
            }
        }
        SmartLocation.with(this).geocoding().reverse(location, listener)
    }

    override fun onLocationUpdated(location: Location?) {
        Log.e("mainActivityLocation", "" + location!!.longitude + "," + location!!.latitude)

        val latitude: String = location.latitude.toString()
        val longitude: String = location.longitude.toString()

        mLatitudeLabel = latitude
        mLongitudeLabel = longitude

        sharedPref.setString(Constants.latitude, mLatitudeLabel)
        sharedPref.setString(Constants.longitude, mLongitudeLabel)
        updateLocation(mLatitudeLabel, mLongitudeLabel, location)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (provider != null) {
                provider!!.onActivityResult(requestCode, resultCode, data)
            }
        } catch (e: Exception) {
            Log.e("vvv", "" + e.message)
        }
    }

    fun startLocation() {
        provider = LocationGooglePlayServicesProvider()
        provider!!.setCheckLocationSettings(true)
        val smartLocation = SmartLocation.Builder(this@HomeActivity).logging(true).build()
        smartLocation.location(provider).start(this)
    }

    private fun stopLocation() {
        SmartLocation.with(this@HomeActivity).location().stop()
        SmartLocation.with(this@HomeActivity).activity().stop()
        SmartLocation.with(this@HomeActivity).geofencing().stop()
    }

    private fun changeToolBarColor(menu:Int)
    {
        if (menu==Constants.MENU_HOME||menu==Constants.MENU_ME||menu==Constants.MENU_NOTIFICATION||menu==Constants.MENU_SETTINGS||menu==Constants.MENU_SETTINGS||menu==Constants.MENU_CARDS||menu==Constants.MENU_WALLET)
        {
            topBarExtra.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorAccent
                )
            )

            toolBar1.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorAccent
                )
            )

            llTitleBarCorner.setBackgroundDrawable(resources.getDrawable(R.drawable.bd_accent_bl_round_90))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = this.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = this.resources.getColor(R.color.colorAccent)
            }
        }
        else if (menu==Constants.MENU_VISITS)
        {
            topBarExtra.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.lightadeblue
                )
            )
            toolBar1.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorDarkSkyBlue
                )
            )
            llTitleBarCorner.setBackgroundDrawable(resources.getDrawable(R.drawable.bd_voilet_bl_round_90))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = this.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = this.resources.getColor(R.color.colorDarkSkyBlue)
            }
        }
    }
}
