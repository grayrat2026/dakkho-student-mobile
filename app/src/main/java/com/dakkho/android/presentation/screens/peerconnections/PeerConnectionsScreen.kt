package com.dakkho.android.presentation.screens.peerconnections

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.PeerUser
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerConnectionsScreen(
    onBackClick: () -> Unit,
    viewModel: PeerConnectionsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val peers by viewModel.peers.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "সহপাঠী সংযোগ",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp16),
            verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            // Suggestions Section
            if (suggestions.isNotEmpty()) {
                item {
                    Text(text = "সুপারিশকৃত সহপাঠী", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = SkyBlue)
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                }
                items(suggestions) { peer ->
                    PeerCard(
                        peer = peer,
                        onFollowToggle = { viewModel.toggleFollow(peer.id, peer.isFollowing) },
                        onMessageClick = { openMessagingApp(context, peer.name) }
                    )
                }
                item { Spacer(modifier = Modifier.height(DesignToken.Space.dp16)) }
            }

            // All Peers
            item {
                Text(text = "সকল সহপাঠী", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            }
            items(peers) { peer ->
                PeerCard(
                    peer = peer,
                    onFollowToggle = { viewModel.toggleFollow(peer.id, peer.isFollowing) },
                    onMessageClick = { openMessagingApp(context, peer.name) }
                )
            }
        }
    }
}

@Composable
private fun PeerCard(
    peer: PeerUser,
    onFollowToggle: () -> Unit,
    onMessageClick: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(DesignToken.Space.dp16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape)) {
                if (peer.avatarUrl != null) {
                    AsyncImage(model = peer.avatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape))
                }
            }
            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = peer.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                peer.technology?.let { Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Row {
                    Text(text = "${peer.xpPoints} XP", style = MaterialTheme.typography.labelSmall, color = SkyBlue)
                    if (peer.mutualConnections > 0) {
                        Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                        Text(text = "${peer.mutualConnections} মিউচুয়াল", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (peer.isFollowing) {
                OutlinedButton(onClick = onMessageClick) {
                    Icon(imageVector = Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("মেসেজ")
                }
            } else {
                GradientButton(text = "ফলো", onClick = onFollowToggle, modifier = Modifier.height(36.dp))
            }
        }
    }
}

private fun openMessagingApp(context: android.content.Context, peerName: String) {
    // Intent-based messaging: open WhatsApp/Telegram/SMS
    val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/")).apply {
        setPackage("com.whatsapp")
    }
    try {
        context.startActivity(whatsappIntent)
    } catch (e: Exception) {
        // Fallback to SMS
        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"))
        context.startActivity(smsIntent)
    }
}
