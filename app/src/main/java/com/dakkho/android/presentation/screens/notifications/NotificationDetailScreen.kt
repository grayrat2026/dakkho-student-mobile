package com.dakkho.android.presentation.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.NotificationItem
import com.dakkho.android.presentation.components.DakkhoTopBar
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500

@Composable
fun NotificationDetailScreen(
    notificationId: String,
    onBackClick: () -> Unit,
    onActionClick: (String) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val selectedNotification by viewModel.selectedNotification

    // Load the notification from Room when screen is opened
    LaunchedEffect(notificationId) {
        val notification = viewModel.getNotificationById(notificationId)
        viewModel.selectNotification(notification)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        DakkhoTopBar(
            title = "Notification",
            showSearch = false,
            showNotification = false,
            showAvatar = false,
            onBackClick = onBackClick
        )

        // Content
        if (selectedNotification != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(DesignToken.Space.dp16),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp16)
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Title
                    Text(
                        text = selectedNotification!!.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                    // Timestamp
                    selectedNotification!!.createdAt?.let { timestamp ->
                        Text(
                            text = timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral400
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                    }

                    // Body
                    selectedNotification!!.body?.let { body ->
                        Text(
                            text = body,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Neutral500
                        )
                    }
                }

                // Action button
                selectedNotification!!.actionUrl?.let { actionUrl ->
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

                    GradientButton(
                        text = "Take Action",
                        onClick = { onActionClick(actionUrl) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Loading state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(DesignToken.Space.dp16),
                verticalArrangement = Arrangement.Center
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Loading notification...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral400
                    )
                }
            }
        }
    }
}
