package com.manhnguyen.trackme.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.observers.DisposableObserver

class PermissionUtils {


    companion object {

        fun checkSelfPermission(context: Context, type: String): Boolean {
            if (ContextCompat.checkSelfPermission(
                    context,
                    type
                ) != PackageManager.PERMISSION_GRANTED
            )
                return false
            return true
        }

        fun locationPermission(context: Context): Boolean {
            try {
                val backgroundLocation: Boolean
                val locationStatus = checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) && checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    backgroundLocation = PermissionUtils.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                } else {
                    /*Auto grant*/
                    backgroundLocation = true
                }

                return locationStatus && backgroundLocation

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }


        fun checkActivityRecognition(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACTIVITY_RECOGNITION
                )
            else
                true
        }

        fun requestPermission(
            activity: FragmentActivity,
            permissions: Array<String>?,
            resultHandler: PermissionResultHandler
        ) {

            if (permissions != null && permissions.isNotEmpty()) {
                val i = intArrayOf(0)
                val granted = booleanArrayOf(true)
                val hasRationale = booleanArrayOf(false)
                val returnedPermissions: Array<PermissionRequestResult?> =
                    arrayOfNulls(permissions.size)

                val rxPermissions = RxPermissions(activity)
                rxPermissions
                    .requestEach(*permissions)
                    .subscribe(object : DisposableObserver<Permission>() {
                        override fun onNext(@NonNull permission: Permission) {
                            if (!permission.granted) {
                                granted[0] = false
                            }

                            if (!permission.shouldShowRequestPermissionRationale) {
                                hasRationale[0] = true
                            }

                            returnedPermissions[i[0]] = PermissionRequestResult(
                                permission.name,
                                permission.granted,
                                !permission.shouldShowRequestPermissionRationale
                            )
                            i[0]++

                            if (i[0] == permissions.size) {
                                if (granted[0]) {
                                    resultHandler.onGranted(returnedPermissions)
                                } else {
                                    if (!hasRationale[0]) {
                                        resultHandler.onDenied(returnedPermissions)
                                    } else {
                                        resultHandler.onDeniedForever(returnedPermissions)

                                    }
                                }
                            }
                        }

                        override fun onError(@NonNull e: Throwable) {
                            e.printStackTrace()

                        }

                        override fun onComplete() {

                        }
                    })
            }
        }

    }

    class PermissionRequestResult {
        var permissionName: String = ""
        var isGranted: Boolean = false
        var isWontAskAgain: Boolean = false

        constructor(permissionName: String, isGranted: Boolean, isWontAskAgain: Boolean) {
            this.permissionName = permissionName
            this.isGranted = isGranted
            this.isWontAskAgain = isWontAskAgain
        }
    }

    interface PermissionResultHandler {
        fun onGranted(results: Array<PermissionRequestResult?>)
        fun onDenied(results: Array<PermissionRequestResult?>)
        fun onDeniedForever(results: Array<PermissionRequestResult?>)
    }

}