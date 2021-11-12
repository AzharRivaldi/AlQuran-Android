package com.azhar.alquran.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.azhar.alquran.R
import com.azhar.alquran.model.nearby.ModelResults
import com.azhar.alquran.viewmodel.MasjidViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_masjid.*
import java.util.*

class MasjidActivity : AppCompatActivity(), OnMapReadyCallback {

    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0
    lateinit var strCurrentLocation: String
    lateinit var mapsView: GoogleMap
    lateinit var simpleLocation: SimpleLocation
    lateinit var progressDialog: ProgressDialog
    lateinit var masjidViewModel: MasjidViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masjid)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu…")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("sedang menampilkan lokasi")

        setInitLayout()
    }

    private fun setInitLayout() {
        toolbar.setTitle(null)
        setSupportActionBar(toolbar)
        assert(supportActionBar != null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        simpleLocation = SimpleLocation(this)
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        //get location
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude

        //set location lat long
        strCurrentLocation = "$strCurrentLatitude,$strCurrentLongitude"

        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapsView = googleMap

        //viewmodel
        setViewModel()
    }

    private fun setViewModel() {
        progressDialog.show()
        masjidViewModel = ViewModelProvider(this, NewInstanceFactory()).get(MasjidViewModel::class.java)
        masjidViewModel.setMarkerLocation(strCurrentLocation)
        masjidViewModel.getMarkerLocation()
            .observe(this, { modelResults: ArrayList<ModelResults> ->
                if (modelResults.size != 0) {

                    //get multiple marker
                    getMarker(modelResults)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(this, "Oops, tidak bisa mendapatkan lokasi kamu!", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
                progressDialog.dismiss()
            })
    }

    private fun getMarker(modelResultsArrayList: ArrayList<ModelResults>) {
        for (i in modelResultsArrayList.indices) {

            //set LatLong from API
            val latLngMarker = LatLng(
                modelResultsArrayList[i]
                    .modelGeometry
                    .modelLocation
                    .lat, modelResultsArrayList[i]
                    .modelGeometry
                    .modelLocation
                    .lng
            )

            //get LatLong to Marker
            mapsView.addMarker(
                MarkerOptions()
                    .position(latLngMarker)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(modelResultsArrayList[i].name)
            )

            //show Marker
            val latLngResult = LatLng(
                modelResultsArrayList[0].modelGeometry
                    .modelLocation
                    .lat, modelResultsArrayList[0]
                    .modelGeometry
                    .modelLocation
                    .lng
            )

            //set position marker
            mapsView.moveCamera(CameraUpdateFactory.newLatLng(latLngResult))
            mapsView.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        latLngResult.latitude,
                        latLngResult.longitude
                    ), 14f
                )
            )
            mapsView.uiSettings.setAllGesturesEnabled(true)
            mapsView.uiSettings.isZoomGesturesEnabled = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}