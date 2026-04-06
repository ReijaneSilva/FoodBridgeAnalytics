package com.foodbridge.foodbridgeanalytics2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.presentation.ui.AnalyticsDashboardFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AnalyticsDashboardFragment())
                .commit()
        }
    }
}