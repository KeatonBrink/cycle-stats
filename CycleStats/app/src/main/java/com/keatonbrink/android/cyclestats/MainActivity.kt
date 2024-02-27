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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.keatonbrink.android.cyclestats.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding

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
        clearMap()

        val pings = trip.getPingsInOrder()
        val polyLineOptions = PolylineOptions().color(R.integer.poly_line_color).width(25f)

        // Used for centering the map on the poly line
        val builder = LatLngBounds.Builder()

        for (ping in pings) {
            val latLng = LatLng(ping.latitude, ping.longitude)
            polyLineOptions.add(latLng)
            builder.include(LatLng(ping.latitude, ping.longitude))
        }
        mMap.addPolyline(polyLineOptions)

        val bounds = builder.build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    // Gets called from TripListFragment.kt when the user drags the recycler view
    fun clearMap() {
        mMap.clear()
    }

}