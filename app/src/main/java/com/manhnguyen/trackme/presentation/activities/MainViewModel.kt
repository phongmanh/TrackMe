package com.manhnguyen.trackme.presentation.activities

import androidx.lifecycle.*
import com.manhnguyen.trackme.common.SchedulerProvider
import com.manhnguyen.trackme.data.room.databases.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var provider: SchedulerProvider

}