package com.dakkho.android.presentation.screens.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SocialApiService
import com.dakkho.android.domain.model.FeedbackCategory
import com.dakkho.android.domain.model.SubmitFeedbackRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val socialApiService: SocialApiService
) : ViewModel() {

    private val _rating = MutableStateFlow(0)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private val _category = MutableStateFlow(FeedbackCategory.GENERAL)
    val category: StateFlow<FeedbackCategory> = _category.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted: StateFlow<Boolean> = _isSubmitted.asStateFlow()

    fun setRating(value: Int) { _rating.value = value }
    fun setComment(value: String) { _comment.value = value }
    fun setCategory(value: FeedbackCategory) { _category.value = value }

    fun submitFeedback() {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val request = SubmitFeedbackRequest(
                    rating = _rating.value,
                    comment = _comment.value,
                    category = _category.value.value
                )
                val response = socialApiService.submitFeedback(request)
                if (response.isSuccessful) {
                    _isSubmitted.value = true
                }
            } catch (_: Exception) {
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
