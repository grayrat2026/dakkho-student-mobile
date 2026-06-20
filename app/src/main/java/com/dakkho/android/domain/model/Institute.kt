package com.dakkho.android.domain.model

data class Institute(
    val id: String,
    val name: String,
    val logoUrl: String? = null,
    val description: String? = null
)
