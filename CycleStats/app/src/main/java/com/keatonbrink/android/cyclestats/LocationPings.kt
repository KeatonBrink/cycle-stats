package com.keatonbrink.android.cyclestats

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TripData::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE // Optional: Specify the action on deletion
        )
    ]
)
data class LocationPings(
//    Primary key
    @PrimaryKey val id: UUID,
//    Foreign key
    var tripId: UUID,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val time: Long,
)
