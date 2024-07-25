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
#-injars      bin/classes
#-injars      libs
##-outjars     bin/classes-processed.jar
##-libraryjars "C:\Users\gigin.ginanjar\AppData\Local\Android\sdk/platforms\android-23/android.jar"
#
-dontpreverify
#-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic,!field
-keepattributes Signature
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep public class * extends android.app.Activity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.fragment.app.FragmentActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends androidx.appcompat.app.AppCompatActivity


-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.NewMenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

#-keepclassmembers class **.R$* {
#    public static <fields>;
#}

-keep class com.adins.mss.base.R$* {
    public static <fields>;
}

-keepclassmembers class ** {
   public static *** parse(***);
}
-keepclassmembers class ** {
  public static <fields>;
}

-keep public class com.adins.mss.logger.Logger{
   public <methods>;
}

### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**

### greenDAO 2
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes InnerClasses

-dontwarn okio.**
-dontwarn com.androidquery.**
-dontwarn org.apache.commons.jexl2.**
-dontwarn com.fasterxml.uuid.**
-dontwarn org.bouncycastle.**
-dontwarn java.lang.**
-dontwarn fr.castorflex.android.smoothprogressbar.**
-dontwarn lib.gegemobile.**
-dontwarn uk.co.senab.actionbarpulltorefresh.library.**
-dontwarn zj.com.**
-dontwarn org.apache.**
-dontwarn com.pax.**
-dontwarn android.databinding.**

-keep class android.databinding.** { *; }
-keep class androidx.databinding.** { *; }
-keep class * extends androidx.databinding.DataBinderMapper { *; }
-keep class com.adins.mss.base.databinding.** { *; }


#Keep classes that are referenced on the AndroidManifest
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends com.adins.mss.base.MssFragmentActivity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.fragment.app.FragmentActivity
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keepclassmembers class * extends androidx.fragment.app.Fragment{
   public void *(android.view.View);
}
-keep class com.androidquery.AQuery {
     public protected <methods>;
     public protected <fields>;
}

-keep class android.** { *; }
-keep class com.google.** { *; }

-keep class com.google.gson.JsonSyntaxException {public protected static *; }

-keep class org.acra.sender.HttpSender {
    public protected <methods>;
    public protected <fields>;
}
-keep class androidx.fragment.app.FragmentManager{
    public protected <methods>;
    public protected <fields>;
}

-keep class org.acra.annotation.** {*; }
-keep class org.acra.ReportField {*; }
-keep class org.acra.ReportingInteractionMode {*; }
-keep class org.acra.annotation.ReportsCrashes {public protected static *; }
-keep class org.acra.ErrorReporter {
    public protected <methods>;
    public protected <fields>;
}
-keep class java.lang.String {
    public protected <methods>;
    public protected <fields>;
}
-keep public enum org.acra.sender.HttpSender.Type$** {
   *;
}
-keep public enum org.acra.sender.HttpSender.Method$** {
   *;
}

-keepclassmembers,allowoptimization enum * {
       public static **[] values();
       public static ** valueOf(java.lang.String);
 }

-keep class org.acra.annotation.** {*; }
-keep class org.acra.ReportField {*; }
-keep class org.acra.ReportingInteractionMode {*; }
-keep class org.acra.annotation.ReportsCrashes {public protected static *; }
-keep class org.acra.ErrorReporter {
    public protected <methods>;
    public protected <fields>;
}
-keep class java.lang.String {
    public protected <methods>;
    public protected <fields>;
}

-keepclassmembers,allowoptimization enum * {
       public static **[] values();
       public static ** valueOf(java.lang.String);
 }

#keep class for mss
#-keep class com.adins.foundation.**
-keep class com.adins.mss.dao.** {*;}
-keep class com.adins.mss.constant.** {*;}
-keep class com.adins.mss.base.crashlytics.** {*;}
-keep class com.adins.mss.foundation.** {*;}
-keep class com.adins.mss.foundation.db.dataaccess.** {*;}
-keep class com.adins.mss.base.login.DefaultLoginModel
-keep class com.squareup.okhttp.** {*;}
-keep class com.androidquery.** {*;}
-keep class android.view.animation.** {*;}
-keep class com.adins.mss.base.todolist.todayplanrepository.** {*;}

#Nendi: 17.12.2020
-keep class com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences {*;}

-keepclasseswithmembernames class com.adins.mss.base.commons.SecondHelper

-keep public class com.gadberry.** {*;}

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

