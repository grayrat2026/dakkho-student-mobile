package com.dakkho.android.presentation.screens.department

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.Technology
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

/**
 * Department List Screen — shows all departments dynamically.
 * Departments come from the API only — whatever Admin/Instructor has added.
 * No hardcoded defaults. If API returns empty, shows a "No departments" empty state.
 */
@Composable
fun DepartmentListScreen(
    onBackClick: () -> Unit,
    onDepartmentClick: (String) -> Unit,
    viewModel: DepartmentListViewModel = hiltViewModel()
) {
    val departments by viewModel.departments.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val error by viewModel.error.collectAsState()

    val isSearching = searchQuery.isNotBlank()
    val displayList = if (isSearching) searchResults else departments

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = DesignToken.Space.dp4, vertical = DesignToken.Space.dp4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Departments",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        // ── Search Bar ──
        SearchBar(
            query = searchQuery,
            onQueryChanged = { viewModel.onSearchQueryChanged(it) },
            onClearClick = { viewModel.clearSearch() },
            modifier = Modifier.padding(horizontal = DesignToken.Space.dp16)
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // ── Content ──
        if (isLoading && departments.isEmpty()) {
            // Loading skeleton
            DepartmentListSkeleton()
        } else if (displayList.isEmpty()) {
            if (isSearching) {
                EmptyState(
                    title = "No results found",
                    subtitle = "No departments match \"$searchQuery\"",
                    iconRes = android.R.drawable.ic_menu_search
                )
            } else {
                EmptyState(
                    title = "No departments yet",
                    subtitle = "Departments will appear here once they are added by administrators or instructors.",
                    iconRes = android.R.drawable.ic_menu_gallery
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp8),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
            ) {
                items(displayList, key = { it.id }) { department ->
                    DepartmentCard(
                        department = department,
                        onClick = { onDepartmentClick(department.slug) }
                    )
                }
            }
        }
    }
}

// ── Search Bar ──

@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = {
            Text(
                text = "Search departments...",
                color = Neutral400
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = Neutral500
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear",
                        tint = Neutral500
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(DesignToken.Space.dp12),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SkyBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = SkyBlue
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// ── Department Card ──

@Composable
private fun DepartmentCard(
    department: Technology,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon / Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (department.iconUrl != null) {
                    AsyncImage(
                        model = department.iconUrl,
                        contentDescription = department.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // Fallback: initial letter with gradient background
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .drawBehind {
                                drawRect(
                                    brush = Brush.linearGradient(
                                        colors = listOf(SkyBlue, DeepBlue)
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = department.name.take(2).uppercase(),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp10))

            // Name
            Text(
                text = department.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

            // Short code badge
            if (department.shortCode.isNotBlank()) {
                Text(
                    text = department.shortCode,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = SkyBlue,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .drawBehind { drawRect(SkyBlue.copy(alpha = 0.1f)) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp6))

            // Course count
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${department.courseCount}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Green
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "courses",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral500
                )
            }
        }
    }
}

// ── Loading Skeleton ──

@Composable
private fun DepartmentListSkeleton() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = DesignToken.Space.dp16),
        horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
    ) {
        items(6) {
            GlassCard {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShimmerEffect(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp10))
                    ShimmerEffect(
                        modifier = Modifier
                            .width(100.dp)
                            .height(14.dp)
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                    ShimmerEffect(
                        modifier = Modifier
                            .width(60.dp)
                            .height(10.dp)
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp6))
                    ShimmerEffect(
                        modifier = Modifier
                            .width(80.dp)
                            .height(10.dp)
                    )
                }
            }
        }
    }
}

// ── Brush import needed ──
private val Brush = androidx.compose.ui.graphics.Brush
