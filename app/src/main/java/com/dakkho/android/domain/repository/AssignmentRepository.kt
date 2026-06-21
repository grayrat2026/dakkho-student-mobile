package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.AssignmentItem
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {

    fun getAssignmentsFlow(courseId: String): Flow<List<AssignmentItem>>

    suspend fun getAssignments(courseId: String): List<AssignmentItem>

    suspend fun getAssignmentById(courseId: String, assignmentId: String): AssignmentItem?

    suspend fun syncAssignments(courseId: String): Result<List<AssignmentItem>>

    suspend fun submitAssignment(
        courseId: String,
        assignmentId: String,
        filePath: String
    ): Result<AssignmentItem>
}
