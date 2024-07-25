package com.adins.mss.coll.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.adins.mss.base.GlobalData;
import com.adins.mss.coll.EmergencyLockActivity;
import com.adins.mss.coll.R;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.tracking.LocationTrackingService;

public class MCLocationTrackingService extends LocationTrackingService {
    public MCLocationTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getPackageName(), getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("App Service Persistent");

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
        boolean enableEmergency = false;
        GeneralParameter gp = GeneralParameterDataAccess.getOne(getApplicationContext(),
                        GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                        Global.GS_ENABLE_EMERGENCY_MC);
        if(null != gp){
            enableEmergency = "1".equals(gp.getGs_value());
        }
        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(this,
                "GlobalData", Context.MODE_PRIVATE);
        if(sharedPref.contains("HAS_LOGGED")) {
            if (enableEmergency) {
                // Create an Intent for the activity you want to start
                Intent resultIntent = new Intent(this, EmergencyLockActivity.class);
                // Create the TaskStackBuilder and add the intent, which inflates the back stack
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // Get the PendingIntent containing the entire back stack
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat.Builder(this, getPackageName())
                        .setSmallIcon(R.drawable.icon_notif_new)
                        .setContentTitle(getString(R.string.app_name))
                        .addAction(R.drawable.ic_sos, "Emergency!", resultPendingIntent)
                        .setContentText(getString(R.string.application_is_running))
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                        .setOngoing(true)
                        .build();

                startForeground(2043, notification);
            } else {
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        String manufacture = Build.MANUFACTURER.toLowerCase();
        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
                "GlobalData", Context.MODE_PRIVATE);
        boolean hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
        if(hasLogged) {
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static class UserLogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent locationTrackingService = new Intent(context, MCLocationTrackingService.class);
            context.stopService(locationTrackingService);
        }
    }
}
