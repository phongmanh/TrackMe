package com.manhnguyen.trackme.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackingDataModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    val id: Int = 0,
    @ColumnInfo(name = "RouteId")
    val routeId: Long,
    @ColumnInfo(name = "Duration")
    val duration: Int,
    @ColumnInfo(name = "Distance")
    val distance: Float,
    @ColumnInfo(name = "Speed")
    val speed: Float,
    @ColumnInfo(name = "Lat")
    val lat: Double,
    @ColumnInfo(name = "Lng")
    val lng: Double
)