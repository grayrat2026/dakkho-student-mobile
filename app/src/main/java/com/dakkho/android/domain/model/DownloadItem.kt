package com.dakkho.android.domain.model

data class DownloadItem(
    val id: String,
    val videoId: String,
    val courseId: String,
    val title: String,
    val filePath: String?,
    val fileSizeBytes: Long = 0,
    val downloadedBytes: Long = 0,
    val status: String = "pending",
    val thumbnailUrl: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long?
) {
    /** Download progress as a 0-100 percentage */
    val progressPercent: Int
        get() = if (fileSizeBytes > 0) {
            ((downloadedBytes.toFloat() / fileSizeBytes) * 100).coerceIn(0f, 100f).toInt()
        } else 0

    /** Whether the download is actively in progress */
    val isDownloading: Boolean
        get() = status == "downloading"

    /** Whether the download completed successfully */
    val isCompleted: Boolean
        get() = status == "completed"

    /** Whether the download failed */
    val isFailed: Boolean
        get() = status == "failed"

    /** Whether the download is pending/queued */
    val isPending: Boolean
        get() = status == "pending"

    /** Human-readable file size string */
    val fileSizeDisplay: String
        get() = formatFileSize(fileSizeBytes)

    /** Quality badge derived from file size heuristic */
    val qualityBadge: String
        get() = when {
            fileSizeBytes >= 1_500_000_000 -> "4K"
            fileSizeBytes >= 800_000_000 -> "1080p"
            fileSizeBytes >= 400_000_000 -> "720p"
            fileSizeBytes >= 150_000_000 -> "480p"
            else -> "360p"
        }

    companion object {
        fun formatFileSize(bytes: Long): String {
            return when {
                bytes >= 1_000_000_000 -> "%.1f GB".format(bytes / 1_000_000_000.0)
                bytes >= 1_000_000 -> "%.1f MB".format(bytes / 1_000_000.0)
                bytes >= 1_000 -> "%.1f KB".format(bytes / 1_000.0)
                else -> "$bytes B"
            }
        }
    }
}
