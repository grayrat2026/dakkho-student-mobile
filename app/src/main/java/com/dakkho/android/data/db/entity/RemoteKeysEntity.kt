package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey
    @ColumnInfo(name = "course_id") val courseId: String,
    @ColumnInfo(name = "prev_key") val prevKey: Int?,
    @ColumnInfo(name = "next_key") val nextKey: Int?,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
