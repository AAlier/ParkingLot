package neobis.alier.parking.ui.map_view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import neobis.alier.parking.R
import neobis.alier.parking.models.Coord
import neobis.alier.parking.models.Data
import neobis.alier.parking.ui.BaseActivity
import neobis.alier.parking.ui.detail_view.ViewDetailActivity
import neobis.alier.parking.utils.CommonUtils.isGooglePlayServicesAvailable

class MapViewActivity : BaseActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.places_view)
        init()
    }

    private fun init() {
        if (isGooglePlayServicesAvailable(this, true)) {
            initGoogleMap()
        }
    }

    private fun initGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.setOnMapClickListener { latLng ->
            Log.e("MAP______________", "${latLng?.latitude} ${latLng?.longitude}")
        }
        drawPolyLines()
    }

    private fun drawPolyLines() {
        val model = intent.getParcelableExtra<Data>("data")
        title = model.title

        if (mMap != null && model != null) {
            mMap!!.clear()
            val builder = LatLngBounds.Builder()
            val pol = initPolygonOptions(resources.getColor(R.color.accept))
            for (list in model.coordinates) {
                val latLng = LatLng(list.lat, list.lng)
                pol.add(latLng)
                builder.include(latLng)
            }
            mMap!!.addPolygon(pol).isClickable = true
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 700, 700, 5))

            mMap!!.setOnPolygonClickListener {
                val intent = Intent(this, ViewDetailActivity::class.java)
                intent.putExtra("data", model)
                startActivity(intent)
            }
        } else {
            // По умолчанию Ориентир Бишкек
            val startLatLng = LatLng(42.8746, 74.5698)
            val camPos = CameraPosition.Builder().target(startLatLng).zoom(13f).build()
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))

        }
    }

    private fun initPolygonOptions(strokeColor: Int): PolygonOptions {
        return PolygonOptions()
                .strokeColor(strokeColor)
                .fillColor(Color.argb(95, 50, 150, 0))
    }

    private fun populateMarkers() {
        val model = intent.getSerializableExtra("data") as? Data?

        if (mMap != null && model != null) {
            mMap!!.clear()
            model.coordinates.forEach(object : (Coord) -> Unit {
                override fun invoke(p1: Coord) {
                    mMap!!.addMarker(MarkerOptions().icon(
                            BitmapDescriptorFactory
                                    .fromResource(R.mipmap.marker))
                            .anchor(0.0f, 1.0f)
                            .position(LatLng(p1.lat, p1.lng)))
                }

            })
        }
    }
}