package com.adins.mss.logger;

/**
 * Class for store all values of variables which needed to send when application crash
 *
 * @author bong.rk
 */
public class ErrorBean {
    private String timestamp;
    private String appVersionCode;
    private String appVersionName;
    private String packageName;
    private String phoneModel;
    private String brand;
    private String product;
    private String totalWebSize;
    private String availableMemSize;
    private String stackTrace;
    private String crashConfigutation;
    private String display;
    private String userAppStartDate;
    private String userCrashDate;
    private String dumpsysMeminfo;
    private String logcat;
    private String deviceId;
    private String installationId;
    private String sharedPreferences;
    private String settingsSystem;

    public ErrorBean() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTotalWebSize() {
        return totalWebSize;
    }

    public void setTotalWebSize(String totalWebSize) {
        this.totalWebSize = totalWebSize;
    }

    public String getAvailableMemSize() {
        return availableMemSize;
    }

    public void setAvailableMemSize(String availableMemSize) {
        this.availableMemSize = availableMemSize;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getCrashConfigutation() {
        return crashConfigutation;
    }

    public void setCrashConfigutation(String crashConfigutation) {
        this.crashConfigutation = crashConfigutation;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getUserAppStartDate() {
        return userAppStartDate;
    }

    public void setUserAppStartDate(String userAppStartDate) {
        this.userAppStartDate = userAppStartDate;
    }

    public String getUserCrashDate() {
        return userCrashDate;
    }

    public void setUserCrashDate(String userCrashDate) {
        this.userCrashDate = userCrashDate;
    }

    public String getDumpsysMeminfo() {
        return dumpsysMeminfo;
    }

    public void setDumpsysMeminfo(String dumpsysMeminfo) {
        this.dumpsysMeminfo = dumpsysMeminfo;
    }

    public String getLogcat() {
        return logcat;
    }

    public void setLogcat(String logcat) {
        this.logcat = logcat;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public String getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(String sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getSettingsSystem() {
        return settingsSystem;
    }

    public void setSettingsSystem(String settingsSystem) {
        this.settingsSystem = settingsSystem;
    }


}
