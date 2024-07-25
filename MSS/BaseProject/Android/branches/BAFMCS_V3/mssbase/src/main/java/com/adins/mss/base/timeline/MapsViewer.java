package com.adins.mss.base.timeline;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapsViewer extends FragmentActivity implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private LatLng locationPoint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        if (savedInstanceState == null) {
            mapFragment.setRetainInstance(true);
        } else {
            mapFragment.getMapAsync(this);
        }
        initialize();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    private void initialize() {
        if (mGoogleMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps)).getMapAsync(this);
            if (mGoogleMap != null) {
                setupMaps();
            }
        }
    }

    private void setupMaps() {
        try {
            String lat = getIntent().getStringExtra("latitude");
            String lng = getIntent().getStringExtra("longitude");
            int accuracy = getIntent().getIntExtra("accuracy", 0);
            double mLatitude = Double.parseDouble(lat);
            double mLongitude = Double.parseDouble(lng);
            locationPoint = new LatLng(mLatitude, mLongitude);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 16));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(locationPoint);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mGoogleMap.addMarker(markerOptions);

            if (accuracy != 0) {
                CircleOptions circleOptions = new CircleOptions()
                        .center(locationPoint)
                        .radius(accuracy)
                        .fillColor(0x402196F3)
                        .strokeColor(Color.TRANSPARENT)
                        .strokeWidth(2);

                mGoogleMap.addCircle(circleOptions);
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mGoogleMap != null) {
            setupMaps();
        }
    }
}
