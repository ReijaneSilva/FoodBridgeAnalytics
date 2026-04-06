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
            val donations = firestore.collection("donations")
                .get()
                .await()

            val stats = donations.documents.groupBy {
                it.getString("donorId") ?: ""
            }.map { (donorId, docs) ->
                DonationStats(
                    donorId = donorId,
                    donorName = docs.firstOrNull()?.getString("donorName") ?: "Desconhecido",
                    totalDonations = docs.size,
                    totalKilos = docs.sumOf { it.getDouble("quantity") ?: 0.0 },
                    totalValue = docs.sumOf { it.getDouble("estimatedValue") ?: 0.0 }
                )
            }

            analyticsDao.insertDonationStats(stats)
            Log.d("AnalyticsRepository", "Sincronização bem-sucedida: ${stats.size} doadores")
        } catch (e: Exception) {
            Log.e("AnalyticsRepository", "Erro ao sincronizar", e)
        }
    }

    suspend fun calculateImpactMetrics(): ImpactMetrics {
        return try {
            val statsList = analyticsDao.getAllDonationStats().first()
            val totalKilos = statsList.sumOf { it.totalKilos }

            ImpactMetrics(
                totalFoodSaved = totalKilos,
                totalFamiliesAssisted = (totalKilos / 5).toInt(),
                co2Avoided = totalKilos * 2.5,
                estimatedMeals = (totalKilos * 3).toInt(),
                totalDonors = statsList.size,
                totalReceivers = 0,
                period = "monthly"
            )
        } catch (e: Exception) {
            Log.e("AnalyticsRepository", "Erro ao calcular impacto", e)
            ImpactMetrics(0.0, 0, 0.0, 0, 0, 0)
        }
    }

    suspend fun generateBadges(userId: String): List<UserBadge> {
        return try {
            val stats = analyticsDao.getDonationStatsByUser(userId)
            val badges = mutableListOf<UserBadge>()

            stats?.let {
                when {
                    it.totalKilos >= 100 -> badges.add(
                        UserBadge(
                            userId = userId,
                            badgeType = "GOLD_DONOR",
                            title = "Doador Ouro",
                            description = "Doou 100+ kg de alimentos",
                            icon = "ic_badge_gold",
                            requirement = 100
                        )
                    )
                    it.totalKilos >= 50 -> badges.add(
                        UserBadge(
                            userId = userId,
                            badgeType = "SILVER_DONOR",
                            title = "Doador Prata",
                            description = "Doou 50+ kg de alimentos",
                            icon = "ic_badge_silver",
                            requirement = 50
                        )
                    )
                    it.totalKilos >= 10 -> badges.add(
                        UserBadge(
                            userId = userId,
                            badgeType = "BRONZE_DONOR",
                            title = "Doador Bronze",
                            description = "Doou 10+ kg de alimentos",
                            icon = "ic_badge_bronze",
                            requirement = 10
                        )
                    )
                }
            }

            analyticsDao.insertBadges(badges)
            badges
        } catch (e: Exception) {
            Log.e("AnalyticsRepository", "Erro ao gerar badges", e)
            emptyList()
        }
    }
}