# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\gigin.ginanjar\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-optimizationpasses 5

#When not preverifing in a case-insensitive filing system, such as Windows. Because this tool unpacks your processed jars, you should then use:
-dontusemixedcaseclassnames

#Specifies not to ignore non-public library classes. As of version 4.5, this is the default setting
-dontskipnonpubliclibraryclasses

#Preverification is irrelevant for the dex compiler and the Dalvik VM, so we can switch it off with the -dontpreverify option.
-dontpreverify

#Specifies to write out some more information during processing. If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
-verbose

#The -optimizations option disables some arithmetic simplifications that Dalvik 1.0 and 1.5 can't handle. Note that the Dalvik VM also can't handle aggressive overloading (of static fields).
#To understand or change this check http://proguard.sourceforge.net/index.html#/manual/optimizations.html
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#To repackage classes on a single package
-repackageclasses ''

#Uncomment if using annotations to keep them.
-keepattributes *Annotation*, Override
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-dontwarn javax.script.**
-dontwarn org.apache.commons.logging.**
-dontwarn java.beans.**
-dontwarn okio.**
-dontwarn com.androidquery.auth.TwitterHandle
-dontwarn org.bouncycastle.**

-keep class javax.** {*;}
-keep class org.apache.commons.logging.** {*;}
-keep class java.beans.** {*;}
-keep class org.apache.commons.jexl2.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class okio.** {*;}
-keep class oauth.signpost.** {*;}
-keep class org.apache.log4j.Logger {*;}
-keep class java.nio.** {*;}
-keep class junit..** {*;}
-keep class oauth.signpost.commonshttp.** {*;}
-keep class net.sqlcipher.** {
    *;
}

#Keep classes that are referenced on the AndroidManifest
#-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends com.adins.mss.base.MssFragmentActivity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.fragment.app.FragmentActivity
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends com.adins.mss.foundation.http.MssRequestType
-keep public class * extends com.adins.mss.foundation.http.MssResponseType
-keep public class com.android.vending.licensing.ILicensingService
-keep class com.adins.**.*Request
-keep class com.adins.**.*Response
-keep class android.** { *; }
-keep class com.google.** { *; }
#To remove debug logs:
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** e(...);
    public static *** i(...);
}

#To avoid changing names of methods invoked on layout's onClick.
# Uncomment and add specific method names if using onClick on layouts
-keepclassmembers class * {
 public void onClickButton(android.view.View);
}

#Maintain java native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

#To maintain custom components names that are used on layouts XML.
#Uncomment if having any problem with the approach below
#-keep public class custom.components.package.and.name.**

#To maintain custom components names that are used on layouts XML:
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#To keep parcelable classes (to serialize - deserialize objects to sent through Intents)
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}

#Keep Interface
-keep interface * {*;}
-keep class sun.misc.Unsafe { *; }

###### ADDITIONAL OPTIONS NOT USED NORMALLY

#To keep callback calls. Uncomment if using any
#http://proguard.sourceforge.net/index.html#/manual/examples.html#callback
#-keep class mypackage.MyCallbackClass {
#   void myCallbackMethod(java.lang.String);
#}

#Uncomment if using Serializable
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class * implements java.io.Serializable {
    public protected <fields>;
}

#Keep fields for Gson transactions
-keep public class * extends com.adins.mss.foundation.http.MssRequestType{
      <fields>;
  }
-keep public class * extends com.adins.mss.foundation.http.MssResponseType{
      <fields>;
}
-keep public class * extends com.adins.mss.foundation.http.KeyValue{
      <fields>;
}

-ignorewarnings


#Keep Class For MSS

-keep class com.adins.mss.dao.** {*;}
-keep class com.adins.mss.base.login.DefaultLoginModel
-keep public class com.gadberry.** {*;}

-keep public class com.adins.mss.base.authentication.AuthenticationResultBean{
    <fields>;
}

-keep public class com.adins.mss.foundation.formatter.DateFormatter{
   public <methods>;
}

-keep public class com.adins.mss.logger.Logger{
   public <methods>;
}

-keep class com.androidquery.AQuery {
     public protected <methods>;
     public protected <fields>;
}

-keep class com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences {*;}
-keep public class com.adins.mss.foundation.UserHelp.** {*;}