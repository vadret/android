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

# MATERIAL
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }