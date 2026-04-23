package com.foodbridge.foodbridgeanalytics2.data

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
            val donations = firestore.collection("doacoes").get().await()

            val stats = donations.documents.groupBy {
                it.getString("id") ?: ""
            }.map { (donorId, docs) ->
                DonationStats(
                    donorId = donorId,
                    donorName = docs.firstOrNull()?.getString("alimento") ?: "Doação",
                    totalDonations = docs.size,
                    totalKilos = docs.sumOf {
                        extrairQuantidadeKg(it.getString("quantidade") ?: "0")
                    },
                    totalValue = 0.0
                )
            }

            analyticsDao.insertDonationStats(stats)
        } catch (e: Exception) {
            Log.e("AnalyticsRepository", "Erro ao sincronizar", e)
        }
    }

    suspend fun calculateImpactMetrics(): ImpactMetrics {
        return try {
            val donations = firestore.collection("doacoes").get().await()
            val docs = donations.documents

            // Total de doações cadastradas
            val totalDoacoes = docs.size

            // Doações disponíveis (ainda não coletadas)
            val disponíveis = docs.filter {
                it.getString("status") == "Disponivel"
            }

            // Doações já coletadas
            val coletadas = docs.filter {
                it.getString("status") == "Coletado"
            }

            // Soma apenas kg de doações disponíveis
            val kgDisponiveis = disponíveis.sumOf {
                extrairQuantidadeKg(it.getString("quantidade") ?: "0")
            }

            // Soma apenas kg de doações coletadas
            val kgColetados = coletadas.sumOf {
                extrairQuantidadeKg(it.getString("quantidade") ?: "0")
            }

            // Total de kg (disponível + coletado)
            val totalKg = kgDisponiveis + kgColetados

            // Famílias assistidas = número de doações coletadas
            // (cada doação coletada representa uma família atendida)
            val familiasAssistidas = coletadas.size

            // Refeições estimadas: cada kg gera ~5 refeições
            val refeicoesEstimadas = (kgColetados * 5).toInt()

            // CO2 evitado: cada kg de alimento salvo evita ~2.5 kg de CO2
            val co2Evitado = totalKg * 2.5

            ImpactMetrics(
                totalFoodSaved = totalKg,
                totalFamiliesAssisted = familiasAssistidas,
                co2Avoided = co2Evitado,
                estimatedMeals = refeicoesEstimadas,
                totalDonors = totalDoacoes,
                totalReceivers = coletadas.size,
                period = "total"
            )
        } catch (e: Exception) {
            Log.e("AnalyticsRepository", "Erro ao calcular impacto", e)
            ImpactMetrics(0.0, 0, 0.0, 0, 0, 0)
        }
    }

    suspend fun generateBadges(userId: String): List<UserBadge> {
        return try {
            val donations = firestore.collection("doacoes")
                .whereEqualTo("uidDoador", userId)
                .get()
                .await()
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

    // Extrai apenas quantidades em kg, ignora unidades como "caixas", "unidades", etc.
    private fun extrairQuantidadeKg(qtdStr: String): Double {
        val lower = qtdStr.lowercase()
        val numero = lower.filter { it.isDigit() || it == '.' }
            .toDoubleOrNull() ?: return 0.0

        return when {
            lower.contains("kg") -> numero
            lower.contains("g") && !lower.contains("kg") -> numero / 1000.0
            // unidades, caixas, pacotes: não conta como kg
            else -> 0.0
        }
    }
}