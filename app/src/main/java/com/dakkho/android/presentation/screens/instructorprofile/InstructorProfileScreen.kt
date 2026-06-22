package com.dakkho.android.presentation.screens.instructorprofile

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.dakkho.android.domain.model.SocialLinks
import com.dakkho.android.domain.model.InstructorDetail
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import androidx.compose.foundation.layout.RowScope
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorProfileScreen(
    onBackClick: () -> Unit,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToInstructorCourses: (String, String) -> Unit = { _, _ -> },
    onNavigateToInstructorReviews: (String, String, Float, Int) -> Unit = { _, _, _, _ -> },
    onNavigateToInstructorSchedule: (String, String) -> Unit = { _, _ -> },
    onNavigateToInstructorContact: (String, String) -> Unit = { _, _ -> },
    viewModel: InstructorProfileViewModel = hiltViewModel()
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
                            text = uiState.instructor?.name ?: "Instructor Profile",
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
                    ShimmerInstructorProfile(modifier = Modifier.padding(paddingValues))
                }
                uiState.error != null && uiState.instructor == null -> {
                    EmptyState(
                        iconRes = android.R.drawable.ic_dialog_alert,
                        title = "Could not load profile",
                        subtitle = uiState.error ?: "Unknown error",
                        onAction = { viewModel.loadInstructor() },
                        actionText = "Retry",
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                uiState.instructor != null -> {
                    InstructorProfileContent(
                        instructor = uiState.instructor!!,
                        courses = uiState.courses,
                        selectedTab = uiState.selectedTab,
                        onSelectTab = viewModel::selectTab,
                        onCourseClick = onNavigateToCourse,
                        onNavigateToInstructorCourses = onNavigateToInstructorCourses,
                        onNavigateToInstructorReviews = onNavigateToInstructorReviews,
                        onNavigateToInstructorSchedule = onNavigateToInstructorSchedule,
                        onNavigateToInstructorContact = onNavigateToInstructorContact,
                        isLoadingCourses = uiState.isLoadingCourses,
                        hasMoreCourses = uiState.hasMoreCourses,
                        onLoadMoreCourses = viewModel::loadMoreCourses,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun InstructorProfileContent(
    instructor: InstructorDetail,
    courses: List<Course>,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onCourseClick: (String) -> Unit,
    onNavigateToInstructorCourses: (String, String) -> Unit,
    onNavigateToInstructorReviews: (String, String, Float, Int) -> Unit,
    onNavigateToInstructorSchedule: (String, String) -> Unit,
    onNavigateToInstructorContact: (String, String) -> Unit,
    isLoadingCourses: Boolean,
    hasMoreCourses: Boolean,
    onLoadMoreCourses: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = InstructorProfileTabs.NAMES
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && selectedTab == InstructorProfileTabs.COURSES) {
            onLoadMoreCourses()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp8)
    ) {
        // ── Cover + Avatar Header ──
        item {
            InstructorProfileHeader(instructor = instructor)
        }

        // ── Stats Row ──
        item {
            InstructorStatsRow(instructor = instructor)
        }

        // ── Bio Section (always visible, truncated in About tab) ──
        if (!instructor.bio.isNullOrBlank() && selectedTab == InstructorProfileTabs.ABOUT) {
            item {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                InstructorBioSection(instructor = instructor)
            }
        }

        // ── Social Links ──
        if (instructor.socialLinks.hasAny && selectedTab == InstructorProfileTabs.ABOUT) {
            item {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                InstructorSocialLinks(socialLinks = instructor.socialLinks)
            }
        }

        // ── Scrollable Tab Row ──
        item {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                edgePadding = 0.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { onSelectTab(index) },
                        text = {
                            Text(
                                text = when (index) {
                                    InstructorProfileTabs.COURSES -> "$title (${instructor.courseCount})"
                                    InstructorProfileTabs.REVIEWS -> "$title"
                                    else -> title
                                },
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    )
                }
            }
        }

        // ── Tab Content ──
        when (selectedTab) {
            InstructorProfileTabs.ABOUT -> {
                // About Tab
                if (!instructor.bio.isNullOrBlank()) {
                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                        Text(
                            text = instructor.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            lineHeight = 22.sp
                        )
                    }
                }

                if (!instructor.specialization.isNullOrBlank()) {
                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                        Column {
                            Text(
                                text = "Specialization",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                            Text(
                                text = instructor.specialization,
                                style = MaterialTheme.typography.bodyMedium,
                                color = SkyBlue
                            )
                        }
                    }
                }

                // Joined date
                if (!instructor.createdAt.isNullOrBlank()) {
                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_today),
                                contentDescription = "Joined",
                                tint = Neutral500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Space.dp4))
                            Text(
                                text = "Joined ${formatDate(instructor.createdAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral500
                            )
                        }
                    }
                }
            }

            InstructorProfileTabs.COURSES -> {
                // Courses Tab
                if (courses.isEmpty() && !isLoadingCourses) {
                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
                        EmptyState(
                            iconRes = android.R.drawable.ic_menu_gallery,
                            title = "No courses yet",
                            subtitle = "This instructor hasn't published any courses"
                        )
                    }
                } else {
                    // "View All" link
                    if (instructor.courseCount > courses.size) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateToInstructorCourses(instructor.id, instructor.name)
                                    }
                                    .padding(vertical = DesignToken.Space.dp4),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "View All ${instructor.courseCount} Courses",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = SkyBlue
                                )
                            }
                        }
                    }

                    items(courses, key = { it.id }) { course ->
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                        InstructorCourseCard(
                            course = course,
                            onClick = { onCourseClick(course.id) }
                        )
                    }

                    if (isLoadingCourses) {
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

            InstructorProfileTabs.REVIEWS -> {
                // Reviews Tab - preview with "View All" link
                item {
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.btn_star_big_on),
                                contentDescription = "Rating",
                                tint = Warning,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", instructor.rating),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "View All Reviews",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = SkyBlue,
                            modifier = Modifier.clickable {
                                onNavigateToInstructorReviews(
                                    instructor.id,
                                    instructor.name,
                                    instructor.rating,
                                    instructor.studentCount // approximate review count
                                )
                            }
                        )
                    }
                }
            }

            InstructorProfileTabs.SCHEDULE -> {
                // Schedule Tab - preview with link to full schedule
                item {
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "View upcoming live classes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                        OutlinedButton(
                            onClick = {
                                onNavigateToInstructorSchedule(instructor.id, instructor.name)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SkyBlue
                            )
                        ) {
                            Text("View Schedule")
                        }
                    }
                }
            }

            InstructorProfileTabs.CONTACT -> {
                // Contact Tab - preview with link to full contact info
                item {
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!instructor.email.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = DesignToken.Space.dp4)
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_dialog_email),
                                    contentDescription = "Email",
                                    tint = SkyBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(DesignToken.Space.dp4))
                                Text(
                                    text = instructor.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (instructor.socialLinks.hasAny) {
                            Text(
                                text = "${instructor.socialLinks.youtube?.let { "YouTube, " } ?: ""}${instructor.socialLinks.github?.let { "GitHub, " } ?: ""}${instructor.socialLinks.linkedin?.let { "LinkedIn" } ?: ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                        OutlinedButton(
                            onClick = {
                                onNavigateToInstructorContact(instructor.id, instructor.name)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SkyBlue
                            )
                        ) {
                            Text("View All Contact Info")
                        }
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

@Composable
fun InstructorProfileHeader(
    instructor: InstructorDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cover image or gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(DesignToken.Space.dp12)),
            contentAlignment = Alignment.Center
        ) {
            if (instructor.coverUrl != null) {
                AsyncImage(
                    model = instructor.coverUrl,
                    contentDescription = "Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Gradient fallback
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    SkyBlue.copy(alpha = 0.3f),
                                    SkyBlue.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            )
                        )
                )
            }

            // Avatar overlapping cover
            // Avatar is rendered below the cover in the Column
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        // Avatar
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            if (instructor.avatarUrl != null) {
                AsyncImage(
                    model = instructor.avatarUrl,
                    contentDescription = instructor.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SkyBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = instructor.name.take(1).uppercase(),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = SkyBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        // Name
        Text(
            text = instructor.name,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Title / Specialization
        if (instructor.title != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = instructor.title,
                style = MaterialTheme.typography.bodyMedium,
                color = SkyBlue
            )
        }

        // Rating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = DesignToken.Space.dp4)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.btn_star_big_on),
                contentDescription = "Rating",
                tint = Warning,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.1f", instructor.rating),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun InstructorStatsRow(
    instructor: InstructorDetail,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = DesignToken.Space.dp16),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            label = "Courses",
            value = "${instructor.courseCount}"
        )
        StatItem(
            label = "Students",
            value = formatStudentCount(instructor.studentCount)
        )
        StatItem(
            label = "Rating",
            value = String.format("%.1f", instructor.rating)
        )
    }
}

