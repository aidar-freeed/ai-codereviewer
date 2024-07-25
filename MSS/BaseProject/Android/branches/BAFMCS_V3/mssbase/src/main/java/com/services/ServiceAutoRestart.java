package com.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import androidx.legacy.content.WakefulBroadcastReceiver;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

import java.util.List;

/**
 * Created by loise on 12/27/2017.
 */

public class ServiceAutoRestart extends WakefulBroadcastReceiver {

    public static void startAutoRestartService(Context context) {
        PendingIntent pendingIntent;
        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context, "GlobalData", Context.MODE_PRIVATE);
        boolean hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
        User user = GlobalData.getSharedGlobalData().getUser();
        if (user != null && hasLogged) {
            long delay = 1000L * 30;
            long time = SystemClock.elapsedRealtime() + delay;

            Intent alarmIntent = new Intent(context, ServiceAutoRestart.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            //untuk memodifikasi periode update ke database ganti AlarmManager.INTERVAL_DAY
            //dengan interval yang dibutuhkan, dan submitDataUsage akan berjalan tiap interval setelah
            //waktu yang telah ditentukan

            if (Build.VERSION.SDK_INT >= 23) {
                // Wakes up the device in Doze Mode
                am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, time,
                        pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                // Wakes up the device in Idle Mode
                am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pendingIntent);
            } else {
                // Old APIs
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pendingIntent);
            }
            Log.i("Alarm", "Alarm Started");

            checkLocationTracking(context);
        }
    }

    private static void checkLocationTracking(Context context) {
        if (null == Global.LTM) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            LocationTrackingManager manager = new LocationTrackingManager(tm, lm, context);
            manager.setMinimalDistanceChangeLocation(100);
            manager.setMinimalTimeChangeLocation(5000);
            manager.applyLocationListener(context);
            Global.LTM = manager;
        } else {
            LocationTrackingManager ltm = Global.LTM;
            if (!ltm.hasConnected()){
                ltm.connectLocationClient();
            }
        }
    }

    public static boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }

            }
        }
        return false;
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new ActivityNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Alarm", "Broadcast Recieved");
        if (!isAppRunning(context, context.getPackageName())) {
            Log.i("Alarm", "App Restarting...");
            openApp(context, context.getPackageName());
            Log.i("Alarm", "App Restarted");
        }
        startAutoRestartService(context);
    }

}
