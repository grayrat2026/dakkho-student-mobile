package com.dakkho.android.di

import android.content.Context
import androidx.room.Room
import com.dakkho.android.data.db.Converters
import com.dakkho.android.data.db.DakkhoDatabase
import com.dakkho.android.data.db.dao.AppSettingsDao
import com.dakkho.android.data.db.dao.BookmarkDao
import com.dakkho.android.data.db.dao.CourseDao
import com.dakkho.android.data.db.dao.CourseDetailDao
import com.dakkho.android.data.db.dao.DownloadDao
import com.dakkho.android.data.db.dao.EnrollmentDao
import com.dakkho.android.data.db.dao.NotificationDao
import com.dakkho.android.data.db.dao.RemoteKeysDao
import com.dakkho.android.data.db.dao.SearchHistoryDao
import com.dakkho.android.data.db.dao.SearchSuggestionDao
import com.dakkho.android.data.db.dao.UserDao
import com.dakkho.android.data.db.dao.WatchHistoryDao
import com.dakkho.android.data.db.EncryptedPrefsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDakkhoDatabase(@ApplicationContext context: Context): DakkhoDatabase {
        return Room.databaseBuilder(
            context,
            DakkhoDatabase::class.java,
            "dakkho_database"
        )
            .addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEncryptedPrefsHelper(@ApplicationContext context: Context): EncryptedPrefsHelper {
        return EncryptedPrefsHelper(context)
    }

    @Provides
    fun provideUserDao(database: DakkhoDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideCourseDao(database: DakkhoDatabase): CourseDao {
        return database.courseDao()
    }

    @Provides
    fun provideCourseDetailDao(database: DakkhoDatabase): CourseDetailDao {
        return database.courseDetailDao()
    }

    @Provides
    fun provideEnrollmentDao(database: DakkhoDatabase): EnrollmentDao {
        return database.enrollmentDao()
    }

    @Provides
    fun provideWatchHistoryDao(database: DakkhoDatabase): WatchHistoryDao {
        return database.watchHistoryDao()
    }

    @Provides
    fun provideNotificationDao(database: DakkhoDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    fun provideDownloadDao(database: DakkhoDatabase): DownloadDao {
        return database.downloadDao()
    }

    @Provides
    fun provideSearchHistoryDao(database: DakkhoDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    fun provideBookmarkDao(database: DakkhoDatabase): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    fun provideAppSettingsDao(database: DakkhoDatabase): AppSettingsDao {
        return database.appSettingsDao()
    }

    @Provides
    fun provideRemoteKeysDao(database: DakkhoDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }

    @Provides
    fun provideSearchSuggestionDao(database: DakkhoDatabase): SearchSuggestionDao {
        return database.searchSuggestionDao()
    }
}
