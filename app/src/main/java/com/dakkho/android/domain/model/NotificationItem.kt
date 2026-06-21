package com.dakkho.android.domain.model

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String? = null,
    val type: String? = null,
    val isRead: Boolean = false,
    val actionUrl: String? = null,
    val createdAt: String? = null
)
