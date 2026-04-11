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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class DonorActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nova Doação"

        val editAlimento = findViewById<EditText>(R.id.editFoodName)
        val editQtd = findViewById<EditText>(R.id.editQuantity)
        val editEndereco = findViewById<EditText>(R.id.editEndereco)
        val editTelefone = findViewById<EditText>(R.id.editTelefone)
        val editObs = findViewById<EditText>(R.id.editObservacoes)
        val botaoEnviar = findViewById<Button>(R.id.btnSubmit)

        botaoEnviar.setOnClickListener {
            val alimento = editAlimento.text.toString().trim()
            val quantidade = editQtd.text.toString().trim()
            val endereco = editEndereco.text.toString().trim()
            val telefone = editTelefone.text.toString().trim()
            val obs = editObs.text.toString().trim()

            if (alimento.isEmpty() || quantidade.isEmpty() || endereco.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            botaoEnviar.isEnabled = false
            capturarLocalizacaoESalvar(alimento, quantidade, endereco, telefone, obs, botaoEnviar)
        }
    }

    private fun capturarLocalizacaoESalvar(
        alimento: String, quantidade: String, endereco: String,
        telefone: String, obs: String, botao: Button
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            buscarNomeDoadorESalvar(alimento, quantidade, endereco, telefone, obs, null, null, botao)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                buscarNomeDoadorESalvar(alimento, quantidade, endereco, telefone, obs,
                    location?.latitude, location?.longitude, botao)
            }
            .addOnFailureListener {
                buscarNomeDoadorESalvar(alimento, quantidade, endereco, telefone, obs, null, null, botao)
            }
    }

    private fun buscarNomeDoadorESalvar(
        alimento: String, quantidade: String, endereco: String,
        telefone: String, obs: String, lat: Double?, lng: Double?, botao: Button
    ) {
        val uid = auth.currentUser?.uid ?: run {
            salvarNoFirebase(alimento, quantidade, endereco, telefone, obs, "Doador", lat, lng, botao)
            return
        }

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val nomeDoador = doc.getString("nome") ?: "Doador"
                salvarNoFirebase(alimento, quantidade, endereco, telefone, obs, nomeDoador, lat, lng, botao)
            }
            .addOnFailureListener {
                salvarNoFirebase(alimento, quantidade, endereco, telefone, obs, "Doador", lat, lng, botao)
            }
    }

    private fun salvarNoFirebase(
        alimento: String, quantidade: String, endereco: String,
        telefone: String, obs: String, nomeDoador: String,
        lat: Double?, lng: Double?, botao: Button
    ) {
        val idUnico = UUID.randomUUID().toString()

        val doacao = hashMapOf(
            "id" to idUnico,
            "alimento" to alimento,
            "quantidade" to quantidade,
            "quantidadeDisponivel" to quantidade,
            "enderecocoleta" to endereco,
            "telefoneDoador" to telefone,
            "observacoes" to obs,
            "nomeDoador" to nomeDoador,
            "uidDoador" to (auth.currentUser?.uid ?: ""),
            "status" to "Disponível",
            "data" to System.currentTimeMillis(),
            "latitude" to lat,
            "longitude" to lng
        )

        db.collection("doacoes").document(idUnico).set(doacao)
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
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}