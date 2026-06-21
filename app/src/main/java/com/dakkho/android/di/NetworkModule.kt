package com.dakkho.android.di

import com.dakkho.android.BuildConfig
import com.dakkho.android.data.api.AuthApiService
import com.dakkho.android.data.api.AuthInterceptor
import com.dakkho.android.data.api.CourseApiService
import com.dakkho.android.data.api.EnrollmentApiService
import com.dakkho.android.data.api.InstructorApiService
import com.dakkho.android.data.api.NotificationApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.ENABLE_LOGGING) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .certificatePinner(
                okhttp3.CertificatePinner.Builder()
                    .add(
                        "dakkho-api.pages.dev",
                        "sha256/PLACEHOLDER_PIN_1_REPLACE_WITH_REAL_SHA256="
                    )
                    .add(
                        "dakkho-api.pages.dev",
                        "sha256/PLACEHOLDER_PIN_2_REPLACE_WITH_REAL_SHA256="
                    )
                    .add(
                        "dakkho-api.pages.dev",
                        "sha256/PLACEHOLDER_BACKUP_PIN_REPLACE_WITH_REAL_SHA256="
                    )
                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCourseApiService(retrofit: Retrofit): CourseApiService {
        return retrofit.create(CourseApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEnrollmentApiService(retrofit: Retrofit): EnrollmentApiService {
        return retrofit.create(EnrollmentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideInstructorApiService(retrofit: Retrofit): InstructorApiService {
        return retrofit.create(InstructorApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }
}
