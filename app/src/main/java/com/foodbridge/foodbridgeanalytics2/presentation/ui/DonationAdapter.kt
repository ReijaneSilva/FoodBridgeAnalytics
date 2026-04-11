package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.foodbridge.foodbridgeanalytics2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DonationAdapter(private val list: List<Map<String, Any>>) :
    RecyclerView.Adapter<DonationAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.txtFoodName)
        val qtd: TextView = view.findViewById(R.id.txtQuantity)
        val doador: TextView = view.findViewById(R.id.txtDoador)
        val endereco: TextView = view.findViewById(R.id.txtEndereco)
        val telefone: TextView = view.findViewById(R.id.txtTelefone)
        val observacoes: TextView = view.findViewById(R.id.txtObservacoes)
        val status: TextView = view.findViewById(R.id.txtStatus)
        val btnReservar: Button = view.findViewById(R.id.btnReservar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = item["id"]?.toString() ?: ""
        val status = item["status"]?.toString() ?: "Disponível"
        val nomeDoador = item["nomeDoador"]?.toString() ?: ""
        val endereco = item["enderecoColeta"]?.toString() ?: ""
        val telefone = item["telefoneDoador"]?.toString() ?: ""
        val obs = item["observacoes"]?.toString() ?: ""

        holder.nome.text = item["alimento"]?.toString() ?: ""
        holder.qtd.text = "📦 Quantidade: ${item["quantidade"]}"
        holder.doador.text = if (nomeDoador.isNotEmpty()) "👤 Doador: $nomeDoador" else ""
        holder.endereco.text = if (endereco.isNotEmpty()) "📍 Coleta: $endereco" else ""
        holder.telefone.text = if (telefone.isNotEmpty()) "📞 Contato: $telefone" else ""
        holder.observacoes.text = if (obs.isNotEmpty()) "💬 $obs" else ""
        holder.observacoes.visibility = if (obs.isNotEmpty()) View.VISIBLE else View.GONE

        when (status) {
            "Reservado" -> {
                val reservadoPor = item["reservadoPor"]?.toString() ?: ""
                holder.status.text = "🔒 Reservado por: $reservadoPor"
                holder.status.setTextColor(Color.parseColor("#E65100"))
                holder.btnReservar.text = "✅ JÁ RESERVADO"
                holder.btnReservar.isEnabled = false
                holder.btnReservar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
            }
            "Coletado" -> {
                holder.status.text = "✅ Já coletado"
                holder.status.setTextColor(Color.parseColor("#888888"))
                holder.btnReservar.text = "✅ COLETADO"
                holder.btnReservar.isEnabled = false
                holder.btnReservar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BDBDBD"))
            }
            else -> {
                holder.status.text = "🟢 Disponível"
                holder.status.setTextColor(Color.parseColor("#2E7D32"))
                holder.btnReservar.text = "🤝 RESERVAR ESTA DOAÇÃO"
                holder.btnReservar.isEnabled = true
                holder.btnReservar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1976D2"))

                holder.btnReservar.setOnClickListener {
                    if (id.isEmpty()) return@setOnClickListener

                    val ongEmail = auth.currentUser?.email ?: "ONG"
                    val ongUid = auth.currentUser?.uid ?: ""

                    // Usa transação para evitar dupla reserva
                    db.runTransaction { transaction ->
                        val docRef = db.collection("doacoes").document(id)
                        val snapshot = transaction.get(docRef)
                        val statusAtual = snapshot.getString("status")

                        if (statusAtual != "Disponível") {
                            throw Exception("Esta doação já foi reservada por outra ONG!")
                        }

                        transaction.update(docRef, mapOf(
                            "status" to "Reservado",
                            "reservadoPor" to ongEmail,
                            "uidOng" to ongUid,
                            "dataReserva" to System.currentTimeMillis()
                        ))
                    }.addOnSuccessListener {
                        Toast.makeText(
                            holder.itemView.context,
                            "Doação reservada! Entre em contato pelo telefone informado. ✅",
                            Toast.LENGTH_LONG
                        ).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            holder.itemView.context,
                            e.message ?: "Erro ao reservar",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun getItemCount() = list.size
}