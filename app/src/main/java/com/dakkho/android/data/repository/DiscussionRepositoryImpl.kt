package com.dakkho.android.data.repository

import com.dakkho.android.data.api.DiscussionApiService
import com.dakkho.android.domain.model.CreateDiscussionRequest
import com.dakkho.android.domain.model.CreateReplyRequest
import com.dakkho.android.domain.model.Discussion
import com.dakkho.android.domain.model.DiscussionReply
import com.dakkho.android.domain.repository.DiscussionRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscussionRepositoryImpl @Inject constructor(
    private val discussionApiService: DiscussionApiService
) : DiscussionRepository {

    override suspend fun getDiscussions(
        courseId: String,
        page: Int,
        limit: Int
    ): Result<List<Discussion>> {
        return try {
            val response = discussionApiService.getDiscussions(courseId, page, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val discussions = body.discussions.map { dto ->
                        Discussion(
                            id = dto.id,
                            courseId = dto.courseId,
                            userId = dto.userId,
                            userName = dto.userName,
                            title = dto.title,
                            body = dto.body,
                            tags = dto.tags,
                            likes = dto.likes,
                            replyCount = dto.replies,
                            isPinned = dto.isPinned,
                            isClosed = dto.isClosed,
                            createdAt = dto.createdAt,
                            updatedAt = dto.updatedAt
                        )
                    }
                    Result.success(discussions)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Timber.e(e, "Get discussions error")
            Result.failure(e)
        }
    }

    override suspend fun getDiscussionDetail(
        threadId: String
    ): Result<Pair<Discussion, List<DiscussionReply>>> {
        return try {
            val response = discussionApiService.getDiscussionDetail(threadId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val thread = Discussion(
                        id = body.thread.id,
                        courseId = body.thread.courseId,
                        userId = body.thread.userId,
                        userName = body.thread.userName,
                        title = body.thread.title,
                        body = body.thread.body,
                        tags = body.thread.tags,
                        likes = body.thread.likes,
                        replyCount = body.thread.replies,
                        isPinned = body.thread.isPinned,
                        isClosed = body.thread.isClosed,
                        createdAt = body.thread.createdAt,
                        updatedAt = body.thread.updatedAt
                    )
                    val replies = body.replies.map { dto ->
                        DiscussionReply(
                            id = dto.id,
                            threadId = dto.threadId,
                            userId = dto.userId,
                            userName = dto.userName,
                            body = dto.body,
                            likes = dto.likes,
                            createdAt = dto.createdAt,
                            updatedAt = dto.updatedAt
                        )
                    }
                    Result.success(Pair(thread, replies))
                } else {
                    Result.failure(Exception("Discussion not found"))
                }
            } else {
                Result.failure(Exception("Failed to load discussion: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get discussion detail error")
            Result.failure(e)
        }
    }

    override suspend fun createDiscussion(
        courseId: String,
        title: String,
        body: String,
        tags: List<String>
    ): Result<Discussion> {
        return try {
            val request = CreateDiscussionRequest(
                courseId = courseId,
                title = title,
                body = body,
                tags = tags
            )
            val response = discussionApiService.createDiscussion(request)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.success && responseBody.data != null) {
                    val dto = responseBody.data.thread
                    Result.success(
                        Discussion(
                            id = dto.id,
                            courseId = dto.courseId,
                            userId = dto.userId,
                            userName = dto.userName,
                            title = dto.title,
                            body = dto.body,
                            tags = dto.tags,
                            likes = dto.likes,
                            replyCount = dto.replies,
                            isPinned = dto.isPinned,
                            isClosed = dto.isClosed,
                            createdAt = dto.createdAt,
                            updatedAt = dto.updatedAt
                        )
                    )
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Failed to create discussion"))
                }
            } else {
                Result.failure(Exception("Create discussion failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Create discussion error")
            Result.failure(e)
        }
    }

    override suspend fun createReply(
        threadId: String,
        body: String
    ): Result<DiscussionReply> {
        return try {
            val request = CreateReplyRequest(body = body)
            val response = discussionApiService.createReply(threadId, request)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.success && responseBody.data != null) {
                    // Get the last reply from the replies list
                    val replies = responseBody.data.replies
                    val dto = replies.lastOrNull()
                    if (dto != null) {
                        Result.success(
                            DiscussionReply(
                                id = dto.id,
                                threadId = dto.threadId,
                                userId = dto.userId,
                                userName = dto.userName,
                                body = dto.body,
                                likes = dto.likes,
                                createdAt = dto.createdAt,
                                updatedAt = dto.updatedAt
                            )
                        )
                    } else {
                        Result.failure(Exception("Reply not found in response"))
                    }
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Failed to create reply"))
                }
            } else {
                Result.failure(Exception("Create reply failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Create reply error")
            Result.failure(e)
        }
    }

    override suspend fun toggleLike(threadId: String): Result<Pair<Boolean, Int>> {
        return try {
            val response = discussionApiService.toggleLike(threadId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(Pair(body.liked, body.likes))
                } else {
                    Result.failure(Exception("Like toggle failed"))
                }
            } else {
                Result.failure(Exception("Like toggle failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Toggle like error")
            Result.failure(e)
        }
    }

    override suspend fun deleteDiscussion(threadId: String): Result<Unit> {
        return try {
            val response = discussionApiService.deleteDiscussion(threadId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete discussion failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Delete discussion error")
            Result.failure(e)
        }
    }
}
