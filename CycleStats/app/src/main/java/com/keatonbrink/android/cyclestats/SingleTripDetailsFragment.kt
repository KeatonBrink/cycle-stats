package com.keatonbrink.android.cyclestats

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class SingleTripDetailsFragment: Fragment() {
    private var trip: TripDataWithPings? = null
    private lateinit var repository: TripRepository


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
        val avgSpeedMPH = totalDistance / (getTimeDurationFromPingsAsSeconds(trip!!.locationPings).toDouble() / 3600)

        // Apply items to fragment
//        1. Title
//        2. Start time
//        3. total time
//        4. total distance
//        5. average speed
//        6. notes
        title = trip!!.tripData.title + " "
        startTimeText = "Start Time: ${getTimeOfDayFromEpochSeconds(trip!!.tripData.startTime)}"
        totalTimeText = "Total Time: ${getTimeDurationFromPingsAsString(trip!!.locationPings)}"
        totalDistanceText = "Total Distance: ${String.format("%.2f", totalDistance)} miles"
        avgSpeedText = "Average Speed: ${String.format("%.2f", avgSpeedMPH)} mph"
        notesText = "Notes: ${trip!!.tripData.tripNotes}"

        repository = TripRepository.get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_single_trip_details, container, false)

        view.findViewById<TextView>(R.id.title).text = title
        // Add on click listener to edit button next to title
        view.findViewById<ImageButton>(R.id.edit_title).setOnClickListener {
//            Log.d("SingleTripDetailsFragment", "Edit button clicked")
            updateTripTitle(view)
        }
        view.findViewById<TextView>(R.id.start_time).text = startTimeText
        view.findViewById<TextView>(R.id.total_time).text = totalTimeText
        view.findViewById<TextView>(R.id.total_distance).text = totalDistanceText
        view.findViewById<TextView>(R.id.average_speed).text = avgSpeedText
        view.findViewById<TextView>(R.id.notes).text = notesText
        // Add on click listener to edit button next to notes
        view.findViewById<ImageButton>(R.id.edit_notes).setOnClickListener {
//            Log.d("SingleTripDetailsFragment", "Edit button clicked")
            updateTripNotes(view)
        }
        return view
    }

    private fun updateTripTitle(viewer: View) {
//        Ask the user to input a new title for the trip
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_trip_title, null)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        editTextTitle.setHint(trip!!.tripData.title)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Trip Title")
            .setView(dialogView)
            .setPositiveButton("Confirm") { dialog, _ ->
                val title = editTextTitle.text.toString()
                if (title.isNotEmpty()) {
                    trip!!.tripData.title = title
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.updateTrip(trip!!.tripData)
                    }
                    viewer.findViewById<TextView>(R.id.title).text = title
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun updateTripNotes(viewer: View) {
//        Ask the user to input new notes for the trip
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_trip_notes, null)
        val editTextNotes = dialogView.findViewById<EditText>(R.id.editTextNotes)
        editTextNotes.setText(trip!!.tripData.tripNotes)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Trip Notes")
            .setView(dialogView)
            .setPositiveButton("Confirm") { dialog, _ ->
                var notes = editTextNotes.text.toString()
                trip!!.tripData.tripNotes = notes
                CoroutineScope(Dispatchers.IO).launch {
                    repository.updateTrip(trip!!.tripData)
                }
                notes = "Notes: $notes"
                viewer.findViewById<TextView>(R.id.notes).text = notes
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()

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