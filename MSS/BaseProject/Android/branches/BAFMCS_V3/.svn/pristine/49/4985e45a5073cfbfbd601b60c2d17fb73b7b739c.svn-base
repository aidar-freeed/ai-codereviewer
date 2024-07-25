package com.adins.mss.base.errorloghelper;

import com.adins.mss.dao.ErrorLog;
import com.adins.mss.foundation.db.dataaccess.ErrorLogDataAccess;
import com.adins.mss.foundation.formatter.Tool;

/**
 * Created by ACER 471 on 3/30/2017.
 */

public class ErrorLogHelper {

    private String uuidUser;
    private String errorMessage;
    private String deviceModel;

    public ErrorLogHelper(String uuidUser, String errorMessage, String deviceModel) {
        this.uuidUser = uuidUser;
        this.errorMessage = errorMessage;
        this.deviceModel = deviceModel;
    }

    public void addToTableLogError() {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setUuid_error_log(Tool.getUUID());
        errorLog.setError_description(getErrorMessage());
        errorLog.setDevice_name(getDeviceModel());
        errorLog.setDtm_activity(Tool.getSystemDateTime());
        errorLog.setUuid_user(getUuidUser());
        ErrorLogDataAccess.addOrReplace(null, errorLog);
    }

    public String getUuidUser() {
        return uuidUser;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDeviceModel() {
        return deviceModel;
    }
}
