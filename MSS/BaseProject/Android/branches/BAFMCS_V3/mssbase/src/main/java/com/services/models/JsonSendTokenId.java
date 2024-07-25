package com.services.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class JsonSendTokenId extends MssRequestType {
    @SerializedName("fcmTokenId")
    String token_id;
    @SerializedName("uuid_user")
    String uuid_user;

    public JsonSendTokenId() {
    }

    public JsonSendTokenId(String token_id, String uuid_user) {
        this.token_id = token_id;
        this.uuid_user = uuid_user;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getUuid_user() {
        return uuid_user;
    }

    public void setUuid_user(String uuid_user) {
        this.uuid_user = uuid_user;
    }
}
