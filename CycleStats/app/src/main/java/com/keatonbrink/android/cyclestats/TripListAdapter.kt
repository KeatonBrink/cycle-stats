package com.keatonbrink.android.cyclestats

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.keatonbrink.android.cyclestats.databinding.ListItemTripBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TripListHolder (
    val binding: ListItemTripBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(trip: TripDataWithPings) {
        binding.apply {
            binding.tripTitle.text = trip.tripData.title
            binding.tripDate.text = trip.tripData.date.toString()
            binding.tripTimeDuration.text = getTimeDurationFromPings(trip.locationPings)
//            TODO: Add distance calculation
            binding.tripDistance.text = trip.tripData.totalMiles.toString() + " miles"

            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "Trip clicked: ${trip.tripData.title}", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getDateStringFromCalendar(calendar: Calendar): String {
        val dateFormat = "MM/dd/yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US).format(calendar.time)
        return simpleDateFormat.format(calendar.time)
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
        return "${maxTime - minTime} seconds"
    }
}

class TripListAdapter(
    private val trips: List<TripDataWithPings>
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
