package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.AuthResponse
import com.dakkho.android.domain.model.User

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<AuthResponse>

    suspend fun signup(
        email: String,
        password: String,
        fullName: String,
        instituteId: String?,
        technology: String?,
        phone: String?
    ): Result<AuthResponse>

    suspend fun verifyOtp(email: String, otp: String): Result<AuthResponse>

    suspend fun forgotPassword(email: String): Result<Unit>

    suspend fun resetPassword(token: String, password: String): Result<Unit>

    suspend fun getProfile(): Result<User>

    suspend fun updateProfile(
        fullName: String?,
        phone: String?,
        avatarUrl: String?,
        instituteId: String?,
        technology: String?
    ): Result<User>

    suspend fun deleteAccount(): Result<Unit>

    fun isLoggedIn(): Boolean

    suspend fun logout()
}
