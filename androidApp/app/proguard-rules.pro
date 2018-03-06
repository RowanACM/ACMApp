# Project specific ProGuard rules
# See https://github.com/krschultz/android-proguard-snippets

-dontobfuscate
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

# Retrofit 2.X
# https://square.github.io/retrofit/
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn javax.annotation.*

# Google Play Services
-dontwarn com.google.android.gms.**

# Picasso
-dontwarn com.squareup.okhttp.**