package com.adins.mss.foundation.http;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model containing device data for audit purposes.
 *
 * @author sumatris
 */
public class AuditDataType implements Serializable {
    /**
     * uuid of a user
     */
    @SerializedName("callerId")
    protected String callerId;
    @SerializedName("os")
    protected String os;
    @SerializedName("osVersion")
    protected String osVersion;
    @SerializedName("deviceId")
    protected String deviceId;
    @SerializedName("deviceModel")
    protected String deviceModel;
    @SerializedName("application")
    protected String application;
    @SerializedName("applicationVersion")
    protected String applicationVersion;
    @SerializedName("locale")
    protected String locale;
    @SerializedName("androidId")
    protected String androidId;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        String loginId = "";
        try {
            loginId = GlobalData.getSharedGlobalData().getTenant();
        } catch (Exception e) {
            FireCrash.log(e);

        }

        this.callerId = callerId + getTenantId(loginId);
    }

    private String getTenantId(String loginId) {
        int index = loginId.indexOf("@");
        String result = "";
        if (index != -1) {

            result = loginId.substring(index, loginId.length());
        }

        return result;

    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }
}
