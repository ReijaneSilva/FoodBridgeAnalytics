package com.foodbridge.foodbridgeanalytics2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.databinding.ActivitySelectionBinding
import com.foodbridge.foodbridgeanalytics2.presentation.ui.DonorActivity
import com.foodbridge.foodbridgeanalytics2.presentation.ui.ReceiverActivity

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botão Doador -> Abre tela de cadastro
        binding.btnSelectionDonor.setOnClickListener {
            startActivity(Intent(this, DonorActivity::class.java))
        }

        // Botão ONG -> Abre lista de doações
        binding.btnSelectionReceiver.setOnClickListener {
            startActivity(Intent(this, ReceiverActivity::class.java))
        }

        // Texto Analytics -> Abre a sua MainActivity (com os gráficos)
        binding.btnViewAnalytics.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
