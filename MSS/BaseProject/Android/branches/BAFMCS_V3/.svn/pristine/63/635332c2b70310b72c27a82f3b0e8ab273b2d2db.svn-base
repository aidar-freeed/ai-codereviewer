package com.adins.mss.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.AdapterView;

import com.adins.mss.base.commons.AppInfo;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.Utility;
import com.adins.mss.foundation.dialog.DialogManager;

/**
 * Created by kusnendi.muhamad on 03/08/2017.
 */

public abstract class MssFragmentActivity extends AppCompatActivity implements LocationListener,
        AdapterView.OnItemClickListener, AppInfo {

    protected LocationManager mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindLocationListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocation != null) {
            mLocation.removeUpdates(this);
            if (mLocation != null) {
                mLocation = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Check GPS Location Setting
//        DialogManager.showGPSAlert(this);

        if (mLocation == null) {
            bindLocationListener();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (location.isFromMockProvider())
                    DialogManager.showMockDialog(this);
            }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        DialogManager.closeGPSAlert();
    }

    @Override
    public void onProviderDisabled(String provider) {
        DialogManager.showGPSAlert(this);
    }

    @Override
    public void checkAppVersion(NewMainActivity activity) {
    }

    public void bindLocationListener() {
        mLocation = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Utility.checkPermissionGranted(this);
                    return;
                } else {
                    if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                        mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15 * 1000L, 10f, this);
                }
            } else {
                if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                    mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15 * 1000L, 10f, this);
            }
        } catch (IllegalArgumentException e) {
            // TODO: handle exception
        } catch (Exception e) {
            FireCrash.log(e);

        }
    }
}
