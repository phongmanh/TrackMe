package com.manhnguyen.trackme.system.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationViewModel @Inject constructor() : ViewModel() {

    private var _currentLocationData = MutableLiveData<LocationChangeEvent>()
    private var _userMoving = AtomicBoolean(false)

    fun getCurrentLocation(): LiveData<LocationChangeEvent> {
        return _currentLocationData
    }

    fun setUserMoving(moving: Boolean) {
        _userMoving.set(moving)
    }

    fun getUserMoving(): Boolean {
        return true //_userMoving.get()
    }

    fun getLocationData() = _currentLocationData

}