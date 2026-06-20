package com.dakkho.android.di

import com.dakkho.android.data.repository.AuthRepositoryImpl
import com.dakkho.android.data.repository.CourseRepositoryImpl
import com.dakkho.android.data.repository.EnrollmentRepositoryImpl
import com.dakkho.android.domain.repository.AuthRepository
import com.dakkho.android.domain.repository.CourseRepository
import com.dakkho.android.domain.repository.EnrollmentRepository
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
}
