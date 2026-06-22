package com.dakkho.android.presentation.screens.help

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.HelpCategory
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBackClick: () -> Unit,
    onNavigateToFAQ: () -> Unit = {},
    onNavigateToContactSupport: () -> Unit = {},
    onNavigateToReportIssue: () -> Unit = {},
    viewModel: HelpViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var searchQuery by remember { mutableStateOf("") }

    val categories = remember {
        listOf(
            HelpCategory("getting_started", "শুরু করুন", "অ্যাপ ব্যবহারের মূল বিষয়", "school", 5),
            HelpCategory("courses", "কোর্স সম্পর্কে", "কোর্স, এনরোলমেন্ট, প্লেব্যাক", "courses", 8),
            HelpCategory("payment", "পেমেন্ট", "পেমেন্ট, রিফান্ড, সাবস্ক্রিপশন", "payment", 6),
            HelpCategory("technical", "প্রযুক্তিগত", "বাগ, ক্র্যাশ, ডাউনলোড সমস্যা", "technical", 7)
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "সাহায্য ও সমর্থন",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = DesignToken.Space.dp16)
        ) {
            // Search
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {},
                active = false,
                onActiveChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = DesignToken.Space.dp8),
                placeholder = { Text("সাহায্য খুঁজুন...") }
            ) {}

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Category Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12),
                modifier = Modifier.weight(1f)
            ) {
                items(categories) { category ->
                    HelpCategoryCard(
                        category = category,
                        onClick = {
                            when (category.id) {
                                "courses", "getting_started" -> onNavigateToFAQ()
                                "payment" -> onNavigateToFAQ()
                                "technical" -> onNavigateToReportIssue()
                            }
                        }
                    )
                }
            }

            // Quick Actions
            Text(
                text = "দ্রুত সংযোগ",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    QuickActionRow(
                        icon = Icons.Default.School,
                        title = "সাধারণ প্রশ্ন",
                        subtitle = "প্রায়শই জিজ্ঞাসিত প্রশ্ন",
                        onClick = onNavigateToFAQ
                    )
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(horizontal = DesignToken.Space.dp16),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                    QuickActionRow(
                        icon = Icons.Default.ContactSupport,
                        title = "সাপোর্টে যোগাযোগ",
                        subtitle = "চ্যাট, ইমেইল, ফোন",
                        onClick = onNavigateToContactSupport
                    )
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(horizontal = DesignToken.Space.dp16),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                    QuickActionRow(
                        icon = Icons.Default.Build,
                        title = "সমস্যা রিপোর্ট",
                        subtitle = "বাগ রিপোর্ট করুন",
                        onClick = onNavigateToReportIssue
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
        }
    }
}

@Composable
private fun HelpCategoryCard(
    category: HelpCategory,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignToken.Space.dp16))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(DesignToken.Space.dp16),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = when (category.id) {
                    "getting_started" -> Icons.Default.School
                    "courses" -> Icons.Default.School
                    "payment" -> Icons.Default.Payment
                    "technical" -> Icons.Default.Build
                    else -> Icons.Default.School
                },
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = SkyBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = "${category.articleCount} টি নিবন্ধ",
                style = MaterialTheme.typography.labelSmall,
                color = SkyBlue
            )
        }
    }
}

@Composable
private fun QuickActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(DesignToken.IconSize.medium))
        Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}
