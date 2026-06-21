package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Certificate
import com.dakkho.android.domain.model.CourseCompletionStatus
import java.io.File

interface CertificateRepository {

    suspend fun getCertificates(): Result<List<Certificate>>

    suspend fun getCourseCompletionStatus(courseId: String): Result<CourseCompletionStatus>

    suspend fun generateCertificatePdf(certificate: Certificate, outputDir: File): Result<File>

    suspend fun shareCertificatePdf(pdfFile: File): Result<Unit>
}
