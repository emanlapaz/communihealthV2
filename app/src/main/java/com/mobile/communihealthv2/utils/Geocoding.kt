package com.mobile.communihealthv2.utils

import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

object GeocodingUtils {

    fun geocodeEircode(eircode: String, apiKey: String, callback: (Double, Double) -> Unit) {
        // Background thread implementation (e.g., AsyncTask, coroutines)
        // This is crucial to avoid NetworkOnMainThreadException
        Thread {
            try {
                val encodedEircode = URLEncoder.encode(eircode, "UTF-8")
                val urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=$encodedEircode&key=$apiKey"

                val jsonResponse = URL(urlString).readText()
                val jsonObject = JSONObject(jsonResponse)

                val location = jsonObject.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location")

                val latitude = location.getDouble("lat")
                val longitude = location.getDouble("lng")

                callback(latitude, longitude) // Callback with the coordinates
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exceptions and errors
            }
        }.start()
    }
}