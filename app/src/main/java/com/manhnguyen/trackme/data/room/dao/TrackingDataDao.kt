package com.manhnguyen.trackme.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.manhnguyen.trackme.domain.model.TrackingDataModel

@Dao
interface TrackingDataDao {

    @Query("SELECT * FROM TrackingDataModel WHERE RouteId =:routeId ")
    fun getTrackingData(routeId: Int): List<TrackingDataModel>

    @Insert
    fun insert(model: TrackingDataModel)

}