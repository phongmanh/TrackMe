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

        val result = ActivityRecognitionResult.extractResult(intent)
        val probably = result.mostProbableActivity
            currentActivity = probably.type
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