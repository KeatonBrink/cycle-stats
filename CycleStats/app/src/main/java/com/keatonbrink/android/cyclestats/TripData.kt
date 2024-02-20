package com.keatonbrink.android.cyclestats

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "trip_data")
data class TripData(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var title: String,
    var date: Date,
    var startTime: Long,
    var totalMiles: Double,
//    var pings: MutableList<LocationPings>,
)