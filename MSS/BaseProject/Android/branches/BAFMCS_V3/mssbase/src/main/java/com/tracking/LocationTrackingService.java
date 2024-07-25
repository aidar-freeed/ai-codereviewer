package com.tracking;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.networkmonitor.NetworkMonitorDataUsage;
import com.adins.mss.base.tracking.LocationTrackingRequestJson;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LocationInfoDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssResponseType;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.logger.Logger;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class LocationTrackingService extends Service implements Runnable, LocationListener {
    private static String TAG = "LocationTrackingService";
    private static final int LIMITED_TRACKING_FOR_SENT = 100;
    private static Context context;
    private static boolean keepRunning;
    private static boolean keepRunningBefore;
    Thread thr;
    NetworkMonitorDataUsage monitorDU = new NetworkMonitorDataUsage();

    //Nendi: 2019.07.01
    public LocationTrackingManager manager;
    protected LocationManager mLocation;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context,
                "GlobalData", Context.MODE_PRIVATE);
        if (!sharedPref.contains("HAS_LOGGED")) {
            stopSelf();
            return START_NOT_STICKY;
        }

        StartLocationTracking();
        return START_STICKY;
    }

    public static boolean isKeepRunning() {
        return keepRunning;
    }

    public static boolean isKeepRunningBefore() {
        return keepRunningBefore;
    }

    public static void setKeepRunningBefore(boolean before) {
        keepRunningBefore = before;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLocation != null) {
            mLocation.removeUpdates(this);
            manager.removeLocationListener();
            manager   = null;
            mLocation = null;
        }

        Intent broadcast = new Intent(getString(R.string.intent_action_restart_location_tracking));
        sendBroadcast(broadcast);

        stopSelf();
        stopAutoSendLocationTrackingThread();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thr = new Thread(null, this, "AutoSendLocationHistory");
        context = this;
        startAutoSendLocationTrackingThread();
    }

    private void startAutoSendLocationTrackingThread() {
        thr.start();
    }

    private void stopAutoSendLocationTrackingThread() {
        if (thr != null) {
            thr.interrupt();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public int getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return (level * 100) / scale;
    }

    @Override
    public void run() {
        String uuid_user = null;
        try {
            uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            if (uuid_user == null)
                uuid_user = Global.user.getUuid_user();
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.user != null)
                uuid_user = Global.user.getUuid_user();
        }
        boolean enableTracking = false;
        try {
            if (uuid_user != null) {
                GeneralParameter gp = GeneralParameterDataAccess.getOne(context, uuid_user, "PRM01_TRCK");
                if (gp != null && gp.getGs_value().equals("1")) {
                    enableTracking = true;
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }

        keepRunning = true;
        keepRunningBefore = true;

        int interval = 0;
        try {
            if (uuid_user != null && context != null)
                interval = Integer.parseInt(GeneralParameterDataAccess.getOne(context, uuid_user, "PRM02_TRIN").getGs_value());
            interval *= Global.SECOND;
        } catch (Exception e) {
            FireCrash.log(e);
            keepRunning = false;
        }

        MssResponseType mrt = new MssResponseType();
        String sentStatus = "0";

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            FireCrash.log(e);
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        synchronized (this) {
            while (keepRunning) {
                try {
                    try {
                        String uuidUser = GlobalData.getSharedGlobalData().getUser()
                                .getUuid_user();
                        if (uuidUser != null) {
                            GeneralParameter gp = GeneralParameterDataAccess.getOne(context, uuidUser, Global.GS_INTERVAL_TRACKING);
                            if (gp != null && gp.getGs_value() != null
                                    && !gp.getGs_value()
                                    .isEmpty()) {
                                interval = Integer.parseInt(gp.getGs_value()) * Global.SECOND;
                            }
                            GeneralParameter gp2 = GeneralParameterDataAccess.getOne(context, uuidUser, Global.GS_TRACKING);
                            if (gp2 != null) {
                                enableTracking = gp2.getGs_value().equals("1");
                            }
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                    if (enableTracking) {
                        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                        if (!Global.APPLICATION_ORDER.equals(application)) {
                            if (checkFlagTracking()) {

                                if (Tool.isInternetconnected(context)) {
                                    LocationTrackingRequestJson ltj = new LocationTrackingRequestJson();
                                    ltj.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                                    ltj.addImeiAndroidIdToUnstructured();
                                    List<LocationInfo> listLocationInfo = new ArrayList<>();
                                    try {
                                        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                                        listLocationInfo = LocationInfoDataAccess.getAllbyTypewithlimited(context, uuidUser, Global.LOCATION_TYPE_TRACKING, LIMITED_TRACKING_FOR_SENT);
                                        if (!listLocationInfo.isEmpty()) {
                                            for (LocationInfo info : listLocationInfo) {
                                                if (info.getUuid_user() != null) {
                                                    info.setUuid_user(uuidUser);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                    }
                                    //proses data usage
                                    ltj.setDataUsage(String.valueOf(monitorDU.getDataThisDay(context) / 1024)); //dirubah dari byte ke kilobyte
                                    //end proses data usage

                                    ltj.addLocationInfoList(listLocationInfo);
                                    ltj.setPrecentageBattery(String.valueOf(getBatteryLevel()));
                                    //retrieve from db
                                    if (!ltj.getLocationInfoList().isEmpty()) {
                                        try {
                                            String json = GsonHelper.toJson(ltj);
                                            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                                            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                                            HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
                                            HttpConnectionResult serverResult = null;

                                            //Firebase Performance Trace HTTP Request
                                            HttpMetric networkMetric =
                                                    FirebasePerformance.getInstance().newHttpMetric(GlobalData.getSharedGlobalData().getURL_SUBMIT_TRACK(), FirebasePerformance.HttpMethod.POST);
                                            Utility.metricStart(networkMetric, json);

                                            try {
                                                serverResult = httpConn.requestToServer(GlobalData.getSharedGlobalData().getURL_SUBMIT_TRACK(), json, Global.DEFAULTCONNECTIONTIMEOUT);
                                                Utility.metricStop(networkMetric, serverResult);
                                            } catch (Exception e) {
                                                FireCrash.log(e);

                                            }
                                            if (serverResult != null && serverResult.isOK()) {
                                                String resultvalue = serverResult.getResult();
                                                try {
                                                    mrt = GsonHelper.fromJson(resultvalue, MssResponseType.class);
                                                    if (mrt.getStatus().getCode() == 0) {
                                                        if (Global.IS_DEV)
                                                            Log.i(TAG, "_____________________Submit Tracking IS SUCCESSFULL");

                                                        LocationInfoDataAccess.deleteList(context, listLocationInfo);
                                                        List<LocationInfo> lastlocation = LocationInfoDataAccess.getAllbyType(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.LOCATION_TYPE_TRACKING);
                                                        if (lastlocation != null && !lastlocation.isEmpty()) {

                                                        } else {
                                                            try {
                                                                wait(interval);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                                Thread.currentThread().interrupt();
                                                            }
                                                        }
                                                    } else {
                                                        if (Global.IS_DEV) Log.i(TAG, mrt.toString());
                                                        try {
                                                            wait(interval);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                            Thread.currentThread().interrupt();
                                                        }
                                                    }
                                                } catch (IllegalStateException ex) {
                                                    try {
                                                        wait(interval);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                        Thread.currentThread().interrupt();
                                                    }
                                                }

                                            } else {
                                                try {
                                                    try {
                                                        wait(interval);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                        Thread.currentThread().interrupt();
                                                    }
                                                } catch (Exception e) {
                                                    FireCrash.log(e);
                                                }
                                            }
                                        } catch (OutOfMemoryError e) {
                                            FireCrash.log(e);
                                            e.printStackTrace();
                                            ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context,
                                                    "GlobalData", Context.MODE_PRIVATE);
                                            String uuidUser = sharedPref.getString("UUID_USER", "");
                                            LocationInfoDataAccess.deleteAllbyType(context, uuidUser, Global.LOCATION_TYPE_TRACKING);
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                            e.printStackTrace();
                                            try {
                                                wait(interval);
                                            } catch (InterruptedException e2) {
                                                e2.printStackTrace();
                                                Thread.currentThread().interrupt();
                                            }
                                        }
                                    } else {
                                        try {
                                            wait(interval);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                } else {
                                    try {
                                        wait(interval);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        Thread.currentThread().interrupt();
                                    }
                                }
                            } else {
                                // end of thread
                                try {
                                    wait(interval);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    FireCrash.log(ex);
                    Logger.e("AUTOSENDLOCTRACKING", Log.getStackTraceString(ex));
                    ex.printStackTrace();
                    keepRunning = false;
                    ACRA.getErrorReporter().handleSilentException(ex);
                }
            }
        }
    }

    private boolean checkFlagTracking() {
        try {
            User user = GlobalData.getSharedGlobalData().getUser();
            String hourFromWebStart;
            String hourFromWeb;
            Calendar cal;
            Calendar calStart;
            String trackingDays;
            List trackingDaysList;
            int thisDayInt = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            String thisDay;
            thisDayInt -= 1;
            thisDay = String.valueOf(thisDayInt);


            if (user != null) {
                if (null != user.getIs_tracking() && "1".equals(user.getIs_tracking())) {
                    trackingDays = user.getTracking_days();
                    if (null != trackingDays && !"".equalsIgnoreCase(trackingDays)) {
                        trackingDaysList = Arrays.asList(trackingDays.split(";"));
                    } else {
                        return false;
                    }

                    hourFromWebStart = user.getStart_time();
                    calStart = Calendar.getInstance();
                    if (null != hourFromWebStart && !"".equalsIgnoreCase(hourFromWebStart)) {
                        String hourSplitStart[] = hourFromWebStart.split(":");
                        int hourStart = Integer.parseInt(hourSplitStart[0]);
                        int minuteStart = Integer.parseInt(hourSplitStart[1]);

                        calStart.set(Calendar.HOUR_OF_DAY, hourStart);
                        calStart.set(Calendar.MINUTE, minuteStart);
                    } else {
                        return false;
                    }

                    hourFromWeb = user.getEnd_time();
                    cal = Calendar.getInstance();
                    if (null != hourFromWeb && !"".equalsIgnoreCase(hourFromWeb)) {
                        String hourSplit[] = hourFromWeb.split(":");
                        int hour = Integer.parseInt(hourSplit[0]);
                        int minute = Integer.parseInt(hourSplit[1]);

                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                    } else {
                        return false;
                    }
                    if (trackingDaysList.contains(thisDay)) {
                        if (Calendar.getInstance().after(calStart) && Calendar.getInstance().before(cal)) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    Log.d("Location Tracking", "Stop Location Service");
                    return false;
                }
            }

        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("LocationListenerImpl", e.getMessage());
            ACRA.getErrorReporter().putCustomData("LocationListenerImpl", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat checkFlagTacking"));
        }
        return false;
    }

    public void StartLocationTracking() {
        try {
            mLocation = (LocationManager) getSystemService(LOCATION_SERVICE);
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            GeneralParameter gp_distance = GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_DISTANCE_TRACKING);
            try {
                if (gp_distance != null) {
                    int distanceTracking = Integer.parseInt(gp_distance.getGs_value());
                    if (distanceTracking != 0) {
                        manager = new LocationTrackingManager(tm, mLocation, this);
                        manager.setMinimalDistanceChangeLocation(Integer.parseInt(GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), "PRM13_DIST").getGs_value()));
                        manager.setMinimalTimeChangeLocation(5);
                        manager.applyLocationListener(context);
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                manager = new LocationTrackingManager(tm, mLocation, this);
                manager.setMinimalDistanceChangeLocation(50);
                manager.setMinimalTimeChangeLocation(5);
                manager.applyLocationListener(context);
            }

            if (Global.LTM == null) {
                Global.LTM = manager;
            } else {
                try {
                    Global.LTM = null;
                    Global.LTM = manager;
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    public void bindLocationListener() {
        mLocation = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                } else {
                    if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                        mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long)(5 * 1000), 10f, this);
                }
            } else {
                if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                    mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long)(5 * 1000), 10f, this);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //EMPTY
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //EMPTY
    }

    @Override
    public void onProviderEnabled(String provider) {
        //EMPTY
    }

    @Override
    public void onProviderDisabled(String provider) {
        //EMPTY
    }

}
