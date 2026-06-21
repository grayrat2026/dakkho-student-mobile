package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.CourseDetail
import com.dakkho.android.domain.model.CoursePackage
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.Review

interface CourseRepository {

    suspend fun getCourses(params: Map<String, String> = emptyMap()): Result<List<Course>>

    suspend fun getCourseDetail(courseId: String): Result<CourseDetail>

    suspend fun getCourseCurriculum(courseId: String): Result<Curriculum>

    suspend fun getCourseReviews(
        courseId: String,
        page: Int = 1,
        limit: Int = 20,
        rating: Int? = null
    ): Result<List<Review>>

    suspend fun submitCourseReview(
        courseId: String,
        rating: Float,
        title: String?,
        comment: String?
    ): Result<Review>

    suspend fun getCoursePackages(courseId: String): Result<List<CoursePackage>>

    suspend fun searchCourses(query: String): Result<List<Course>>

    suspend fun getCoursesByTechnology(technology: String): Result<List<Course>>
}
