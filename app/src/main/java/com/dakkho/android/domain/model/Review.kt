package com.dakkho.android.domain.model

data class Review(
    val id: String,
    val userId: String,
    val courseId: String,
    val userName: String? = null,
    val userAvatar: String? = null,
    val rating: Float,
    val title: String? = null,
    val comment: String? = null,
    val createdAt: String? = null
)

data class CoursePackage(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: Double? = null,
    val isFree: Boolean = false,
    val features: List<String> = emptyList(),
    val courseIds: List<String> = emptyList(),
    val isActive: Boolean = true
)

/**
 * Rating breakdown for a course — count per star level.
 */
data class RatingBreakdown(
    val star5: Int = 0,
    val star4: Int = 0,
    val star3: Int = 0,
    val star2: Int = 0,
    val star1: Int = 0
) {
    val total: Int get() = star5 + star4 + star3 + star2 + star1

    fun getPercentage(star: Int): Float {
        if (total == 0) return 0f
        return when (star) {
            5 -> star5.toFloat() / total * 100f
            4 -> star4.toFloat() / total * 100f
            3 -> star3.toFloat() / total * 100f
            2 -> star2.toFloat() / total * 100f
            1 -> star1.toFloat() / total * 100f
            else -> 0f
        }
    }
}
