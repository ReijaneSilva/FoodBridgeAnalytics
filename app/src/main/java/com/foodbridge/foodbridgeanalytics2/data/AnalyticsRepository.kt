package com.foodbridge.foodbridgeanalytics2.data

import kotlinx.coroutines.flow.first
import android.util.Log
import com.foodbridge.foodbridgeanalytics2.data.local.AnalyticsDao
import com.foodbridge.foodbridgeanalytics2.data.models.DonationStats
import com.foodbridge.foodbridgeanalytics2.data.models.UserBadge
import com.foodbridge.foodbridgeanalytics2.domain.models.ImpactMetrics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class AnalyticsRepository(
    private val analyticsDao: AnalyticsDao,
    private val firestore: FirebaseFirestore
) {

    fun getDonationStatsLocal(): Flow<List<DonationStats>> {
        return analyticsDao.getAllDonationStats()
    }

    suspend fun syncAnalyticsFromFirestore() {
        try {
            // Corrigido: busca da coleção "doacoes" que é onde o app salva
            val donations = firestore.collection("doacoes")
                .get()
                .await()

            val stats = donations.documents.groupBy {
                it.getString("id") ?: ""
            }.map { (donorId, docs) ->
                DonationStats(
                    donorId = donorId,
                    donorName = docs.firstOrNull()?.getString("alimento") ?: "Doação",
                    totalDonations = docs.size,
                    totalKilos = docs.sumOf {
                        it.getString("quantidade")?.filter { c ->
                            c.isDigit() || c == '.'
                        }?.toDoubleOrNull() ?: 0.0
                    },
                    totalValue = 0.0
                )
            }

            analyticsDao.insertDonationStats(stats)
            Log.d("AnalyticsRepository", "Sincronizado: ${stats.size} doações")
        } catch (e: Exception) {
            Log.e("AnalyticsRepository", "Erro ao sincronizar", e)
        }
    }

    suspend fun calculateImpactMetrics(): ImpactMetrics {
        return try {
            // Busca diretamente do Firestore para ter dados atualizados
            val donations = firestore.collection("doacoes").get().await()
            val totalDoacoes = donations.size()

            // Soma as quantidades numéricas
            val totalKilos = donations.documents.sumOf {
                it.getString("quantidade")?.filter { c ->
                    c.isDigit() || c == '.'
                }?.toDoubleOrNull() ?: 0.0
            }

            ImpactMetrics(
                totalFoodSaved = totalKilos,
                totalFamiliesAssisted = (totalDoacoes * 2),
                co2Avoided = totalKilos * 2.5,
                estimatedMeals = (totalDoacoes * 3),
                totalDonors = totalDoacoes,
                totalReceivers = 0,
                period = "total"
            )
        } catch (e: Exception) {
            Log.e("AnalyticsRepository", "Erro ao calcular impacto", e)
            ImpactMetrics(0.0, 0, 0.0, 0, 0, 0)
        }
    }

    suspend fun generateBadges(userId: String): List<UserBadge> {
        return try {
            val donations = firestore.collection("doacoes").get().await()
            val totalDoacoes = donations.size()
            val badges = mutableListOf<UserBadge>()

            when {
                totalDoacoes >= 10 -> badges.add(
                    UserBadge(
                        userId = userId,
                        badgeType = "GOLD_DONOR",
                        title = "Doador Ouro",
                        description = "10+ doações realizadas",
                        icon = "ic_badge_gold",
                        requirement = 10
                    )
                )
                totalDoacoes >= 5 -> badges.add(
                    UserBadge(
                        userId = userId,
                        badgeType = "SILVER_DONOR",
                        title = "Doador Prata",
                        description = "5+ doações realizadas",
                        icon = "ic_badge_silver",
                        requirement = 5
                    )
                )
                totalDoacoes >= 1 -> badges.add(
                    UserBadge(
                        userId = userId,
                        badgeType = "BRONZE_DONOR",
                        title = "Doador Bronze",
                        description = "Primeira doação realizada",
                        icon = "ic_badge_bronze",
                        requirement = 1
                    )
                )
            }

            analyticsDao.insertBadges(badges)
            badges
        } catch (e: Exception) {
            Log.e("AnalyticsRepository", "Erro ao gerar badges", e)
            emptyList()
        }
    }
}