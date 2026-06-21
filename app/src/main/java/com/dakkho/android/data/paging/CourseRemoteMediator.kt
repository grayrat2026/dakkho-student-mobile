package com.dakkho.android.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.dakkho.android.data.api.CourseApiService
import com.dakkho.android.data.db.dao.CourseDao
import com.dakkho.android.data.db.dao.RemoteKeysDao
import com.dakkho.android.data.db.entity.CourseEntity
import com.dakkho.android.data.db.entity.RemoteKeysEntity
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class CourseRemoteMediator(
    private val courseApiService: CourseApiService,
    private val courseDao: CourseDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val filterParams: Map<String, String>
) : RemoteMediator<Int, CourseEntity>() {

    private val CACHE_TIMEOUT_MS = TimeUnit.HOURS.toMillis(2)

    override suspend fun initialize(): InitializeAction {
        // Always refresh since filters may have changed and
        // we need to sync the Room cache with the current filter params
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CourseEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val queryParams = filterParams.toMutableMap()
            queryParams["page"] = page.toString()
            queryParams["limit"] = state.config.pageSize.toString()

            val response = courseApiService.getCourses(queryParams)
            if (response.isSuccessful) {
                val body = response.body()
                val courses = body?.data?.items ?: emptyList()
                val totalPages = body?.data?.totalPages ?: 1
                val endOfPaginationReached = page >= totalPages

                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearAll()
                    courseDao.deleteAll()
                }

                val remoteKeys = courses.map { course ->
                    RemoteKeysEntity(
                        courseId = course.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                }

                remoteKeysDao.insertAll(remoteKeys)
                courseDao.insertAll(courses.map { mapCourseDtoToEntity(it) })

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                MediatorResult.Error(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "RemoteMediator load error")
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, CourseEntity>
    ): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { courseId ->
                remoteKeysDao.getRemoteKeyByCourseId(courseId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, CourseEntity>
    ): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { course ->
                remoteKeysDao.getRemoteKeyByCourseId(course.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, CourseEntity>
    ): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { course ->
                remoteKeysDao.getRemoteKeyByCourseId(course.id)
            }
    }

    private fun mapCourseDtoToEntity(dto: CourseDto): CourseEntity {
        return CourseEntity(
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
