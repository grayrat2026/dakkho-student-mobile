package com.dakkho.android.presentation.screens.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.RatingBreakdown
import com.dakkho.android.domain.model.Review
import com.dakkho.android.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ReviewsUiState(
    val isLoading: Boolean = true,
    val reviews: List<Review> = emptyList(),
    val allReviews: List<Review> = emptyList(), // unfiltered copy for breakdown calculation
    val courseTitle: String = "",
    val averageRating: Float = 0f,
    val totalReviewCount: Int = 0,
    val ratingBreakdown: RatingBreakdown = RatingBreakdown(),
    val selectedRatingFilter: Int? = null, // null = all, 1-5 = specific star
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val isSubmittingReview: Boolean = false,
    val isWriteReviewVisible: Boolean = false,
    val writeReviewRating: Float = 0f,
    val writeReviewTitle: String = "",
    val writeReviewComment: String = "",
    val submitReviewSuccess: Boolean = false,
    val submitReviewError: String? = null,
    val error: String? = null
)

@HiltViewModel
class CourseReviewsViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    private var courseId: String = ""

    fun initialize(courseId: String, courseTitle: String, averageRating: Float, reviewCount: Int) {
        this.courseId = courseId
        _uiState.value = _uiState.value.copy(
            courseTitle = courseTitle,
            averageRating = averageRating,
            totalReviewCount = reviewCount
        )
        loadReviews()
    }

    fun loadReviews(page: Int = 1) {
        viewModelScope.launch {
            if (page == 1) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }
            courseRepository.getCourseReviews(
                courseId = courseId,
                page = page,
                limit = 20,
                rating = _uiState.value.selectedRatingFilter
            )
                .onSuccess { reviews ->
                    val currentReviews = if (page == 1) reviews else _uiState.value.reviews + reviews
                    val allReviews = if (_uiState.value.selectedRatingFilter == null) currentReviews else _uiState.value.allReviews

                    // Calculate breakdown from all reviews (unfiltered)
                    val breakdown = calculateRatingBreakdown(allReviews)

                    _uiState.value = _uiState.value.copy(
                        reviews = currentReviews,
                        allReviews = allReviews,
                        ratingBreakdown = breakdown,
                        totalReviewCount = allReviews.size,
                        averageRating = if (allReviews.isNotEmpty()) {
                            allReviews.map { it.rating }.average().toFloat()
                        } else 0f,
                        currentPage = page,
                        hasMorePages = reviews.size >= 20,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Load reviews failed")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load reviews"
                    )
                }
        }
    }

    fun setRatingFilter(rating: Int?) {
        _uiState.value = _uiState.value.copy(selectedRatingFilter = rating)
        loadReviews(page = 1)
    }

    fun loadMoreReviews() {
        if (_uiState.value.hasMorePages && !_uiState.value.isLoading) {
            loadReviews(page = _uiState.value.currentPage + 1)
        }
    }

    fun showWriteReview() {
        _uiState.value = _uiState.value.copy(
            isWriteReviewVisible = true,
            writeReviewRating = 0f,
            writeReviewTitle = "",
            writeReviewComment = "",
            submitReviewSuccess = false,
            submitReviewError = null
        )
    }

    fun hideWriteReview() {
        _uiState.value = _uiState.value.copy(isWriteReviewVisible = false)
    }

    fun setWriteReviewRating(rating: Float) {
        _uiState.value = _uiState.value.copy(writeReviewRating = rating)
    }

    fun setWriteReviewTitle(title: String) {
        _uiState.value = _uiState.value.copy(writeReviewTitle = title)
    }

    fun setWriteReviewComment(comment: String) {
        _uiState.value = _uiState.value.copy(writeReviewComment = comment)
    }

    fun submitReview() {
        val rating = _uiState.value.writeReviewRating
        if (rating <= 0f) {
            _uiState.value = _uiState.value.copy(submitReviewError = "Please select a rating")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingReview = true, submitReviewError = null)
            courseRepository.submitCourseReview(
                courseId = courseId,
                rating = rating,
                title = _uiState.value.writeReviewTitle.ifBlank { null },
                comment = _uiState.value.writeReviewComment.ifBlank { null }
            )
                .onSuccess { newReview ->
                    // Add new review to the top of the list
                    val updatedReviews = listOf(newReview) + _uiState.value.reviews
                    val updatedAllReviews = listOf(newReview) + _uiState.value.allReviews
                    val breakdown = calculateRatingBreakdown(updatedAllReviews)

                    _uiState.value = _uiState.value.copy(
                        reviews = updatedReviews,
                        allReviews = updatedAllReviews,
                        ratingBreakdown = breakdown,
                        totalReviewCount = updatedAllReviews.size,
                        averageRating = updatedAllReviews.map { it.rating }.average().toFloat(),
                        isSubmittingReview = false,
                        submitReviewSuccess = true,
                        isWriteReviewVisible = false
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Submit review failed")
                    _uiState.value = _uiState.value.copy(
                        isSubmittingReview = false,
                        submitReviewError = error.message ?: "Failed to submit review"
                    )
                }
        }
    }

    fun retry() {
        loadReviews(page = 1)
    }

    private fun calculateRatingBreakdown(reviews: List<Review>): RatingBreakdown {
        return RatingBreakdown(
            star5 = reviews.count { it.rating >= 4.5f },
            star4 = reviews.count { it.rating in 3.5f..<4.5f },
            star3 = reviews.count { it.rating in 2.5f..<3.5f },
            star2 = reviews.count { it.rating in 1.5f..<2.5f },
            star1 = reviews.count { it.rating < 1.5f }
        )
    }
}
