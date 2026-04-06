package com.foodbridge.foodbridgeanalytics2.domain.models

data class ImpactMetrics(
    val totalFoodSaved: Double,
    val totalFamiliesAssisted: Int,
    val co2Avoided: Double,
    val estimatedMeals: Int,
    val totalDonors: Int,
    val totalReceivers: Int,
    val period: String = "monthly"
)
