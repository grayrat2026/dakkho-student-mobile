package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.DownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Query("SELECT * FROM downloads ORDER BY created_at DESC")
    suspend fun getAll(): List<DownloadEntity>

    @Query("SELECT * FROM downloads ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status = :status")
    suspend fun getByStatus(status: String): List<DownloadEntity>

    @Query("SELECT * FROM downloads WHERE course_id = :courseId")
    suspend fun getByCourseId(courseId: String): List<DownloadEntity>

    @Query("SELECT * FROM downloads WHERE video_id = :videoId")
    suspend fun getByVideoId(videoId: String): DownloadEntity?

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getById(id: String): DownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(download: DownloadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(downloads: List<DownloadEntity>)

    @Delete
    suspend fun delete(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM downloads WHERE course_id = :courseId")
    suspend fun deleteByCourseId(courseId: String)

    @Query("DELETE FROM downloads")
    suspend fun deleteAll()

    @Query("UPDATE downloads SET downloaded_bytes = :downloadedBytes, status = :status WHERE id = :id")
    suspend fun updateProgress(id: String, downloadedBytes: Long, status: String)

    @Query("UPDATE downloads SET status = :status, file_path = :filePath, completed_at = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, filePath: String?, completedAt: Long?)
}
