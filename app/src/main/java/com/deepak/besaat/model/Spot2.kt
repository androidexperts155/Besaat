package com.deepak.besaat.model

import com.google.android.gms.maps.model.LatLng
class Spot2(name:String, position:LatLng) {
    var name:String
    var position:LatLng
    init{
        this.name = name
        this.position = position
    }
    fun put(name: String,position: LatLng){

        this.name=name
        this.position = position
    }
}