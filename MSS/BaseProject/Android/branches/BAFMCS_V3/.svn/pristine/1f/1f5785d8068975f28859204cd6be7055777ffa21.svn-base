package com.adins.mss.foundation.questiongenerator.form;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

public class LocationTagingView extends FragmentActivity implements OnClickListener, OnMapReadyCallback {

    public static LocationInfo locationInfo;
    static GoogleMap mGoogleMap;
    private static CheckInManager manager;
    private static LatLng latLng;
    int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private ImageButton btnRefresh;
    private Button btnOk;
    private FirebaseAnalytics screenName;

    public static void setNewLocation() {
        locationInfo = manager.getNewLocation();
        latLng = manager.getLatLng(locationInfo);
        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mGoogleMap.addMarker(markerOptions);
        int accuracy = locationInfo.getAccuracy();
        if (accuracy != 0) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(accuracy)
                    .fillColor(0x402196F3)
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(2);

            mGoogleMap.addCircle(circleOptions);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.location_tagging_layout);
        initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this,getString(R.string.screen_name_location_tagging),null);

        if (checkPlayServices()) {
            // Then we're good to go!
        }
    }

    private void initialize() {
        btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);

        btnOk = (Button) findViewById(R.id.btnOK);
        btnOk.setOnClickListener(this);

        manager = new CheckInManager(this);

        if (manager != null) {
            locationInfo = manager.getLocationInfoCheckIn();
            latLng = manager.getLatLng(locationInfo);
            if (latLng == null) {
                latLng = new LatLng(0, 0);
            }
            if (latLng.latitude == 0) {
                Toast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_SHORT).show();
            }
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTagging);

            // Setting Google Map
            mapFragment.getMapAsync(this);
        }
    }

    private void initializeMaps() {

        try {
            if (latLng.latitude != 0 && latLng.longitude != 0) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mGoogleMap.addMarker(markerOptions);
                int accuracy = locationInfo.getAccuracy();
                if (accuracy != 0) {
                    CircleOptions circleOptions = new CircleOptions()
                            .center(latLng)
                            .radius(accuracy)
                            .fillColor(0x402196F3)
                            .strokeColor(Color.TRANSPARENT)
                            .strokeWidth(2);
                    mGoogleMap.addCircle(circleOptions);
                }
                String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                if (Global.APPLICATION_ORDER.equals(application)) {
                    mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng location) {
                            mGoogleMap.clear();
                            latLng = location;
                            locationInfo = manager.getLocationInfoCheckIn();
                            locationInfo.setLatitude(String.valueOf(latLng.latitude));
                            locationInfo.setLongitude(String.valueOf(latLng.longitude));
                            locationInfo.setAccuracy(10);
                            String titleMarker = LocationTrackingManager.toAnswerStringShort(locationInfo);
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title("Selected Location");
                            markerOptions.snippet(titleMarker);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            mGoogleMap.addMarker(markerOptions);

                        }
                    });
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    private boolean checkPlayServices() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
                showErrorDialog(status);
            } else {
                Toast.makeText(this, "This device is not supported.",
                        Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    void showErrorDialog(int code) {
        GoogleApiAvailability.getInstance().getErrorDialog(this, code, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnRefresh) {
            Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.icon_rotate);
            btnRefresh.startAnimation(a);
            manager.updateLocationCheckin();
        } else if (id == R.id.btnOK) {
            if (latLng.latitude == 0 || latLng.longitude == 0) {
                Toast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        initializeMaps();
    }
}
