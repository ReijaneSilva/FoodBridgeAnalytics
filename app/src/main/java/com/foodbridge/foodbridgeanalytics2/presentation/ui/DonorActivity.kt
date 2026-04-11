package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.foodbridge.foodbridgeanalytics2.R
import com.foodbridge.foodbridgeanalytics2.SelectionActivity
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class DonorActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nova Doação"

        val editAlimento = findViewById<EditText>(R.id.editFoodName)
        val editQtd = findViewById<EditText>(R.id.editQuantity)
        val botaoEnviar = findViewById<Button>(R.id.btnSubmit)

        botaoEnviar.setOnClickListener {
            val nomeAlimento = editAlimento.text.toString().trim()
            val quantidade = editQtd.text.toString().trim()

            if (nomeAlimento.isNotEmpty() && quantidade.isNotEmpty()) {
                botaoEnviar.isEnabled = false
                capturarLocalizacaoESalvar(nomeAlimento, quantidade, botaoEnviar)
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun capturarLocalizacaoESalvar(nome: String, qtd: String, botao: Button) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            // Salva sem localização se permissão negada
            salvarNoFirebase(nome, qtd, null, null, botao)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                salvarNoFirebase(nome, qtd, location?.latitude, location?.longitude, botao)
            }
            .addOnFailureListener {
                salvarNoFirebase(nome, qtd, null, null, botao)
            }
    }

    private fun salvarNoFirebase(
        nome: String,
        qtd: String,
        lat: Double?,
        lng: Double?,
        botao: Button
    ) {
        val idUnico = UUID.randomUUID().toString()

        val doacao = hashMapOf(
            "id" to idUnico,
            "alimento" to nome,
            "quantidade" to qtd,
            "status" to "Disponível",
            "data" to System.currentTimeMillis(),
            "latitude" to lat,
            "longitude" to lng
        )

        db.collection("doacoes")
            .document(idUnico)
            .set(doacao)
            .addOnSuccessListener {
                Toast.makeText(this, "Doação publicada com sucesso! ✅", Toast.LENGTH_LONG).show()
                android.os.Handler(mainLooper).postDelayed({
                    startActivity(Intent(this, SelectionActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    finish()
                }, 1500)
            }
            .addOnFailureListener { erro ->
                botao.isEnabled = true
                Toast.makeText(this, "Erro ao salvar: ${erro.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish(); return true
        }
        return super.onOptionsItemSelected(item)
    }
}
