package com.dakkho.android.domain.model

data class SearchResult(
    val id: String,
    val title: String,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val type: String,
    val rating: Float? = null,
    val instructorName: String? = null,
    val price: Double? = null
)
