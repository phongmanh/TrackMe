package com.manhnguyen.trackme.data.repository

import com.manhnguyen.trackme.data.room.databases.AppDatabase
import javax.inject.Inject

class TrackingHistoryRepo @Inject constructor(private val db: AppDatabase) {
    suspend fun getTrackingHistory() = db.trackingHistoryDao().getTrackingHistory()
}