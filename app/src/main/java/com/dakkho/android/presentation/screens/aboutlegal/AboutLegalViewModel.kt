package com.dakkho.android.presentation.screens.aboutlegal

import androidx.lifecycle.ViewModel
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.AboutAppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AboutLegalUiState(
    val appInfo: AboutAppInfo = AboutAppInfo(),
    val showLicensesDialog: Boolean = false
)

@HiltViewModel
class AboutLegalViewModel @Inject constructor(
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutLegalUiState())
    val uiState: StateFlow<AboutLegalUiState> = _uiState.asStateFlow()

    init {
        loadAppInfo()
    }

    private fun loadAppInfo() {
        // In production, this would come from BuildConfig or an API
        val appInfo = AboutAppInfo(
            appName = "DAKKHO",
            appVersion = "1.0.0",
            buildNumber = 1,
            buildDate = "2026-06-22",
            minAndroidVersion = "7.0 (API 24)",
            targetSdkVersion = "35 (API 35)",
            developerName = "DAKKHO Team",
            developerEmail = "support@dakkho.com.bd",
            websiteUrl = "https://dakkho.com.bd",
            privacyPolicyUrl = "https://dakkho.com.bd/privacy",
            termsOfServiceUrl = "https://dakkho.com.bd/terms",
            licensesUrl = "https://dakkho.com.bd/licenses",
            playStoreUrl = "https://play.google.com/store/apps/details?id=com.dakkho.android"
        )
        _uiState.update { it.copy(appInfo = appInfo) }
    }

    fun showLicensesDialog() { _uiState.update { it.copy(showLicensesDialog = true) } }
    fun dismissLicensesDialog() { _uiState.update { it.copy(showLicensesDialog = false) } }
}
