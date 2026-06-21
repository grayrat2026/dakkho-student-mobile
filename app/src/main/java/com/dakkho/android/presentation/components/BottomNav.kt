package com.dakkho.android.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIconRes: Int,
    val unselectedIconRes: Int,
    val badgeCount: Int = 0
)

@Composable
fun DakkhoBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    notificationCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(
            route = "home",
            label = "Home",
            selectedIconRes = android.R.drawable.btn_star_big_on,
            unselectedIconRes = android.R.drawable.btn_star_big_off,
            badgeCount = 0
        ),
        BottomNavItem(
            route = "explore",
            label = "Explore",
            selectedIconRes = android.R.drawable.ic_menu_search,
            unselectedIconRes = android.R.drawable.ic_menu_search,
            badgeCount = 0
        ),
        BottomNavItem(
            route = "my_courses",
            label = "My Courses",
            selectedIconRes = android.R.drawable.ic_menu_info_details,
            unselectedIconRes = android.R.drawable.ic_menu_info_details,
            badgeCount = notificationCount
        ),
        BottomNavItem(
            route = "profile",
            label = "Profile",
            selectedIconRes = android.R.drawable.ic_menu_myplaces,
            unselectedIconRes = android.R.drawable.ic_menu_myplaces,
            badgeCount = 0
        )
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = DesignToken.Elevation.level2
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val interactionSource = remember { MutableInteractionSource() }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount > 0) {
                                    Badge {
                                        Text(
                                            text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) SkyBlue.copy(alpha = 0.12f)
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isSelected) item.selectedIconRes else item.unselectedIconRes
                                    ),
                                    contentDescription = item.label,
                                    tint = if (isSelected) SkyBlue else Neutral400,
                                    modifier = Modifier.size(DesignToken.IconSize.medium)
                                )
                            }
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(width = 16.dp, height = 3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(SkyBlue)
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) SkyBlue else Neutral400
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SkyBlue,
                    unselectedIconColor = Neutral400,
                    selectedTextColor = SkyBlue,
                    unselectedTextColor = Neutral400,
                    indicatorColor = Color.Transparent
                ),
                interactionSource = interactionSource
            )
        }
    }
}
