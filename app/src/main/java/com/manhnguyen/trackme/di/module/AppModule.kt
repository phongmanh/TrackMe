package com.manhnguyen.trackme.di.module

import android.content.Context
import android.location.LocationManager
import com.manhnguyen.trackme.common.SchedulerProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module(includes = [], subcomponents = [])
class AppModule {

    private lateinit var locationManager: LocationManager

    @Provides
    @Singleton
    fun provideSchedulerProvider(): SchedulerProvider {
        return SchedulerProvider(
            Schedulers.io(),
            AndroidSchedulers.mainThread(),
            Schedulers.computation(),
            Schedulers.single(),
            Schedulers.trampoline()
        )
    }

    @Provides
    @Singleton
    fun providerLocationManager(context: Context): LocationManager {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager
    }

    @Provides
    @Singleton
    fun fusedLocationProviderClient(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

}