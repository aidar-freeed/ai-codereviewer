package com.adins.mss.base.commons;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;

import com.adins.mss.base.R;

import java.util.List;

public class Helper {

    public static Helper getInstance(){
        return new Helper();
    }

    //Method for read developer options state
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isDevEnabled(Context context) {
        int mode = 0;

        if (Build.VERSION.SDK_INT >= 17) {
            mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        } else {
            mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 0);
        }

        return mode == 1;
    }

    public static void requestAutostartService(Context context) {

        SharedPreferences securityPrefsPrefs = context.getSharedPreferences(context.getString(R.string.security_prefs), Context.MODE_PRIVATE);
        if (securityPrefsPrefs.contains("autostart") && securityPrefsPrefs.getBoolean("autostart", false)) return;

        Intent intent = new Intent();
        String manufacture = Build.MANUFACTURER.toLowerCase();

        switch (manufacture) {
            case "xiaomi":
                intent.setComponent(new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                break;
            case "oppo":
                intent.setComponent(new ComponentName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"));

                break;
            case "vivo":
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                break;
            default:
                break;
        }

        List<ResolveInfo> arrayList = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        if (arrayList.size() > 0) {
            securityPrefsPrefs.edit().putBoolean("autostart", true).apply();
            context.startActivity(intent);
        }
    }
}
