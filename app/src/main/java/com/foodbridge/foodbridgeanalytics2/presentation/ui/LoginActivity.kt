package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.SelectionActivity
import com.foodbridge.foodbridgeanalytics2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se já logado, pula direto
        if (auth.currentUser != null) {
            irParaSelecao()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val senha = binding.etSenha.text.toString().trim()
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.progressBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener {
                    binding.progressBar.visibility = View.GONE
                    irParaSelecao()
                }
                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        binding.btnIrCadastro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun irParaSelecao() {
        startActivity(Intent(this, SelectionActivity::class.java))
        finish()
    }
}