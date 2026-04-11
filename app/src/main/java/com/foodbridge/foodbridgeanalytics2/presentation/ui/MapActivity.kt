package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mapa de Doações"

        findViewById<Button>(R.id.btnVoltarMapa).setOnClickListener {
            finish()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        db.collection("doacoes").get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val lat = doc.getDouble("latitude") ?: continue
                    val lng = doc.getDouble("longitude") ?: continue
                    val alimento = doc.getString("alimento") ?: "Doação"
                    val quantidade = doc.getString("quantidade") ?: ""

                    val posicao = LatLng(lat, lng)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(posicao)
                            .title(alimento)
                            .snippet("Qtd: $quantidade")
                    )
                }
            }

        val brasil = LatLng(-15.0, -47.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brasil, 4f))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}