package com.dakkho.android.domain.model

data class Instructor(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val title: String? = null,
    val courseCount: Int = 0,
    val studentCount: Int = 0,
    val rating: Float = 0f
)
