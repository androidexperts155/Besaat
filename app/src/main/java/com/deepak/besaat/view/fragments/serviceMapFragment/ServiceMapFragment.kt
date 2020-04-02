package com.deepak.besaat.view.fragments.serviceMapFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deepak.besaat.R
import com.deepak.besaat.view.fragments.BaseFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class ServiceMapFragment:BaseFragment(), OnMapReadyCallback {
    var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_store_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.googleMap = map
//        var latLng = LatLng(30.7046, 76.7179)
//        val markerOptions = MarkerOptions()
//        markerOptions.position(latLng)
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_green));
//        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
//        googleMap!!.setInfoWindowAdapter(
//            ServiceInfoWindow(
//                activity!!
//            )
//        )
//        googleMap!!.addMarker(markerOptions)
    }
}