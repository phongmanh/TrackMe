package com.manhnguyen.trackme.di.module

import android.content.Context
import androidx.room.Room
import com.manhnguyen.trackme.data.room.databases.AppDatabase
import com.manhnguyen.trackme.data.room.databases.DataMigration
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "gpstrackingdb")
            .allowMainThreadQueries()
            .addMigrations(
                DataMigration().migration_1_2,
                DataMigration().migration_2_3,
                DataMigration().migration_3_4,
                DataMigration().migration_4_5
            )
            .build()
    }

}