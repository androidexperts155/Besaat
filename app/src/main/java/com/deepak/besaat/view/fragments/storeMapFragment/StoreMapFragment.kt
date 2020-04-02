package com.deepak.besaat.view.fragments.storeMapFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.view.fragments.BaseFragment

import android.graphics.drawable.BitmapDrawable
import com.deepak.besaat.Interfaces.InfoWIndowClickListener
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.newRequestStore.NewRequestStore
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import org.koin.android.ext.android.inject
import com.deepak.besaat.R

import com.appolica.interactiveinfowindow.InfoWindow
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener

import android.widget.FrameLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.appolica.interactiveinfowindow.InfoWindowManager
import com.deepak.besaat.Interfaces.getMapViewComponent
import com.deepak.besaat.utils.FragmentFucntions
import com.deepak.besaat.view.fragments.MapFragment.MapFragment
import com.deepak.besaat.view.fragments.storeListingFragment.adapter.StoresListAdapter
import com.squareup.picasso.Picasso
import java.text.DecimalFormat


import de.hdodenhof.circleimageview.CircleImageView


class StoreMapFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private var infoButtonListener: OnInfoWindowElemTouchListener? = null

    private var recyclerWindow: InfoWindow? = null
    private var infoWindow2: InfoWindow? = null
    private var infoWindowManager: InfoWindowManager? = null
    private var mapWrapperLayout: MapWrapperLayout? = null
    private var infoWindow: ViewGroup? = null
    var infoListener: InfoWIndowClickListener? = null
    private var not_first_time_showing_info_window = false
    var textName: TextView? = null
    var textAddress: TextView? = null
    var textRadius: TextView? = null
    var textRating: TextView? = null
    var textRequest: TextView? = null
    var ratingBar: RatingBar? = null
    var value: String = ""
    var catName:String=""
    var markerIcon:String=""
    var circleImageView: CircleImageView? = null
    var infoWindoww: ViewGroup? = null
    var frameLayout: FrameLayout? = null
    var arrayList: ArrayList<SourceDetails> = ArrayList<SourceDetails>()
    val fragmentFucntions: FragmentFucntions by inject()
    //private var infoButtonListener: OnInfoWindowElemTouchListener? = null


    override fun onInfoWindowClick(marker: Marker?) {
        Log.e("markerInfo", "marker info is " + marker?.position?.latitude)

        val intent = Intent(activity, NewRequestStore::class.java)
        intent.putExtra("latitute", marker?.position?.latitude)
        intent.putExtra("longitute", marker?.position?.longitude)
        intent.putExtra("from", "MapFrag")
        activity?.startActivity(intent)

    }

    var googleMap: GoogleMap? = null
    var lat: Double? = null
    var long: Double? = null
    val sharedPref: SharedPref by inject()
    var sourceDetails: MutableList<SourceDetails> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_store_map, container, false)
        return view
    }

    fun smallMarker(): Bitmap {
        val height = 50
        val width = 50
        val bitmapdraw = resources.getDrawable(R.drawable.ic_restraurentplaces) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        return smallMarker
    }

    //  @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map)

        //val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment?
        mapWrapperLayout = view.findViewById(R.id.map_relative_layout) as MapWrapperLayout
        var iterator: MutableListIterator<SourceDetails>
        if (value.equals("")) {
            iterator = sourceDetails.listIterator()
        } else {
            var storeListFiltered: MutableList<SourceDetails> = ArrayList()
            // storeListFiltered.addAll(servicesList)
            // storeListFiltered.retainAll{ it.name.toString().toLowerCase().contains(value.toLowerCase()) }
            for (item in sourceDetails) {
                if (item.name.toString().toLowerCase().contains(value.toLowerCase())) {
                    storeListFiltered.add(item)
                }
            }
            iterator = storeListFiltered.listIterator()
        }
        //   frameLayout=view.findViewById(R.id.frameMapContainer)
        //  val iterator = sourceDetails?.listIterator()
        if (iterator != null) {
            var latLng: LatLng? = null
            for ((i, item) in iterator.withIndex()) {
                Log.e("Map", "map fragment list $i item $item")
                latLng = LatLng(item.latitute!!, item.longitute!!)
                // drawMarker(latLng,item)
                arrayList.add(item)
                //  googleMap?.setOnMarkerClickListener(this)
                Log.e("Map", "iterator next elelment " + iterator.hasNext())
                /* if(!iterator.hasNext()){
                     Log.e("Map","lat long of a map "+latLng+ " google map  "+googleMap)
                     //  googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
                     googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f))

                 }*/
                // bld.include(latLng);
            }
            MapFragment.setDetails(arrayList, sharedPref,catName!!,markerIcon!!)
        }
        //mapFragment.getMapAsync(this)
    }


    override fun onMapReady(map: GoogleMap?) {
        this.googleMap = map
        if (map != null) {
            mapWrapperLayout?.init(map, getPixelsFromDp(this, (39 + 20).toFloat()))
        }


        infoWindow =
            layoutInflater.inflate(R.layout.view_store_info_window, null, false) as ViewGroup?

        circleImageView = infoWindow?.findViewById(R.id.circleImageView)
        textName = infoWindow?.findViewById(R.id.textView19)
        textAddress = infoWindow?.findViewById(R.id.textView20)
        textRadius = infoWindow?.findViewById(R.id.textView23)
        textRating = infoWindow?.findViewById(R.id.textRating)
        textRequest = infoWindow?.findViewById(R.id.textViewRequest)
        ratingBar = infoWindow?.findViewById(R.id.ratingB)

        // var latLng = LatLng(30.7046, 76.7179)
        val bld = LatLngBounds.Builder()
        val iterator = sourceDetails?.listIterator()
        if (iterator != null) {
            var latLng: LatLng? = null
            for ((i, item) in iterator.withIndex()) {
                Log.e("Map", "map fragment list " + i + " item " + item)
                latLng = LatLng(item.latitute!!, item.longitute!!)
                drawMarker(latLng, item)
                //  googleMap?.setOnMarkerClickListener(this)
                Log.e("Map", "iterator next elelment " + iterator.hasNext())
                if (!iterator.hasNext()) {
                    Log.e("Map", "lat long of a map " + latLng + " google map  " + googleMap)
                    //  googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f))

                }
                // bld.include(latLng);
            }

        }
        //   val bounds = bld.build()
        // val markerOptions = MarkerOptions()
        // markerOptions.position(latLng)
        //  markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_green));
        var storeInfoWindow =
            StoreInfoWindow(activity!!, sharedPref, mapWrapperLayout, object : getMapViewComponent {
                override fun viewClicked(view: View, lat: Double, long: Double, marker: Marker) {
                    val textRequest = view?.findViewById(R.id.textViewRequest) as View

                    textRequest.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            val intent = Intent(activity, NewRequestStore::class.java)
                            intent.putExtra("latitute", lat)
                            intent.putExtra("longitute", long)
                            intent.putExtra("from", "MapFrag")
                            activity?.startActivity(intent)
                        }
                    })

                    mapWrapperLayout?.setMarkerWithInfoWindow(marker, view)

                    infoButtonListener = object : OnInfoWindowElemTouchListener(
                        textRequest,
                        activity?.getResources()?.getDrawable(R.drawable.arrow_down)!!,
                        activity?.getResources()?.getDrawable(R.drawable.arrow_down)!!
                    ) {
                        override fun onClickConfirmed(v: View, marker: Marker?) {
                            // Here we can perform some action triggered after clicking the button
                            Toast.makeText(
                                activity,
                                marker!!.title + "'s button clicked!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(activity, NewRequestStore::class.java)
                            intent.putExtra("latitute", lat)
                            intent.putExtra("longitute", long)
                            intent.putExtra("from", "MapFrag")
                            activity?.startActivity(intent)
                        }
                    }
                    /* val intent = Intent(activity, NewRequestStore::class.java)
                     intent.putExtra("latitute",lat)
                     intent.putExtra("longitute", long)
                     intent.putExtra("from","MapFrag")
                     activity?.startActivity(intent)*/
                }
            })
        //  storeInfoWindow.setClickListener(infoWIndowClickListener)
        /*  googleMap!!.setInfoWindowAdapter(
              storeInfoWindow

          )*/

        googleMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {


                val sourceDetails = marker.tag as SourceDetails?
                val profilePicUrl =
                    "https://maps.googleapis.com/maps/api/place/photo?photoreference=" + sourceDetails!!.photoRefrence + "&sensor=false&maxheight=" + sourceDetails.heightt + "&maxwidth=" + sourceDetails.widthh + "&key=" + activity?.getResources()?.getString(
                        R.string.google_api
                    )
                /*  Glide.with(activity)
                .load(profilePicUrl)
                .placeholder(R.drawable.placeholder)

                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .transform(new CircleCrop()) //4
                .into(circleImageView);*/

                if (not_first_time_showing_info_window) {
                    Picasso.get()
                        .load(profilePicUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(circleImageView)
                } else { // if it's the first time, load the image with the callback set
                    not_first_time_showing_info_window = true
                    Picasso.get()
                        .load(profilePicUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(circleImageView)
                }


                Log.e(
                    "StoreInfo",
                    "Store info object " + sourceDetails.name + "  " + sourceDetails.locAddress + " " + sourceDetails.rating
                )
                textName?.setText(sourceDetails.name)
                textAddress?.setText(sourceDetails.locAddress)


                val latLng1 = LatLng(
                    java.lang.Double.parseDouble(sharedPref.getString("latitude")),
                    java.lang.Double.parseDouble(sharedPref.getString("longitude"))
                )
                val latLng2 = LatLng(
                    sourceDetails.latitute!!.toDouble(),
                    sourceDetails.longitute!!.toDouble()
                )
                val radiusValue = CalculationByDistance(latLng1, latLng2)
                textRadius?.setText(radiusValue.toString().substring(0, 4) + " km")
                textRating?.setText(sourceDetails.rating!!.toString())
                ratingBar?.setRating(sourceDetails.rating!!)
                // Setting up the infoWindow with current's marker info
                // infoTitle.setText(marker.title)
                //infoSnippet.setText(marker.snippet)
                //infoButtonListener.setMarker(marker)

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout?.setMarkerWithInfoWindow(marker, infoWindow!!)
                return infoWindow as ViewGroup
            }
        })

        infoButtonListener = object : OnInfoWindowElemTouchListener(
            this!!.textRequest!!,
            activity?.getResources()?.getDrawable(R.drawable.arrow_down)!!,
            activity?.getResources()?.getDrawable(R.drawable.arrow_down)!!
        ) {
            override fun onClickConfirmed(v: View, marker: Marker?) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(
                    activity,
                    marker!!.title + "'s button clicked!",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(activity, NewRequestStore::class.java)
                intent.putExtra("latitute", lat)
                intent.putExtra("longitute", long)
                intent.putExtra("from", "MapFrag")
                activity?.startActivity(intent)
            }
        }
        textRequest?.setOnTouchListener(infoButtonListener)

        googleMap?.setOnInfoWindowClickListener(this);

        googleMap?.setOnMarkerClickListener(OnMarkerClickListener { mark ->
            mark.showInfoWindow()


            true
        })
        //  googleMap!!.addMarker(markerOptions)
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

        return Radius * c
    }

    fun getPixelsFromDp(context: StoreMapFragment, dp: Float): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (dp * scale + 0.5f).toInt()
    }

    var infoWIndowClickListener = object : InfoWIndowClickListener {

        override fun crossClick(sucess: Boolean) {

        }

        override fun requestClick(sucess: Boolean) {

        }

        override fun viewClick(sucess: Boolean) {

        }


    }

    private fun drawMarker(
        point: LatLng,
        item: SourceDetails
    ) {
        val markerOptions = MarkerOptions()
        markerOptions.position(point);
        // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_green));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker()))
        var marker: Marker = googleMap!!.addMarker(markerOptions)
        var sourceDetail: SourceDetails = SourceDetails()
        sourceDetail.icon = item.icon
        sourceDetail.name = item.name
        sourceDetail.locAddress = item.locAddress
        sourceDetail.rating = item.rating
        sourceDetail.latitute = item.latitute
        sourceDetail.longitute = item.longitute
        if (item.photoRefrence != null) {
            sourceDetail.photoRefrence = item.photoRefrence
            sourceDetail.heightt = item.heightt
            sourceDetail.widthh = item.heightt
        }
        // var mapFragment =MapViewFragment.newInstance(item)
        //   val offsetX = resources.getDimension(R.dimens.marker_offset_x).toInt()
        // val offsetY = resources.getDimension(R.dimens.marker_offset_y).toInt()

        // val markerSpec = InfoWindow.MarkerSpecification(offsetX, offsetY)

        marker.tag = sourceDetail

        //   val offsetX = resources.getDimension(R.dimen.marker_offset_x).toInt()
        //  val offsetY = resources.getDimension(R.dimen.marker_offset_y).toInt()

        ////  val markerSpec = InfoWindow.MarkerSpecification(offsetX, offsetY)
        // formWindow  = InfoWindow(marker, markerSpec, MapViewFragment())
        //formWindow = InfoWindow(marker2, markerSpec, FormFragment())

        marker.showInfoWindow()
    }

    companion object {
        @JvmStatic
        fun newInstance(
            sourceDetails: MutableList<SourceDetails>,
            valu: String,
            catName: String,
            markerIcon: String
        ): StoreMapFragment {
            var fragment: StoreMapFragment = StoreMapFragment()
            fragment.sourceDetails = sourceDetails
            fragment.value = valu
            fragment.catName=catName
            fragment.markerIcon=markerIcon
            return fragment

        }
        /* BlankFragment().apply {
             arguments = Bundle().apply {
                 putString(ARG_PARAM1, param1)
                 putString(ARG_PARAM2, param2)
             }
         }*/
    }
}