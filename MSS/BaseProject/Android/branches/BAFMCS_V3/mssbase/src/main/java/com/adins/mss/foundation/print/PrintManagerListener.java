package com.adins.mss.foundation.print;

/**
 * Created by angga.permadi on 3/3/2016.
 */
public interface PrintManagerListener {
    void onConnected(String deviceName);

    void onConnectFailed();

    void onConnecting();

    void onDisconnect();
}

