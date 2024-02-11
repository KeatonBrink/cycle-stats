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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.keatonbrink.android.cyclestats.databinding.FragmentCurrentTripDetailBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CurrentTripFragment: Fragment() {

    private lateinit var binding: FragmentCurrentTripDetailBinding

    private val runStatus = CurrentRunTimer(R.string.start_button, "", 0, false)
    private lateinit var currentTrip: TripData
    private var trackingEnabled = false
    private val logIntervalMinnis = Constants.LOG_INTERVAL_MILLIS
    private val trips: MutableList<TripData> = mutableListOf()
    private val requestCode = 200

    // Location Pings
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentTripDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

                currentTrip = TripData(
//                    I am planning to change this when added to db
                    UUID.randomUUID(),
                    getCurrentTimeString(),
                    Calendar.getInstance(),
                    System.currentTimeMillis() / 1000,
                    mutableListOf()
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

                stopLogging()

//                TODO: Add the trip to the database
                trips.add(currentTrip)
                Intent(requireContext(), CyclingService::class.java).also {
                    it.action = CyclingService.Actions.STOP.name
                    requireContext().startService(it)
                }
            }
        }

        binding.debugButton.setOnClickListener { _: View ->
            var tripString = ""
            for ((i, trip) in trips.withIndex()) {
                Log.i("TAG", "Trip " + i.toString() + " has a number of pings: " + trip.pings.size)
                tripString += "Trip " + i.toString() + " has a number of pings: " + trip.pings.size + "\n"
                tripString += trip.pings.toString() + "\n"
                for (ping in trip.pings) {
                    Log.i("TAG", "Ping: " + ping.latitude.toString() + ", " + ping.longitude.toString())
                }
            }
            binding.tripData.text = tripString
        }

        binding.apply {
            
        }
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
                    val curLocationPing = LocationPings(
//                    I am planning to change this when added to db

                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        location.latitude,
                        location.longitude,
                        location.altitude,
                        location.speed,
                        location.time
                    )
                    currentTrip.pings.add(curLocationPing)
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