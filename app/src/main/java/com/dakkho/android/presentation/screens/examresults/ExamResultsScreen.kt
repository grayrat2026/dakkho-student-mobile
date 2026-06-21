package com.dakkho.android.presentation.screens.examresults

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.ExamModels.SubjectResult
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultsScreen(
    onBackClick: () -> Unit,
    viewModel: ExamResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "পরীক্ষার ফলাফল",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
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
            // Semester Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignToken.Spacing.md, vertical = DesignToken.Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.Spacing.sm)
            ) {
                (1..7).forEach { semester ->
                    FilterChip(
                        selected = uiState.selectedSemester == semester,
                        onClick = { viewModel.loadResults(semester) },
                        label = { Text(viewModel.toBengaliNumber(semester)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SkyBlue)
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))
                            OutlinedButton(onClick = { viewModel.loadResults(uiState.selectedSemester) }) {
                                Text("আবার চেষ্টা করুন")
                            }
                        }
                    }
                }
                uiState.result != null -> {
                    val result = uiState.result!!

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = DesignToken.Spacing.md,
                            vertical = DesignToken.Spacing.sm
                        ),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.Spacing.md)
                    ) {
                        // GPA Display Card
                        item {
                            GpaDisplayCard(
                                gpa = result.gpa,
                                earnedCredits = result.earnedCredits,
                                totalCredits = result.totalCredits,
                                toBengaliNumber = { viewModel.toBengaliNumber(it) }
                            )
                        }

                        // Subject-wise Marks Table
                        item {
                            Text(
                                text = "বিষয়ভিত্তিক নম্বর",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = DesignToken.Spacing.sm)
                            )
                        }

                        // Table Header
                        item {
                            MarksTableHeader()
                        }

                        // Table Rows
                        itemsIndexed(uiState.subjectResults) { index, subject ->
                            MarksTableRow(
                                subjectResult = subject,
                                isEven = index % 2 == 0,
                                toBengaliNumber = { viewModel.toBengaliNumber(it) }
                            )
                        }

                        // Analysis Section
                        item {
                            Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))
                            Text(
                                text = "বিশ্লেষণ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = DesignToken.Spacing.sm)
                            )
                        }

                        item {
                            MarksBarChart(
                                subjects = uiState.subjectResults,
                                toBengaliNumber = { viewModel.toBengaliNumber(it) }
                            )
                        }

                        // Bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(DesignToken.Spacing.lg))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GpaDisplayCard(
    gpa: Float,
    earnedCredits: Float,
    totalCredits: Float,
    toBengaliNumber: (Number) -> String
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large GPA number
            Column(horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.2f", gpa),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (gpa >= 3.5f) Green else if (gpa >= 3.0f) SkyBlue else Color(0xFFF97316)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GPA",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${toBengaliNumber(earnedCredits)} / ${toBengaliNumber(totalCredits)} ক্রেডিট অর্জিত",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Circular GPA indicator
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SkyBlue.copy(alpha = 0.2f),
                                SkyBlue.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(1.dp, SkyBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Grade,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = toBengaliNumber((gpa * 25).toInt()),
                        style = MaterialTheme.typography.labelSmall,
                        color = SkyBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.labelSmall,
                        color = SkyBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun MarksTableHeader() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignToken.Spacing.md, vertical = DesignToken.Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "বিষয়",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(2f)
            )
            Text(
                text = "ক্রেডিট",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "গ্রেড",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "নম্বর",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MarksTableRow(
    subjectResult: SubjectResult,
    isEven: Boolean,
    toBengaliNumber: (Number) -> String
) {
    val bgColor = if (isEven) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor, RoundedCornerShape(8.dp))
                .padding(horizontal = DesignToken.Spacing.md, vertical = DesignToken.Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subjectResult.subjectName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(2f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = toBengaliNumber(subjectResult.credit),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = subjectResult.letterGrade,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    subjectResult.letterGrade.startsWith("A") -> Green
                    subjectResult.letterGrade.startsWith("B") -> SkyBlue
                    else -> Color(0xFFF97316)
                },
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = toBengaliNumber(subjectResult.marksObtained.toInt()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MarksBarChart(
    subjects: List<SubjectResult>,
    toBengaliNumber: (Number) -> String
) {
    if (subjects.isEmpty()) return

    val maxMarks = remember(subjects) {
        subjects.maxOfOrNull { it.totalMarks } ?: 100f
    }
    val barColor = SkyBlue
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Spacing.md)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barCount = subjects.size
                val barSpacing = 12f
                val totalSpacing = barSpacing * (barCount + 1)
                val barWidth = (canvasWidth - totalSpacing) / barCount
                val chartHeight = canvasHeight - 40f // leave space for labels

                // Draw horizontal grid lines
                for (i in 0..4) {
                    val y = chartHeight - (chartHeight * i / 4f)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                    )
                }

                // Draw bars
                subjects.forEachIndexed { index, subject ->
                    val barHeight = (subject.marksObtained / maxMarks) * chartHeight
                    val x = barSpacing + index * (barWidth + barSpacing)
                    val y = chartHeight - barHeight

                    // Draw rounded rect bar
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Fill
                    )

                    // Draw marks on top of bar
                    val marksText = toBengaliNumber(subject.marksObtained.toInt())
                    val textLayoutResult = textMeasurer.measure(marksText, textStyle)
                    drawText(
                        textLayoutResult = textLayoutResult,
                        color = Color.White,
                        topLeft = Offset(
                            x + (barWidth - textLayoutResult.size.width) / 2f,
                            y + 4f
                        )
                    )

                    // Draw subject name below bar
                    val shortName = subject.subjectName.take(6)
                    val labelLayoutResult = textMeasurer.measure(shortName, textStyle)
                    drawText(
                        textLayoutResult = labelLayoutResult,
                        color = Color.Gray,
                        topLeft = Offset(
                            x + (barWidth - labelLayoutResult.size.width) / 2f,
                            chartHeight + 8f
                        )
                    )
                }
            }
        }
    }
}
