package com.foodbridge.foodbridgeanalytics2.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodbridge.foodbridgeanalytics2.data.AnalyticsRepository
import com.foodbridge.foodbridgeanalytics2.data.models.DonationStats
import com.foodbridge.foodbridgeanalytics2.data.models.UserBadge
import com.foodbridge.foodbridgeanalytics2.domain.models.ImpactMetrics
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class AnalyticsViewModel(
    private val repository: AnalyticsRepository
) : ViewModel() {

    private val _impactMetrics = MutableLiveData<ImpactMetrics>()
    val impactMetrics: LiveData<ImpactMetrics> = _impactMetrics

    private val _donationStats = MutableLiveData<List<DonationStats>>()
    val donationStats: LiveData<List<DonationStats>> = _donationStats

    private val _userBadges = MutableLiveData<List<UserBadge>>()
    val userBadges: LiveData<List<UserBadge>> = _userBadges

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadAnalytics(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Tenta sincronizar com Firebase, mas desiste após 5 segundos
                withTimeoutOrNull(5000L) {
                    repository.syncAnalyticsFromFirestore()
                }

                val stats = repository.getDonationStatsLocal().first()
                _donationStats.value = stats

                val metrics = repository.calculateImpactMetrics()
                _impactMetrics.value = metrics

                val badges = repository.generateBadges(userId)
                _userBadges.value = badges

            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}