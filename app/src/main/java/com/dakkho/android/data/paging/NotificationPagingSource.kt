package com.dakkho.android.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dakkho.android.data.api.NotificationApiService
import com.dakkho.android.domain.model.NotificationDto
import timber.log.Timber

class NotificationPagingSource(
    private val notificationApiService: NotificationApiService,
    private val filterParams: Map<String, String> = emptyMap()
) : PagingSource<Int, NotificationDto>() {

    override fun getRefreshKey(state: PagingState<Int, NotificationDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationDto> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        return try {
            val apiParams = filterParams.toMutableMap()
            apiParams["page"] = page.toString()
            apiParams["pageSize"] = pageSize.toString()

            val response = notificationApiService.getNotifications(apiParams)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val notifications = body.data.items
                    val totalPages = body.data.totalPages

                    LoadResult.Page(
                        data = notifications,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (page >= totalPages) null else page + 1
                    )
                } else {
                    LoadResult.Error(Exception(body?.message ?: "Failed to load notifications"))
                }
            } else {
                LoadResult.Error(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "NotificationPagingSource load error")
            LoadResult.Error(e)
        }
    }
}