@Composable
fun RowScope.StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.weight(1f)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = DesignToken.Space.dp12),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = SkyBlue
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

@Composable
fun InstructorBioSection(
    instructor: InstructorDetail,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "About",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
        val bioText = instructor.bio ?: ""
        val isLong = bioText.length > 200
        Text(
            text = if (isLong) bioText.take(200) + "..." else bioText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            lineHeight = 20.sp
        )
        if (isLong) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
            Text(
                text = "Read more in About tab",
                style = MaterialTheme.typography.labelMedium,
                color = SkyBlue
            )
        }
    }
}

@Composable
fun InstructorSocialLinks(
    socialLinks: SocialLinks,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Social Links",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
        ) {
            socialLinks.youtube?.let { url ->
                item { SocialLinkChip(label = "YouTube", url = url) }
            }
            socialLinks.github?.let { url ->
                item { SocialLinkChip(label = "GitHub", url = url) }
            }
            socialLinks.facebook?.let { url ->
                item { SocialLinkChip(label = "Facebook", url = url) }
            }
            socialLinks.linkedin?.let { url ->
                item { SocialLinkChip(label = "LinkedIn", url = url) }
            }
            socialLinks.website?.let { url ->
                item { SocialLinkChip(label = "Website", url = url) }
            }
        }
    }
}

