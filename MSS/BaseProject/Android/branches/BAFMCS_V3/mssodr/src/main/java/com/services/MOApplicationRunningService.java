package com.services;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;

import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.odr.R;

public class MOApplicationRunningService extends Service {
    public MOApplicationRunningService() {}


    public void onCreate() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getPackageName(), getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("App Service Persistent");

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(this,
                "GlobalData", Context.MODE_PRIVATE);
        if (sharedPref.contains("HAS_LOGGED")) {
            Notification notification = new NotificationCompat.Builder(this, getPackageName())
                    .setSmallIcon(R.drawable.icon_notif_new)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.application_is_running))
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setOngoing(true)
                    .build();

            startForeground(2043, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        String manufacture = Build.MANUFACTURER.toLowerCase();

        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
                "GlobalData", Context.MODE_PRIVATE);
        boolean hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
        if (hasLogged) {
            switch (manufacture) {
                case "oppo":
                case "vivo":
                    Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
                    restartServiceIntent.setPackage(getPackageName());

                    PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmService.set(
                            AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + 1000,
                            restartServicePendingIntent);
                    break;
                default:
                    break;
            }
        }
        super.onTaskRemoved(rootIntent);
    }

    public static class UserLogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent locationTrackingService = new Intent(context, MOApplicationRunningService.class);
            context.stopService(locationTrackingService);
        }
    }
}
