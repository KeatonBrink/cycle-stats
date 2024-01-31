package com.keatonbrink.android.cyclestats

import android.location.Location
import java.util.Calendar

data class TripData(
    var title: String,
    var date: Calendar,
    var startTime: Long,
    var pings: MutableList<Location>,
)
