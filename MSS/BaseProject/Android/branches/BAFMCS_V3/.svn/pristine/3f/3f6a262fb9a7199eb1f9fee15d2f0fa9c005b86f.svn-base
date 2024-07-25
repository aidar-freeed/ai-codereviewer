package com.services.plantask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ConnectivityChangeReceiver extends ConnectivityManager.NetworkCallback {
    private ConnectivityListener listener;
    private Handler handler;

    public interface ConnectivityListener{
        void onOnline();
        void onOffline();
    }

    public ConnectivityChangeReceiver(ConnectivityListener listener) {
        this.listener = listener;
        handler = new Handler();
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        if(listener == null)
            return;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onOnline();
            }
        },1000);
        Log.d("NCallback","Available");
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        Log.d("NCallback","Capability Change");
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
        Log.d("NCallback","Unavailable");
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        if(listener == null)
            return;
        listener.onOffline();
        Log.d("NCallback","Lost");
    }

}
