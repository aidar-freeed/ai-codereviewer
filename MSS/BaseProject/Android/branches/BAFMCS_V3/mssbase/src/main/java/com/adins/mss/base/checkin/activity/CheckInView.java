package com.adins.mss.base.checkin.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.checkin.CheckInLocationTask;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.commons.CommonImpl;
import com.adins.mss.base.commons.ViewInterface;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.timeline.Constants;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public class CheckInView extends CommonImpl implements ViewInterface, View.OnClickListener, OnMapReadyCallback, IShowError {
    public static Context context;
    public static TextView AddressLocale;
    public static TextView AddressLine;
    public static LinearLayout descLayout;
    static GoogleMap mGoogleMap;
    static CheckInManager manager;
    static LocationInfo info;
    static LatLng latLng;
    private static View view;
    private static String[] address;
    public Activity activity;
    public Fragment fragment;
    ImageButton btnRefresh;
    private ErrorMessageHandler errorMessageHandler;

    public CheckInView(Activity activity) {
        this.activity = activity;
        errorMessageHandler = new ErrorMessageHandler(this);
    }

    public static void setLocation(String[] address) {
        try {
            descLayout.setVisibility(View.VISIBLE);
            AddressLocale.setText(address[0]);
            AddressLine.setText(address[1]);
            CheckInView.address = address;
        } catch (Exception e) {
            FireCrash.log(e);
        }

    }

    public static void setNewLocation() {
        info = manager.getNewLocation();
        manager.getAddressLocation(context, info);
        latLng = manager.getLatLng(info);
        int accuracy = info.getAccuracy();
        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mGoogleMap.addMarker(markerOptions);

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
    public void publish() {
        //EMPTY
    }

    public void onAttach() {
        context = activity;
        try {
            manager = new CheckInManager(activity);
            info = manager.getLocationInfoCheckIn();
            manager.getAddressLocation(context, info);
            latLng = manager.getLatLng(info);
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public View layoutInflater(LayoutInflater inflater, ViewGroup container) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.checkin_layout, container, false);

            AddressLocale = (TextView) view.findViewById(R.id.textLocalIn);
            AddressLine = (TextView) view.findViewById(R.id.textAddressIn);
            descLayout = (LinearLayout) view.findViewById(R.id.DescLayoutIn);
            btnRefresh = (ImageButton) view.findViewById(R.id.btnRefreshCIn);

            btnRefresh.setOnClickListener(this);
            descLayout.setOnClickListener(this);
            SupportMapFragment supportMapFragment = (SupportMapFragment)
                    fragment.getChildFragmentManager().findFragmentById(R.id.mapIn);

            // Setting Google Map
            supportMapFragment.getMapAsync(this);
        } catch (Exception e) {
            FireCrash.log(e);
        }

        manager.updateLocationCheckin();
        Constants.inAbsent = true;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelp.showAllUserHelp((Activity)view.getContext(),fragment.getClass().getSimpleName());
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);

        return view;
    }

    @Override
    public void onCreate() {
        //EMPTY
    }

    @Override
    public void onResume() {
        if (checkPlayServices(activity)) {
            // Then we're good to go!
        }
        showGPSAlert(activity);
    }

    @Override
    public void onDestroy() {
        //EMPTY
    }

    @Override
    public void onCreateOptionsMenu(Menu menu) {
        //EMPTY
    }

    @Override
    public void onOptionsItemSelected(int id) {
        if(id == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserHelp.showAllUserHelp((Activity) view.getContext(), fragment.getClass().getSimpleName());
                }
            }, SHOW_USERHELP_DELAY_DEFAULT);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnRefreshCIn) {
            Animation a = AnimationUtils.loadAnimation(context, R.anim.icon_rotate);
            btnRefresh.startAnimation(a);
            manager.updateLocationCheckin();
        } else if (id == R.id.DescLayoutIn) {
            if (AddressLocale.getText().equals(context.getString(R.string.address_not_found)) || AddressLocale.getText().equals("")) {
                errorMessageHandler.processError(context.getString(R.string.error_capital)
                        ,context.getString(R.string.msgUnavailableAddress)
                        , ErrorMessageHandler.DIALOG_TYPE);
            } else {
                try {
                    CheckInLocationTask task = new CheckInLocationTask(context, context.getString(R.string.progressWait),
                            context.getString(R.string.msgUnavaibleLocationCheckIn), info, address);
                    task.execute();
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (context != null) {
                        errorMessageHandler.processError(context.getString(R.string.error_capital)
                                ,context.getString(R.string.msgUnavaibleLocationCheckIn)
                                , ErrorMessageHandler.DIALOG_TYPE);
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        initializeMaps();
    }

    private void initializeMaps() {
        try {
            try {
                mGoogleMap.clear();
            } catch (Exception e) {
                FireCrash.log(e);
            }
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker marker = mGoogleMap.addMarker(markerOptions);
            Log.d("marker_id",marker.getId());

            int accuracy = info.getAccuracy();
            if (accuracy != 0) {
                CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
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
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(notifType == ErrorMessageHandler.DIALOG_TYPE){
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withTitle(errorSubject)
                    .withMessage(errorMsg)
                    .isCancelable(true)
                    .show();
        }
    }
}
