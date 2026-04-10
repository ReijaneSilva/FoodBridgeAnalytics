package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.R
import com.foodbridge.foodbridgeanalytics2.SelectionActivity
import com.foodbridge.foodbridgeanalytics2.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCadastrar.setOnClickListener {
            val nome  = binding.etNome.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val senha = binding.etSenha.text.toString().trim()
            val tipo  = when (binding.rgTipoUsuario.checkedRadioButtonId) {
                R.id.rbDoador    -> "Doador"
                R.id.rbReceptor  -> "Receptor"
                R.id.rbVoluntario -> "Voluntário"
                else -> ""
            }

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (tipo.isEmpty()) {
                Toast.makeText(this, "Selecione o tipo de usuário", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (senha.length < 6) {
                Toast.makeText(this, "Senha precisa ter 6+ caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener { result ->
                    val uid = result.user!!.uid
                    val usuario = mapOf(
                        "uid"          to uid,
                        "nome"         to nome,
                        "email"        to email,
                        "tipoUsuario"  to tipo,
                        "dataCadastro" to System.currentTimeMillis()
                    )
                    db.collection("usuarios").document(uid).set(usuario)
                        .addOnSuccessListener {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Conta criada! 🎉", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, SelectionActivity::class.java))
                            finishAffinity()
                        }
                        .addOnFailureListener {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Erro ao salvar perfil: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        binding.btnVoltarLogin.setOnClickListener { finish() }
    }
}