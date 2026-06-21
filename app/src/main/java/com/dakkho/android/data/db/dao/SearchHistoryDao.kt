package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY queried_at DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 10): List<SearchHistoryEntity>

    @Query("SELECT DISTINCT query FROM search_history ORDER BY queried_at DESC LIMIT :limit")
    suspend fun getRecentQueries(limit: Int = 10): List<String>

    @Query("SELECT * FROM search_history WHERE query LIKE '%' || :query || '%' ORDER BY queried_at DESC LIMIT :limit")
    suspend fun search(query: String, limit: Int = 10): List<SearchHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistoryEntity)

    @Delete
    suspend fun delete(searchHistory: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM search_history")
    suspend fun deleteAll()
}
