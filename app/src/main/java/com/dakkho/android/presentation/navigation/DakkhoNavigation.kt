package com.dakkho.android.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dakkho.android.presentation.components.DakkhoBottomNav
import com.dakkho.android.presentation.components.DakkhoSidebar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DakkhoNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarRoutes = listOf(
        Route.Home::class,
        Route.Explore::class,
        Route.MyCourses::class,
        Route.Profile::class
    )

    val showBottomBar by remember(currentDestination) {
        derivedStateOf {
            bottomBarRoutes.any { routeClass ->
                currentDestination?.hasRoute(routeClass) == true
            }
        }
    }

    val currentRoute by remember(currentDestination) {
        derivedStateOf {
            when {
                currentDestination?.hasRoute(Route.Home::class) == true -> "home"
                currentDestination?.hasRoute(Route.Explore::class) == true -> "explore"
                currentDestination?.hasRoute(Route.MyCourses::class) == true -> "my_courses"
                currentDestination?.hasRoute(Route.Profile::class) == true -> "profile"
                else -> ""
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DakkhoSidebar(
                    user = null,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        when (route) {
                            "home" -> navController.navigate(Route.Home) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            "explore" -> navController.navigate(Route.Explore) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            "my_courses" -> navController.navigate(Route.MyCourses) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            "profile" -> navController.navigate(Route.Profile) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            "settings" -> navController.navigate(Route.Settings)
                            "downloads" -> navController.navigate(Route.Downloads)
                            "bookmarks" -> navController.navigate(Route.Bookmarks)
                            "watch_history" -> navController.navigate(Route.WatchHistory)
                            "certificates" -> navController.navigate(Route.Certificates)
                            "achievements" -> navController.navigate(Route.Achievements)
                        }
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Route.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        },
        gesturesEnabled = showBottomBar
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    DakkhoBottomNav(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            when (route) {
                                "home" -> navController.navigate(Route.Home) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                "explore" -> navController.navigate(Route.Explore) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                "my_courses" -> navController.navigate(Route.MyCourses) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                "profile" -> navController.navigate(Route.Profile) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        notificationCount = 0
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                DakkhoNavHost(navController = navController)
            }
        }
    }
}
