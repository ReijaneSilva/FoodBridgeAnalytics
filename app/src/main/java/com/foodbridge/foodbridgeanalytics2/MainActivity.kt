package com.foodbridge.foodbridgeanalytics2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.databinding.ActivityMainBinding
import com.foodbridge.foodbridgeanalytics2.presentation.ui.AnalyticsDashboardFragment
import com.foodbridge.foodbridgeanalytics2.presentation.ui.ChartsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AnalyticsDashboardFragment())
                .commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AnalyticsDashboardFragment())
                        .commit()
                    true
                }
                R.id.nav_charts -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ChartsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}