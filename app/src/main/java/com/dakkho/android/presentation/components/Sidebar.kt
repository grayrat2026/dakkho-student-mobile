package com.dakkho.android.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.User
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.Neutral700
import com.dakkho.android.presentation.theme.SkyBlue

data class SidebarMenuItem(
    val route: String,
    val label: String,
    val iconRes: Int,
    val isDestructive: Boolean = false
)

data class SidebarMenuGroup(
    val title: String,
    val items: List<SidebarMenuItem>
)

@Composable
fun DakkhoSidebar(
    user: User?,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuGroups = listOf(
        SidebarMenuGroup(
            title = "Main",
            items = listOf(
                SidebarMenuItem("home", "Home", android.R.drawable.btn_star_big_on),
                SidebarMenuItem("explore", "Explore", android.R.drawable.ic_menu_search),
                SidebarMenuItem("my_courses", "My Courses", android.R.drawable.ic_menu_info_details)
            )
        ),
        SidebarMenuGroup(
            title = "Learning",
            items = listOf(
                SidebarMenuItem("downloads", "Downloads", android.R.drawable.ic_menu_save),
                SidebarMenuItem("bookmarks", "Bookmarks", android.R.drawable.ic_menu_agenda),
                SidebarMenuItem("watch_history", "Watch History", android.R.drawable.ic_menu_recent_history),
                SidebarMenuItem("certificates", "Certificates", android.R.drawable.ic_menu_gallery),
                SidebarMenuItem("achievements", "Achievements", android.R.drawable.ic_menu_day)
            )
        ),
        SidebarMenuGroup(
            title = "Social",
            items = listOf(
                SidebarMenuItem("discussion", "Discussion", android.R.drawable.ic_menu_send),
                SidebarMenuItem("live_sessions", "Live Sessions", android.R.drawable.ic_menu_camera)
            )
        ),
        SidebarMenuGroup(
            title = "Settings",
            items = listOf(
                SidebarMenuItem("settings", "Settings", android.R.drawable.ic_menu_preferences)
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {
        // User header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(DeepBlue, SkyBlue)
                    )
                )
                .padding(DesignToken.Space.dp24)
        ) {
            Box(
                modifier = Modifier
                    .size(DesignToken.ComponentSize.avatarLarge)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (user?.avatarUrl != null) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(DesignToken.ComponentSize.avatarLarge)
                    )
                } else {
                    Text(
                        text = user?.fullName?.firstOrNull()?.uppercase() ?: "D",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            Text(
                text = user?.fullName ?: "Guest User",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }

        // Menu groups
        menuGroups.forEach { group ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = group.title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = Neutral500,
                    modifier = Modifier.padding(
                        start = DesignToken.Space.dp16,
                        top = DesignToken.Space.dp16,
                        bottom = DesignToken.Space.dp8
                    )
                )

                group.items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(item.route) }
                            .background(
                                if (isSelected) SkyBlue.copy(alpha = 0.08f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .padding(
                                horizontal = DesignToken.Space.dp16,
                                vertical = DesignToken.Space.dp12
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            tint = if (item.isDestructive) Error
                            else if (isSelected) SkyBlue else Neutral700,
                            modifier = Modifier.size(DesignToken.IconSize.medium)
                        )
                        Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (item.isDestructive) Error
                            else if (isSelected) SkyBlue else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (group != menuGroups.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = DesignToken.Space.dp16,
                            vertical = DesignToken.Space.dp8
                        ),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp16)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_lock_power_off),
                contentDescription = "Logout",
                tint = Error,
                modifier = Modifier.size(DesignToken.IconSize.medium)
            )
            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Error
            )
        }
    }
}
