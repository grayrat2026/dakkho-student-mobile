package com.dakkho.android.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dakkho.android.presentation.screens.about.AboutScreen
import com.dakkho.android.presentation.screens.announcements.AnnouncementsScreen
import com.dakkho.android.presentation.screens.assignment.AssignmentScreen
import com.dakkho.android.presentation.screens.auth.ForgotPasswordScreen
import com.dakkho.android.presentation.screens.auth.LoginScreen
import com.dakkho.android.presentation.screens.auth.SignupScreen
import com.dakkho.android.presentation.screens.category.CategoryScreen
import com.dakkho.android.presentation.screens.coursedetail.CourseDetailScreen
import com.dakkho.android.presentation.screens.curriculum.CourseCurriculumScreen
import com.dakkho.android.presentation.screens.explore.ExploreScreen
import com.dakkho.android.presentation.screens.home.HomeScreen
import com.dakkho.android.presentation.screens.notifications.NotificationDetailScreen
import com.dakkho.android.presentation.screens.notifications.NotificationsScreen
import com.dakkho.android.presentation.screens.profile.ProfileScreen
import com.dakkho.android.presentation.screens.qna.QnAScreen
import com.dakkho.android.presentation.screens.resources.ResourcesScreen
import com.dakkho.android.presentation.screens.reviews.CourseReviewsScreen
import com.dakkho.android.presentation.screens.search.SearchScreen
import com.dakkho.android.presentation.screens.notes.CourseNotesScreen
import com.dakkho.android.presentation.screens.quizzes.CourseQuizzesScreen
import com.dakkho.android.presentation.screens.progress.CourseProgressScreen
import com.dakkho.android.presentation.screens.instructorlist.InstructorListScreen
import com.dakkho.android.presentation.screens.instructorprofile.InstructorProfileScreen
import com.dakkho.android.presentation.screens.instructorcourses.InstructorCoursesScreen
import com.dakkho.android.presentation.screens.instructorreviews.InstructorReviewsScreen
import com.dakkho.android.presentation.screens.instructorschedule.InstructorScheduleScreen
import com.dakkho.android.presentation.screens.instructorcontact.InstructorContactScreen
import com.dakkho.android.presentation.screens.department.DepartmentListScreen
import com.dakkho.android.presentation.screens.department.DepartmentScreen
import com.dakkho.android.presentation.screens.semester.SemesterScreen
import com.dakkho.android.presentation.screens.videoplayer.VideoPlayerScreen
import com.dakkho.android.presentation.screens.watchhistory.WatchHistoryScreen
import com.dakkho.android.presentation.screens.downloads.DownloadsScreen
import com.dakkho.android.presentation.screens.certificates.CertificatesScreen
import com.dakkho.android.presentation.screens.livesessions.LiveSessionsScreen
import com.dakkho.android.presentation.screens.achievements.AchievementsScreen
import com.dakkho.android.presentation.screens.discussionforum.DiscussionForumScreen
import com.dakkho.android.presentation.screens.editprofile.EditProfileScreen
import com.dakkho.android.presentation.screens.changepassword.ChangePasswordScreen
import com.dakkho.android.presentation.screens.learningstats.LearningStatsScreen
import com.dakkho.android.presentation.screens.subscription.SubscriptionScreen
import com.dakkho.android.presentation.screens.referral.ReferralScreen
import com.dakkho.android.presentation.screens.bookmarks.BookmarksScreen
import com.dakkho.android.presentation.screens.settings.SettingsScreen
import com.dakkho.android.presentation.screens.themesettings.ThemeSettingsScreen
import com.dakkho.android.presentation.screens.downloadsettings.DownloadSettingsScreen
import com.dakkho.android.presentation.screens.videoquality.VideoQualityScreen
import com.dakkho.android.presentation.screens.networkdata.NetworkDataScreen
import com.dakkho.android.presentation.screens.contentprotection.ContentProtectionScreen
import com.dakkho.android.presentation.screens.activesessions.ActiveSessionsScreen
import com.dakkho.android.presentation.theme.AnimationConstants

