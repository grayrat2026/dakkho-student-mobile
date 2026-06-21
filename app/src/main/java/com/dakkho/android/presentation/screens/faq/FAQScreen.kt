package com.dakkho.android.presentation.screens.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.FAQCategory
import com.dakkho.android.domain.model.FAQItem
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(
    onBackClick: () -> Unit,
    viewModel: FAQViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "সাধারণ প্রশ্ন",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                onSearch = {},
                active = false,
                onActiveChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignToken.Space.dp16)
                    .padding(vertical = DesignToken.Space.dp8),
                placeholder = { Text("প্রশ্ন খুঁজুন...") }
            ) {}

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // FAQ Categories List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = DesignToken.Space.dp16),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
            ) {
                item { Spacer(modifier = Modifier.height(DesignToken.Space.dp4)) }

                items(uiState.filteredCategories, key = { it.id }) { category ->
                    FAQCategorySection(
                        category = category,
                        searchQuery = uiState.searchQuery
                    )
                }

                item { Spacer(modifier = Modifier.height(DesignToken.Space.dp24)) }
            }
        }
    }
}

@Composable
private fun FAQCategorySection(
    category: FAQCategory,
    searchQuery: String
) {
    var isExpanded by remember { mutableStateOf(searchQuery.isNotEmpty()) }

    // Auto-expand when searching
    if (searchQuery.isNotEmpty() && !isExpanded) {
        isExpanded = true
    }

    val categoryIcon: ImageVector = when (category.id) {
        "courses" -> Icons.Default.School
        "payment" -> Icons.Default.Payment
        "technical" -> Icons.Default.Build
        else -> Icons.Default.School
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DesignToken.Shape.large)
    ) {
        // Category Header - clickable to expand/collapse
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = DesignToken.Space.dp4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = null,
                modifier = Modifier.size(DesignToken.IconSize.medium),
                tint = SkyBlue
            )

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${category.questions.size} টি প্রশ্ন",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Animated expand/collapse icon
            val rotation by animateFloatAsState(
                targetValue = if (isExpanded) 180f else 0f,
                label = "expand_rotation"
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "সংকুচিত করুন" else "প্রসারিত করুন",
                modifier = Modifier
                    .size(DesignToken.IconSize.medium)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Animated expandable content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                initialHeight = { 0 }
            ),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(top = DesignToken.Space.dp8)
            ) {
                category.questions.forEachIndexed { index, faqItem ->
                    FAQItemRow(
                        faqItem = faqItem,
                        searchQuery = searchQuery
                    )
                    if (index < category.questions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                vertical = DesignToken.Space.dp4,
                                horizontal = DesignToken.Space.dp4
                            ),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FAQItemRow(
    faqItem: FAQItem,
    searchQuery: String
) {
    var isAnswerVisible by remember { mutableStateOf(false) }

    // Auto-expand answer when searching and query matches
    if (searchQuery.isNotEmpty() && !isAnswerVisible) {
        if (faqItem.question.contains(searchQuery, ignoreCase = true) ||
            faqItem.answer.contains(searchQuery, ignoreCase = true)
        ) {
            isAnswerVisible = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DesignToken.Shape.medium)
            .clickable { isAnswerVisible = !isAnswerVisible }
            .padding(vertical = DesignToken.Space.dp8)
    ) {
        // Question row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isAnswerVisible) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(DesignToken.IconSize.small),
                tint = SkyBlue
            )

            Spacer(modifier = Modifier.width(DesignToken.Space.dp8))

            Text(
                text = faqItem.question,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f),
                color = if (isAnswerVisible) SkyBlue else MaterialTheme.colorScheme.onSurface
            )
        }

        // Animated answer
        AnimatedVisibility(
            visible = isAnswerVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Text(
                text = faqItem.answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    start = DesignToken.Space.dp8 + DesignToken.IconSize.small,
                    top = DesignToken.Space.dp8,
                    bottom = DesignToken.Space.dp4
                ),
                textAlign = TextAlign.Start
            )
        }
    }
}
