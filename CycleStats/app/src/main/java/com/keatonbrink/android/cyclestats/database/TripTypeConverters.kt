package com.keatonbrink.android.cyclestats.database

import androidx.room.TypeConverter
import java.util.Date

class TripTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}