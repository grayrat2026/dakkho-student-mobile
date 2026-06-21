package com.dakkho.android.presentation.screens.networkdata

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SettingsApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.NetworkDataConfig
import com.dakkho.android.domain.model.NetworkDataConfigDto
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class NetworkDataUiState(
    val config: NetworkDataConfig = NetworkDataConfig(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val showBandwidthLimitDialog: Boolean = false
)

@HiltViewModel
class NetworkDataViewModel @Inject constructor(
    private val apiService: SettingsApiService,
    private val prefsHelper: EncryptedPrefsHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(NetworkDataUiState())
    val uiState: StateFlow<NetworkDataUiState> = _uiState.asStateFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateNetworkState()
        }

        override fun onLost(network: Network) {
            updateNetworkState()
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            updateNetworkState()
        }
    }

    init {
        loadSettings()
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
            updateNetworkState()
        } catch (e: Exception) {
            Timber.e(e, "Failed to register network callback")
        }
    }

    private fun updateNetworkState() {
        try {
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            val isWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            val isMetered = connectivityManager.isActiveNetworkMetered

            val updated = _uiState.value.config.copy(
                isWifiConnected = isWifi,
                isOnMeteredNetwork = isMetered
            )
            _uiState.value = _uiState.value.copy(config = updated)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update network state")
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val localConfig = loadFromPrefs()
                _uiState.value = _uiState.value.copy(config = localConfig, isLoading = false)
                val response = apiService.getNetworkDataConfig()
                if (response.isSuccessful) {
                    response.body()?.data?.let { dto ->
                        val apiConfig = dto.toDomain()
                        _uiState.value = _uiState.value.copy(config = apiConfig)
                        saveToPrefs(apiConfig)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load network config")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setWifiOnlyEnabled(enabled: Boolean) {
        val updated = _uiState.value.config.copy(isWifiOnlyEnabled = enabled)
        _uiState.value = _uiState.value.copy(config = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun setDataSaverMode(enabled: Boolean) {
        val updated = _uiState.value.config.copy(isDataSaverModeEnabled = enabled)
        _uiState.value = _uiState.value.copy(config = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun setBandwidthLimit(limitMB: Int) {
        val updated = _uiState.value.config.copy(bandwidthLimitMB = limitMB)
        _uiState.value = _uiState.value.copy(config = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun showBandwidthLimitDialog() {
        _uiState.value = _uiState.value.copy(showBandwidthLimitDialog = true)
    }

    fun dismissBandwidthLimitDialog() {
        _uiState.value = _uiState.value.copy(showBandwidthLimitDialog = false)
    }

    private fun pushToApi(config: NetworkDataConfig) {
        viewModelScope.launch {
            try {
                val dto = NetworkDataConfigDto(
                    isWifiOnlyEnabled = config.isWifiOnlyEnabled,
                    isDataSaverModeEnabled = config.isDataSaverModeEnabled,
                    bandwidthLimitMB = config.bandwidthLimitMB,
                    dataUsedThisMonthMB = config.dataUsedThisMonthMB
                )
                apiService.updateNetworkDataConfig(dto)
            } catch (e: Exception) {
                Timber.e(e, "Failed to push network config")
            }
        }
    }

    private fun saveToPrefs(config: NetworkDataConfig) {
        prefsHelper.saveBoolean("wifi_only_enabled", config.isWifiOnlyEnabled)
        prefsHelper.saveBoolean("data_saver_mode", config.isDataSaverModeEnabled)
        prefsHelper.saveString("bandwidth_limit_mb", config.bandwidthLimitMB.toString())
    }

    private fun loadFromPrefs(): NetworkDataConfig {
        val wifiOnly = prefsHelper.getBoolean("wifi_only_enabled", false)
        val dataSaver = prefsHelper.getBoolean("data_saver_mode", false)
        val limitStr = prefsHelper.getString("bandwidth_limit_mb") ?: "0"
        val limit = limitStr.toIntOrNull() ?: 0
        return NetworkDataConfig(
            isWifiOnlyEnabled = wifiOnly,
            isDataSaverModeEnabled = dataSaver,
            bandwidthLimitMB = limit
        )
    }

    override fun onCleared() {
        super.onCleared()
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Timber.e(e, "Failed to unregister network callback")
        }
    }
}
