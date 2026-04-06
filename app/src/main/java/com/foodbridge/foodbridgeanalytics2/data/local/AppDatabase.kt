package com.foodbridge.foodbridgeanalytics2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.foodbridge.foodbridgeanalytics2.data.models.DonationStats
import com.foodbridge.foodbridgeanalytics2.data.models.UserBadge

@Database(
    entities = [DonationStats::class, UserBadge::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "foodbridge_analytics_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
