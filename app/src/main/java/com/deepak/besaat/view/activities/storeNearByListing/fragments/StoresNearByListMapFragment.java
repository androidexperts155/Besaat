package com.deepak.besaat.view.activities.storeNearByListing.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.deepak.besaat.R;
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails;
import com.deepak.besaat.model.Spot;
import com.deepak.besaat.utils.SharedPref;
import com.deepak.besaat.view.activities.newRequestStore.NewRequestStore;
import com.deepak.besaat.view.activities.storeDetail.StoreDetailActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class StoresNearByListMapFragment extends
        com.google.android.gms.maps.SupportMapFragment
        implements
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        OnClickListener, OnMapReadyCallback, ClusterManager.OnClusterClickListener<SourceDetails>, ClusterManager.OnClusterInfoWindowClickListener<SourceDetails>, ClusterManager.OnClusterItemClickListener<SourceDetails>, ClusterManager.OnClusterItemInfoWindowClickListener<SourceDetails> {

    private static Spot[] SPOTS_ARRAY = new Spot[]{
            new Spot("Киев", new LatLng(50.4546600, 30.5238000)),
            new Spot("Одесса", new LatLng(46.4774700, 30.7326200)),
            new Spot("Харьков", new LatLng(50.0000000, 36.2500000)),
            new Spot("Львов", new LatLng(49.8382600, 24.0232400)),
            new Spot("Донецк", new LatLng(48.0000000, 37.8000000)),
    };

    //интервал обновления положения всплывающего окна.
    //для плавности необходимо 60 fps, то есть 1000 ms / 60 = 16 ms между обновлениями.
    private static final int POPUP_POSITION_REFRESH_INTERVAL = 16;
    //длительность анимации перемещения карты
    private static final int ANIMATION_DURATION = 500;
    private boolean not_first_time_showing_info_window = false;

    private Map<Marker, SourceDetails> spots;
    private static ArrayList<SourceDetails> sourceDetailsArrayList = new ArrayList<>();
    private static ClusterManager<SourceDetails> mClusterManager;

    //точка на карте, соответственно перемещению которой перемещается всплывающее окно
    private LatLng trackedPosition;

    //Handler, запускающий обновление окна с заданным интервалом
    private Handler handler;

    //Runnable, который обновляет положение окна
    private Runnable positionUpdaterRunnable;

    //смещения всплывающего окна, позволяющее
    //скорректировать его положение относительно маркера
    private int popupXOffset;
    private int popupYOffset;
    public GoogleMap Map;
    private static SharedPref sharedPreff;
    private static String catName = "";
    private static String markerIcon = "";
    private static Bitmap markerIconBitmap;

    private static String curLat = "0.0";
    private static String curLng = "0.0";

    //высота маркера
    private int markerHeight = 70;
    private AbsoluteLayout.LayoutParams overlayLayoutParams;

    //слушатель, который будет обновлять смещения
    private ViewTreeObserver.OnGlobalLayoutListener infoWindowLayoutListener;

    //контейнер всплывающего окна
    private View infoWindowContainer;
    private TextView textView;
    private TextView button;

    CircleImageView circleImageView;
    TextView textName;
    TextView textAddress;
    TextView textRadius;
    TextView textRating;
    TextView textRequest;
    RatingBar ratingBar;
    private ImageView imageCross;
    private CardView cardView;
    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spots = new HashMap<>();
//        markerHeight = getResources().getDrawable(R.drawable.pin).getIntrinsicHeight();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment, null);
        getMap();
        FrameLayout containerMap = (FrameLayout) rootView.findViewById(R.id.container_map);
        View mapView = super.onCreateView(inflater, container, savedInstanceState);
        containerMap.addView(mapView, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        infoWindowContainer = rootView.findViewById(R.id.container_popup);
        //подписываемся на изменения размеров всплывающего окна
        infoWindowLayoutListener = new InfoWindowLayoutListener();
        infoWindowContainer.getViewTreeObserver().addOnGlobalLayoutListener(infoWindowLayoutListener);
        overlayLayoutParams = (AbsoluteLayout.LayoutParams) infoWindowContainer.getLayoutParams();

        circleImageView = infoWindowContainer.findViewById(R.id.circleImageView);
        cardView = infoWindowContainer.findViewById(R.id.cardView);
        textName = infoWindowContainer.findViewById(R.id.textView19);
        textAddress = infoWindowContainer.findViewById(R.id.textView20);
        textRadius = infoWindowContainer.findViewById(R.id.textView23);
        textRating = infoWindowContainer.findViewById(R.id.textRating);
        textRequest = infoWindowContainer.findViewById(R.id.textViewRequest);
        ratingBar = infoWindowContainer.findViewById(R.id.ratingB);
        imageCross = infoWindowContainer.findViewById(R.id.imageCross);
        infoWindowContainer.setVisibility(INVISIBLE);
// String profilePicUrl = "https://maps.googleapis.com/maps/api/place/photo?photoreference=" +sourceDetails.getPhotoRefrence() + "&sensor=false&maxheight=" + sourceDetails.getHeightt() + "&maxwidth=" + sourceDetails.getWidthh()+ "&key=" + activity.getResources().getString(R.string.google_api);
        // button.setOnClickListener(this);

        return rootView;
    }

    public void setDetails(ArrayList<SourceDetails> details, SharedPref sharedPref, String catName1, String markerIcon1, String curLat1, String curLng1) {
        sourceDetailsArrayList = details;
        sharedPreff = sharedPref;
        catName = catName1;
        markerIcon = markerIcon1;
        if (curLat1 != null && !curLat1.equals("")) {
            curLat = curLat1;
            curLng = curLng1;
        }
        getMap();
    }


    private void getMap() {
        this.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Map = googleMap;
                final GoogleMap map = Map;

                /// for cluster ///////////
                mClusterManager = new ClusterManager<>(getActivity(), Map);
                Map.setOnCameraIdleListener(mClusterManager);
                //////////////////////////
                map.getUiSettings().setRotateGesturesEnabled(false);
                map.setOnMapClickListener(StoresNearByListMapFragment.this);
                map.setOnMarkerClickListener(StoresNearByListMapFragment.this);
                final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin);
                final MarkerOptions markerOptions = new MarkerOptions();

                final MarkerOptions markerOptions1 = new MarkerOptions();
                // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_green));
                //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker()));
                Bitmap b = ((BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.icon_transparent_marker)).getBitmap();
                final Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 70, markerHeight, false);
                spots.clear();
