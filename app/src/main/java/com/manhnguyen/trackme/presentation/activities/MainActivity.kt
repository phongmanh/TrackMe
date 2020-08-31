package com.manhnguyen.trackme.presentation.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ProcessLifecycleOwner

import butterknife.BindView
import butterknife.ButterKnife
import com.manhnguyen.trackme.ApplicationController
import com.manhnguyen.trackme.base.ActivityBase
import com.manhnguyen.trackme.R
import com.manhnguyen.trackme.common.Constants
import com.manhnguyen.trackme.presentation.recording.RecordFragment
import com.manhnguyen.trackme.presentation.history.TrackingHistoryFragment
import com.manhnguyen.trackme.system.locations.FusedLocationService
import com.manhnguyen.trackme.util.PermissionUtils
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

import javax.inject.Inject


class MainActivity : ActivityBase() {

    @Inject
    lateinit var mainViewModel: MainViewModel

    @BindView(R.id.progressBar)
    lateinit var progressBar: MaterialProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!isTaskRoot) {
            Log.e("LoginActivity", "Prevent relaunch")
            finish()
            return
        }

        ButterKnife.bind(this)
        ApplicationController.appComponent.inject(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            ActivityLifecycle(mainViewModel, this)
        )
        showProgressBar(progressBar)
        initView(Constants.ViewType.HISTORY)
        requestPermission()
    }

    private fun init() {
        startLocationService()
        closeProgressbar(progressBar)
    }

    private fun requestPermission() {
        val permissionArrayList: Array<String> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        PermissionUtils.requestPermission(this,
            permissionArrayList
            ,
            object : PermissionUtils.PermissionResultHandler {
                override fun onGranted(results: Array<PermissionUtils.PermissionRequestResult?>) {
                    init()
                }

                override fun onDenied(results: Array<PermissionUtils.PermissionRequestResult?>) {
                    Toast.makeText(baseContext, "Permission Denied", Toast.LENGTH_LONG).show()
                }

                override fun onDeniedForever(results: Array<PermissionUtils.PermissionRequestResult?>) {
                    Toast.makeText(baseContext, "Denied Forever", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun startLocationService() {
        try {
            val intentService = Intent(this, FusedLocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(intentService)
            else
                startService(intentService)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopLocationService() {
        val intentService = Intent(this, FusedLocationService::class.java)
        stopService(intentService)
    }


    fun initView(type: Constants.ViewType) {
        try {
            when (type) {
                Constants.ViewType.HISTORY -> replaceFragment(TrackingHistoryFragment(), "history")
                Constants.ViewType.RECORD -> replaceFragment(RecordFragment(), "map")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationService()
    }


}





