package com.keatonbrink.android.cyclestats

import android.content.Context
import androidx.room.Room
import com.keatonbrink.android.cyclestats.database.TripListDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "trip-list-database"

class TripRepository private constructor(context: Context, private val coroutineScope: CoroutineScope = GlobalScope) {

    private val database: TripListDatabase = Room.databaseBuilder(
        context.applicationContext,
        TripListDatabase::class.java,
        DATABASE_NAME
    ).build(
    )

    suspend fun  addTripDataWithPings(trip: TripDataWithPings) {
        val parentID = database.tripDao().addTrip(trip.tripData)
        trip.locationPings.forEach { ping ->
            ping.tripId = parentID
            database.tripLocationPingDao().addTripLocationPing(ping)
        }
    }

    fun deleteTripDataWithPings(trip: TripDataWithPings) {
        coroutineScope.launch {
            database.tripDao().deleteTrip(trip.tripData)
            trip.locationPings.forEach { ping ->
                database.tripLocationPingDao().deleteTripLocationPing(ping)
            }
        }
    }

    suspend fun addTripData(trip: TripData): Long {
        return database.tripDao().addTrip(trip)
    }

    suspend fun deleteTripData(trip: TripData) {
        database.tripDao().deleteTrip(trip)
    }

    fun getTrips(): Flow<List<TripDataWithPings>> {
        return database.tripDao().getTrips()
    }

    suspend fun getTrip(tripId: Long): TripDataWithPings {
        return database.tripDao().getTrip(tripId)
    }

    suspend fun getTripPings(tripId: Long): List<LocationPing> {
        return database.tripLocationPingDao().getTripPings(tripId)
    }

    suspend fun addLocationPing(locationPing: LocationPing) {
        database.tripLocationPingDao().addTripLocationPing(locationPing)
    }

    suspend fun deleteLocationPing(locationPing: LocationPing) {
        database.tripLocationPingDao().deleteTripLocationPing(locationPing)
    }


    companion object {
        private var INSTANCE: TripRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TripRepository(context)
            }
        }

        fun get(): TripRepository {
            return INSTANCE ?: throw IllegalStateException("TripRepository must be initialized")
        }
    }
}