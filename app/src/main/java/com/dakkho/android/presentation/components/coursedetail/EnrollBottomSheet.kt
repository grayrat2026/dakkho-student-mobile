package com.dakkho.android.presentation.components.coursedetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dakkho.android.domain.model.CoursePackage
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollBottomSheet(
    packages: List<CoursePackage>,
    isEnrolling: Boolean,
    onDismiss: () -> Unit,
    onEnrollClick: (packageId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedPackageId by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Text(
                text = "Choose a Plan",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Select the package that works best for you",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (packages.isEmpty()) {
                // Default free + premium cards when no packages from API
                DefaultPackageCards(
                    selectedPackageId = selectedPackageId,
                    onSelectPackage = { selectedPackageId = it }
                )
            } else {
                // API-provided packages
                packages.forEach { pkg ->
                    CoursePackageCard(
                        coursePackage = pkg,
                        isSelected = selectedPackageId == pkg.id,
                        onSelect = { selectedPackageId = pkg.id }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enroll CTA
            GradientButton(
                text = if (isEnrolling) "Enrolling..." else "Start Learning",
                onClick = { onEnrollClick(selectedPackageId) },
                enabled = !isEnrolling,
                isLoading = isEnrolling,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DefaultPackageCards(
    selectedPackageId: String?,
    onSelectPackage: (String?) -> Unit
) {
    // Free tier
    CoursePackageCard(
        coursePackage = CoursePackage(
            id = "free",
            name = "Free Access",
            description = "Access free lessons and course preview content",
            price = 0.0,
            isFree = true,
            features = listOf(
                "Free preview lessons",
                "Course curriculum access",
                "Community Q&A (read-only)"
            )
        ),
        isSelected = selectedPackageId == "free",
        onSelect = { onSelectPackage("free") }
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Premium tier
    CoursePackageCard(
        coursePackage = CoursePackage(
            id = "premium",
            name = "Full Course Access",
            description = "Unlock all lessons, quizzes, and certificates",
            price = null,
            isFree = false,
            features = listOf(
                "All video lessons",
                "Downloadable resources",
                "Quizzes & assignments",
                "Certificate of completion",
                "Direct Q&A with instructor",
                "Lifetime access"
            )
        ),
        isSelected = selectedPackageId == "premium",
        onSelect = { onSelectPackage("premium") }
    )
}
