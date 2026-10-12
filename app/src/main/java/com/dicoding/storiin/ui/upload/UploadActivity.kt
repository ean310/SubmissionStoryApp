package com.dicoding.storiin.ui.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.storiin.databinding.ActivityUploadBinding
import com.dicoding.storiin.user.Hasil
import com.dicoding.storiin.user.getImageUri
import com.dicoding.storiin.user.reduceFileImage
import com.dicoding.storiin.user.uriToFile
import com.dicoding.storiin.ui.ViewModelFactory
import com.dicoding.storiin.ui.main.MainActivity
import com.dicoding.storiin.ui.maps.MapsActivity
import com.dicoding.storiin.ui.sign.SigninActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var locationManager: LocationManager
    private var gpsStatus: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Disable the upload button initially
        binding.uploadButton.isEnabled = false

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, SigninActivity::class.java))
                finish()
            } else {
                binding.uploadButton.setOnClickListener {
                    getMyLastLocation()
                }
            }

            if (!allPermissionsGranted()) {
                requestCameraPermissionLauncher.launch(REQUIRED_PERMISSION)
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.cameraButton.setOnClickListener {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri!!)
        }

        binding.galleryButton.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
            enableUploadButton()  // Enable the upload button when an image is selected
        } else {
            Log.d("Photo Picker", "No media selected")
            showToast("No image selected from gallery.")
            disableUploadButton() // Disable the upload button if no image selected
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            showImage()
            enableUploadButton() // Enable the upload button after taking the picture
        } else {
            Log.d("Camera", "Photo not taken or canceled")
            showToast("Photo not taken.")
            disableUploadButton() // Disable the upload button if photo is not taken
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ImageView.setImageURI(it)
        }
    }

    private fun enableUploadButton() {
        binding.uploadButton.isEnabled = true  // Enable the upload button
    }

    private fun disableUploadButton() {
        binding.uploadButton.isEnabled = false  // Disable the upload button
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    // Handle permission denial
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        viewModel.getSession().observe(this) { user ->
            val shareLocation = binding.switch1
            if (shareLocation.isChecked) {
                if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            // Ensure an image is selected before uploading
                            if (currentImageUri != null) {
                                uploadStory(user.token, location.latitude.toString(), location.longitude.toString())
                            } else {
                                showToast("Please select an image to upload.")
                            }
                        } else {
                            val intents = Intent(this, MapsActivity::class.java)
                            startActivity(intents)

                            gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                            Toast.makeText(this, "Please turn on your GPS and set your location", Toast.LENGTH_SHORT).show()
                            if (!gpsStatus) {
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                startActivity(Intent(this, MapsActivity::class.java))
                            }
                        }
                    }
                } else {
                    requestPermissionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                }
            } else {
                // Ensure an image is selected before uploading
                if (currentImageUri != null) {
                    uploadStory(user.token)
                } else {
                    showToast("Please select an image to upload.")
                }
            }
        }
    }

    private fun uploadStory(token: String) {
        if (currentImageUri != null) {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                Log.d("Image File", "showImage: ${imageFile.path}")
                val description = binding.description.text.toString()

                viewModel.getUpload(token, imageFile, description).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Hasil.Loading -> {
                                // Show loading indicator if needed
                            }
                            is Hasil.Success -> {
                                result.data.message?.let { showToast(it) }
                                startActivity(Intent(this, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                                finish()
                            }
                            is Hasil.Error -> {
                                showToast(result.error)
                            }
                        }
                    }
                }
            }
        } else {
            showToast("Image not found. Please select an image to upload.")
        }
    }

    private fun uploadStory(token: String, lat: String, lon: String) {
        if (currentImageUri != null) {
            currentImageUri?.let { uri ->
                val file = uriToFile(uri, this).reduceFileImage()
                Log.d("Image File", "showImage: ${file.path}")
                val description = binding.description.text.toString()

                viewModel.getUpload(token, file, description, lat, lon).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Hasil.Loading -> {
                                // Show loading indicator if needed
                            }
                            is Hasil.Success -> {
                                result.data.message?.let { showToast(it) }
                                startActivity(Intent(this, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                                finish()
                            }
                            is Hasil.Error -> {
                                showToast(result.error)
                            }
                        }
                    }
                }
            }
        } else {
            showToast("Image not found. Please select an image to upload.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
