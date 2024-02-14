package com.keatonbrink.android.cyclestats.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.keatonbrink.android.cyclestats.LocationPing
import com.keatonbrink.android.cyclestats.TripData
import com.keatonbrink.android.cyclestats.TripDataWithPings
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TripDao {

    @Insert
    suspend fun insertTrip(trip: TripData): Long

    @Query("SELECT * FROM trip_data")
    fun getTrips(): Flow<List<TripDataWithPings>>

    @Query("SELECT * FROM trip_data WHERE id=(:tripId)")
    suspend fun getTrip(tripId: Long): TripDataWithPings
}

@Dao
interface TripLocationPingDao {

    @Insert
    suspend fun insertTripLocationPing(tripPing: LocationPing)

    @Query("SELECT * FROM location_pings WHERE tripId=(:tripId)")
    suspend fun getTripPings(tripId: Long): List<LocationPing>
}