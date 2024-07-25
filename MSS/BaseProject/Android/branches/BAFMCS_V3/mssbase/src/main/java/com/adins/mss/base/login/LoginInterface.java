package com.adins.mss.base.login;

import android.location.LocationListener;
import android.location.LocationManager;

import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

/**
 * Created by kusnendi.muhamad on 26/07/2017.
 */

public interface LoginInterface extends LocationListener {
    public boolean isRooted();

    public void initializePreferences();

    public void bindLocationListener();

    public void removeUpdateLocation();

    public LocationManager getLocationManager();

    public ObscuredSharedPreferences getSharedPref();

    public ObscuredSharedPreferences getLoginPreferences();
//    public void setModel(DefaultLoginModel dataContex);
}
