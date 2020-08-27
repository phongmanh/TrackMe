package com.manhnguyen.trackme.system.networking

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.manhnguyen.trackme.common.SchedulerProvider
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkViewModel @Inject constructor(private val schedulerProvider: SchedulerProvider) :
    ViewModel() {

    private val internetConnected = MutableLiveData<Boolean>()

    init {
        initInternetListener()
    }

    fun getInternetState(): LiveData<Boolean> {
        return internetConnected
    }

    fun getIsConnected(): Boolean {
        return internetConnected.value ?: false
    }

    @SuppressLint("CheckResult")
    fun initInternetListener() {
        try {
            val settings = InternetObservingSettings.builder()
                .interval(60 * 1000)
                .build()

            ReactiveNetwork.observeInternetConnectivity(settings)
                .observeOn(schedulerProvider.mainThread)
                .subscribeOn(schedulerProvider.ioScheduler)
                .subscribe {
                    if (internetConnected.value != it)
                        internetConnected.postValue(it)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}