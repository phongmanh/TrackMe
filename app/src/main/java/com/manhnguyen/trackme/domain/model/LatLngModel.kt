package com.manhnguyen.trackme.domain.model

import com.google.gson.annotations.SerializedName

data class LatLngModel(
    @SerializedName("Latitude") val lat: Double,
    @SerializedName("Longitude") val lng: Double
)