package com.services;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoSendTaskService extends Service {
    private AutoSendTaskThread auto;

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAutoSendThread();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//TODO:		auto = MainMenuActivity.autoSendTask;
        startAutoSendThread();
    }

    public synchronized void startAutoSendThread() {
        if (auto == null) {
            auto = new AutoSendTaskThread(getApplicationContext(),this);
            auto.start();
        } else {
            auto.start();
        }
    }

    public synchronized void stopAutoSendThread() {
        if (auto != null) {
            auto.requestStop();
            auto = null;
        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
