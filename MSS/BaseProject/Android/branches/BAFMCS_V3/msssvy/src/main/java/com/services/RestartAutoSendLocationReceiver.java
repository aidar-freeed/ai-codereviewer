package com.services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

import java.util.List;

public class RestartAutoSendLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getSimpleName(), "Received message to restart service Tracking History");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            return;

        if (!isAppRunning(context, context.getPackageName())) {
            Intent main = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (main != null) {
                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(main);
        }
        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context,
                "GlobalData", Context.MODE_PRIVATE);
        boolean hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
        if (!ForegroundServiceNotification.isServiceAvailable(context, MSLocationTrackingService.class)
            && hasLogged) {
            String manufacture = Build.MANUFACTURER.toLowerCase();
            switch (manufacture) {
                case "vivo":
                    context.startService(new Intent(context, MSLocationTrackingService.class));
                    break;
                default:
                    break;
            }
        }
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
