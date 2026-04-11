package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.os.Bundle
import android.view.MenuItem
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Alimentos Disponíveis"

        val recycler = findViewById<RecyclerView>(R.id.recyclerDonations)
        recycler.layoutManager = LinearLayoutManager(this)

        val doacaoDao = AppDatabase.getDatabase(this).doacaoDao()

        // Observa o banco local (funciona offline)
        lifecycleScope.launch {
            doacaoDao.listarTodas().collect { doacoesLocais ->
                recycler.adapter = DonationAdapter(
                    doacoesLocais.map {
                        mapOf(
                            "alimento" to it.alimento,
                            "quantidade" to it.quantidade,
                            "status" to it.status
                        )
                    }
                )
            }
        }

        // Sincroniza com o Firestore quando online
        db.collection("doacoes")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Modo offline ativo", Toast.LENGTH_SHORT).show()
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
                            sincronizado = true
                        )
                    } ?: emptyList()

                    doacaoDao.inserirTodas(doacoes)
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}