package com.dakkho.android.domain.model

data class VideoBookmark(
    val id: Long = 0,
    val videoId: String,
    val courseId: String,
    val userId: String,
    val positionMs: Long,
    val note: String? = null,
    val videoTitle: String? = null,
    val courseTitle: String? = null,
    val thumbnailUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents an audio track found in a video container.
 * Video files (MKV, MP4) can contain multiple audio tracks (different languages).
 */
data class AudioTrackInfo(
    val trackIndex: Int,
    val label: String?,       // e.g. "English", "বাংলা", "Hindi"
    val language: String?,    // ISO 639-2 code e.g. "eng", "ben", "hin"
    val mimeType: String?,    // e.g. "audio/mp4a-latm"
    val channelCount: Int?,   // 1=mono, 2=stereo, 6=5.1
    val sampleRate: Int?,
    val bitrate: Int?,
    val isSelected: Boolean = false
)

/**
 * Represents a subtitle track.
 */
data class SubtitleTrackInfo(
    val trackIndex: Int,
    val label: String?,
    val language: String?,
    val mimeType: String?,
    val isSelected: Boolean = false
)