-keep public class com.adins.mss.base.util.Utility{
    public <methods>;
}

-keep class com.adins.mss.base.dynamictheme.DynamicTheme{
    public <methods>;
}

-keep class com.soundcloud.android.crop.Crop{
    public <methods>;
    public public <fields>;
}

-keep class com.adins.mss.base.dynamictheme.ThemeLoader{
    public <methods>;
    public public <fields>;
}

-keep public interface com.adins.mss.base.dynamictheme.ThemeLoader$ColorSetLoaderCallback {*;}

-keep class com.adins.mss.base.dynamictheme.ThemeUtility{
    public <methods>;
}
-keep public class com.adins.mss.base.util.EventBusHelper{
   public <methods>;
}
-keep public class com.adins.mss.base.util.LocaleHelper{
   public <methods>;
}

-keep public class com.adins.mss.base.checkin.CheckInManager{
   public <methods>;
}

-keep public class com.adins.mss.base.errorhandler.ErrorMessageHandler{
   public <methods>;
}
-keep public class com.adins.mss.base.errorhandler.IShowError{
   public <methods>;
}

-keep class net.sqlcipher.** {
    *;
}

-keep class com.zebra.** { *; }
-keep interface com.zebra.** { *; }
-keep class com.fasterxml.** { *; }
-keep interface com.fasterxml.** { *; }
-keep interface com.adins.libs.** { *; }
-keep class uk.co.deanwild.materialshowcaseview.** { *; }
-keep interface uk.co.deanwild.materialshowcaseview.** { *; }

-keep class com.adins.mss.base.commons.CommonImpl{
    public <methods>;
}
-keep class com.adins.mss.base.tasklog.TaskLogImpl{
     public <methods>;
 }
-keep class com.adins.mss.base.commons.ViewImpl{
      public <methods>;
}
-keep class com.adins.mss.base.commons.TaskListener{
    public <methods>;
}

-keep class com.adins.mss.base.depositreport.TaskLogHelper{
    public <methods>;
}
-keep class com.adins.mss.base.dynamicform.form.questions.viewholder.ImageQuestionViewHolder{
    public <methods>;
}
-keep class com.adins.mss.base.timeline.Constants{
    public <methods>;
}
-keep class com.adins.mss.base.dynamicform.TaskDBean{
    public <methods>;
}
-keep class org.acra.ACRAConfiguration{
    public <methods>;
}
-keep class com.services.ForegroundServiceNotification{
    public <methods>;
}
-keep class com.services.SurveyAssignmentThread{
    public <methods>;
}
-keep class com.adins.mss.base.commons.BackupManager{
    public <methods>;
}
-keep class com.adins.mss.base.mainmenu.NewMenuItem{
    public <methods>;
}

-keep class com.adins.mss.base.NewMainActivity{
     public static <fields>;
     public protected <methods>;
     public protected <fields>;
}