//                for (final SourceDetails details : sourceDetailsArrayList) {
//                    Log.e("MarkerDetails", "Marker Details " + details);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            markerOptions1.position(new LatLng(details.getLatitute(), details.getLongitute()));
////                            markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(bitmapResized));
//                            if (details.getBitmapIcon() != null)
//                                markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(details.getBitmapIcon()));
//                            else
//                                markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(bitmapResized));
//                            Marker marker22 = map.addMarker(markerOptions1);
//                            Log.e("MarkerDetails", "Marker icons  " + marker22);
//                            //Marker marker1 = map.addMarker(markerOptions.position(new LatLng(details.getLatitute(), details.getLongitute()))
//                            // .icon(icon));
//                            spots.put(marker22, details);
//                        }
//                    }, 100);
//                }

                if (!sourceDetailsArrayList.isEmpty()) {
//                    if (markerIconBitmap == null) {
                    new GetMapMarkerBitmap().execute();
//                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sourceDetailsArrayList.get(sourceDetailsArrayList.size() - 1).getLatitute(), sourceDetailsArrayList.get(sourceDetailsArrayList.size() - 1).getLongitute()), 12.0f));
                } else {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(curLat), Double.parseDouble(curLng)), 12f));
                }
                map.clear();
            }
        });
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            Bitmap resizedBitmap = Bitmap.createScaledBitmap(myBitmap, 300, 300, false);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //очистка
        handler = new Handler(Looper.getMainLooper());
        positionUpdaterRunnable = new PositionUpdaterRunnable();
        handler.post(positionUpdaterRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        infoWindowContainer.getViewTreeObserver().removeGlobalOnLayoutListener(infoWindowLayoutListener);
        handler.removeCallbacks(positionUpdaterRunnable);
        handler = null;
    }

    @Override
    public void onClick(View v) {
        String name = (String) v.getTag();
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.google.com/search?q=" + name)));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        infoWindowContainer.setVisibility(INVISIBLE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        GoogleMap map = Map;
        Projection projection = map.getProjection();
        trackedPosition = marker.getPosition();
        Point trackedPoint = projection.toScreenLocation(trackedPosition);
        trackedPoint.y -= popupYOffset / 2;
        LatLng newCameraLocation = projection.fromScreenLocation(trackedPoint);
        map.animateCamera(CameraUpdateFactory.newLatLng(newCameraLocation), ANIMATION_DURATION, null);

        final SourceDetails sourceDetails = spots.get(marker);
        final String profilePicUrl = "https://maps.googleapis.com/maps/api/place/photo?photoreference=" + sourceDetails.getPhotoRefrence() + "&sensor=false&maxheight=" + sourceDetails.getHeightt() + "&maxwidth=" + sourceDetails.getWidthh() + "&key=" + getActivity().getResources().getString(R.string.google_api);

        if (not_first_time_showing_info_window) {
            Picasso.get()
                    .load(profilePicUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(circleImageView);
        } else { // if it's the first time, load the image with the callback set
            not_first_time_showing_info_window = true;
            Picasso.get()
                    .load(profilePicUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(circleImageView);
        }

        textName.setText(sourceDetails.getName());
        textAddress.setText(sourceDetails.getLocAddress());
        LatLng latLng1 = new LatLng(Double.parseDouble(curLat), Double.parseDouble(curLng));
        LatLng latLng2 = new LatLng(sourceDetails.getLatitute(), sourceDetails.getLongitute());
        Double radiusValue = CalculationByDistance(latLng1, latLng2);
        textRadius.setText(radiusValue.toString().substring(0, 4) + " km");
        if (sourceDetails.getRating() != null) {
            textRating.setText(sourceDetails.getRating().toString());
            ratingBar.setRating(sourceDetails.getRating());
        }

        //  getMapViewComponent.viewClicked(textRequest,sourceDetails.getLatitute().doubleValue(),sourceDetails.getLongitute().doubleValue());
        textRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Infowindow", "Text Request Click ");
                Intent intent = new Intent(getActivity(), NewRequestStore.class);
                intent.putExtra("latitute", sourceDetails.getLatitute());
                intent.putExtra("longitute", sourceDetails.getLongitute());
                intent.putExtra("title", sourceDetails.getName());
                intent.putExtra("from", "MapFrag");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().startActivity(intent);
            }
        });
        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
                intent.putExtra("latitute", sourceDetails.getLatitute());
                intent.putExtra("longitute", sourceDetails.getLongitute());
                intent.putExtra("title", sourceDetails.getName());
                intent.putExtra("location", sourceDetails.getLocAddress());
                intent.putExtra("imageurl", profilePicUrl);
                intent.putExtra("rating", sourceDetails.getRating());
                intent.putExtra("placeid", sourceDetails.getPlaceId());
                intent.putExtra("no_rating_users", sourceDetails.getUserRatingsTotal());
                intent.putExtra("from", "MapFrag");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().startActivity(intent);

            }
        });
        imageCross.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                infoWindowContainer.setVisibility(INVISIBLE);
            }
        });

        infoWindowContainer.setVisibility(VISIBLE);

        return true;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public boolean onClusterClick(Cluster<SourceDetails> cluster) {
        Map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                cluster.getPosition(), (float) Math.floor(Map
                        .getCameraPosition().zoom + 2)), 300,
                null);
        return true;
