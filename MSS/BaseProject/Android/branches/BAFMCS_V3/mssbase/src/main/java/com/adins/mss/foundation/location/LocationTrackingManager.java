package com.adins.mss.foundation.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.core.content.ContextCompat;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.UpdateMenuGPS;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.db.dataaccess.LocationInfoDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * @author gigin.ginanjar
 */
public class LocationTrackingManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int DEFAULT_MIN_TIME_CHANGE_LOCATION = 5 * 1000; // milliseconds
    public static final int DEFAULT_MIN_DISTANCE_CHANGE_LOCATION = 0; // meters
    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int FAST_INTERVAL_CEILING_IN_MILLISECONDS = 1000;
    private static Context context;
    private static int status = 0; //0=can't get location; 1= get location not accurate; 2 = get location accurate
    private static LocationManager locationManager;
    private static LocationInfo locationInfo = new LocationInfo();
    private static LocationListenerImpl locationListener;
    private static GoogleApiClient mGoogleApiClient;
    private boolean isConnected = false;
    private TelephonyManager telephonyManager;
    private int minTime = -1;

    //bong 31 mar 15 - add gpsListener
    private int minDistance = -1;
    // A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object

    /**
     * Inisialize LocationTrackingManager with Telephony Manager and Location Manager as parameter
     *
     * @param tm -
     *           Telephony Manager
     * @param lm -
     *           Location Manager
     */
    public LocationTrackingManager(TelephonyManager tm, LocationManager lm, Context context) {
        setContextLocation(context);
        if (tm == null)
            throw new NullPointerException("Could not get telephony service!");
        if (lm == null)
            throw new NullPointerException("Could not get location service!");

        telephonyManager = tm;
        setLocationmanager(lm);
        setLocationListener(new LocationListenerImpl(context));
        //23 Mei 17 add Sync GoogleAPIClient
        buildGoogleApiClient();
        //bong 31 mar 15 add gpsStatusListener
    }

    public static Boolean isGpsEnable() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    /**
     * Gets List of Locations Tracking from Database
     *
     * @param context
     * @return all Location
     */
    public static List<LocationInfo> getAllLocationInfoTrackingFromDB(Context context) {
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return LocationInfoDataAccess.getAllbyType(context, uuidUser, Global.LOCATION_TYPE_TRACKING);
    }

    /**
     * flag for get Location status that use on GPS Indicator
     *
     * @return <b>0</b> if Location un-Available<br/>
     * <b>1</b> if Location Available but not accurate<br/>
     * <b>2</b> if Location Available with good accuracy
     */
    public static int getLocationStatus() {
        return status;
    }

    /**
     * Set Location Status Availability
     *
     * @param statusLocation <br/><br/><b>0</b> if Location un-Available<br/>
     *                       <b>1</b> if Location Available but not accurate<br/>
     *                       <b>2</b> if Location Available with good accuracy
     */
    public static void setLocationStatus(int statusLocation) {
        if (statusLocation == 2 || statusLocation == 1) {
            Global.isGPS = true;
        } else if (statusLocation == 0) {
            Global.isGPS = false;
        }

        status = statusLocation;
    }

    public static LatLng getLatLng(LocationInfo locationInfo) {
        double mLatitude = Double.parseDouble(locationInfo.getLatitude());
        double mLongitude = Double.parseDouble(locationInfo.getLongitude());

        return new LatLng(mLatitude, mLongitude);
    }

    /**
     * Insert a LocationInfo to Data Base
     *
     * @param locationInfo
     */
    public static void insertLocationInfoToDB(LocationInfo locationInfo) {
        LocationInfoDataAccess.add(getContextLocation(), locationInfo);
    }

    /**
     * Insert a LocationInfo to Data Base
     *
     * @param locationInfo
     */
    public static void insertLocationInfoListToDB(List<LocationInfo> locationInfo) {
        LocationInfoDataAccess.add(getContextLocation(), locationInfo);
    }

    public static String toAnswerString(LocationInfo locationInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(locationInfo.getLatitude()).append(",")
                .append(locationInfo.getLongitude()).append(",")
                .append(locationInfo.getCid()).append(",")
                .append(locationInfo.getMcc()).append(",")
                .append(locationInfo.getMnc()).append(",")
                .append(locationInfo.getLac()).append(",")
                .append(locationInfo.getAccuracy()).append(",")
                .append(locationInfo.getGps_time());
        return sb.toString();
    }

    public static String toAnswerStringShort(LocationInfo locationInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(getContextLocation().getString(R.string.lblCoord))
                .append(" ")
                .append(locationInfo.getLatitude()).append(", ")
                .append(locationInfo.getLongitude()).append("\n")
                .append(getContextLocation().getString(R.string.lblAcc))
                .append(" ")
                .append(locationInfo.getAccuracy()).append(" m")
        ;
        return sb.toString();
    }

    public static int ageMinutes(Location last) {
        return (int) (ageMs(last) / (60 * 1000));
    }

    private static long ageMs(Location last) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return ageMsApi17(last);
        return ageMsApiPre17(last);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static long ageMsApi17(Location last) {
        return (SystemClock.elapsedRealtimeNanos() - last
                .getElapsedRealtimeNanos()) / 1000000;
    }

    private static long ageMsApiPre17(Location last) {
        return System.currentTimeMillis() - last.getTime();
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContextLocation())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Begin Location Listener and Request Location Update
     *
     * @param context - Context
     *                context of the activity
     */
    public void applyLocationListener(Context context) {
        setContextLocation(context);
        if (this.minTime == -1)
            this.minTime = DEFAULT_MIN_TIME_CHANGE_LOCATION;
        if (this.minDistance == -1)
            this.minDistance = DEFAULT_MIN_DISTANCE_CHANGE_LOCATION;
        // Create a new global location parameters object

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(minTime);
        mLocationRequest.setFastestInterval(minTime);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(this.minDistance);//if u take this to 300 m so u will get location if u walk by 300 m or more
        // devide by 10 to make displacement right and get the right time to update
        mLocationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        connectLocationClient();
    }

    public void connectLocationClient() {
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting())
            mGoogleApiClient.connect();
    }

    public boolean hasConnected() {
        return mGoogleApiClient.isConnected();
    }

    /**
     * Stop Periodic Update of Location Tracking
     */
    public void removeLocationListener() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    /**
     * Gets Local Area Code
     *
     * @return Local Area Code
     */
    public int getLac() {
        @SuppressLint("MissingPermission")
        GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
        if (location == null)
            return 0;
        else
            return location.getLac();
    }

    /**
     * Gets Cell ID
     *
     * @return Cell ID from the Handset
     */
    public int getCid() {
        @SuppressLint("MissingPermission")
        GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
        if (location == null)
            return 0;
        else
            return location.getCid();
    }

    /**
     * Gets Mobile Country Code
     *
     * @return Mobile Country Code
     */
    public int getMcc() { //Mobile Country Code
        String networkOperator = this.telephonyManager.getNetworkOperator();
        if (networkOperator == null || "".equals(networkOperator)) {
            return 0;
        } else {
            int val = 0;
            try {
                val = Integer.parseInt(networkOperator.substring(0, 3));
            } catch (NumberFormatException nfe) {
                val = 0;
            }
            return val;
        }
    }

    /**
     * Gets Mobile Network Code
     *
     * @return Mobile Network Code
     */
    public int getMnc() { //Mobile Network Code
        String networkOperator = this.telephonyManager.getNetworkOperator();
        if (networkOperator == null || "".equals(networkOperator)) {
            return 0;
        } else {
            int val = 0;
            try {
                val = Integer.parseInt(networkOperator.substring(3));
            } catch (NumberFormatException nfe) {
                val = 0;
            }
            return val;
        }
    }

    /**
     * <p>Set the desired interval for active location updates, in seconds.<br/>
     * Set to 5 seconds for mapping applications that are showing your location in real-time<br/>
     * By default this is 5 second</p>
     *
     * @param minTime Minimum time in second
     */
    public void setMinimalTimeChangeLocation(int minTime) {
        this.minTime = minTime * MILLISECONDS_PER_SECOND; // miliseconds
    }

    /**
     * <p>Set the minimum displacement between location updates in meters.<br/>
     * By default this is 0.</p>
     *
     * @param minDistance
     */
    public void setMinimalDistanceChangeLocation(int minDistance) {
        this.minDistance = minDistance;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        setIsConnected(false);
    }

    @Override
    public void onConnected(Bundle arg0) {
        startPeriodicUpdates();
        setIsConnected(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        setIsConnected(false);
    }

    private void startPeriodicUpdates() {
        try {
            if (ContextCompat.checkSelfPermission(getContextLocation(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContextLocation(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    /**
     * Gets Current Location
     *
     * @return LocationTracking
     */
    public LocationInfo getCurrentLocation(int flagLocationType) {
        boolean isHasGps = Tool.gpsEnabled(locationManager);
        if (isHasGps) {
            Location gps = null;
            Location locationSaved = null;
            try {
                if (ContextCompat.checkSelfPermission(getContextLocation(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContextLocation(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                gps = locationListener.getCurrentLocation();
                locationSaved = locationListener.getCurrentLocation();
            } catch (Exception e) {
                FireCrash.log(e);
            }

            int greenAccuracy = GlobalData.getSharedGlobalData().getGreenAccuracy();
            if (greenAccuracy == 0)
                greenAccuracy = 50;
            int yellowAccuracy = GlobalData.getSharedGlobalData().getYellowAccuracy();
            if (yellowAccuracy == 0)
                yellowAccuracy = 200;

            if (gps != null) {
                setLocationStatus(2);
                if (gps.getAccuracy() < greenAccuracy) {
                    locationInfo.setLatitude(String.valueOf(gps.getLatitude()));
                    locationInfo.setLongitude(String.valueOf(gps.getLongitude()));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(gps.getTime());
                    locationInfo.setGps_time(calendar.getTime());
                    locationInfo.setIs_gps_time(Global.TRUE_STRING);
                    locationInfo.setAccuracy(Math.round(gps.getAccuracy()));
                }
                //if Last Location isn't Accurate then get Location from Last Location which good accuracy
                else {
                    if (locationSaved != null) {
                        if (locationSaved.getAccuracy() <= greenAccuracy) {
                            LocationTrackingManager.setLocationStatus(2);
                        } else if (locationSaved.getAccuracy() > greenAccuracy && locationSaved.getAccuracy() <= yellowAccuracy) {
                            LocationTrackingManager.setLocationStatus(1);
                        } else {
                            LocationTrackingManager.setLocationStatus(0);
                        }
                        locationInfo.setLatitude(String.valueOf(locationSaved.getLatitude()));
                        locationInfo.setLongitude(String.valueOf(locationSaved.getLongitude()));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(locationSaved.getTime());
                        locationInfo.setGps_time(calendar.getTime());
                        locationInfo.setIs_gps_time(Global.TRUE_STRING);
                        locationInfo.setAccuracy(Math.round(locationSaved.getAccuracy()));
                    } else {
                        setLocationStatus(0);
                        locationInfo.setLatitude(String.valueOf(0d));
                        locationInfo.setLongitude(String.valueOf(0d));
                        locationInfo.setGps_time(null);
                        locationInfo.setIs_gps_time(Global.FALSE_STRING);
                        locationInfo.setAccuracy(0);
                    }
                }
            } else {
                setLocationStatus(0); //
                locationInfo.setGps_time(null);
                locationInfo.setIs_gps_time(Global.FALSE_STRING);
                locationInfo.setAccuracy(0);
                locationInfo.setLatitude(String.valueOf(0d));
                locationInfo.setLongitude(String.valueOf(0d));
            }
        } else {
            setLocationStatus(0);
            locationInfo.setGps_time(null);
            locationInfo.setIs_gps_time(Global.FALSE_STRING);
            locationInfo.setAccuracy(0);
            locationInfo.setLatitude(String.valueOf(0d));
            locationInfo.setLongitude(String.valueOf(0d));
        }

        String networkOperator = telephonyManager.getNetworkOperator();

        if (networkOperator == null || "".equals(networkOperator)) {
            locationInfo.setMcc("0");
            locationInfo.setMnc("0");
        } else {
            String mcc = "0";
            String mnc = "0";
            try {
                if (networkOperator != null && networkOperator.length() > 0) {
                    mcc = networkOperator.substring(0, 3);
                    mnc = networkOperator.substring(3);
                }
                if (networkOperator.equalsIgnoreCase("null")) {
                    mcc = "0";
                    mnc = "0";
                }
                locationInfo.setMcc(mcc);
                locationInfo.setMnc(mnc);
            } catch (NumberFormatException nfe) {
                locationInfo.setMcc("0");
                locationInfo.setMnc("0");
            }
        }

        GsmCellLocation location = null;
        try {
            location = (GsmCellLocation) telephonyManager.getCellLocation();
        } catch (Exception e) {
            FireCrash.log(e);
            locationInfo.setCid("0");
            locationInfo.setLac("0");

        }

        if (location == null) {
            locationInfo.setCid("0");
            locationInfo.setLac("0");
        } else {
            locationInfo.setCid(String.valueOf(location.getCid()));
            locationInfo.setLac(String.valueOf(location.getLac()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
                String mCid = null;
                String mLac = null;
                String mMnc = null;
                String mMcc = null;
                if (cellInfoList != null) {
                    for (CellInfo cellInfo : cellInfoList) {
                        if (cellInfo.isRegistered()) {
                            if (cellInfo instanceof CellInfoGsm) {
                                CellInfoGsm infoGsm = (CellInfoGsm) cellInfo;
                                CellIdentityGsm gsmCellIdentity = infoGsm.getCellIdentity();
                                if (gsmCellIdentity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    mCid = String.valueOf(gsmCellIdentity.getCid());
                                    mLac = String.valueOf(gsmCellIdentity.getLac());
                                    mMnc = String.valueOf(gsmCellIdentity.getMnc());
                                    mMcc = String.valueOf(gsmCellIdentity.getMcc());
                                }
                            } else if (cellInfo instanceof CellInfoLte) {
                                CellInfoLte infoLte = (CellInfoLte) cellInfo;
                                CellIdentityLte lteCellIdentity = infoLte.getCellIdentity();
                                if (lteCellIdentity != null) {
                                    mMnc = String.valueOf(lteCellIdentity.getMnc());
                                    mMcc = String.valueOf(lteCellIdentity.getMcc());
                                    mLac = String.valueOf(lteCellIdentity.getTac());
                                    mCid = String.valueOf(lteCellIdentity.getCi());
                                }
                            } else if (cellInfo instanceof CellInfoWcdma) {
                                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                                CellIdentityWcdma identityWcdma = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    identityWcdma = cellInfoWcdma.getCellIdentity();
                                    if (identityWcdma != null) {
                                        mCid = String.valueOf(identityWcdma.getCid());
                                        mLac = String.valueOf(identityWcdma.getLac());
                                        mMnc = String.valueOf(identityWcdma.getMnc());
                                        mMcc = String.valueOf(identityWcdma.getMcc());
                                    }
                                }
                            }
                            if (mCid != null && !mCid.equals(String.valueOf(Integer.MAX_VALUE)))
                                locationInfo.setCid(mCid);
                            if (mLac != null && !mLac.equals(String.valueOf(Integer.MAX_VALUE)))
                                locationInfo.setLac(mLac);
                            if (mMnc != null && !mMnc.equals(String.valueOf(Integer.MAX_VALUE)))
                                locationInfo.setMnc(mMnc);
                            if (mMcc != null && !mMcc.equals(String.valueOf(Integer.MAX_VALUE)))
                                locationInfo.setMcc(mMcc);
                        }
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }
        }

        Date date = new Date(System.currentTimeMillis());
        locationInfo.setHandset_time(date);


        locationInfo.setUsr_crt(GlobalData.getSharedGlobalData().getUser().getUuid_user());
        locationInfo.setDtm_crt(date);
        locationInfo.setUser(GlobalData.getSharedGlobalData().getUser());

        switch (flagLocationType) {
            case Global.FLAG_LOCATION_TRACKING: // for tracking
                locationInfo.setLocation_type(Global.LOCATION_TYPE_TRACKING);
                break;
            case Global.FLAG_LOCATION_CHECKIN: // for check in
                locationInfo.setLocation_type(Global.LOCATION_TYPE_CHECKIN);
                break;
            case Global.FLAG_LOCATION_CHECKOUT: // for check out
                locationInfo.setLocation_type(Global.LOCATION_TYPE_CHECKOUT);
                break;
            case Global.FLAG_LOCATION_CAMERA: // for check out
                locationInfo.setLocation_type(Global.LOCATION_TYPE_CAMERA);
                break;

            default:
                break;
        }

        locationInfo.setUuid_location_info(Tool.getUUID());

        try {
            UpdateMenuGPS.SetMenuIcon();
        } catch (Exception e) {
            FireCrash.log(e);
        }

        return locationInfo;
    }

    public void setIsConnected(boolean isConnect){
        isConnected = isConnect;
    }

    public boolean getIsConnected(){
        return isConnected;
    }

    public static void setLocationmanager(LocationManager lm){
        locationManager = lm;
    }

    public LocationManager getLocationManager(){
        return locationManager;
    }

    private static void setLocationListener(LocationListenerImpl lli){
        locationListener = lli;
    }

    public LocationListenerImpl getLocationListener(){
        return locationListener;
    }

    private static void setContextLocation(Context contxt){
        context = contxt;
    }

    public static Context getContextLocation(){
        return context;
    }


}
