package com.keatonbrink.android.cyclestats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

private const val TAG = "TripListFragment"

class TripListFragment: Fragment() {
    private val tripListViewModel: TripListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total trips: ${tripListViewModel.getTrips().size}")
    }
}