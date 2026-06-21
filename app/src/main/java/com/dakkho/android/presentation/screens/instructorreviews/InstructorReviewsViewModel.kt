package com.dakkho.android.presentation.screens.instructorreviews

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.RatingBreakdown
import com.dakkho.android.domain.model.Review
import com.dakkho.android.domain.repository.InstructorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InstructorReviewsUiState(
    val instructorName: String = "",
    val reviews: List<Review> = emptyList(),
    val averageRating: Float = 0f,
    val ratingBreakdown: RatingBreakdown = RatingBreakdown(),
    val totalReviews: Int = 0,
    val selectedRatingFilter: Int? = null,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true
)

@HiltViewModel
class InstructorReviewsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val instructorRepository: InstructorRepository
) : ViewModel() {

    private val instructorId: String = savedStateHandle["instructorId"] ?: ""
    private val instructorName: String = savedStateHandle["instructorName"] ?: ""

    private val _uiState = MutableStateFlow(InstructorReviewsUiState(instructorName = instructorName))
    val uiState: StateFlow<InstructorReviewsUiState> = _uiState.asStateFlow()

    private val pageSize = 20

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = instructorRepository.getInstructorReviews(
                instructorId, pageSize, 0, _uiState.value.selectedRatingFilter
            )
            result.fold(
                onSuccess = { reviewsResult ->
                    _uiState.update {
                        it.copy(
                            reviews = reviewsResult.reviews,
                            totalReviews = reviewsResult.total,
                            averageRating = reviewsResult.averageRating,
                            ratingBreakdown = reviewsResult.ratingBreakdown,
                            hasMore = reviewsResult.reviews.size < reviewsResult.total,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message ?: "Failed to load reviews"
                        )
                    }
                }
            )
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (!state.hasMore || state.isLoadingMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val offset = state.reviews.size
            val result = instructorRepository.getInstructorReviews(
                instructorId, pageSize, offset, state.selectedRatingFilter
            )
            result.fold(
                onSuccess = { reviewsResult ->
                    _uiState.update {
                        it.copy(
                            reviews = it.reviews + reviewsResult.reviews,
                            totalReviews = reviewsResult.total,
                            averageRating = reviewsResult.averageRating,
                            ratingBreakdown = reviewsResult.ratingBreakdown,
                            hasMore = it.reviews.size + reviewsResult.reviews.size < reviewsResult.total,
                            isLoadingMore = false
                        )
                    }
                },
                onFailure = { _ ->
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
            )
        }
    }

    fun setRatingFilter(rating: Int?) {
        _uiState.update { it.copy(selectedRatingFilter = rating, reviews = emptyList()) }
        loadReviews()
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadReviews()
    }
}
