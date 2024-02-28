package com.keatonbrink.android.cyclestats

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.keatonbrink.android.cyclestats.Constants.EARTH_RADIUS_KM
import com.keatonbrink.android.cyclestats.Constants.KM_TO_MILES
import com.keatonbrink.android.cyclestats.databinding.FragmentCurrentTripDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class CurrentTripFragment: Fragment() {

    private var _binding: FragmentCurrentTripDetailBinding? = null
    private val binding get() = checkNotNull(_binding) {
        "Cannot access the binding because it is null. Is the view visible"
    }

    private val runStatus = CurrentRunTimer(R.string.start_button, "", 0, false)
    private lateinit var currentTrip: TripDataWithPings
    private var trackingEnabled = false
    private val logIntervalMinnis = Constants.LOG_INTERVAL_MILLIS
    private lateinit var mainActivity: MainActivity
    private val requestCode = 200

    private val tripListViewModel: TripListViewModel by viewModels()

    // Location Pings
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentTripDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        locationRequest = LocationRequest.Builder(logIntervalMinnis).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.toggleCycleButton.setOnClickListener { _: View ->
            runStatus.isCycling = !runStatus.isCycling
            if (runStatus.isCycling) {
                getPermissionsIfNeeded()

                if (isPermissionDenied()) {
                    runStatus.isCycling = !runStatus.isCycling
                    return@setOnClickListener
                }

                // log to the console that the user has started cycling
                Log.i("TAG", "User has started cycling")

                Toast.makeText(requireContext(), R.string.start_cycling, Toast.LENGTH_LONG).show()

                runStatus.statusTextID = R.string.stop_button
                runStatus.startTimeString = getCurrentTimeString()
                runStatus.startTimeSeconds = System.currentTimeMillis() / 1000
                binding.toggleCycleButton.setText(runStatus.statusTextID)
                binding.runTime.text = runStatus.startTimeString

                currentTrip = TripDataWithPings(
//                    I am planning to change this when added to db
                    TripData(0,
                        getCurrentTimeString(),
                        Date(),
                        System.currentTimeMillis() / 1000,
                        0.0),
                    mutableListOf(),
                )

                Intent(requireContext(), CyclingService::class.java).also {
                    it.action = CyclingService.Actions.START.name
                    requireContext().startService(it)
                }

                startLogging()
            } else {
                Toast.makeText(requireContext(), R.string.stop_cycling, Toast.LENGTH_LONG).show()

                runStatus.statusTextID = R.string.start_button
                runStatus.startTimeString = ""
                runStatus.startTimeSeconds = 0
                binding.toggleCycleButton.setText(runStatus.statusTextID)
                binding.runTime.text = ""

                currentTrip.tripData.totalMiles = calculateTotalMiles(currentTrip.locationPings)

                stopLogging()

//                TODO: Add the trip to the database
//                mainActivity.addTrip(currentTrip)
                showNewTrip(currentTrip)
                Intent(requireContext(), CyclingService::class.java).also {
                    it.action = CyclingService.Actions.STOP.name
                    requireContext().startService(it)
                }
            }
        }

//        binding.debugButton.setOnClickListener { _: View ->
//            val tripString = ""
//            for ((i, _) in mainActivity.getTrips().withIndex()) {
//                Log.i("TAG", "Trip " + i.toString())
//            }
//            binding.tripData.text = tripString
//        }

        binding.apply {
            
        }
    }

    private fun showNewTrip(trip: TripDataWithPings) {
        viewLifecycleOwner.lifecycleScope.launch {
            tripListViewModel.addTripWithPings(trip)
        }
        mainActivity.addTripPingsToMapAsPolyLines(trip)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calculateTotalMiles(pings: MutableList<LocationPing>): Double {
        var totalMiles = 0.0
        for (i in 0 until pings.size - 1) {
            totalMiles += calculateMilesBetweenPings(pings[i], pings[i + 1])
        }
        return totalMiles
    }

    private fun calculateMilesBetweenPings(ping1: LocationPing, ping2: LocationPing): Double {
        val dLat = Math.toRadians(ping2.latitude - ping1.latitude)
        val dLon = Math.toRadians(ping2.longitude - ping2.longitude)
        val originLat = Math.toRadians(ping1.latitude)
        val destinationLat = Math.toRadians(ping2.latitude)

        val a = sin(dLat / 2).pow(2.toDouble()) + sin(dLon / 2).pow(2.toDouble()) * cos(originLat) * cos(destinationLat)
        val c = 2 * asin(sqrt(a))
        return EARTH_RADIUS_KM * c * KM_TO_MILES
    }


    private fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.US)
        return sdf.format(Date())
    }

    private fun startLogging() {
        trackingEnabled = true
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let { location ->
                    val curLocationPing = LocationPing(
//                    I am planning to change this when added to db

                        0,
                        0,
                        location.latitude,
                        location.longitude,
                        location.altitude,
                        location.speed,
                        location.time / 1000
                    )
                    currentTrip.locationPings.add(curLocationPing)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    private fun stopLogging() {
        trackingEnabled = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun getPermissionsIfNeeded() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED) {
            getPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_DENIED) {
            getPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun getPermission(appPermission: String) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(appPermission), requestCode)
    }

    private fun isPermissionDenied(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_DENIED
    }

}