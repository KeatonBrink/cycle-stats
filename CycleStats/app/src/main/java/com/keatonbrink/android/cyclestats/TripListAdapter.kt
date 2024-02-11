package com.keatonbrink.android.cyclestats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.keatonbrink.android.cyclestats.databinding.ListItemTripBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TripListHolder (
    val binding: ListItemTripBinding
    ) : RecyclerView.ViewHolder(binding.root) {
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
        var minTime = Long.MAX_VALUE
        var maxTime = Long.MIN_VALUE
        for (ping in trip.pings) {
            if(ping.time < minTime) {
                minTime = ping.time
            }
            if(ping.time > maxTime) {
                maxTime = ping.time
            }
        }
        val tripTimeDuration = "${maxTime - minTime} seconds"
        holder.apply {
            binding.tripTitle.text = trip.title
            binding.tripDate.text = getDateStringFromCalendar(trip.date)
            binding.tripTimeDuration.text = tripTimeDuration
//            TODO: Add distance calculation
            binding.tripDistance.text = "0.0 miles"
        }

    }

    fun getDateStringFromCalendar(calendar: Calendar): String {
        val dateFormat = "MM/dd/yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US).format(calendar.time)
        return simpleDateFormat.format(calendar.time)
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
