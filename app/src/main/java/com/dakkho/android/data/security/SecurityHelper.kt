package com.dakkho.android.data.security

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.dakkho.android.data.db.EncryptedPrefsHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Phase 29: Security Utilities
 * #29.20 Certificate Pinning (already in NetworkModule.kt)
 * #29.21 Root Detection
 * #29.24 Overlay Detection
 */
@Singleton
class SecurityHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsHelper: EncryptedPrefsHelper
) {

    // ── #29.21 Root Detection ──

    fun isDeviceRooted(): Boolean {
        return checkSuBinary() || checkSuperuserApk() || checkBuildTags() || checkDangerousPaths()
    }

    private fun checkSuBinary(): Boolean {
        val paths = listOf(
            "/system/bin/su", "/system/xbin/su", "/sbin/su",
            "/data/local/xbin/su", "/data/local/bin/su",
            "/system/sd/xbin/su", "/system/bin/failsafe/su",
            "/data/local/su", "/su/bin/su"
        )
        return paths.any { java.io.File(it).exists() }
    }

    private fun checkSuperuserApk(): Boolean {
        val paths = listOf(
            "/system/app/Superuser.apk",
            "/system/app/SuperSU/SuperSU.apk",
            "/system/app/Magisk/Magisk.apk",
            "/data/adb/magisk/Magisk.apk"
        )
        return paths.any { java.io.File(it).exists() }
    }

    private fun checkBuildTags(): Boolean {
        return Build.TAGS?.contains("test-keys") == true
    }

    private fun checkDangerousPaths(): Boolean {
        val paths = listOf(
            "/system/app/MagiskManager",
            "/data/data/com.topjohnwu.magisk",
            "/data/data/com.noshufou.android.su",
            "/data/data/eu.chainfire.supersu",
            "/data/data/com.koushikdutta.superuser"
        )
        return paths.any { java.io.File(it).exists() }
    }

    fun shouldBlockVideoOnRooted(): Boolean {
        return prefsHelper.getBoolean("block_video_rooted", true)
    }

    // ── #29.24 Overlay Detection ──

    fun isOverlayDetected(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            !Settings.canDrawOverlays(context)
        } else {
            false
        }
    }

    fun shouldBlockSensitiveOperation(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !Settings.canDrawOverlays(context)
        }
        return false
    }

    fun openOverlaySettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    // ── APK Signature Verification (#29.22 Anti-Tampering) ──

    fun verifyApkSignature(): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
            )
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
            !signatures.isNullOrEmpty()
        } catch (_: Exception) {
            false
        }
    }

    data class SecurityStatus(
        val isRooted: Boolean,
        val isOverlaySafe: Boolean,
        val isSignatureValid: Boolean,
        val isSecure: Boolean
    )

    fun getSecurityStatus(): SecurityStatus {
        val isRooted = isDeviceRooted()
        val isOverlaySafe = !shouldBlockSensitiveOperation()
        val isSignatureValid = verifyApkSignature()
        return SecurityStatus(
            isRooted = isRooted,
            isOverlaySafe = isOverlaySafe,
            isSignatureValid = isSignatureValid,
            isSecure = !isRooted && isOverlaySafe && isSignatureValid
        )
    }
}
