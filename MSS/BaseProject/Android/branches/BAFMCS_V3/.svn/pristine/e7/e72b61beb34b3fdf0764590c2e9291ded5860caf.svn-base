package com.adins.mss.base.networkmonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.preference.PreferenceManager;

import com.adins.mss.foundation.camerainapp.helper.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by e5 on 18/08/2017.
 */

public class NetworkMonitorDataUsage {

    /**
     * Method ini digunakan untuk mengambil tanggal instalasi aplikasi
     *
     * @param context Instance activity yang sedang berjalan misal: this
     * @return mengembalikan String tanggal install aplikasi dengan format yang sudah ditentukan
     */
    private String getInstallDate(Context context) {

        PackageManager packageManager = context.getPackageManager();
        long installTimeInMilliseconds;

        Date installDate = null;
        String installDateString = null;

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            installTimeInMilliseconds = packageInfo.firstInstallTime;

            //sesuaikan format sdf dengan yang dibutuhkan
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
            Date date = new Date(installTimeInMilliseconds);
            installDateString = sdf.format(date);
        } catch (PackageManager.NameNotFoundException e) {
            //handler untuk error yang tidak terduga
            installDate = new Date(0);
            installDateString = installDate.toString();
        }

        return installDateString;
    }

    /**
     * Di bawah ini yang tidak membedakan rx tx
     *
     * @param context
     * @return
     */
    public long getDataUsage(Context context) {
        long rxtx;
        final PackageManager pm = context.getPackageManager();
        //ambil dafter aplikasi yang terinstall
        List<ApplicationInfo> packages = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);
        int UID = 0;
        //loop daftar aplikasi mencari yang cocok dengan nama aplikasi yang sedang berjalan
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(context.getPackageName())) {
                //mengambil UID aplikasi
                String appName = packageInfo.packageName;
                UID = packageInfo.uid;
                break;
            }
        }

        //mengambil total penggunaan data dalam satuan bytes
        rxtx = TrafficStats.getUidRxBytes(UID) + TrafficStats.getUidTxBytes(UID);//Tx = Transferred = Upload ;//Rx = Recieved = Download

        //handler apabila API level device terlalu rendah dan tidak support TrafficStats
        if (rxtx == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            //masukkan pesan disini
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        }
        return rxtx;
    }

    public void setDataLastDay(Context context, long data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("DataUsageperHari", data);// value rx to store
        editor.commit();
    }

    public void setDateLastDay(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        Logger.d("NetworkMonitor", "" + cal.getTimeInMillis());
        editor.putLong("ThisDateForDataUsage", cal.getTimeInMillis());
        editor.commit();
    }

    public long getDataLastDay(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong("DataUsageperHari", 0); // value rx to store
    }

    public long getLastDate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong("ThisDateForDataUsage", Calendar.getInstance().getTimeInMillis());
    }

    public void update(Context context, long data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("DataUsage", data); // value rx to store
        editor.commit();
    }

    public long getDataThisDay(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong("DataUsage", 0); // value rx to store
    }

    public void resetDataThisDay(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("DataUsage", 0); // value DU to store
        editor.commit();
    }
}
