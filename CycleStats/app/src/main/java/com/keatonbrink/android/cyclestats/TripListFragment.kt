package com.keatonbrink.android.cyclestats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.keatonbrink.android.cyclestats.databinding.FragmentTripListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

private const val TAG = "TripListFragment"

class TripListFragment: Fragment() {
    private var _binding: FragmentTripListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val tripListViewModel: TripListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTripListBinding.inflate(inflater, container, false)

        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        LinearSnapHelper().attachToRecyclerView(binding.tripsRecyclerView)

        binding.tripsRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val mainActivity = activity as? MainActivity
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onScrollStateChanged: SCROLL_STATE_IDLE")
                    val currentVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: currentVisibleItemPosition: $currentVisibleItemPosition")
                    if (currentVisibleItemPosition != RecyclerView.NO_POSITION) {
                        val trip = (recyclerView.adapter as TripListAdapter).getTripAtPosition(currentVisibleItemPosition)
                        Log.d(TAG, "onScrollStateChanged: trip: ${trip.tripData.title}")
                        // Call add trip pings to map from MainActivity
                        mainActivity?.addTripPingsToMapAsPolyLines(trip)
                    }
                } else if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    Log.d(TAG, "onScrollStateChanged: SCROLL_STATE_DRAGGING")
                    mainActivity?.clearMap()
                } else if(newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    Log.d(TAG, "onScrollStateChanged: SCROLL_STATE_SETTLING")
                } else {
                    Log.d(TAG, "onScrollStateChanged: UNKNOWN")
                }
            }
        })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                tripListViewModel.trips.collect { trips ->
                    binding.tripsRecyclerView.adapter = TripListAdapter(trips)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}