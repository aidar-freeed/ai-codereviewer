package com.adins.mss.foundation.camerainapp.helper;

import android.util.Log;

import com.adins.mss.constant.Global;

/**
 * Created by angga.permadi on 7/26/2016.
 */
public class Logger {

    private static final String TAG = "com.adins.mss";

    public static void d(Object object, String message) {
        if (!Global.IS_DEV) return;

        Log.d(getTag(object), message);
    }

    public static void v(Object object, String message, Throwable e) {
        if (!Global.IS_DEV) return;

        Log.v(getTag(object), message, e);
    }

    public static void e(Object object, String message) {
        if (!Global.IS_DEV) return;

        Logger.e(getTag(object), message);
    }

    public static void e(Object object, Throwable e) {
        if (!Global.IS_DEV) return;

        Logger.e(getTag(object), getTag(object).toString(), e);
    }

    public static void w(Object object, String message) {
        if (!Global.IS_DEV) return;

        Log.w(getTag(object), message);
    }


    public static void e(Object object, String message, Throwable e) {
        if (!Global.IS_DEV) return;

        Logger.e(getTag(object), message, e);
    }

    public static void i(Object object, String message) {
        if (!Global.IS_DEV) return;

        Log.i(getTag(object), message);
    }

    public static void printStackTrace(Exception e) {
        if (!Global.IS_DEV) return;

        e.printStackTrace();
    }

    private static String getTag(Object object) {
        return object != null ? object.getClass().getSimpleName() : TAG;
    }

}
