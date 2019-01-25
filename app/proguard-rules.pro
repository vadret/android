# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class localityName to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class fi.kroon.vadret.data.aboutinfo.model.** { *; }
-keep class fi.kroon.vadret.data.weather.model.** { *; }
-keep class fi.kroon.vadret.data.radar.model.** { *; }
-keep class fi.kroon.vadret.data.alert.model.** { *; }
-keep class fi.kroon.vadret.data.location.model.** { *; }
-keep class fi.kroon.vadret.data.nominatim.model.** { *; }
-keep class fi.kroon.vadret.data.autocomplete.model.** { *; }
-keep class fi.kroon.vadret.data.library.model.** { *; }

# MATERIAL
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }