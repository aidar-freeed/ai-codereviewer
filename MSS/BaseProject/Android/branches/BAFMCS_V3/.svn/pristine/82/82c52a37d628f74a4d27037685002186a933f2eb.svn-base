package com.adins.mss.base.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.PrintActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.authentication.Authentication;
import com.adins.mss.base.commons.CommonImpl;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.config.ConfigFileReader;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.print.CopyBitmapLogo;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

import java.util.Properties;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kusnendi.muhamad on 26/07/2017.
 */

public class LoginImpl extends CommonImpl implements LoginInterface {

    public LocationManager mLocation = null;
    public LocationListener locationListener;
    private Activity activity;
    private ObscuredSharedPreferences loginPreferences;
    private ObscuredSharedPreferences sharedPref;
    private boolean isRooted;
    private boolean can_access_developer_mode = false;

    public LoginImpl(Activity activity) {
        this.activity = activity;
        initialize();
    }

    public boolean isCan_access_developer_mode() {
        return can_access_developer_mode;
    }

    public void setCan_access_developer_mode(boolean can_access_developer_mode) {
        this.can_access_developer_mode = can_access_developer_mode;
    }

    //    public String language = LocaleHelper.getLanguage(activity);

    public void initialize() {
        String language = LocaleHelper.getLanguage(activity);
        sharedPref = ObscuredSharedPreferences.getPrefs(activity,
                "GlobalData", Context.MODE_PRIVATE);

        GlobalData.getSharedGlobalData();
        GlobalData.getSharedGlobalData().loadFromProperties(activity);
        GlobalData.getSharedGlobalData().setLocale(language);

        if (Global.IS_BYPASSROOT)
            isRooted = false;
        else
            isRooted = checkIsRooted();

        Properties prop = ConfigFileReader.propertiesFromFile(activity, GlobalData.PROPERTY_FILENAME);
        can_access_developer_mode = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_CAN_ACCESS_DEVELOPER_MODE, "false"));
        boolean isDev = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_IS_DEVELOPER, "false"));
        Global.IS_DEV = sharedPref.getBoolean("IS_DEV", isDev);
        String urlHeader = sharedPref.getString("URL_HEADER", "");
        if (urlHeader != null && urlHeader.length() > 0) {
            GlobalData.getSharedGlobalData().setUrlMain(urlHeader);
            GlobalData.getSharedGlobalData().reloadUrl(activity);
        }

        boolean accessTokenEnable = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_IS_REQUIRED_ACCESS_TOKEN, "false"));
        String propClientId = prop.getProperty(GlobalData.PROP_CLIENT_ID, "android");
        boolean encrypt = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_ENCRYPT, "false"));
        boolean decrypt = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_DECRYPT, "false"));
        boolean byPassDeveloper = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_IS_BYPASS_DEVELOPER, "false"));
        boolean hasEncrypt = sharedPref.getBoolean("IS_ENCRYPT", encrypt);
        boolean hasDecrypt = sharedPref.getBoolean("IS_DECRYPT", decrypt);
        boolean isTokenEnable = sharedPref.getBoolean("IS_ACCESS_TOKEN_ENABLE", accessTokenEnable);
        boolean isByPassEnable = sharedPref.getBoolean("IS_BYPASS_ENABLE", byPassDeveloper);
        String clientId = sharedPref.getString("CLIENT_ID", propClientId);
        boolean freshInstall = Authentication.isFreshInstall(activity);
        if(freshInstall && accessTokenEnable){
            isTokenEnable = accessTokenEnable;
            sharedPref.edit().putBoolean(Global.IDF_IS_ACCESS_TOKEN_ENABLE,isTokenEnable).apply();
        }
        GlobalData.getSharedGlobalData().setEncrypt(hasDecrypt);
        GlobalData.getSharedGlobalData().setDecrypt(hasEncrypt);
        GlobalData.getSharedGlobalData().setRequiresAccessToken(isTokenEnable);
        GlobalData.getSharedGlobalData().setByPassDeveloper(isByPassEnable);

        if (GlobalData.getSharedGlobalData().isRequiresAccessToken()) {
            GlobalData.getSharedGlobalData().setClientId(clientId);
        }

        if (isRooted == true) {
            DialogManager.showRootAlert(activity, activity.getApplicationContext());
        }

        Global.printActivityClass = PrintActivity.class;
        CopyBitmapLogo copyBitmapLogo = new CopyBitmapLogo(activity);
        copyBitmapLogo.copyLogoPrint();

        TextView tvAppVersion = (TextView) activity.findViewById(R.id.contentVersion);
    }

    @Override
    public void initializePreferences() {
        loginPreferences = ObscuredSharedPreferences.getPrefs(activity, DefaultLoginModel.LOGIN_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    public void bindLocationListener() {
        mLocation = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Utility.checkPermissionGranted(activity);
                    return;
                } else {
                    if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                        mLocation.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                }
            } else {
                if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                    mLocation.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            }
        } catch (IllegalArgumentException e) {
            // TODO: handle exception
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (Global.IS_DEV)
            System.out.println(activity.getString(R.string.masuk_on_change));
        if (location != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (location.isFromMockProvider())
                    DialogManager.showMockDialog(activity);
            }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (Global.IS_DEV)
            System.out.println(activity.getString(R.string.masuk_on_status_change));
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (Global.IS_DEV)
            System.out.println(activity.getString(R.string.masuk_on_provider_change));
        DialogManager.closeGPSAlert();
    }

    @Override
    public void onProviderDisabled(String provider) {
        DialogManager.showGPSAlert(activity);
    }

    @Override
    public LocationManager getLocationManager() {
        return mLocation;
    }

    public ObscuredSharedPreferences getLoginPreferences() {
        return loginPreferences;
    }

    public ObscuredSharedPreferences getSharedPref() {
        return sharedPref;
    }

    public boolean isRooted() {
        return isRooted;
    }

    //Method for remove location listener
    @Override
    public void removeUpdateLocation() {
        mLocation.removeUpdates(this);
        mLocation = null;
    }
}
