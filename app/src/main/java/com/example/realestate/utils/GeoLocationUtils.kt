package com.example.realestate.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import java.io.IOException
import java.util.*

class GeoLocationUtils(private val context: Context) {

    companion object {
        private const val TAG = "GeoLocationUtils"
    }

    fun getAddress(location: Location): Address? {
        val geocoder = Geocoder(context, Locale.US)
        var address: Address? = null
        try {
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                address = addressList[0]
            }

        } catch (e: IOException) {
            // Handle the exception
            e.printStackTrace()
            Log.e(TAG, "e: ${e.message}")
        }

        return address
    }

    fun getCityAndCountry(location: Location): Pair<String?, String?> {
        val geocoder = Geocoder(context, Locale.US)
        var city: String? = null
        var country: String? = null
        var area: String? = null

        try {
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                city = addressList[0].locality
                country = addressList[0].countryName
                Log.d(TAG, "thoroughfare: ${addressList[0].thoroughfare}")
                Log.d(TAG, "subLocality: ${addressList[0].subLocality}")
                Log.d(TAG, "locale: ${addressList[0].locale}")
            }

        } catch (e: IOException) {
            // Handle the exception
            e.printStackTrace()
            Log.e(TAG, "e: ${e.message}")
        }

        val pair = Pair(city, country)

        Log.d(TAG, "pair: $pair")
        return pair
    }
}