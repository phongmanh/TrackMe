package com.manhnguyen.trackme.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.manhnguyen.trackme.domain.model.AvgTrackingDataModel

@Dao
interface TrackingHistoryDao {

    @Query("SELECT * FROM AvgTrackingDataModel ORDER BY timestamp DESC")
    suspend fun getTrackingHistory(): List<AvgTrackingDataModel>

    @Insert
    suspend fun insert(model: AvgTrackingDataModel)

}