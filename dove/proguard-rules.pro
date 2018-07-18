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

# Global
-verbose
#-ignorewarnings
# Iteration optimization times
-optimizationpasses 5
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-dontwarn com.google.common.**
-dontwarn com.google.errorprone.**
-dontwarn afu.org.checkerframework.checker.**
-dontwarn org.checkerframework.checker.**

-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.preference.Preference

-keep class * extends cn.wrh.smart.dove.mvp.AbstractViewDelegate

-keepclassmembers class cn.wrh.smart.dove.** {
  @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
