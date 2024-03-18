package com.keatonbrink.android.cyclestats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson

class SingleTripDetailsFragment: Fragment() {
    private var trip: TripDataWithPings? = null

    private var title: String = ""
    private var startTimeText: String = ""
    private var totalTimeText: String = ""
    private var totalDistanceText: String = ""
    private var avgSpeedText: String = ""
    private var notesText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val tripJson = it.getString(ARG_PARAM1)
            val gson = Gson()
            trip = gson.fromJson(tripJson, TripDataWithPings::class.java)
        }

        var totalDistance = trip!!.tripData.totalMiles
        val avgSpeedMPH = totalDistance / (getTimeDurationFromPingsAsSeconds(trip!!.locationPings) / 3600)

        // Apply items to fragment
//        1. Title
//        2. Start time
//        3. total time
//        4. total distance
//        5. average speed
//        6. notes
        title = trip!!.tripData.title
        startTimeText = "Start Time: ${getTimeOfDayFromEpochSeconds(trip!!.tripData.startTime)}"
        totalTimeText = "Total Time: ${getTimeDurationFromPingsAsString(trip!!.locationPings)}"
        totalDistanceText = "Total Distance: ${String.format("%.2f", totalDistance)} miles"
        avgSpeedText = "Average Speed: ${String.format("%.2f", avgSpeedMPH)} mph"
        notesText = "Notes: ${trip!!.tripData.tripNotes}"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_single_trip_details, container, false)

        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.start_time).text = startTimeText
        view.findViewById<TextView>(R.id.total_time).text = totalTimeText
        view.findViewById<TextView>(R.id.total_distance).text = totalDistanceText
        view.findViewById<TextView>(R.id.average_speed).text = avgSpeedText
        view.findViewById<TextView>(R.id.notes).text = notesText
        return view
    }

    companion object {
        private const val ARG_PARAM1 = "trip"

        fun newInstance(trip: String) =
            SingleTripDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, trip)
                }
            }
    }
}