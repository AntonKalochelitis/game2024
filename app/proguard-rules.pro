# AdMob rules
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }

# Game Models and Database
-keep class com.wdevelop.game2048.data.** { *; }
-keep class com.wdevelop.game2048.TileModel { *; }
-keep class com.wdevelop.game2048.GameState { *; }

# Jetpack Compose and Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# Fix for potential crashes on older Android versions with Compose
-keepclassmembers class androidx.compose.ui.platform.ComposeView {
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve Line Numbers for CrashHandler
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
