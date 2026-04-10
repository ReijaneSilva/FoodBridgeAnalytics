package com.foodbridge.foodbridgeanalytics2.data.models

data class Usuario(
    val uid: String = "",
    val nome: String = "",
    val email: String = "",
    val tipoUsuario: String = "",  // "Doador", "Receptor", "Voluntário"
    val dataCadastro: Long = System.currentTimeMillis()
)