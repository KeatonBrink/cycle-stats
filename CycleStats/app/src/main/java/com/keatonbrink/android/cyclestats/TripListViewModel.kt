package com.keatonbrink.android.cyclestats

import android.util.Log
import java.util.Calendar
import java.util.UUID
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Random

private const val TAG = "TripListViewModel"

class TripListViewModel: ViewModel() {
    private val tripRepository = TripRepository.get()

    private val _trips: MutableStateFlow<List<TripDataWithPings>> = MutableStateFlow(emptyList())
    val trips: StateFlow<List<TripDataWithPings>>
        get() = _trips.asStateFlow()

    suspend fun addTripWithPings(trip: TripDataWithPings) {
        tripRepository.addTripDataWithPings(trip)
    }

    fun deleteTripWithPings(trip: TripDataWithPings) {
        tripRepository.deleteTripDataWithPings(trip)
    }

//    Generate random trip data for testing
    init {
        viewModelScope.launch {
            tripRepository.getTrips().collect {
                _trips.value = it
            }
        }
    }
}