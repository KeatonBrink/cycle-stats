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
    fun bind(trip: TripData) {
        binding.apply {
            binding.tripTitle.text = trip.title
            binding.tripDate.text = getDateStringFromCalendar(trip.date)
            binding.tripTimeDuration.text = getTimeDurationFromPings(trip.pings)
//            TODO: Add distance calculation
            binding.tripDistance.text = "0.0 miles"

            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "Trip clicked: ${trip.title}", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getDateStringFromCalendar(calendar: Calendar): String {
        val dateFormat = "MM/dd/yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US).format(calendar.time)
        return simpleDateFormat.format(calendar.time)
    }

    private fun getTimeDurationFromPings(pings: List<LocationPings>): String {
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
    private val trips: List<TripData>
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


//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripListHolder {
//        val binding = ListItemTripBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return TripListHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: TripListHolder, position: Int) {
//        val trip = trips[position]
//        holder.binding.apply {
//            tripTitle.text = trip.title
//            tripDate.text = trip.date.toString()
//        }
//    }
//
//    override fun getItemCount() = trips.size
}
