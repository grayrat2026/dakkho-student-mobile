package com.dakkho.android.presentation.screens.changelog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.PaymentMiscApiService
import com.dakkho.android.domain.model.ChangelogEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangelogViewModel @Inject constructor(
    private val paymentApiService: PaymentMiscApiService
) : ViewModel() {

    private val _entries = MutableStateFlow<List<ChangelogEntry>>(emptyList())
    val entries: StateFlow<List<ChangelogEntry>> = _entries.asStateFlow()

    private val _selectedVersion = MutableStateFlow<String?>(null)
    val selectedVersion: StateFlow<String?> = _selectedVersion.asStateFlow()

    init { loadChangelog() }

    fun loadChangelog() {
        viewModelScope.launch {
            try {
                val response = paymentApiService.getChangelog()
                if (response.isSuccessful) {
                    _entries.value = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                }
            } catch (_: Exception) {}
        }
    }

    fun setVersionFilter(version: String?) { _selectedVersion.value = version }
}
