package com.example.bytebuilders.view.activities

import android.location.Geocoder
import com.google.api.Context
import java.util.Locale

data class LocationData(val latitude: Double, val longitude: Double) {
    override fun toString(): String {
        return "Latitud: $latitude, Longitud: $longitude"
    }
}