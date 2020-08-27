package com.manhnguyen.trackme.di.module

import com.manhnguyen.trackme.system.locations.FusedLocationService
import com.manhnguyen.trackme.system.locations.LocationService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun providerLocationService(): LocationService

    @ContributesAndroidInjector
    abstract fun providerFusedLocationService(): FusedLocationService
    
}