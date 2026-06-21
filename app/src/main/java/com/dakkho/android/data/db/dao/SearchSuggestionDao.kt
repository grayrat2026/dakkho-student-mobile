package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.SearchSuggestionEntity

@Dao
interface SearchSuggestionDao {

    @Query("SELECT * FROM search_suggestions WHERE search_suggestions MATCH :query LIMIT :limit")
    suspend fun search(query: String, limit: Int = 10): List<SearchSuggestionEntity>

    @Query("SELECT * FROM search_suggestions WHERE type = :type AND search_suggestions MATCH :query LIMIT :limit")
    suspend fun searchByType(query: String, type: String, limit: Int = 10): List<SearchSuggestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suggestions: List<SearchSuggestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(suggestion: SearchSuggestionEntity)

    @Query("DELETE FROM search_suggestions")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM search_suggestions")
    suspend fun getCount(): Int
}
