package com.adins.mss.base.todolist.form;


import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.todolist.ToDoList;
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

import org.acra.ACRA;

import java.util.List;

public class ViewMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static List<TaskH> listTaskH;
    SupportMapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private LatLng locationPoint;

    public List<TaskH> getListTaskH() {
        return listTaskH;
    }

    public static void setListTaskH(List<TaskH> value) {
        ViewMapActivity.listTaskH = value;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        if (savedInstanceState == null) {
            mapFragment.setRetainInstance(true);
        } else {
            mapFragment.getMapAsync(this);
        }
        initialize();
    }

    private void initialize() {
        if (mGoogleMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps)).getMapAsync(this);
            if (mGoogleMap != null) {
                setupMaps();
            }
        }
        if (GlobalData.getSharedGlobalData().getUser() == null) {
            NewMainActivity.InitializeGlobalDataIfError(getApplicationContext());
        }
        if (getListTaskH() == null)
            setListTaskH(ToDoList.getListTaskInPriority(getApplicationContext(), ToDoList.SEARCH_BY_ALL, null));
        if (getListTaskH() != null && getListTaskH().size() > 0) {
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
                } catch (Exception e) {
                    FireCrash.log(e);

                }
            }
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
            markerOptions.title("Your Location");
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

