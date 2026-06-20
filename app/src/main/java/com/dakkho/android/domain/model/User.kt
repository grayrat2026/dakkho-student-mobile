package com.dakkho.android.domain.model

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val instituteId: String? = null,
    val technology: String? = null,
    val avatarUrl: String? = null,
    val role: String,
    val phone: String? = null,
    val isVerified: Boolean = false,
    val createdAt: String? = null
)
