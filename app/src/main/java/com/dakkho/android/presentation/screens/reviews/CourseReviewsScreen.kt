package com.dakkho.android.presentation.screens.reviews

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.Review
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseReviewsScreen(
    courseId: String,
    courseTitle: String,
    averageRating: Float,
    reviewCount: Int,
    isEnrolled: Boolean,
    onBackClick: () -> Unit,
    viewModel: CourseReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // Detect scroll to bottom for pagination
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMoreReviews()
        }
    }

    LaunchedEffect(courseId) {
        viewModel.initialize(courseId, courseTitle, averageRating, reviewCount)
    }

    // Show success snackbar
    LaunchedEffect(uiState.submitReviewSuccess) {
        if (uiState.submitReviewSuccess) {
            snackbarHostState.showSnackbar("Review submitted successfully!")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reviews",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isEnrolled) {
                        IconButton(onClick = { viewModel.showWriteReview() }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Write Review",
                                tint = SkyBlue
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SkyBlue)
            }
        } else if (uiState.error != null && uiState.reviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.error ?: "Something went wrong",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { viewModel.retry() }) {
                        Text(text = "Retry")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Rating summary section
                RatingSummarySection(
                    averageRating = uiState.averageRating,
                    totalReviews = uiState.totalReviewCount,
                    breakdown = uiState.ratingBreakdown
                )

                // Rating filter chips
                RatingFilterChips(
                    selectedFilter = uiState.selectedRatingFilter,
                    onFilterSelected = { viewModel.setRatingFilter(it) }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Review list
                if (uiState.reviews.isEmpty()) {
                    EmptyReviewsState()
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = uiState.reviews.size,
                            key = { uiState.reviews[it].id }
                        ) { index ->
                            ReviewCard(review = uiState.reviews[index])
                            if (index < uiState.reviews.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Loading indicator at bottom for pagination
                        if (uiState.hasMorePages) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = SkyBlue,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Write Review Bottom Sheet
    if (uiState.isWriteReviewVisible) {
        WriteReviewBottomSheet(
            rating = uiState.writeReviewRating,
            title = uiState.writeReviewTitle,
            comment = uiState.writeReviewComment,
            isSubmitting = uiState.isSubmittingReview,
            error = uiState.submitReviewError,
            onRatingChange = { viewModel.setWriteReviewRating(it) },
            onTitleChange = { viewModel.setWriteReviewTitle(it) },
            onCommentChange = { viewModel.setWriteReviewComment(it) },
            onSubmit = { viewModel.submitReview() },
            onDismiss = { viewModel.hideWriteReview() }
        )
    }
}

@Composable
private fun RatingSummarySection(
    averageRating: Float,
    totalReviews: Int,
    breakdown: com.dakkho.android.domain.model.RatingBreakdown
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Left side: big average rating + stars
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (averageRating > 0) String.format("%.1f", averageRating) else "-",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Mini star display
            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (averageRating >= i.toFloat()) Icons.Filled.Star
                        else if (averageRating >= i.toFloat() - 0.5f) Icons.Filled.Star
                        else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (averageRating >= i.toFloat() - 0.5f) Warning
                        else MaterialTheme.colorScheme.outline
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$totalReviews reviews",
                style = MaterialTheme.typography.labelSmall,
                color = Neutral400
            )
        }

        // Right side: breakdown bars
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (star in 5 downTo 1) {
                RatingBar(
                    star = star,
                    percentage = breakdown.getPercentage(star),
                    count = when (star) {
                        5 -> breakdown.star5
                        4 -> breakdown.star4
                        3 -> breakdown.star3
                        2 -> breakdown.star2
                        else -> breakdown.star1
                    }
                )
            }
        }
    }
}

@Composable
private fun RatingBar(
    star: Int,
    percentage: Float,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$star",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(12.dp)
        )
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = Warning
        )
        Spacer(modifier = Modifier.width(4.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        ) {
            // Background
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(3.dp)
            ) {}
            // Foreground
            val animatedWidth by animateColorAsState(
                targetValue = if (percentage > 0) SkyBlue else Color.Transparent,
                label = "barColor"
            )
            Surface(
                modifier = Modifier.fillMaxWidth(percentage / 100f),
                color = animatedWidth,
                shape = RoundedCornerShape(3.dp)
            ) {}
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall,
            color = Neutral400,
            modifier = Modifier.width(24.dp)
        )
    }
}

@Composable
private fun RatingFilterChips(
    selectedFilter: Int?,
    onFilterSelected: (Int?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == null,
            onClick = { onFilterSelected(null) },
            label = { Text("All", style = MaterialTheme.typography.labelMedium) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SkyBlue,
                selectedLabelColor = Color.White
            )
        )
        for (star in 5 downTo 1) {
            FilterChip(
                selected = selectedFilter == star,
                onClick = { onFilterSelected(star) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$star", style = MaterialTheme.typography.labelMedium)
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = if (selectedFilter == star) Color.White else Warning
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SkyBlue,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // User info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            AsyncImage(
                model = review.userAvatar,
                contentDescription = "User avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.userName ?: "Anonymous",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Rating stars
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (review.rating >= i.toFloat()) Icons.Filled.Star
                            else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = if (review.rating >= i.toFloat()) Warning
                            else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = review.createdAt ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                }
            }
        }

        // Title (if present)
        if (!review.title.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = review.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Comment
        if (!review.comment.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WriteReviewBottomSheet(
    rating: Float,
    title: String,
    comment: String,
    isSubmitting: Boolean,
    error: String?,
    onRatingChange: (Float) -> Unit,
    onTitleChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Write a Review",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive star rating
            Text(
                text = "Your Rating",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 1..5) {
                    val isSelected = rating >= i.toFloat()
                    IconButton(
                        onClick = { onRatingChange(i.toFloat()) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Rate $i star",
                            modifier = Modifier.size(32.dp),
                            tint = if (isSelected) Warning else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Review Title (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Comment input
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                label = { Text("Your Review (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = MaterialTheme.shapes.medium
            )

            // Error message
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(enabled = !isSubmitting && rating > 0f) { onSubmit() },
                color = if (rating > 0f) SkyBlue else MaterialTheme.colorScheme.outlineVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Submit Review",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = if (rating > 0f) Color.White else Neutral400
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyReviewsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No reviews yet",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Neutral400
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Be the first to review this course!",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral400
        )
    }
}
