package com.dakkho.android.data.repository

import com.dakkho.android.data.db.dao.CourseNoteDao
import com.dakkho.android.data.db.entity.CourseNoteEntity
import com.dakkho.android.domain.model.CourseNote
import com.dakkho.android.domain.repository.CourseNoteRepository
import com.dakkho.android.data.db.EncryptedPrefsHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseNoteRepositoryImpl @Inject constructor(
    private val courseNoteDao: CourseNoteDao,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : CourseNoteRepository {

    private fun getUserId(): String {
        return encryptedPrefsHelper.getUserId() ?: ""
    }

    private fun CourseNoteEntity.toDomain() = CourseNote(
        id = id,
        videoId = videoId,
        courseId = courseId,
        userId = userId,
        positionMs = positionMs,
        content = content,
        videoTitle = videoTitle,
        timestampLabel = timestampLabel,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun CourseNote.toEntity() = CourseNoteEntity(
        id = if (id > 0) id else 0,
        videoId = videoId,
        courseId = courseId,
        userId = getUserId(),
        positionMs = positionMs,
        content = content,
        videoTitle = videoTitle,
        timestampLabel = timestampLabel,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )

    override suspend fun getNotesForCourse(courseId: String): List<CourseNote> {
        return try {
            courseNoteDao.getNotesForCourse(courseId, getUserId()).map { it.toDomain() }
        } catch (e: Exception) {
            Timber.e(e, "Get notes for course error")
            emptyList()
        }
    }

    override fun getNotesForCourseFlow(courseId: String): Flow<List<CourseNote>> {
        return courseNoteDao.getNotesForCourseFlow(courseId, getUserId()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getNotesForVideo(videoId: String): List<CourseNote> {
        return try {
            courseNoteDao.getNotesForVideo(videoId, getUserId()).map { it.toDomain() }
        } catch (e: Exception) {
            Timber.e(e, "Get notes for video error")
            emptyList()
        }
    }

    override fun getNotesForVideoFlow(videoId: String): Flow<List<CourseNote>> {
        return courseNoteDao.getNotesForVideoFlow(videoId, getUserId()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getNoteById(id: Long): CourseNote? {
        return try {
            courseNoteDao.getNoteById(id)?.toDomain()
        } catch (e: Exception) {
            Timber.e(e, "Get note by id error")
            null
        }
    }

    override suspend fun saveNote(note: CourseNote): Long {
        return try {
            courseNoteDao.insert(note.toEntity())
        } catch (e: Exception) {
            Timber.e(e, "Save note error")
            -1L
        }
    }

    override suspend fun updateNote(note: CourseNote) {
        try {
            courseNoteDao.update(note.toEntity())
        } catch (e: Exception) {
            Timber.e(e, "Update note error")
        }
    }

    override suspend fun deleteNote(id: Long) {
        try {
            courseNoteDao.deleteById(id)
        } catch (e: Exception) {
            Timber.e(e, "Delete note error")
        }
    }

    override suspend fun deleteAllForVideo(videoId: String) {
        try {
            courseNoteDao.deleteAllForVideo(videoId, getUserId())
        } catch (e: Exception) {
            Timber.e(e, "Delete all notes for video error")
        }
    }

    override suspend fun deleteAllForCourse(courseId: String) {
        try {
            courseNoteDao.deleteAllForCourse(courseId, getUserId())
        } catch (e: Exception) {
            Timber.e(e, "Delete all notes for course error")
        }
    }
}
