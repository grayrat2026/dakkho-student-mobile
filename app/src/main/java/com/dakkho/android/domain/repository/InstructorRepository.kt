package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.model.InstructorDetail

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
}
