package com.adins.mss.foundation.oauth2;


import android.content.Context;

import com.adins.mss.base.GlobalData;

public class OAuth2Client {

    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;
    private final String site;

    /**
     * @param username     - username
     * @param password     - password
     * @param clientId     - Client ID
     * @param clientSecret - Client Secret
     * @param site         - Main Url
     */
    public OAuth2Client(String username, String password, String clientId, String clientSecret, String site) {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.site = site;
    }

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public String getClientId() {
        return clientId;
    }


    public String getClientSecret() {
        return clientSecret;
    }


    public String getSite() {
        return site;
    }

    /**
     * Get Access Token from Oauth
     *
     * @return
     */
    public Token getAccessToken(Context context) {
        OAuth2Config oauthConfig = new OAuth2Config.OAuth2ConfigBuilder(username, password, clientId, clientSecret, site)
                .grantType("password").build();
        if (GlobalData.getSharedGlobalData().isSecureConnection()) {
            return OAuthUtils.getAccessTokenHttps(context, oauthConfig);
        } else {
            return OAuthUtils.getAccessTokenHttp(context, oauthConfig);
        }
    }
}
