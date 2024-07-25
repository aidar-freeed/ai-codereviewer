package com.adins.mss.base.checkout;

import android.location.Location;

import com.adins.mss.base.checkout.activity.CheckOutActivity;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.google.android.gms.location.LocationListener;

import java.util.Date;

public class CheckOutLocationListener implements LocationListener {
    public static Location currentLocation;
    private LocationInfo locationInfo = new LocationInfo();

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if (location != null) {
            if (location.getAccuracy() < 1000) {
                currentLocation = location;
                LocationTrackingManager ltm = Global.LTM;

                Global.FLAG_LOCATION_TYPE = 2; //CheckOut
                locationInfo = ltm.getCurrentLocation(Global.FLAG_LOCATION_CHECKOUT);
                locationInfo.setLatitude(String.valueOf(location.getLatitude()));
                locationInfo.setLongitude(String.valueOf(location.getLongitude()));
                Date date = new Date(location.getTime());
                locationInfo.setGps_time(date);
                locationInfo.setIs_gps_time(Global.TRUE_STRING);
                locationInfo.setAccuracy(Math.round(location.getAccuracy()));

                CheckOutActivity.setNewLocation();
                CheckOutManager.stopPeriodicUpdates();
            }
        } else {
        }
    }

    /**
     * Get Current Location which Good Accuracy
     *
     * @return <b>Location</b>
     */
    public LocationInfo getNewLocationInfo() {
        // TODO Auto-generated method stub
        return locationInfo;
    }
}
