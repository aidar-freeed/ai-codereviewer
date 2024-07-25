package com.adins.mss.base.commons;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.form.questions.QuestionsValidator;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.AuditDataTypeGenerator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by kusnendi.muhamad on 26/07/2017.
 */

public class CommonImpl extends Tool implements Common {
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

    private static String gigabyteSuffix = " Gigabytes";
    private static String megabyteSuffix = " Megabytes";
    private static String kilobyteSuffix = " Kilobytes";
    private static String byteSuffix = " Bytes";
    private static NumberFormat nf = new DecimalFormat("#,###.##");

    private static ThreadLocal dateFormatPool = new ThreadLocal();

    public static boolean findBinary(String binaryName) {
        boolean found = false;
        String[] places = {"/sbin/", "/system/bin/", "/system/xbin/",
                "/data/local/xbin/", "/data/local/bin/",
                "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
        for (String where : places) {
            if (new File(where + binaryName).exists()) {
                found = true;

                break;
            }
        }

        return found;
    }

    public static boolean dateIsToday(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate = null;
        try {
            tempDate = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date today = Tool.getSystemDate();

        return today.equals(tempDate);
    }

    public static Date resetDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        return cal.getTime();
    }

    @Override
    public void checkGPS(boolean value) {
        //EMPTY
    }

    @Override
    public void askForDownload(Activity activity, int code) {
        DialogManager.showAskForDownloadDialog(activity);
    }

    @Override
    public boolean checkPlayServices(Activity activity) {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity.getApplicationContext());
        if (status != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
                showErrorDialog(activity, status);
            } else {
                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.device_not_supported),
                        Toast.LENGTH_LONG).show();
                activity.finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void setAuditData() {
        AuditDataType tempAudit = AuditDataTypeGenerator.generateActiveUserAuditData();
        GlobalData.getSharedGlobalData().setAuditData(tempAudit);
    }

    @Override
    public boolean isInternetConnected(Context context) {
        return isInternetconnected(context);
    }

