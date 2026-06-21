package com.dakkho.android.data.repository

import com.dakkho.android.data.api.ForumApiService
import com.dakkho.android.domain.model.CreateForumCommentRequest
import com.dakkho.android.domain.model.CreateForumThreadRequest
import com.dakkho.android.domain.model.ForumComment
import com.dakkho.android.domain.model.ForumCommentDto
import com.dakkho.android.domain.model.ForumThread
import com.dakkho.android.domain.model.ForumThreadDto
import com.dakkho.android.domain.repository.ForumRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForumRepositoryImpl @Inject constructor(
    private val forumApi: ForumApiService
) : ForumRepository {

    override suspend fun getForumThreads(
        category: String?,
        page: Int,
        limit: Int
    ): Result<List<ForumThread>> {
        return try {
            val response = forumApi.getForumThreads(category, page, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val threads = body.threads.map { mapThreadDtoToDomain(it) }
                    Result.success(threads)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Failed to load forum threads: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get forum threads error")
            Result.failure(e)
        }
    }

    override suspend fun getForumThreadDetail(threadId: String): Result<Pair<ForumThread, List<ForumComment>>> {
        return try {
            val response = forumApi.getForumThreadDetail(threadId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val thread = mapThreadDtoToDomain(body.thread)
                    val comments = body.comments.map { mapCommentDtoToDomain(it) }
                    Result.success(Pair(thread, comments))
                } else {
                    Result.failure(Exception("Thread not found"))
                }
            } else {
                Result.failure(Exception("Failed to load thread: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get forum thread detail error")
            Result.failure(e)
        }
    }

    override suspend fun createForumThread(
        title: String,
        body: String,
        category: String
    ): Result<ForumThread> {
        return try {
            val request = CreateForumThreadRequest(title, body, category)
            val response = forumApi.createForumThread(request)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.success && responseBody.data != null) {
                    val thread = mapThreadDtoToDomain(responseBody.data.thread)
                    Result.success(thread)
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Failed to create thread"))
                }
            } else {
                Result.failure(Exception("Create thread failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Create forum thread error")
            Result.failure(e)
        }
    }

    override suspend fun createComment(
        threadId: String,
        body: String
    ): Result<ForumComment> {
        return try {
            val request = CreateForumCommentRequest(body)
            val response = forumApi.createComment(threadId, request)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.success && responseBody.data != null) {
                    val dto = responseBody.data.comments.lastOrNull()
                    if (dto != null) {
                        Result.success(mapCommentDtoToDomain(dto))
                    } else {
                        Result.failure(Exception("Comment not found in response"))
                    }
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Failed to create comment"))
                }
            } else {
                Result.failure(Exception("Create comment failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Create forum comment error")
            Result.failure(e)
        }
    }

    override suspend fun toggleUpvote(threadId: String): Result<Pair<Boolean, Int>> {
        return try {
            val response = forumApi.toggleUpvote(threadId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val upvoted = body["upvoted"] as? Boolean ?: false
                    val count = (body["upvotes"] as? Number)?.toInt() ?: 0
                    Result.success(Pair(upvoted, count))
                } else {
                    Result.failure(Exception("Upvote toggle failed"))
                }
            } else {
                Result.failure(Exception("Upvote toggle failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Toggle upvote error")
            Result.failure(e)
        }
    }

    override suspend fun deleteForumThread(threadId: String): Result<Unit> {
        return try {
            val response = forumApi.deleteForumThread(threadId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete thread failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Delete forum thread error")
            Result.failure(e)
        }
    }

    private fun mapThreadDtoToDomain(dto: ForumThreadDto): ForumThread {
        return ForumThread(
            id = dto.id,
            authorId = dto.userId,
            authorName = dto.userName,
            authorAvatar = dto.userAvatar,
            title = dto.title,
            body = dto.body,
            category = dto.category,
            upvotes = dto.upvotes,
            commentCount = dto.commentCount,
            isUpvotedByUser = dto.isUpvoted,
            isPinned = dto.isPinned,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    private fun mapCommentDtoToDomain(dto: ForumCommentDto): ForumComment {
        return ForumComment(
            id = dto.id,
            threadId = dto.threadId,
            authorId = dto.userId,
            authorName = dto.userName,
            authorAvatar = dto.userAvatar,
            body = dto.body,
            upvotes = dto.upvotes,
            isUpvotedByUser = dto.isUpvoted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }
}
