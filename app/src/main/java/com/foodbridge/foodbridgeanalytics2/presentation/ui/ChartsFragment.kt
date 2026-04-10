package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.foodbridge.foodbridgeanalytics2.R
import com.foodbridge.foodbridgeanalytics2.data.AnalyticsRepository
import com.foodbridge.foodbridgeanalytics2.data.local.AppDatabase
import com.foodbridge.foodbridgeanalytics2.databinding.FragmentChartsBinding
import com.foodbridge.foodbridgeanalytics2.presentation.viewmodel.AnalyticsViewModel
import com.foodbridge.foodbridgeanalytics2.presentation.viewmodel.AnalyticsViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding
    private lateinit var viewModel: AnalyticsViewModel

    private val colors = listOf(
        "#4CAF50", "#2196F3", "#FF9800", "#E91E63",
        "#9C27B0", "#00BCD4", "#FF5722", "#607D8B"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = AppDatabase.getDatabase(requireContext()).analyticsDao()
        val firestore = FirebaseFirestore.getInstance()
        val repository = AnalyticsRepository(dao, firestore)
        val factory = AnalyticsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AnalyticsViewModel::class.java]

        viewModel.donationStats.observe(viewLifecycleOwner) { stats ->
            if (stats.isNotEmpty()) {
                setupPieChart(stats.map { it.donorName to it.totalKilos })
                setupBarChart(stats.map { it.donorName to it.totalKilos })
            }
        }

        viewModel.loadAnalytics("user_123")
    }

    private fun setupPieChart(data: List<Pair<String, Double>>) {
        binding.pieChartContainer.removeAllViews()
        val total = data.sumOf { it.second }
        if (total == 0.0) return

        data.forEachIndexed { index, (nome, valor) ->
            val percent = (valor / total * 100).toInt()
            val color = colors[index % colors.size]

            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
            }

            val colorBox = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(32, 32).also {
                    it.marginEnd = 16
                }
                setBackgroundColor(Color.parseColor(color))
            }

            val barContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val label = TextView(requireContext()).apply {
                text = "$nome — $percent% (${valor.toInt()} kg)"
                textSize = 13f
                setTextColor(Color.parseColor("#333333"))
            }

            val bar = View(requireContext()).apply {
                val width = (resources.displayMetrics.widthPixels * percent / 100)
                layoutParams = LinearLayout.LayoutParams(width, 20).also {
                    it.topMargin = 4
                }
                setBackgroundColor(Color.parseColor(color))
            }

            barContainer.addView(label)
            barContainer.addView(bar)
            row.addView(colorBox)
            row.addView(barContainer)
            binding.pieChartContainer.addView(row)
        }
    }

    private fun setupBarChart(data: List<Pair<String, Double>>) {
        binding.barChartContainer.removeAllViews()
        val maxVal = data.maxOfOrNull { it.second } ?: return

        data.forEachIndexed { index, (nome, valor) ->
            val color = colors[index % colors.size]
            val heightRatio = (valor / maxVal).toFloat()
            val maxHeightPx = 200

            val col = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).also {
                    it.marginEnd = 4
                    it.marginStart = 4
                }
            }

            val valueLabel = TextView(requireContext()).apply {
                text = "${valor.toInt()}"
                textSize = 10f
                gravity = android.view.Gravity.CENTER
                setTextColor(Color.parseColor("#333333"))
            }

            val bar = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (maxHeightPx * heightRatio).toInt().coerceAtLeast(4)
                )
                setBackgroundColor(Color.parseColor(color))
            }

            val nameLabel = TextView(requireContext()).apply {
                text = nome.take(8)
                textSize = 9f
                gravity = android.view.Gravity.CENTER
                setTextColor(Color.parseColor("#666666"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = 4 }
            }

            col.addView(valueLabel)
            col.addView(bar)
            col.addView(nameLabel)
            binding.barChartContainer.addView(col)
        }
    }
}