-keep class com.adins.mss.base.AppContext {
     public protected <methods>;
     public protected <fields>;
}
-keep class org.acra.annotation.ReportsCrashes {
     public protected <methods>;
     public protected <fields>;
}
-keep class org.acra.ReportField {
     public protected <methods>;
     public protected <fields>;
}
-keep class org.acra.ReportingInteractionMode {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.GlobalData {
    public protected <methods>;
    public protected <fields>;
}
-keep class com.adins.mss.base.util.GsonHelper {
     public protected <methods>;
     public protected <fields>;
}

-keep class com.adins.mss.base.util.ExcludeFromGson{
     public protected <methods>;
     public protected <fields>;

}

-keep class com.adins.mss.foundation.dialog.NiftyDialogBuilder {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.http.HttpConnectionResult {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.http.HttpCryptedConnection {
     public protected <methods>;
     public protected <fields>;
}

-keep class org.acra.ACRA {
    public protected <methods>;
    public protected <fields>;
}
-keep class com.adins.mss.foundation.http.KeyValue {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.http.MssResponseType {
     public protected <methods>;
     public protected <fields>;
}
-keep class androidx.fragment.app.Fragment  {
    public protected <methods>;
    public protected <fields>;
}
-keep class com.adins.mss.dao.TaskH {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.timeline.MapsViewer {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.dialog.DialogManager {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.formatter.Tool {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.image.Utils {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.mainmenu.MainMenuActivity {
         public protected <methods>;
         public protected <fields>;
}
-keep class androidx.fragment.app.FragmentActivity {
     public protected <methods>;
     public protected <fields>;
}
-keep class androidx.fragment.app.FragmentTransaction {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.dynamicform.JsonResponseSubmitTask {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.mikepenz.aboutlibraries.entity.Library {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.dynamicform.CustomerFragment {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.dynamicform.SurveyHeaderBean {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.tasklog.TaskLogArrayAdapter {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.timeline.TimelineManager {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.todolist.ToDoList {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.todolist.form.JsonResponseTaskList {
     public protected <methods>;
}
-keep class com.adins.mss.dao.Scheme {
     public protected <methods>;
}
-keep class com.adins.mss.dao.User {
     public protected <methods>;
}
-keep class com.adins.mss.foundation.db.dataaccess.SchemeDataAccess {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.db.dataaccess.TaskHDataAccess {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.db.dataaccess.TimelineDataAccess {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.http.MssRequestType {
     public protected <methods>;
}
-keep class com.mikepenz.aboutlibraries.entity.License {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.about.activity.AboutActivity {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.login.DefaultLoginModel {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.LoginActivity {
     public protected <methods>;
     public protected <fields>;
}
-keep class androidx.core.app.NotificationCompat {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.dynamicform.JsonRequestTaskD {
     public protected <methods>;
}
-keep class com.adins.mss.base.dynamicform.JsonResponseTaskD {
     public protected <methods>;
}
-keep class com.adins.mss.base.timeline.activity.Timeline_Activity {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.dao.TaskD {
     public protected <methods>;
}
-keep class com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.db.dataaccess.TaskDDataAccess {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.notification.Notification {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.ChangePasswordFragment {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.mainmenu.MainMenuHelper {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.tasklog.TaskLogImplImpl {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.timeline.MenuAdapter {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.timeline.MenuModel {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.todo.form.GetSchemeTask {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.todolist.form.StatusSectionFragment {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.todolist.form.TaskListTask {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.todolist.form.TaskList_Fragment {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.services.NotificationThread {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.todo.form.NewTaskActivity {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.todo.form.NewTaskAdapter {
     public protected <methods>;
}
-keep class com.adins.mss.base.todolist.DoList {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.SynchronizeActivity {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.formatter.Formatter {
     public protected <methods>;
     public protected <fields>;
}
-keep class androidx.fragment.app.DialogFragment {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.dynamicform.Constant {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.dynamicform.DynamicFormActivity {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.dynamicform.SendResultActivity {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.base.dynamicform.TaskManager {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.adins.mss.foundation.questiongenerator.QuestionBean {
     public protected <methods>;
     public protected <fields>;
}
-keep class com.google.gson.JsonSyntaxException {public protected static *; }
-keep class com.adins.mss.foundation.db.DaoOpenHelper {public protected static *; }
-keep class com.adins.mss.foundation.image.JsonResponseImage {public protected static *; }
-keep class com.adins.mss.svy.models.SurveyorSearchResponse {public protected static *;}
-keep class com.adins.mss.svy.reassignment.JsonResponseServer {
     public protected <methods>;
     public protected <fields>;
}

-keep public class com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener{
     public protected <methods>;
}

-keep public class com.adins.mss.base.dynamicform.form.questions.viewholder.ExpandableRecyclerView$GroupViewHolder{
     public protected <methods>;
     public protected <fields>;
}

-keep public class com.adins.mss.base.dynamicform.QuestionSetTask{
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.adins.libs.nineoldandroids.view.ViewHelper{
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.adins.mss.base.dynamicform.form.questions.viewholder.ExpandableRecyclerView$Adapter{
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.adins.mss.base.dialogfragments.NewTaskDialog{
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.adins.mss.base.todolist.form.OnTaskListClickListener{
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.adins.mss.base.authentication.Authentication{
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.adins.mss.base.todolist.form.TasklistListener{
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.adins.mss.dummy.userhelp_dummy.Adapter.NewTaskLogDummyAdapter{
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.adins.mss.base.dynamicform.TaskManager$ApproveTaskOnBackground{
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.adins.mss.base.dynamicform.TaskManager$VerifTaskOnBackground{
      public protected <methods>;
      public protected <fields>;
}

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
    <methods>;
 }

-keep public class * extends RecyclerView.Adapter{
     <fields>;
     <methods>;
}

-keep public class * extends com.adins.mss.foundation.http.MssResponseType{
    <fields>;
    <methods>;
}

-keep public class com.adins.mss.foundation.UserHelp.** {*;}