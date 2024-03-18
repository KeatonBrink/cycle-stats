package com.keatonbrink.android.cyclestats

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getTimeOfDayFromEpochSeconds(timeInSeconds: Long): String {
    val date = Date(timeInSeconds * 1000L) // Convert seconds to milliseconds
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(date)
}

fun getTimeDurationFromPingsAsString(pings: List<LocationPing>): String {
    val duration = getTimeDurationFromPingsAsSeconds(pings)
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

fun getTimeDurationFromPingsAsSeconds(pings: List<LocationPing>): Long {
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
    return maxTime - minTime
}