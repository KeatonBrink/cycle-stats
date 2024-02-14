package com.keatonbrink.android.cyclestats

import androidx.room.Embedded
import androidx.room.Relation

data class TripDataWithPings(
    @Embedded val tripData: TripData,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val locationPings: MutableList<LocationPing>
)
