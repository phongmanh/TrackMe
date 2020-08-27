package com.manhnguyen.trackme.system.locations

import android.app.IntentService
import android.content.Intent
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

class DetectedActivitiesIntentService : IntentService("DetectService") {
    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onHandleIntent(intent: Intent?) {

        var currentActivity = DetectedActivity.UNKNOWN
        var messenger: Messenger? = null
        val bundle = intent?.extras
        if (bundle != null) {
            messenger = bundle.get("MESSENGER") as Messenger
        }

/*        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent) as ActivityTransitionResult
            result.transitionEvents?.let { events ->
                messenger?.let {
                    if (!events.isNullOrEmpty()) {
                        val msg = Message.obtain()
                        msg.arg1 = events[events.size - 1].activityType
                        messenger.send(msg)
                    }
                    *//*       for (item in events){
                               val msg = Message.obtain()
                               msg.arg1 = item.activityType
                               messenger.send(msg)
                           }*//*
                }
            }
        } else*/

        /*
        *
        *   WALKING (confidence = 80)
            RUNNING (confidence = 20)
            IN_VEHICLE (confidence = 10)
        * */

        val result = ActivityRecognitionResult.extractResult(intent)
        val probably = result.mostProbableActivity
        /*if (probably.confidence >= 50) {*/
            currentActivity = probably.type
        /*}*/

        messenger?.let {
            val msg = Message.obtain()
            msg.arg1 = currentActivity
            messenger.send(msg)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}