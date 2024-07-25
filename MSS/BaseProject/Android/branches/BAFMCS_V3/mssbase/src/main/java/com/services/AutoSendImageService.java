package com.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoSendImageService extends Service {
    private AutoSendImageThread auto;

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAutoSendImageThread();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//TODO:		auto = MainMenuActivity.autoSendImage;
        startAutoSendImageThread();
    }

    public synchronized void startAutoSendImageThread() {
        if (auto == null) {
            auto = new AutoSendImageThread(getApplicationContext(),this);
            auto.start();
        } else {
            auto.start();
        }
    }

    public synchronized void stopAutoSendImageThread() {
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