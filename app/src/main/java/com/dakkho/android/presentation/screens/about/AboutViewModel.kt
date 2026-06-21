package com.dakkho.android.presentation.screens.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.AboutApiService
import com.dakkho.android.domain.model.AboutData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class AboutUiState(
    val aboutData: AboutData? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val aboutApiService: AboutApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    init {
        loadAbout()
    }

    fun loadAbout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = aboutApiService.getAbout()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        _uiState.update { it.copy(aboutData = body.data, isLoading = false) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = body?.message ?: "Failed to load about data") }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error: ${response.code()}") }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load about data")
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
