package com.foodbridge.foodbridgeanalytics2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.databinding.ActivitySelectionBinding
import com.foodbridge.foodbridgeanalytics2.presentation.ui.DonorActivity
import com.foodbridge.foodbridgeanalytics2.presentation.ui.MapActivity
import com.foodbridge.foodbridgeanalytics2.presentation.ui.ReceiverActivity
import com.google.firebase.auth.FirebaseAuth
import com.foodbridge.foodbridgeanalytics2.presentation.ui.LoginActivity

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectionDonor.setOnClickListener {
            startActivity(Intent(this, DonorActivity::class.java))
        }

        binding.btnSelectionReceiver.setOnClickListener {
            startActivity(Intent(this, ReceiverActivity::class.java))
        }

        binding.btnVerMapa.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        binding.btnViewAnalytics.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnSair.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}