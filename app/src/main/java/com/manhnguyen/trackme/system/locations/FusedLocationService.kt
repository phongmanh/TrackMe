package com.manhnguyen.trackme.system.locations

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.manhnguyen.trackme.common.SchedulerProvider
import com.manhnguyen.trackme.util.KalmanLatLong
import com.google.android.gms.location.*
import dagger.android.AndroidInjection
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class FusedLocationService : Service() {

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var locationViewModel: LocationViewModel

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var activityRecognitionClient: ActivityRecognitionClient

    private val NOTIFICATION_ID = 12345678
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var notification: Notification
    private var lastLocation: Location? = null

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

    private var currentActivity = DetectedActivity.UNKNOWN

    /**
     * The service will call the handler to send back information.
     **/
    private val handler = Handler(Handler.Callback() { msg ->
        currentActivity = msg.arg1
        /*     val result =
                 if (currentActivity == DetectedActivity.STILL) "STILL"
                 else if (currentActivity == DetectedActivity.UNKNOWN) "UNKNOWN"
                 else if (currentActivity == DetectedActivity.TILTING) "TILTING"
                 else if (currentActivity == DetectedActivity.IN_VEHICLE) "IN_VEHICLE"
                 else "RUNING"*/
        /*Toast.makeText(this, "Activity: $result", Toast.LENGTH_SHORT).show()*/
        locationViewModel.setUserMoving(DetectedActivity.STILL != currentActivity)
        /*Log.e("Handler", "" + result)*/
        return@Callback true
    })

    private fun getActivityDetectionPendingIntent(): PendingIntent? {
        val intent = Intent(this, DetectedActivitiesIntentService::class.java)
        val messenger = Messenger(handler)
        intent.putExtra("MESSENGER", messenger)

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun requestActivityUpdate() {
        try {
            /*        activityRecognitionClient = ActivityRecognitionClient(this)
                    activityRecognitionClient.requestActivityUpdates(
                        5000,
                        getActivityDetectionPendingIntent()
                    )
                        .addOnSuccessListener {
                            Log.e("requestActivityUpdates", "addOnSuccessListener")
                        }
                        .addOnFailureListener {
                            Log.e("requestActivityUpdates", "addOnFailureListener")
                        }*/


        } catch (it: Exception) {
            it.printStackTrace()
        }
    }

    private fun requestActivityTransitionUpdates() {
        activityRecognitionClient = ActivityRecognitionClient(this)
        val request = buildTransitionRequest()
        /*   request?.let {
               val pendingIntent = getActivityDetectionPendingIntent()
               activityRecognitionClient.requestActivityTransitionUpdates(request, pendingIntent)
                   .addOnSuccessListener {
                       Log.e("Transition", "addOnSuccessListener")
                   }
                   .addOnFailureListener {
                       it.printStackTrace()
                       Log.e("Transition", "addOnFailureListener")
                   }
           }*/

    }


    private fun buildTransitionRequest(): ActivityTransitionRequest? {
        try {
            val transitions = ArrayList<ActivityTransition>()
            transitions.apply {
                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build()
                )
/*                add(
                    ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build()
                )*/
                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build()
                )
/*                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build()
                )*/

                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build()
                )
/*                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build()
                )*/

                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build()
                )
/*                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build()
                )*/

                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build()
                )
/*                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build()
                )*/

                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.ON_FOOT)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build()
                )
