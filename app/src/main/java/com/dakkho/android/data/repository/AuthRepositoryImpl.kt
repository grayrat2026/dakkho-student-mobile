package com.dakkho.android.data.repository

import com.dakkho.android.data.api.AuthApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.data.db.dao.UserDao
import com.dakkho.android.data.db.entity.UserEntity
import com.dakkho.android.domain.model.AuthResponse
import com.dakkho.android.domain.model.ForgotPasswordRequest
import com.dakkho.android.domain.model.LoginRequest
import com.dakkho.android.domain.model.ResetPasswordRequest
import com.dakkho.android.domain.model.SignupRequest
import com.dakkho.android.domain.model.UpdateProfileRequest
import com.dakkho.android.domain.model.User
import com.dakkho.android.domain.model.UserDto
import com.dakkho.android.domain.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userDao: UserDao,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApiService.login(LoginRequest(email = email, password = password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val authResponse = body.data
                    saveAuthData(authResponse)
                    saveUserToDb(authResponse.user)
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception(body?.message ?: "Login failed"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Login error")
            Result.failure(e)
        }
    }

    override suspend fun signup(
        email: String,
        password: String,
        fullName: String,
        instituteId: String?,
        technology: String?,
        phone: String?
    ): Result<AuthResponse> {
        return try {
            val request = SignupRequest(
                email = email,
                password = password,
                fullName = fullName,
                instituteId = instituteId,
                technology = technology,
                phone = phone
            )
            val response = authApiService.signup(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val authResponse = body.data
                    saveAuthData(authResponse)
                    saveUserToDb(authResponse.user)
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception(body?.message ?: "Signup failed"))
                }
            } else {
                Result.failure(Exception("Signup failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Signup error")
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): Result<AuthResponse> {
        return try {
            val response = authApiService.verifyOtp(
                com.dakkho.android.domain.model.VerifyOtpRequest(email = email, otp = otp)
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val authResponse = body.data
                    saveAuthData(authResponse)
                    saveUserToDb(authResponse.user)
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception(body?.message ?: "OTP verification failed"))
                }
            } else {
                Result.failure(Exception("OTP verification failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Verify OTP error")
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = authApiService.forgotPassword(ForgotPasswordRequest(email = email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Forgot password failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Forgot password error")
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(token: String, password: String): Result<Unit> {
        return try {
            val response = authApiService.resetPassword(
                ResetPasswordRequest(token = token, password = password)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Reset password failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Reset password error")
            Result.failure(e)
        }
    }

    override suspend fun getProfile(): Result<User> {
        return try {
            val response = authApiService.getProfile()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val userDto = body.data
                    saveUserToDb(userDto)
                    Result.success(mapDtoToDomain(userDto))
                } else {
                    // Fallback to Room
                    getCachedProfile()
                }
            } else {
                // Fallback to Room
                getCachedProfile()
            }
        } catch (e: Exception) {
            Timber.e(e, "Get profile error, falling back to cache")
            return getCachedProfile()
        }
    }

    override suspend fun updateProfile(
        fullName: String?,
        phone: String?,
        avatarUrl: String?,
        instituteId: String?,
        technology: String?
    ): Result<User> {
        return try {
            val request = UpdateProfileRequest(
                fullName = fullName,
                phone = phone,
                avatarUrl = avatarUrl,
                instituteId = instituteId,
                technology = technology
            )
            val response = authApiService.updateProfile(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val userDto = body.data
                    saveUserToDb(userDto)
                    Result.success(mapDtoToDomain(userDto))
                } else {
                    Result.failure(Exception(body?.message ?: "Update profile failed"))
                }
            } else {
                Result.failure(Exception("Update profile failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Update profile error")
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val response = authApiService.deleteAccount()
            if (response.isSuccessful) {
                clearLocalData()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete account failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Delete account error")
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Boolean {
        return encryptedPrefsHelper.isLoggedIn()
    }

    override suspend fun logout() {
        clearLocalData()
    }

    private fun saveAuthData(authResponse: AuthResponse) {
        encryptedPrefsHelper.saveToken(authResponse.token)
        authResponse.refreshToken?.let { encryptedPrefsHelper.saveRefreshToken(it) }
        encryptedPrefsHelper.saveUserId(authResponse.user.id)
        encryptedPrefsHelper.saveEmail(authResponse.user.email)
    }

    private suspend fun saveUserToDb(userDto: UserDto) {
        try {
            userDao.insert(
                UserEntity(
                    id = userDto.id,
                    email = userDto.email,
                    fullName = userDto.fullName,
                    instituteId = userDto.instituteId,
                    technology = userDto.technology,
                    avatarUrl = userDto.avatarUrl,
                    role = userDto.role,
                    phone = userDto.phone,
                    isVerified = userDto.isVerified ?: false,
                    createdAt = userDto.createdAt
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to save user to DB")
        }
    }

    private suspend fun getCachedProfile(): Result<User> {
        val userId = encryptedPrefsHelper.getUserId()
        if (userId != null) {
            val cachedUser = userDao.getUser(userId)
            if (cachedUser != null) {
                return Result.success(mapEntityToDomain(cachedUser))
            }
        }
        return Result.failure(Exception("No cached profile available"))
    }

    private suspend fun clearLocalData() {
        try {
            userDao.deleteAll()
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear user data")
        }
        encryptedPrefsHelper.clearAll()
    }

    private fun mapDtoToDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            email = dto.email,
            fullName = dto.fullName,
            instituteId = dto.instituteId,
            technology = dto.technology,
            avatarUrl = dto.avatarUrl,
            role = dto.role,
            phone = dto.phone,
            isVerified = dto.isVerified ?: false,
            createdAt = dto.createdAt
        )
    }

    private fun mapEntityToDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            email = entity.email,
            fullName = entity.fullName,
            instituteId = entity.instituteId,
            technology = entity.technology,
            avatarUrl = entity.avatarUrl,
            role = entity.role,
            phone = entity.phone,
            isVerified = entity.isVerified,
            createdAt = entity.createdAt
        )
    }
}
