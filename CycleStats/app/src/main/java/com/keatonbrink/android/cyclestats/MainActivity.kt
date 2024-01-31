package com.keatonbrink.android.cyclestats

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
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
    private val trips: MutableList<TripData> = mutableListOf<TripData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Currently using only one button that alternates functionality on press
        // I should probably reconsider this setup
        binding.toggleCycleButton.setOnClickListener{ _: View ->
            runStatus.isCycling = !runStatus.isCycling
            if (runStatus.isCycling) {
                // Toast to the start of cycling
                Toast.makeText(this, R.string.start_cycling, Toast.LENGTH_LONG).show()

                // Handle initial button/screen changes
                runStatus.statusTextID = R.string.stop_button
                runStatus.startTimeString = getCurrentTimeString()
                runStatus.startTimeSeconds = System.currentTimeMillis() / 1000
                binding.toggleCycleButton.setText(runStatus.statusTextID)
                binding.runTime.text = runStatus.startTimeString

                // Handle the in-memory current trip setup
                currentTrip = TripData(
                    getCurrentTimeString(),
                    Calendar.getInstance(),
                    System.currentTimeMillis() / 1000,
                    mutableListOf<Location>()
                )
                currentTrip.pings.add(harvestData())

                // Start logging data as callback
                startLogging()
            } else {
                // Toast to the end of cycling
                Toast.makeText(this, R.string.stop_cycling, Toast.LENGTH_LONG).show()

                // Handle end of cycle button/screen changes
                // Also clearing out the cycle data, not necessary
                runStatus.statusTextID = R.string.start_button
                runStatus.startTimeString = ""
                runStatus.startTimeSeconds = 0
                binding.toggleCycleButton.setText(runStatus.statusTextID)
                binding.runTime.text = ""

                // Stop logging data as callback
                stopLogging()

                // Add last ping to current trip
                currentTrip.pings.add(harvestData())
                // Add current trip to list of trips
                trips.add(currentTrip)
            }
        }

        binding.debugButton.setOnClickListener { _: View ->
            for ((i, trip) in trips.withIndex()) {
                Log.i("TAG", "Trip " + i.toString() + " has a number of pings: " + trip.pings.size)
            }
        }
    }

    private fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.US)
        return sdf.format(Date())
    }

    private fun startLogging() {
        trackingEnabled = true
        Handler(Looper.getMainLooper()).postDelayed({
            logRunnable()
        }, logIntervalMinnis)
    }

    private fun logRunnable() {
        if (trackingEnabled) {
            currentTrip.pings.add(harvestData())
            Log.i("TAG", "Data can be captured here" + getCurrentTimeString())
            Handler(Looper.getMainLooper()).postDelayed({
                logRunnable()
            }, logIntervalMinnis)
        }
    }

    private fun harvestData(): Location {
        return Location("dummyLocation")
    }

    private fun stopLogging() {
        trackingEnabled = false
    }
}