/*                add(
                    ActivityTransition.Builder().setActivityType(DetectedActivity.ON_FOOT)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build()
                )*/


            }

            return ActivityTransitionRequest(transitions)

        } catch (it: Exception) {
            it.printStackTrace()
        }
        return null
    }


    private fun meterDistanceBetweenPoints(
        lat_a: Float,
        lng_a: Float,
        lat_b: Float,
        lng_b: Float
    ): Double {
        val pk = (180f / Math.PI).toFloat()
        val a1 = lat_a / pk
        val a2 = lng_a / pk
        val b1 = lat_b / pk
        val b2 = lng_b / pk
        val t1 =
            Math.cos(a1.toDouble()) * Math.cos(a2.toDouble()) * Math.cos(
                b1.toDouble()
            ) * Math.cos(b2.toDouble())
        val t2 =
            Math.cos(a1.toDouble()) * Math.sin(a2.toDouble()) * Math.cos(
                b1.toDouble()
            ) * Math.sin(b2.toDouble())
        val t3 =
            Math.sin(a1.toDouble()) * Math.sin(b1.toDouble())
        val tt = Math.acos(t1 + t2 + t3)
        return 6366000 * tt
    }

    private fun getDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val R = 6371000.0 // for haversine use R = 6372.8 km instead of 6371 km
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) *
                sin(dLon / 2) * sin(dLon / 2)
        //double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 2 * R * atan2(sqrt(a), sqrt(1 - a))
        // simplify haversine:
        //return 2 * R * 1000 * Math.asin(Math.sqrt(a));
    }


    private var currentSpeed = 0.0f
    private val real: KalmanLatLong = KalmanLatLong(3f)
    private var isIgnored = false

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        startForeground()

        /*requestActivityUpdate()*/
/*        requestActivityTransitionUpdates()*/
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 2500
            smallestDisplacement = 0f
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.lastLocation?.let { newLoc ->
                    try {
                        if (lastLocation == null) { // first time
                            locationViewModel.getLocationData()
                                .postValue(LocationChangeEvent(newLoc))
                            lastLocation = newLoc
                            return
                        }
                        lastLocation?.let { last ->

                            if (currentSpeed == 0.0f)
                                real.Process(
                                    newLoc.latitude,
                                    newLoc.longitude,
                                    newLoc.accuracy,
                                    newLoc.time,
                                    3f
                                )
                            else
                                real.Process(
                                    newLoc.latitude,
                                    newLoc.longitude,
                                    newLoc.accuracy,
                                    newLoc.time,
                                    newLoc.speed
                                )


                            val filterLocation = Location("").apply {
                                latitude = Location.convert(real.get_lat(), Location.FORMAT_DEGREES)
                                    .toDouble()
                                longitude =
                                    Location.convert(real.get_lng(), Location.FORMAT_DEGREES)
                                        .toDouble()
                            }
                            val time: Long = real.get_TimeStamp() - last.time
                            val distance: Double = last.distanceTo(
                                filterLocation
                            ).toDouble()

                            /*val speed: Float = String.format("%.3f", distance / time).toFloat()*/
                            if (distance > 3f) {
                                if (isIgnored) { // case user not move
                                    locationViewModel.getLocationData()
                                        .postValue(LocationChangeEvent(lastLocation))
                                }
                                locationViewModel.getLocationData()
                                    .postValue(LocationChangeEvent(filterLocation))
                                lastLocation = filterLocation
                                currentSpeed = newLoc.speed
                                isIgnored = false
                            } else {
                                isIgnored = true
                            }

/*                            val message1 =
                                "distance1: $distance distance2: $distance2 speedCalc: $speed locationSpeed1: ${newLoc.speed}"
                            Log.e(
                                "onLocationChanged1",
                                message1
                            )

                       *//*     Toast.makeText(
                                applicationContext,
                                String.format("%.3f", distance),
                                Toast.LENGTH_LONG
                            ).show()*//*
                            val message2 =
                                "Location1: ${newLoc.latitude}, ${newLoc.longitude} Location2: ${real.get_lat()}, ${real.get_lng()}  "
                            Log.e(
                                "NewLocation",
                                message2
                            )*/
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
            }
        }
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
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("FusedLocationService", "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        Log.i("FusedLocationService", "onBind")
        return null
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
        Log.i("FusedLocationService", "onTaskRemoved")
    }


    override fun onDestroy() {
        super.onDestroy()
        /*activityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent())*/
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
        Log.i("FusedLocationService", "Location services is destroyed")
    }

}