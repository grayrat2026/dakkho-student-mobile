package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.LiveClass

interface LiveClassRepository {

    suspend fun getLiveClasses(
        status: String? = null,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<LiveClass>>

    suspend fun getFeaturedLiveClasses(): Result<List<LiveClass>>

    suspend fun getLiveClassDetail(id: String): Result<LiveClass>

    suspend fun joinLiveClass(id: String): Result<LiveClassJoinResult>

    suspend fun toggleReminder(id: String): Result<Boolean>
}

data class LiveClassJoinResult(
    val token: String? = null,
    val livekitUrl: String? = null,
    val roomName: String? = null,
    val meetingUrl: String? = null
)
