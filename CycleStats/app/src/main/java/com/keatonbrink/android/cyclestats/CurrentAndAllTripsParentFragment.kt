package com.keatonbrink.android.cyclestats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

import com.keatonbrink.android.cyclestats.databinding.FragmentCurrentAndListTripParentBinding

class CurrentAndAllTripsParentFragment : Fragment() {
    private var _binding: FragmentCurrentAndListTripParentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentAndListTripParentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Replace the current trip fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.currentTripContainer, CurrentTripFragment())
            .commit()

        // Replace the trip list fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.tripListContainer, TripListFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
