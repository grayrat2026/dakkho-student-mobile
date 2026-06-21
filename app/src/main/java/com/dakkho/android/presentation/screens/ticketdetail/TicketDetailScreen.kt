package com.dakkho.android.presentation.screens.ticketdetail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.dakkho.android.data.api.SupportApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.TicketAttachment
import com.dakkho.android.domain.model.TicketMessage
import com.dakkho.android.domain.model.TicketStatus
import com.dakkho.android.domain.model.SupportTicket
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral300
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ════════════════════════════════════════════════════
// #86: Ticket Detail ViewModel
// ════════════════════════════════════════════════════

data class TicketDetailUiState(
    val isLoading: Boolean = false,
    val ticket: SupportTicket? = null,
    val messages: List<TicketMessage> = emptyList(),
    val messageInput: String = "",
    val selectedAttachmentUri: Uri? = null,
    val isSending: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TicketDetailViewModel @Inject constructor(
    private val apiService: SupportApiService,
    private val prefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketDetailUiState())
    val uiState: StateFlow<TicketDetailUiState> = _uiState.asStateFlow()

    private var ticketId: String = ""

    fun loadTicket(id: String) {
        ticketId = id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.getTicketDetail(id)
                if (response.isSuccessful) {
                    response.body()?.data?.let { dto ->
                        _uiState.update {
                            it.copy(
                                ticket = dto.toDomain(),
                                messages = dto.toDomain().messages,
                                isLoading = false
                            )
                        }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load ticket")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onMessageInputChanged(input: String) {
        _uiState.update { it.copy(messageInput = input) }
    }

    fun onAttachmentSelected(uri: Uri?) {
        _uiState.update { it.copy(selectedAttachmentUri = uri) }
    }

    fun sendMessage() {
        val currentInput = _uiState.value.messageInput.trim()
        if (currentInput.isEmpty() && _uiState.value.selectedAttachmentUri == null) return

        val newMessage = TicketMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = prefsHelper.getEmail() ?: "শিক্ষার্থী",
            senderRole = "student",
            content = currentInput,
            timestamp = getCurrentTimestamp(),
            isFromStudent = true
        )

        _uiState.update {
            it.copy(
                messages = it.messages + newMessage,
                messageInput = "",
                selectedAttachmentUri = null,
                isSending = false
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun getCurrentTimestamp(): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale("bn", "BD"))
        return sdf.format(java.util.Date())
    }
}

// ════════════════════════════════════════════════════
// #86: Ticket Detail Screen
// ════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: String,
    onBackClick: () -> Unit,
    viewModel: TicketDetailViewModel = hiltViewModel()
) {
    // Initialize ViewModel with ticket ID
    val uiState by viewModel.uiState.collectAsState()
    val ticketData = uiState.ticket

    // Load ticket data on first composition
    LaunchedEffect(ticketId) {
        viewModel.loadTicket(ticketId)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Loading state
    if (uiState.isLoading || ticketData == null) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = { Text("টিকেট বিস্তারিত") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SkyBlue)
            }
        }
        return
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.onAttachmentSelected(uri)
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = ticketData.subject,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            MessageInputBar(
                messageInput = uiState.messageInput,
                onMessageInputChanged = { viewModel.onMessageInputChanged(it) },
                onSendClick = { viewModel.sendMessage() },
                onAttachmentClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                selectedAttachmentUri = uiState.selectedAttachmentUri,
                onClearAttachment = { viewModel.onAttachmentSelected(null) },
                isSending = uiState.isSending
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = DesignToken.Space.dp16)
        ) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // Status Badge
            StatusBadge(status = ticketData.status)

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Status Timeline
            StatusTimeline(currentStatus = ticketData.status)

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            // Message Thread Header
            Text(
                text = "বার্তালাপ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = SkyBlue
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // Message Thread
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════
// Status Badge
// ════════════════════════════════════════════════════

@Composable
private fun StatusBadge(status: TicketStatus) {
    val (bgColor, textColor) = when (status) {
        TicketStatus.OPEN -> SkyBlue.copy(alpha = 0.15f) to SkyBlue
        TicketStatus.IN_PROGRESS -> Warning.copy(alpha = 0.15f) to Warning
        TicketStatus.RESOLVED -> Green.copy(alpha = 0.15f) to Green
        TicketStatus.CLOSED -> Neutral400.copy(alpha = 0.15f) to Neutral500
    }

    Row(
        modifier = Modifier
            .clip(DesignToken.Shape.full)
            .background(bgColor)
            .padding(horizontal = DesignToken.Space.dp12, vertical = DesignToken.Space.dp4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(textColor)
        )
        Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
    }
}

// ════════════════════════════════════════════════════
// Status Timeline
// ════════════════════════════════════════════════════

@Composable
private fun StatusTimeline(currentStatus: TicketStatus) {
    val steps = listOf(
        TicketStatus.OPEN to "খোলা",
        TicketStatus.IN_PROGRESS to "প্রক্রিয়াধীন",
        TicketStatus.RESOLVED to "সমাধান"
    )

    val currentStepIndex = steps.indexOfFirst { it.first == currentStatus }.coerceAtLeast(0)

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = DesignToken.Space.dp8),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, (status, label) ->
                // Step indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Dot
                    val dotColor = when {
                        index < currentStepIndex -> Green
                        index == currentStepIndex -> SkyBlue
                        else -> Neutral300
                    }

                    val isCompleted = index < currentStepIndex

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(dotColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Green
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (index <= currentStepIndex) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (index <= currentStepIndex) dotColor else Neutral500,
                        textAlign = TextAlign.Center
                    )
                }

                // Connector line between steps
                if (index < steps.lastIndex) {
                    val lineColor = if (index < currentStepIndex) Green else Neutral300
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(2.dp)
                            .background(lineColor),
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════
// Message Bubble
// ════════════════════════════════════════════════════

@Composable
private fun MessageBubble(message: TicketMessage) {
    val isFromStudent = message.isFromStudent

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromStudent) Alignment.End else Alignment.Start
    ) {
        // Sender name
        Text(
            text = message.senderName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = if (isFromStudent) SkyBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                horizontal = DesignToken.Space.dp12,
                vertical = DesignToken.Space.dp2
            )
        )

        // Message content card
        val bubbleShape = if (isFromStudent) {
            RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 4.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 16.dp
            )
        }

        val bubbleColor = if (isFromStudent) {
            SkyBlue.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        }

        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleColor)
                .padding(DesignToken.Space.dp12)
        ) {
            // Message text
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Attachments
            if (message.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
                ) {
                    message.attachments.forEach { attachment ->
                        AttachmentThumbnail(attachment = attachment)
                    }
                }
            }

            // Timestamp
            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = if (isFromStudent) TextAlign.End else TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ════════════════════════════════════════════════════
