package com.keatonbrink.android.cyclestats.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.keatonbrink.android.cyclestats.TripData

@Database(entities = [TripData::class], version = 1)
abstract class TripListDatabase : RoomDatabase() {

}