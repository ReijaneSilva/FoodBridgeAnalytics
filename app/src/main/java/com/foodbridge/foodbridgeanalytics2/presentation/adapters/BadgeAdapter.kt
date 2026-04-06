package com.foodbridge.foodbridgeanalytics2.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foodbridge.foodbridgeanalytics2.data.models.UserBadge
import com.foodbridge.foodbridgeanalytics2.databinding.ItemBadgeBinding

class BadgeAdapter(private val badges: List<UserBadge>) :
    RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(badges[position])
    }

    override fun getItemCount() = badges.size

    class BadgeViewHolder(private val binding: ItemBadgeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(badge: UserBadge) {
            binding.apply {
                tvBadgeTitle.text = badge.title
                tvBadgeDescription.text = badge.description
            }
        }
    }
}