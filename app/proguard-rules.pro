# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# OkHttp, Retrofit
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# Moshi
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep class kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoaderImpl
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-keepclassmembernames @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
}

#Sublime Picker
-keepnames class com.appeaser.sublimepickerlibrary.datepicker.** {
    <fields>;
    <methods>;
}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#pushwoosh
-keep class com.pushwoosh.** { *; }
-dontwarn com.pushwoosh.**
-dontwarn com.arellomobile.**
-keep class com.arellomobile.** { *; }

#Sentry
-keepattributes LineNumberTable,SourceFile
-dontwarn org.slf4j.**
-dontwarn javax.**
-keep class io.sentry.event.Event { *; }

#okhttp - disable minify for requests / responses
-keep class merchant.mokka.api.request.* { *; }
-keep class merchant.mokka.api.response.** { *; }
-keep class merchant.mokka.model.** { *; }

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepclasseswithmembers class merchant.mokka.api.apis.* { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>