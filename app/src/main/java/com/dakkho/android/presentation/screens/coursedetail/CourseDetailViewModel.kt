package com.dakkho.android.presentation.screens.coursedetail

import androidx.compose.ui.util.lerp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.CourseDetail
import com.dakkho.android.domain.model.CoursePackage
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.Enrollment
import com.dakkho.android.domain.model.Review
import com.dakkho.android.domain.repository.CourseRepository
import com.dakkho.android.domain.repository.EnrollmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val course: CourseDetail? = null,
    val curriculum: Curriculum? = null,
    val reviews: List<Review> = emptyList(),
    val packages: List<CoursePackage> = emptyList(),
    val enrollment: Enrollment? = null,
    val isEnrolled: Boolean = false,
    val enrollmentProgress: Float = 0f,
    val isEnrollmentChecking: Boolean = true,
    val isEnrolling: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val error: String? = null,
    val isOffline: Boolean = false
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val enrollmentRepository: EnrollmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseDetailUiState())
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    private var courseId: String = ""

    fun initialize(courseId: String) {
        this.courseId = courseId
        loadCourseDetail()
        checkEnrollment()
        loadCoursePackages()
    }

    fun loadCourseDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            courseRepository.getCourseDetail(courseId)
                .onSuccess { detail ->
                    _uiState.value = _uiState.value.copy(
                        course = detail,
                        isLoading = false,
                        isOffline = false
                    )
                    // Load curriculum after course detail
                    loadCurriculum()
                    // Load reviews
                    loadReviews()
                }
                .onFailure { error ->
                    Timber.e(error, "Load course detail failed")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load course detail",
                        isOffline = error.message?.contains("offline") == true
                    )
                }
        }
    }

    private fun loadCurriculum() {
        viewModelScope.launch {
            courseRepository.getCourseCurriculum(courseId)
                .onSuccess { curriculum ->
                    _uiState.value = _uiState.value.copy(curriculum = curriculum)
                }
                .onFailure { error ->
                    Timber.e(error, "Load curriculum failed")
                }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            courseRepository.getCourseReviews(courseId)
                .onSuccess { reviews ->
                    _uiState.value = _uiState.value.copy(reviews = reviews)
                }
                .onFailure { error ->
                    Timber.e(error, "Load reviews failed")
                }
        }
    }

    private fun checkEnrollment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEnrollmentChecking = true)
            enrollmentRepository.checkEnrollment(courseId)
                .onSuccess { enrollment ->
                    _uiState.value = _uiState.value.copy(
                        enrollment = enrollment,
                        isEnrolled = enrollment != null,
                        enrollmentProgress = enrollment?.progress ?: 0f,
                        isEnrollmentChecking = false
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Check enrollment failed")
                    _uiState.value = _uiState.value.copy(
                        isEnrollmentChecking = false
                    )
                }
        }
    }

    private fun loadCoursePackages() {
        viewModelScope.launch {
            courseRepository.getCoursePackages(courseId)
                .onSuccess { packages ->
                    _uiState.value = _uiState.value.copy(packages = packages)
                }
                .onFailure { error ->
                    Timber.e(error, "Load course packages failed")
                }
        }
    }

    fun showEnrollBottomSheet() {
        _uiState.value = _uiState.value.copy(isBottomSheetVisible = true)
    }

    fun hideEnrollBottomSheet() {
        _uiState.value = _uiState.value.copy(isBottomSheetVisible = false)
    }

    fun onEnrollClick(packageId: String? = null) {
        val course = _uiState.value.course ?: return
        if (course.price == null || course.price == 0.0) {
            // Free course - direct enrollment
            performFreeEnrollment()
        } else {
            // Paid course - create payment via PipraPay
            createPayment()
        }
    }

    private fun performFreeEnrollment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEnrolling = true)
            // Simulate enrollment - actual API call will be added when backend supports it
            _uiState.value = _uiState.value.copy(
                isEnrolling = false,
                isEnrolled = true,
                enrollmentProgress = 0f,
                isBottomSheetVisible = false
            )
        }
    }

    private fun createPayment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEnrolling = true)
            enrollmentRepository.createPayment(courseId)
                .onSuccess { payment ->
                    _uiState.value = _uiState.value.copy(
                        isEnrolling = false,
                        isBottomSheetVisible = false
                    )
                    // Payment URL will be handled by the screen to open Chrome Custom Tabs
                }
                .onFailure { error ->
                    Timber.e(error, "Create payment failed")
                    _uiState.value = _uiState.value.copy(
                        isEnrolling = false,
                        error = error.message ?: "Payment failed"
                    )
                }
        }
    }

    fun getPaymentUrl(): String? {
        // This will be used by the screen to open PipraPay
        return null // Will be populated after createPayment succeeds
    }

    fun retry() {
        loadCourseDetail()
        checkEnrollment()
    }

    fun onCourseNotFound() {
        _uiState.value = _uiState.value.copy(
            error = "Course not found",
            isLoading = false
        )
    }
}

/**
 * Enum representing the 3-state enroll button behavior.
 */
enum class EnrollButtonState {
    /** Not enrolled, show "Enroll Now" with gradient */
    NOT_ENROLLED,
    /** Just enrolled, show "Enrolled - Continue" with outlined style */
    JUST_ENROLLED,
    /** Already enrolled with progress, show "Continue" with green style */
    CONTINUE
}

fun CourseDetailUiState.getEnrollButtonState(): EnrollButtonState {
    return when {
        !isEnrolled -> EnrollButtonState.NOT_ENROLLED
        enrollmentProgress > 0f -> EnrollButtonState.CONTINUE
        else -> EnrollButtonState.JUST_ENROLLED
    }
}
