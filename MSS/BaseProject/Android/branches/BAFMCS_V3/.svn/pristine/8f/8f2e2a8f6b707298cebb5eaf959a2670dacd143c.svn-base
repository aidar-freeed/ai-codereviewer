package com.adins.mss.base;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.multidex.MultiDexApplication;

import com.adins.mss.base.crashlytics.FireCrash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gigin.ginanjar on 25/02/2016.
 */
public abstract class AppContext extends MultiDexApplication {
    protected static Context context = null;
    protected static List<IMemoryInfo> memInfoList = new ArrayList<AppContext.IMemoryInfo>();
    protected static AppContext instance;
    private String versionName;
    private int versionCode;

    public static AppContext getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return AppContext.context;
    }

    public static void registerMemoryListener(IMemoryInfo implementor) {
        memInfoList.add(implementor);
    }

    public static void unregisterMemoryListener(IMemoryInfo implementor) {
        memInfoList.remove(implementor);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            try {
                // Activity at the front will get earliest than activity at the
                // back
                for (int i = memInfoList.size() - 1; i >= 0; i--) {
                    try {
                        memInfoList.get(i).goodTimeToReleaseMemory();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
//        ACRA.init(this);
        super.onCreate();

        instance = this;
    }

    public String getVersionName() {
        if (versionName == null || versionName.isEmpty()) {
            try {
                versionName = retrieveVersionName();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return versionName;
    }

    private String retrieveVersionName() throws PackageManager.NameNotFoundException {
        return getPackageManager().getPackageInfo(
                getPackageName(), 0).versionName;
    }

    public int getVersionCode() {
        if (versionCode == 0) {
            try {
                versionCode = retrieveVersionCode();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return versionCode;
    }

    private int retrieveVersionCode() throws PackageManager.NameNotFoundException {
        return getPackageManager().getPackageInfo(
                getPackageName(), 0).versionCode;
    }

    public abstract Class getHomeClass();

    public interface IMemoryInfo {
        void goodTimeToReleaseMemory();
    }
}
