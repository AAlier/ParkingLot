package neobis.alier.parking.ui.service

/**
 * Created by Alier on 28.03.2018.
 */

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import neobis.alier.parking.utils.Shares

class LocationUpdateService : Service(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val LOGSERVICE = "LocationSender"

    override fun onCreate() {
        super.onCreate()

        buildGoogleApiClient()
        initGoogleApi()
        Log.i(LOGSERVICE, "onCreate")
    }

    private fun initGoogleApi() {
        if (mGoogleApiClient != null && !mGoogleApiClient!!.isConnected) {
            Log.i(LOGSERVICE, "Connecting")
            mGoogleApiClient!!.connect()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        Log.i(LOGSERVICE, "onConnected")

        val l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        if (l != null) {
            Log.i(LOGSERVICE, "last: lat " + l.latitude + ", lng " + l.longitude)
        }
        startLocationUpdate()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.i(LOGSERVICE, "onConnectionSuspended " + i)
    }

    override fun onLocationChanged(location: Location) {
        Log.i(LOGSERVICE, "lat: " + location.latitude + ", lng: " + location.longitude)
        Shares.setUserCoords(applicationContext, location)
        startService(Intent(baseContext, WaitingService::class.java))
    }

    override fun onDestroy() {
        if (mGoogleApiClient != null)
            stopLocationUpdate()
        Log.i(LOGSERVICE, "onDestroy ")
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(LOGSERVICE, "onConnectionFailed ")
    }

    @SuppressLint("RestrictedApi")
    private fun initLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 60000
        mLocationRequest!!.fastestInterval = 20000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return
        }
        initLocationRequest()
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    private fun stopLocationUpdate() {
        if (mGoogleApiClient!!.isConnected)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build()
    }
}