# Data
-keep class fi.kroon.vadret.data.aboutinfo.model.** { *; }
-keep class fi.kroon.vadret.data.alert.model.** { *; }
-keep class fi.kroon.vadret.data.weatherforecast.model.** { *; }
-keep class fi.kroon.vadret.data.radar.model.** { *; }
-keep class fi.kroon.vadret.data.autocomplete.model.** { *; }
-keep class fi.kroon.vadret.data.library.model.** { *; }
-keep class fi.kroon.vadret.data.location.model.** { *; }
-keep class fi.kroon.vadret.data.nominatim.model.** { *; }
-keep class fi.kroon.vadret.data.radar.model.** { *; }
-keep class fi.kroon.vadret.data.theme.model.** { *; }
-keep class fi.kroon.vadret.data.aggregatedfeed.model.** { *; }
-keep class fi.kroon.vadret.data.district.model.** { *; }
-keep class fi.kroon.vadret.data.districtpreference.model.** { *; }
-keep class fi.kroon.vadret.data.feedsource.model.** { *; }
-keep class fi.kroon.vadret.data.feedsourcepreference.model.** { *; }

# Okhttp
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Android
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
