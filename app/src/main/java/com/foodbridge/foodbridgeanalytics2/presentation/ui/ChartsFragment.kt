package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.foodbridge.foodbridgeanalytics2.data.AnalyticsRepository
import com.foodbridge.foodbridgeanalytics2.data.local.AppDatabase
import com.foodbridge.foodbridgeanalytics2.databinding.FragmentChartsBinding
import com.foodbridge.foodbridgeanalytics2.presentation.viewmodel.AnalyticsViewModel
import com.foodbridge.foodbridgeanalytics2.presentation.viewmodel.AnalyticsViewModelFactory
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore

class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding
    private lateinit var viewModel: AnalyticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        val entries = data.map { PieEntry(it.second.toFloat(), it.first) }
        val dataSet = PieDataSet(entries, "Doações por Doador").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
            valueTextColor = Color.WHITE
        }
        binding.pieChart.apply {
            this.data = PieData(dataSet)
            description.text = ""
            centerText = "Doações"
            setCenterTextSize(14f)
            animateY(1000)
            invalidate()
        }
    }

    private fun setupBarChart(data: List<Pair<String, Double>>) {
        val entries = data.mapIndexed { index, pair ->
            BarEntry(index.toFloat(), pair.second.toFloat())
        }
        val dataSet = BarDataSet(entries, "Kg doados").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
        }
        binding.barChart.apply {
            this.data = BarData(dataSet)
            description.text = ""
            xAxis.labelCount = data.size
            animateY(1000)
            invalidate()
        }
    }
}