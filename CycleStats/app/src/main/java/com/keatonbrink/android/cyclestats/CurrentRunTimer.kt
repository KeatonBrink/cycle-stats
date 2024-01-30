package com.keatonbrink.android.cyclestats

import androidx.annotation.StringRes

data class CurrentRunTimer(@StringRes var statusTextID: Int, @StringRes var startTimeID: Int, var isCycling: Boolean)