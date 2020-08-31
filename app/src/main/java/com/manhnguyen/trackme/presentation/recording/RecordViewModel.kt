package com.manhnguyen.trackme.presentation.recording

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import butterknife.internal.Utils
import com.google.android.gms.common.util.JsonUtils
import com.google.android.gms.maps.model.LatLng
import com.manhnguyen.trackme.common.SchedulerProvider
import com.manhnguyen.trackme.data.room.databases.AppDatabase
import com.manhnguyen.trackme.domain.model.AvgTrackingDataModel
import com.manhnguyen.trackme.domain.model.TrackingDataModel
import com.manhnguyen.trackme.domain.model.UserRecordInfo
import com.manhnguyen.trackme.util.JsonUtil
import com.manhnguyen.trackme.util.durationFormat
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.collections.ArrayList

class RecordViewModel @Inject constructor(
    private val db: AppDatabase,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {

    private val _compositeDisposable = CompositeDisposable()
    private var _routeId: Long = System.currentTimeMillis()
    private var _lastLocation: Location? = null
    private var _startLocation: Location? = null
    private val _pointList = AtomicReference<ArrayList<LatLng>>()
    private val _durationData = AtomicInteger(0)
    private val _durationUI = MutableLiveData("00:00:00")
    private val _running = AtomicBoolean(true)
    private val _userRecordInfo = MutableLiveData<UserRecordInfo>()
    private val _trackingData = AtomicReference<ArrayList<TrackingDataModel>>()


    init {
        _trackingData.set(ArrayList())
        _pointList.set(ArrayList())
    }


    fun setIsRunning(value: Boolean) = _running.set(value)
    fun getUserRecordInfo(): LiveData<UserRecordInfo> = _userRecordInfo
    fun getDurationData(): LiveData<String> = _durationUI

    fun saveUserLocation(location: Location) {
        try {
            createTrackingModel(location)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createTrackingModel(location: Location) {
        var _distance = 0f
        _lastLocation?.let { last ->
            _distance = location.distanceTo(last)
        }
        val model = TrackingDataModel(
            routeId = _routeId,
            duration = _durationData.get(),
            distance = _distance,
            speed = location.speed,
            lat = location.latitude,
            lng = location.longitude
        )
        db.trackingData().insert(model)
        _trackingData.get().add(model)
        _pointList.get().add(LatLng(location.latitude, location.longitude))
        _lastLocation = location
        updateUI(
            _trackingData.get().sumByDouble { it.distance.toDouble() }.toFloat(),
            location.speed
        )

    }


    private fun updateUI(distance: Float, speed: Float) {
        val dist = "${BigDecimal((distance / 1000).toDouble()).setScale(
            2,
            RoundingMode.HALF_EVEN
        ).toFloat()} km"
        val sp = "${BigDecimal(speed * 3.6).setScale(
            2,
            RoundingMode.HALF_EVEN
        ).toFloat()} km/h"

        _userRecordInfo.postValue(
            UserRecordInfo(
                dist,
                sp,
                _pointList.get()
            )
        )

    }

    fun finishRecord() {
        try {
            runBlocking {

                var avgSpeed = 0f
                var distance = 0f
                var points = ""
                _trackingData.get().let { arr ->
                    val sum = arr.sumByDouble { it.speed.toDouble() }
                    val count = arr.count()
                    avgSpeed = (sum / count).toFloat()
                    distance = arr.sumByDouble { it.distance.toDouble() }.toFloat()

                }
                points = JsonUtil.instance.gson.toJsonTree(_pointList.get()).asJsonArray.toString()
                val model = AvgTrackingDataModel(
                    routeId = _routeId,
                    duration = _durationData.get(),
                    distance = distance,
                    agvSpeed = avgSpeed,
                    timestamp = Date().time,
                    points = points
                )
                db.trackingHistoryDao().insert(model)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun extractDuration(duration: Int) {
        viewModelScope.launch {
            _durationUI.postValue(duration.durationFormat())
        }
    }

    fun startDurationChange() {
        try {
            _compositeDisposable.add(
                Flowable.interval(1, TimeUnit.SECONDS)
                    .filter { _running.get() }
                    .observeOn(schedulerProvider.ioScheduler)
                    .subscribeOn(schedulerProvider.runOnUiThread)
                    .subscribe {
                        _durationData.set(_durationData.get().plus(1))
                        extractDuration(_durationData.get())
                    }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        _compositeDisposable.dispose()
    }

}

