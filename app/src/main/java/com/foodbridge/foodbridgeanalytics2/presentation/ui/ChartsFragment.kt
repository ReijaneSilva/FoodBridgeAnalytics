package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.foodbridge.foodbridgeanalytics2.databinding.FragmentChartsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding
    private val db = FirebaseFirestore.getInstance()

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
        carregarDadosDoFirestore()
    }

    private fun carregarDadosDoFirestore() {
        db.collection("doacoes").get()
            .addOnSuccessListener { documents ->
                if (!isAdded) return@addOnSuccessListener

                val porAlimento = mutableMapOf<String, Double>()
                val porStatus = mutableMapOf<String, Int>()

                for (doc in documents) {
                    val alimento = doc.getString("alimento") ?: continue
                    val qtdStr = doc.getString("quantidade") ?: "0"
                    val qtd = qtdStr.filter { it.isDigit() || it == '.' }
                        .toDoubleOrNull() ?: 1.0
                    val status = doc.getString("status") ?: "Disponivel"

                    porAlimento[alimento] = (porAlimento[alimento] ?: 0.0) + qtd
                    porStatus[status] = (porStatus[status] ?: 0) + 1
                }

                if (porAlimento.isNotEmpty()) {
                    setupPieChart(porAlimento.entries.map { it.key to it.value })
                }
                if (porStatus.isNotEmpty()) {
                    setupStatusChart(porStatus)
                }
                if (porAlimento.isNotEmpty()) {
                    setupBarChart(porAlimento.entries.map { it.key to it.value })
                }
            }
    }

    private fun setupPieChart(data: List<Pair<String, Double>>) {
        binding.pieChartContainer.removeAllViews()
        val total = data.sumOf { it.second }
        if (total == 0.0) return

        data.sortedByDescending { it.second }.forEachIndexed { index, (nome, valor) ->
            val percent = (valor / total * 100).toInt()
            val color = colors[index % colors.size]

            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
            }

            val colorBox = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(16), dpToPx(16)).also {
                    it.marginEnd = dpToPx(12)
                    it.gravity = android.view.Gravity.CENTER_VERTICAL
                }
                setBackgroundColor(Color.parseColor(color))
            }

            val barContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val label = TextView(requireContext()).apply {
                text = "$nome — $percent% (${valor.toInt()} un)"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTextColor(Color.parseColor("#333333"))
            }

            val barWidthRatio = percent / 100f
            val availableWidth = resources.displayMetrics.widthPixels - dpToPx(80)
            val bar = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (availableWidth * barWidthRatio).toInt().coerceAtLeast(dpToPx(4)),
                    dpToPx(10)
                ).also { it.topMargin = dpToPx(4) }
                setBackgroundColor(Color.parseColor(color))
            }

            barContainer.addView(label)
            barContainer.addView(bar)
            row.addView(colorBox)
            row.addView(barContainer)
            binding.pieChartContainer.addView(row)
        }
    }

    private fun setupStatusChart(data: Map<String, Int>) {
        val container = binding.pieChartContainer

        val divider = TextView(requireContext()).apply {
            text = "Status das Doações"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#2E7D32"))
            setPadding(0, dpToPx(16), 0, dpToPx(8))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        container.addView(divider)

        val statusColors = mapOf(
            "Disponivel" to "#4CAF50",
            "Reservado" to "#FF9800",
            "Coletado" to "#9E9E9E"
        )

        data.forEach { (status, count) ->
            val color = statusColors[status] ?: "#607D8B"

            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, dpToPx(6), 0, dpToPx(6))
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val colorBox = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(16), dpToPx(16)).also {
                    it.marginEnd = dpToPx(12)
                }
                setBackgroundColor(Color.parseColor(color))
            }

            val label = TextView(requireContext()).apply {
                text = "$status: $count doação(ões)"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTextColor(Color.parseColor("#333333"))
            }

            row.addView(colorBox)
            row.addView(label)
            container.addView(row)
        }
    }

    private fun setupBarChart(data: List<Pair<String, Double>>) {
        binding.barChartContainer.removeAllViews()
        val sorted = data.sortedByDescending { it.second }
        val maxVal = sorted.maxOfOrNull { it.second } ?: return
        val maxHeightPx = dpToPx(200)

        sorted.forEachIndexed { index, (nome, valor) ->
            val color = colors[index % colors.size]
            val heightRatio = (valor / maxVal).toFloat()

            val col = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).also {
                    it.marginEnd = dpToPx(4)
                    it.marginStart = dpToPx(4)
                }
            }

            val valueLabel = TextView(requireContext()).apply {
                text = "${valor.toInt()}"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                gravity = android.view.Gravity.CENTER
                setTextColor(Color.parseColor("#333333"))
            }

            val bar = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (maxHeightPx * heightRatio).toInt().coerceAtLeast(dpToPx(4))
                )
                setBackgroundColor(Color.parseColor(color))
            }

            val nameLabel = TextView(requireContext()).apply {
                text = nome.take(8)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)
                gravity = android.view.Gravity.CENTER
                setTextColor(Color.parseColor("#666666"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = dpToPx(4) }
            }

            col.addView(valueLabel)
            col.addView(bar)
            col.addView(nameLabel)
            binding.barChartContainer.addView(col)
        }
    }

    // Converte dp para pixels corretamente
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}