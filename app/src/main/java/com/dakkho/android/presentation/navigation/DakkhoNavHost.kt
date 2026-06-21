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
import com.dakkho.android.presentation.screens.auth.ForgotPasswordScreen
import com.dakkho.android.presentation.screens.auth.LoginScreen
import com.dakkho.android.presentation.screens.auth.SignupScreen
import com.dakkho.android.presentation.screens.category.CategoryScreen
import com.dakkho.android.presentation.screens.explore.ExploreScreen
import com.dakkho.android.presentation.screens.home.HomeScreen
import com.dakkho.android.presentation.screens.notifications.NotificationDetailScreen
import com.dakkho.android.presentation.screens.notifications.NotificationsScreen
import com.dakkho.android.presentation.screens.profile.ProfileScreen
import com.dakkho.android.presentation.screens.search.SearchScreen
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
                    // Navigate to edit profile (future phase)
                },
                onNavigateToChangePassword = {
                    // Navigate to change password (future phase)
                },
                onNavigateToLearningStats = {
                    // Navigate to learning stats (future phase)
                },
                onNavigateToSubscription = {
                    // Navigate to subscription (future phase)
                },
                onNavigateToReferral = {
                    // Navigate to referral (future phase)
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
        ) {
            PlaceholderScreen(title = "Course Detail")
        }

        composable<Route.VideoPlayer>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            PlaceholderScreen(title = "Video Player")
        }

        composable<Route.InstructorProfile>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            PlaceholderScreen(title = "Instructor Profile")
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
            PlaceholderScreen(title = "Settings")
        }

        composable<Route.Downloads>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            PlaceholderScreen(title = "Downloads")
        }

        composable<Route.Bookmarks>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            PlaceholderScreen(title = "Bookmarks")
        }

        composable<Route.WatchHistory>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            PlaceholderScreen(title = "Watch History")
        }

        composable<Route.Certificates>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            PlaceholderScreen(title = "Certificates")
        }

        composable<Route.Achievements>(
            enterTransition = { enterTransition },
            exitTransition = { exitTransition }
        ) {
            PlaceholderScreen(title = "Achievements")
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
        else -> {
            // Unknown deep link — no-op
        }
    }
}
