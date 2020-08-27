package com.manhnguyen.trackme.presentation.activities

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class ActivityLifecycle(val mainViewModel: MainViewModel, val activity: MainActivity) :
    LifecycleObserver {

    private var resumed = 0
    private var started = 0

    private val DebugName = "AppLifecycleHandler"

    private var isVisible = false
    private var isInForeground = false

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppInBackground() {
        --started;
        android.util.Log.w(
            DebugName,
            "onActivityStopped -> application is visible: " + (started > 0) + " (" + activity::class + ")"
        );
        setVisible((started > 0));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppInPause() {
        --resumed;
        android.util.Log.w(
            DebugName,
            "onActivityPaused -> application is in foreground: " + (resumed > 0) + " (" + activity::class + ")"
        )
        setForeground((resumed > 0));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppResume() {
        ++resumed
        android.util.Log.w(
            DebugName,
            "onActivityResumed -> application is in foreground: " + (resumed > 0) + " (" + activity::class + ")"
        )
        setForeground((resumed > 0));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppInForeground() {
        ++started;
        android.util.Log.w(
            DebugName,
            "onActivityStarted -> application is visible: " + (started > 0) + " (" + activity::class + ")"
        )
        setVisible((started > 0));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroyed() {
        print("onAppDestroyed")
    }


    private fun setVisible(visible: Boolean) {
        if (isVisible == visible) {
            // no change
            return
        }

        // visibility changed
        isVisible = visible
        Log.w(
            DebugName,
            "App Visiblility Changed -> application is visible: $isVisible"
        )

        // take some action on change of visibility
    }

    private fun setForeground(inForeground: Boolean) {
        if (isInForeground == inForeground) {
            // no change
            return
        }

        // in foreground changed
        isInForeground = inForeground
        Log.w(
            DebugName,
            "App In Foreground Changed -> application is in foreground: $isInForeground"
        )

        // take some action on change of in foreground
    }

    fun isApplicationVisible(): Boolean {
        return started > 0
    }

    fun isApplicationInForeground(): Boolean {
        return resumed > 0
    }


}