package com.dakkho.android.presentation.screens.reportissue

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SupportApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.BugCategory
import com.dakkho.android.domain.model.BugReport
import com.dakkho.android.domain.model.BugSeverity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

data class ReportIssueUiState(
    val category: BugCategory = BugCategory.OTHER,
    val severity: BugSeverity = BugSeverity.MINOR,
    val description: String = "",
    val screenshotUri: Uri? = null,
    val screenshotThumbnail: String? = null,
    val collectedLogs: String? = null,
    val isCollectingLogs: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val errorMessage: String? = null,
    val deviceModel: String = Build.MODEL,
    val osVersion: String = Build.VERSION.RELEASE,
    val appVersion: String = "1.0.0", // BuildConfig.VERSION_NAME replacement
    val categoryDropdownExpanded: Boolean = false
)

@HiltViewModel
class ReportIssueViewModel @Inject constructor(
    private val supportApiService: SupportApiService,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportIssueUiState())
    val uiState: StateFlow<ReportIssueUiState> = _uiState.asStateFlow()

    fun selectCategory(category: BugCategory) {
        _uiState.update { it.copy(category = category, categoryDropdownExpanded = false) }
    }

    fun toggleCategoryDropdown() {
        _uiState.update { it.copy(categoryDropdownExpanded = !it.categoryDropdownExpanded) }
    }

    fun dismissCategoryDropdown() {
        _uiState.update { it.copy(categoryDropdownExpanded = false) }
    }

    fun selectSeverity(severity: BugSeverity) {
        _uiState.update { it.copy(severity = severity) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun selectScreenshot(uri: Uri?) {
        _uiState.update {
            it.copy(
                screenshotUri = uri,
                screenshotThumbnail = uri?.toString()
            )
        }
    }

    fun removeScreenshot() {
        _uiState.update { it.copy(screenshotUri = null, screenshotThumbnail = null) }
    }

    fun collectLogs() {
        _uiState.update { it.copy(isCollectingLogs = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec("logcat -d -t 200")
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val logs = reader.readText()
                reader.close()
                val trimmedLogs = if (logs.length > 5000) logs.takeLast(5000) else logs
                _uiState.update { it.copy(collectedLogs = trimmedLogs, isCollectingLogs = false) }
            } catch (e: Exception) {
                Log.e("ReportIssueVM", "Failed to collect logs", e)
                _uiState.update {
                    it.copy(
                        collectedLogs = "লগ সংগ্রহ করা যায়নি: ${e.message}",
                        isCollectingLogs = false
                    )
                }
            }
        }
    }

    fun removeLogs() {
        _uiState.update { it.copy(collectedLogs = null) }
    }

    fun submitBugReport() {
        val currentState = _uiState.value

        if (currentState.description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "বিবরণ লিখুন") }
            return
        }

        if (currentState.description.length < 20) {
            _uiState.update { it.copy(errorMessage = "কমপক্ষে ২০ অক্ষরের বিবরণ লিখুন") }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val userId = encryptedPrefsHelper.getUserId() ?: "anonymous"
                val bugReport = BugReport(
                    category = currentState.category,
                    severity = currentState.severity,
                    description = currentState.description,
                    screenshotUri = currentState.screenshotUri?.toString(),
                    logs = currentState.collectedLogs,
                    deviceModel = currentState.deviceModel,
                    osVersion = currentState.osVersion,
                    appVersion = currentState.appVersion,
                    userId = userId,
                    timestamp = System.currentTimeMillis()
                )

                val response = supportApiService.submitBugReport(bugReport)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(isSubmitting = false, isSubmitted = true)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "জমা দিতে সমস্যা হয়েছে: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ReportIssueVM", "Failed to submit bug report", e)
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "নেটওয়ার্ক ত্রুটি: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetForm() {
        _uiState.update {
            ReportIssueUiState(
                deviceModel = Build.MODEL,
                osVersion = Build.VERSION.RELEASE,
                appVersion = "1.0.0"
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