//        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<SourceDetails> cluster) {

    }

    @Override
    public boolean onClusterItemClick(final SourceDetails sourceDetails) {

        GoogleMap map = Map;
        Projection projection = map.getProjection();
//        trackedPosition = marker.getPosition();
        trackedPosition = sourceDetails.getPosition();
        Point trackedPoint = projection.toScreenLocation(trackedPosition);
        trackedPoint.y -= popupYOffset / 2;
        LatLng newCameraLocation = projection.fromScreenLocation(trackedPoint);
        map.animateCamera(CameraUpdateFactory.newLatLng(newCameraLocation), ANIMATION_DURATION, null);

//        final SourceDetails sourceDetails = spots.get(marker);
        final String profilePicUrl = "https://maps.googleapis.com/maps/api/place/photo?photoreference=" + sourceDetails.getPhotoRefrence() + "&sensor=false&maxheight=" + sourceDetails.getHeightt() + "&maxwidth=" + sourceDetails.getWidthh() + "&key=" + getActivity().getResources().getString(R.string.google_api);

        if (not_first_time_showing_info_window) {
            Picasso.get()
                    .load(profilePicUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(circleImageView);
        } else { // if it's the first time, load the image with the callback set
            not_first_time_showing_info_window = true;
            Picasso.get()
                    .load(profilePicUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(circleImageView);
        }

        textName.setText(sourceDetails.getName());
        textAddress.setText(sourceDetails.getLocAddress());
        LatLng latLng1 = new LatLng(Double.parseDouble(curLat), Double.parseDouble(curLng));
        LatLng latLng2 = new LatLng(sourceDetails.getLatitute(), sourceDetails.getLongitute());
        Double radiusValue = CalculationByDistance(latLng1, latLng2);
        textRadius.setText(radiusValue.toString().substring(0, 4) + " km");
        if (sourceDetails.getRating() != null) {
            textRating.setText(sourceDetails.getRating().toString());
            ratingBar.setRating(sourceDetails.getRating());
        }

        //  getMapViewComponent.viewClicked(textRequest,sourceDetails.getLatitute().doubleValue(),sourceDetails.getLongitute().doubleValue());
        textRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Infowindow", "Text Request Click ");
                Intent intent = new Intent(getActivity(), NewRequestStore.class);
                intent.putExtra("latitute", sourceDetails.getLatitute());
                intent.putExtra("longitute", sourceDetails.getLongitute());
                intent.putExtra("title", sourceDetails.getName());
                intent.putExtra("from", "MapFrag");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().startActivity(intent);
            }
        });
        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
                intent.putExtra("latitute", sourceDetails.getLatitute());
                intent.putExtra("longitute", sourceDetails.getLongitute());
                intent.putExtra("title", sourceDetails.getName());
                intent.putExtra("location", sourceDetails.getLocAddress());
                intent.putExtra("imageurl", profilePicUrl);
                intent.putExtra("rating", sourceDetails.getRating());
                intent.putExtra("placeid", sourceDetails.getPlaceId());
                intent.putExtra("no_rating_users", sourceDetails.getUserRatingsTotal());
                intent.putExtra("from", "MapFrag");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().startActivity(intent);

            }
        });
        imageCross.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                infoWindowContainer.setVisibility(INVISIBLE);
            }
        });

        infoWindowContainer.setVisibility(VISIBLE);
        PersonRenderer myClusterRenderer = (PersonRenderer) mClusterManager.getRenderer();
        myClusterRenderer.getMarker(sourceDetails).hideInfoWindow();

        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(SourceDetails sourceDetails) {
        Toast.makeText(getActivity(), "ClusterInfoWindow", Toast.LENGTH_SHORT).show();
    }


    private class InfoWindowLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            //размеры окна изменились, обновляем смещения
            popupXOffset = infoWindowContainer.getWidth() / 2;
            popupYOffset = infoWindowContainer.getHeight();
        }
    }

    private class PositionUpdaterRunnable implements Runnable {
        private int lastXPosition = Integer.MIN_VALUE;
        private int lastYPosition = Integer.MIN_VALUE;

        @Override
        public void run() {
            //помещаем в очередь следующий цикл обновления
            handler.postDelayed(this, POPUP_POSITION_REFRESH_INTERVAL);

            //если всплывающее окно скрыто, ничего не делаем
            if (trackedPosition != null && infoWindowContainer.getVisibility() == VISIBLE) {
                Point targetPosition = Map.getProjection().toScreenLocation(trackedPosition);

                //если положение окна не изменилось, ничего не делаем
                if (lastXPosition != targetPosition.x || lastYPosition != targetPosition.y) {
                    //обновляем положение
                    overlayLayoutParams.x = targetPosition.x - popupXOffset;
                    overlayLayoutParams.y = targetPosition.y - popupYOffset - markerHeight;
                    infoWindowContainer.setLayoutParams(overlayLayoutParams);

                    //запоминаем текущие координаты
                    lastXPosition = targetPosition.x;
                    lastYPosition = targetPosition.y;
                }
            }
        }
    }

    class GetMapMarkerBitmap extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
