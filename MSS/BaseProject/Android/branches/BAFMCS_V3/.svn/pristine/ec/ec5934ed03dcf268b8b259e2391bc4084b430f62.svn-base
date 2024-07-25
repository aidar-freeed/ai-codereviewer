package com.adins.mss.base;

/**
 * BaseCommunicationModel provides a class with basic parameters needed in most communication with MSS Server,
 * like user login Id and IMEI code. Any class which are going to communicate in JSON should use/subclass this model object
 * to convert to JSON to ensure the same JSON format with MSS Server's JSON
 *
 * @author glen.iglesias
 * @deprecated as of 17 Dec 2014, communication should use MssRequestType and MssResponseType as standard format
 */
public class BaseCommunicationModel {

    protected String osName;
    protected String userId;
    protected String deviceModel;
    protected String imei;
    protected String imsi;
    protected String pin;
    protected String androidId;

    public BaseCommunicationModel() {
    }

    public BaseCommunicationModel(boolean useDefault) {
        if (useDefault) {

            GlobalData gd = GlobalData.getSharedGlobalData();

            osName = gd.getOsName();
            deviceModel = gd.getDeviceModel();
            imei = gd.getImei();
            imsi = gd.getImsi();
            pin = "ANDROID";
            androidId = GlobalData.getSharedGlobalData().getAndroidId();
            userId = gd.getUser().getLogin_id();
        }
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

}
