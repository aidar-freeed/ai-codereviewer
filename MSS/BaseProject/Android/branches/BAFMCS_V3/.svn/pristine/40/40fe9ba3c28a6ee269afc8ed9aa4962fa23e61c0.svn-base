package com.adins.mss.odr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.LoginActivity;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.odr.login.MODefaultLoginModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;

public class MOLoginActivity extends LoginActivity implements ThemeLoader.ColorSetLoaderCallback{

	private FirebaseAnalytics screenName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        //instantiate theme loader
	    themeLoader = new ThemeLoader(this);

		defaultConfig = new HashMap<>();
		defaultConfig.put("cipher_unsupported_device", Global.SQLITE_CIPHER_UNSUPPORTED);
		this.fetchConfig();

		PackageInfo pInfo = null;
		String sqliteCipherException;
		String deviceModel = Build.MODEL;

		screenName = FirebaseAnalytics.getInstance(this);

		try {
			sqliteCipherException = GlobalData.getSharedGlobalData().getRemoteConfig().getString("cipher_unsupported_device");
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Global.APP_VERSION = pInfo.versionName;
			Global.BUILD_VERSION = pInfo.versionCode;
			Global.IS_DEV = BuildConfig.IS_DEV;
			Global.FLAVORS = BuildConfig.IS_FLAVORS;
			Global.IS_BYPASSROOT = BuildConfig.IS_BYPASSROOT;
			Global.IS_DBENCRYPT  = (!sqliteCipherException.contains(deviceModel)) && BuildConfig.IS_DBENCRYPT;
		} catch (PackageManager.NameNotFoundException e1) {
			e1.printStackTrace();
		}

		if (Global.AUTOLOGIN_ENABLE) {
			NewMainActivity.setMainMenuClass(NewMOMainActivity.class);
			ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
					"GlobalData", Context.MODE_PRIVATE);
			hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
			if(hasLogged && GlobalData.getSharedGlobalData().getUser() != null){
				super.onCreate(savedInstanceState);
				NewMainActivity.InitializeGlobalDataIfError(getApplicationContext());
				//check theme config.
				String uuidUser = GlobalData.getSharedGlobalData().getUser() == null ? sharedPref.getString("UUID_USER", "") : GlobalData.getSharedGlobalData().getUser().getUuid_user();
				GeneralParameter generalParameter = GeneralParameterDataAccess.getOne(this,
						uuidUser,
						Global.GS_THEME_CONFIG_ORDER);

				if(generalParameter != null){
					String urlConfig = generalParameter.getGs_value();
					themeLoader.loadThemeFromUrl(urlConfig,this);
				}
				else {//go to main activity
					Intent intent = new Intent(getApplicationContext(), NewMOMainActivity.class);
					startActivity(intent);
					finish();
				}
			}
			else {//load directly login page and load saved theme
				super.onCreate(savedInstanceState);
				logo.setImageResource(com.adins.mss.base.R.drawable.icon_lead_in);
				loadSavedTheme();
			}
		}else {//load directly login page and load saved theme
			super.onCreate(savedInstanceState);
			logo.setImageResource(com.adins.mss.base.R.drawable.icon_lead_in);
			loadSavedTheme();
		}
	}

	private void loadSavedTheme(){
		if(themeLoader != null){
			themeLoader.loadSavedColorSet(this);
		}
	}

	@Override
	protected DefaultLoginModel getNewDefaultLoginModel(Context context) {
		return new MODefaultLoginModel(this);
	}
	@Override
	public void onResume() {
		super.onResume();

		//Set Firebase screen name
		screenName.setCurrentScreen(this,getString(R.string.screen_name_odr_login),null);

		defaultConfig = new HashMap<>();
		defaultConfig.put("cipher_unsupported_device", Global.SQLITE_CIPHER_UNSUPPORTED);
		this.fetchConfig();
	}

	@Override
	public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {//callback load from server
		Intent intent = new Intent(getApplicationContext(), NewMOMainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onHasLoaded(DynamicTheme dynamicTheme) {//callback load from local db
		if(dynamicTheme != null && dynamicTheme.getThemeItemList().size() > 0){
			applyColorTheme(dynamicTheme);
		}
	}

}
