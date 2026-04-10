package com.foodbridge.foodbridgeanalytics2.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foodbridge.foodbridgeanalytics2.R

class DonationAdapter(private val list: List<Map<String, Any>>) :
    RecyclerView.Adapter<DonationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.txtFoodName)
        val qtd: TextView = view.findViewById(R.id.txtQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.nome.text = item["alimento"].toString()
        holder.qtd.text = "Qtd: ${item["quantidade"]}"
    }

    override fun getItemCount() = list.size
}
