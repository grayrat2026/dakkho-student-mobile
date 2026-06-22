package com.dakkho.android.presentation.screens.videoplayer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.data.db.entity.VideoBookmarkEntity
import com.dakkho.android.domain.model.AudioTrackInfo
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.domain.model.SubtitleTrackInfo
import com.dakkho.android.domain.model.VideoBookmark
import com.dakkho.android.domain.repository.CourseRepository
import com.dakkho.android.domain.repository.EnrollmentRepository
import com.dakkho.android.domain.repository.WatchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// ── Playback Speed Options ──
data class PlaybackSpeedOption(val label: String, val speed: Float)
val PLAYBACK_SPEEDS = listOf(
    PlaybackSpeedOption("0.25x", 0.25f),
    PlaybackSpeedOption("0.5x", 0.5f),
    PlaybackSpeedOption("0.75x", 0.75f),
    PlaybackSpeedOption("1x Normal", 1f),
    PlaybackSpeedOption("1.25x", 1.25f),
    PlaybackSpeedOption("1.5x", 1.5f),
    PlaybackSpeedOption("1.75x", 1.75f),
    PlaybackSpeedOption("2x", 2f)
)

data class QualityOption(val label: String, val height: Int?)
val QUALITY_OPTIONS = listOf(
    QualityOption("Auto", null),
    QualityOption("1080p", 1080),
    QualityOption("720p", 720),
    QualityOption("480p", 480),
    QualityOption("360p", 360)
)

