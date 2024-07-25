package com.adins.mss.foundation.oauth2.store;

import android.content.Context;
import android.util.Log;

import com.adins.mss.foundation.oauth2.Token;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

import java.io.IOException;

/**
 * Created by gigin.ginanjar on 13/05/2016.
 */
public class SharedPreferencesTokenStore {
    public static final String TOKEN_PREFERENCES = "token_preferences";
    public static final String TAG = "AndroidOauth2";
    private static final String ACCESS_TOKEN = "_access_token";
    private static final String EXPIRES_IN = "_expires_in";
    private static final String EXPIRES_AT = "_expires_at";
    private static final String REFRESH_TOKEN = "_refresh_token";
    private static final String TOKEN_TYPE = "_token_type";
    private static final String SCOPE = "_scope";

    private ObscuredSharedPreferences prefs;

    public SharedPreferencesTokenStore(ObscuredSharedPreferences prefs) {
        this.prefs = prefs;
    }

    public SharedPreferencesTokenStore(Context context) {
        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context, TOKEN_PREFERENCES, Context.MODE_PRIVATE);
        this.prefs = sharedPref;
    }

    public Token load(String userId) throws IOException {
        Log.i(TAG, "Loading credential for userId " + userId);
        Log.i(TAG, "Loaded access token = " + prefs.getString(userId + ACCESS_TOKEN, ""));
        /*String accessToken = prefs.getString(userId+ACCESS_TOKEN,null);
        String refreshToken = prefs.getString(userId+REFRESH_TOKEN,null);
        String tokenType = prefs.getString(userId+TOKEN_TYPE,null);
        long expiresAt = prefs.getLong(userId+EXPIRES_AT, 0);
        long expiresIn = prefs.getLong(userId+EXPIRES_IN, 0);*/
        String accessToken = prefs.getString(ACCESS_TOKEN, null);
        String refreshToken = prefs.getString(REFRESH_TOKEN, null);
        String tokenType = prefs.getString(TOKEN_TYPE, null);
        long expiresAt = prefs.getLong(EXPIRES_AT, 0);
        long expiresIn = prefs.getLong(EXPIRES_IN, 0);
        Token token = new Token(expiresIn, tokenType, refreshToken, accessToken, expiresAt);
        return token;
    }

    public void store(String userId, Token token) throws IOException {
        Log.i(TAG, "Storing credential for userId " + userId);
        Log.i(TAG, "Access Token = " + token.getAccessToken());

        ObscuredSharedPreferences.Editor editor = prefs.edit();

        /*editor.putString(userId + ACCESS_TOKEN, token.getAccessToken());
        editor.putString(userId + REFRESH_TOKEN, token.getRefreshToken());
        editor.putString(userId + TOKEN_TYPE, token.getTokenType());
        editor.putLong(userId + EXPIRES_IN, token.getExpiresIn());
        editor.putLong(userId + EXPIRES_AT, token.getExpiresAt());*/
        editor.putString(ACCESS_TOKEN, token.getAccessToken());
        editor.putString(REFRESH_TOKEN, token.getRefreshToken());
        editor.putString(TOKEN_TYPE, token.getTokenType());
        editor.putLong(EXPIRES_IN, token.getExpiresIn());
        editor.putLong(EXPIRES_AT, token.getExpiresAt());

        editor.commit();
    }

    public void delete(String userId) throws IOException {
        Log.i(TAG, "Deleting credential for userId " + userId);
        ObscuredSharedPreferences.Editor editor = prefs.edit();
        /*editor.remove(userId + ACCESS_TOKEN);
        editor.remove(userId + EXPIRES_IN);
        editor.remove(userId + REFRESH_TOKEN);
        editor.remove(userId + TOKEN_TYPE);
        editor.remove(userId + EXPIRES_AT);*/
        editor.remove(ACCESS_TOKEN);
        editor.remove(EXPIRES_IN);
        editor.remove(REFRESH_TOKEN);
        editor.remove(TOKEN_TYPE);
        editor.remove(EXPIRES_AT);
        editor.commit();
    }
}
