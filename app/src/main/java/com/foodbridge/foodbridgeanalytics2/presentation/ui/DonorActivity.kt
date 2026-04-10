package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.R
import com.foodbridge.foodbridgeanalytics2.SelectionActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class DonorActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor)

        // Botão de voltar na ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nova Doação"

        val editAlimento = findViewById<EditText>(R.id.editFoodName)
        val editQtd = findViewById<EditText>(R.id.editQuantity)
        val botaoEnviar = findViewById<Button>(R.id.btnSubmit)

        botaoEnviar.setOnClickListener {
            val nomeAlimento = editAlimento.text.toString().trim()
            val quantidade = editQtd.text.toString().trim()

            if (nomeAlimento.isNotEmpty() && quantidade.isNotEmpty()) {
                botaoEnviar.isEnabled = false // evita duplo clique
                salvarNoFirebase(nomeAlimento, quantidade)
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Botão de voltar da ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun salvarNoFirebase(nome: String, qtd: String) {
        val idUnico = UUID.randomUUID().toString()

        val doacao = hashMapOf(
            "id" to idUnico,
            "alimento" to nome,
            "quantidade" to qtd,
            "status" to "Disponível",
            "data" to System.currentTimeMillis()
        )

        db.collection("doacoes")
            .document(idUnico)
            .set(doacao)
            .addOnSuccessListener {
                Toast.makeText(this, "Doação publicada com sucesso! ✅", Toast.LENGTH_LONG).show()
                // Volta para o menu principal após 1.5s
                android.os.Handler(mainLooper).postDelayed({
                    startActivity(Intent(this, SelectionActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    finish()
                }, 1500)
            }
            .addOnFailureListener { erro ->
                findViewById<Button>(R.id.btnSubmit).isEnabled = true
                Toast.makeText(this, "Erro ao salvar: ${erro.message}", Toast.LENGTH_SHORT).show()
            }
    }
}