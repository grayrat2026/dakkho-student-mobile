package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "body") val body: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "is_read") val isRead: Boolean = false,
    @Json(name = "action_url") val actionUrl: String? = null,
    @Json(name = "icon_url") val iconUrl: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)
