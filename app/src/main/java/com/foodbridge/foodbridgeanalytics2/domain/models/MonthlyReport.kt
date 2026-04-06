package com.foodbridge.foodbridgeanalytics2.domain.models

import com.foodbridge.foodbridgeanalytics2.data.models.DonationStats
import java.util.UUID

data class MonthlyReport(
    val reportId: String = UUID.randomUUID().toString(),
    val organizationId: String,
    val month: Int,
    val year: Int,
    val metrics: ImpactMetrics,
    val topDonors: List<DonationStats> = emptyList(),
    val generatedAt: Long = System.currentTimeMillis(),
    val pdfUrl: String? = null
)