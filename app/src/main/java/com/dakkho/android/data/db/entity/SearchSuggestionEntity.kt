package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

/**
 * FTS4 virtual table for fast full-text search suggestions.
 * Stores both course titles and instructor names for
 * real-time suggestions as the user types.
 */
@Fts4
@Entity(tableName = "search_suggestions")
data class SearchSuggestionEntity(
    @ColumnInfo(name = "rowid") val rowId: Long = 0,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "type") val type: String, // "course" or "instructor"
    @ColumnInfo(name = "reference_id") val referenceId: String,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String? = null
)
