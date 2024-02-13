package com.keatonbrink.android.cyclestats

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class TripData(
    @PrimaryKey var id: UUID,
    var title: String,
    var date: Date,
    var startTime: Long,
//    var pings: MutableList<LocationPings>,
)