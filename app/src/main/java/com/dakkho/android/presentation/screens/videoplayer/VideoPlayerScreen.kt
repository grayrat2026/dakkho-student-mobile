package com.dakkho.android.presentation.screens.videoplayer

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionOff
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkmark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import com.dakkho.android.domain.model.AudioTrackInfo
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.domain.model.SubtitleTrackInfo
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral300
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral800
import com.dakkho.android.presentation.theme.SkyBlue
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    videoId: String,
    courseId: String,
    onBackClick: () -> Unit,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDCAPE

    val trackSelector = remember { DefaultTrackSelector(context) }
    val exoPlayer = remember {
        ExoPlayerFactory.createPlayer(
            context = context,
            trackSelector = trackSelector,
            authToken = null // TODO: Get from EncryptedPrefsHelper
        )
    }

    LaunchedEffect(videoId, courseId) {
        viewModel.initialize(videoId, courseId)
        viewModel.setupPlayer(context, trackSelector, exoPlayer)
        viewModel.setLandscape(isLandscape)
    }

    // Auto-hide controls
    LaunchedEffect(uiState.showControls) {
        if (uiState.showControls) {
            delay(3000)
            if (uiState.showControls && !uiState.showSettingsPanel && !uiState.showEpisodesPanel) {
                viewModel.hideControls()
            }
        }
    }

    // Position update loop
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.updatePosition()
            delay(250)
        }
    }

    // Orientation handling
    LaunchedEffect(uiState.isFullscreen) {
        activity?.requestedOrientation = if (uiState.isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Build and set media source when URL is available
    val currentLesson = uiState.currentLesson
    LaunchedEffect(currentLesson?.videoUrl) {
        currentLesson?.videoUrl?.let { url ->
            val mediaSource = ExoPlayerFactory.buildMediaSource(
                url = url,
                context = context,
                trackSelector = trackSelector
            )
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Player view
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // We use custom controls
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { viewModel.showControls() }
                    )
                }
        )

        // Buffering indicator
        if (uiState.isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Green,
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
            }
        }

        // Controls overlay
        AnimatedVisibility(
            visible = uiState.showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PlayerControlsOverlay(
                uiState = uiState,
                viewModel = viewModel,
                onBackClick = onBackClick,
                isLandscape = isLandscape
            )
        }

        // Settings panel
        if (uiState.showSettingsPanel) {
            SettingsDropdownPanel(
                uiState = uiState,
                viewModel = viewModel,
                isLandscape = isLandscape
            )
        }

        // Audio track selector
        if (uiState.showAudioTrackSelector && uiState.hasMultipleAudioTracks) {
            AudioTrackSelectorPanel(
                audioTracks = uiState.audioTracks,
                onSelectTrack = { viewModel.selectAudioTrack(it) },
                onDismiss = { viewModel.toggleAudioTrackSelector() }
            )
        }

        // Subtitle selector
        if (uiState.showSubtitleSelector) {
            SubtitleSelectorPanel(
                subtitleTracks = uiState.subtitleTracks,
                onSelectTrack = { viewModel.selectSubtitleTrack(it) },
                onDisableSubtitles = { viewModel.disableSubtitles() },
                onDismiss = { viewModel.toggleSubtitleSelector() }
            )
        }

        // Episodes panel
        if (uiState.showEpisodesPanel && uiState.hasMultipleVideos) {
            EpisodesPanel(
                lessons = uiState.allLessons,
                currentLessonId = uiState.currentLesson?.id ?: "",
                onSelectLesson = { viewModel.switchToLesson(it) },
                onDismiss = { viewModel.toggleEpisodesPanel() }
            )
        }
    }
}

// ── Controls Overlay ──

