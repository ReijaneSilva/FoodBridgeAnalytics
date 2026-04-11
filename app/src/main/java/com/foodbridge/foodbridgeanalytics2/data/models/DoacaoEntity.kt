package com.foodbridge.foodbridgeanalytics2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doacoes_local")
data class DoacaoEntity(
    @PrimaryKey
    val id: String,
    val alimento: String,
    val quantidade: String,
    val status: String = "Disponível",
    val data: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val sincronizado: Boolean = false
)