package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Discussion
import com.dakkho.android.domain.model.DiscussionReply

interface DiscussionRepository {

    suspend fun getDiscussions(
        courseId: String,
        page: Int = 1,
        limit: Int = 20
    ): Result<List<Discussion>>

    suspend fun getDiscussionDetail(
        threadId: String
    ): Result<Pair<Discussion, List<DiscussionReply>>>

    suspend fun createDiscussion(
        courseId: String,
        title: String,
        body: String,
        tags: List<String> = emptyList()
    ): Result<Discussion>

    suspend fun createReply(
        threadId: String,
        body: String
    ): Result<DiscussionReply>

    suspend fun toggleLike(
        threadId: String
    ): Result<Pair<Boolean, Int>>

    suspend fun deleteDiscussion(
        threadId: String
    ): Result<Unit>
}
