package com.dakkho.android.presentation.screens.paymentfailed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.PaymentMiscApiService
import com.dakkho.android.domain.model.PaymentReceipt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentFailedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val paymentApiService: PaymentMiscApiService
) : ViewModel() {

    private val orderId: String = savedStateHandle["orderId"] ?: ""
    private val _reason = MutableStateFlow("")
    val reason: StateFlow<String> = _reason.asStateFlow()

    init {
        if (orderId.isNotEmpty()) checkStatus()
    }

    private fun checkStatus() {
        viewModelScope.launch {
            try {
                val response = paymentApiService.getPaymentStatus(orderId)
                if (response.isSuccessful) {
                    val status = response.body()?.data?.status ?: "failed"
                    _reason.value = when (status) {
                        "failed" -> "পেমেন্ট প্রক্রিয়ায় ত্রুটি হয়েছে"
                        "expired" -> "পেমেন্টের সময় শেষ হয়ে গেছে"
                        "declined" -> "পেমেন্ট প্রত্যাখ্যাত হয়েছে"
                        else -> "পেমেন্ট ব্যর্থ হয়েছে"
                    }
                }
            } catch (_: Exception) {
                _reason.value = "নেটওয়ার্ক ত্রুটি"
            }
        }
    }
}
