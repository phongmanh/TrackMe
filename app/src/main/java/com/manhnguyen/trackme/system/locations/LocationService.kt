package com.manhnguyen.trackme.system.locations

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.manhnguyen.trackme.common.Constants
import dagger.android.AndroidInjection
import javax.inject.Inject
import kotlin.properties.Delegates


class LocationService : Service(), LocationListener {


    private var lastLocation: Location? = null

    override fun onLocationChanged(location: Location?) {
        /*MLocationManager.LocationChange(location)*/

        location?.let { newLoc ->

            if (lastLocation == null) { // first time
                locationViewModel.getLocationData().postValue(LocationChangeEvent(newLoc))
                lastLocation = newLoc
                return
            }
            lastLocation?.let { last ->
                val time: Long = newLoc.time - last.time
                val distance: Double = last.distanceTo(
                    newLoc
                ).toDouble()
                val speed: Float = String.format("%.3f", distance / time).toFloat()
                if (distance > 2f) {
                    locationViewModel.getLocationData()
                        .postValue(LocationChangeEvent(newLoc))
                    lastLocation = newLoc
                }
                val message1 =
                    "Location: ${newLoc.latitude}, ${newLoc.longitude} distance1: $distance  speedCalc: $speed locationSpeed: ${newLoc.speed} "
                Log.e(
                    "onLocationChanged1",
                    message1
                )
            }


        }

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    @Inject
    lateinit var locationManager: LocationManager
    private var isGPSEnabled: Boolean = false
    private var isNetworkEnabled: Boolean = false
    private val LOCATION_INTERVAL = 0L
    private val LOCATION_DISTANCE = 0f

    @Inject
    lateinit var locationViewModel: LocationViewModel


    private val NOTIFICATION_ID = 12345678
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var notification: Notification


    companion object {
        var instance: LocationService? = null
        var primaryStartId by Delegates.notNull<Int>()
    }


    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        startForeground()
        initService()
        instance = this
        /*var result: ComponentName?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = startForegroundService(Intent(this, NotificationHidingService::class.java))
        } else {
            result = startService(Intent(this, NotificationHidingService::class.java))
        }
        if (result == null) {

        }*/
    }

    private fun createFineCriteria(): Criteria {
        val c = Criteria()
        c.accuracy = Criteria.ACCURACY_FINE
        c.isAltitudeRequired = false
        c.isBearingRequired = false
        c.isSpeedRequired = false
        c.isCostAllowed = true
        c.powerRequirement = Criteria.POWER_HIGH
        return c
    }

    private fun startForeground() {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                mNotificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                val CHANNEL_ID = "my_channel_01"
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                mNotificationManager.createNotificationChannel(channel)
                notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build()
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun initService() {

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (!isGPSEnabled && !isNetworkEnabled) return
        val high = locationManager.getProvider(
            locationManager.getBestProvider(
                createFineCriteria(),
                true
            )!!
        )

        locationManager.requestLocationUpdates(high.name, 0, 0f, this)
/*        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_INTERVAL,
                LOCATION_DISTANCE,
                this
            )
        }

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, this
            )
        }*/
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        instance = null
        locationManager.removeUpdates(this)
        stopForeground(true)
        stopSelf()
        super.onDestroy()
    }
}