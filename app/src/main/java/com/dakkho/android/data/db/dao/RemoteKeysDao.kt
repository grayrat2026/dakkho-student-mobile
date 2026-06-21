package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.RemoteKeysEntity

@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM remote_keys WHERE course_id = :courseId")
    suspend fun getRemoteKeyByCourseId(courseId: String): RemoteKeysEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKeysEntity>)

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}
