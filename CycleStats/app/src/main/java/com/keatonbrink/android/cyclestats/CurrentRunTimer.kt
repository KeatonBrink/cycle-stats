package com.keatonbrink.android.cyclestats

import androidx.annotation.StringRes

data class CurrentRunTimer(@StringRes var statusTextID: Int, var startTimeString: String, var startTimeSeconds: Long, var isCycling: Boolean)