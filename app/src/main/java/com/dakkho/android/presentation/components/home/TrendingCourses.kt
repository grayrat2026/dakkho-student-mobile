package com.dakkho.android.presentation.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dakkho.android.domain.model.Course
import com.dakkho.android.presentation.components.CourseCard
import com.dakkho.android.presentation.components.CourseCardSkeleton
import com.dakkho.android.presentation.theme.DesignToken

@Composable
fun TrendingCourses(
    courses: List<Course>,
    onCourseClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Skip section if not loading and no courses
    if (!isLoading && courses.isEmpty()) return

    Column(modifier = modifier) {
        SectionHeader(
            title = "Trending Courses \uD83D\uDD25",
            showSeeAll = true,
            onSeeAllClick = onSeeAllClick
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        LazyRow(
            contentPadding = PaddingValues(horizontal = DesignToken.Space.dp16),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            if (isLoading && courses.isEmpty()) {
                // Show skeleton items while loading
                items(3) {
                    CourseCardSkeleton(
                        modifier = Modifier.width(180.dp)
                    )
                }
            } else {
                items(courses, key = { it.id }) { course ->
                    CourseCard(
                        course = course,
                        onClick = onCourseClick,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        }
    }
}
