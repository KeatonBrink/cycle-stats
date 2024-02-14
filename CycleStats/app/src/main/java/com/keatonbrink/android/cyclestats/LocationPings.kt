package com.keatonbrink.android.cyclestats

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "location_pings",
    foreignKeys = [
        ForeignKey(
            entity = TripData::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE // Optional: Specify the action on deletion
        )
    ]
)
data class LocationPing(
//    Primary key
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    Foreign key
    var tripId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val time: Long,
)
