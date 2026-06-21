package com.dakkho.android.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dakkho.android.data.api.CourseApiService
import com.dakkho.android.domain.model.CourseDto

class CoursePagingSource(
    private val courseApiService: CourseApiService,
    private val params: Map<String, String>
) : PagingSource<Int, CourseDto>() {

    override fun getRefreshKey(state: PagingState<Int, CourseDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CourseDto> {
        val page = params.key ?: 1
        return try {
            val queryParams = this.params.toMutableMap()
            queryParams["page"] = page.toString()
            queryParams["limit"] = params.loadSize.toString()

            val response = courseApiService.getCourses(queryParams)
            if (response.isSuccessful) {
                val body = response.body()
                val courses = body?.data?.items ?: emptyList()
                val totalPages = body?.data?.totalPages ?: 1

                LoadResult.Page(
                    data = courses,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page >= totalPages) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
