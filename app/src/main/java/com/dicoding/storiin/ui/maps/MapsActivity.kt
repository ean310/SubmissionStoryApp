package com.dicoding.storiin.ui.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.storiin.R
import com.dicoding.storiin.databinding.ActivityMapsBinding
import com.dicoding.storiin.response.Story
import com.dicoding.storiin.ui.ViewModelFactory
import com.dicoding.storiin.ui.welcome.WelcomeActivity
import com.dicoding.storiin.user.Hasil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            enableUserLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.hide()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        enableUserLocation()
        applyMapStyle()
        loadMarkers()
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun applyMapStyle() {
        try {
            val isStyleLoaded = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!isStyleLoaded) {
                showToast("Style parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            showToast("Error loading map style: $e")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun loadMarkers() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateToWelcome()
            } else {
                viewModel.getLoc(user.token)
                observeLocationData()
            }
        }
    }

    private fun observeLocationData() {
        viewModel.location.observe(this) { locationResult ->
            when (locationResult) {
                is Hasil.Success -> {
                    addStoryMarkers(locationResult.data.listStory)
                }
                else -> {
                }
            }
        }
    }

    private fun addStoryMarkers(stories: List<Story>) {
        stories.forEach { story ->
            val position = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(story.name)
                    .snippet(story.description)
            )
            boundsBuilder.include(position)
        }

        val bounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}