@Composable
fun SocialLinkChip(
    label: String,
    url: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = DesignToken.Space.dp12, vertical = DesignToken.Space.dp8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_share),
                contentDescription = label,
                tint = SkyBlue,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(DesignToken.Space.dp4))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = SkyBlue
            )
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
                    // Level badge
                    if (!course.level.isNullOrBlank()) {
                        Text(
                            text = course.level.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral500
                        )
                    }

                    // Rating
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

                    // Student count
                    course.enrollmentCount?.let { count ->
                        Text(
                            text = "${formatStudentCount(count)} students",
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral500
                        )
                    }
                }

                // Price
                Spacer(modifier = Modifier.height(DesignToken.Space.dp2))
                val priceText = when {
                    course.price == null || course.price == 0.0 -> "Free"
                    course.discountedPrice != null && course.discountedPrice < course.price -> {
                        "৳${course.discountedPrice.toInt()}"
                    }
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
fun ShimmerInstructorProfile(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DesignToken.Space.dp16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(DesignToken.Space.dp12))
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
        ShimmerEffect(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
        ShimmerEffect(
            modifier = Modifier
                .width(180.dp)
                .height(24.dp)
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
        ShimmerEffect(
            modifier = Modifier
                .width(120.dp)
                .height(16.dp)
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                ShimmerEffect(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .padding(horizontal = DesignToken.Space.dp4)
                )
            }
        }
        Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
        repeat(3) {
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

private fun formatDate(dateStr: String): String {
    return try {
        // Handle ISO date format: 2026-01-15T10:30:00 or 2026-01-15
        val datePart = dateStr.substringBefore("T").substringBefore(" ")
        val parts = datePart.split("-")
        if (parts.size == 3) {
            val months = listOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val monthIndex = parts[1].toIntOrNull() ?: 1
            "${months.getOrElse(monthIndex) { "Jan" }} ${parts[0]}"
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}
