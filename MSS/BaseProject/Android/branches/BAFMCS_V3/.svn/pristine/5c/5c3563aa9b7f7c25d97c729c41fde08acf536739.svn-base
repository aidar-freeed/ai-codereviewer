package com.adins.mss.foundation.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.text.format.DateFormat;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.User;
import com.adins.mss.logger.Logger;
import com.google.android.gms.location.LocationListener;

import org.acra.ACRA;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author gigin.ginanjar
 */
public class LocationListenerImpl implements LocationListener {
    public static Location currentLocation;
    private LocationInfo locationInfo = new LocationInfo();
    private Context context;

    public LocationListenerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location.isFromMockProvider()) {
            if(context != null){
                Intent intent = new Intent(context.getString(R.string.action_turn_off_mocklocation));
                context.sendBroadcast(intent);
            }

            return;
        }

        if (location != null) {
            Logger.d("LocationListenerImpl", "" + location.getLatitude() + " , " + location.getLongitude() + " , " + location.getAccuracy());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (!location.isFromMockProvider()) {
                    currentLocation = location;
                    LocationTrackingManager ltm = Global.LTM;

                    Global.FLAG_LOCATION_TYPE = 0; //Tracking

                    try {
                        locationInfo = ltm.getCurrentLocation(Global.FLAG_LOCATION_TRACKING);
                    } catch (Exception e){
                        FireCrash.log(e);
                    }

                    String lat = locationInfo.getLatitude();
                    String lng = locationInfo.getLongitude();

                    float accuracy = location.getAccuracy();

                    if (!String.valueOf(0d).equalsIgnoreCase(lat) && !String.valueOf(0d).equalsIgnoreCase(lng)) {
                        try {
                            if (accuracy <= GlobalData.getSharedGlobalData().getMaxAccuracySafely() && !Global.APPLICATION_ORDER.equals(application) && checkFlagTracking()) {
                                LocationTrackingManager.insertLocationInfoToDB(locationInfo);
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);

                        }
                    }
                    int greenAccuracy = GlobalData.getSharedGlobalData().getGreenAccuracy();
                    if (greenAccuracy == 0)
                        greenAccuracy = 50;
                    int yellowAccuracy = GlobalData.getSharedGlobalData().getYellowAccuracy();
                    if (yellowAccuracy == 0)
                        yellowAccuracy = 200;

                    if (location.getAccuracy() <= greenAccuracy) {
                        LocationTrackingManager.setLocationStatus(2);
                        if (!Global.APPLICATION_ORDER.equals(application)) {
                            checkFlagTracking();
                        }
                    } else if (location.getAccuracy() > greenAccuracy && location.getAccuracy() <= yellowAccuracy) {
                        LocationTrackingManager.setLocationStatus(1);
                    } else {
                        LocationTrackingManager.setLocationStatus(0);
                    }
                } else {
                    LocationTrackingManager.setLocationStatus(0);
                }
            } else {
                currentLocation = location;
                LocationTrackingManager ltm = Global.LTM;

                Global.FLAG_LOCATION_TYPE = 0; //Tracking

                locationInfo = ltm.getCurrentLocation(Global.FLAG_LOCATION_TRACKING);

                String lat = locationInfo.getLatitude();
                String lng = locationInfo.getLongitude();

                float accuracy = location.getAccuracy();

                if (!String.valueOf(0d).equalsIgnoreCase(lat) && !String.valueOf(0d).equalsIgnoreCase(lng)) {
                    try {
                        if (accuracy <= GlobalData.getSharedGlobalData().getMaxAccuracySafely() && !Global.APPLICATION_ORDER.equals(application) && checkFlagTracking()) {
                            LocationTrackingManager.insertLocationInfoToDB(locationInfo);
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (!Global.APPLICATION_ORDER.equals(application) && checkFlagTracking()) {
                            LocationTrackingManager.insertLocationInfoToDB(locationInfo);
                        }
                    }
                }
                int greenAccuracy = GlobalData.getSharedGlobalData().getGreenAccuracy();
                if (greenAccuracy == 0)
                    greenAccuracy = 50;
                int yellowAccuracy = GlobalData.getSharedGlobalData().getYellowAccuracy();
                if (yellowAccuracy == 0)
                    yellowAccuracy = 200;

                if (location.getAccuracy() <= greenAccuracy) {
                    LocationTrackingManager.setLocationStatus(2);
                    if (!Global.APPLICATION_ORDER.equals(application)) {
                        checkFlagTracking();
                    }
                } else if (location.getAccuracy() > greenAccuracy && location.getAccuracy() <= yellowAccuracy) {
                    LocationTrackingManager.setLocationStatus(1);
                } else {
                    LocationTrackingManager.setLocationStatus(0);
                }
            }
        } else {
            LocationTrackingManager.setLocationStatus(0);
        }

        Intent locationListener = new Intent(context.getString(R.string.action_location_listener));
        context.sendBroadcast(locationListener);
    }

    /**
     * Get Current Location which Good Accuracy
     *
     * @return <b>Location</b>
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    public boolean checkFlagTracking() {
        try {
            User user = GlobalData.getSharedGlobalData().getUser();
            String hourFromWebStart, hourFromWeb;
            Calendar cal, calStart;
            String trackingDays;
            List trackingDaysList;
            int thisDayInt = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            String thisDay;
            thisDayInt -= 1;

            thisDay = String.valueOf(thisDayInt);

            if (null != user && null != user.getIs_tracking() && user.getIs_tracking().equals("1")) {
                trackingDays = user.getTracking_days();
                if (null != trackingDays) {
                    trackingDaysList = Arrays.asList(trackingDays.split(";"));
                } else {
                    return false;
                }

                hourFromWebStart = user.getStart_time();
                calStart = Calendar.getInstance();
                if (null != hourFromWebStart) {
                    String[] hourSplitStart = hourFromWebStart.split(":");
                    int hourStart = Integer.parseInt(hourSplitStart[0]);
                    int minuteStart = Integer.parseInt(hourSplitStart[1]);

                    calStart.set(Calendar.HOUR_OF_DAY, hourStart);
                    calStart.set(Calendar.MINUTE, minuteStart);
                } else {
                    return false;
                }

                hourFromWeb = user.getEnd_time();
                cal = Calendar.getInstance();
                if (null != hourFromWeb) {
                    String[] hourSplit = hourFromWeb.split(":");
                    int hour = Integer.parseInt(hourSplit[0]);
                    int minute = Integer.parseInt(hourSplit[1]);

                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                } else {
                    return false;
                }
                if (trackingDaysList.contains(thisDay)) {
                    if (Calendar.getInstance().after(calStart) && Calendar.getInstance().before(cal)) {
                        boolean b = Global.LTM.getIsConnected();
                        if (!b) {
                            Global.LTM.connectLocationClient();
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }


        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("LocationListenerImpl", e.getMessage());
            ACRA.getErrorReporter().putCustomData("LocationListenerImpl", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat checkFlagTacking"));

        }
        return false;
    }
}
