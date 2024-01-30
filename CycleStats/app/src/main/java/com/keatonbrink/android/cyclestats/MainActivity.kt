package com.keatonbrink.android.cyclestats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.keatonbrink.android.cyclestats.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val runStatus = CurrentRunTimer(R.string.start_button, R.string.run_time_text, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toggleCycleButton.setOnClickListener{ view: View ->
            runStatus.isCycling = !runStatus.isCycling
            if (runStatus.isCycling) {
                Toast.makeText(this, R.string.start_cycling, Toast.LENGTH_LONG).show()
                runStatus.statusTextID = R.string.stop_button
                binding.toggleCycleButton.setText(runStatus.statusTextID)
            } else {
                Toast.makeText(this, R.string.stop_cycling, Toast.LENGTH_LONG).show()
                runStatus.statusTextID = R.string.start_button
                binding.toggleCycleButton.setText(runStatus.statusTextID)
            }
        }
    }
}