package com.dakkho.android.data.api

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int? = null, val message: String?) : ApiResponse<Nothing>()
    data object Loading : ApiResponse<Nothing>()
}

data class ApiResult<T>(
    val success: Boolean,
    val data: T?,
    val message: String?
)

data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)
