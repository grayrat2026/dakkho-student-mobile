package com.dakkho.android.presentation.screens.coursedetail

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.components.coursedetail.CourseAnnouncementsTab
import com.dakkho.android.presentation.components.coursedetail.CourseCurriculumTab
import com.dakkho.android.presentation.components.coursedetail.CourseHeroSection
import com.dakkho.android.presentation.components.coursedetail.CourseOverviewTab
import com.dakkho.android.presentation.components.coursedetail.CourseQnATab
import com.dakkho.android.presentation.components.coursedetail.CourseReviewsTab
import com.dakkho.android.presentation.components.coursedetail.EnrollBottomSheet
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue
import kotlinx.coroutines.launch

private val COURSE_TABS = listOf("Overview", "Curriculum", "Reviews", "Q&A", "Announcements")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    onBackClick: () -> Unit,
    onNavigateToVideo: (videoId: String, courseId: String) -> Unit,
    onNavigateToInstructor: (instructorId: String) -> Unit,
    viewModel: CourseDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext()
    val scope = rememberCoroutineScope()

    LaunchedEffect(courseId) {
        viewModel.initialize(courseId)
    }

    // Handle course not found
    LaunchedEffect(uiState.error) {
        if (uiState.error == "Course not found" && !uiState.isLoading) {
            onBackClick()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (uiState.course != null) {
                        Text(
                            text = uiState.course!!.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1
                        )
                    }
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
                    if (uiState.course != null) {
                        IconButton(onClick = {
                            // Share course deep link
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    uiState.course!!.title
                                )
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out this course: ${uiState.course!!.title}\n" +
                                            "dakkho://course/${uiState.course!!.id}"
                                )
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, "Share Course")
                            )
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Share,
                                contentDescription = "Share"
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        bottomBar = {
            if (uiState.course != null) {
                EnrollBottomBar(
                    enrollState = uiState.getEnrollButtonState(),
                    price = uiState.course!!.price,
                    discountedPrice = uiState.course!!.discountedPrice,
                    isEnrolling = uiState.isEnrolling,
                    onEnrollClick = { viewModel.showEnrollBottomSheet() },
                    onContinueClick = {
                        // Navigate to first incomplete video or curriculum
                        uiState.course!!.let { course ->
                            val firstLesson = uiState.curriculum?.sections
                                ?.firstOrNull()?.lessons?.firstOrNull()
                            if (firstLesson != null && firstLesson.videoUrl != null) {
                                onNavigateToVideo(firstLesson.id, course.id)
                            }
                        }
                    }
                )
            }
        }
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
        } else if (uiState.error != null && uiState.course == null) {
            // Error state
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
        } else if (uiState.course != null) {
            CourseDetailContent(
                uiState = uiState,
                innerPadding = innerPadding,
                onLessonClick = { lesson ->
                    if (lesson.videoUrl != null) {
                        onNavigateToVideo(lesson.id, uiState.course!!.id)
                    }
                }
            )
        }
    }

    // Enroll bottom sheet
    if (uiState.isBottomSheetVisible) {
        EnrollBottomSheet(
            packages = uiState.packages,
            isEnrolling = uiState.isEnrolling,
            onDismiss = { viewModel.hideEnrollBottomSheet() },
            onEnrollClick = { packageId -> viewModel.onEnrollClick(packageId) }
        )
    }
}

@Composable
private fun CourseDetailContent(
    uiState: CourseDetailUiState,
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    onLessonClick: (Lesson) -> Unit
) {
    val course = uiState.course ?: return
    val pagerState = rememberPagerState(pageCount = { COURSE_TABS.size })
    val scope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // Hero section
        CourseHeroSection(
            title = course.title,
            thumbnailUrl = course.thumbnailUrl,
            rating = course.rating,
            reviewCount = course.reviewCount,
            enrollmentCount = course.enrollmentCount,
            durationHours = course.durationHours,
            level = course.level,
            instructorName = course.instructorName,
            instructorAvatar = course.instructorAvatar,
            technology = course.technology
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tab row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = SkyBlue,
            divider = {}
        ) {
            COURSE_TABS.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    selectedContentColor = SkyBlue,
                    unselectedContentColor = Neutral400
                )
            }
        }

        // Tab pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { page ->
            LaunchedEffect(page) {
                selectedTabIndex = page
            }

            when (page) {
                0 -> CourseOverviewTab(
                    description = course.description,
                    whatYouLearn = course.whatYouLearn,
                    requirements = course.requirements,
                    targetAudience = course.targetAudience
                )

                1 -> CourseCurriculumTab(
                    curriculum = uiState.curriculum,
                    isEnrolled = uiState.isEnrolled,
                    onLessonClick = onLessonClick
                )

                2 -> CourseReviewsTab(reviews = uiState.reviews)

                3 -> CourseQnATab()

                4 -> CourseAnnouncementsTab()
            }
        }
    }
}

@Composable
private fun EnrollBottomBar(
    enrollState: EnrollButtonState,
    price: Double?,
    discountedPrice: Double?,
    isEnrolling: Boolean,
    onEnrollClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val isFree = price == null || price == 0.0

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Price display
            Column {
                if (enrollState == EnrollButtonState.NOT_ENROLLED) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (discountedPrice != null && price != null && discountedPrice < price) {
                            Text(
                                text = "৳${discountedPrice.toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = DeepBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "৳${price.toInt()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral400
                            )
                        } else if (!isFree) {
                            Text(
                                text = "৳${price?.toInt() ?: 0}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = DeepBlue
                            )
                        } else {
                            Text(
                                text = "Free",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Green
                            )
                        }
                    }
                }
            }

            // Enroll/Continue button
            when (enrollState) {
                EnrollButtonState.NOT_ENROLLED -> {
                    GradientButton(
                        text = if (isFree) "Enroll Now" else "Buy Now",
                        onClick = onEnrollClick,
                        isLoading = isEnrolling,
                        modifier = Modifier.weight(0.6f)
                    )
                }

                EnrollButtonState.JUST_ENROLLED -> {
                    OutlinedButton(
                        onClick = onContinueClick,
                        modifier = Modifier.weight(0.6f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Enrolled - Continue",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = SkyBlue
                        )
                    }
                }

                EnrollButtonState.CONTINUE -> {
                    androidx.compose.material3.Button(
                        onClick = onContinueClick,
                        modifier = Modifier.weight(0.6f),
                        shape = MaterialTheme.shapes.medium,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Green
                        )
                    ) {
                        Text(
                            text = "Continue",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
