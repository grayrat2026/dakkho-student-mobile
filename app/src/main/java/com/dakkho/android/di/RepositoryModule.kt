package com.dakkho.android.di

import com.dakkho.android.data.repository.AssignmentRepositoryImpl
import com.dakkho.android.data.repository.AuthRepositoryImpl
import com.dakkho.android.data.repository.WatchHistoryRepositoryImpl
import com.dakkho.android.data.repository.CourseRepositoryImpl
import com.dakkho.android.data.repository.EnrollmentRepositoryImpl
import com.dakkho.android.data.repository.NotificationRepositoryImpl
import com.dakkho.android.domain.repository.AssignmentRepository
import com.dakkho.android.domain.repository.AuthRepository
import com.dakkho.android.domain.repository.CourseRepository
import com.dakkho.android.domain.repository.EnrollmentRepository
import com.dakkho.android.domain.repository.NotificationRepository
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
}
