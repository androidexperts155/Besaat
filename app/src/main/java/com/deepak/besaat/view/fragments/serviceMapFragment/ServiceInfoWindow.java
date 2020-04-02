package com.deepak.besaat.view.fragments.serviceMapFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.deepak.besaat.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class ServiceInfoWindow implements GoogleMap.InfoWindowAdapter  {

    Context context;
    LayoutInflater inflater;

    public ServiceInfoWindow(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.view_service_info_window,null);
        return v;
    }
}
