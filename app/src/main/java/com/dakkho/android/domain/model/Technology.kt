package com.dakkho.android.domain.model

data class Technology(
    val id: String,
    val name: String,
    val iconUrl: String? = null,
    val courseCount: Int = 0
)
