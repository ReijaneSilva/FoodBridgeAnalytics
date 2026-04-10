package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodbridge.foodbridgeanalytics2.R
import com.google.firebase.firestore.FirebaseFirestore

class ReceiverActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)

        val recycler = findViewById<RecyclerView>(R.id.recyclerDonations)
        recycler.layoutManager = LinearLayoutManager(this) // Define que a lista é vertical

        // ESCUTADOR EM TEMPO REAL
        db.collection("doacoes")
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener

                val listaDoacoes = mutableListOf<Map<String, Any>>()

                // Pegamos cada doação encontrada na nuvem
                for (doc in snapshots!!) {
                    listaDoacoes.add(doc.data)
                }

                // Entregamos a lista para o nosso "garçom" (Adapter) mostrar na tela
                recycler.adapter = DonationAdapter(listaDoacoes)
            }
    }
}