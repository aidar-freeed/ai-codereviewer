package com.services.models;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Kamil on 11/10/17.
 */

public class JsonResponseTokenId extends MssResponseType {
    @SerializedName("fcmTokenId")
    String token_id;

    public JsonResponseTokenId() {
    }

    public JsonResponseTokenId(String token_id) {
        this.token_id = token_id;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }
}
