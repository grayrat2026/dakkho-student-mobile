package com.dakkho.android.presentation.screens.videoplayer

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.MappingTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelectionArray
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.dakkho.android.domain.model.AudioTrackInfo
import com.dakkho.android.domain.model.SubtitleTrackInfo
import okhttp3.OkHttpClient
import timber.log.Timber

/**
 * Factory that creates an ExoPlayer instance with multi-format support:
 * - MP4, MKV, AVI, WEBM (ProgressiveMediaSource)
 * - HLS / m3u8 (HlsMediaSource)
 * - DASH / mpd (DashMediaSource)
 * Auto-detects source type from URL extension/mime.
 */
object ExoPlayerFactory {

    fun createPlayer(
        context: Context,
        trackSelector: DefaultTrackSelector,
        okHttpClient: OkHttpClient? = null,
        authToken: String? = null
    ): ExoPlayer {
        val dataSourceFactory = if (okHttpClient != null && authToken != null) {
            OkHttpDataSource.Factory(okHttpClient)
                .setUserAgent("DAKKHO-Student/1.0")
                .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $authToken"))
        } else {
            DefaultHttpDataSource.Factory()
                .setUserAgent("DAKKHO-Student/1.0")
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)
        }

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(dataSourceFactory)

        return ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setMediaSourceFactory(mediaSourceFactory)
            .setHandleAudioBecomingNoisy(true)
            .setAudioAttributes(
                androidx.media3.common.AudioAttributes.Builder()
                    .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(androidx.media3.common.C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()
    }

    /**
     * Build a MediaSource for the given URL, auto-detecting the format.
     */
    fun buildMediaSource(
        url: String,
        context: Context,
        trackSelector: DefaultTrackSelector,
        okHttpClient: OkHttpClient? = null,
        authToken: String? = null
    ): MediaSource {
        val dataSourceFactory = if (okHttpClient != null && authToken != null) {
            OkHttpDataSource.Factory(okHttpClient)
                .setUserAgent("DAKKHO-Student/1.0")
                .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $authToken"))
        } else {
            DefaultHttpDataSource.Factory()
                .setUserAgent("DAKKHO-Student/1.0")
        }

        val mediaItem = MediaItem.fromUri(url)
        val extension = url.substringAfterLast(".", "").lowercase()

        return when {
            // HLS
            extension == "m3u8" || url.contains(".m3u8") -> {
                Timber.d("Creating HLS media source for: $url")
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)
            }
            // DASH
            extension == "mpd" || url.contains(".mpd") -> {
                Timber.d("Creating DASH media source for: $url")
                DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)
            }
            // Progressive (MP4, MKV, AVI, WEBM, etc.)
            else -> {
                Timber.d("Creating Progressive media source for: $url")
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)
            }
        }
    }
}

/**
 * Manages audio track detection and selection for ExoPlayer.
 * Handles video containers that have multiple audio tracks (e.g., MKV with dual audio).
 */
class AudioTrackManager(private val trackSelector: DefaultTrackSelector) {

    /**
     * Detect all available audio tracks from the current player state.
     * Returns a list of AudioTrackInfo with metadata for each track.
     */
    fun getAudioTracks(player: ExoPlayer): List<AudioTrackInfo> {
        val trackInfoList = mutableListOf<AudioTrackInfo>()
        val trackGroups = trackSelector.currentMappedTrackInfo ?: return emptyList()

        // Audio renderer is typically at index 1 (0 = video, 1 = audio, 2 = subtitle)
        for (rendererIndex in 0 until trackGroups.rendererCount) {
            if (trackGroups.getRendererType(rendererIndex) != C.TRACK_TYPE_AUDIO) continue

            val trackGroupArray = trackGroups.getTrackGroups(rendererIndex)
            val selectedTrackIndices = getSelectedTrackIndices(rendererIndex)

            for (groupIndex in 0 until trackGroupArray.length) {
                val trackGroup = trackGroupArray[groupIndex]
                for (trackIndex in 0 until trackGroup.length) {
                    val format = trackGroup.getFormat(trackIndex)
                    val globalIndex = trackInfoList.size
                    trackInfoList.add(
                        AudioTrackInfo(
                            trackIndex = globalIndex,
                            label = format.label ?: getLanguageDisplayName(format.language),
                            language = format.language,
                            mimeType = format.sampleMimeType,
                            channelCount = format.channelCount,
                            sampleRate = format.sampleRate,
                            bitrate = format.bitrate,
                            isSelected = isTrackSelected(rendererIndex, groupIndex, trackIndex, selectedTrackIndices)
                        )
                    )
                }
            }
        }

        return trackInfoList
    }

    /**
     * Check if the source has multiple audio tracks available.
     */
    fun hasMultipleAudioTracks(player: ExoPlayer): Boolean {
        return getAudioTracks(player).size > 1
    }

