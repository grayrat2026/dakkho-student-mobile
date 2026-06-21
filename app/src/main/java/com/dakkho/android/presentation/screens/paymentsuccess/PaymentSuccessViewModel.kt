package com.dakkho.android.presentation.screens.paymentsuccess

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.PaymentMiscApiService
import com.dakkho.android.domain.model.PaymentReceipt
import com.dakkho.android.domain.model.PaymentResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentSuccessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val paymentApiService: PaymentMiscApiService
) : ViewModel() {

    private val orderId: String = savedStateHandle["orderId"] ?: ""

    private val _receipt = MutableStateFlow<PaymentReceipt?>(null)
    val receipt: StateFlow<PaymentReceipt?> = _receipt.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        if (orderId.isNotEmpty()) loadReceipt()
    }

    private fun loadReceipt() {
        viewModelScope.launch {
            try {
                val response = paymentApiService.getPaymentReceipt(orderId)
                if (response.isSuccessful) {
                    _receipt.value = response.body()?.data?.toDomain()
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}
