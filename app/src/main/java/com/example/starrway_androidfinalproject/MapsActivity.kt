package com.example.starrway_androidfinalproject

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var alertBuilder: AlertDialog.Builder
    private lateinit var pins: List<Pin>
    lateinit var sharedPrefHandler:SharedPrefHandler
    private var locationUpdateState = false
    private val dbHandler:DbasHandler=DbasHandler(this)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val AUTOCOMPLETE_REQUEST_CODE  = 3
        var activePin: Pin =
            Pin()
    }

    // check for permissions and start loading map
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                pins = dbHandler.viewAll()
                if(pins.isNotEmpty()) {
                    placeMarkerOnMap(pins)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 100000
        locationRequest.fastestInterval = 50000
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().pl
                    e.startResolutionForResult(this@MapsActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("test", "${place.name}")

                        // this convolution was not my creation, IDE suggestion
                        place.latLng?.let { it1 -> placeMarkerOnMap(it1, BitmapDescriptorFactory.HUE_RED) }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Toast.makeText(this,
                            "Error completing Auto-complete activity, please try again later",
                            Toast.LENGTH_LONG).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation, so do nothing
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    //
    // This is used when the user is finding a place to add
    private fun placeMarkerOnMap(location: LatLng, colour: Float){
        val markerOptions = MarkerOptions().position(location)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(colour))
        map.addMarker(markerOptions)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))

        activePin.latLng = location
    }

    //
    // This is only used when loading the pins from the database
    private fun placeMarkerOnMap(pins:List<Pin>){
        for (pin in pins){
            val lastModified = sharedPrefHandler.getValueLong(this?.getString(R.string.last_modified_key))
            val markerOptions = MarkerOptions().position(pin.latLng)
            markerOptions.title(pin.pk.toString() + ": " + pin.title)

            if(pin.pk.toLong() != lastModified){
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            }
            else{
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(pin.latLng, 12f))
            }

            map.addMarker(markerOptions)
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        // permission check was required, find better solution later
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng,BitmapDescriptorFactory.HUE_RED)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        setTitle("The GeoGallery")
        // Initialize the SDK
        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        sharedPrefHandler = SharedPrefHandler(this)

        // Create a new PlacesClient instance
        Places.createClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val fabSearch = findViewById<FloatingActionButton>(R.id.fabSearch)
        fabSearch.setOnClickListener {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent,
                AUTOCOMPLETE_REQUEST_CODE
            )
        }
        fabPinColours.setOnClickListener {
            val dialogPinColours=LayoutInflater.from(this).inflate(R.layout.pin_colours, null)
            val builderPinColours=AlertDialog.Builder(this).setView(dialogPinColours)
            val alertDialogPinColours=builderPinColours.show()
        }

        val fabAddPin = findViewById<FloatingActionButton>(R.id.fabAddPin)
        fabAddPin.setOnClickListener{
            if(activePin.latLng != LatLng(0.0,0.0)) {
                alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle("Add Pin?")
                alertBuilder.setPositiveButton("Yes", { dialog, which -> addPin(which) })
                alertBuilder.setNegativeButton("No", { dialog, which -> addPin(which) })
                alertBuilder.setMessage("Would you like to add this location to your records?")

                alertBuilder.show()
            }
            else {
                Toast.makeText(this, "ERROR: A pin must exist before you add it", Toast.LENGTH_LONG).show()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                if (p0 != null) {
                    lastLocation = p0.lastLocation
                }
            }
        }
        createLocationRequest()
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)
        map.setOnMyLocationButtonClickListener(this)
        setUpMap()
    }

    private fun addPin(choice : Int){
        // add a pin
        if(choice == -1){
            val intent = Intent(this, AddPinActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMarkerClick(p0: Marker?) : Boolean{
        if(p0?.title != null) {
            alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle("Edit Pin: " + p0?.title)
            alertBuilder.setPositiveButton("Yes", { dialog, which -> editPin(which, p0) })
            alertBuilder.setNegativeButton("No", { dialog, which -> editPin(which, p0) })
            alertBuilder.setMessage("Would you like to make changes to this pin?")
            alertBuilder.show()
        }
        else{
            Toast.makeText(this, "ERROR: you can't modify a pin that hasn't yet been added", Toast.LENGTH_LONG)
                .show()
        }
        return false
    }

    private fun editPin(choice: Int, p0:Marker?){
        if(choice == -1){
            // start activity
            var pinID = p0?.title?.substring(0, p0?.title.indexOf(':'))

            if (pinID != null) {
                activePin = dbHandler.viewSingle(pinID.toInt())

                val intent = Intent(this, AddPinActivity::class.java)
                startActivity(intent)

                Toast.makeText(this,"Now editting pin number " + activePin.pk, Toast.LENGTH_LONG)
                    .show()
            }
            else{
                // not found, don't start activity
                Toast.makeText(this, "DEBUG: ERROR COULDN'T FIND PIN", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}