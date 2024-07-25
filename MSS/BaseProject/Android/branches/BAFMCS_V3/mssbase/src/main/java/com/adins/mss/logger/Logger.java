package com.adins.mss.logger;

import android.util.Log;

import com.adins.mss.constant.Global;

/**
 * Created by angga.permadi on 4/5/2016.
 */
public class Logger {

    private static final String TAG = "com.adins.mss";

    public static void d(Object object, String message) {
        Log.d(getTag(object), message);
    }

    public static void d(String tag, String message) {
        if (!Global.IS_DEV) return;
        Log.d(TAG, message);
    }

    public static void e(Object object, String message) {
        Log.e(getTag(object), message);
    }

    public static void i(Object object, String message) {
        Log.i(getTag(object), message);
    }

    public static void printStackTrace(Exception e) {
        e.printStackTrace();
    }

    private static String getTag(Object object) {
        return object != null ? object.getClass().getSimpleName() : TAG;
    }

}
