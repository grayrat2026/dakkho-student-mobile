package com.dakkho.android.presentation.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.PaymentHistoryItem
import com.dakkho.android.domain.model.Subscription
import com.dakkho.android.domain.model.SubscriptionPlan
import com.dakkho.android.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SubscriptionUiState(
    val currentSubscription: Subscription? = null,
    val plans: List<SubscriptionPlan> = emptyList(),
    val paymentHistory: List<PaymentHistoryItem> = emptyList(),
    val selectedPlanId: String? = null,
    val isLoading: Boolean = true,
    val isSubscribing: Boolean = false,
    val isCancelling: Boolean = false,
    val error: String? = null,
    val showCancelDialog: Boolean = false
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load subscription and plans in parallel
            val subResult = profileRepository.getCurrentSubscription()
            val plansResult = profileRepository.getSubscriptionPlans()
            val paymentsResult = profileRepository.getPaymentHistory()

            subResult.onSuccess { sub ->
                _uiState.update { it.copy(currentSubscription = sub) }
            }.onFailure { e ->
                Timber.e(e, "Failed to load subscription")
            }

            plansResult.onSuccess { plans ->
                _uiState.update { it.copy(plans = plans) }
            }.onFailure { e ->
                Timber.e(e, "Failed to load plans")
            }

            paymentsResult.onSuccess { payments ->
                _uiState.update { it.copy(paymentHistory = payments) }
            }.onFailure { e ->
                Timber.e(e, "Failed to load payment history")
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectPlan(planId: String) {
        _uiState.update { it.copy(selectedPlanId = planId) }
    }

    fun subscribeToPlan() {
        val planId = _uiState.value.selectedPlanId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubscribing = true) }
            profileRepository.subscribeToPlan(planId)
                .onSuccess { sub ->
                    _uiState.update {
                        it.copy(
                            currentSubscription = sub,
                            isSubscribing = false,
                            selectedPlanId = null
                        )
                    }
                }
                .onFailure { e ->
                    Timber.e(e, "Subscribe failed")
                    _uiState.update {
                        it.copy(
                            isSubscribing = false,
                            error = e.message ?: "সাবস্ক্রিপশন ব্যর্থ হয়েছে"
                        )
                    }
                }
        }
    }

    fun showCancelDialog() {
        _uiState.update { it.copy(showCancelDialog = true) }
    }

    fun dismissCancelDialog() {
        _uiState.update { it.copy(showCancelDialog = false) }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCancelling = true, showCancelDialog = false) }
            profileRepository.cancelSubscription()
                .onSuccess { sub ->
                    _uiState.update {
                        it.copy(
                            currentSubscription = sub,
                            isCancelling = false
                        )
                    }
                }
                .onFailure { e ->
                    Timber.e(e, "Cancel subscription failed")
                    _uiState.update {
                        it.copy(
                            isCancelling = false,
                            error = e.message ?: "বাতিল ব্যর্থ হয়েছে"
                        )
                    }
                }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
