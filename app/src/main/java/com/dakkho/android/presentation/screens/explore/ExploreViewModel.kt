package com.dakkho.android.presentation.screens.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.map
import com.dakkho.android.data.api.CourseApiService
import com.dakkho.android.data.api.InstructorApiService
import com.dakkho.android.data.db.dao.CourseDao
import com.dakkho.android.data.db.dao.RemoteKeysDao
import com.dakkho.android.data.paging.CoursePagingSource
import com.dakkho.android.data.paging.CourseRemoteMediator
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.ExploreFilters
import com.dakkho.android.domain.model.PriceType
import com.dakkho.android.domain.model.SortOption
import com.dakkho.android.domain.model.Technology
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val courseApiService: CourseApiService,
    private val courseDao: CourseDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val instructorApiService: InstructorApiService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_TECHNOLOGY = "filter_technology"
        private const val KEY_LEVEL = "filter_level"
        private const val KEY_PRICE_TYPE = "filter_price_type"
        private const val KEY_SORT_BY = "filter_sort_by"
    }

    private val _filters = MutableStateFlow(ExploreFilters())
    val filters: StateFlow<ExploreFilters> = _filters.asStateFlow()

    private val _technologies = MutableStateFlow<List<Technology>>(emptyList())
    val technologies: StateFlow<List<Technology>> = _technologies.asStateFlow()

    private val _isSearchExpanded = MutableStateFlow(false)
    val isSearchExpanded: StateFlow<Boolean> = _isSearchExpanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Restore filters from SavedStateHandle
    init {
        savedStateHandle.get<String>(KEY_TECHNOLOGY)?.let { _filters.update { f -> f.copy(technology = it) } }
        savedStateHandle.get<String>(KEY_LEVEL)?.let { _filters.update { f -> f.copy(level = it) } }
        savedStateHandle.get<String>(KEY_PRICE_TYPE)?.let {
            try { _filters.update { f -> f.copy(priceType = PriceType.valueOf(it)) } } catch (_: Exception) {}
        }
        savedStateHandle.get<String>(KEY_SORT_BY)?.let {
            try { _filters.update { f -> f.copy(sortBy = SortOption.valueOf(it)) } } catch (_: Exception) {}
        }
        loadTechnologies()
    }

    // PagingData flow — re-creates Pager whenever filters change
    val pagingDataFlow: Flow<PagingData<Course>> = _filters
        .flatMapLatest { filters ->
            val apiParams = buildApiParams(filters)
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 5,
                    enablePlaceholders = true,
                    initialLoadSize = 40
                ),
                remoteMediator = CourseRemoteMediator(
                    courseApiService = courseApiService,
                    courseDao = courseDao,
                    remoteKeysDao = remoteKeysDao,
                    filterParams = apiParams
                ),
                pagingSourceFactory = {
                    // Room PagingSource — observes DB changes triggered by RemoteMediator
                    courseDao.getCoursesPagingSource()
                }
            ).flow.map { pagingData ->
                pagingData.map { entity ->
                    Course(
                        id = entity.id,
                        title = entity.title,
                        description = entity.description,
                        instructorId = entity.instructorId,
                        instructorName = entity.instructorName,
                        technology = entity.technology,
                        price = entity.price,
                        discountedPrice = entity.discountedPrice,
                        thumbnailUrl = entity.thumbnailUrl,
                        isPublished = entity.isPublished,
                        rating = entity.rating,
                        enrollmentCount = entity.enrollmentCount,
                        durationHours = entity.durationHours,
                        level = entity.level,
                        createdAt = entity.createdAt
                    )
                }
            }
        }
        .cachedIn(viewModelScope)

    // Simple search paging — when user types in search bar
    val searchPagingFlow: Flow<PagingData<Course>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                Pager(PagingConfig(pageSize = 1)) { EmptyPagingSource() }.flow
            } else {
                val params = buildApiParams(_filters.value).toMutableMap()
                params["search"] = query
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        prefetchDistance = 5,
                        enablePlaceholders = false,
                        initialLoadSize = 40
                    ),
                    pagingSourceFactory = {
                        CoursePagingSource(courseApiService, params)
                    }
                ).flow.map { pagingData ->
                    pagingData.map { dto ->
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
                }
            }
        }
        .cachedIn(viewModelScope)

    fun setTechnology(technology: String?) {
        _filters.update { it.copy(technology = technology) }
        savedStateHandle[KEY_TECHNOLOGY] = technology
    }

    fun setLevel(level: String?) {
        _filters.update { it.copy(level = level) }
        savedStateHandle[KEY_LEVEL] = level
    }

    fun setPriceType(priceType: PriceType) {
        _filters.update { it.copy(priceType = priceType) }
        savedStateHandle[KEY_PRICE_TYPE] = priceType.name
    }

    fun setSortBy(sortOption: SortOption) {
        _filters.update { it.copy(sortBy = sortOption) }
        savedStateHandle[KEY_SORT_BY] = sortOption.name
    }

    fun clearFilters() {
        _filters.update { ExploreFilters() }
        savedStateHandle[KEY_TECHNOLOGY] = null
        savedStateHandle[KEY_LEVEL] = null
        savedStateHandle[KEY_PRICE_TYPE] = null
        savedStateHandle[KEY_SORT_BY] = null
    }

    fun toggleSearchExpanded() {
        _isSearchExpanded.update { !it }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadTechnologies() {
        viewModelScope.launch {
            try {
                val response = instructorApiService.getTechnologies()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        _technologies.value = body.data.map { dto ->
                            Technology(
                                id = dto.id,
                                name = dto.name,
                                iconUrl = dto.iconUrl,
                                courseCount = dto.courseCount ?: 0
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load technologies for Explore")
            }
        }
    }

    private fun buildApiParams(filters: ExploreFilters): Map<String, String> {
        val params = mutableMapOf<String, String>()
        filters.technology?.let { params["technology"] = it }
        filters.level?.let { params["level"] = it }
        when (filters.priceType) {
            PriceType.FREE -> params["price"] = "0"
            PriceType.PAID -> params["price"] = "gt:0"
            PriceType.ALL -> { /* no filter */ }
        }
        when (filters.sortBy) {
            SortOption.LATEST -> params["sort"] = "created_at:desc"
            SortOption.POPULAR -> params["sort"] = "enrollment_count:desc"
            SortOption.RATING -> params["sort"] = "rating:desc"
            SortOption.PRICE_LOW_HIGH -> params["sort"] = "price:asc"
            SortOption.PRICE_HIGH_LOW -> params["sort"] = "price:desc"
        }
        return params
    }

    // Empty paging source for blank search
    private class EmptyPagingSource : PagingSource<Int, Course>() {
        override fun getRefreshKey(state: PagingState<Int, Course>): Int? = null
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Course> =
            LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
    }
}
