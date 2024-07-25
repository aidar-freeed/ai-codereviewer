package com.adins.mss.base.todolist.form;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.TaskH;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;

import lib.gegemobile.gddlibrary.GoogleDirection;
import lib.gegemobile.gddlibrary.GoogleDirection.OnDirectionResponseListener;
import lib.gegemobile.gddlibrary.GoogleDistanceMatrix;
import lib.gegemobile.gddlibrary.GoogleDistanceMatrix.DistanceResponseJson;
import lib.gegemobile.gddlibrary.GoogleDistanceMatrix.OnDistanceResponseListener;

public class ViewMapActivityWithDirection extends FragmentActivity implements OnMapReadyCallback {
    private static List<TaskH> listTaskH;
    SupportMapFragment mapFragment;
    Button btnDirection;
    Button btnAnimate;
    CheckBox cbAvoidTolls;
    TextView txtDistance;
    TextView txtTime;
    LinearLayout resultLayout;
    boolean isAvoidTolls = false;
    List<LatLng> taskPositions = new ArrayList<>();
    LatLng EndPoint = null;
    List<LatLng> waypoints = new ArrayList<>();
    HashMap<LatLng, Integer> distances = new HashMap<>();
    int largestDistance = 0;
    GoogleDirection gd;
    GoogleDistanceMatrix gdm;
    Document mDoc;
    private GoogleMap mGoogleMap;
    private LatLng locationPoint;
    private Random rnd = new Random();

    public List<TaskH> getListTaskH() {
        return listTaskH;
    }

    public static void setListTaskH(List<TaskH> value) {
        ViewMapActivityWithDirection.listTaskH = value;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout_with_direction);
        btnDirection = (Button) findViewById(R.id.buttonDirection);
        btnAnimate = (Button) findViewById(R.id.buttonAnimate);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        if (savedInstanceState == null) {
            mapFragment.setRetainInstance(true);
        } else {
            mapFragment.getMapAsync(ViewMapActivityWithDirection.this);
        }
        initialize();
        initializeDirection();
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

    private void initializeDirection() {
        cbAvoidTolls = (CheckBox) findViewById(R.id.cbAvoidTolls);
        txtDistance = (TextView) findViewById(R.id.textDistance);
        txtTime = (TextView) findViewById(R.id.textTime);
        resultLayout = (LinearLayout) findViewById(R.id.ResultValue);

        cbAvoidTolls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAvoidTolls = isChecked;
            }
        });

        btnDirection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                gdm.setLogging(true);
                gdm.request(locationPoint, taskPositions, GoogleDirection.MODE_DRIVING, isAvoidTolls);
                btnAnimate.setVisibility(View.VISIBLE);
            }
        });
        btnAnimate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                gd.animateDirection(mGoogleMap, gd.getDirection(mDoc), GoogleDirection.SPEED_NORMAL
                        , true, true, true, false, null, false, true, new PolylineOptions().width(3).color(color));
            }
        });
        gd = new GoogleDirection(this);
        gd.setOnDirectionResponseListener(new OnDirectionResponseListener() {
            public void onResponse(String status, Document doc, GoogleDirection gd) {
                mDoc = doc;
                resultLayout.setVisibility(View.VISIBLE);
                mGoogleMap.addPolyline(gd.getPolyline(doc, 3, Color.RED));
                String str = gd.getAllTotalDistanceText(doc);
                String time = gd.getAllTotalDurationText(doc);
                txtDistance.setText(str);
                txtTime.setText(time);
            }
        });

        gdm = new GoogleDistanceMatrix(this);
        gdm.setOnDistanceResponseListener(new OnDistanceResponseListener() {

            @Override
            public void onResponse(String status, DistanceResponseJson json,
                                   GoogleDistanceMatrix gdm) {
                if (gdm != null) {
                    for (int i = 0; i < taskPositions.size(); i++) {
                        int timer = gdm.getDurationValue(json, 0, i);
                        distances.put(taskPositions.get(i), timer);
                    }

                    for (Entry<LatLng, Integer> result : distances.entrySet()) {
                        if (largestDistance == 0 || largestDistance < result.getValue()) {
                            largestDistance = result.getValue();
                        }
                        if (largestDistance == result.getValue())
                            EndPoint = result.getKey();
                    }

                    for (Entry<LatLng, Integer> result : distances.entrySet()) {
                        if (EndPoint != null && EndPoint != result.getKey())
                                waypoints.add(result.getKey());
                    }
                    gd.setLogging(true);
                    gd.request(locationPoint, EndPoint, waypoints, GoogleDirection.MODE_DRIVING, isAvoidTolls);
                }
            }
        });
    }

    private void initialize() {
        if (mGoogleMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps)).getMapAsync(this);
            if (mGoogleMap != null) {
                setupMaps();
            }
        }
        if (getListTaskH() == null)
            setListTaskH(ToDoList.getListTaskInPriority(getApplicationContext(), ToDoList.SEARCH_BY_ALL, null));
        if (getListTaskH() != null && !getListTaskH().isEmpty()) {
            for (TaskH task : getListTaskH()) {
                try {
                    double latitude = Double.parseDouble(task.getLatitude());
                    double longitude = Double.parseDouble(task.getLongitude());
                    LatLng latLng = new LatLng(latitude, longitude);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(task.getCustomer_name());
                    markerOptions.snippet(task.getCustomer_phone() + "\r\n" + task.getCustomer_address());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mGoogleMap.addMarker(markerOptions);
                    mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View v = getLayoutInflater().inflate(R.layout.marker, null);
                            TextView infoTitle = (TextView) v.findViewById(R.id.infoTitle);
                            infoTitle.setText(marker.getTitle());
                            TextView infoSnippet = (TextView) v.findViewById(R.id.infoSnippet);
                            infoSnippet.setText(marker.getSnippet());
                            return v;
                        }
                    });
                    taskPositions.add(latLng);
                } catch (Exception e) {
                    FireCrash.log(e);

                }
            }
        }
        taskPositions.add(new LatLng(-6.201071, 106.766674));
        taskPositions.add(new LatLng(-6.200223, 106.765252));
        taskPositions.add(new LatLng(-6.198845, 106.761106));
        taskPositions.add(new LatLng(-6.193935, 106.763401));
        taskPositions.add(new LatLng(-6.201945, 106.762071));
        taskPositions.add(new LatLng(-6.197922, 106.763393));
        taskPositions.add(new LatLng(-6.195699, 106.763559));
        for (LatLng latLng : taskPositions) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mGoogleMap.addMarker(markerOptions);
        }

    }

    private void setupMaps() {
        try {
            LocationInfo info = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
            double mLatitude = Double.parseDouble(info.getLatitude());
            double mLongitude = Double.parseDouble(info.getLongitude());
            locationPoint = new LatLng(mLatitude, mLongitude);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 12));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(locationPoint);
            markerOptions.title("My Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mGoogleMap.addMarker(markerOptions);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }
}

