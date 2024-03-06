package com.keatonbrink.android.cyclestats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson

class SummaryDetailsFragment : Fragment() {
    private var trips: List<TripDataWithPings>? = null

    private var totalTimeText: String = ""
    private var totalDistanceText: String = ""
    private var avgSpeedText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val tripsJson = it.getString(ARG_PARAM1)
            val gson = Gson()
            trips = gson.fromJson(tripsJson, Array<TripDataWithPings>::class.java).toList()
        }
        // Calculate in seconds, and eventually convert to hours and minutes
        var totalTime = 0
        for(trip in trips!!) {
            val startTime = trip.tripData.startTime
            var endTime = 0L
            for(ping in trip.locationPings) {
                if(ping.time > endTime) {
                    endTime = ping.time
                }
            }
            val tripTime = endTime - startTime
            totalTime += (tripTime).toInt()
        }
        val totalHours = totalTime / 3600
        val totalMinutes = (totalTime % 3600) / 60

        var totalDistance = 0.0
        for(trip in trips!!) {
            totalDistance += trip.tripData.totalMiles
        }

        val avgSpeedMPH = totalDistance / (totalTime / 3600)

//        Apply 3 items to the fragment
//        1. Total time
//        2. Total distance
//        3. Average speed
        totalTimeText = "Total Time: ${totalHours}h ${totalMinutes}m"
        totalDistanceText = "Total Distance: ${String.format("%.2f", totalDistance)} miles"
        avgSpeedText = "Average Speed: ${String.format("%.2f", avgSpeedMPH)} mph"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_summary_details, container, false)

        // Update TextViews with calculated values
        view.findViewById<TextView>(R.id.total_time).text = totalTimeText
        view.findViewById<TextView>(R.id.total_distance).text = totalDistanceText
        view.findViewById<TextView>(R.id.average_speed).text = avgSpeedText

        return view
    }

    companion object {

        private const val ARG_PARAM1 = "tripsJson"

        fun newInstance(tripsJson: String) =
            SummaryDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, tripsJson)
                }
            }
    }
}