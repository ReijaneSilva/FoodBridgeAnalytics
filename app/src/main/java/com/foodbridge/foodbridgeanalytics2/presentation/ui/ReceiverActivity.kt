package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodbridge.foodbridgeanalytics2.R
import com.foodbridge.foodbridgeanalytics2.data.local.AppDatabase
import com.foodbridge.foodbridgeanalytics2.data.models.DoacaoEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ReceiverActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)

        val recycler = findViewById<RecyclerView>(R.id.recyclerDonations)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarReceiver)
        val tvErro = findViewById<TextView>(R.id.tvErroReceiver)

        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnVoltarReceiver).setOnClickListener { finish() }

        val doacaoDao = AppDatabase.getDatabase(this).doacaoDao()

        // Observa banco local (offline)
        lifecycleScope.launch {
            doacaoDao.listarTodas().collect { doacoesLocais ->
                if (doacoesLocais.isNotEmpty()) {
                    progressBar.visibility = View.GONE
                    recycler.visibility = View.VISIBLE
                    recycler.adapter = DonationAdapter(
                        doacoesLocais.map {
                            mapOf(
                                "id" to it.id,
                                "alimento" to it.alimento,
                                "quantidade" to it.quantidade,
                                "status" to it.status,
                                "nomeDoador" to (it.nomeDoador ?: ""),
                                "enderecoColeta" to (it.enderecoColeta ?: ""),
                                "telefoneDoador" to (it.telefoneDoador ?: ""),
                                "observacoes" to (it.observacoes ?: "")
                            )
                        }
                    )
                }
            }
        }

        // Sincroniza com Firestore — mostra apenas Disponível e Reservado
        db.collection("doacoes")
            .whereIn("status", listOf("Disponível", "Reservado"))
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    progressBar.visibility = View.GONE
                    if (recycler.adapter == null || recycler.adapter!!.itemCount == 0) {
                        tvErro.visibility = View.VISIBLE
                        tvErro.text = "Sem conexão — mostrando dados salvos offline"
                        recycler.visibility = View.VISIBLE
                    }
                    return@addSnapshotListener
                }

                lifecycleScope.launch {
                    val doacoes = snapshots?.documents?.mapNotNull { doc ->
                        DoacaoEntity(
                            id = doc.id,
                            alimento = doc.getString("alimento") ?: return@mapNotNull null,
                            quantidade = doc.getString("quantidade") ?: "",
                            status = doc.getString("status") ?: "Disponível",
                            data = doc.getLong("data") ?: System.currentTimeMillis(),
                            latitude = doc.getDouble("latitude"),
                            longitude = doc.getDouble("longitude"),
                            nomeDoador = doc.getString("nomeDoador"),
                            enderecoColeta = doc.getString("enderecoColeta"),
                            telefoneDoador = doc.getString("telefoneDoador"),
                            observacoes = doc.getString("observacoes"),
                            sincronizado = true
                        )
                    } ?: emptyList()

                    doacaoDao.limparTodas()
                    doacaoDao.inserirTodas(doacoes)

                    progressBar.visibility = View.GONE
                    tvErro.visibility = View.GONE
                    recycler.visibility = View.VISIBLE
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}