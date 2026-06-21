package com.dakkho.android.data.api

import com.dakkho.android.domain.model.ApiResponse
import com.dakkho.android.domain.model.ApiResult
import com.dakkho.android.domain.model.AuthResponse
import com.dakkho.android.domain.model.ForgotPasswordRequest
import com.dakkho.android.domain.model.LoginRequest
import com.dakkho.android.domain.model.ResetPasswordRequest
import com.dakkho.android.domain.model.SignupRequest
import com.dakkho.android.domain.model.UpdateProfileRequest
import com.dakkho.android.domain.model.UserDto
import com.dakkho.android.domain.model.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {

    @POST("api/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<ApiResult<AuthResponse>>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResult<AuthResponse>>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<ApiResult<AuthResponse>>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResult<Unit>>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResult<Unit>>

    @GET("api/auth/me")
    suspend fun getProfile(): Response<ApiResult<UserDto>>

    @PUT("api/auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResult<UserDto>>

    @DELETE("api/auth/me")
    suspend fun deleteAccount(): Response<ApiResult<Unit>>
}
