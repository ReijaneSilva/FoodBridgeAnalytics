package com.foodbridge.foodbridgeanalytics2.presentation.utils

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.foodbridge.foodbridgeanalytics2.data.models.DonationStats
import com.foodbridge.foodbridgeanalytics2.domain.models.ImpactMetrics
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {

    fun generateReport(
        metrics: ImpactMetrics,
        topDonors: List<DonationStats>
    ): String {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        drawReport(canvas, metrics, topDonors)

        document.finishPage(page)

        val fileName = "relatorio_foodbridge_${SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        document.writeTo(FileOutputStream(file))
        document.close()

        return file.absolutePath
    }

    private fun drawReport(
        canvas: Canvas,
        metrics: ImpactMetrics,
        topDonors: List<DonationStats>
    ) {
        val headerPaint = Paint().apply {
            color = Color.parseColor("#2E7D32")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, 595f, 120f, headerPaint)

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Food Bridge Analytics", 30f, 55f, titlePaint)

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#C8E6C9")
            textSize = 14f
        }
        val date = SimpleDateFormat("MMMM 'de' yyyy", Locale("pt", "BR")).format(Date())
        canvas.drawText("Relatorio de Impacto Social - $date", 30f, 85f, subtitlePaint)

        val sectionPaint = Paint().apply {
            color = Color.parseColor("#1B5E20")
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("METRICAS DE IMPACTO", 30f, 150f, sectionPaint)

        val linePaint = Paint().apply {
            color = Color.parseColor("#4CAF50")
            strokeWidth = 2f
        }
        canvas.drawLine(30f, 158f, 565f, 158f, linePaint)

        drawMetricCard(canvas, 30f, 170f, "Alimentos Salvos", "${metrics.totalFoodSaved.toInt()} kg", Color.parseColor("#E8F5E9"), Color.parseColor("#2E7D32"))
        drawMetricCard(canvas, 310f, 170f, "Familias Assistidas", "${metrics.totalFamiliesAssisted}", Color.parseColor("#E3F2FD"), Color.parseColor("#1565C0"))
        drawMetricCard(canvas, 30f, 280f, "CO2 Evitado", "${metrics.co2Avoided.toInt()} kg", Color.parseColor("#FFF3E0"), Color.parseColor("#E65100"))
        drawMetricCard(canvas, 310f, 280f, "Refeicoes Estimadas", "${metrics.estimatedMeals}", Color.parseColor("#FCE4EC"), Color.parseColor("#880E4F"))

        canvas.drawText("TOP DOADORES", 30f, 420f, sectionPaint)
        canvas.drawLine(30f, 428f, 565f, 428f, linePaint)

        val labelPaint = Paint().apply {
            color = Color.parseColor("#555555")
            textSize = 12f
        }
        val valuePaint = Paint().apply {
            color = Color.parseColor("#2E7D32")
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        topDonors.take(5).forEachIndexed { index, donor ->
            val y = 460f + (index * 45f)
            val bgPaint = Paint().apply {
                color = if (index % 2 == 0) Color.parseColor("#F9FBE7") else Color.WHITE
                style = Paint.Style.FILL
            }
            canvas.drawRect(30f, y - 20f, 565f, y + 20f, bgPaint)

            val posicao = when (index) { 0 -> "1."; 1 -> "2."; 2 -> "3."; else -> "${index + 1}." }
            canvas.drawText("$posicao ${donor.donorName}", 40f, y, labelPaint)
            canvas.drawText("${donor.totalKilos.toInt()} kg", 470f, y, valuePaint)
        }

        val footerPaint = Paint().apply {
            color = Color.parseColor("#EEEEEE")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 790f, 595f, 842f, footerPaint)
        val footerTextPaint = Paint().apply {
            color = Color.parseColor("#999999")
            textSize = 10f
        }
        canvas.drawText("Gerado pelo Food Bridge Analytics - ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}", 30f, 820f, footerTextPaint)
    }

    private fun drawMetricCard(
        canvas: Canvas,
        x: Float, y: Float,
        label: String,
        value: String,
        bgColor: Int,
        textColor: Int
    ) {
        val bgPaint = Paint().apply {
            color = bgColor
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(x, y, x + 250f, y + 90f, 12f, 12f, bgPaint)

        val labelPaint = Paint().apply {
            color = Color.parseColor("#555555")
            textSize = 11f
        }
        canvas.drawText(label, x + 12f, y + 28f, labelPaint)

        val valuePaint = Paint().apply {
            color = textColor
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(value, x + 12f, y + 68f, valuePaint)
    }
}