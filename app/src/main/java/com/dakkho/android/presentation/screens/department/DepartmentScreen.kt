package com.dakkho.android.presentation.screens.department

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.Technology
import com.dakkho.android.presentation.components.CourseCardGrid
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.home.FeaturedInstructors
import com.dakkho.android.presentation.components.home.SectionHeader
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.SkyBlueLight
import com.dakkho.android.presentation.theme.createDiagonalGradient

/**
 * Department Screen — template composable for any department.
 * Fully dynamic: receives a slug and loads everything from the API.
 * No hardcoded department data.
 */
@Composable
fun DepartmentScreen(
    slug: String,
    onBackClick: () -> Unit,
    onCourseClick: (String) -> Unit,
    onInstructorClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: DepartmentViewModel = hiltViewModel()
) {
    val department by viewModel.department.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val instructors by viewModel.instructors.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(slug) {
        viewModel.initialize(slug)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Top Bar ──
        DepartmentTopBar(
            departmentName = department?.name ?: "Department",
            onBackClick = onBackClick,
            onSearchClick = onSearchClick
        )

        if (isLoading && department == null) {
            // Full screen loading skeleton
            DepartmentLoadingSkeleton()
        } else if (error != null && department == null) {
            // Error state
            EmptyState(
                title = "Something went wrong",
                subtitle = error ?: "Failed to load department",
                actionText = "Retry",
                onAction = { viewModel.retry() }
            )
        } else {
            // ── Department Hero ──
            department?.let { dept ->
                DepartmentHero(department = dept)

                // ── Stats Row ──
                DepartmentStatsRow(department = dept)

                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

                // ── Semester Navigation ──
                SemesterTabs(
                    totalSemesters = dept.semesterCount,
                    selectedSemester = selectedSemester,
                    onSemesterSelected = { viewModel.selectSemester(it) }
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

                // ── Courses Grid ──
                if (courses.isEmpty() && !isLoading) {
                    EmptyState(
                        title = "No courses yet",
                        subtitle = "No courses available for ${dept.name} yet. Check back later!",
                        iconRes = android.R.drawable.ic_menu_gallery
                    )
                } else {
                    SectionHeader(
                        title = "Courses",
                        showSeeAll = courses.size > 6,
                        onSeeAllClick = { /* TODO: Navigate to full course list filtered */ }
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    CourseCardGrid(
                        courses = courses,
                        onCourseClick = onCourseClick,
                        modifier = Modifier.padding(horizontal = DesignToken.Space.dp16)
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

                // ── Featured Instructors ──
                if (instructors.isNotEmpty()) {
                    FeaturedInstructors(
                        instructors = instructors,
                        onInstructorClick = onInstructorClick,
                        onSeeAllClick = { /* TODO: Navigate to instructor list filtered */ }
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
            }
        }
    }
}

// ── Top Bar ──

@Composable
private fun DepartmentTopBar(
    departmentName: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = DesignToken.Space.dp4, vertical = DesignToken.Space.dp4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = departmentName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Department Hero Section ──

@Composable
private fun DepartmentHero(department: Technology) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(bottomStart = DesignToken.Space.dp24, bottomEnd = DesignToken.Space.dp24))
    ) {
        // Background gradient or banner image
        if (department.bannerUrl != null) {
            AsyncImage(
                model = department.bannerUrl,
                contentDescription = department.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Dark overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color.Transparent,
                            DeepBlue.copy(alpha = 0.85f)
                        )
                    ))
            )
        } else {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawRect(
                            brush = createDiagonalGradient(
                                DeepBlue,
                                SkyBlue.copy(alpha = 0.7f)
                            )
                        )
                    }
            )
        }

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(DesignToken.Space.dp24),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Department badge (short code)
            if (department.shortCode.isNotBlank()) {
                Text(
                    text = department.shortCode,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Green,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .drawBehind { drawRect(Green.copy(alpha = 0.2f)) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            }

            // Department name
            Text(
                text = department.name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = androidx.compose.ui.graphics.Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Description
            if (department.description != null) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                Text(
                    text = department.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ── Stats Row ──

@Composable
private fun DepartmentStatsRow(department: Technology) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.Space.dp16),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatBadge(
            label = "Courses",
            value = "${department.courseCount}",
            color = SkyBlue
        )
        StatBadge(
            label = "Instructors",
            value = "${department.instructorCount}",
            color = Green
        )
        StatBadge(
            label = "Students",
            value = "${department.studentCount}",
            color = DeepBlue
        )
        StatBadge(
            label = "Semesters",
            value = "${department.semesterCount}",
            color = SkyBlueLight
        )
    }
}

@Composable
private fun StatBadge(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    GlassCard(
        modifier = Modifier.width(78.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Neutral500
            )
        }
    }
}

// ── Semester Tabs ──

@Composable
private fun SemesterTabs(
    totalSemesters: Int,
    selectedSemester: Int,
    onSemesterSelected: (Int) -> Unit
) {
    if (totalSemesters <= 0) return

    LazyRow(
        contentPadding = PaddingValues(horizontal = DesignToken.Space.dp16),
        horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
    ) {
        // "All" chip
        item {
            FilterChip(
                selected = selectedSemester == 0,
                onClick = { onSemesterSelected(0) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SkyBlue,
                    selectedLabelColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }

        // Semester 1..N chips
        items(totalSemesters) { index ->
            val semNum = index + 1
            FilterChip(
                selected = selectedSemester == semNum,
                onClick = { onSemesterSelected(semNum) },
                label = { Text("Sem $semNum") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SkyBlue,
                    selectedLabelColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    }
}

// ── Loading Skeleton ──

@Composable
private fun DepartmentLoadingSkeleton() {
    Column(
        modifier = Modifier.padding(DesignToken.Space.dp16)
    ) {
        // Hero skeleton
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(DesignToken.Space.dp24))
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Stats skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) {
                ShimmerEffect(
                    modifier = Modifier
                        .width(78.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(DesignToken.Space.dp12))
                )
            }
        }
        Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

        // Semester tabs skeleton
        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
        ) {
            repeat(5) {
                ShimmerEffect(
                    modifier = Modifier
                        .width(70.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(DesignToken.Space.dp16))
                )
            }
        }
        Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

        // Course grid skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            repeat(2) {
                ShimmerEffect(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                        .clip(RoundedCornerShape(DesignToken.Space.dp12))
                )
            }
        }
    }
}
