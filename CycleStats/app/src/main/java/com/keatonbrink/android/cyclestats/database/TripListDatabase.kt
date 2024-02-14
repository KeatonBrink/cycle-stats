package com.keatonbrink.android.cyclestats.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.keatonbrink.android.cyclestats.LocationPing
import com.keatonbrink.android.cyclestats.TripData

@Database(entities = [TripData::class, LocationPing::class], version = 1)
@TypeConverters(TripTypeConverters::class)
abstract class TripListDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun tripLocationPingDao(): TripLocationPingDao

    companion object {
        @Volatile
        private var INSTANCE: TripListDatabase? = null

        fun getDatabase(context: Context): TripListDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    TripListDatabase::class.java,
                    "trip_list_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}