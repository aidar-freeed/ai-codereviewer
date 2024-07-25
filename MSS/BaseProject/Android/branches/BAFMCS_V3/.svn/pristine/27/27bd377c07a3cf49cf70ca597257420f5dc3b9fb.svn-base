package com.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service {
    public static NotificationThread auto;
    public static Class mss;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        startNotificationThread();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        stopNotificationThread();
    }

    public synchronized void startNotificationThread() {
        if (auto == null) {
            auto = new NotificationThread(getApplicationContext(),this);
            auto.start();
        } else {
            auto.start();
        }
    }

    public synchronized void stopNotificationThread() {
        if (auto != null) {
            auto.requestStop();
            auto = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
