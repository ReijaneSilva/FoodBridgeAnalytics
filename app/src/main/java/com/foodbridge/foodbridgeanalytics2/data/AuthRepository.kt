package com.foodbridge.foodbridgeanalytics2.data

import com.foodbridge.foodbridgeanalytics2.data.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, senha: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, senha).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cadastrar(
        nome: String,
        email: String,
        senha: String,
        tipoUsuario: String
    ): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, senha).await()
            val uid = result.user?.uid ?: throw Exception("UID nulo")

            val usuario = Usuario(
                uid = uid,
                nome = nome,
                email = email,
                tipoUsuario = tipoUsuario
            )

            firestore.collection("usuarios").document(uid).set(usuario).await()
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun usuarioLogado() = auth.currentUser

    fun logout() = auth.signOut()
}

