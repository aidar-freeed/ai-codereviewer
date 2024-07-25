package com.adins.mss.base.checkin;

import android.content.Context;

import com.adins.mss.dao.LocationInfo;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public interface CheckInInterface {
    public void startGPSTracking(Context context);

    public LocationInfo getLocationInfoCheckIn();

    public LocationInfo getNewLocation();

    public void updateLocationCheckin();

    public LocationInfo getCurrentCheckIn();

    public String toLocationCheckInString(LocationInfo locationInfo);

    public LatLng getLatLng(LocationInfo locationInfo);

    public void getAddressLocation(Context context, LocationInfo locationInfo);

}
