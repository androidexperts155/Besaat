package com.deepak.besaat.view.fragments.storeMapFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.deepak.besaat.Interfaces.InfoWIndowClickListener;
import com.deepak.besaat.Interfaces.getMapViewComponent;
import com.deepak.besaat.R;
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails;
import com.deepak.besaat.utils.SharedPref;
import com.deepak.besaat.view.activities.newRequestStore.NewRequestStore;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreInfoWindow implements GoogleMap.InfoWindowAdapter {
    Context context;
    FragmentActivity activity;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private getMapViewComponent getMapViewComponent;
    LayoutInflater inflater;
    SharedPref sharedPref;
    MapWrapperLayout mapWrapperLayout;
    private InfoWIndowClickListener infoWIndowClickListener;
    private boolean not_first_time_showing_info_window=false;
   ArrayList<SourceDetails>   sourceDetails = new ArrayList<>();

    public StoreInfoWindow(FragmentActivity activity, SharedPref sharedPref, MapWrapperLayout mapWrapperLayout, getMapViewComponent getMapViewComponent) {
        this.activity = (FragmentActivity) activity;
        this.sharedPref=sharedPref;
      this.mapWrapperLayout=mapWrapperLayout;
      this.getMapViewComponent=getMapViewComponent;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = activity.getLayoutInflater().inflate(R.layout.view_store_info_window, null,false);
        final SourceDetails sourceDetails= (SourceDetails) marker.getTag();

        getMapViewComponent.viewClicked(view,sourceDetails.getLatitute(),sourceDetails.getLongitute(),marker);
        CircleImageView circleImageView=view.findViewById(R.id.circleImageView);
        TextView textName=view.findViewById(R.id.textView19);
        TextView textAddress= view.findViewById(R.id.textView20);
        TextView textRadius= view.findViewById(R.id.textView23);
        TextView textRating=view.findViewById(R.id.textRating);
        TextView textRequest= view.findViewById(R.id.textViewRequest);
        RatingBar ratingBar=view.findViewById(R.id.ratingB);


        String profilePicUrl = "https://maps.googleapis.com/maps/api/place/photo?photoreference=" +sourceDetails.getPhotoRefrence() + "&sensor=false&maxheight=" + sourceDetails.getHeightt() + "&maxwidth=" + sourceDetails.getWidthh()+ "&key=" + activity.getResources().getString(R.string.google_api);
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
                    .into(circleImageView);
        } else { // if it's the first time, load the image with the callback set
            not_first_time_showing_info_window=true;
            Picasso.get()
                    .load(profilePicUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(circleImageView,new InfoWindowRefresher(marker));
        }


       Log.e("StoreInfo","Store info object "+sourceDetails.getName() + "  "+sourceDetails.getLocAddress()+" "+sourceDetails.getRating());
        textName.setText(sourceDetails.getName());
        textAddress.setText(sourceDetails.getLocAddress());


        LatLng latLng1= new LatLng(Double.parseDouble(sharedPref.getString("latitude")),Double.parseDouble(sharedPref.getString("longitude")));
        LatLng latLng2= new LatLng(sourceDetails.getLatitute(),sourceDetails.getLongitute());
        Double radiusValue= CalculationByDistance(latLng1,latLng2);
       textRadius.setText(radiusValue.toString().substring(0,4)+" km");
        textRating.setText(sourceDetails.getRating().toString());
        ratingBar.setRating(sourceDetails.getRating());
      //  getMapViewComponent.viewClicked(textRequest,sourceDetails.getLatitute().doubleValue(),sourceDetails.getLongitute().doubleValue());
        textRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Infowindow","Text Request Click ");
                Intent intent =new Intent(activity, NewRequestStore.class);
                intent.putExtra("latitute",sourceDetails.getLatitute());
                intent.putExtra("longitute",sourceDetails.getLongitute());
                intent.putExtra("title",sourceDetails.getName());
                activity.startActivity(intent);
            }
        });

        mapWrapperLayout.setMarkerWithInfoWindow(marker, view);


        this.infoButtonListener = new OnInfoWindowElemTouchListener(textRequest,activity.getResources().getDrawable(R.drawable.arrow_down),activity.getResources().getDrawable(R.drawable.arrow_down))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(activity, marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();
            }
        };



        return view;
    }
    public void setClickListener(InfoWIndowClickListener infoWIndowClickListener){
        this.infoWIndowClickListener=infoWIndowClickListener;
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError(Exception e) {

        }


    }


    @Override
    public View getInfoWindow(Marker marker) {
        //inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // View v=inflater.inflate(R.layout.view_store_info_window,null);
        return null;
    }

}