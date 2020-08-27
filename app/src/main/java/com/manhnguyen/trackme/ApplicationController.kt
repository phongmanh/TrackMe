package com.manhnguyen.trackme

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleObserver
import androidx.multidex.MultiDexApplication
import com.manhnguyen.trackme.common.SharedPreferenceHelper
import com.manhnguyen.trackme.di.component.AppComponent
import com.manhnguyen.trackme.di.component.DaggerAppComponent
import com.manhnguyen.trackme.presentation.UncaughtExceptionHandler
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.*
import javax.inject.Inject


class ApplicationController : MultiDexApplication(), HasAndroidInjector, LifecycleObserver {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>


    companion object {
        val instance by lazy { ApplicationController() }
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler(this))
        SharedPreferenceHelper.getInstance(this)
        init()
    }

    @SuppressLint("CheckResult")
    fun init() {



    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

}
