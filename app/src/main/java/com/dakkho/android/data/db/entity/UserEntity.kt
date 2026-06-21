package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "institute_id") val instituteId: String?,
    @ColumnInfo(name = "technology") val technology: String?,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "is_verified") val isVerified: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: String?,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)
