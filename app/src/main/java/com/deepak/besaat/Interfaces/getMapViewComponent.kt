package com.deepak.besaat.Interfaces

import android.view.View
import com.google.android.gms.maps.model.Marker

interface getMapViewComponent {
    fun viewClicked(
        view: View,
        v: Double,
        doubleValue: Double,
        marker: Marker
    )
}