package com.dakkho.android.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.data.db.dao.UserDao
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.CourseDetail
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.model.Technology
import com.dakkho.android.domain.model.User
import com.dakkho.android.domain.model.WatchHistoryItem
import com.dakkho.android.domain.repository.CourseRepository
import com.dakkho.android.domain.repository.EnrollmentRepository
import com.dakkho.android.domain.repository.InstructorRepository
import com.dakkho.android.data.api.InstructorApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val currentUser: User? = null,
    val hasEnrollments: Boolean = false,
    val continueWatching: List<WatchHistoryItem> = emptyList(),
    val trendingCourses: List<Course> = emptyList(),
    val featuredInstructors: List<Instructor> = emptyList(),
    val technologies: List<Technology> = emptyList(),
    val enrolledCourses: List<Course> = emptyList(),
    val courseMap: Map<String, Course> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val userDao: UserDao,
    private val instructorRepository: InstructorRepository,
    private val instructorApiService: InstructorApiService,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedTechnology = MutableStateFlow<String?>(null)
    val selectedTechnology: StateFlow<String?> = _selectedTechnology.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load current user from Room
            loadCurrentUser()

            // Load all sections in parallel-like fashion
            launch { loadTrendingCourses() }
            launch { loadWatchHistory() }
            launch { loadFeaturedInstructors() }
            launch { loadTechnologies() }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadCurrentUser() {
        try {
            val userId = encryptedPrefsHelper.getUserId()
            if (userId != null) {
                val userEntity = userDao.getUser(userId)
                if (userEntity != null) {
                    val user = User(
                        id = userEntity.id,
                        email = userEntity.email,
                        fullName = userEntity.fullName,
                        instituteId = userEntity.instituteId,
                        technology = userEntity.technology,
                        avatarUrl = userEntity.avatarUrl,
                        role = userEntity.role,
                        phone = userEntity.phone,
                        createdAt = userEntity.createdAt
                    )
                    _uiState.update { it.copy(currentUser = user) }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load current user")
        }
    }

    private suspend fun loadTrendingCourses() {
        courseRepository.getCourses()
            .onSuccess { courses ->
                // Sort by enrollment count (descending) for trending
                val sorted = courses.sortedByDescending { it.enrollmentCount ?: 0 }
                val courseMap = courses.associateBy { it.id }
                _uiState.update {
                    it.copy(
                        trendingCourses = sorted,
                        courseMap = it.courseMap + courseMap
                    )
                }
            }
            .onFailure { e ->
                Timber.e(e, "Failed to load trending courses")
            }
    }

    private suspend fun loadWatchHistory() {
        enrollmentRepository.getWatchHistory()
            .onSuccess { historyItems ->
                val incompleteItems = historyItems.filter { !it.completed }
                _uiState.update {
                    it.copy(continueWatching = incompleteItems)
                }

                // Determine if user has enrollments (has any watch history)
                if (historyItems.isNotEmpty()) {
                    _uiState.update { it.copy(hasEnrollments = true) }

                    // Load course details for watch history items
                    val courseIds = historyItems.map { item -> item.courseId }.distinct()
                    loadEnrolledCourses(courseIds)
                }
            }
            .onFailure { e ->
                Timber.e(e, "Failed to load watch history")
            }
    }

    private suspend fun loadEnrolledCourses(courseIds: List<String>) {
        val enrolledCourses = mutableListOf<Course>()
        for (courseId in courseIds) {
            courseRepository.getCourseDetail(courseId)
                .onSuccess { detail ->
                    enrolledCourses.add(
                        Course(
                            id = detail.id,
                            title = detail.title,
                            description = detail.description,
                            instructorId = detail.instructorId,
                            instructorName = detail.instructorName,
                            technology = detail.technology,
                            price = detail.price,
                            discountedPrice = detail.discountedPrice,
                            thumbnailUrl = detail.thumbnailUrl,
                            isPublished = detail.isPublished,
                            rating = detail.rating,
                            enrollmentCount = detail.enrollmentCount,
                            durationHours = detail.durationHours,
                            level = detail.level,
                            createdAt = detail.createdAt
                        )
                    )
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to load enrolled course: $courseId")
                }
        }
        _uiState.update {
            it.copy(
                enrolledCourses = enrolledCourses,
                courseMap = it.courseMap + enrolledCourses.associateBy { course -> course.id }
            )
        }
    }

    private suspend fun loadFeaturedInstructors() {
        try {
            val result = instructorRepository.getInstructors(limit = 10)
            result.fold(
                onSuccess = { (instructors, _) ->
                    val sorted = instructors.sortedByDescending { it.rating }
                    _uiState.update { it.copy(featuredInstructors = sorted) }
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to load featured instructors")
                }
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to load featured instructors")
        }
    }

    private suspend fun loadTechnologies() {
        try {
            val response = instructorApiService.getTechnologies()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == null) {
                    val technologies = body.technologies.map { dto ->
                        Technology(
                            id = dto.id,
                            name = dto.name,
                            iconUrl = dto.iconUrl,
                            courseCount = dto.courseCount ?: 0
                        )
                    }
                    _uiState.update { it.copy(technologies = technologies) }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load technologies")
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            // Reload all data from API
            launch { loadCurrentUser() }
            launch { loadTrendingCourses() }
            launch { loadWatchHistory() }
            launch { loadFeaturedInstructors() }
            launch { loadTechnologies() }

            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun selectTechnology(technology: String?) {
        _selectedTechnology.value = technology

        if (technology != null) {
            viewModelScope.launch {
                courseRepository.getCoursesByTechnology(technology)
                    .onSuccess { courses ->
                        val sorted = courses.sortedByDescending { it.enrollmentCount ?: 0 }
                        _uiState.update { it.copy(trendingCourses = sorted) }
                    }
                    .onFailure { e ->
                        Timber.e(e, "Failed to filter courses by technology")
                    }
            }
        } else {
            // Reset to all courses
            viewModelScope.launch {
                loadTrendingCourses()
            }
        }
    }

    fun getLastWatchedCourse(): Course? {
        val history = _uiState.value.continueWatching
        if (history.isEmpty()) return null
        val lastItem = history.first()
        return _uiState.value.courseMap[lastItem.courseId]
    }

    fun getLastWatchProgress(): Float {
        val history = _uiState.value.continueWatching
        if (history.isEmpty()) return 0f
        val lastItem = history.first()
        return if (lastItem.totalSeconds > 0) {
            lastItem.progressSeconds.toFloat() / lastItem.totalSeconds.toFloat()
        } else {
            0f
        }
    }
}
