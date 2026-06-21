package com.dakkho.android.presentation.screens.instructorcourses

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.Course
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorCoursesScreen(
    onBackClick: () -> Unit,
    onCourseClick: (String) -> Unit,
    viewModel: InstructorCoursesViewModel = hiltViewModel()
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
                            text = "${uiState.instructorName} Courses",
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
            when {
                uiState.isLoading -> {
                    ShimmerInstructorCourses(modifier = Modifier.padding(paddingValues))
                }
                uiState.error != null && uiState.courses.isEmpty() -> {
                    EmptyState(
                        icon = painterResource(id = android.R.drawable.ic_dialog_alert),
                        title = "Could not load courses",
                        subtitle = uiState.error,
                        onActionClick = { viewModel.loadCourses() },
                        actionLabel = "Retry",
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                uiState.courses.isEmpty() -> {
                    EmptyState(
                        icon = painterResource(id = android.R.drawable.ic_menu_gallery),
                        title = "No courses yet",
                        subtitle = "This instructor hasn't published any courses",
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
                        ),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
                    ) {
                        item {
                            Text(
                                text = "${uiState.total} course${if (uiState.total != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral500
                            )
                            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                        }

                        items(uiState.courses, key = { it.id }) { course ->
                            InstructorCourseCard(
                                course = course,
                                onClick = { onCourseClick(course.id) }
                            )
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
                }
            }
        }
    }
}

@Composable
fun InstructorCourseCard(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            if (course.thumbnailUrl != null) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SkyBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Course",
                        tint = SkyBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            // Course info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!course.level.isNullOrBlank()) {
                        Text(
                            text = course.level.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral500
                        )
                    }

                    course.rating?.let { rating ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.btn_star_big_on),
                                contentDescription = "Rating",
                                tint = Warning,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format("%.1f", rating),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    course.enrollmentCount?.let { count ->
                        Text(
                            text = "${formatStudentCount(count)} students",
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp2))
                val priceText = when {
                    course.price == null || course.price == 0.0 -> "Free"
                    course.discountedPrice != null && course.discountedPrice < course.price -> "৳${course.discountedPrice.toInt()}"
                    else -> "৳${course.price.toInt()}"
                }
                Text(
                    text = priceText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (priceText == "Free") Color(0xFF10B981) else SkyBlue
                )
            }
        }
    }
}

@Composable
fun ShimmerInstructorCourses(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DesignToken.Space.dp16)
    ) {
        repeat(5) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = DesignToken.Space.dp4)
            )
        }
    }
}

private fun formatStudentCount(count: Int): String {
    return when {
        count >= 1000 -> String.format("%.1fk", count / 1000f)
        else -> count.toString()
    }
}
