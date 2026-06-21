package com.dakkho.android.data.api

import com.dakkho.android.domain.model.AchievementListResponse
import retrofit2.Response
import retrofit2.http.GET

interface AchievementApiService {

    @GET("student/achievements")
    suspend fun getAchievements(): Response<AchievementListResponse>
}
