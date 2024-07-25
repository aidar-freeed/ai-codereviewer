package com.adins.mss.foundation.http;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.phone.TelephonyInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class to generate AuditDataType with all needed data, if any. It's recommended to generate once and store to
 * singleton/global if used frequently
 *
 * @author glen.iglesias
 */
public class AuditDataTypeGenerator {

    public AuditDataTypeGenerator() {
    }

    /**
     * Generate AuditDataType object with specified username
     *
     * @param callerId to be set in the generated AuditDataType object
     * @return generated AuditDataType with specified username and loaded audit data
     */
    public static AuditDataType generateAuditDataType(String callerId) {
//		TelephonyManager tm  = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        AuditDataType auditDataType = new AuditDataType();

        //TODO
        String os = "ANDROID";
        String osVersion = String.valueOf(android.os.Build.VERSION.SDK_INT);
        String deviceId = GlobalData.getSharedGlobalData().getImei();
        String androidId = GlobalData.getSharedGlobalData().getAndroidId();
        String deviceModel = Build.MODEL;

        String application = GlobalData.getSharedGlobalData().getApplication();
        if (application == null) application = "";

        String applicationVersion = Global.APP_VERSION;
        String language = GlobalData.getSharedGlobalData().getLocale();

        auditDataType.setCallerId(callerId);
        auditDataType.setOs(os);
        auditDataType.setOsVersion(osVersion);
        auditDataType.setDeviceId(deviceId);
        auditDataType.setDeviceModel(deviceModel);
        auditDataType.setApplication(application);
        auditDataType.setApplicationVersion(applicationVersion);
        auditDataType.setLocale(language);
        auditDataType.setAndroidId(androidId);
        return auditDataType;
    }

    /**
     * Like generateAuditDataType, this method generate a new AuditDataType, but use active user uuid instead of parameter
     *
     * @return generated AuditDataType with active user uuid and loaded audit data
     */
    public static AuditDataType generateActiveUserAuditData() {
        User user = GlobalData.getSharedGlobalData().getUser();
        String uuid = "";
        if (user != null) {
            uuid = user.getUuid_user();
        }
        AuditDataType auditData = generateAuditDataType(uuid);
        return auditData;
    }

    /**
     * Read IMEI from device using TelephonyManager provided by Android SDK
     *
     * @param context used to get TelephonyManager
     * @return IMEI obtained from TelephonyManager
     */
    public static String getImeiFromDevice(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        return imei;
    }

    public static Map<String, String> getListImeiFromDevice(Context context) {
        TelephonyInfo tm = TelephonyInfo.getInstance(context);
        Map<String, String> imeiMap = new LinkedHashMap<>();

        String imei;
        if(Build.VERSION.SDK_INT > 28){
            imei = getAndroidId();
        }else {
            imei = tm.getImeiSIM1();
        }

        imeiMap.put(MssRequestType.UN_KEY_IMEI, imei);
        if (tm.isDualSIM()) {
            String imei2 = tm.getImeiSIM2();
            imeiMap.put(MssRequestType.UN_KEY_IMEI2, imei2);
        }
        return imeiMap;
    }

    public static String getAndroidId() {
        return Settings.Secure.getString(AppContext.getInstance().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

//	/**
//	 * @return
//	 */
//	public static AuditDataType generateAuditDataType(){
//		String callerId = "";
//		//user might be empty
//		try{
//			callerId = GlobalData.getSharedGlobalData().getUser().getLogin_id();
//		}
//		catch(Exception e){
//		}
//		AuditDataType auditDataType = generateAuditDataType(callerId);
//		return auditDataType;
//	}

}
