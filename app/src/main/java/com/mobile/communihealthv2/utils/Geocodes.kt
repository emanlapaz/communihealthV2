package com.mobile.communihealthv2.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

fun getAddressFromEircode(eircode: String, context: Context): Address? {
    val geocoder = Geocoder(context)
    try {
        val addresses = geocoder.getFromLocationName(eircode, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            return addresses[0]
        }
    } catch (e: IOException) {
        // Handle the exception, e.g., log the error
    }
    return null
}

fun getLatLngFromEircode(eircode: String, context: Context): LatLng? {
    val geocoder = Geocoder(context)
    try {
        val addresses = geocoder.getFromLocationName(eircode, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val location = addresses[0]
                return LatLng(location.latitude, location.longitude)
            }
        }
    } catch (e: IOException) {
        // Handle the exception
    }
    return null
}