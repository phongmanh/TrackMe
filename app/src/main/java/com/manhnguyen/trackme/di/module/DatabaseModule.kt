package com.manhnguyen.trackme.di.module

import android.content.Context
import androidx.room.Room
import com.manhnguyen.trackme.data.room.databases.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "trackmedb")
            .allowMainThreadQueries()
            .addMigrations()
            .build()
    }

}