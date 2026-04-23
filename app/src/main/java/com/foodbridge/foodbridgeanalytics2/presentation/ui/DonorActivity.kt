package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
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
        supportActionBar?.title = "Nova Doacao"

        val editAlimento = findViewById<EditText>(R.id.editFoodName)
        val editQtd = findViewById<EditText>(R.id.editQuantity)
        val editEndereco = findViewById<EditText>(R.id.editEndereco)
        val editTelefone = findViewById<EditText>(R.id.editTelefone)
        val editObs = findViewById<EditText>(R.id.editObservacoes)
        val botaoEnviar = findViewById<Button>(R.id.btnSubmit)
        findViewById<Button>(R.id.btnVoltarDoador).setOnClickListener {
            finish()
        }
        val listaDoacoes = findViewById<LinearLayout>(R.id.layoutMinhasDoacoes)

        botaoEnviar.setOnClickListener {
            val alimento = editAlimento.text.toString().trim()
            val quantidade = editQtd.text.toString().trim()
            val endereco = editEndereco.text.toString().trim()
            val telefone = editTelefone.text.toString().trim()
            val obs = editObs.text.toString().trim()


            if (alimento.isEmpty() || quantidade.isEmpty() || endereco.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatorios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            botaoEnviar.isEnabled = false
            capturarLocalizacaoESalvar(alimento, quantidade, endereco, telefone, obs, botaoEnviar)
        }

        carregarMinhasDoacoes(listaDoacoes)
    }

    private fun carregarMinhasDoacoes(container: LinearLayout) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("doacoes")
            .whereEqualTo("uidDoador", uid)
            .addSnapshotListener { snapshots, _ ->
                container.removeAllViews()

                val doacoes = snapshots?.documents ?: return@addSnapshotListener
                if (doacoes.isEmpty()) return@addSnapshotListener

                val titulo = TextView(this).apply {
                    text = "Minhas Doacoes"
                    textSize = 16f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setTextColor(android.graphics.Color.parseColor("#2E7D32"))
                    setPadding(0, 24, 0, 8)
                }
                container.addView(titulo)

                for (doc in doacoes) {
                    val alimento = doc.getString("alimento") ?: continue
                    val quantidade = doc.getString("quantidade") ?: ""
                    val status = doc.getString("status") ?: "Disponivel"
                    val reservadoPor = doc.getString("reservadoPor") ?: ""
                    val docId = doc.id

                    val card = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(24, 16, 24, 16)
                        setBackgroundColor(android.graphics.Color.WHITE)
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 8, 0, 8)
                        layoutParams = params
                    }

                    val tvAlimento = TextView(this).apply {
                        text = "$alimento - $quantidade"
                        textSize = 15f
                        setTypeface(null, android.graphics.Typeface.BOLD)
                    }

                    val tvStatus = TextView(this).apply {
                        text = when (status) {
                            "Reservado" -> "Reservado por: $reservadoPor"
                            "Coletado" -> "Coletado"
                            else -> "Disponivel"
                        }
                        textSize = 13f
                        setTextColor(when (status) {
                            "Reservado" -> android.graphics.Color.parseColor("#E65100")
                            "Coletado" -> android.graphics.Color.parseColor("#9E9E9E")
                            else -> android.graphics.Color.parseColor("#2E7D32")
                        })
                    }

                    card.addView(tvAlimento)
                    card.addView(tvStatus)

                    if (status == "Reservado") {
                        val btnColetar = Button(this).apply {
                            text = "CONFIRMAR COLETA"
                            setBackgroundColor(android.graphics.Color.parseColor("#388E3C"))
                            setTextColor(android.graphics.Color.WHITE)
                            val params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            params.topMargin = 8
                            layoutParams = params
                        }
                        btnColetar.setOnClickListener {
                            db.collection("doacoes").document(docId)
                                .update("status", "Coletado")
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Coleta confirmada com sucesso!", Toast.LENGTH_SHORT).show()
                                }
                        }
                        card.addView(btnColetar)
                    }

                    container.addView(card)
                }
            }
    }

    private fun capturarLocalizacaoESalvar(
        alimento: String, quantidade: String, endereco: String,
        telefone: String, obs: String, botao: Button
    ) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            buscarNomeDoadorESalvar(alimento, quantidade, endereco, telefone, obs, null, null, botao)
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = com.google.android.gms.location.CurrentLocationRequest.Builder()
            .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        fusedLocationClient.getCurrentLocation(locationRequest, null)
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
            "enderecoColeta" to endereco,
            "telefoneDoador" to telefone,
            "observacoes" to obs,
            "nomeDoador" to nomeDoador,
            "uidDoador" to (auth.currentUser?.uid ?: ""),
            "status" to "Disponivel",
            "data" to System.currentTimeMillis(),
            "latitude" to lat,
            "longitude" to lng
        )

        db.collection("doacoes").document(idUnico).set(doacao)
            .addOnSuccessListener {
                Toast.makeText(this, "Doacao publicada com sucesso!", Toast.LENGTH_LONG).show()
                val handler = android.os.Handler(mainLooper)
                val runnable = Runnable {
                    if (!isFinishing && !isDestroyed) {
                        startActivity(Intent(this, SelectionActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        })
                        finish()
                    }
                }
                handler.postDelayed(runnable, 1500)
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