//            for (int i = 0; i < sourceDetailsArrayList.size(); i++) {
            try {
                if (catName.toLowerCase().equals("all") || catName.equals("")) {
                    Bitmap b = ((BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.pin)).getBitmap();
                    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 70, 70, false);
//                        sourceDetailsArrayList.get(i).setBitmapIcon(bitmapResized);
                    markerIconBitmap = bitmapResized;
                } else {
                    Bitmap b = getBitmapFromURL(markerIcon);
                    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 70, 70, false);
//                        sourceDetailsArrayList.get(i).setBitmapIcon(bitmapResized);
                    markerIconBitmap = bitmapResized;
                }
            } catch (Exception e) {
                Bitmap b = ((BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.pin)).getBitmap();
                Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 70, 70, false);
//                    sourceDetailsArrayList.get(i).setBitmapIcon(bitmapResized);
                markerIconBitmap = bitmapResized;
            }
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            getMap();
            if (getActivity() != null)
                mClusterManager.setRenderer(new PersonRenderer());
            mClusterManager.addItems(sourceDetailsArrayList);
            mClusterManager.setOnClusterItemClickListener(StoresNearByListMapFragment.this);
            mClusterManager.setOnClusterClickListener(StoresNearByListMapFragment.this);
        }
    }

    private class PersonRenderer extends DefaultClusterRenderer<SourceDetails> {
        private final IconGenerator mIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;
        Bitmap icon;

        public PersonRenderer() {
            super(getActivity(), Map, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.cluster_multi_profile, null, false);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getActivity().getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(SourceDetails person, MarkerOptions markerOptions) {
//            icon = mIconGenerator.makeIcon();
            if (markerIconBitmap != null)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerIconBitmap));
            super.onBeforeClusterItemRendered(person, markerOptions);
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.getName());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<SourceDetails> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }

        @Override
        protected void onClusterRendered(final Cluster<SourceDetails> cluster, final Marker marker) {
            final List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
            final int width = mDimension;
            final int height = mDimension;
            Bitmap dummyBitmap = null;
            Drawable drawable;
            final int clusterSize = cluster.getSize();
            final int[] count = {0};

            for (SourceDetails p : cluster.getItems()) {
                // Draw 4 at most.
                if (cluster.getSize() <= 5) {
//                    mClusterImageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_cluster_1));
                    mClusterIconGenerator.setBackground(getResources().getDrawable(R.drawable.icn_cluster_1));
                    Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                } else if (cluster.getSize() > 5 && cluster.getSize() <= 10) {
//                    mClusterImageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_cluster_2));
                    mClusterIconGenerator.setBackground(getResources().getDrawable(R.drawable.icn_cluster_2));
                    Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                } else if (cluster.getSize() > 10 && cluster.getSize() <= 15) {
//                    mClusterImageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_cluster_3));
                    mClusterIconGenerator.setBackground(getResources().getDrawable(R.drawable.icn_cluster_3));
                    Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                } else if (cluster.getSize() > 15 && cluster.getSize() < 20) {
//                    mClusterImageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_cluster_4));
                    mClusterIconGenerator.setBackground(getResources().getDrawable(R.drawable.icn_cluster_4));
                    Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                }
//                else if(cluster.getSize()>20)
                else {
//                    mClusterImageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_cluster_5));
                    mClusterIconGenerator.setBackground(getResources().getDrawable(R.drawable.icn_cluster_5));
                    Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                }
            }
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
            super.onClusterRendered(cluster, marker);
        }

        @Override
        protected void onClusterItemRendered(SourceDetails clusterItem, final Marker marker) {
            if (markerIconBitmap != null)
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(markerIconBitmap));
            super.onClusterItemRendered(clusterItem, marker);
        }
    }
}