// Attachment Thumbnail
// ════════════════════════════════════════════════════

@Composable
private fun AttachmentThumbnail(attachment: TicketAttachment) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(DesignToken.Shape.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { /* Open attachment */ },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = attachment.fileName,
                modifier = Modifier.size(24.dp),
                tint = SkyBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (attachment.fileName.length > 8) {
                    attachment.fileName.take(6) + "..."
                } else {
                    attachment.fileName
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// ════════════════════════════════════════════════════
// Message Input Bar
// ════════════════════════════════════════════════════

@Composable
private fun MessageInputBar(
    messageInput: String,
    onMessageInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachmentClick: () -> Unit,
    selectedAttachmentUri: Uri?,
    onClearAttachment: () -> Unit,
    isSending: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
    ) {
        // Selected attachment preview
        if (selectedAttachmentUri != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = SkyBlue
                )
                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                Text(
                    text = "ছবি নির্বাচিত",
                    style = MaterialTheme.typography.bodySmall,
                    color = SkyBlue,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "বাদ দিন",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.clickable { onClearAttachment() }
                )
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment button
            IconButton(
                onClick = onAttachmentClick,
                modifier = Modifier.size(DesignToken.IconSize.large)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "সংযুক্তি যোগ করুন",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Text input
            OutlinedTextField(
                value = messageInput,
                onValueChange = onMessageInputChanged,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = DesignToken.Space.dp4),
                placeholder = {
                    Text(
                        text = "মেসেজ লিখুন...",
                        color = Neutral400
                    )
                },
                shape = DesignToken.Shape.xl,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkyBlue.copy(alpha = 0.5f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ),
                maxLines = 3,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            // Send button
            IconButton(
                onClick = onSendClick,
                enabled = messageInput.isNotBlank() || selectedAttachmentUri != null,
                modifier = Modifier.size(DesignToken.IconSize.large)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "পাঠান",
                    tint = if (messageInput.isNotBlank() || selectedAttachmentUri != null) {
                        SkyBlue
                    } else {
                        Neutral400
                    }
                )
            }
        }
    }
}
