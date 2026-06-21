package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.CourseDetail
import com.dakkho.android.domain.model.Curriculum

interface CourseRepository {

    suspend fun getCourses(params: Map<String, String> = emptyMap()): Result<List<Course>>

    suspend fun getCourseDetail(courseId: String): Result<CourseDetail>

    suspend fun getCourseCurriculum(courseId: String): Result<Curriculum>

    suspend fun searchCourses(query: String): Result<List<Course>>

    suspend fun getCoursesByTechnology(technology: String): Result<List<Course>>
}
