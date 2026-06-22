package com.dakkho.android.presentation.screens.instructorreviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.RatingBreakdown
import com.dakkho.android.domain.model.Review
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorReviewsScreen(
    onBackClick: () -> Unit,
    viewModel: InstructorReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    AnimatedPage {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "${uiState.instructorName} Reviews",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_revert),
                                contentDescription = "Back"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            val errorMsg = uiState.error
            when {
                uiState.isLoading -> {
                    ShimmerReviews(modifier = Modifier.padding(paddingValues))
                }
                errorMsg != null && uiState.reviews.isEmpty() -> {
                    EmptyState(
                        iconRes = android.R.drawable.ic_dialog_alert,
                        title = "Could not load reviews",
                        subtitle = errorMsg,
                        actionText = "Retry",
                        onAction = { viewModel.loadReviews() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                else -> {
                    val listState = rememberLazyListState()
                    val shouldLoadMore by remember {
                        derivedStateOf {
                            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            lastVisible >= listState.layoutInfo.totalItemsCount - 3
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(
                            horizontal = DesignToken.Space.dp16,
                            vertical = DesignToken.Space.dp8
                        )
                    ) {
                        // ── Rating Summary Header ──
                        item {
                            RatingSummaryHeader(
                                averageRating = uiState.averageRating,
                                totalReviews = uiState.totalReviews,
                                breakdown = uiState.ratingBreakdown,
                                selectedFilter = uiState.selectedRatingFilter,
                                onFilterChange = { viewModel.setRatingFilter(it) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                        }

                        // ── Review List ──
                        if (uiState.reviews.isEmpty() && !uiState.isLoadingMore) {
                            item {
                                Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
                                EmptyState(
                                    iconRes = android.R.drawable.ic_menu_agenda,
                                    title = "No reviews yet",
                                    subtitle = if (uiState.selectedRatingFilter != null)
                                        "No ${uiState.selectedRatingFilter}-star reviews found"
                                    else
                                        "This instructor hasn't received any reviews yet"
                                )
                            }
                        } else {
                            items(uiState.reviews, key = { it.id }) { review ->
                                ReviewCard(review = review)
                                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                            }

                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(DesignToken.Space.dp16),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp,
                                            color = SkyBlue
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom spacer
                        item {
                            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingSummaryHeader(
    averageRating: Float,
    totalReviews: Int,
    breakdown: RatingBreakdown,
    selectedFilter: Int?,
    onFilterChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp16)
        ) {
            // Top row: big rating number + bar chart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Big average rating
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    Text(
                        text = String.format("%.1f", averageRating),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$totalReviews review${if (totalReviews != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral500
                    )
                }

                Spacer(modifier = Modifier.width(DesignToken.Space.dp16))

                // Right: 5-bar chart using Compose layout
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val starLabels = listOf(5, 4, 3, 2, 1)
                    val starCounts = listOf(breakdown.star5, breakdown.star4, breakdown.star3, breakdown.star2, breakdown.star1)
                    val maxCount = starCounts.maxOrNull()?.coerceAtLeast(1) ?: 1

                    starLabels.forEachIndexed { index, star ->
                        val count = starCounts[index]
                        val fraction = if (maxCount > 0) count.toFloat() / maxCount else 0f

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${star}★",
                                style = MaterialTheme.typography.labelSmall,
                                color = Neutral500,
                                modifier = Modifier.width(28.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Neutral400.copy(alpha = 0.2f))
                            ) {
                                if (fraction > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Warning)
                                    )
                                }
                            }

                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelSmall,
                                color = Neutral500,
                                modifier = Modifier
                                    .width(36.dp)
                                    .padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            // Filter chips
            if (totalReviews > 0) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { onFilterChange(null) },
                        label = { Text("All", style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlue.copy(alpha = 0.15f),
                            selectedLabelColor = SkyBlue
                        )
                    )
                    listOf(5, 4, 3, 2, 1).forEach { star ->
                        val count = when (star) {
                            5 -> breakdown.star5
                            4 -> breakdown.star4
                            3 -> breakdown.star3
                            2 -> breakdown.star2
                            else -> breakdown.star1
                        }
                        if (count > 0) {
                            FilterChip(
                                selected = selectedFilter == star,
                                onClick = { onFilterChange(if (selectedFilter == star) null else star) },
                                label = { Text("$star★", style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SkyBlue.copy(alpha = 0.15f),
                                    selectedLabelColor = SkyBlue
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp12)
        ) {
            // Top row: avatar + name + date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                if (review.userAvatar != null) {
                    AsyncImage(
                        model = review.userAvatar,
                        contentDescription = review.userName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SkyBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (review.userName ?: "U").take(1).uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = SkyBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.userName ?: "Anonymous",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (review.createdAt != null) {
                        Text(
                            text = formatReviewDate(review.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral500
                        )
                    }
                }

                // Star rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { i ->
                        Text(
                            text = if (i < review.rating.toInt()) "★" else "☆",
                            style = MaterialTheme.typography.bodySmall,
                            color = Warning
                        )
                    }
                }
            }

            // Title (if present)
            if (!review.title.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
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
                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun ShimmerReviews(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DesignToken.Space.dp16)
    ) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
        repeat(4) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = DesignToken.Space.dp4)
            )
        }
    }
}

private fun formatReviewDate(dateStr: String): String {
    return try {
        val datePart = dateStr.substringBefore("T").substringBefore(" ")
        val parts = datePart.split("-")
        if (parts.size == 3) {
            val months = listOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val monthIndex = parts[1].toIntOrNull() ?: 1
            "${months.getOrElse(monthIndex) { "Jan" }} ${parts[2]}, ${parts[0]}"
        } else dateStr
    } catch (e: Exception) { dateStr }
}
