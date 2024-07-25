package com.adins.mss.foundation.oauth2;

public class OAuth2Config {

    private final String scope;
    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String username;
    private final String password;
    private final String site;

    private OAuth2Config(OAuth2ConfigBuilder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.site = builder.site;
        this.scope = builder.scope;
        this.grantType = builder.grantType;
    }

    public String getScope() {
        return scope;
    }

    public String getGrantType() {
        return grantType;
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTokenEndPointUrl() {
        return site + "/oauth/token";
    }

    public static class OAuth2ConfigBuilder {
        private String scope;
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;
        private String site;

        public OAuth2ConfigBuilder(String username, String password, String clientId, String clientSecret, String site) {
            this.username = username;
            this.password = password;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.site = site;
        }

        public OAuth2ConfigBuilder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public OAuth2ConfigBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public OAuth2Config build() {
            return new OAuth2Config(this);
        }
    }
}
