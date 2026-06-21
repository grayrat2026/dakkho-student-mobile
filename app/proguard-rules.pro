# ── DAKKHO Student App ProGuard/R8 Rules ──

# ── Compose ──
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# ── Hilt / Dagger ──
-dontwarn dagger.**
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}
-keepclassmembers class * {
    @dagger.hilt.InstallIn <methods>;
}

# ── Retrofit ──
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ── OkHttp ──
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ── Moshi ──
-dontwarn com.squareup.moshi.**
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers @com.squareup.moshi.JsonClass class * {
    <init>(...);
}
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# ── Room ──
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Entity class * {
    <fields>;
}
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers @androidx.room.Dao interface * {
    @androidx.room.Query <methods>;
    @androidx.room.Insert <methods>;
    @androidx.room.Update <methods>;
    @androidx.room.Delete <methods>;
}

# ── Kotlin Serialization ──
-dontwarn kotlinx.serialization.**
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class kotlinx.serialization.** { *; }
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# ── Kotlin Coroutines ──
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ── Model Classes ──
-keep class com.dakkho.android.domain.model.** { *; }
-keepclassmembers class com.dakkho.android.domain.model.** { *; }
-keep class com.dakkho.android.data.api.** { *; }
-keepclassmembers class com.dakkho.android.data.api.** { *; }
-keep class com.dakkho.android.data.db.entity.** { *; }
-keepclassmembers class com.dakkho.android.data.db.entity.** { *; }

# ── Timber ──
-dontwarn timber.log.**
-keep class timber.log.** { *; }

# ── Coil ──
-dontwarn coil.**
-keep class coil.** { *; }

# ── Firebase ──
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }

# ── General Android ──
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-renamesourcefileattribute SourceFile

# ── String Encryption for API Keys ──
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ── Remove verbose logging in release ──
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
