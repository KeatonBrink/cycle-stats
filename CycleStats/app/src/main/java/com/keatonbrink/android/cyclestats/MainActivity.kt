package com.keatonbrink.android.cyclestats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.keatonbrink.android.cyclestats.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val runStatus = CurrentRunTimer(R.string.start_button, "", 0, false)
    private var trackingEnabled = false
    private val logIntervalMinnis = Constants.LOG_INTERVAL_MILLIS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toggleCycleButton.setOnClickListener{ _: View ->
            runStatus.isCycling = !runStatus.isCycling
            if (runStatus.isCycling) {
                Toast.makeText(this, R.string.start_cycling, Toast.LENGTH_LONG).show()
                runStatus.statusTextID = R.string.stop_button
                runStatus.startTimeString = getCurrentTimeString()
                runStatus.startTimeSeconds = System.currentTimeMillis() / 1000
                binding.toggleCycleButton.setText(runStatus.statusTextID)
                binding.runTime.text = runStatus.startTimeString
                startLogging()
            } else {
                Toast.makeText(this, R.string.stop_cycling, Toast.LENGTH_LONG).show()
                runStatus.statusTextID = R.string.start_button
                binding.toggleCycleButton.setText(runStatus.statusTextID)
                stopLogging()
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
            Log.i("TAG", "Data can be captured here" + getCurrentTimeString())
            Handler(Looper.getMainLooper()).postDelayed({
                logRunnable()
            }, logIntervalMinnis)
        }
    }

    private fun stopLogging() {
        trackingEnabled = false
    }
}