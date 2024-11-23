package com.example.bytebuilders.view.activities

data class LocationData(val latitude: Double, val longitude: Double) {
    override fun toString(): String {
        return "Latitud: $latitude, Longitud: $longitude"
    }
}