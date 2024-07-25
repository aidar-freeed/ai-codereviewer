package com.adins.mss.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.config.ConfigFileReader;
import com.adins.mss.odr.BuildConfig;
import com.adins.mss.odr.NewMOMainActivity;
import com.adins.mss.odr.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ACRAConfigurationException;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;

import java.util.Properties;

/**
 * Created by wendy.ac on 10/02/20109.
 */

public class MSMApplication extends AppContext {

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        final ACRAConfiguration config = new ACRAConfiguration();
        if (BuildConfig.FLAVOR.contains("prod")) {
            config.setFormUri("http://app.ad-ins.com:5984/acra-aitmss-prod/_design/acra-storage/_update/report");
        } else {
            config.setFormUri("http://app.ad-ins.com:5984/acra-aitmss-dev/_design/acra-storage/_update/report");
        }
//        else {
//            config.setFormUri("http://app.ad-ins.com:5984/acra-wfimoscs-dev/_design/acra-storage/_update/report");
//        }
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
    }

    @Override
    public Class getHomeClass() {
        return NewMOMainActivity.class;
    }

//    @Override
//    @TargetApi(Build.VERSION_CODES.N)
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(LocaleHelper.onAttach(newBase));
//    }
}