data class VideoPlayerUiState(
    val isLoading: Boolean = true,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val playbackSpeed: Float = 1f,
    val isFullscreen: Boolean = false,
    val isLandscape: Boolean = false,
    val showControls: Boolean = true,
    val showSettingsPanel: Boolean = false,
    val showEpisodesPanel: Boolean = false,
    val showAudioTrackSelector: Boolean = false,
    val showSubtitleSelector: Boolean = false,
    val isLooping: Boolean = false,
    val isBookmarked: Boolean = false,
    val isPipSupported: Boolean = false,

    // Track info
    val audioTracks: List<AudioTrackInfo> = emptyList(),
    val subtitleTracks: List<SubtitleTrackInfo> = emptyList(),
    val hasMultipleAudioTracks: Boolean = false,
    val hasSubtitles: Boolean = false,

    // Curriculum & episodes
    val curriculum: Curriculum? = null,
    val currentLesson: Lesson? = null,
    val allLessons: List<Lesson> = emptyList(),
    val hasMultipleVideos: Boolean = false,

    // Bookmarks
    val videoBookmarks: List<VideoBookmark> = emptyList(),

    // Error
    val error: String? = null
)

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val watchHistoryRepository: WatchHistoryRepository,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    private var trackSelector: DefaultTrackSelector? = null
    private var audioTrackManager: AudioTrackManager? = null
    private var player: ExoPlayer? = null
    private var videoId: String = ""
    private var courseId: String = ""
    private var watchHistoryJob: kotlinx.coroutines.Job? = null

    fun initialize(videoId: String, courseId: String) {
        this.videoId = videoId
        this.courseId = courseId
        loadCurriculum()
        loadBookmarks()
    }

    fun setupPlayer(context: Context, trackSelector: DefaultTrackSelector, player: ExoPlayer) {
        this.trackSelector = trackSelector
        this.audioTrackManager = AudioTrackManager(trackSelector)
        this.player = player

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.value = _uiState.value.copy(
                    isPlaying = isPlaying,
                    isBuffering = isPlaying && player.playbackState == Player.STATE_BUFFERING
                )
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _uiState.value = _uiState.value.copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING,
                    isLoading = playbackState == Player.STATE_IDLE,
                    durationMs = if (player.duration > 0) player.duration else 0L
                )
            }

            override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
                detectTracks()
            }

            override fun onPlayerError(error: PlaybackException) {
                Timber.e(error, "Player error")
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Playback error",
                    isLoading = false
                )
            }
        })

        startWatchHistorySync()
    }

    /**
     * Detect audio and subtitle tracks from the current media source.
     * Called after tracks change (i.e., when media is loaded).
     */
    fun detectTracks() {
        val p = player ?: return
        val atm = audioTrackManager ?: return

        val audioTracks = atm.getAudioTracks(p)
        val subtitleTracks = atm.getSubtitleTracks(p)

        _uiState.value = _uiState.value.copy(
            audioTracks = audioTracks,
            subtitleTracks = subtitleTracks,
            hasMultipleAudioTracks = audioTracks.size > 1,
            hasSubtitles = subtitleTracks.isNotEmpty()
        )

        Timber.d("Detected ${audioTracks.size} audio tracks, ${subtitleTracks.size} subtitle tracks")
    }

    // ── Playback Controls ──

    fun togglePlayPause() {
        val p = player ?: return
        if (p.isPlaying) p.pause() else p.play()
    }

    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }

    fun rewind10s() {
        val p = player ?: return
        val newPos = maxOf(0L, p.currentPosition - 10000)
        p.seekTo(newPos)
    }

    fun forward10s() {
        val p = player ?: return
        val newPos = minOf(p.duration, p.currentPosition + 10000)
        p.seekTo(newPos)
    }

    fun setPlaybackSpeed(speed: Float) {
        player?.setPlaybackSpeed(speed)
        _uiState.value = _uiState.value.copy(playbackSpeed = speed)
    }

    fun toggleLooping() {
        val looping = !_uiState.value.isLooping
        player?.repeatMode = if (looping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        _uiState.value = _uiState.value.copy(isLooping = looping)
    }

    // ── Track Selection ──

    fun selectAudioTrack(trackInfo: AudioTrackInfo) {
        val p = player ?: return
        audioTrackManager?.selectAudioTrack(p, trackInfo)
        _uiState.value = _uiState.value.copy(showAudioTrackSelector = false)
    }

    fun selectSubtitleTrack(trackInfo: SubtitleTrackInfo) {
        audioTrackManager?.selectSubtitleTrack(trackInfo)
        _uiState.value = _uiState.value.copy(showSubtitleSelector = false)
    }

    fun disableSubtitles() {
        audioTrackManager?.disableSubtitles()
        _uiState.value = _uiState.value.copy(showSubtitleSelector = false)
    }

    // ── UI State Toggles ──

    fun toggleControls() {
        _uiState.value = _uiState.value.copy(showControls = !_uiState.value.showControls)
    }

    fun showControls() {
        _uiState.value = _uiState.value.copy(showControls = true)
    }

    fun hideControls() {
        _uiState.value = _uiState.value.copy(
            showControls = false,
            showSettingsPanel = false,
            showAudioTrackSelector = false,
            showSubtitleSelector = false
        )
    }

    fun toggleSettingsPanel() {
        _uiState.value = _uiState.value.copy(
            showSettingsPanel = !_uiState.value.showSettingsPanel,
            showAudioTrackSelector = false,
            showSubtitleSelector = false,
            showEpisodesPanel = false
        )
    }

    fun toggleEpisodesPanel() {
        _uiState.value = _uiState.value.copy(
            showEpisodesPanel = !_uiState.value.showEpisodesPanel,
            showSettingsPanel = false,
            showAudioTrackSelector = false,
            showSubtitleSelector = false
        )
    }

    fun toggleAudioTrackSelector() {
        _uiState.value = _uiState.value.copy(
            showAudioTrackSelector = !_uiState.value.showAudioTrackSelector,
            showSubtitleSelector = false,
            showSettingsPanel = false
        )
    }

    fun toggleSubtitleSelector() {
        _uiState.value = _uiState.value.copy(
            showSubtitleSelector = !_uiState.value.showSubtitleSelector,
            showAudioTrackSelector = false,
            showSettingsPanel = false
        )
    }

    fun setFullscreen(isFullscreen: Boolean) {
        _uiState.value = _uiState.value.copy(isFullscreen = isFullscreen)
    }

    fun setLandscape(isLandscape: Boolean) {
        _uiState.value = _uiState.value.copy(isLandscape = isLandscape)
    }

    // ── Bookmark ──

    fun toggleBookmark() {
        val p = player ?: return
        val userId = encryptedPrefsHelper.getUserId() ?: return
        val currentPosition = p.currentPosition

        viewModelScope.launch {
            val existing = _uiState.value.videoBookmarks.find {
                kotlin.math.abs(it.positionMs - currentPosition) < 5000 // within 5s
            }

            if (existing != null) {
                // Remove bookmark
                watchHistoryRepository.deleteVideoBookmark(existing.id)
                _uiState.value = _uiState.value.copy(
                    isBookmarked = false,
                    videoBookmarks = _uiState.value.videoBookmarks.filter { it.id != existing.id }
                )
            } else {
                // Add bookmark at current position
                val bookmark = VideoBookmarkEntity(
                    videoId = videoId,
                    courseId = courseId,
                    userId = userId,
                    positionMs = currentPosition,
                    videoTitle = _uiState.value.currentLesson?.title,
                    courseTitle = null
                )
                val id = watchHistoryRepository.addVideoBookmark(bookmark)
                _uiState.value = _uiState.value.copy(
                    isBookmarked = true,
                    videoBookmarks = _uiState.value.videoBookmarks + VideoBookmark(
                        id = id,
                        videoId = videoId,
                        courseId = courseId,
                        userId = userId,
                        positionMs = currentPosition,
                        note = null,
                        videoTitle = _uiState.value.currentLesson?.title,
                        courseTitle = null
                    )
                )
            }
        }
    }

    private fun loadBookmarks() {
        val userId = encryptedPrefsHelper.getUserId() ?: return
        viewModelScope.launch {
            val bookmarks = watchHistoryRepository.getVideoBookmarks(videoId, userId)
            _uiState.value = _uiState.value.copy(videoBookmarks = bookmarks)
        }
    }

    // ── Curriculum & Episode Navigation ──

    private fun loadCurriculum() {
        viewModelScope.launch {
            courseRepository.getCourseCurriculum(courseId)
                .onSuccess { curriculum ->
                    val allLessons = collectAllLessons(curriculum)
                    val currentLesson = allLessons.find { it.id == videoId }
                    _uiState.value = _uiState.value.copy(
                        curriculum = curriculum,
                        currentLesson = currentLesson,
                        allLessons = allLessons,
                        hasMultipleVideos = allLessons.size > 1
                    )
                }
                .onFailure { Timber.e(it, "Load curriculum failed") }
        }
    }

    private fun collectAllLessons(curriculum: Curriculum): List<Lesson> {
        val lessons = mutableListOf<Lesson>()
        for (subject in curriculum.sections) {
            for (cls in subject.classes) {
                for (unit in cls.units) {
                    lessons.addAll(unit.lessons)
                }
            }
        }
        return lessons
    }

    fun switchToLesson(lesson: Lesson) {
        if (lesson.videoUrl != null) {
            videoId = lesson.id
            _uiState.value = _uiState.value.copy(
                currentLesson = lesson,
                showEpisodesPanel = false,
                showControls = false
            )
            // Player will handle the URL change via the screen
        }
    }

    // ── Watch History Sync ──

    private fun startWatchHistorySync() {
        watchHistoryJob?.cancel()
        watchHistoryJob = viewModelScope.launch {
            while (true) {
                delay(10_000) // every 10 seconds
                val p = player ?: continue
                if (p.duration > 0 && p.currentPosition > 0) {
                    try {
                        watchHistoryRepository.updateWatchProgress(
                            videoId = videoId,
                            courseId = courseId,
                            progressSeconds = (p.currentPosition / 1000).toInt(),
                            totalSeconds = (p.duration / 1000).toInt()
                        )
                    } catch (e: Exception) {
                        Timber.e(e, "Watch history sync error")
                    }
                }
            }
        }
    }

    // ── Position Updates ──

    fun updatePosition() {
        val p = player ?: return
        _uiState.value = _uiState.value.copy(
            currentPositionMs = p.currentPosition,
            durationMs = if (p.duration > 0) p.duration else 0L,
            bufferedPositionMs = p.bufferedPosition
        )
    }

    override fun onCleared() {
        super.onCleared()
        watchHistoryJob?.cancel()
        // Final watch history save
        val p = player
        if (p != null && p.duration > 0) {
            try {
                viewModelScope.launch {
                    watchHistoryRepository.updateWatchProgress(
                        videoId = videoId,
                        courseId = courseId,
                        progressSeconds = (p.currentPosition / 1000).toInt(),
                        totalSeconds = (p.duration / 1000).toInt()
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Final watch history save error")
            }
        }
    }
}
