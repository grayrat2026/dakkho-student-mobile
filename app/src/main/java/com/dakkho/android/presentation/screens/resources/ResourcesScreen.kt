package com.dakkho.android.presentation.screens.resources

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    courseId: String,
    courseTitle: String,
    isEnrolled: Boolean,
    onBackClick: () -> Unit,
    viewModel: ResourcesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(courseId) {
        viewModel.initialize(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Resources",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { innerPadding ->
        ResourcesContent(
            uiState = uiState,
            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
            onFilterChange = { viewModel.updateFilterType(it) },
            onDownloadClick = { viewModel.downloadResource(it) },
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun ResourcesContent(
    uiState: ResourcesUiState,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (String?) -> Unit,
    onDownloadClick: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search resources...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Neutral400
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.filterType == null || uiState.filterType == "all",
                onClick = { onFilterChange(null) },
                label = { Text("All", style = MaterialTheme.typography.labelSmall) }
            )
            FilterChip(
                selected = uiState.filterType == "pdf",
                onClick = { onFilterChange("pdf") },
                label = { Text("PDF", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            )
            FilterChip(
                selected = uiState.filterType == "image",
                onClick = { onFilterChange("image") },
                label = { Text("Images", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Resources list
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SkyBlue
                )
            } else if (uiState.resources.isEmpty() && uiState.error != null) {
                EmptyState(
                    title = "Could not load resources",
                    subtitle = uiState.error ?: "An error occurred",
                    actionText = "Retry",
                    onAction = onRefresh
                )
            } else if (uiState.filteredResources.isEmpty()) {
                EmptyState(
                    title = if (uiState.searchQuery.isNotBlank()) "No matching resources"
                           else "No resources yet",
                    subtitle = if (uiState.searchQuery.isNotBlank()) "Try a different search term"
                              else "Course resources will appear here when available"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Stats header
                    item {
                        Text(
                            text = "${uiState.filteredResources.size} resources available",
                            style = MaterialTheme.typography.bodySmall,
                            color = Neutral400
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    items(uiState.filteredResources, key = { it.id }) { resource ->
                        ResourceCard(
                            resource = resource,
                            isDownloading = uiState.downloadingResourceId == resource.id,
                            onDownloadClick = { onDownloadClick(resource.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourceCard(
    resource: ResourceItem,
    isDownloading: Boolean,
    onDownloadClick: () -> Unit
) {
    val iconTint = when (resource.fileType.lowercase()) {
        "pdf" -> Color(0xFFEF4444)
        "image", "jpg", "jpeg", "png" -> Color(0xFF8B5CF6)
        "doc", "docx" -> SkyBlue
        else -> Neutral400
    }

    val fileIcon = when (resource.fileType.lowercase()) {
        "pdf" -> Icons.Default.PictureAsPdf
        "image", "jpg", "jpeg", "png" -> Icons.Default.Image
        else -> Icons.Default.Description
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File type icon
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = iconTint.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = fileIcon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = iconTint
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // File info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = resource.lessonTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = resource.fileType.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = iconTint,
                        fontWeight = FontWeight.SemiBold
                    )
                    resource.fileSize?.let { size ->
                        Text(
                            text = size,
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral400
                        )
                    }
                }
            }

            // Download button
            if (resource.isDownloaded) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Downloaded",
                    modifier = Modifier.size(24.dp),
                    tint = Green
                )
            } else if (isDownloading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = SkyBlue
                )
            } else {
                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        modifier = Modifier.size(22.dp),
                        tint = SkyBlue
                    )
                }
            }
        }
    }
}
