package com.keatonbrink.android.cyclestats

import java.util.Calendar
import java.util.UUID

data class TripData(
    var id: UUID,
    var title: String,
    var date: Calendar,
    var startTime: Long,
    var pings: MutableList<LocationPings>,
)
