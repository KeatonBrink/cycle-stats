package com.keatonbrink.android.cyclestats

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.keatonbrink.android.cyclestats.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: TripRepository

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = TripRepository.get()

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

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up the on click listener for items in navigation drawer
        val navigationView = binding.navigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_summary -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val trips = repository.getTripList()
                        val gson = Gson()
                        val tripsJson = gson.toJson(trips)
                        val fragment = SummaryDetailsFragment.newInstance(tripsJson)
                        withContext(Dispatchers.Main) {
                            // Replace the current trip fragment
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.current_trip_fragment_container, fragment)
                                .commit()
                        }
                        drawerLayout.closeDrawers()
                    }
                }
                R.id.nav_trips -> {
                    // Replace the trip list fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.current_trip_fragment_container, CurrentAndAllTripsParentFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                }
            }
            // Close the drawer
            drawerLayout.closeDrawers()
            true
        }

    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        // Start a coroutine on the IO dispatcher
        CoroutineScope(Dispatchers.IO).launch {
            val mostRecentTrip = repository.getTripList().firstOrNull()
            withContext(Dispatchers.Main) {
                if (mostRecentTrip != null) {
                    addTripPingsToMapAsPolyLines(mostRecentTrip)
                } else {
                    // Add a marker in Sydney and move the camera
                    val sydney = LatLng(-34.0, 151.0)
                    mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                }
            }
        }

    }

    fun addAllTripPingsToMapAsPolylines() {
        clearMap()
        // Polylines should have random colors
        CoroutineScope(Dispatchers.IO).launch {

            val trips = repository.getTripList()
            // Generate pings first, then launch withContext(Dispatchers.Main) to add them to the map
            for (trip in trips) {
                val pings = trip.getPingsInOrder()
                val polyLineOptions = PolylineOptions().color(R.integer.poly_line_color).width(25f)

                // Used for centering the map on the poly line
                val builder = LatLngBounds.Builder()

                for (ping in pings) {
                    val latLng = LatLng(ping.latitude, ping.longitude)
                    polyLineOptions.add(latLng)
                    builder.include(LatLng(ping.latitude, ping.longitude))
                }
                withContext(Dispatchers.Main) {
                    mMap.addPolyline(polyLineOptions)
                    val bounds = builder.build()
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }
        }
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