package com.foodbridge.foodbridgeanalytics2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_badges")
data class UserBadge(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val badgeType: String,
    val title: String,
    val description: String,
    val icon: String = "",
    val unlockedAt: Long = System.currentTimeMillis(),
    val progress: Int = 0,
    val requirement: Int = 0
)

enum class BadgeType {
    GOLD_DONOR,
    SILVER_DONOR,
    BRONZE_DONOR,
    VOLUNTEER_STAR,
    IMPACT_CHAMPION,
    CONSISTENCY_MASTER
}
