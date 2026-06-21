package com.dakkho.android.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.CourseApiService
import com.dakkho.android.data.api.InstructorApiService
import com.dakkho.android.data.db.dao.SearchHistoryDao
import com.dakkho.android.data.db.dao.SearchSuggestionDao
import com.dakkho.android.data.db.entity.SearchHistoryEntity
import com.dakkho.android.data.db.entity.SearchSuggestionEntity
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val suggestions: List<SearchSuggestionEntity> = emptyList(),
    val recentSearches: List<SearchHistoryEntity> = emptyList(),
    val courseResults: List<Course> = emptyList(),
    val instructorResults: List<Instructor> = emptyList(),
    val hasSearched: Boolean = false,
    val showClearConfirmation: Boolean = false,
    val error: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val courseApiService: CourseApiService,
    private val instructorApiService: InstructorApiService,
    private val searchHistoryDao: SearchHistoryDao,
    private val searchSuggestionDao: SearchSuggestionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")
    private var searchJob: Job? = null

    companion object {
        private const val DEBOUNCE_MS = 300L
        private const val MAX_HISTORY = 50
        private const val SUGGESTION_LIMIT = 8
    }

    init {
        loadRecentSearches()
        observeQueryForSuggestions()
    }

    /**
     * Observes query changes with 300ms debounce to provide
     * real-time FTS suggestions from local Room cache.
     */
    private fun observeQueryForSuggestions() {
        viewModelScope.launch {
            _queryFlow
                .debounce(DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotBlank() && query.length >= 2) {
                        loadLocalSuggestions(query)
                    } else {
                        _uiState.update { it.copy(suggestions = emptyList()) }
                    }
                }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        _queryFlow.value = query

        // Cancel previous search job if typing continues
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    suggestions = emptyList(),
                    courseResults = emptyList(),
                    instructorResults = emptyList(),
                    hasSearched = false,
                    isSearching = false
                )
            }
        }
    }

    /**
     * Performs the actual API search with debounced input.
     * Called when user submits the search (IME action or button).
     */
    fun performSearch() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, hasSearched = true, suggestions = emptyList()) }

            // Save to search history
            saveToHistory(query)

            // Search courses and instructors in parallel
            val courseDeferred = kotlinx.coroutines.async { searchCourses(query) }
            val instructorDeferred = kotlinx.coroutines.async { searchInstructors(query) }

            val courses = courseDeferred.await()
            val instructors = instructorDeferred.await()

            _uiState.update {
                it.copy(
                    courseResults = courses,
                    instructorResults = instructors,
                    isSearching = false,
                    error = if (courses.isEmpty() && instructors.isEmpty()) null else null
                )
            }

            // Update local suggestion index for future FTS queries
            indexSearchResults(query, courses, instructors)
        }
    }

    /**
     * Loads FTS suggestions from Room as the user types.
     */
    private suspend fun loadLocalSuggestions(query: String) {
        try {
            val ftsQuery = buildFtsQuery(query)
            val suggestions = searchSuggestionDao.search(ftsQuery, SUGGESTION_LIMIT)
            _uiState.update { it.copy(suggestions = suggestions) }
        } catch (e: Exception) {
            Timber.e(e, "FTS suggestion error")
            _uiState.update { it.copy(suggestions = emptyList()) }
        }
    }

    /**
     * Builds an FTS4-compatible MATCH query.
     * FTS4 uses * for prefix matching. We append * to each token.
     */
    private fun buildFtsQuery(query: String): String {
        return query
            .trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .joinToString(" AND ") { token ->
                "\"${token.replace("\"", "\"\"")}*\""
            }
    }

    private suspend fun searchCourses(query: String): List<Course> {
        return try {
            val response = courseApiService.getCourses(mapOf("search" to query))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    body.data.items.map { dto ->
                        Course(
                            id = dto.id,
                            title = dto.title,
                            description = dto.description,
                            instructorId = dto.instructorId,
                            instructorName = dto.instructorName,
                            technology = dto.technology,
                            price = dto.price,
                            discountedPrice = dto.discountedPrice,
                            thumbnailUrl = dto.thumbnailUrl,
                            isPublished = dto.isPublished ?: true,
                            rating = dto.rating,
                            enrollmentCount = dto.enrollmentCount,
                            durationHours = dto.durationHours,
                            level = dto.level,
                            createdAt = dto.createdAt
                        )
                    }
                } else emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Search courses API error")
            emptyList()
        }
    }

    private suspend fun searchInstructors(query: String): List<Instructor> {
        return try {
            val response = instructorApiService.getInstructorsPaginated(search = query)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == null) {
                    body.instructors
                        .map { dto ->
                            Instructor(
                                id = dto.id,
                                name = dto.name,
                                avatarUrl = dto.avatarUrl,
                                title = dto.specialization ?: dto.title,
                                courseCount = dto.totalCourses ?: dto.courseCount ?: 0,
                                studentCount = dto.totalStudents ?: dto.studentCount ?: 0,
                                rating = dto.rating ?: 0f
                            )
                        }
                } else emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Search instructors API error")
            emptyList()
        }
    }

    /**
     * Saves a search query to Room history, enforcing the 50-entry limit.
     */
    private suspend fun saveToHistory(query: String) {
        try {
            searchHistoryDao.insert(
                SearchHistoryEntity(query = query, queriedAt = System.currentTimeMillis())
            )

            // Enforce max history limit
            val allHistory = searchHistoryDao.getRecent(MAX_HISTORY + 10)
            if (allHistory.size > MAX_HISTORY) {
                val toDelete = allHistory.drop(MAX_HISTORY)
                toDelete.forEach { searchHistoryDao.deleteById(it.id) }
            }

            loadRecentSearches()
        } catch (e: Exception) {
            Timber.e(e, "Save search history error")
        }
    }

    /**
     * Indexes search results into the FTS suggestion table
     * so future searches can use local FTS first.
     */
    private suspend fun indexSearchResults(
        query: String,
        courses: List<Course>,
        instructors: List<Instructor>
    ) {
        try {
            val suggestions = mutableListOf<SearchSuggestionEntity>()

            courses.forEach { course ->
                suggestions.add(
                    SearchSuggestionEntity(
                        text = course.title,
                        type = "course",
                        referenceId = course.id,
                        thumbnailUrl = course.thumbnailUrl
                    )
                )
            }

            instructors.forEach { instructor ->
                suggestions.add(
                    SearchSuggestionEntity(
                        text = instructor.name,
                        type = "instructor",
                        referenceId = instructor.id,
                        thumbnailUrl = instructor.avatarUrl
                    )
                )
            }

            if (suggestions.isNotEmpty()) {
                searchSuggestionDao.insertAll(suggestions)
            }
        } catch (e: Exception) {
            Timber.e(e, "Index search results error")
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            try {
                val recent = searchHistoryDao.getRecent(15)
                _uiState.update { it.copy(recentSearches = recent) }
            } catch (e: Exception) {
                Timber.e(e, "Load recent searches error")
            }
        }
    }

    fun deleteSearchHistoryItem(id: Long) {
        viewModelScope.launch {
            try {
                searchHistoryDao.deleteById(id)
                loadRecentSearches()
            } catch (e: Exception) {
                Timber.e(e, "Delete search history item error")
            }
        }
    }

    fun showClearHistoryConfirmation() {
        _uiState.update { it.copy(showClearConfirmation = true) }
    }

    fun dismissClearHistoryConfirmation() {
        _uiState.update { it.copy(showClearConfirmation = false) }
    }

    fun clearAllSearchHistory() {
        viewModelScope.launch {
            try {
                searchHistoryDao.deleteAll()
                _uiState.update {
                    it.copy(recentSearches = emptyList(), showClearConfirmation = false)
                }
            } catch (e: Exception) {
                Timber.e(e, "Clear search history error")
            }
        }
    }

    fun getSearchResults(): List<SearchResult> {
        val results = mutableListOf<SearchResult>()

        _uiState.value.courseResults.forEach { course ->
            results.add(
                SearchResult(
                    id = course.id,
                    title = course.title,
                    description = course.description,
                    thumbnailUrl = course.thumbnailUrl,
                    type = "course",
                    rating = course.rating,
                    instructorName = course.instructorName,
                    price = course.discountedPrice ?: course.price
                )
            )
        }

        _uiState.value.instructorResults.forEach { instructor ->
            results.add(
                SearchResult(
                    id = instructor.id,
                    title = instructor.name,
                    description = instructor.title,
                    thumbnailUrl = instructor.avatarUrl,
                    type = "instructor",
                    rating = instructor.rating
                )
            )
        }

        return results
    }

    fun selectSuggestion(suggestion: SearchSuggestionEntity) {
        _uiState.update { it.copy(query = suggestion.text) }
        _queryFlow.value = suggestion.text
        performSearch()
    }

    fun selectRecentSearch(query: String) {
        _uiState.update { it.copy(query = query) }
        _queryFlow.value = query
        performSearch()
    }
}
