package com.deepak.besaat.view.fragments.courierGuysMapFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deepak.besaat.R
import com.deepak.besaat.view.fragments.BaseFragment
import com.deepak.besaat.view.fragments.serviceMapFragment.ServiceInfoWindow
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CourierGuysMapFragment:BaseFragment(), OnMapReadyCallback {
    var googleMap: GoogleMap? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_courier_guys_map, container, false)
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.googleMap = map
        var latLng = LatLng(30.7046, 76.7179)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_green));
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
        googleMap!!.setInfoWindowAdapter(
            CourierGuysInfoWindow(
                activity!!
            )
        )
        googleMap!!.addMarker(markerOptions)
    }
}