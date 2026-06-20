package com.dakkho.android.data.api

import com.dakkho.android.data.db.EncryptedPrefsHelper
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.encodedPath

        if (isUnauthenticatedEndpoint(url)) {
            return chain.proceed(originalRequest)
        }

        val token = encryptedPrefsHelper.getToken()
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }

    private fun isUnauthenticatedEndpoint(url: String): Boolean {
        return UNAUTHENTICATED_ENDPOINTS.any { endpoint -> url.contains(endpoint) }
    }

    companion object {
        private val UNAUTHENTICATED_ENDPOINTS = listOf(
            "api/auth/login",
            "api/auth/signup",
            "api/auth/forgot-password",
            "api/auth/reset-password",
            "api/auth/verify-otp"
        )
    }
}
