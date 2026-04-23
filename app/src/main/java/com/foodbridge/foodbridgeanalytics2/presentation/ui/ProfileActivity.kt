package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.R
import com.foodbridge.foodbridgeanalytics2.SelectionActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Proteção: redireciona se não estiver logado
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
            return
        }

        val tvNome = findViewById<TextView>(R.id.tvNome)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvTipo = findViewById<TextView>(R.id.tvTipo)
        val tvDataCadastro = findViewById<TextView>(R.id.tvDataCadastro)
        val btnVoltar = findViewById<Button>(R.id.btnVoltarPerfil)

        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { doc ->
                    tvNome.text = doc.getString("nome") ?: "Usuário"
                    tvEmail.text = doc.getString("email") ?: auth.currentUser?.email
                    tvTipo.text = "Tipo: ${doc.getString("tipoUsuario") ?: "-"}"

                    val dataCadastro = doc.getLong("dataCadastro")
                    if (dataCadastro != null) {
                        val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                        tvDataCadastro.text = "Membro desde: ${formato.format(Date(dataCadastro))}"
                    }
                }
                .addOnFailureListener {
                    tvNome.text = auth.currentUser?.email ?: "Usuário"
                }
        }

        btnVoltar.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }
    }
}