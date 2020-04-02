package com.deepak.besaat.view.fragments.storeMapFragment

import com.google.android.gms.maps.model.Marker
import android.view.MotionEvent
import androidx.core.os.HandlerCompat.postDelayed
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.view.View.OnTouchListener


abstract class OnInfoWindowElemTouchListener(
    private val view: View,
    private val bgDrawableNormal: Drawable,
    private val bgDrawablePressed: Drawable
) : OnTouchListener {
    private val handler = Handler()

    private var marker: Marker? = null
    private var pressed = false

    private val confirmClickRunnable = Runnable {
        if (endPress()) {
            onClickConfirmed(view, marker)
        }
    }

    fun setMarker(marker: Marker) {
        this.marker = marker
    }

   override fun onTouch(vv: View, event: MotionEvent): Boolean {
        if (0 <= event.x && event.x <= view.getWidth() &&
            0 <= event.y && event.y <= view.getHeight()
        ) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> startPress()

                // We need to delay releasing of the view a little so it shows the pressed state on the screen
                MotionEvent.ACTION_UP -> handler.postDelayed(confirmClickRunnable, 150)

                MotionEvent.ACTION_CANCEL -> endPress()
                else -> {
                }
            }
        } else {
            // If the touch goes outside of the view's area
            // (like when moving finger out of the pressed button)
            // just release the press
            endPress()
        }
        return false
    }

    private fun startPress() {
        if (!pressed) {
            pressed = true
            handler.removeCallbacks(confirmClickRunnable)
            view.setBackground(bgDrawablePressed)
            if (marker != null)
                marker!!.showInfoWindow()
        }
    }

    private fun endPress(): Boolean {
        if (pressed) {
            this.pressed = false
            handler.removeCallbacks(confirmClickRunnable)
            view.setBackground(bgDrawableNormal)
            if (marker != null)
                marker!!.showInfoWindow()
            return true
        } else
            return false
    }

    /**
     * This is called after a successful click
     */
    protected abstract fun onClickConfirmed(v: View, marker: Marker?)
}
