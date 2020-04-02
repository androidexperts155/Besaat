package com.deepak.besaat.view.fragments.courierGuysMapFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.deepak.besaat.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CourierGuysInfoWindow implements  GoogleMap.InfoWindowAdapter  {
    Context context;
    LayoutInflater inflater;

    public CourierGuysInfoWindow(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.view_courier_guyzs_info_window,null);
        return v;
    }

}
