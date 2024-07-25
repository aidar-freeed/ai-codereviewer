package com.adins.mss.foundation.phone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.adins.mss.base.crashlytics.FireCrash;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by gigin.ginanjar on 14/09/2016.
 */
public class TelephonyInfo {
    private static TelephonyInfo telephonyInfo;
    private String imsiSIM1;
    private String imsiSIM2;

    private TelephonyInfo() {
    }

    public static TelephonyInfo getInstance(Context context) {

        if (telephonyInfo == null) {

            telephonyInfo = new TelephonyInfo();

            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT <= 28) {

                telephonyInfo.imsiSIM1 = telephonyManager.getDeviceId();
                telephonyInfo.imsiSIM2 = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (telephonyManager.getPhoneCount() >= 2) { //Check is phone has 2 or more sim card
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            telephonyInfo.imsiSIM1 = telephonyManager.getImei(0);
                            telephonyInfo.imsiSIM2 = telephonyManager.getImei(1);
                        } else {
                            telephonyInfo.imsiSIM1 = telephonyManager.getDeviceId(0);
                            telephonyInfo.imsiSIM2 = telephonyManager.getDeviceId(1);
                        }
                    } else { //Phone only has single sim card
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            telephonyInfo.imsiSIM1 = telephonyManager.getImei();
                            telephonyInfo.imsiSIM2 = null;
                        }
                    }
                } else {
                    try {
                        telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
                        telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
                    } catch (GeminiMethodNotFoundException e) {
                        try {
                            telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                            telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
                        } catch (GeminiMethodNotFoundException e1) {
                            try {
                                telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getDeviceIdDs", 0);
                                telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getDeviceIdDs", 1);
                            } catch (GeminiMethodNotFoundException e2) {
                                try {
                                    telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getSimSerialNumberGemini", 0);
                                    telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getSimSerialNumberGemini", 1);
                                } catch (GeminiMethodNotFoundException e3) {
                                    //Call here for next manufacturer's predicted method name if you wish
                                    e3.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } else {
                telephonyInfo.imsiSIM1 = UUID.randomUUID().toString();
                telephonyInfo.imsiSIM2 = null;
            }
        }

        return telephonyInfo;
    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String imsi = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if (ob_phone != null) {
                imsi = ob_phone.toString();

            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imsi;
    }

    private static boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if (ob_phone != null) {
                int simState = Integer.parseInt(ob_phone.toString());
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    isReady = true;
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }

    public String getImeiSIM1() {
        return imsiSIM1;
    }

    public String getImeiSIM2() {
        return imsiSIM2;
    }

    public boolean isDualSIM() {
        return imsiSIM2 != null;
    }

    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}
