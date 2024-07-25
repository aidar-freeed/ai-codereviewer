package com.adins.mss.base.authentication;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginUserRequest extends MssRequestType implements Serializable {
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    //	private String imei;
    @SerializedName("flagFreshInstall")
    private String flagFreshInstall;
    private String fcmTokenId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //	public String getImei() {
//		return imei;
//	}
//	public void setImei(String imei) {
//		this.imei = imei;
//	}
    public String getFlagFreshInstall() {
        return flagFreshInstall;
    }

    public void setFlagFreshInstall(String flagFreshInstall) {
        this.flagFreshInstall = flagFreshInstall;
    }

    public String getFcmTokenId() {
        return fcmTokenId;
    }

    public void setFcmTokenId(String fcmTokenId) {
        this.fcmTokenId = fcmTokenId;
    }
}
