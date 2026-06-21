package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.CourseNote
import kotlinx.coroutines.flow.Flow

interface CourseNoteRepository {

    suspend fun getNotesForCourse(courseId: String): List<CourseNote>

    fun getNotesForCourseFlow(courseId: String): Flow<List<CourseNote>>

    suspend fun getNotesForVideo(videoId: String): List<CourseNote>

    fun getNotesForVideoFlow(videoId: String): Flow<List<CourseNote>>

    suspend fun getNoteById(id: Long): CourseNote?

    suspend fun saveNote(note: CourseNote): Long

    suspend fun updateNote(note: CourseNote)

    suspend fun deleteNote(id: Long)

    suspend fun deleteAllForVideo(videoId: String)

    suspend fun deleteAllForCourse(courseId: String)
}
