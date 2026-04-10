package com.foodbridge.foodbridgeanalytics2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.foodbridge.foodbridgeanalytics2.databinding.ActivityMainBinding
import com.foodbridge.foodbridgeanalytics2.presentation.ui.AnalyticsDashboardFragment
import com.foodbridge.foodbridgeanalytics2.presentation.ui.ChartsFragment
import com.foodbridge.foodbridgeanalytics2.presentation.ui.DonorActivity
import com.foodbridge.foodbridgeanalytics2.presentation.ui.ReceiverActivity
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    // 1. O 'binding' é a forma moderna de conectar o código Kotlin aos componentes do XML
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val btnDoador = findViewById<Button>(R.id.btnGoToDonor)
        val btnOng = findViewById<Button>(R.id.btnGoToReceiver)

        btnDoador.setOnClickListener {
            // Comando para mudar de tela
            val intent = Intent(this, DonorActivity::class.java)
            startActivity(intent)
        }

        btnOng.setOnClickListener {
            val intent = Intent(this, ReceiverActivity::class.java)
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)

        // 2. Inicializa o ViewBinding para acessarmos os IDs do XML sem erro
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- FUNCIONALIDADES DE NAVEGAÇÃO ENTRE TELAS (DOADOR E ONG) ---

        // 3. Configura o botão para abrir a tela de Cadastro de Doações (Doador)
        binding.btnAbrirDoador.setOnClickListener {
            val intent = Intent(this, DonorActivity::class.java)
            startActivity(intent)
        }

        // 4. Configura o botão para abrir a tela de Lista de Doações (ONG)
        binding.btnAbrirONG.setOnClickListener {
            val intent = Intent(this, ReceiverActivity::class.java)
            startActivity(intent)
        }

        // --- FUNCIONALIDADES DE DASHBOARD (FRAGMENTOS QUE VOCÊ JÁ TINHA) ---

        // 5. Define a primeira tela que aparece dentro do 'fragmentContainer' ao abrir o app
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AnalyticsDashboardFragment())
                .commit()
        }

        // 6. Configura a barra de navegação inferior (Bottom Navigation)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    // Troca para o fragmento de Dashboard
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AnalyticsDashboardFragment())
                        .commit()
                    true
                }
                R.id.nav_charts -> {
                    // Troca para o fragmento de Gráficos
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