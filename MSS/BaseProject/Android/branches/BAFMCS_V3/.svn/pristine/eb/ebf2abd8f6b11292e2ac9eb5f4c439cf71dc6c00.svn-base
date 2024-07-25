package com.adins.mss.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.core.text.HtmlCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.DeveloperOptionActivity;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.LoginActivity;
import com.adins.mss.base.ServerLinkActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.coll.BuildConfig;
import com.adins.mss.coll.NewMCMainActivity;
import com.adins.mss.coll.R;
import com.adins.mss.coll.services.FirebaseMessagingService;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Broadcast;
import com.adins.mss.foundation.config.ConfigFileReader;
import com.adins.mss.foundation.db.dataaccess.BroadcastDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ACRAConfigurationException;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;

import java.util.List;
import java.util.Properties;

/**
 * Created by dian.ina on 04/06/2015.
 */

//@ReportsCrashes(
//        formUri = "http://app.ad-ins.com:5984/acra-demotest/_design/acra-storage/_update/report",
//        reportType = org.acra.sender.HttpSender.Type.JSON,
//        httpMethod = org.acra.sender.HttpSender.Method.PUT,
//        formUriBasicAuthLogin="admin",
//        formUriBasicAuthPassword="12345",
//        customReportContent = {
//                ReportField.APP_VERSION_CODE,
//                ReportField.APP_VERSION_NAME,
//                ReportField.ANDROID_VERSION,
//                ReportField.INSTALLATION_ID,
//                ReportField.PACKAGE_NAME,
//                ReportField.REPORT_ID,
//                ReportField.BUILD,
//                ReportField.STACK_TRACE,
//                ReportField.AVAILABLE_MEM_SIZE,
//                ReportField.TOTAL_MEM_SIZE,
//                ReportField.SETTINGS_SECURE,
//                ReportField.DISPLAY,
//                ReportField.CUSTOM_DATA
//        },
//        mode = ReportingInteractionMode.TOAST,
//        resToastText = R.string.toast_crash
//)

