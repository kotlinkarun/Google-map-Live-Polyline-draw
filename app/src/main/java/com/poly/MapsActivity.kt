package com.poly

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

//Curent address..........
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {


    private lateinit var mMap: GoogleMap
    private lateinit var instance: GetLocation
    var ischeck = false

    private  var arrayPoints: MutableList<LatLng> = mutableListOf()
    private lateinit var polylineOptions: PolylineOptions


    /* recevie Broadcast lattude and longtude*/
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent != null && !ischeck) {
                val lat = intent.getDoubleExtra("lat", 0.0)
                val lng = intent.getDoubleExtra("lng", 0.0)
                val address = intent.getStringExtra("add")
                val sydney = LatLng(lat, lng)

                mapMaker(address, sydney, R.drawable.pick_location)

                val marker = MarkerOptions()
                marker.position(sydney);
                mMap.addMarker(marker);
                polylineOptions = PolylineOptions()
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(7f);
                arrayPoints.add(sydney);
                polylineOptions.addAll(arrayPoints);
                mMap.addPolyline(polylineOptions);
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        initBroadCastMap()
        instance.startLocation()


    }

    private fun initBroadCastMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        instance = GetLocation(this)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("key_action"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            GetLocation.REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> Log.e("TAG", "User agreed to make required location settings changes.")
                Activity.RESULT_CANCELED -> {
                    Log.e("TAG", "User chose not to make required location settings changes.")
                    instance.mRequestingLocationUpdates = false
                }
            }// Nothing to do. startLocationupdates() gets called in onResume again.
        }
    }


    public override fun onResume() {
        super.onResume()
        if (instance.mRequestingLocationUpdates!! && instance.checkPermissions()) {
            instance.startLocationUpdates()
        }
        instance.updateLocationUI()
    }

    override fun onPause() {
        super.onPause()
        if (instance.mRequestingLocationUpdates!!) {
            instance.stopLocationUpdates()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.mMap = googleMap;
        val style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        this.mMap.setMapStyle(style);


      //  mMap.isMyLocationEnabled = true
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

    }


    /* Click press map view Start polyline draw*/
    override fun onMapClick(point: LatLng?) {

      /*  ischeck=true
        val marker = MarkerOptions()
        marker.position(point!!);
        mMap.addMarker(marker);
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(5f);
        arrayPoints.add(point);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);*/

    }

    /* Click press map view Stop polyline draw*/
    override fun onMapLongClick(p0: LatLng?) {
        ischeck=false
        mMap.clear();
        arrayPoints.clear();
    }

    /* Curenter location*/
    private fun mapMaker(address: String, latLng: LatLng, customMarker: Int) {
        mMap.clear()
        mMap.addMarker(MarkerOptions()
                .position(latLng).title(address).snippet(address)
                .icon(BitmapDescriptorFactory.fromResource(customMarker)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))


    }
}
