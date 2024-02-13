package com.keatonbrink.android.cyclestats

import android.util.Log
import java.util.Calendar
import java.util.UUID
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Random

private const val TAG = "TripListViewModel"

class TripListViewModel: ViewModel() {
    private val trips: MutableList<TripData> = mutableListOf()

    private val random = Random()



//    Generate random trip data for testing
    init {
        Log.d(TAG, "init starting")
        viewModelScope.launch {
            Log.d(TAG, "coroutine launched")
            trips += loadTrips()

            Log.d(TAG, "Loading complete")
        }
    }

    suspend fun loadTrips(): List<TripData> {
        var trips = mutableListOf<TripData>()
        var trip1 = TripData(
            UUID.randomUUID(),
            "Morning Ride",
            Date(),
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
//        delay(5000)
        return trips
    }

    fun addTrip(trip: TripData) {
        trips.add(trip)
    }
    fun getTrips(): MutableList<TripData> {
        return trips
    }
}