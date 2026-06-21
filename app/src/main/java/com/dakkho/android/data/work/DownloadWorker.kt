package com.dakkho.android.data.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.dakkho.android.R
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.data.db.dao.DownloadDao
import com.dakkho.android.data.db.entity.DownloadEntity
import com.dakkho.android.domain.model.DownloadItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val downloadDao: DownloadDao,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val downloadId = inputData.getString(KEY_DOWNLOAD_ID) ?: return Result.failure()
        val videoUrl = inputData.getString(KEY_VIDEO_URL) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: "Video"
        val fileSize = inputData.getLong(KEY_FILE_SIZE, 0L)

        Timber.d("DownloadWorker: Starting download $downloadId - $title")

        // Set as foreground to show notification
        setForeground(createForegroundInfo(downloadId, title, 0))

        return try {
            // Update status to downloading
            updateStatus(downloadId, "downloading")

            // Get download directory
            val downloadDir = getDownloadDirectory()
            if (downloadDir == null) {
                updateStatus(downloadId, "failed")
                return Result.failure()
            }

            // Check available space
            val availableSpace = getAvailableStorageBytes(downloadDir)
            if (fileSize > 0 && fileSize > availableSpace) {
                Timber.e("Not enough storage space. Required: $fileSize, Available: $availableSpace")
                updateStatus(downloadId, "failed")
                return Result.failure()
            }

            // Perform the download
            val outputFile = File(downloadDir, "${sanitizeFileName(title)}_${downloadId}.mp4")
            val encryptedFile = File(downloadDir, "${sanitizeFileName(title)}_${downloadId}.mp4.enc")

            val result = downloadFile(
                downloadId = downloadId,
                url = videoUrl,
                outputFile = encryptedFile,
                title = title,
                fileSize = fileSize
            )

            if (result) {
                // Update entity with completed status and file path
                val entity = downloadDao.getById(downloadId)
                if (entity != null) {
                    downloadDao.insert(
                        entity.copy(
                            status = "completed",
                            filePath = encryptedFile.absolutePath,
                            downloadedBytes = entity.fileSizeBytes,
                            completedAt = System.currentTimeMillis()
                        )
                    )
                }
                Timber.d("DownloadWorker: Download completed $downloadId")
                Result.success()
            } else {
                updateStatus(downloadId, "failed")
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "DownloadWorker: Download failed $downloadId")
            updateStatus(downloadId, "failed")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun downloadFile(
        downloadId: String,
        url: String,
        outputFile: File,
        title: String,
        fileSize: Long
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 30_000
            connection.readTimeout = 30_000

            // Add auth header
            val token = encryptedPrefsHelper.getToken()
            if (token != null) {
                connection.setRequestProperty("Authorization", "Bearer $token")
            }

            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Timber.e("Download HTTP error: $responseCode")
                connection.disconnect()
                return@withContext false
            }

            val contentLength = connection.contentLengthLong.toLong().coerceAtLeast(fileSize)

            // Set up AES-256 encryption
            val encryptionKey = getOrCreateEncryptionKey()
            val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
            val keySpec = SecretKeySpec(encryptionKey, "AES")
            val iv = ByteArray(16) // Zero IV for simplicity (in production, use random IV stored separately)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

            connection.inputStream.buffered().use { input ->
                CipherOutputStream(FileOutputStream(outputFile), cipher).use { encryptedOutput ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var bytesRead: Int
                    var totalBytesRead = 0L
                    var lastProgressUpdate = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        encryptedOutput.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        // Update progress in Room periodically
                        val progress = if (contentLength > 0) {
                            ((totalBytesRead.toFloat() / contentLength) * 100).toInt()
                        } else 0

                        if (System.currentTimeMillis() - lastProgressUpdate > PROGRESS_UPDATE_INTERVAL_MS) {
                            val entity = downloadDao.getById(downloadId)
                            if (entity != null) {
                                downloadDao.insert(
                                    entity.copy(
                                        downloadedBytes = totalBytesRead,
                                        fileSizeBytes = contentLength,
                                        status = "downloading"
                                    )
                                )
                            }
                            setForeground(createForegroundInfo(downloadId, title, progress))
                            lastProgressUpdate = System.currentTimeMillis()
                        }
                    }
                    encryptedOutput.flush()
                }
            }

            connection.disconnect()
            true
        } catch (e: Exception) {
            Timber.e(e, "Download file error")
            // Clean up partial file
            if (outputFile.exists()) {
                outputFile.delete()
            }
            false
        }
    }

    private suspend fun updateStatus(downloadId: String, status: String) {
        try {
            val entity = downloadDao.getById(downloadId)
            if (entity != null) {
                downloadDao.insert(entity.copy(status = status))
            }
        } catch (e: Exception) {
            Timber.e(e, "Update download status error")
        }
    }

    private fun createForegroundInfo(downloadId: String, title: String, progress: Int): ForegroundInfo {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Download progress notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val cancelIntent = PendingIntent.getActivity(
            appContext,
            0,
            android.content.Intent(appContext, Class.forName("com.dakkho.android.MainActivity")),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Downloading: $title")
            .setContentText(if (progress > 0) "$progress%" else "Starting download...")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .setContentIntent(cancelIntent)
            .build()

        return ForegroundInfo(downloadId.hashCode(), notification)
    }

    private fun getDownloadDirectory(): File? {
        return try {
            val dir = File(appContext.getExternalFilesDir(android.os.Environment.DIRECTORY_MOVIES), "dakkho_downloads")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            if (dir.exists() && dir.canWrite()) dir else null
        } catch (e: Exception) {
            Timber.e(e, "Get download directory error")
            null
        }
    }

    private fun getAvailableStorageBytes(directory: File): Long {
        return try {
            directory.usableSpace
        } catch (e: Exception) {
            Long.MAX_VALUE
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(50)
    }

    private fun getOrCreateEncryptionKey(): ByteArray {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var keyBase64 = prefs.getString(KEY_ENCRYPTION_KEY, null)
        if (keyBase64 == null) {
            val key = ByteArray(32) // AES-256 key
            java.security.SecureRandom().nextBytes(key)
            keyBase64 = android.util.Base64.encodeToString(key, android.util.Base64.NO_WRAP)
            prefs.edit().putString(KEY_ENCRYPTION_KEY, keyBase64).apply()
        }
        return android.util.Base64.decode(keyBase64, android.util.Base64.NO_WRAP)
    }

    companion object {
        const val KEY_DOWNLOAD_ID = "key_download_id"
        const val KEY_VIDEO_ID = "key_video_id"
        const val KEY_COURSE_ID = "key_course_id"
        const val KEY_TITLE = "key_title"
        const val KEY_VIDEO_URL = "key_video_url"
        const val KEY_FILE_SIZE = "key_file_size"
        const val KEY_THUMBNAIL_URL = "key_thumbnail_url"

        private const val NOTIFICATION_CHANNEL_ID = "dakkho_downloads"
        private const val BUFFER_SIZE = 8192
        private const val PROGRESS_UPDATE_INTERVAL_MS = 1000L
        private const val ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding"
        private const val PREFS_NAME = "dakkho_download_prefs"
        private const val KEY_ENCRYPTION_KEY = "key_encryption_key"

        /**
         * Decrypt a downloaded encrypted video file for playback.
         * Returns a temporary decrypted file path.
         */
        fun decryptFile(context: Context, encryptedFilePath: String): File? {
            return try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val keyBase64 = prefs.getString(KEY_ENCRYPTION_KEY, null) ?: return null
                val key = android.util.Base64.decode(keyBase64, android.util.Base64.NO_WRAP)

                val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
                val keySpec = SecretKeySpec(key, "AES")
                val iv = ByteArray(16)
                val ivSpec = IvParameterSpec(iv)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

                val encryptedFile = File(encryptedFilePath)
                if (!encryptedFile.exists()) return null

                val tempFile = File.createTempFile("dakkho_play_", ".mp4", context.cacheDir)
                encryptedFile.inputStream().buffered().use { input ->
                    javax.crypto.CipherInputStream(input, cipher).use { cipherInput ->
                        FileOutputStream(tempFile).use { output ->
                            val buffer = ByteArray(BUFFER_SIZE)
                            var bytesRead: Int
                            while (cipherInput.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                }

                tempFile
            } catch (e: Exception) {
                Timber.e(e, "Decrypt file error")
                null
            }
        }
    }
}
