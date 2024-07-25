package com.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.adins.mss.base.R;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

/**
 * Created by loise on 12/21/2017.
 */

public class ForegroundServiceNotification {

    private static final int NOTIFICATION_ID = 1094;
    public static final String NOTIFICATION_CHANNEL_ID = "10002";

    private static Notification notification;

    public static Notification getNotification(Context context) {

        if (notification == null) {
            notification = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.application_is_running))
                    .setSmallIcon(R.drawable.icon_notif_new)
                    .setPriority(5)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .build();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        return notification;
    }

    public static int getNotificationId() {
        return NOTIFICATION_ID;
    }

    public static void startForegroundService(Service service) {
        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(service.getApplicationContext(),
                "GlobalData", Context.MODE_PRIVATE);
        boolean hasLogged = sharedPref.getBoolean(Global.IDF_HAS_LOGGED, false);
        if (hasLogged){
            service.startForeground(getNotificationId(), getNotification(service.getApplication().getBaseContext()));
        } else {
            service.stopForeground(true);
        }
    }

    public static void stopForegroundService(Service service) {
        service.stopForeground(true);
    }

    public static void restartForegroundService(Service service) {
        if (isServiceAvailable(service.getApplication().getBaseContext(), service.getClass())) {
            stopForegroundService(service);
        }
        startForegroundService(service);
    }

    public static boolean isServiceAvailable(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            }
        }

        return false;
    }
}