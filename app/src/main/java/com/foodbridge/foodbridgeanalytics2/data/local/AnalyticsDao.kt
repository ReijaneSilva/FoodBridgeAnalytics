package com.foodbridge.foodbridgeanalytics2.data.local

import androidx.room.*
import com.foodbridge.foodbridgeanalytics2.data.models.DonationStats
import com.foodbridge.foodbridgeanalytics2.data.models.UserBadge
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonationStats(stats: List<DonationStats>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonationStat(stat: DonationStats)

    @Query("SELECT * FROM donation_stats ORDER BY totalKilos DESC")
    fun getAllDonationStats(): Flow<List<DonationStats>>

    @Query("SELECT * FROM donation_stats WHERE donorId = :userId")
    suspend fun getDonationStatsByUser(userId: String): DonationStats?

    @Query("SELECT * FROM donation_stats ORDER BY totalKilos DESC LIMIT :limit")
    suspend fun getTopDonors(limit: Int): List<DonationStats>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<UserBadge>)

    @Query("SELECT * FROM user_badges WHERE userId = :userId")
    fun getUserBadges(userId: String): Flow<List<UserBadge>>

    @Query("DELETE FROM donation_stats")
    suspend fun clearDonationStats()
}