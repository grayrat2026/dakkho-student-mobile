package com.dakkho.android.presentation.screens.studygroups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.StudyGroup
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StudyGroupsScreen(
    onBackClick: () -> Unit,
    viewModel: StudyGroupsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val groups by viewModel.groups
    val showCreateDialog by viewModel.showCreateDialog

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "স্টাডি গ্রুপ",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                containerColor = SkyBlue
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "নতুন গ্রুপ")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Group, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                    Text(text = "কোনো গ্রুপ নেই", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "নতুন গ্রুপ তৈরি করুন বা যোগ দিন", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp16),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
            ) {
                items(groups) { group ->
                    StudyGroupCard(
                        group = group,
                        onJoinClick = { viewModel.joinGroup(group.id) },
                        onLeaveClick = { viewModel.leaveGroup(group.id) }
                    )
                }
            }
        }
    }

    // Create Group Dialog
    if (showCreateDialog) {
        CreateGroupDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onCreate = { name, desc, subject -> viewModel.createGroup(name, desc, subject) }
        )
    }
}

@Composable
private fun StudyGroupCard(
    group: StudyGroup,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(DesignToken.Space.dp16)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                Text(text = group.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                Text(text = "${group.memberCount}/${group.maxMembers}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = group.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // Member Avatars Row
            if (group.avatarUrls.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    group.avatarUrls.take(5).forEachIndexed { index, url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp).clip(CircleShape)
                                .padding(start = if (index > 0) (-4).dp else 0.dp)
                        )
                    }
                    if (group.memberCount > 5) {
                        Text(text = "+${group.memberCount - 5}", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Note, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${group.sharedNotesCount} নোট", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (group.isJoined) {
                    TextButton(onClick = onLeaveClick) {
                        Text(text = "ছেড়ে দিন", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    GradientButton(text = "যোগ দিন", onClick = onJoinClick, modifier = Modifier.height(36.dp))
                }
            }
        }
    }
}

@Composable
private fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "নতুন গ্রুপ তৈরি") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("গ্রুপের নাম") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("বিষয়") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("বিবরণ") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            }
        },
        confirmButton = {
            GradientButton(text = "তৈরি করুন", onClick = { onCreate(name, description, subject) }, enabled = name.isNotBlank() && subject.isNotBlank())
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}
