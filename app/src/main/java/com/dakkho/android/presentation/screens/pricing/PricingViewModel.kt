package com.dakkho.android.presentation.screens.pricing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.PaymentMiscApiService
import com.dakkho.android.domain.model.PricingPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PricingViewModel @Inject constructor(
    private val paymentApiService: PaymentMiscApiService
) : ViewModel() {

    private val _plans = MutableStateFlow<List<PricingPlan>>(emptyList())
    val plans: StateFlow<List<PricingPlan>> = _plans.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedPlanId = MutableStateFlow<String?>(null)
    val selectedPlanId: StateFlow<String?> = _selectedPlanId.asStateFlow()

    private val _showPlanSheet = MutableStateFlow(false)
    val showPlanSheet: StateFlow<Boolean> = _showPlanSheet.asStateFlow()

    init { loadPlans() }

    fun loadPlans() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = paymentApiService.getPricingPlans()
                if (response.isSuccessful) {
                    _plans.value = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectPlan(planId: String) {
        _selectedPlanId.value = planId
        _showPlanSheet.value = true
    }

    fun hidePlanSheet() { _showPlanSheet.value = false }
}
