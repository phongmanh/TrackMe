package com.manhnguyen.trackme.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity
data class AvgTrackingDataModel(
    @PrimaryKey
    @ColumnInfo(name = "RouteId")
    val routeId: Long,
    @ColumnInfo(name = "Duration")
    val duration: Int,
    @ColumnInfo(name = "Distance")
    val distance: Float,
    @ColumnInfo(name = "AvgSpeed")
    val agvSpeed: Float,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "Points")
    val points: String
)