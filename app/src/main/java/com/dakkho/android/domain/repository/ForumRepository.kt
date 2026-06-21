package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.ForumComment
import com.dakkho.android.domain.model.ForumThread

interface ForumRepository {

    suspend fun getForumThreads(
        category: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): Result<List<ForumThread>>

    suspend fun getForumThreadDetail(threadId: String): Result<Pair<ForumThread, List<ForumComment>>>

    suspend fun createForumThread(
        title: String,
        body: String,
        category: String
    ): Result<ForumThread>

    suspend fun createComment(
        threadId: String,
        body: String
    ): Result<ForumComment>

    suspend fun toggleUpvote(threadId: String): Result<Pair<Boolean, Int>>

    suspend fun deleteForumThread(threadId: String): Result<Unit>
}
