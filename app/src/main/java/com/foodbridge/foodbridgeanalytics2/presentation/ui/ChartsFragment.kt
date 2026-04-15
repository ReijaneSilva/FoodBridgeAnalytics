package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.graphics.Color
import android.os.Bundle
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

                // Agrupa por alimento e soma quantidades numéricas
                val porAlimento = mutableMapOf<String, Double>()
                val porStatus = mutableMapOf<String, Int>()

                for (doc in documents) {
                    val alimento = doc.getString("alimento") ?: continue
                    val qtdStr = doc.getString("quantidade") ?: "0"
                    val qtd = qtdStr.filter { it.isDigit() || it == '.' }
                        .toDoubleOrNull() ?: 1.0
                    val status = doc.getString("status") ?: "Disponível"

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
                layoutParams = LinearLayout.LayoutParams(32, 32).also { it.marginEnd = 16 }
                setBackgroundColor(Color.parseColor(color))
            }

            val barContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val label = TextView(requireContext()).apply {
                text = "$nome — $percent% (${valor.toInt()} un)"
                textSize = 13f
                setTextColor(Color.parseColor("#333333"))
            }

            val barWidth = (resources.displayMetrics.widthPixels * percent / 100)
            val bar = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(barWidth, 20).also { it.topMargin = 4 }
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
        // Adiciona gráfico de status se o container existir
        val container = binding.pieChartContainer
        val divider = TextView(requireContext()).apply {
            text = "Status das Doações"
            textSize = 14f
            setTextColor(Color.parseColor("#2E7D32"))
            setPadding(0, 24, 0, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        container.addView(divider)

        val statusColors = mapOf(
            "Disponível" to "#4CAF50",
            "Reservado" to "#FF9800",
            "Coletado" to "#9E9E9E"
        )

        data.forEach { (status, count) ->
            val color = statusColors[status] ?: "#607D8B"
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 6, 0, 6)
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val colorBox = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(32, 32).also { it.marginEnd = 16 }
                setBackgroundColor(Color.parseColor(color))
            }

            val label = TextView(requireContext()).apply {
                text = "$status: $count doação(ões)"
                textSize = 13f
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

        sorted.forEachIndexed { index, (nome, valor) ->
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