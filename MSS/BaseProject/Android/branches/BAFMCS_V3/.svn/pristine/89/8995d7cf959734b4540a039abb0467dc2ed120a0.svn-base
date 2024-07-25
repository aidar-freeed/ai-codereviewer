package com.adins.mss.base.checkin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.checkin.activity.CheckInView;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LocationInfoDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CheckInManager implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static String[] address;
    static LocationTrackingManager ltm = Global.LTM;
    static LocationInfo infoCheckIn;
    private static GoogleApiClient mGoogleApiClient;
    private static CheckInLocationListener listener;

    boolean updateLocation = true;
    private Context context;
    private LocationRequest mLocationRequest;

    public CheckInManager(Context context) {
        this.context = context;
        listener = new CheckInLocationListener(context);
    }

    public static void startGPSTracking(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        GeneralParameter gp_distance = GeneralParameterDataAccess.getOne(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_DISTANCE_TRACKING);
        try {
            if (gp_distance != null) {
                int distanceTracking = Integer.parseInt(gp_distance.getGs_value());
                if (distanceTracking != 0) {
                    Global.LTM = new LocationTrackingManager(tm, lm, context);
                    Global.LTM.setMinimalDistanceChangeLocation(Integer.parseInt(GeneralParameterDataAccess.getOne(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_DISTANCE_TRACKING).getGs_value()));
                    Global.LTM.setMinimalTimeChangeLocation(15);
                    Global.LTM.applyLocationListener(context);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            Global.LTM= new LocationTrackingManager(tm, lm, context);
            Global.LTM.setMinimalDistanceChangeLocation(50);
            Global.LTM.setMinimalTimeChangeLocation(5);
            Global.LTM.applyLocationListener(context);
        }
    }

    /**
     * Method for get Current Location info Of Check In
     *
     * @return Location Info
     */
    public static LocationInfo getCurrentCheckIn() {
        return infoCheckIn;
    }

    /**
     * Gets List of Locations info CheckIn from Database
     *
     * @param context
     * @param Uuid_user
     * @return all Location info in type of Check In
     */

    public static List<LocationInfo> getAllLocationInfoCheckInFromDB(
            Context context, String Uuid_user) {
        return LocationInfoDataAccess.getAllbyType(context, Uuid_user,
                Global.LOCATION_TYPE_CHECKIN);
    }

    /**
     * Insert a LocationInfo CheckIn to Data Base
     *
     * @param locationInfo
     */
    public static void insertLocationCheckInToDB(Context context,
                                                 LocationInfo locationInfo) {
        try {
            LocationInfoDataAccess.add(context, locationInfo);
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    /**
     * In response to a request to stop updates, send a request to Location
     * Services
     */
    public static void stopPeriodicUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, listener);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Method to Update Location (Refresh Location)
     */
    public void updateLocationCheckin() {
        mLocationRequest = LocationRequest.create();

        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        buildGoogleApiClient();
        updateLocation = true;
        mGoogleApiClient.connect();
    }

    /**
     * Method to get new LocationInfo. Use in Button Refresh or etc.
     *
     * @return Location Info
     */
    public LocationInfo getNewLocation() {
        infoCheckIn = listener.getNewLocationInfo();
        return infoCheckIn;
    }

    /**
     * Method for Get Location Info Of CheckIn
     *
     * @return Location Info
     */
    public LocationInfo getLocationInfoCheckIn() {
        Global.FLAG_LOCATION_TYPE = 1; // checkIn
        if (ltm == null) ltm = Global.LTM;
        if (Global.LTM == null) {
            try {
                startGPSTracking(context);
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
        try {
            if (ltm == null) ltm = Global.LTM;
            infoCheckIn = ltm.getCurrentLocation(Global.FLAG_LOCATION_CHECKIN);
            if (infoCheckIn == null) infoCheckIn = new LocationInfo(Tool.getUUID());
        } catch (Exception e) {
            FireCrash.log(e);
        }

        return infoCheckIn;
    }

    /**
     * Method for Get Latitude and longitude from LocationInfo
     *
     * @param locationInfo
     * @return LatLng
     */
    public LatLng getLatLng(LocationInfo locationInfo) {
        double mLatitude = Double.parseDouble(locationInfo.getLatitude());
        double mLongitude = Double.parseDouble(locationInfo.getLongitude());
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        return latLng;
    }

    /**
     * Method For Get Address Location from Geocoder
     *
     * @param context      - context of activity
     * @param locationInfo - location info
     * @return String[] - length = 2 <br/>
     * String[0] is Local of Location, ex: Kebon Jeruk <br/>
     * String[1] is Address Line of Location, ex: Jalan Kebon Jeruk Raya
     * No. 80
     */
    public void getAddressLocation(Context context, LocationInfo locationInfo) {
        new CheckInManager.GetAddressTask().execute();
    }

    public String[] getAddress() {
        return address;
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        //EMPTY
    }

    @Override
    public void onConnected(Bundle arg0) {
        startPeriodicUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //EMPTY
    }

    /**
     * In response to a request to start updates, send a request to Location
     * Services
     */
    private void startPeriodicUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public class GetAddressTask extends AsyncTask<Void, Void, String[]> {

        /**
         * Get a Geocoder instance, get the latitude and longitude look up the
         * address, and return it
         *
         * @return A string containing the address of the current location, or
         * an empty string if no address can be found, or an error
         * message
         * @params params One or more Location objects
         */
        @Override
        protected String[] doInBackground(Void... params) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            // Get the current location from the input parameter list
            LocationInfo loc = infoCheckIn;
            double mLatitude = 0;
            double mLongitude = 0;
            try {
                mLatitude = Double.parseDouble(loc.getLatitude());
                mLongitude = Double.parseDouble(loc.getLongitude());
            } catch (Exception e) {
                FireCrash.log(e);
            }

            String[] addressString = new String[2];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                /*
				 * Return 1 address.
				 */
                addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
            } catch (IOException e1) {
                if (Global.IS_DEV) {
                    Logger.e("LocationSampleActivity",
                            "IO Exception in getFromLocation()");
                    e1.printStackTrace();
                }
                addressString[0] = context.getString(R.string.address_not_found);
                addressString[1] = context.getString(R.string.address_not_found);
                return addressString;
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                addressString[0] = context.getString(R.string.address_not_found);
                addressString[1] = context.getString(R.string.address_not_found);
                if (Global.IS_DEV) {
                    Logger.e("CheckIn Manager :", addressString[1]);
                    e2.printStackTrace();
                }
                return addressString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && !addresses.isEmpty()) {
                // Get the first address
                Address addressLocal = addresses.get(0);
                addressString[0] = addressLocal.getLocality();
                try {
                    addressString[1] = addressLocal.getAddressLine(0);
                    if (addressString[1] != null && addressString[1].contains("Timed out"))
                        addressString[1] = context.getString(R.string.address_not_found);
                } catch (Exception e) {
                    FireCrash.log(e);
                    addressString[1] = context.getString(R.string.address_not_found);
                }
                // Return the text
                return addressString;
            } else {
                addressString[0] = context.getString(R.string.address_not_found);
                addressString[1] = context.getString(R.string.lblCoord) + " " + mLatitude + ", " + mLongitude;
                return addressString;
            }
        }

        @Override
        protected void onPostExecute(String[] addressResult) {
            // Set activity indicator visibility to "gone"
            // Display the results of the lookup.
            CheckInView.setLocation(addressResult);
            address = addressResult;
        }
    }


}
