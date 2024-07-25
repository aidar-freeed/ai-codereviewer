package com.adins.mss.foundation.oauth2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gigin.ginanjar on 13/05/2016.
 */
public class OauthErrorResponse {
    @SerializedName("error")
    public String error;
    @SerializedName("error_description")
    public String error_description;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}
