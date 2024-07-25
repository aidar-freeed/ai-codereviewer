package com.adins.mss.base.commons;

import android.app.Activity;
import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by kusnendi.muhamad on 26/07/2017.
 */

public interface Common {
    public void checkGPS(boolean value);

    public boolean checkIsRooted();

    public boolean checkPlayServices(Activity activity);

    public void askForDownload(Activity activity, int code);

    public void setAuditData();

    public boolean isInternetConnected(Context context);

    public boolean isAlphaNum(String text, int length);

    public String formatDate(Date date, String format);

    public String formatDate(Date dt, DateFormat formatter);

    public Date parseDate(String dateStr, String format) throws ParseException;

    public Date parseDate2(String dateStr, String format) throws ParseException;

    public Date parseDate(String dateStr, DateFormat formatter) throws ParseException;

    public String formatByteSize(long byteSize);

    public String dateToString(long date);

    public long stringToDate(String s);

    public String timeToString(long date);

    public String timeSecToString(long date);

    public String dateTimeToString(long date);

    public String dateTimeSecToString(long date);

    public String getDateTimeFormat(String type);

    public String getJsonFromObject(Object object);

    public Boolean stringToBoolean(String stringBoolean);

    public String booleanToString(boolean b);

    public void hideKeyboard(Activity activity);
}
