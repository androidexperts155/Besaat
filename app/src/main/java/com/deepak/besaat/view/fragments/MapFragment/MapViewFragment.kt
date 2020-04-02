package com.deepak.besaat.view.fragments.MapFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AbsoluteLayout
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.util.HashMap
import android.view.View.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.deepak.besaat.R
import com.deepak.besaat.model.Spot2

/*class MapFragment:com.google.android.gms.maps.MapFragment(), GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, OnClickListener {
    private var spots:Map<Marker, Spot2>
    //точка на карте, соответственно перемещению которой перемещается всплывающее окно
    private val trackedPosition:LatLng
    //Handler, запускающий обновление окна с заданным интервалом
    private val handler:Handler
    //Runnable, который обновляет положение окна
    private val positionUpdaterRunnable:Runnable
    //смещения всплывающего окна, позволяющее
    //скорректировать его положение относительно маркера
    private val popupXOffset:Int = 0
    private val popupYOffset:Int = 0
    //высота маркера
    private val markerHeight:Int = 0
    private val overlayLayoutParams:AbsoluteLayout.LayoutParams
    //слушатель, который будет обновлять смещения
    private val infoWindowLayoutListener:ViewTreeObserver.OnGlobalLayoutListener
    //контейнер всплывающего окна
    private val infoWindowContainer:View
    private val textView:TextView
    private val button:TextView
   override fun onCreate(savedInstanceState:Bundle) {
        super.onCreate(savedInstanceState)
        spots = HashMap<Marker, Spot2>()
        markerHeight = getResources().getDrawable(R.drawable.pin).getIntrinsicHeight()
    }
    override  fun onCreateView(inflater:LayoutInflater, container:ViewGroup, savedInstanceState:Bundle):View {
        val rootView = inflater.inflate(R.layout.fragment, null)
        val containerMap = rootView.findViewById(R.id.container_map) as FrameLayout
        val mapView = super.onCreateView(inflater, container, savedInstanceState)
        containerMap.addView(mapView, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        val map =activity. getMap()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(48.35, 31.16), 5.5f))
        map.getUiSettings().setRotateGesturesEnabled(false)
        map.setOnMapClickListener(this)
        map.setOnMarkerClickListener(this)
        map.clear()
        spots.clear()
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.pin)
        for (spot in SPOTS_ARRAY)
        {
            val marker = map.addMarker(MarkerOptions()
                .position(spot.getPosition())
                .title("Title")
                .snippet("Subtitle")
                .icon(icon))
            spots.put(marker, spot)
        }
        infoWindowContainer = rootView.findViewById(R.id.container_popup)
        //подписываемся на изменения размеров всплывающего окна
        infoWindowLayoutListener = InfoWindowLayoutListener()
        infoWindowContainer.getViewTreeObserver().addOnGlobalLayoutListener(infoWindowLayoutListener)
        overlayLayoutParams = infoWindowContainer.getLayoutParams() as AbsoluteLayout.LayoutParams
        textView = infoWindowContainer.findViewById(R.id.textview_title) as TextView
        button = infoWindowContainer.findViewById(R.id.button_view_article) as TextView
        button.setOnClickListener(this)
        return rootView
    }
    override  fun onViewCreated(view:View, savedInstanceState:Bundle) {
        super.onViewCreated(view, savedInstanceState)
        //очистка
        handler = Handler(Looper.getMainLooper())
        positionUpdaterRunnable = PositionUpdaterRunnable()
        handler.post(positionUpdaterRunnable)
    }
    override  fun onDestroyView() {
        super.onDestroyView()
        infoWindowContainer.getViewTreeObserver().removeGlobalOnLayoutListener(infoWindowLayoutListener)
        handler.removeCallbacks(positionUpdaterRunnable)
        handler = null
    }
    override  fun onClick(v:View) {
        val name = v.getTag() as String
        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.google.com/search?q=" + name)))
    }
    override    fun onMapClick(latLng:LatLng) {
        infoWindowContainer.setVisibility(INVISIBLE)
    }
    override    fun onMarkerClick(marker:Marker):Boolean {
        val map = getMap()
        val projection = map.getProjection()
        trackedPosition = marker.getPosition()
        val trackedPoint = projection.toScreenLocation(trackedPosition)
        (trackedPoint.y)-= (popupYOffset / 2).toDouble()
        val newCameraLocation = projection.fromScreenLocation(trackedPoint)
        map.animateCamera(CameraUpdateFactory.newLatLng(newCameraLocation), ANIMATION_DURATION, null)
        val spot = spots.get(marker)
        textView.setText(spot?.name)
        button.setTag(spot?.name)
        infoWindowContainer.setVisibility(VISIBLE)
        return true
    }
    private inner class InfoWindowLayoutListener:ViewTreeObserver.OnGlobalLayoutListener {
        fun onGlobalLayout() {
            //размеры окна изменились, обновляем смещения
            popupXOffset = infoWindowContainer.getWidth() / 2
            popupYOffset = infoWindowContainer.getHeight()
        }
    }
    private inner class PositionUpdaterRunnable:Runnable {
        private val lastXPosition = Integer.MIN_VALUE
        private val lastYPosition = Integer.MIN_VALUE
        public override fun run() {
            //помещаем в очередь следующий цикл обновления
            handler.postDelayed(this, POPUP_POSITION_REFRESH_INTERVAL)
            //если всплывающее окно скрыто, ничего не делаем
            if (trackedPosition != null && infoWindowContainer.getVisibility() === VISIBLE)
            {
                val targetPosition = getMap().getProjection().toScreenLocation(trackedPosition)
                //если положение окна не изменилось, ничего не делаем
                if (lastXPosition != targetPosition.x || lastYPosition != targetPosition.y)
                {
                    //обновляем положение
                    overlayLayoutParams.x = targetPosition.x - popupXOffset
                    overlayLayoutParams.y = targetPosition.y - popupYOffset - markerHeight - 30
                    infoWindowContainer.setLayoutParams(overlayLayoutParams)
                    //запоминаем текущие координаты
                    lastXPosition = targetPosition.x
                    lastYPosition = targetPosition.y
                }
            }
        }
    }
    companion object {
        private val SPOTS_ARRAY = arrayOf<Spot2>(Spot2("Киев", LatLng(50.4546600, 30.5238000)), Spot2("Одесса", LatLng(46.4774700, 30.7326200)), Spot2("Харьков", LatLng(50.0000000, 36.2500000)), Spot2("Львов", LatLng(49.8382600, 24.0232400)), Spot2("Донецк", LatLng(48.0000000, 37.8000000)))
        //интервал обновления положения всплывающего окна.
        //для плавности необходимо 60 fps, то есть 1000 ms / 60 = 16 ms между обновлениями.
        private val POPUP_POSITION_REFRESH_INTERVAL = 16
        //длительность анимации перемещения карты
        private val ANIMATION_DURATION = 500
        @JvmStatic
        fun   newInstance(item: SourceDetails):MapViewFragment{
            var fragment : MapViewFragment = MapViewFragment()
            //   fragment.sourceDetails=sourceDetails
            fragment.item=item

            return fragment

        }
    }
}



}*/
