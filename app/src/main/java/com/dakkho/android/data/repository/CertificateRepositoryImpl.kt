package com.dakkho.android.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.dakkho.android.data.api.CertificateApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.Certificate
import com.dakkho.android.domain.model.CertificateDto
import com.dakkho.android.domain.model.CourseCompletionStatus
import com.dakkho.android.domain.repository.CertificateRepository
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CertificateRepositoryImpl @Inject constructor(
    private val certificateApiService: CertificateApiService,
    private val encryptedPrefsHelper: EncryptedPrefsHelper,
    private val context: Context
) : CertificateRepository {

    override suspend fun getCertificates(): Result<List<Certificate>> {
        return try {
            val response = certificateApiService.getCertificates()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val certificates = body.data.map { mapDtoToDomain(it) }
                    Result.success(certificates)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Timber.e("Get certificates API error: ${response.code()}")
                Result.failure(Exception("Failed to load certificates: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get certificates error")
            Result.failure(e)
        }
    }

    override suspend fun getCourseCompletionStatus(courseId: String): Result<CourseCompletionStatus> {
        return try {
            val response = certificateApiService.getCourseCompletionStatus(courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("No completion status found"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get course completion status error")
            Result.failure(e)
        }
    }

    override suspend fun generateCertificatePdf(
        certificate: Certificate,
        outputDir: File
    ): Result<File> {
        return try {
            val fileName = "certificate_${certificate.id}.pdf"
            val pdfFile = File(outputDir, fileName)

            // Generate PDF using Android PrintDocument/Canvas API
            val pageWidth = 595 // A4 width in points
            val pageHeight = 842 // A4 height in points

            // Using PdfDocument for native PDF generation
            val pdfDocument = android.graphics.pdf.PdfDocument()
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(
                pageWidth, pageHeight, 1
            ).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Background
            val bgPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                style = android.graphics.Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

            // Decorative border
            val borderPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#0EA5E9") // SkyBlue
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 4f
            }
            canvas.drawRect(20f, 20f, pageWidth - 20f, pageHeight - 20f, borderPaint)

            // Inner border
            val innerBorderPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#0EA5E9")
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 2f
            }
            canvas.drawRect(30f, 30f, pageWidth - 30f, pageHeight - 30f, innerBorderPaint)

            // Header line decoration
            val linePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#1E3A8A") // DeepBlue
                style = android.graphics.Paint.Style.FILL
            }
            canvas.drawRect(80f, 120f, pageWidth - 80f, 123f, linePaint)

            // Title: "CERTIFICATE"
            val titlePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#1E3A8A")
                textSize = 48f
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText("CERTIFICATE", pageWidth / 2f, 100f, titlePaint)

            // Subtitle: "OF COMPLETION"
            val subtitlePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#0EA5E9")
                textSize = 24f
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText("OF COMPLETION", pageWidth / 2f, 160f, subtitlePaint)

            // "This is to certify that"
            val bodyPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#64748B") // Neutral500
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText("This is to certify that", pageWidth / 2f, 230f, bodyPaint)

            // Student name
            val studentPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#0F172A")
                textSize = 32f
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText(certificate.studentName, pageWidth / 2f, 290f, studentPaint)

            // Underline below student name
            val nameUnderlinePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#0EA5E9")
                style = android.graphics.Paint.Style.FILL
            }
            val nameWidth = studentPaint.measureText(certificate.studentName)
            val nameStart = (pageWidth - nameWidth) / 2f
            canvas.drawRect(nameStart, 300f, nameStart + nameWidth, 303f, nameUnderlinePaint)

            // "has successfully completed the course"
            canvas.drawText("has successfully completed the course", pageWidth / 2f, 350f, bodyPaint)

            // Course name
            val coursePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#0F172A")
                textSize = 28f
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText(certificate.courseName, pageWidth / 2f, 410f, coursePaint)

            // Course underline
            val courseUnderlinePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#0EA5E9")
                style = android.graphics.Paint.Style.FILL
            }
            val courseWidth = coursePaint.measureText(certificate.courseName)
            val courseStart = (pageWidth - courseWidth) / 2f
            canvas.drawRect(courseStart, 420f, courseStart + courseWidth, 423f, courseUnderlinePaint)

            // Completion date
            val datePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#64748B")
                textSize = 14f
                typeface = android.graphics.Typeface.DEFAULT
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText(
                "Completed on ${certificate.completionDate}",
                pageWidth / 2f, 470f, datePaint
            )

            // Grade if available
            certificate.grade?.let { grade ->
                canvas.drawText("Grade: $grade", pageWidth / 2f, 500f, datePaint)
            }

            // Duration if available
            certificate.durationHours?.let { hours ->
                canvas.drawText("Course Duration: ${hours}h", pageWidth / 2f, 530f, datePaint)
            }

            // Signature line
            val sigLinePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8") // Neutral400
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 1f
            }
            canvas.drawLine(100f, 680f, 250f, 680f, sigLinePaint)
            canvas.drawLine(pageWidth - 250f, 680f, pageWidth - 100f, 680f, sigLinePaint)

            // Signature labels
            val sigLabelPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#64748B")
                textSize = 12f
                typeface = android.graphics.Typeface.DEFAULT
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText("Student", 175f, 700f, sigLabelPaint)
            canvas.drawText("Instructor", pageWidth - 175f, 700f, sigLabelPaint)

            // DAKKHO footer
            val footerPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#1E3A8A")
                textSize = 14f
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText("DAKKHO", pageWidth / 2f, 780f, footerPaint)

            val footerSubPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#64748B")
                textSize = 10f
                typeface = android.graphics.Typeface.DEFAULT
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText("Online Learning Platform", pageWidth / 2f, 800f, footerSubPaint)

            // Certificate ID
            canvas.drawText(
                "Certificate ID: ${certificate.id}",
                pageWidth / 2f, 820f, footerSubPaint
            )

            pdfDocument.finishPage(page)

            // Write to file
            FileOutputStream(pdfFile).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            Timber.d("Certificate PDF generated: ${pdfFile.absolutePath}")
            Result.success(pdfFile)
        } catch (e: Exception) {
            Timber.e(e, "Generate certificate PDF error")
            Result.failure(e)
        }
    }

    override suspend fun shareCertificatePdf(pdfFile: File): Result<Unit> {
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "My Certificate - DAKKHO")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "I've completed a course on DAKKHO! Here's my certificate."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooserIntent = Intent.createChooser(shareIntent, "Share Certificate")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Share certificate PDF error")
            Result.failure(e)
        }
    }

    private fun mapDtoToDomain(dto: CertificateDto): Certificate {
        return Certificate(
            id = dto.id,
            courseId = dto.courseId,
            courseName = dto.courseName ?: "",
            studentName = dto.studentName ?: encryptedPrefsHelper.getEmail() ?: "Student",
            completionDate = dto.completionDate ?: "",
            certificateUrl = dto.certificateUrl,
            instructorName = dto.instructorName,
            grade = dto.grade,
            durationHours = dto.durationHours
        )
    }
}
