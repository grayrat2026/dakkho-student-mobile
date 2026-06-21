package com.dakkho.android.presentation.screens.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.FeedbackCategory
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    onBackClick: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val rating by viewModel.rating
    val comment by viewModel.comment
    val category by viewModel.category
    val isSubmitting by viewModel.isSubmitting
    val isSubmitted by viewModel.isSubmitted

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "মতামত দিন", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp16),
            verticalArrangement = Arrangement.Center
        ) {
            if (isSubmitted) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(64.dp), tint = SkyBlue)
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                    Text(text = "ধন্যবাদ!", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Text(text = "আপনার মতামত জমা হয়েছে", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                // Rating Stars
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(DesignToken.Space.dp24), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "আপনার অভিজ্ঞতা কেমন?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                        Row(horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)) {
                            for (i in 1..5) {
                                IconButton(onClick = { viewModel.setRating(i) }) {
                                    Icon(
                                        imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = if (i <= rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                        val ratingText = when (rating) {
                            1 -> "খারাপ"
                            2 -> "মোটামুটি"
                            3 -> "ভালো"
                            4 -> "খুব ভালো"
                            5 -> "অসাধারণ"
                            else -> ""
                        }
                        if (rating > 0) {
                            Text(text = ratingText, style = MaterialTheme.typography.bodyLarge, color = SkyBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

                // Category
                Text(text = "বিভাগ", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                Row(horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)) {
                    FeedbackCategory.entries.forEach { cat ->
                        FilterChip(selected = category == cat, onClick = { viewModel.setCategory(cat) }, label = { Text(text = cat.label) })
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

                // Comment
                OutlinedTextField(
                    value = comment,
                    onValueChange = { viewModel.setComment(it) },
                    label = { Text("আপনার মতামত লিখুন") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

                GradientButton(
                    text = "জমা দিন",
                    onClick = { viewModel.submitFeedback() },
                    enabled = rating > 0 && comment.isNotBlank(),
                    isLoading = isSubmitting,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )
            }
        }
    }
}
