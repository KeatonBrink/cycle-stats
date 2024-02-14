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
    private val tripRepository = TripRepository.get()

    val trips = tripRepository.getTrips()

//    Generate random trip data for testing
    init {
        viewModelScope.launch {
        }
    }
}