public class MSMApplication extends AppContext {
    private BroadcastReceiver mReceiver;
    private List<Broadcast> broadcastList;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        final ACRAConfiguration config = new ACRAConfiguration();
        if(BuildConfig.FLAVOR.contains("prod")){
            config.setFormUri("http://app.ad-ins.com:5984/acra-aitmss-prod/_design/acra-storage/_update/report");
        }else{
            config.setFormUri("http://app.ad-ins.com:5984/acra-aitmss-dev/_design/acra-storage/_update/report");
        }
        config.setReportType(org.acra.sender.HttpSender.Type.JSON);
        config.setHttpMethod(org.acra.sender.HttpSender.Method.PUT);
        config.setFormUriBasicAuthLogin("admin");
        config.setFormUriBasicAuthPassword("12345");
        ReportField[] customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.INSTALLATION_ID,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE,
                ReportField.AVAILABLE_MEM_SIZE,
                ReportField.TOTAL_MEM_SIZE,
                ReportField.SETTINGS_SECURE,
                ReportField.DISPLAY,
                ReportField.CUSTOM_DATA,
                ReportField.SHARED_PREFERENCES
        };
        config.setCustomReportContent(customReportContent);

        //disable acra => set acra custom preference sesuai app.properties
        Properties properties = ConfigFileReader.propertiesFromFile(this, GlobalData.PROPERTY_FILENAME);
        boolean disableAcra = Boolean.parseBoolean(properties.getProperty(GlobalData.PROP_DISABLE_ACRA));
        Global.ACRA_DISABLED = disableAcra;
        String acraCustomPrefName = "ACRACUSTOMPREF";
        SharedPreferences acracustompref = getSharedPreferences(acraCustomPrefName, Context.MODE_PRIVATE);
        acracustompref.edit().putBoolean(ACRA.PREF_DISABLE_ACRA,disableAcra).commit();

        config.setSharedPreferenceName(acraCustomPrefName);
        config.setSharedPreferenceMode(Context.MODE_PRIVATE);

        try {
            config.setMode(ReportingInteractionMode.TOAST);
        } catch (ACRAConfigurationException e) {
            e.printStackTrace();
        }
        config.setResToastText(R.string.toast_crash);

        ACRA.init(this, config);

        //setting firebase crashlytic instance
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FireCrash.setInstance(FirebaseCrashlytics.getInstance());

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(final Activity activity) {
                context = activity;
                broadcastList = BroadcastDataAccess.getAllNotShown(activity);

                showNewBroadcast();

                mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String title = intent.getStringExtra(FirebaseMessagingService.KEY_BROADCAST_MESSAGE_TITLE);
                        if(null == title || title.length() < 1){
                            title = getApplicationContext().getString(com.adins.mss.base.R.string.push_notification);
                        }

                        final String broadcastId = intent.getStringExtra(FirebaseMessagingService.KEY_BROADCAST_MESSAGE_ID);
                        String notif = intent.getStringExtra(FirebaseMessagingService.KEY_BROADCAST_MESSAGE_MESSAGE);

                        final NiftyDialogBuilder ndb = NiftyDialogBuilder.getInstance(activity);
                        ndb.withTitle(title)
                                .withMessage(HtmlCompat.fromHtml(notif, HtmlCompat.FROM_HTML_MODE_LEGACY))
                                .withButton1Text(context.getString(R.string.btnOk))
                                .setButton1Click(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                                        NewTimelineFragment.SendUpdateNotification sendUpdateNotif = new NewTimelineFragment.SendUpdateNotification(activity, uuidUser, broadcastId);
                                        sendUpdateNotif.execute();
                                        ndb.dismiss();
                                        onBroadcastDismissed(broadcastId);
                                    }
                                })
                                .show();
                    }
                };

                if(shouldActivityShowBroadcast(activity)){
                    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(activity);
                    manager.registerReceiver(mReceiver, new IntentFilter(FirebaseMessagingService.INTENT_BROADCAST_MESSAGE));
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(activity);
                manager.unregisterReceiver(mReceiver);
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private boolean shouldActivityShowBroadcast(Activity activity){
        return !(activity instanceof LoginActivity
                || activity instanceof ServerLinkActivity
                || activity instanceof DeveloperOptionActivity
        );
    }

    private void showNewBroadcast(){
        if(broadcastList != null && !broadcastList.isEmpty()){
            Broadcast broadcast = broadcastList.remove(0);
            showBroadcast(broadcast);
        }
    }

    private void showBroadcast(final Broadcast broadcast){
        String title = broadcast.getTitle();
        if(null == title || title.length() < 1){
            title = getApplicationContext().getString(com.adins.mss.base.R.string.push_notification);
        }

        final NiftyDialogBuilder ndb = NiftyDialogBuilder.getInstance(context);
        ndb.withTitle(title)
                .withMessage(HtmlCompat.fromHtml(broadcast.getMessage(), HtmlCompat.FROM_HTML_MODE_LEGACY))
                .withButton1Text(context.getString(R.string.btnOk))
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        String uuidNotification = broadcast.getUuid_broadcast();
                        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                        NewTimelineFragment.SendUpdateNotification sendUpdateNotif = new NewTimelineFragment.SendUpdateNotification(context, uuidUser, uuidNotification);
                        sendUpdateNotif.execute();
                        ndb.dismiss();
                        onBroadcastDismissed(broadcast.getUuid_broadcast());
                    }
                })
                .show();
    }

    private void onBroadcastDismissed(String uuidBroadcast){
        Broadcast broadcast = BroadcastDataAccess.getById(context, uuidBroadcast);
        broadcast.setIs_shown(true);
        BroadcastDataAccess.addOrUpdate(context, broadcast);
    }

    @Override
    public Class getHomeClass() {
        return NewMCMainActivity.class;
    }
}
