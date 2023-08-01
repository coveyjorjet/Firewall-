-optimizationpasses 5
-repackageclasses ''
-ignorewarnings
-allowaccessmodification
-keepattributes
-renamesourcefileattribute

-keepnames class net.i2p.crypto.eddsa.**
-keep class com.ambabovpn.pro.AmbaboVpnProApp
-keep class com.ambabovpn.pro.preference.**
-keep public class com.trilead.ssh2.** {
public protected *;
}
-keep class org.conscrypt.** { *; }
-keep class net.** { *; }
-keep class org.** { *; }
-keep class android.** { *; }
-keep class org.apache.** { *; }
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption

## Google Play Services 4.3.23 specific rules ##
## https://developer.android.com/google/play-services/setup.html#Proguard ##


-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

#Httplibrary
-dontwarn org.apache.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.http.**
-dontwarn sun.security.**
-dontnote android.net.http.*
-dontnote org.apache.**
-dontnote org.apache.commons.logging.**
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**


-keep public class org.apache.** {*;}


-keep interface org.apache.http.**

# OrmLite uses reflection
-keepattributes Signature

-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

-keepclassmembers class com.j256.** {
  public *;
}
-keep public class com.j256.ormlite.**{
public protected *;
}
-keepattributes *Annotation*

-keep class com.google.android.gms.ads.rewarded.RewardedAd
-keep class com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
-keep class com.google.android.gms.ads.rewarded.RewardItem
-keep class com.google.android.gms.ads.rewarded.**

