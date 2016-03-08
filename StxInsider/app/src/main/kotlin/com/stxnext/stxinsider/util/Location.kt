package com.stxnext.stxinsider.util

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log

/**
 * Created by Łukasz Ciupa on 08.03.2016.
 */

class Location(context: Context) {

    val locationManager : LocationManager;
    var listener : OnLocationListener? = null;
    val locationListener = object: LocationListener  {
        override fun onLocationChanged(p0: Location?) {
            Log.d(TAG, "On Location changed: " + p0)
            listener?.onOfficeLocationDetected()
        }
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            throw UnsupportedOperationException()
        }
        override fun onProviderEnabled(p0: String?) {
            throw UnsupportedOperationException()
        }
        override fun onProviderDisabled(p0: String?) {
            throw UnsupportedOperationException()
        }
    };
    companion object {
        private val TAG = Location::class.java.simpleName
    }

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
    }

    public fun startLookingForLocation(listener : OnLocationListener) {
        this.listener = listener
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60 * 1000, 0f, locationListener)
    }

    public fun stopLookingForLocation() {
        locationManager.removeUpdates(locationListener)
    }

    public interface OnLocationListener {
        fun onOfficeLocationDetected()
    }

}