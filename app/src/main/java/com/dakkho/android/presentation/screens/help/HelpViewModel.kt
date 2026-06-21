package com.dakkho.android.presentation.screens.help

import androidx.lifecycle.ViewModel
import com.dakkho.android.data.api.SupportApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.HelpCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HelpUiState(
    val isLoading: Boolean = false,
    val categories: List<HelpCategory> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val apiService: SupportApiService,
    private val prefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(HelpUiState())
    val uiState: StateFlow<HelpUiState> = _uiState.asStateFlow()
}