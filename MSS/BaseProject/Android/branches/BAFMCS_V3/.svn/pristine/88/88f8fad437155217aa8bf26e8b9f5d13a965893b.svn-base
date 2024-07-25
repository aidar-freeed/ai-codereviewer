package com.adins.mss.base.checkout.activity;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.checkout.CheckOutLocationTask;
import com.adins.mss.base.checkout.CheckOutManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.timeline.Constants;
import com.adins.mss.dao.LocationInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CheckOutActivity extends Fragment implements
        OnClickListener, OnMapReadyCallback {

    public static TextView AddressLocale;
    public static TextView AddressLine;
    public static LinearLayout descLayout;
    static CheckOutManager manager;
    static LocationInfo info;
    static LatLng latLng;
    static GoogleMap mGoogleMap;
    private static Context context;
    private static String[] address;
    private static View view;
    Button btnRefresh;
    double mLatitude = 0;
    double mLongitude = 0;

    public static void setLocation(String[] address) {
        descLayout.setVisibility(View.VISIBLE);
        AddressLocale.setText(address[0]);
        AddressLine.setText(address[1]);
        CheckOutActivity.address = address;
    }

    public static void setNewLocation() {
        info = manager.getNewLocation();
        manager.getAddressLocation(context, info);
        latLng = manager.getLatLng(info);
        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mGoogleMap.addMarker(markerOptions);

//	    CheckOutManager.stopPeriodicUpdates();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_absentout));
        context = activity;
        try {
            manager = new CheckOutManager(activity);
            info = manager.getLocationInfoCheckOut();
            manager.getAddressLocation(context, info);
            latLng = manager.getLatLng(info);
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO: handle exception
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().removeAllTabs();
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_absentout));
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.checkout_layout, container, false);

            AddressLocale = (TextView) view.findViewById(R.id.textLocalOut);
            AddressLine = (TextView) view.findViewById(R.id.textAddressOut);
            descLayout = (LinearLayout) view.findViewById(R.id.DescLayoutOut);
            btnRefresh = (Button) view.findViewById(R.id.btnRefreshCOut);

            btnRefresh.setOnClickListener(this);
            descLayout.setOnClickListener(this);
            SupportMapFragment fragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapOut);

            // Setting Google Map
            fragment.getMapAsync(this);

        } catch (Exception e) {
            FireCrash.log(e);
            // TODO: handle exception
//			initializeMaps();
        }


        Constants.inAbsent = true;

        return view;
    }

    private void initializeMaps() {
        try {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mGoogleMap.addMarker(markerOptions);
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO: handle exception
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == R.id.btnRefreshCOut) {
            manager.updateLocationCheckOut();
        } else if (id == R.id.DescLayoutOut) {
            CheckOutLocationTask task = new CheckOutLocationTask(context, getString(R.string.progressWait),
                    getString(R.string.msgUnavaibleLocationCheckOut), info, address);
            task.execute();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        initializeMaps();
    }
}