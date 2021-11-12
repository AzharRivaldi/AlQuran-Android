package com.azhar.alquran.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.azhar.alquran.R
import com.azhar.alquran.adapter.MainAdapter
import com.azhar.alquran.fragment.FragmentJadwalSholat.Companion.newInstance
import com.azhar.alquran.model.main.ModelSurah
import com.azhar.alquran.viewmodel.SurahViewModel
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    var REQ_PERMISSION = 100
    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0
    lateinit var strCurrentLatLong: String
    lateinit var strDate: String
    lateinit var strDateNow: String
    lateinit var simpleLocation: SimpleLocation
    lateinit var mainAdapter: MainAdapter
    lateinit var progressDialog: ProgressDialog
    lateinit var surahViewModel: SurahViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setInitLayout()
        setPermission()
        setLocation()
        setCurrentLocation()
        setViewModel()
    }

    private fun setInitLayout() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Sedang menampilkan data...")

        val dateNow = Calendar.getInstance().time
        strDate = DateFormat.format("EEEE", dateNow) as String
        strDateNow = DateFormat.format("d MMMM yyyy", dateNow) as String

        tvToday.setText("$strDate,")
        tvDate.setText(strDateNow)

        mainAdapter = MainAdapter(this)
        rvSurah.setHasFixedSize(true)
        rvSurah.setLayoutManager(LinearLayoutManager(this))
        rvSurah.setAdapter(mainAdapter)

        val jadwalSholat = newInstance("Jadwal Sholat")
        layoutTime.setOnClickListener {
            jadwalSholat.show(
                supportFragmentManager, jadwalSholat.tag
            )
        }

        layoutMosque.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    MasjidActivity::class.java
                )
            )
        }
    }

    private fun setLocation() {
        simpleLocation = SimpleLocation(this)
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        //get location
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude

        //set location lat long
        strCurrentLatLong = "$strCurrentLatitude,$strCurrentLongitude"
    }

    private fun setCurrentLocation() {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
            if (addressList != null && addressList.size > 0) {
                val strCurrentLocation = addressList[0].locality
                tvLocation.text = strCurrentLocation
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setViewModel() {
        progressDialog.show()
        surahViewModel = ViewModelProvider(this, NewInstanceFactory()).get(SurahViewModel::class.java)
        surahViewModel.setSurah()
        surahViewModel.getSurah()
            .observe(this, { modelSurah: ArrayList<ModelSurah> ->
                if (modelSurah.size != 0) {
                    mainAdapter.setAdapter(modelSurah)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(this, "Data Tidak Ditemukan!", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
                progressDialog.dismiss()
            })
    }

    private fun setPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQ_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_PERMISSION && resultCode == RESULT_OK) {

            //load data
            setViewModel()
        }
    }
}