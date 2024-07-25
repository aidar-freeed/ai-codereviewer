package com.adins.mss.foundation.oauth2;


import android.app.Activity;

import com.adins.mss.base.GlobalData;
import com.google.gson.annotations.SerializedName;

import java.lang.ref.WeakReference;

public class Token {

    @SerializedName("expires_in")
    private final Long expires_in;
    @SerializedName("expires_at")
    private final Long expires_at;
    @SerializedName("token_type")
    private final String token_type;
    @SerializedName("refresh_token")
    private final String refresh_token;
    @SerializedName("access_token")
    private final String access_token;


    public Token(Long expiresIn, String tokenType, String refreshToken, String accessToken) {
        this.expires_in = expiresIn;
        this.token_type = tokenType;
        this.refresh_token = refreshToken;
        this.access_token = accessToken;
        this.expires_at = (expiresIn * 1000) + System.currentTimeMillis();
    }

    public Token(Long expiresIn, String tokenType, String refreshToken, String accessToken, Long expiresAt) {
        this.expires_in = expiresIn;
        this.token_type = tokenType;
        this.refresh_token = refreshToken;
        this.access_token = accessToken;
        this.expires_at = expiresAt;
    }

    public Long getExpiresIn() {
        return expires_in;
    }

    public Long getExpiresAt() {
        return expires_at;
    }

    public String getTokenType() {
        return token_type;
    }


    public String getRefreshToken() {
        return refresh_token;
    }


    public String getAccessToken() {
        return access_token;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() >= this.getExpiresAt()) ? true : false;
    }

    public String getResource(OAuth2Client client, Token token, String path) {
        if (GlobalData.getSharedGlobalData().isSecureConnection()) {
            return OAuthUtils.getProtectedResourceHttps(client, token, path);
        } else {
            return OAuthUtils.getProtectedResourceHttp(client, token, path);
        }
    }

    public Token refresh(Activity activity, OAuth2Client client) {
        OAuth2Config oauthConfig = new OAuth2Config.OAuth2ConfigBuilder(client.getUsername(), client.getPassword(), client.getClientId(), client.getClientSecret(), client.getSite())
                .grantType("refresh_token").build();
        WeakReference activityReference = new WeakReference(activity);
        if (GlobalData.getSharedGlobalData().isSecureConnection()) {
            return OAuthUtils.refreshAccessTokenHttps(activityReference,this, oauthConfig);
        } else {
            return OAuthUtils.refreshAccessTokenHttp(activityReference, this, oauthConfig);
        }
    }
}
