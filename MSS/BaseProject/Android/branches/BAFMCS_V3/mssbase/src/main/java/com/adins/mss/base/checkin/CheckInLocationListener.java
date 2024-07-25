package com.adins.mss.base.checkin;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.checkin.activity.CheckInView;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.questiongenerator.form.LocationTagingView;
import com.google.android.gms.location.LocationListener;

import java.util.Date;

/**
 * @author gigin.ginanjar
 */
public class CheckInLocationListener implements LocationListener {
    public static Location currentLocation;
    private LocationInfo locationInfo = new LocationInfo();
    private Context mContext;

    public CheckInLocationListener(Context context) {
        mContext = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (location.isFromMockProvider()){
                DialogManager.showMockDialog(mContext);
                return;
            }
        }

        if (location == null) return;
        if (location.getAccuracy() < 1000) {
            currentLocation = location;
            LocationTrackingManager ltm = Global.LTM;

            Global.FLAG_LOCATION_TYPE = 1; //CheckIn
            locationInfo = ltm.getCurrentLocation(Global.FLAG_LOCATION_CHECKIN);
            locationInfo.setLatitude(String.valueOf(location.getLatitude()));
            locationInfo.setLongitude(String.valueOf(location.getLongitude()));
            Date date = new Date(location.getTime());
            locationInfo.setGps_time(date);
            locationInfo.setIs_gps_time(Global.TRUE_STRING);
            locationInfo.setAccuracy(Math.round(location.getAccuracy()));

            try {
                CheckInView.setNewLocation();
            } catch (Exception e) {
                FireCrash.log(e);
                // TODO: handle exception
            }

            try {
                LocationTagingView.setNewLocation();
            } catch (Exception e) {
                FireCrash.log(e);
            }

            CheckInManager.stopPeriodicUpdates();

        }
    }

    /**
     * Get Current Location that have best Accuracy
     *
     * @return <b>Location</b>
     */
    public LocationInfo getNewLocationInfo() {
        return locationInfo;
    }
}
