package com.adins.mss.base.checkout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

import com.adins.mss.base.R;
import com.adins.mss.base.checkout.activity.CheckOutActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.LocationInfoDataAccess;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CheckOutManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    static LocationTrackingManager ltm = Global.LTM;
    static LocationInfo infoCheckOut;
    private static GoogleApiClient mGoogleApiClient;
    private static CheckOutLocationListener listener = new CheckOutLocationListener();
    List<LocationInfo> LocationInfoCheckOut;
    boolean updateLocation = true;
    private Context context;
    private LocationRequest mLocationRequest;

    public CheckOutManager(Context context) {
        this.context = context;

    }

    /**
     * Gets List of Locations info CheckOut from Database
     *
     * @param context
     * @param Uuid_user
     * @return all Location info in type of Check In
     */

    public static List<LocationInfo> getAllLocationInfoCheckOutFromDB(Context context, String Uuid_user) {
        return LocationInfoDataAccess.getAllbyType(context, Uuid_user, Global.LOCATION_TYPE_CHECKOUT);
    }

    /**
     * Insert a LocationInfo CheckOut to Data Base
     *
     * @param LocationInfo
     */
    public static void insertLocationCheckOutToDB(Context context, LocationInfo LocationInfo) {
        LocationInfoDataAccess.add(context, LocationInfo);
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
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
     * Method for get Current Location info Of Check In
     *
     * @return Location Info
     */
    public static LocationInfo getCurrentCheckOut() {
        return infoCheckOut;
    }

    public static String toLocationCheckOutString(LocationInfo locationInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(locationInfo.getLatitude()).append(",")
                .append(locationInfo.getLongitude()).append(",")
                .append(locationInfo.getCid()).append(",")
                .append(locationInfo.getMcc()).append(",")
                .append(locationInfo.getMnc()).append(",")
                .append(locationInfo.getLac());
        return sb.toString();
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
     * Method to Update Location
     */
    public void updateLocationCheckOut() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        buildGoogleApiClient();
        updateLocation = true;
        updateLocation = false;
        mGoogleApiClient.connect();

    }

    /**
     * Method to get new LocationInfo. Use in Button Refresh or etc.
     *
     * @return Location Info
     */
    public LocationInfo getNewLocation() {
        infoCheckOut = listener.getNewLocationInfo();
        return infoCheckOut;
    }

    /**
     * Method for Get Location Info Of CheckOut
     *
     * @return
     */
    public LocationInfo getLocationInfoCheckOut() {
        Global.FLAG_LOCATION_TYPE = 2; //CheckOut
        infoCheckOut = ltm.getCurrentLocation(Global.FLAG_LOCATION_CHECKOUT);
        return infoCheckOut;
    }

    /**
     * Method for Get Latitude and longitude from LocationInfo
     *
     * @param LocationInfo
     * @return LatLng
     */
    public LatLng getLatLng(LocationInfo LocationInfo) {
        double mLatitude = Double.parseDouble(LocationInfo.getLatitude());
        double mLongitude = Double.parseDouble(LocationInfo.getLongitude());
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        return latLng;
    }

    /**
     * Method For Get Address Location from Geocoder
     *
     * @param context      - context of activity
     * @param LocationInfo - location info
     * @return String[] - length = 2
     * <br/>String[0] is Local of Location, ex: Kebon Jeruk
     * <br/>String[1] is Address Line of Location, ex: Jalan Kebon Jeruk Raya No. 80
     */
    public void getAddressLocation(Context context, LocationInfo LocationInfo) {
        new GetAddressTask().execute();
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
     * In response to a request to start updates, send a request
     * to Location Services
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
        public GetAddressTask() {
            //EMPTY
        }

        /**
         * Get a Geocoder instance, get the latitude and longitude
         * look up the address, and return it
         *
         * @return A string containing the address of the current
         * location, or an empty string if no address can be found,
         * or an error message
         * @params params One or more Location objects
         */
        @Override
        protected String[] doInBackground(Void... params) {
            Geocoder geocoder =
                    new Geocoder(context, Locale.getDefault());
            // Get the current location from the input parameter list
            LocationInfo loc = infoCheckOut;
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
                addressString[0] = "No address found";
                addressString[1] = "IO Exception trying to get address";
                return addressString;
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                addressString[0] = "No address found";
                addressString[1] = "Illegal arguments " +
                        Double.toString(mLatitude) +
                        " , " +
                        Double.toString(mLongitude) +
                        " passed to address service";
                if (Global.IS_DEV) {
                    Logger.e("CheckOut Manager :", addressString[1]);
                    e2.printStackTrace();
                }
                return addressString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && !addresses.isEmpty()) {
                // Get the first address
                Address address = addresses.get(0);
                addressString[0] = address.getLocality();
                addressString[1] = address.getAddressLine(0);
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
            CheckOutActivity.setLocation(addressResult);
        }
    }
}
