package com.dakkho.android.data.api

import com.dakkho.android.domain.model.AboutData
import com.dakkho.android.domain.model.ApiResult
import retrofit2.Response
import retrofit2.http.GET

interface AboutApiService {

    @GET("api/about")
    suspend fun getAbout(): Response<ApiResult<AboutData>>
}
