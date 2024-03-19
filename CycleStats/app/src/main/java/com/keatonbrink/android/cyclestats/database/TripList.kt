package com.keatonbrink.android.cyclestats.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.keatonbrink.android.cyclestats.LocationPing
import com.keatonbrink.android.cyclestats.TripData
import com.keatonbrink.android.cyclestats.TripDataWithPings
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TripDao {

    @Insert
    suspend fun addTrip(trip: TripData): Long

    @Delete
    suspend fun deleteTrip(trip: TripData)

    // Sort also by start time
    @Query("SELECT * FROM trip_data ORDER BY date DESC, startTime DESC")
    fun getTrips(): Flow<List<TripDataWithPings>>

    @Query("SELECT * FROM trip_data ORDER BY date DESC, startTime DESC")
    fun getTripList(): List<TripDataWithPings>

    @Query("SELECT * FROM trip_data WHERE id=(:tripId)")
    suspend fun getTrip(tripId: Long): TripDataWithPings

    // Update the title of the trip
    @Update
    suspend fun updateTrip(trip: TripData)
}

@Dao
interface TripLocationPingDao {

    @Insert
    suspend fun addTripLocationPing(tripPing: LocationPing)

    @Delete
    suspend fun deleteTripLocationPing(tripPing: LocationPing)

    @Query("SELECT * FROM location_pings WHERE tripId=(:tripId)")
    suspend fun getTripPings(tripId: Long): List<LocationPing>
}