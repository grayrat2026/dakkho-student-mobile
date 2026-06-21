package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Announcement

interface AnnouncementRepository {

    suspend fun getAnnouncements(
        courseId: String,
        page: Int = 1,
        limit: Int = 20
    ): Result<List<Announcement>>

    suspend fun getAnnouncementDetail(
        announcementId: String
    ): Result<Announcement>
}