@Composable
fun DakkhoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val enterTransition = fadeIn(
        animationSpec = tween(AnimationConstants.FADE_IN_MS)
    ) + slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(AnimationConstants.SLIDE_DURATION_MS)
    )

    val exitTransition = fadeOut(
        animationSpec = tween(AnimationConstants.FADE_OUT_MS)
    ) + slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(AnimationConstants.SLIDE_DURATION_MS)
    )

    val popEnterTransition = fadeIn(
        animationSpec = tween(AnimationConstants.FADE_IN_MS)
    ) + slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(AnimationConstants.SLIDE_DURATION_MS)
    )

    val popExitTransition = fadeOut(
        animationSpec = tween(AnimationConstants.FADE_OUT_MS)
    ) + slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(AnimationConstants.SLIDE_DURATION_MS)
    )

    NavHost(
        navController = navController,
        startDestination = Route.Login,
        modifier = modifier
    ) {
        // ── Auth Graph ──
        composable<Route.Splash>(
            enterTransition = { fadeIn(tween(AnimationConstants.FADE_IN_MS)) },
            exitTransition = { fadeOut(tween(AnimationConstants.FADE_OUT_MS)) }
        ) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo<Route.Splash> { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Splash> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Login>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Login> { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Route.Signup)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Route.ForgotPassword)
                }
            )
        }

        composable<Route.Signup>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            SignupScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Signup> { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.ForgotPassword>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            ForgotPasswordScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ── Main Graph ──
        composable<Route.Home>(
            enterTransition = { fadeIn(tween(AnimationConstants.FADE_IN_MS)) },
            exitTransition = { fadeOut(tween(AnimationConstants.FADE_OUT_MS)) }
        ) {
            HomeScreen(
                onNavigateToCourse = { courseId ->
                    navController.navigate(Route.CourseDetail(courseId))
                },
                onNavigateToSearch = {
                    navController.navigate(Route.Search)
                },
                onNavigateToNotifications = {
                    navController.navigate(Route.Notifications)
                },
                onNavigateToVideo = { videoId, courseId ->
                    navController.navigate(Route.VideoPlayer(videoId, courseId))
                },
                onNavigateToInstructor = { instructorId ->
                    navController.navigate(Route.InstructorProfile(instructorId))
                },
                onNavigateToInstructorList = {
                    navController.navigate(Route.InstructorList)
                },
                onNavigateToDepartment = { slug ->
                    navController.navigate(Route.Department(slug))
                },
                onNavigateToDepartmentList = {
                    navController.navigate(Route.DepartmentList)
                }
            )
        }

        composable<Route.Explore>(
            enterTransition = { fadeIn(tween(AnimationConstants.FADE_IN_MS)) },
            exitTransition = { fadeOut(tween(AnimationConstants.FADE_OUT_MS)) }
        ) {
            ExploreScreen(
                onNavigateToCourse = { courseId ->
                    navController.navigate(Route.CourseDetail(courseId))
                },
                onNavigateToSearch = {
                    navController.navigate(Route.Search)
                },
                onNavigateToNotifications = {
                    navController.navigate(Route.Notifications)
                },
                onBookmarkClick = { courseId ->
                    // Bookmark toggle handled by ViewModel later
                }
            )
        }

        composable<Route.MyCourses>(
            enterTransition = { fadeIn(tween(AnimationConstants.FADE_IN_MS)) },
            exitTransition = { fadeOut(tween(AnimationConstants.FADE_OUT_MS)) }
        ) {
            PlaceholderScreen(title = "My Courses")
        }

        composable<Route.Profile>(
            enterTransition = { fadeIn(tween(AnimationConstants.FADE_IN_MS)) },
            exitTransition = { fadeOut(tween(AnimationConstants.FADE_OUT_MS)) }
        ) {
            ProfileScreen(
                onNavigateToEditProfile = {
                    navController.navigate(Route.EditProfile)
                },
                onNavigateToChangePassword = {
                    navController.navigate(Route.ChangePassword)
                },
                onNavigateToLearningStats = {
                    navController.navigate(Route.LearningStats)
                },
                onNavigateToSubscription = {
                    navController.navigate(Route.Subscription)
                },
                onNavigateToReferral = {
                    navController.navigate(Route.Referral)
                },
                onNavigateToDownloads = {
                    navController.navigate(Route.Downloads)
                },
                onNavigateToBookmarks = {
                    navController.navigate(Route.Bookmarks)
                },
                onNavigateToSettings = {
                    navController.navigate(Route.Settings)
                },
                onNavigateToAbout = {
                    navController.navigate(Route.About)
                },
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Detail Screens ──
        composable<Route.Category>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            CategoryScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToCourse = { courseId ->
                    navController.navigate(Route.CourseDetail(courseId))
                }
            )
        }

        composable<Route.About>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.CourseDetail>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { backStackEntry ->
            val detailRoute = backStackEntry.toRoute<Route.CourseDetail>()
            CourseDetailScreen(
                courseId = detailRoute.courseId,
                onBackClick = { navController.popBackStack() },
                onNavigateToVideo = { videoId, courseId ->
                    navController.navigate(Route.VideoPlayer(videoId, courseId))
                },
                onNavigateToInstructor = { instructorId ->
                    navController.navigate(Route.InstructorProfile(instructorId))
                },
                onNavigateToCurriculum = { courseId, courseTitle, isEnrolled ->
                    navController.navigate(Route.CourseCurriculum(courseId, courseTitle, isEnrolled))
                },
                onNavigateToReviews = { courseId, courseTitle, averageRating, reviewCount, isEnrolled ->
                    navController.navigate(Route.CourseReviews(courseId, courseTitle, averageRating, reviewCount.toInt(), isEnrolled))
                },
                onNavigateToQnA = { courseId, courseTitle, isEnrolled ->
                    navController.navigate(Route.CourseQnA(courseId, courseTitle, isEnrolled))
                },
                onNavigateToAnnouncements = { courseId, courseTitle ->
                    navController.navigate(Route.CourseAnnouncements(courseId, courseTitle))
                },
                onNavigateToResources = { courseId, courseTitle, isEnrolled ->
                    navController.navigate(Route.CourseResources(courseId, courseTitle, isEnrolled))
                },
                onNavigateToNotes = { courseId, courseTitle ->
                    navController.navigate(Route.CourseNotes(courseId, courseTitle))
                },
                onNavigateToQuizzes = { courseId, courseTitle ->
                    navController.navigate(Route.CourseQuizzes(courseId, courseTitle))
                },
                onNavigateToProgress = { courseId, courseTitle ->
                    navController.navigate(Route.CourseProgress(courseId, courseTitle))
                }
            )
        }

        composable<Route.VideoPlayer>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val playerRoute = backStackEntry.toRoute<Route.VideoPlayer>()
            VideoPlayerScreen(
                videoId = playerRoute.videoId,
                courseId = playerRoute.courseId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.InstructorList>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            InstructorListScreen(
                onBackClick = { navController.popBackStack() },
                onInstructorClick = { instructorId ->
                    navController.navigate(Route.InstructorProfile(instructorId))
                }
            )
        }

        composable<Route.InstructorProfile>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            InstructorProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToCourse = { courseId ->
                    navController.navigate(Route.CourseDetail(courseId))
                },
                onNavigateToInstructorCourses = { instructorId, instructorName ->
                    navController.navigate(Route.InstructorCourses(instructorId, instructorName))
                },
                onNavigateToInstructorReviews = { instructorId, instructorName, avgRating, reviewCount ->
                    navController.navigate(Route.InstructorReviews(instructorId, instructorName, avgRating, reviewCount))
                },
                onNavigateToInstructorSchedule = { instructorId, instructorName ->
                    navController.navigate(Route.InstructorSchedule(instructorId, instructorName))
                },
                onNavigateToInstructorContact = { instructorId, instructorName ->
                    navController.navigate(Route.InstructorContact(instructorId, instructorName))
                }
            )
        }

        composable<Route.Search>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            SearchScreen(
                onNavigateToCourse = { courseId ->
                    navController.navigate(Route.CourseDetail(courseId))
                },
                onNavigateToInstructor = { instructorId ->
                    navController.navigate(Route.InstructorProfile(instructorId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Notifications>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { notification ->
                    navController.navigate(Route.NotificationDetail(notification.id))
                }
            )
        }

        composable<Route.NotificationDetail>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val detailRoute = backStackEntry.toRoute<Route.NotificationDetail>()
            NotificationDetailScreen(
                notificationId = detailRoute.notificationId,
                onBackClick = { navController.popBackStack() },
                onActionClick = { actionUrl ->
                    // Parse deep link URL and navigate accordingly
                    handleDeepLink(navController, actionUrl)
                }
            )
        }

        composable<Route.Settings>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onDeleteAccount = {
                    // Delete account → navigate to Login
                    navController.navigate(Route.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToStorageManagement = {
                    navController.navigate(Route.StorageManagement)
                },
                onNavigateToNotificationPreferences = {
                    navController.navigate(Route.NotificationPreferences)
                },
                onNavigateToDataSaver = {
                    navController.navigate(Route.DataSaver)
                },
                onNavigateToAccessibilitySettings = {
                    navController.navigate(Route.AccessibilitySettings)
                },
                onNavigateToAboutLegal = {
                    navController.navigate(Route.AboutLegal)
                },
                onNavigateToThemeSettings = {
                    navController.navigate(Route.ThemeSettings)
                },
                onNavigateToDownloadSettings = {
                    navController.navigate(Route.DownloadSettings)
                },
                onNavigateToVideoQualitySettings = {
                    navController.navigate(Route.VideoQualitySettings)
                },
                onNavigateToNetworkData = {
                    navController.navigate(Route.NetworkData)
                },
                onNavigateToContentProtection = {
                    navController.navigate(Route.ContentProtection)
                },
                onNavigateToActiveSessions = {
                    navController.navigate(Route.ActiveSessions)
                }
            )
        }

        composable<Route.Downloads>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            DownloadsScreen(
                onBackClick = { navController.popBackStack() },
                onPlayVideo = { videoId, courseId ->
                    navController.navigate(Route.VideoPlayer(videoId, courseId))
                },
                onBrowseCourses = {
                    navController.navigate(Route.Explore)
                }
            )
        }

        composable<Route.Bookmarks>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            BookmarksScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToCourse = { courseId ->
                    navController.navigate(Route.CourseDetail(courseId))
                },
                onNavigateToExplore = {
                    navController.navigate(Route.Explore)
                }
            )
        }

        composable<Route.WatchHistory>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            WatchHistoryScreen(
                onBackClick = { navController.popBackStack() },
                onResumeVideo = { videoId, courseId ->
                    navController.navigate(Route.VideoPlayer(videoId, courseId))
                }
            )
        }

        composable<Route.Certificates>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            CertificatesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.Achievements>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            AchievementsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.LiveSessions>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            LiveSessionsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLiveClass = { liveClassId ->
                    navController.navigate(Route.LiveClassDetail(liveClassId))
                }
            )
        }

        composable<Route.LiveClassDetail>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Route.LiveClassDetail>()
            LiveSessionsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLiveClass = { }
            )
        }

        composable<Route.DiscussionForum>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            DiscussionForumScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.DiscussionThread>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            DiscussionForumScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.PaymentStatus>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            PlaceholderScreen(title = "Payment Status")
        }

        composable<Route.OtpVerification>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val otpRoute = backStackEntry.toRoute<Route.OtpVerification>()
            PlaceholderScreen(title = "OTP Verification - ${otpRoute.email}")
        }

        composable<Route.Assignments>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val assignmentsRoute = backStackEntry.toRoute<Route.Assignments>()
            AssignmentScreen(
                courseId = assignmentsRoute.courseId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.CourseCurriculum>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val curriculumRoute = backStackEntry.toRoute<Route.CourseCurriculum>()
            CourseCurriculumScreen(
                courseId = curriculumRoute.courseId,
                courseTitle = curriculumRoute.courseTitle,
                isEnrolled = curriculumRoute.isEnrolled,
                onBackClick = { navController.popBackStack() },
                onLessonClick = { lesson ->
                    if (lesson.videoUrl != null) {
                        navController.navigate(Route.VideoPlayer(lesson.id, curriculumRoute.courseId))
                    }
                }
            )
        }

        composable<Route.CourseReviews>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val reviewsRoute = backStackEntry.toRoute<Route.CourseReviews>()
            CourseReviewsScreen(
                courseId = reviewsRoute.courseId,
                courseTitle = reviewsRoute.courseTitle,
                averageRating = reviewsRoute.averageRating,
                reviewCount = reviewsRoute.reviewCount,
                isEnrolled = reviewsRoute.isEnrolled,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.CourseQnA>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val qnaRoute = backStackEntry.toRoute<Route.CourseQnA>()
            QnAScreen(
                courseId = qnaRoute.courseId,
                courseTitle = qnaRoute.courseTitle,
                isEnrolled = qnaRoute.isEnrolled,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.CourseAnnouncements>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val annRoute = backStackEntry.toRoute<Route.CourseAnnouncements>()
            AnnouncementsScreen(
                courseId = annRoute.courseId,
                courseTitle = annRoute.courseTitle,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.CourseResources>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val resRoute = backStackEntry.toRoute<Route.CourseResources>()
            ResourcesScreen(
                courseId = resRoute.courseId,
                courseTitle = resRoute.courseTitle,
                isEnrolled = resRoute.isEnrolled,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.CourseNotes>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val notesRoute = backStackEntry.toRoute<Route.CourseNotes>()
            CourseNotesScreen(
                courseId = notesRoute.courseId,
                courseTitle = notesRoute.courseTitle,
                videoId = notesRoute.videoId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.CourseQuizzes>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val quizzesRoute = backStackEntry.toRoute<Route.CourseQuizzes>()
            CourseQuizzesScreen(
                courseId = quizzesRoute.courseId,
                courseTitle = quizzesRoute.courseTitle,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.CourseProgress>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val progressRoute = backStackEntry.toRoute<Route.CourseProgress>()
            CourseProgressScreen(
                courseId = progressRoute.courseId,
                courseTitle = progressRoute.courseTitle,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Instructor Sub-pages ──

        composable<Route.InstructorCourses>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val coursesRoute = backStackEntry.toRoute<Route.InstructorCourses>()
            InstructorCoursesScreen(
                onBackClick = { navController.popBackStack() },
                onCourseClick = { courseId ->
                    navController.navigate(Route.CourseDetail(courseId))
                }
            )
        }

        composable<Route.InstructorReviews>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            InstructorReviewsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.InstructorSchedule>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            InstructorScheduleScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.InstructorContact>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            InstructorContactScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Phase 23: Dynamic Department Routes ──
        // No hardcoded 20 departments — all from API

        composable<Route.DepartmentList>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            DepartmentListScreen(
                onBackClick = { navController.popBackStack() },
                onDepartmentClick = { slug ->
                    navController.navigate(Route.Department(slug))
                }
            )
        }

        composable<Route.Department>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val deptRoute = backStackEntry.toRoute<Route.Department>()
            DepartmentScreen(
                slug = deptRoute.slug,
                onBackClick = { navController.popBackStack() },
                onCourseClick = { courseId ->
                    navController.navigate(Route.CourseDetail(courseId))
                },
                onInstructorClick = { instructorId ->
                    navController.navigate(Route.InstructorProfile(instructorId))
                },
                onSearchClick = {
                    navController.navigate(Route.Search)
                },
                onSemesterClick = { deptSlug, semNumber ->
                    navController.navigate(Route.Semester(deptSlug, semNumber))
                }
            )
        }

        // ── Phase 24: Semester Routes ──
        // 7 regular semesters + 8th = ইন্টার্নি (Internship)

        composable<Route.Semester>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) { backStackEntry ->
            val semesterRoute = backStackEntry.toRoute<Route.Semester>()
            SemesterScreen(
                departmentSlug = semesterRoute.departmentSlug,
                semesterNumber = semesterRoute.semesterNumber,
                onBackClick = { navController.popBackStack() },
                onCourseClick = { courseId ->
                    navController.navigate(Route.CourseDetail(courseId))
                },
                onSubjectClick = { subjectId ->
                    // TODO: Navigate to subject detail page
                }
            )
        }

        // ── Phase 25: Profile Sub-pages #65-71 ──

        composable<Route.EditProfile>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.ChangePassword>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onPasswordChanged = { navController.popBackStack() }
            )
        }

        composable<Route.LearningStats>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            LearningStatsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.Subscription>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            SubscriptionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.Referral>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            ReferralScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Phase 26: Settings Part 1 #72-76 ──

        composable<Route.StorageManagement>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            com.dakkho.android.presentation.screens.storagemanagement.StorageManagementScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.NotificationPreferences>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            com.dakkho.android.presentation.screens.notificationpreferences.NotificationPreferencesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.DataSaver>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            com.dakkho.android.presentation.screens.datasaver.DataSaverScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.AccessibilitySettings>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            com.dakkho.android.presentation.screens.accessibilitysettings.AccessibilitySettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.AboutLegal>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            com.dakkho.android.presentation.screens.aboutlegal.AboutLegalScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Phase 27: Settings Part 2 #77-82 ──

        composable<Route.ThemeSettings>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            ThemeSettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.DownloadSettings>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            DownloadSettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.VideoQualitySettings>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            VideoQualityScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.NetworkData>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            NetworkDataScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.ContentProtection>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            ContentProtectionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.ActiveSessions>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            ActiveSessionsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "DAKKHO",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Parse notification action URLs (deep links) and navigate to the relevant screen.
 * Supported patterns:
 * - dakkho://course/:id  → CourseDetail
 * - dakkho://notification/:id → NotificationDetail
 * - dakkho://payment/status → PaymentStatus
 * - dakkho://certificate/:id → Certificates
 */
private fun handleDeepLink(navController: NavHostController, actionUrl: String) {
    when {
        actionUrl.contains("/course/") -> {
            val courseId = actionUrl.substringAfterLast("/")
            if (courseId.isNotEmpty()) {
                navController.navigate(Route.CourseDetail(courseId))
            }
        }
        actionUrl.contains("/notification/") -> {
            val notificationId = actionUrl.substringAfterLast("/")
            if (notificationId.isNotEmpty()) {
                navController.navigate(Route.NotificationDetail(notificationId))
            }
        }
        actionUrl.contains("/payment/status") -> {
            navController.navigate(Route.PaymentStatus)
        }
        actionUrl.contains("/certificate/") -> {
            navController.navigate(Route.Certificates)
        }
        actionUrl.contains("dakkho://certificate") -> {
            // Deep link: dakkho://certificate/:id
            navController.navigate(Route.Certificates)
        }
        else -> {
            // Unknown deep link — no-op
        }
    }
}
