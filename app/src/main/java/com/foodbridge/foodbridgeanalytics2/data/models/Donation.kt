package com.foodbridge.foodbridgeanalytics2.data.models

// 'data class' é como um formulário em branco que o sistema preenche
data class Donation(
    val id: String = "",           // Identificador único da doação
    val foodName: String = "",     // Nome do alimento (ex: Maçã)
    val quantity: String = "",     // Peso ou unidade (ex: 5kg)
    val donorName: String = "",    // Quem está doando
    val status: String = "PENDING" // PENDING (esperando), COLLECTED (coletado)
)
