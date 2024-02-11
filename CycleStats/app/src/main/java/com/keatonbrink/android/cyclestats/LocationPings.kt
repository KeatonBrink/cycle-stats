package com.keatonbrink.android.cyclestats

import android.location.Location
import java.util.UUID

data class LocationPings(
//    Primary key
    val id: UUID,
//    Foreign key
    val tripId: UUID,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val time: Long,
)
