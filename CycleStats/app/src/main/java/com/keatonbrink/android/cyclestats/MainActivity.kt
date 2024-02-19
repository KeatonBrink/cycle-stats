package com.keatonbrink.android.cyclestats

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.keatonbrink.android.cyclestats.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding

//    private val trips: MutableList<TripDataWithPings> = mutableListOf()
//    fun addTrip(trip: TripDataWithPings) {
//        trips.add(trip)
//    }
//    fun getTrips(): MutableList<TripDataWithPings> {
//        return trips
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    POST_NOTIFICATIONS
                ),
                200
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }

    // Gets called from TripListFragment.kt when a new trip is selected (in focus)
    fun addTripPingsToMapAsPolyLines(trip: TripDataWithPings) {
        val pings = trip.getPingsInOrder()
        val polyLineOptions = PolylineOptions()
        for (ping in pings) {
            val latLng = LatLng(ping.latitude, ping.longitude)
            polyLineOptions.add(latLng)
        }
        mMap.addPolyline(polyLineOptions)
        // Center map at first ping
        val firstPing = pings.first()
        val firstPingLatLng = LatLng(firstPing.latitude, firstPing.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(firstPingLatLng))
    }

    // Gets called from TripListFragment.kt when the user drags the recycler view
    fun clearMap() {
        mMap.clear()
    }

}