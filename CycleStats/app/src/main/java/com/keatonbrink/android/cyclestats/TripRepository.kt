package com.keatonbrink.android.cyclestats

import android.content.Context
import androidx.room.Room
import com.keatonbrink.android.cyclestats.database.TripListDatabase
import kotlinx.coroutines.flow.Flow
import java.util.UUID

private const val DATABASE_NAME = "trip-list-database"

class TripRepository private constructor(context: Context) {

    private val database: TripListDatabase = Room.databaseBuilder(
        context.applicationContext,
        TripListDatabase::class.java,
        DATABASE_NAME
    ).build(
    )

    suspend fun addTrip(trip: TripData): Long {
        return database.tripDao().insertTrip(trip)
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
        database.tripLocationPingDao().insertTripLocationPing(locationPing)
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