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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# Retrofit library rules
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions


# Okio library rules (Okio is a dependency of the Retrofit library)
-dontwarn okio.**


# Retrolanbda library rules
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*


# Jackson library rules
-keep @com.fasterxml.jackson.annotation.JsonIgnoreProperties class * { *; }
-keep class com.fasterxml.** { *; }
-keep class org.codehaus.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepclassmembers public final enum com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility {
    public static final com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility *;
}
-dontwarn com.fasterxml.jackson.databind.**


# Butter Knife library rules
-dontwarn butterknife.internal.**
-keep class butterknife.** {*;}
-keep class **$$ViewBinder {*;}
-keepclasseswithmembernames class * {
    @butterknife.** <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.** <methods>;
}
