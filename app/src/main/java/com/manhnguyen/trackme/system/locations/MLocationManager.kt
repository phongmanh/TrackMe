package com.manhnguyen.trackme.system.locations

import android.location.Location
import org.greenrobot.eventbus.EventBus

class MLocationManager {

    companion object {

        var currentLocation: Location? = null
        fun LocationChange(location: Location?) {
            currentLocation = location
            EventBus.getDefault().post(LocationChangeEvent(location))
        }



    }


}