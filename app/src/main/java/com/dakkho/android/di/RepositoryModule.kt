package com.dakkho.android.di

import com.dakkho.android.data.repository.AnnouncementRepositoryImpl
import com.dakkho.android.data.repository.AssignmentRepositoryImpl
import com.dakkho.android.data.repository.AuthRepositoryImpl
import com.dakkho.android.data.repository.CertificateRepositoryImpl
import com.dakkho.android.data.repository.CourseNoteRepositoryImpl
import com.dakkho.android.data.repository.DiscussionRepositoryImpl
import com.dakkho.android.data.repository.DownloadRepositoryImpl
import com.dakkho.android.data.repository.QuizRepositoryImpl
import com.dakkho.android.data.repository.WatchHistoryRepositoryImpl
import com.dakkho.android.data.repository.CourseRepositoryImpl
import com.dakkho.android.data.repository.EnrollmentRepositoryImpl
import com.dakkho.android.data.repository.InstructorRepositoryImpl
import com.dakkho.android.data.repository.NotificationRepositoryImpl
import com.dakkho.android.domain.repository.AnnouncementRepository
import com.dakkho.android.domain.repository.AssignmentRepository
import com.dakkho.android.domain.repository.AuthRepository
import com.dakkho.android.domain.repository.CertificateRepository
import com.dakkho.android.domain.repository.CourseNoteRepository
import com.dakkho.android.domain.repository.CourseRepository
import com.dakkho.android.domain.repository.DiscussionRepository
import com.dakkho.android.domain.repository.DownloadRepository
import com.dakkho.android.domain.repository.EnrollmentRepository
import com.dakkho.android.domain.repository.InstructorRepository
import com.dakkho.android.domain.repository.NotificationRepository
import com.dakkho.android.domain.repository.QuizRepository
import com.dakkho.android.domain.repository.WatchHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(courseRepositoryImpl: CourseRepositoryImpl): CourseRepository

    @Binds
    @Singleton
    abstract fun bindEnrollmentRepository(enrollmentRepositoryImpl: EnrollmentRepositoryImpl): EnrollmentRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindWatchHistoryRepository(watchHistoryRepositoryImpl: WatchHistoryRepositoryImpl): WatchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindAssignmentRepository(assignmentRepositoryImpl: AssignmentRepositoryImpl): AssignmentRepository

    @Binds
    @Singleton
    abstract fun bindDiscussionRepository(discussionRepositoryImpl: DiscussionRepositoryImpl): DiscussionRepository

    @Binds
    @Singleton
    abstract fun bindAnnouncementRepository(announcementRepositoryImpl: AnnouncementRepositoryImpl): AnnouncementRepository

    @Binds
    @Singleton
    abstract fun bindCourseNoteRepository(courseNoteRepositoryImpl: CourseNoteRepositoryImpl): CourseNoteRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(quizRepositoryImpl: QuizRepositoryImpl): QuizRepository

    @Binds
    @Singleton
    abstract fun bindInstructorRepository(instructorRepositoryImpl: InstructorRepositoryImpl): InstructorRepository

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(downloadRepositoryImpl: DownloadRepositoryImpl): DownloadRepository

    @Binds
    @Singleton
    abstract fun bindCertificateRepository(certificateRepositoryImpl: CertificateRepositoryImpl): CertificateRepository
}
