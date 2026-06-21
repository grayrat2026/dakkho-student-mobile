package com.dakkho.android.domain.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val earnedAt: String? = null
)
