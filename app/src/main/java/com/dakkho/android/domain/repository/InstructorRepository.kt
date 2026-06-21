package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.model.InstructorDetail
import com.dakkho.android.domain.model.LiveClass
import com.dakkho.android.domain.model.RatingBreakdown
import com.dakkho.android.domain.model.Review

interface InstructorRepository {

    /**
     * Get a paginated list of active instructors.
     */
    suspend fun getInstructors(
        limit: Int = 20,
        offset: Int = 0,
        search: String = ""
    ): Result<Pair<List<Instructor>, Int>>

    /**
     * Get full instructor profile detail.
     */
    suspend fun getInstructorDetail(instructorId: String): Result<InstructorDetail>

    /**
     * Get courses by instructor (paginated).
     */
    suspend fun getInstructorCourses(
        instructorId: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<Pair<List<Course>, Int>>

    /**
     * Get reviews for an instructor (paginated).
     */
    suspend fun getInstructorReviews(
        instructorId: String,
        limit: Int = 20,
        offset: Int = 0,
        rating: Int? = null
    ): Result<InstructorReviewsResult>

    /**
     * Get live classes for an instructor.
     */
    suspend fun getInstructorLiveClasses(
        instructorId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<Pair<List<LiveClass>, Int>>
}

/**
 * Result wrapper for instructor reviews with breakdown data.
 */
data class InstructorReviewsResult(
    val reviews: List<Review>,
    val total: Int,
    val averageRating: Float,
    val ratingBreakdown: RatingBreakdown
)
