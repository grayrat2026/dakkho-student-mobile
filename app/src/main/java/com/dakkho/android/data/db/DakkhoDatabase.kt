package com.dakkho.android.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dakkho.android.data.db.dao.AppSettingsDao
import com.dakkho.android.data.db.dao.BookmarkDao
import com.dakkho.android.data.db.dao.CourseDao
import com.dakkho.android.data.db.dao.DownloadDao
import com.dakkho.android.data.db.dao.EnrollmentDao
import com.dakkho.android.data.db.dao.NotificationDao
import com.dakkho.android.data.db.dao.SearchHistoryDao
import com.dakkho.android.data.db.dao.UserDao
import com.dakkho.android.data.db.dao.WatchHistoryDao
import com.dakkho.android.data.db.entity.AppSettingsEntity
import com.dakkho.android.data.db.entity.BookmarkEntity
import com.dakkho.android.data.db.entity.CourseEntity
import com.dakkho.android.data.db.entity.DownloadEntity
import com.dakkho.android.data.db.entity.EnrollmentEntity
import com.dakkho.android.data.db.entity.NotificationEntity
import com.dakkho.android.data.db.entity.SearchHistoryEntity
import com.dakkho.android.data.db.entity.UserEntity
import com.dakkho.android.data.db.entity.WatchHistoryEntity

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        EnrollmentEntity::class,
        WatchHistoryEntity::class,
        NotificationEntity::class,
        DownloadEntity::class,
        SearchHistoryEntity::class,
        BookmarkEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class DakkhoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun notificationDao(): NotificationDao
    abstract fun downloadDao(): DownloadDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun appSettingsDao(): AppSettingsDao
}
