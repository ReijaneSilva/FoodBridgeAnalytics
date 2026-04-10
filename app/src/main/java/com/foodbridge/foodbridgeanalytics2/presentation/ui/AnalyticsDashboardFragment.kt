package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.foodbridge.foodbridgeanalytics2.data.AnalyticsRepository
import com.foodbridge.foodbridgeanalytics2.data.local.AppDatabase
import com.foodbridge.foodbridgeanalytics2.data.models.UserBadge
import com.foodbridge.foodbridgeanalytics2.databinding.FragmentAnalyticsDashboardBinding
import com.foodbridge.foodbridgeanalytics2.domain.models.ImpactMetrics
import com.foodbridge.foodbridgeanalytics2.presentation.adapters.BadgeAdapter
import com.foodbridge.foodbridgeanalytics2.presentation.viewmodel.AnalyticsViewModel
import com.foodbridge.foodbridgeanalytics2.presentation.viewmodel.AnalyticsViewModelFactory
import com.foodbridge.foodbridgeanalytics2.presentation.utils.PdfGenerator
import com.google.firebase.firestore.FirebaseFirestore

class AnalyticsDashboardFragment : Fragment() {

    private lateinit var binding: FragmentAnalyticsDashboardBinding
    private lateinit var viewModel: AnalyticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = AppDatabase.getDatabase(requireContext()).analyticsDao()
        val firestore = FirebaseFirestore.getInstance()
        val repository = AnalyticsRepository(dao, firestore)
        val factory = AnalyticsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AnalyticsViewModel::class.java]

        setupObservers()
        viewModel.loadAnalytics(userId = "user_123")

        // 🔹 Este bloco precisa estar dentro do onViewCreated
        binding.btnGeneratePdf.setOnClickListener {
            val metrics = viewModel.impactMetrics.value
            val stats = viewModel.donationStats.value

            if (metrics != null && stats != null) {
                val generator = PdfGenerator(requireContext())
                val path = generator.generateReport(metrics, stats)
                Toast.makeText(requireContext(), "PDF salvo em:\n$path", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Aguarde carregar os dados!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.impactMetrics.observe(viewLifecycleOwner) { metrics ->
            updateMetricsUI(metrics)
        }
        viewModel.userBadges.observe(viewLifecycleOwner) { badges ->
            updateBadgesUI(badges)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMetricsUI(metrics: ImpactMetrics) {
        binding.apply {
            tvTotalFood.text = "${metrics.totalFoodSaved.toInt()} kg"
            tvFamiliesAssisted.text = "${metrics.totalFamiliesAssisted}"
            tvCo2Avoided.text = "${metrics.co2Avoided.toInt()} kg"
            tvEstimatedMeals.text = "${metrics.estimatedMeals}"
        }
    }

    private fun updateBadgesUI(badges: List<UserBadge>) {
        val adapter = BadgeAdapter(badges)
        binding.recyclerViewBadges.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            this.adapter = adapter
        }
    }
}
