package com.dakkho.android.presentation.screens.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dakkho.android.data.api.CourseApiService
import com.dakkho.android.data.db.dao.CourseDao
import com.dakkho.android.data.db.dao.RemoteKeysDao
import com.dakkho.android.data.paging.CourseRemoteMediator
import com.dakkho.android.domain.model.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val courseApiService: CourseApiService,
    private val courseDao: CourseDao,
    private val remoteKeysDao: RemoteKeysDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val technology: String = savedStateHandle["technology"] ?: ""

    val pagingDataFlow: Flow<PagingData<Course>> = Pager(
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
            filterParams = mapOf("technology" to technology)
        ),
        pagingSourceFactory = {
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
    }.cachedIn(viewModelScope)
}
