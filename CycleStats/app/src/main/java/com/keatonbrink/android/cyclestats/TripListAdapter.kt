package com.keatonbrink.android.cyclestats

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.keatonbrink.android.cyclestats.databinding.ListItemTripBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripListHolder (
    val binding: ListItemTripBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(trip: TripDataWithPings) {
        binding.apply {
            binding.tripTitle.text = trip.tripData.title
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            binding.tripDate.text = sdf.format(trip.tripData.date)
            binding.tripStartTime.text = getTimeOfDayFromEpochSeconds(trip.tripData.startTime)
            binding.tripTimeDuration.text = getTimeDurationFromPings(trip.locationPings)
            binding.tripDistance.text = String.format("%.2f miles", trip.tripData.totalMiles)

            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "Trip clicked: ${trip.tripData.title}", Toast.LENGTH_SHORT).show()
            }

            binding.tripDeletion.setOnClickListener {
                // Set up a confirmation dialog
                val builder = AlertDialog.Builder(binding.root.context)
                builder.setMessage("Are you sure you want to delete this trip?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        Toast.makeText(binding.root.context, "Trip deleted: ${trip.tripData.title}", Toast.LENGTH_SHORT).show()
                        TripRepository.get().deleteTripDataWithPings(trip)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }

        }
    }

    private fun getTimeOfDayFromEpochSeconds(timeInSeconds: Long): String {
        val date = Date(timeInSeconds * 1000L) // Convert seconds to milliseconds
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    private fun getTimeDurationFromPings(pings: List<LocationPing>): String {
        var minTime = Long.MAX_VALUE
        var maxTime = Long.MIN_VALUE
        for (ping in pings) {
            if(ping.time < minTime) {
                minTime = ping.time
            }
            if(ping.time > maxTime) {
                maxTime = ping.time
            }
        }
        val duration = maxTime - minTime
        if (duration < 60) {
            return "$duration seconds"
        }
        if (duration < 3600) {
            // Display the seconds always with 2 digits
//            return "${duration / 60}m ${duration % 60} s"
            return "${duration / 60}m ${String.format("%02d", duration % 60)}s"
        }
        return "${duration / 3600}h ${String.format("%02d", duration % 3600 / 60)}m"
    }
}

class TripListAdapter(
    private val trips: List<TripDataWithPings>,
    ) : RecyclerView.Adapter<TripListHolder>() {

//        Generates unique view holders for each trip
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripListHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTripBinding.inflate(inflater, parent, false)
        return TripListHolder(binding)
    }

//    Populates the view holder with the data from the trip
    override fun onBindViewHolder(holder: TripListHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip)
    }

    override fun getItemCount() = trips.size

    fun getTripAtPosition(position: Int): TripDataWithPings {
        return trips[position]
    }

}