    /**
     * Select a specific audio track by its index.
     * This overrides the track selection for the audio renderer.
     */
    fun selectAudioTrack(player: ExoPlayer, trackInfo: AudioTrackInfo) {
        val trackGroups = trackSelector.currentMappedTrackInfo ?: return

        for (rendererIndex in 0 until trackGroups.rendererCount) {
            if (trackGroups.getRendererType(rendererIndex) != C.TRACK_TYPE_AUDIO) continue

            val trackGroupArray = trackGroups.getTrackGroups(rendererIndex)
            var currentIndex = 0
            for (groupIndex in 0 until trackGroupArray.length) {
                val trackGroup = trackGroupArray[groupIndex]
                for (trackIndex in 0 until trackGroup.length) {
                    if (currentIndex == trackInfo.trackIndex) {
                        val override = TrackSelectionOverride(
                            trackGroup,
                            listOf(trackIndex)
                        )
                        val params = trackSelector.buildUponParameters()
                            .setOverrideForType(override)
                            .build()
                        trackSelector.parameters = params
                        Timber.d("Selected audio track: ${trackInfo.label} (index=${trackInfo.trackIndex})")
                        return
                    }
                    currentIndex++
                }
            }
        }
    }

    /**
     * Reset to automatic audio track selection.
     */
    fun resetAudioTrackSelection() {
        val params = DefaultTrackSelector.ParametersBuilder(trackSelector.context!!)
            .build()
        trackSelector.parameters = params
    }

    /**
     * Get all subtitle tracks from the current player state.
     */
    fun getSubtitleTracks(player: ExoPlayer): List<SubtitleTrackInfo> {
        val trackInfoList = mutableListOf<SubtitleTrackInfo>()
        val trackGroups = trackSelector.currentMappedTrackInfo ?: return emptyList()

        for (rendererIndex in 0 until trackGroups.rendererCount) {
            if (trackGroups.getRendererType(rendererIndex) != C.TRACK_TYPE_TEXT) continue

            val trackGroupArray = trackGroups.getTrackGroups(rendererIndex)
            val selectedTrackIndices = getSelectedTrackIndices(rendererIndex)

            for (groupIndex in 0 until trackGroupArray.length) {
                val trackGroup = trackGroupArray[groupIndex]
                for (trackIndex in 0 until trackGroup.length) {
                    val format = trackGroup.getFormat(trackIndex)
                    trackInfoList.add(
                        SubtitleTrackInfo(
                            trackIndex = trackInfoList.size,
                            label = format.label ?: getLanguageDisplayName(format.language),
                            language = format.language,
                            mimeType = format.sampleMimeType,
                            isSelected = isTrackSelected(rendererIndex, groupIndex, trackIndex, selectedTrackIndices)
                        )
                    )
                }
            }
        }

        return trackInfoList
    }

    /**
     * Check if the source has any subtitle tracks.
     */
    fun hasSubtitles(player: ExoPlayer): Boolean {
        return getSubtitleTracks(player).isNotEmpty()
    }

    /**
     * Select a specific subtitle track.
     */
    fun selectSubtitleTrack(trackInfo: SubtitleTrackInfo) {
        val trackGroups = trackSelector.currentMappedTrackInfo ?: return

        for (rendererIndex in 0 until trackGroups.rendererCount) {
            if (trackGroups.getRendererType(rendererIndex) != C.TRACK_TYPE_TEXT) continue

            val trackGroupArray = trackGroups.getTrackGroups(rendererIndex)
            var currentIndex = 0
            for (groupIndex in 0 until trackGroupArray.length) {
                val trackGroup = trackGroupArray[groupIndex]
                for (trackIndex in 0 until trackGroup.length) {
                    if (currentIndex == trackInfo.trackIndex) {
                        val override = TrackSelectionOverride(
                            trackGroup,
                            listOf(trackIndex)
                        )
                        val params = trackSelector.buildUponParameters()
                            .setOverrideForType(override)
                            .build()
                        trackSelector.parameters = params
                        Timber.d("Selected subtitle track: ${trackInfo.label}")
                        return
                    }
                    currentIndex++
                }
            }
        }
    }

    /**
     * Disable all subtitles.
     */
    fun disableSubtitles() {
        val params = trackSelector.buildUponParameters()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
            .build()
        trackSelector.parameters = params
    }

    /**
     * Enable subtitles (re-enable after disabling).
     */
    fun enableSubtitles() {
        val params = trackSelector.buildUponParameters()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
            .build()
        trackSelector.parameters = params
    }

    // ── Helpers ──

    private fun getSelectedTrackIndices(rendererIndex: Int): Set<Pair<Int, Int>> {
        val selected = mutableSetOf<Pair<Int, Int>>()
        val overrides = trackSelector.parameters.overrides
        // We'll check selection state via the player's current track selection
        // This is a simplified approach
        return selected
    }

    private fun isTrackSelected(
        rendererIndex: Int,
        groupIndex: Int,
        trackIndex: Int,
        selectedIndices: Set<Pair<Int, Int>>
    ): Boolean {
        // For the initial implementation, consider the first track as selected
        return groupIndex == 0 && trackIndex == 0
    }

    private fun getLanguageDisplayName(languageCode: String?): String? {
        if (languageCode.isNullOrBlank()) return null
        return try {
            java.util.Locale(languageCode).displayLanguage.ifBlank { languageCode }
        } catch (e: Exception) {
            languageCode
        }
    }
}
