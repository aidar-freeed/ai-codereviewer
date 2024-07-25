package com.adins.mss.coll;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.LoginActivity;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.coll.login.MCDefaultLoginModel;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;


public class MCLoginActivity extends LoginActivity implements ThemeLoader.ColorSetLoaderCallback {

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
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(Global.AUTOLOGIN_ENABLE){
			NewMainActivity.setMainMenuClass(NewMCMainActivity.class);
			ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
					"GlobalData", Context.MODE_PRIVATE);
			hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
			if (hasLogged /*&& GlobalData.getSharedGlobalData().getUser() != null*/) {
				super.onCreate(savedInstanceState);
				NewMainActivity.InitializeGlobalDataIfError(getApplicationContext());

				String uuidUser = GlobalData.getSharedGlobalData().getUser() == null ? sharedPref.getString("UUID_USER", "") : GlobalData.getSharedGlobalData().getUser().getUuid_user();
				//check theme config.
				GeneralParameter generalParameter = GeneralParameterDataAccess.getOne(this, uuidUser,
						Global.GS_THEME_CONFIG_COLLECTION);

				if(generalParameter != null){
					String urlConfig = generalParameter.getGs_value();
					themeLoader.loadThemeFromUrl(urlConfig,this);
				}
				else {//go to main activity
					Intent intent = new Intent(getApplicationContext(), NewMCMainActivity.class);
					startActivity(intent);
					finish();
				}
			}
			else {//load directly login page and load saved theme
				super.onCreate(savedInstanceState);
				logo.setImageResource(com.adins.mss.base.R.drawable.icon_coll_act);
				try {
					loadSavedTheme();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else {//load directly login page and load saved theme
			super.onCreate(savedInstanceState);
			logo.setImageResource(com.adins.mss.base.R.drawable.icon_coll_act);
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
		// TODO Auto-generated method stub
		return new MCDefaultLoginModel(this);
	}

	@Override
	public void onHasLoaded(DynamicTheme dynamicTheme) {//callback load from local db
		if(dynamicTheme != null && dynamicTheme.getThemeItemList().size() > 0){
			applyColorTheme(dynamicTheme);
		}
	}

	@Override
	public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {//callback load from server
		Intent intent = new Intent(getApplicationContext(), NewMCMainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onResume() {
		super.onResume();

		//Set Firebase screen name
		screenName.setCurrentScreen(this,getString(R.string.screen_name_coll_login),null);

		defaultConfig = new HashMap<>();
		defaultConfig.put("cipher_unsupported_device", Global.SQLITE_CIPHER_UNSUPPORTED);
		this.fetchConfig();
	}
}