@Composable
private fun PlayerControlsOverlay(
    uiState: VideoPlayerUiState,
    viewModel: VideoPlayerViewModel,
    onBackClick: () -> Unit,
    isLandscape: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top gradient + bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Video title
            Text(
                text = uiState.currentLesson?.title ?: "Loading...",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Audio/DUB button: ONLY show if multiple audio tracks AND in landscape mode
            // In portrait mode, it's inside the Settings menu
            if (uiState.hasMultipleAudioTracks && isLandscape) {
                IconButton(onClick = { viewModel.toggleAudioTrackSelector() }) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Audio/DUB",
                        tint = if (uiState.showAudioTrackSelector) Green else Color.White
                    )
                }
            }

            // CC/Subtitles button: only show if subtitles available
            if (uiState.hasSubtitles) {
                IconButton(onClick = { viewModel.toggleSubtitleSelector() }) {
                    Icon(
                        imageVector = if (uiState.showSubtitleSelector) Icons.Default.ClosedCaption else Icons.Default.ClosedCaptionOff,
                        contentDescription = "Subtitles",
                        tint = if (uiState.showSubtitleSelector) Green else Color.White
                    )
                }
            }

            // Bookmark button
            IconButton(onClick = { viewModel.toggleBookmark() }) {
                Icon(
                    imageVector = if (uiState.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (uiState.isBookmarked) Green else Color.White
                )
            }

            // Episodes button: ONLY show if course has multiple videos
            if (uiState.hasMultipleVideos) {
                IconButton(onClick = { viewModel.toggleEpisodesPanel() }) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Episodes",
                        tint = if (uiState.showEpisodesPanel) Green else Color.White
                    )
                }
            }

            // Settings button
            IconButton(onClick = { viewModel.toggleSettingsPanel() }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (uiState.showSettingsPanel) Green else Color.White
                )
            }
        }

        // Center controls
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            // Rewind 10s
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { viewModel.rewind10s() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Rewind 10s",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text("10", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Play/Pause
            IconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier
                    .size(68.dp)
                    .background(Green.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                    tint = Green,
                    modifier = Modifier.size(48.dp)
                )
            }

            // Forward 10s
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { viewModel.forward10s() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "Forward 10s",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text("10", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Bottom gradient + bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
        )

        // Bottom bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Seek bar
            SeekBarWithTime(
                currentPositionMs = uiState.currentPositionMs,
                durationMs = uiState.durationMs,
                bufferedPositionMs = uiState.bufferedPositionMs,
                onSeek = { viewModel.seekTo(it) }
            )

            // Bottom buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // PiP button
                IconButton(onClick = { /* PiP handled by activity */ }) {
                    Icon(
                        imageVector = Icons.Default.Settings, // placeholder for PiP icon
                        contentDescription = "PiP",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Fullscreen button
                IconButton(onClick = { viewModel.setFullscreen(!uiState.isFullscreen) }) {
                    Icon(
                        imageVector = if (uiState.isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// ── Seek Bar ──

@Composable
private fun SeekBarWithTime(
    currentPositionMs: Long,
    durationMs: Long,
    bufferedPositionMs: Long,
    onSeek: (Long) -> Unit
) {
    Column {
        Slider(
            value = if (durationMs > 0) currentPositionMs.toFloat() else 0f,
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..maxOf(durationMs.toFloat(), 1f),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Green,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                bufferedTrackColor = Color.White.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPositionMs),
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = formatTime(durationMs),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// ── Settings Dropdown Panel ──

@Composable
private fun SettingsDropdownPanel(
    uiState: VideoPlayerUiState,
    viewModel: VideoPlayerViewModel,
    isLandscape: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isLandscape) 60.dp else 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xE6171C24), // dark surface
        shadowElevation = 8.dp
    ) {
        LazyColumn(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // ── Playback Speed ──
            item {
                SettingsSectionHeader(title = "Playback Speed")
            }
            items(PLAYBACK_SPEEDS) { option ->
                SettingsOptionRow(
                    label = option.label,
                    isSelected = uiState.playbackSpeed == option.speed,
                    onClick = { viewModel.setPlaybackSpeed(option.speed) }
                )
            }

            // ── Quality ──
            item {
                SettingsSectionHeader(title = "Quality")
            }
            items(QUALITY_OPTIONS) { option ->
                SettingsOptionRow(
                    label = option.label,
                    isSelected = false, // TODO: track current quality
                    onClick = { /* TODO: set quality */ }
                )
            }

            // ── Loop ──
            item {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                SettingsOptionRow(
                    label = if (uiState.isLooping) "Loop: ON" else "Loop: OFF",
                    isSelected = uiState.isLooping,
                    onClick = { viewModel.toggleLooping() }
                )
            }

            // ── Audio Track (shown in settings for portrait mode, or always) ──
            // In portrait mode, Audio/DUB is inside Settings
            if (uiState.hasMultipleAudioTracks && !isLandscape) {
                item {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    SettingsSectionHeader(title = "Audio Track")
                }
                items(uiState.audioTracks) { track ->
                    SettingsOptionRow(
                        label = track.label ?: "Track ${track.trackIndex}",
                        isSelected = track.isSelected,
                        onClick = { viewModel.selectAudioTrack(track) }
                    )
                }
            }
            // In landscape, Audio/DUB has its own button, but also shown here
            if (uiState.hasMultipleAudioTracks && isLandscape) {
                item {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    SettingsSectionHeader(title = "Audio Track")
                }
                items(uiState.audioTracks) { track ->
                    SettingsOptionRow(
                        label = track.label ?: "Track ${track.trackIndex}",
                        isSelected = track.isSelected,
                        onClick = { viewModel.selectAudioTrack(track) }
                    )
                }
            }

            // ── Subtitles (in settings for quick access) ──
            if (uiState.hasSubtitles) {
                item {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    SettingsSectionHeader(title = "Subtitles")
                }
                item {
                    SettingsOptionRow(
                        label = "Off",
                        isSelected = false,
                        onClick = { viewModel.disableSubtitles() }
                    )
                }
                items(uiState.subtitleTracks) { track ->
                    SettingsOptionRow(
                        label = track.label ?: "Track ${track.trackIndex}",
                        isSelected = track.isSelected,
                        onClick = { viewModel.selectSubtitleTrack(track) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = SkyBlue,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsOptionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Green else Color.White
        )
        if (isSelected) {
            Checkmark(color = Green, modifier = Modifier.size(20.dp))
        }
    }
}

// ── Audio Track Selector Panel ──

@Composable
private fun AudioTrackSelectorPanel(
    audioTracks: List<AudioTrackInfo>,
    onSelectTrack: (AudioTrackInfo) -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xE6171C24)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "Audio / DUB",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            audioTracks.forEach { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTrack(track) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = if (track.isSelected) Green else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = track.label ?: "Track ${track.trackIndex}",
                            color = if (track.isSelected) Green else Color.White
                        )
                    }
                    if (track.isSelected) {
                        Checkmark(color = Green, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

// ── Subtitle Selector Panel ──

@Composable
private fun SubtitleSelectorPanel(
    subtitleTracks: List<SubtitleTrackInfo>,
    onSelectTrack: (SubtitleTrackInfo) -> Unit,
    onDisableSubtitles: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xE6171C24)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "Subtitles / CC",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDisableSubtitles() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Off", color = Color.White)
            }
            subtitleTracks.forEach { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTrack(track) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = track.label ?: "Track ${track.trackIndex}",
                        color = if (track.isSelected) Green else Color.White
                    )
                    if (track.isSelected) {
                        Checkmark(color = Green, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

// ── Episodes Panel ──

@Composable
private fun EpisodesPanel(
    lessons: List<Lesson>,
    currentLessonId: String,
    onSelectLesson: (Lesson) -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .fillMaxWidth(0.85f)
            .align(Alignment.CenterEnd),
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
        color = Color(0xE6171C24)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Episodes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.FullscreenExit,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

            // Episode list
            LazyColumn {
                items(lessons) { lesson ->
                    val isCurrent = lesson.id == currentLessonId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectLesson(lesson) }
                            .background(
                                if (isCurrent) Green.copy(alpha = 0.1f) else Color.Transparent
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Green left border for current
                        if (isCurrent) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(40.dp)
                                    .background(Green, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        } else {
                            Spacer(modifier = Modifier.width(15.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lesson.label ?: "EP ${lesson.order}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Green else SkyBlue
                                ),
                                color = if (isCurrent) Green else SkyBlue
                            )
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Play icon for current
                        if (isCurrent) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Playing",
                                tint = Green,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                }
            }
        }
    }
}

// ── Utility ──

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return when {
        hours > 0 -> "%d:%02d:%02d".format(hours, minutes, seconds)
        else -> "%d:%02d".format(minutes, seconds)
    }
}
