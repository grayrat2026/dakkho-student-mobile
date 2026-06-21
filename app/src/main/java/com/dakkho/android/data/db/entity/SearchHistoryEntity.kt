package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "search_history",
    indices = [
        Index(value = ["queried_at"], name = "index_search_history_queried_at")
    ]
)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "query") val query: String,
    @ColumnInfo(name = "queried_at") val queriedAt: Long = System.currentTimeMillis()
)
