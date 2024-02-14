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
        )
        trips.add(trip1)
        val trip2 = TripData(
            UUID.randomUUID(),
            "Afternoon Ride",
            Date(),
            System.currentTimeMillis(),
        )
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