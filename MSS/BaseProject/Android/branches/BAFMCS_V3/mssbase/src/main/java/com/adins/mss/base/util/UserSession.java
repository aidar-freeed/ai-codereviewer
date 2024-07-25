package com.adins.mss.base.util;

import android.content.Context;

import com.adins.mss.base.AppContext;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

/**
 * Created by angga.permadi on 8/3/2016.
 */
public class UserSession {
    protected static final String PREF_NAME_USER_SESSION = "UserSession";
    protected static final String PREF_APP_VERSION = "appVersion";
    protected static final int APP_VERSION_DEF = 0;
    protected static final String PREF_INVALID_TOKEN = "isInvalidToken";
    protected static final boolean IS_INVALID_TOKEN_DEF = false;

    private static ObscuredSharedPreferences preferences = ObscuredSharedPreferences.getPrefs(AppContext.getInstance().getApplicationContext()
            , PREF_NAME_USER_SESSION, Context.MODE_PRIVATE);

    private static ObscuredSharedPreferences.Editor preferencesEditor = preferences.edit();

    public static int getAppVersion() {
        return preferences.getInt(PREF_APP_VERSION, APP_VERSION_DEF);
    }

    public static void setAppVersion(int appVersion) {
        preferencesEditor.putInt(PREF_APP_VERSION, appVersion).apply();
    }

    public static boolean needUpdateVersion() {
        return getAppVersion() < AppContext.getInstance().getVersionCode();
    }

    public static boolean isInvalidToken() {
        return preferences.getBoolean(PREF_INVALID_TOKEN, IS_INVALID_TOKEN_DEF);
    }

    public static void setInvalidToken(boolean invalidToken) {
        preferencesEditor.putBoolean(PREF_INVALID_TOKEN, invalidToken).apply();
    }

    public static void clear() {
        preferencesEditor.remove(PREF_INVALID_TOKEN).apply();
//                clear().apply();
    }
}
