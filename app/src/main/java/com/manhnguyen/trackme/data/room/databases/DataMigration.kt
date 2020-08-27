package com.manhnguyen.trackme.data.room.databases

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Singleton

@Singleton
class DataMigration {


    val migration_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE UserTrackingBody  ADD COLUMN UserId TEXT")

            database.execSQL("ALTER TABLE WorkEventModel  ADD COLUMN UserId TEXT")
        }
    }

    val migration_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS CheckPointRequest (Id INTEGER NOT NULL , Type INTEGER NOT NULL, SiteId TEXT NOT NULL, Name TEXT NOT NULL, Points TEXT NOT NULL, PRIMARY KEY('Id')  )")
        }
    }

    val migration_3_4 = object : Migration(3,4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS UserTrackingBodyTemp (Id INTEGER NOT NULL , UserId TEXT, SiteId TEXT, Points TEXT NOT NULL, PRIMARY KEY('Id')  )")
            database.execSQL("INSERT INTO UserTrackingBodyTemp (UserId,SiteId,Points) SELECT UserId,SiteId,Points FROM UserTrackingBody")
            database.execSQL("DROP TABLE UserTrackingBody")
            database.execSQL("ALTER TABLE UserTrackingBodyTemp RENAME TO UserTrackingBody")
        }
    }

    val migration_4_5 = object : Migration(4,5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE UserTrackingBody  ADD COLUMN CreatedDate TEXT")
        }
    }

}