package com.dakkho.android.presentation.screens.qna

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.Discussion
import com.dakkho.android.domain.model.DiscussionReply
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnAScreen(
    courseId: String,
    courseTitle: String,
    isEnrolled: Boolean,
    onBackClick: () -> Unit,
    viewModel: QnAViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(courseId) {
        viewModel.initialize(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.selectedDiscussion != null) "Thread" else "Q&A Forum",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.selectedDiscussion != null) {
                            viewModel.closeDiscussionDetail()
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { innerPadding ->
        if (uiState.selectedDiscussion != null) {
            DiscussionDetailContent(
                uiState = uiState,
                onToggleLike = { viewModel.toggleLike(it) },
                onReplyClick = { viewModel.showReplySheet() },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            DiscussionListContent(
                uiState = uiState,
                onDiscussionClick = { viewModel.openDiscussionDetail(it) },
                onToggleLike = { viewModel.toggleLike(it) },
                onAskQuestion = { viewModel.showAskQuestionSheet() },
                isEnrolled = isEnrolled,
                onLoadMore = { viewModel.loadMoreDiscussions() },
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    // Ask Question Bottom Sheet
    if (uiState.showAskQuestionSheet && isEnrolled) {
        AskQuestionSheet(
            title = uiState.questionTitle,
            body = uiState.questionBody,
            tags = uiState.questionTags,
            isSubmitting = uiState.isSubmittingQuestion,
            onTitleChange = { viewModel.updateQuestionTitle(it) },
            onBodyChange = { viewModel.updateQuestionBody(it) },
            onTagsChange = { viewModel.updateQuestionTags(it) },
            onSubmit = { viewModel.submitQuestion() },
            onDismiss = { viewModel.hideAskQuestionSheet() }
        )
    }

    // Reply Bottom Sheet
    if (uiState.showReplySheet && isEnrolled) {
        ReplySheet(
            body = uiState.replyBody,
            isSubmitting = uiState.isSubmittingReply,
            onBodyChange = { viewModel.updateReplyBody(it) },
            onSubmit = { viewModel.submitReply() },
            onDismiss = { viewModel.hideReplySheet() }
        )
    }
}

@Composable
private fun DiscussionListContent(
    uiState: QnAUiState,
    onDiscussionClick: (Discussion) -> Unit,
    onToggleLike: (Discussion) -> Unit,
    onAskQuestion: () -> Unit,
    isEnrolled: Boolean,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Load more when near the end
    LaunchedEffect(listState, uiState.discussions.size) {
        val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        if (lastVisibleIndex >= uiState.discussions.size - 3 && uiState.hasMore) {
            onLoadMore()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = SkyBlue
            )
        } else if (uiState.discussions.isEmpty() && uiState.error != null) {
            EmptyState(
                title = "Could not load discussions",
                subtitle = uiState.error ?: "An error occurred",
                actionText = "Retry",
                onAction = onRefresh
            )
        } else if (uiState.discussions.isEmpty()) {
            EmptyState(
                title = "No questions yet",
                subtitle = "Be the first to ask a question about this course"
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stats header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.discussions.size} discussions",
                            style = MaterialTheme.typography.bodySmall,
                            color = Neutral400
                        )
                        if (isEnrolled) {
                            TextButton(onClick = onAskQuestion) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ask Question",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SkyBlue
                                )
                            }
                        }
                    }
                }

                items(uiState.discussions, key = { it.id }) { discussion ->
                    DiscussionCard(
                        discussion = discussion,
                        onClick = { onDiscussionClick(discussion) },
                        onToggleLike = { onToggleLike(discussion) }
                    )
                }
            }
        }

        // FAB for asking questions
        if (isEnrolled && uiState.discussions.isNotEmpty()) {
            androidx.compose.material3.FloatingActionButton(
                onClick = onAskQuestion,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = SkyBlue
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Ask Question",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}

@Composable
private fun DiscussionCard(
    discussion: Discussion,
    onClick: () -> Unit,
    onToggleLike: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Pin indicator
            if (discussion.isPinned) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier.size(14.dp),
                        tint = SkyBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pinned",
                        style = MaterialTheme.typography.labelSmall,
                        color = SkyBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Title
            Text(
                text = discussion.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Body preview
            Text(
                text = discussion.body,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral400,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Tags
            if (discussion.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    discussion.tags.take(3).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SkyBlue.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = SkyBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row: author, likes, replies
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author + time
                Text(
                    text = "${discussion.userName} \u2022 ${formatRelativeTime(discussion.createdAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Like + Reply counts
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onToggleLike() }
                    ) {
                        Icon(
                            imageVector = if (discussion.isLikedByUser) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = "Like",
                            modifier = Modifier.size(16.dp),
                            tint = if (discussion.isLikedByUser) SkyBlue else Neutral400
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = discussion.likes.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (discussion.isLikedByUser) SkyBlue else Neutral400
                        )
                    }

                    // Replies
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Replies",
                            modifier = Modifier.size(16.dp),
                            tint = Neutral400
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = discussion.replyCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral400
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscussionDetailContent(
    uiState: QnAUiState,
    onToggleLike: (Discussion) -> Unit,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val discussion = uiState.selectedDiscussion ?: return

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoadingDetail) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = SkyBlue
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Original question
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Pin indicator
                            if (discussion.isPinned) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = "Pinned",
                                        modifier = Modifier.size(14.dp),
                                        tint = SkyBlue
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Pinned",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SkyBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Text(
                                text = discussion.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = discussion.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Tags
                            if (discussion.tags.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    discussion.tags.forEach { tag ->
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = SkyBlue.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = tag,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SkyBlue,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Author + meta + like
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${discussion.userName} \u2022 ${formatRelativeTime(discussion.createdAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Neutral400,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onToggleLike(discussion) }
                                ) {
                                    Icon(
                                        imageVector = if (discussion.isLikedByUser) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                                        contentDescription = "Like",
                                        modifier = Modifier.size(18.dp),
                                        tint = if (discussion.isLikedByUser) SkyBlue else Neutral400
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${discussion.likes} likes",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (discussion.isLikedByUser) SkyBlue else Neutral400
                                    )
                                }
                            }

                            // Closed indicator
                            if (discussion.isClosed) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "This discussion is closed",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Replies header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.replies.size} replies",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Replies
                items(uiState.replies, key = { it.id }) { reply ->
                    ReplyCard(reply = reply)
                }
            }

            // Reply FAB
            if (!discussion.isClosed) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = onReplyClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = SkyBlue
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Reply",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ReplyCard(reply: DiscussionReply) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Author + time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar placeholder
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = SkyBlue.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = reply.userName.take(1).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = SkyBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = reply.userName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatRelativeTime(reply.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reply body
            Text(
                text = reply.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AskQuestionSheet(
    title: String,
    body: String,
    tags: String,
    isSubmitting: Boolean,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onTagsChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ask a Question",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                placeholder = { Text("What's your question?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = body,
                onValueChange = onBodyChange,
                label = { Text("Details") },
                placeholder = { Text("Provide more context...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = tags,
                onValueChange = onTagsChange,
                label = { Text("Tags (comma separated)") },
                placeholder = { Text("e.g. math, calculus, integration") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                com.dakkho.android.presentation.components.GradientButton(
                    text = "Post Question",
                    onClick = onSubmit,
                    isLoading = isSubmitting,
                    enabled = title.isNotBlank() && body.isNotBlank()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReplySheet(
    body: String,
    isSubmitting: Boolean,
    onBodyChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Write a Reply",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = body,
                onValueChange = onBodyChange,
                label = { Text("Your reply") },
                placeholder = { Text("Share your answer or thoughts...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                com.dakkho.android.presentation.components.GradientButton(
                    text = "Post Reply",
                    onClick = onSubmit,
                    isLoading = isSubmitting,
                    enabled = body.isNotBlank()
                )
            }
        }
    }
}

private fun formatRelativeTime(dateString: String): String {
    return try {
        val date = java.time.Instant.parse(dateString)
        val now = java.time.Instant.now()
        val duration = java.time.Duration.between(date, now)
        when {
            duration.toMinutes() < 1 -> "just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
            duration.toHours() < 24 -> "${duration.toHours()}h ago"
            duration.toDays() < 30 -> "${duration.toDays()}d ago"
            else -> "${duration.toDays() / 30}mo ago"
        }
    } catch (e: Exception) {
        dateString
    }
}
