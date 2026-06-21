package com.dakkho.android.presentation.screens.certificates

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Certificate
import com.dakkho.android.domain.repository.CertificateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

data class CertificatesUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val certificates: List<Certificate> = emptyList(),
    val generatingPdfForId: String? = null,
    val sharingPdfForId: String? = null,
    val pdfGeneratedFile: File? = null,
    val showShareSuccess: Boolean = false
)

@HiltViewModel
class CertificatesViewModel @Inject constructor(
    private val certificateRepository: CertificateRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CertificatesUiState())
    val uiState: StateFlow<CertificatesUiState> = _uiState.asStateFlow()

    init {
        loadCertificates()
    }

    fun loadCertificates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = certificateRepository.getCertificates()
            if (result.isSuccess) {
                val certificates = result.getOrDefault(emptyList())
                _uiState.update {
                    it.copy(isLoading = false, certificates = certificates)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load certificates"
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = certificateRepository.getCertificates()
            if (result.isSuccess) {
                val certificates = result.getOrDefault(emptyList())
                _uiState.update {
                    it.copy(isRefreshing = false, certificates = certificates)
                }
            } else {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun downloadCertificate(certificate: Certificate) {
        viewModelScope.launch {
            _uiState.update { it.copy(generatingPdfForId = certificate.id) }
            try {
                val outputDir = File(context.cacheDir, "certificates").apply {
                    if (!exists()) mkdirs()
                }
                val result = certificateRepository.generateCertificatePdf(certificate, outputDir)
                if (result.isSuccess) {
                    val pdfFile = result.getOrNull()
                    _uiState.update {
                        it.copy(
                            generatingPdfForId = null,
                            pdfGeneratedFile = pdfFile
                        )
                    }
                    Timber.d("Certificate PDF saved: ${pdfFile?.absolutePath}")
                } else {
                    _uiState.update {
                        it.copy(
                            generatingPdfForId = null,
                            error = result.exceptionOrNull()?.message ?: "Failed to generate certificate"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Download certificate error")
                _uiState.update {
                    it.copy(generatingPdfForId = null)
                }
            }
        }
    }

    fun shareCertificate(certificate: Certificate) {
        viewModelScope.launch {
            _uiState.update { it.copy(sharingPdfForId = certificate.id) }
            try {
                // Generate PDF first if not already generated
                val outputDir = File(context.cacheDir, "certificates").apply {
                    if (!exists()) mkdirs()
                }
                val pdfResult = certificateRepository.generateCertificatePdf(certificate, outputDir)
                if (pdfResult.isSuccess) {
                    val pdfFile = pdfResult.getOrNull()
                    if (pdfFile != null) {
                        val shareResult = certificateRepository.shareCertificatePdf(pdfFile)
                        if (shareResult.isFailure) {
                            _uiState.update {
                                it.copy(
                                    sharingPdfForId = null,
                                    error = shareResult.exceptionOrNull()?.message ?: "Failed to share certificate"
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(sharingPdfForId = null, showShareSuccess = true)
                            }
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            sharingPdfForId = null,
                            error = pdfResult.exceptionOrNull()?.message ?: "Failed to generate certificate"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Share certificate error")
                _uiState.update { it.copy(sharingPdfForId = null) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun dismissShareSuccess() {
        _uiState.update { it.copy(showShareSuccess = false) }
    }
}
