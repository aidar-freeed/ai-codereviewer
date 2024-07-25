package com.adins.mss.coll.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.services.ForegroundServiceNotification;

public class RestartAutoSendLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getSimpleName(), "Received message to restart service Tracking History");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            return;

        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context,
                "GlobalData", Context.MODE_PRIVATE);
        boolean hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
        String manufacture = Build.MANUFACTURER.toLowerCase();
        if (!ForegroundServiceNotification.isServiceAvailable(context, MCLocationTrackingService.class)
                && hasLogged) {
            switch (manufacture) {
                case "oppo":
                case "vivo":
                    context.startService(new Intent(context, MCLocationTrackingService.class));
                    break;
                default:
                    break;
            }
        }
    }
}
