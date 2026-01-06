# ===================== HILT / DAGGER FOR FRAMEWORK =====================

# Keep all annotations (required for Hilt/Dagger to work)
-keepattributes *Annotation*

# Preserve internal Hilt/Dagger classes
-keep class dagger.hilt.** { *; }
-keep interface dagger.hilt.** { *; }
-keep class dagger.hilt.internal.** { *; }
-dontwarn dagger.hilt.internal.**

# Preserve module and install-related annotations
-keep @dagger.Module class * { *; }
-keep @dagger.Provides class * { *; }
-keep @dagger.Binds class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }

# Keep generated dependency aggregation classes
-keep class hilt_aggregated_deps.** { *; }
-dontwarn hilt_aggregated_deps.**

# ===================== RETROFIT =====================

# Keep Retrofit interfaces and annotations
-keep class retrofit2.** { *; }
-keep interface retrofit2.http.* { *; }
-keepclassmembers interface * {
    @retrofit2.http.* <methods>;
}

# ===================== kotlinx.serialization =====================

# Preserve kotlinx.serialization runtime
-keep class kotlinx.serialization.** { *; }

# Keep all @Serializable model classes from this module
-keep @kotlinx.serialization.Serializable class com.davidluna.tmdb.media_data.data.remote.model.** { *; }

# Keep fields annotated with @SerialName and companion objects
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class ** {
    *** Companion;
}
# Keep Companion objects if they exist (valid usage)
-keepclassmembers class ** {
    public static final ** Companion;
}

# ===================== LOCAL CLASSES IN auth_framework MODULE =====================

-keep class com.davidluna.tmdb.media_data.** { *; }
-dontwarn com.davidluna.tmdb.media_data.**