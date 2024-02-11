package com.keatonbrink.android.cyclestats

import java.util.Calendar
import java.util.UUID
import androidx.lifecycle.ViewModel
import java.util.Random

class TripListViewModel: ViewModel() {
    private val trips: MutableList<TripData> = mutableListOf()

    private val random = Random()



//    Generate random trip data for testing
    init {
        var trip1 = TripData(
            UUID.randomUUID(),
            "Morning Ride",
            Calendar.getInstance(),
            System.currentTimeMillis(),
            mutableListOf(
                LocationPings(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    37.7749,
                    -122.4194,
                    0.0,
                    0.0f,
                    System.currentTimeMillis() + random.nextInt(100000)
                ),
                LocationPings(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    37.7749,
                    -122.4194,
                    0.0,
                    0.0f,
                    System.currentTimeMillis() + random.nextInt(100000)
                ),
                LocationPings(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    37.7749,
                    -122.4194,
                    0.0,
                    0.0f,
                    System.currentTimeMillis() + random.nextInt(100000)
                ),
                LocationPings(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    37.7749,
                    -122.4194,
                    0.0,
                    0.0f,
                    System.currentTimeMillis() + random.nextInt(100000)
                ),
                LocationPings(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    37.7749,
                    -122.4194,
                    0.0,
                    0.0f,
                    System.currentTimeMillis() + random.nextInt(100000)
                )
            )
        )
        for (i in 0 until trip1.pings.size) {
            trip1.pings[i].tripId = trip1.id
        }
        trips.add(trip1)
        val trip2 = TripData(
            UUID.randomUUID(),
            "Afternoon Ride",
            Calendar.getInstance(),
            System.currentTimeMillis(),
            mutableListOf(
                LocationPings(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    37.7749,
                    -122.4194,
                    0.0,
                    0.0f,
                    System.currentTimeMillis() + random.nextInt(100000)
                ),
                LocationPings(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    37.7749,
                    -122.4194,
                    0.0,
                    0.0f,
                    System.currentTimeMillis() + random.nextInt(100000)
                ),
                LocationPings(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    37.7749,
                    -122.4194,
                    0.0,
                    0.0f,
                    System.currentTimeMillis() + random.nextInt(100000)
                ),
                LocationPings(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    37.7749,
                    -122.4194,
                    0.0,
                    0.0f,
                    System.currentTimeMillis() + random.nextInt(100000)
                )
            )
        )
        for (i in 0 until trip2.pings.size) {
            trip2.pings[i].tripId = trip1.id
        }
        trips.add(trip2)
    }

    fun addTrip(trip: TripData) {
        trips.add(trip)
    }
    fun getTrips(): MutableList<TripData> {
        return trips
    }
}