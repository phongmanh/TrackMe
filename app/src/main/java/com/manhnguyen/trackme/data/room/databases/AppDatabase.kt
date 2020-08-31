package com.manhnguyen.trackme.data.room.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.manhnguyen.trackme.data.room.dao.TrackingDataDao
import com.manhnguyen.trackme.data.room.dao.TrackingHistoryDao
import com.manhnguyen.trackme.domain.model.AvgTrackingDataModel
import com.manhnguyen.trackme.domain.model.TrackingDataModel

@Database(
    entities = [TrackingDataModel::class, AvgTrackingDataModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackingHistoryDao(): TrackingHistoryDao
    abstract fun trackingData(): TrackingDataDao
}