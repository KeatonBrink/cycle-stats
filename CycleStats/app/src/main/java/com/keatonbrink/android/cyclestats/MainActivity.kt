package com.keatonbrink.android.cyclestats

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.keatonbrink.android.cyclestats.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationRequest = LocationRequest.Builder(logIntervalMinnis).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.toggleCycleButton.setOnClickListener { _: View ->
            runStatus.isCycling = !runStatus.isCycling
            if (runStatus.isCycling) {
                getPermissionsIfNeeded()

                if (isPermissionDenied()) {
                    runStatus.isCycling = !runStatus.isCycling
                    return@setOnClickListener
                }

                Toast.makeText(this, R.string.start_cycling, Toast.LENGTH_LONG).show()

                runStatus.statusTextID = R.string.stop_button
                runStatus.startTimeString = getCurrentTimeString()
                runStatus.startTimeSeconds = System.currentTimeMillis() / 1000
                binding.toggleCycleButton.setText(runStatus.statusTextID)
                binding.runTime.text = runStatus.startTimeString

                currentTrip = TripData(
                    getCurrentTimeString(),
                    Calendar.getInstance(),
                    System.currentTimeMillis() / 1000,
                    mutableListOf()
                )

                startLogging()
            } else {
                Toast.makeText(this, R.string.stop_cycling, Toast.LENGTH_LONG).show()

                runStatus.statusTextID = R.string.start_button
                runStatus.startTimeString = ""
                runStatus.startTimeSeconds = 0
                binding.toggleCycleButton.setText(runStatus.statusTextID)
                binding.runTime.text = ""

                stopLogging()

                trips.add(currentTrip)
            }
        }

        binding.debugButton.setOnClickListener { _: View ->
            for ((i, trip) in trips.withIndex()) {
                Log.i("TAG", "Trip " + i.toString() + " has a number of pings: " + trip.pings.size)
                for (ping in trip.pings) {
                    Log.i("TAG", "Ping: " + ping.latitude.toString() + ", " + ping.longitude.toString())
                }
            }
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
                    currentTrip.pings.add(location)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
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
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            getPermission(ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
            getPermission(ACCESS_BACKGROUND_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            getPermission(ACCESS_COARSE_LOCATION)
        }
    }

    private fun getPermission(appPermission: String) {
        ActivityCompat.requestPermissions(this, arrayOf(appPermission), requestCode)
    }

    private fun isPermissionDenied(): Boolean {
        return ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
    }

}