    void showErrorDialog(Activity activity, int code) {
        Dialog googlePlayDialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, code,
                REQUEST_CODE_RECOVER_PLAY_SERVICES);
        googlePlayDialog.setCancelable(false);
        googlePlayDialog.setCanceledOnTouchOutside(false);
        googlePlayDialog.show();
    }

    public void showGPSAlert(Activity activity) {
        try {
            DialogManager.showGPSAlert(activity);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    public boolean isAlphaNum(String text, int length) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        String answer = text.trim();
        boolean result = false;
        if (!answer.isEmpty() && QuestionsValidator.regexIsMatch(answer, regex)) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean checkIsRooted() {
        return findBinary("su");
    }

    public DateFormat getDateFormat(String format) {
        DateFormat result = null;
        HashMap dateFormats = (HashMap) dateFormatPool.get();
        if (dateFormats == null) {
            dateFormats = new HashMap<>();
            dateFormatPool.set(dateFormats);
        } else {
            result = (DateFormat) dateFormats.get(format);
        }
        if (result == null) {
            result = new SimpleDateFormat(format);
            dateFormats.put(format, result);
        }

        return result;
    }

    /**
     * Format specified java.util.Date date using format
     *
     * @param date   Date
     * @param format String
     * @return String
     */
    public String formatDate(Date date, String format) {
        return getDateFormat(format).format(date);
    }

    /**
     * Format specified java.util.Date dt using formatter to text String
     *
     * @param dt        Date
     * @param formatter DateFormat
     * @return String
     */
    public String formatDate(Date dt, DateFormat formatter) {
        return formatter.format(dt);
    }

    /**
     * Parse specified String dateStr using format
     *
     * @param dateStr String
     * @param format  String
     * @return Date
     * @throws ParseException
     */
    public Date parseDate(String dateStr, String format) throws ParseException {
        return getDateFormat(format).parse(dateStr);
    }


    public Date parseDate2(String dateStr, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    /**
     * Parse specified String dateStr using formatter to java.util.Date
     *
     * @param dateStr   String
     * @param formatter DateFormat
     * @return Date
     * @throws ParseException
     */
    public Date parseDate(String dateStr, DateFormat formatter) throws ParseException {
        return formatter.parse(dateStr);
    }

    /**
     * Converts a byte size into a human-readable string, such as "1.43 MB" or "27 KB".
     * The values used are based on powers of 1024, ie 1 KB = 1024 bytes, not 1000 bytes.
     *
     * @param byteSize the byte size of some item
     * @return a human-readable description of the byte size
     */
    public String formatByteSize(long byteSize) {
        String result = null;
        try {
            if (byteSize > Math.pow(1024, 3)) {
                // Report gigabytes
                result = nf.format(byteSize / Math.pow(1024, 3)) + gigabyteSuffix;
            } else if (byteSize > Math.pow(1024, 2)) {
                // Report megabytes
                result = nf.format(byteSize / Math.pow(1024, 2)) + megabyteSuffix;
            } else if (byteSize > 1024) {
                // Report kilobytes
                result = nf.format(byteSize / Math.pow(1024, 1)) + kilobyteSuffix;
            } else if (byteSize >= 0) {
                // Report bytes
                result = byteSize + byteSuffix;
            }
        } catch (NumberFormatException e) {
            return byteSize + byteSuffix;
        }
        return result;
    }

    //mengubah format long ke time "dd/MM/yyyy" --> bangkit 2011-10-26

    /**
     * change format Long Date to "dd/MM/yyyy"
     *
     * @param date
     * @return
     */
    public String dateToString(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DATE);

        return (d < 10 ? "0" : "") + d + "/" + (m < 10 ? "0" : "") + m
                + "/" + y;
    }

    // mengubah format long ke time "HH:mm" --> bangkit 2011-10-26

    /**
     * change format Long Date to HH:mm
     *
     * @param date
     * @return
     */
    public String timeToString(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);

        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m;
    }

    // mengubah format long ke time "HH:mm:ss" --> bangkit 2011-10-26

    /**
     * change format Long Date to HH:mm:ss
     *
     * @param date
     * @return
     */
    public String timeSecToString(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        int s = c.get(Calendar.SECOND);

        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "" + s);
    }

    public String dateTimeToString(long date) {
        return dateToString(date) + " " + timeToString(date);
    }

    public String dateTimeSecToString(long date) {
        return dateToString(date) + " " + timeSecToString(date);
    }


    // mengubah format string tanggal dd/MM/yyyy HH:mm ke date --> bangkit 2011-10-26
    // cth format nya "09/07/2011 12:45" atau "09/07/2011"
    // mengubah format long ke time "HH:mm:ss" --> bangkit 2011-10-26

    /**
     * Change format dd/MM/yyyy HH:mm to long Date.
     * eg :  "09/07/2011 12:45" or "09/07/2011" to Date
     *
     * @param s
     * @return
     */
    public long stringToDate(String s) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());

        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, 2)));
        c.set(Calendar.MONTH, Integer.parseInt(s.substring(3, 5)) - 1);
        c.set(Calendar.YEAR, Integer.parseInt(s.substring(06, 10)));
        if (s.length() > 10) {
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(11, 13)));
            c.set(Calendar.MINUTE, Integer.parseInt(s.substring(14, 16)));
        }

        return c.getTime().getTime();
    }

    /**
     * @param object
     * @return
     */
    public String getJsonFromObject(Object object) {
        return GsonHelper.toJson(object);
    }

    public String getDateTimeFormat(String type) {
        if (Global.AT_DATE.equals(type)) {
            return Global.DATE_STR_FORMAT;
        } else if (Global.AT_TIME.equals(type)) {
            return Global.TIME_STR_FORMAT;
        } else if (Global.AT_DATE_TIME.equals(type)) {
            return Global.DATE_TIME_STR_FORMAT;
        } else {
            return Global.DATE_STR_FORMAT;
        }
    }

    public Boolean stringToBoolean(String stringBoolean) {
        boolean b = false;
        if (stringBoolean.equals("0")) b = false;
        else b = stringBoolean.equals("1");
        return b;
    }

    public String booleanToString(boolean b) {
        String stringBoolean;
        if (b) stringBoolean = "1";
        else stringBoolean = "0";
        return stringBoolean;
    }

    @Override
    public void hideKeyboard(Activity activity) {
        try {
            InputMethodManager keyboard = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
        }
    }

    public static void clearFormatter(){
        dateFormatPool.remove();
    }
}
