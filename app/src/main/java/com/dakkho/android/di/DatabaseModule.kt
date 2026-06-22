package com.dakkho.android.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.dakkho.android.data.db.dao.CourseNoteDao
import com.dakkho.android.data.db.dao.DepartmentDao
import com.dakkho.android.data.db.dao.VideoBookmarkDao
import com.dakkho.android.data.db.dao.WatchHistoryDao
import com.dakkho.android.data.db.dao.SemesterDao
import com.dakkho.android.data.db.dao.SubjectDao
import com.dakkho.android.data.db.dao.RoutineEntryDao
import com.dakkho.android.data.db.EncryptedPrefsHelper
import androidx.work.WorkManager
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
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `departments` (
                        `id` TEXT NOT NULL,
                        `slug` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `shortCode` TEXT NOT NULL,
                        `description` TEXT,
                        `iconUrl` TEXT,
                        `bannerUrl` TEXT,
                        `courseCount` INTEGER NOT NULL,
                        `instructorCount` INTEGER NOT NULL,
                        `studentCount` INTEGER NOT NULL,
                        `semesterCount` INTEGER NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_departments_slug` ON `departments` (`slug`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_departments_shortCode` ON `departments` (`shortCode`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_departments_courseCount` ON `departments` (`courseCount`)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Phase 24: Semesters table (7 regular + 8th = ইন্টার্নি)
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `semesters` (
                        `id` TEXT NOT NULL,
                        `departmentSlug` TEXT NOT NULL,
                        `number` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `subjectCount` INTEGER NOT NULL,
                        `totalCredits` INTEGER NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`departmentSlug`) REFERENCES `departments`(`slug`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_semesters_departmentSlug` ON `semesters` (`departmentSlug`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_semesters_departmentSlug_number` ON `semesters` (`departmentSlug`, `number`)")

                // Phase 24: Subjects table (subjects within each semester)
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `subjects` (
                        `id` TEXT NOT NULL,
                        `semesterId` TEXT NOT NULL,
                        `departmentSlug` TEXT NOT NULL,
                        `semesterNumber` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `code` TEXT NOT NULL,
                        `creditHours` INTEGER NOT NULL,
                        `instructorName` TEXT,
                        `instructorId` TEXT,
                        `courseId` TEXT,
                        `description` TEXT,
                        `syllabusTopics` TEXT NOT NULL,
                        `sortOrder` INTEGER NOT NULL,
                        `color` TEXT,
                        `isActive` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`semesterId`) REFERENCES `semesters`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subjects_semesterId` ON `subjects` (`semesterId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subjects_departmentSlug_semesterNumber` ON `subjects` (`departmentSlug`, `semesterNumber`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subjects_courseId` ON `subjects` (`courseId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subjects_sortOrder` ON `subjects` (`sortOrder`)")

                // Phase 24: Routine entries table (weekly timetable)
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `routine_entries` (
                        `id` TEXT NOT NULL,
                        `subjectId` TEXT NOT NULL,
                        `subjectName` TEXT NOT NULL,
                        `subjectCode` TEXT NOT NULL,
                        `departmentSlug` TEXT NOT NULL,
                        `semesterNumber` INTEGER NOT NULL,
                        `dayOfWeek` INTEGER NOT NULL,
                        `startTime` TEXT NOT NULL,
                        `endTime` TEXT NOT NULL,
                        `roomNumber` TEXT,
                        `instructorName` TEXT,
                        `color` TEXT,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`subjectId`) REFERENCES `subjects`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_routine_entries_subjectId` ON `routine_entries` (`subjectId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_routine_entries_dept_sem_day` ON `routine_entries` (`departmentSlug`, `semesterNumber`, `dayOfWeek`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_routine_entries_dayOfWeek_startTime` ON `routine_entries` (`dayOfWeek`, `startTime`)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Phase 25: Add extra columns to bookmarks table for richer display
                db.execSQL("ALTER TABLE `bookmarks` ADD COLUMN `course_title` TEXT")
                db.execSQL("ALTER TABLE `bookmarks` ADD COLUMN `instructor_name` TEXT")
                db.execSQL("ALTER TABLE `bookmarks` ADD COLUMN `thumbnail_url` TEXT")
                db.execSQL("ALTER TABLE `bookmarks` ADD COLUMN `price` REAL")
                db.execSQL("ALTER TABLE `bookmarks` ADD COLUMN `rating` REAL")
                db.execSQL("ALTER TABLE `bookmarks` ADD COLUMN `technology` TEXT")
            }
        }

        return Room.databaseBuilder(
            context,
            DakkhoDatabase::class.java,
            "dakkho_database"
        )
            .addTypeConverter(Converters())
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEncryptedPrefsHelper(@ApplicationContext context: Context): EncryptedPrefsHelper {
        return EncryptedPrefsHelper(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
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

    @Provides
    fun provideVideoBookmarkDao(database: DakkhoDatabase): VideoBookmarkDao {
        return database.videoBookmarkDao()
    }

    @Provides
    fun provideCourseNoteDao(database: DakkhoDatabase): CourseNoteDao {
        return database.courseNoteDao()
    }

    @Provides
    fun provideDepartmentDao(database: DakkhoDatabase): DepartmentDao {
        return database.departmentDao()
    }

    @Provides
    fun provideSemesterDao(database: DakkhoDatabase): SemesterDao {
        return database.semesterDao()
    }

    @Provides
    fun provideSubjectDao(database: DakkhoDatabase): SubjectDao {
        return database.subjectDao()
    }

    @Provides
    fun provideRoutineEntryDao(database: DakkhoDatabase): RoutineEntryDao {
        return database.routineEntryDao()
    }
}
