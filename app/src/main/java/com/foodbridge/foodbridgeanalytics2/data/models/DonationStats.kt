package com.foodbridge.foodbridgeanalytics2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "donation_stats")
data class DonationStats(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val donorId: String,
    val donorName: String,
    val totalDonations: Int = 0,
    val totalKilos: Double = 0.0,
    val totalValue: Double = 0.0,
    val lastDonationDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
