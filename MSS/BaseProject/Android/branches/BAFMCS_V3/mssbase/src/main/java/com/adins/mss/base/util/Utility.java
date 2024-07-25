package com.adins.mss.base.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.perf.metrics.HttpMetric;
import com.services.MainServices;
import com.services.MssJobScheduler;
import com.services.NotificationThread;
import com.tracking.LocationTrackingService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gigin.ginanjar on 25/02/2016.
 */
public class Utility {
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 6666;
    public static final int REQUEST_CODE_ASK_SINGLE_PERMISSIONS = 6667;
    private static boolean isShow = false;
    /**
     * Permissions required to access Location.
     */
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    public static void deleteCache(Context context) {
        try {
            File cache = context.getCacheDir();
            File appDir = new File(cache.getParent());
            if (appDir.exists()) {
                String[] children = appDir.list();
                for (String s : children) {
                    if (!s.equals("lib") && !s.equals("shared_prefs")) {
                        deleteDir(new File(appDir, s));
                        Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                    }
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }

    public static void freeMemory() {
        System.runFinalization();
        System.gc();
    }

    public static void stopAllServices(Activity activity) {
        try {
            if (NewMainActivity.AutoSendLocationHistoryService != null)
                activity.stopService(NewMainActivity.AutoSendLocationHistoryService);
            else
                activity.stopService(new Intent(activity, LocationTrackingService.class));

            if (NewMainActivity.RunNotificationService != null)
                activity.stopService(NewMainActivity.RunNotificationService);
            else
                activity.stopService(new Intent(activity, MainServices.class));

            if (Global.approvalNotivIntent != null)
                activity.stopService(Global.approvalNotivIntent);

            if (Global.verifyNotivIntent != null)
                activity.stopService(Global.verifyNotivIntent);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void checkPermission(final Activity activity, final String permission, String description) {
        if (activity.checkSelfPermission(
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (activity.shouldShowRequestPermissionRationale(
                    permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                String message = activity.getString(R.string.need_permision) + " " + description;
                final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
                showMessageOKCancel2(builder, activity, message,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                builder.dismiss();
                                activity.requestPermissions(new String[]{permission},
                                        REQUEST_CODE_ASK_SINGLE_PERMISSIONS);
                            }
                        }, null);
                return;
            } else {

                // No explanation needed, we can request the permission.

                activity.requestPermissions(
                        new String[]{permission},
                        REQUEST_CODE_ASK_SINGLE_PERMISSIONS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public static boolean checkPermissionResult(final Activity activity, String[] permissions, int[] grantResults) {
        Map<String, Integer> perms = new HashMap<String, Integer>();
        // Initial

        perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.ACCESS_BACKGROUND_LOCATION, PackageManager.PERMISSION_GRANTED);

        // Fill with results
        for (int i = 0; i < permissions.length; i++)
            perms.put(permissions[i], grantResults[i]);

        if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
                ) {
            // All Permissions Granted
            return true;
        } else {
            // Permission Denied
            isShow = true;
            if (activity == null) {
                return false;
            }
            String errorPermissionMessage = activity.getString(R.string.permission_required);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    perms.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                //TODO Change message and test permission requesta when android 11 device available at hand.
                //Message will be changed later to accommodate android 11, since there is no android 11 device on hand atm.
                errorPermissionMessage = activity.getString(R.string.background_location_permission_required);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    perms.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                errorPermissionMessage = activity.getString(R.string.background_location_permission_required);
            }
            final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
            showMessageOK2(builder, activity, errorPermissionMessage, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    isShow = false;
                    final Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + activity.getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    activity.startActivity(i);
                }
            });
            return false;
        }
    }

    public static void checkPermissionGranted(final Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    List<String> permissionsNeeded = new ArrayList<String>();

                    final List<String> permissionsList = new ArrayList<String>();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (!addPermission(activity, permissionsList, Manifest.permission.ACCESS_BACKGROUND_LOCATION) &&
                                !addPermission(activity, permissionsList, Manifest.permission.ACCESS_FINE_LOCATION) &&
                                !addPermission(activity, permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            permissionsNeeded.add("GPS");
                        }
                    } else {
                        if (!addPermission(activity, permissionsList, Manifest.permission.ACCESS_FINE_LOCATION) &&
                                !addPermission(activity, permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            permissionsNeeded.add("GPS");
                        }
                    }

                    if (!addPermission(activity, permissionsList, Manifest.permission.CAMERA)) {
                        permissionsNeeded.add("Camera");
                    }

                    if (!addPermission(activity, permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            && !addPermission(activity, permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        permissionsNeeded.add("Storage");
                    }
                    if (!addPermission(activity, permissionsList, Manifest.permission.READ_PHONE_STATE)) {
                        permissionsNeeded.add("Phone State");
                    }

                    if (permissionsList.size() > 0) {
                        if (permissionsNeeded.size() > 0) {
                            // Need Rationale
                            String message = activity.getString(R.string.need_permision) + " " + permissionsNeeded.get(0);
                            for (int i = 1; i < permissionsNeeded.size(); i++)
                                message = message + ", " + permissionsNeeded.get(i);
                            if (!isShow) {
                                final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
                                showMessageOKCancel2(builder, activity, message,
                                        new View.OnClickListener() {
                                            @TargetApi(Build.VERSION_CODES.M)
                                            @Override
                                            public void onClick(View v) {
                                                builder.dismiss();
                                                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                            }
                                        }, null);
                            }
                            return;
                        }
                        if (!isShow) {
                            activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        }
                    }
                }
            }
        });
    }

    private static boolean addPermission(Activity activity, List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!activity.shouldShowRequestPermissionRationale(permission))
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private static void showMessageOKCancel2(NiftyDialogBuilder builder, Activity activity, String message, View.OnClickListener okListener, View.OnClickListener cancelListener) {
        builder.withTitle(activity.getString(R.string.info_capital))
                .withMessage(message)
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .withButton1Text(activity.getString(R.string.btnOk))
                .setButton1Click(okListener);
        if (cancelListener != null) {
            builder.withButton2Text(activity.getString(R.string.btnCancel))
                    .setButton2Click(cancelListener);
        }

        builder.show();
    }

    private static void showMessageOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(activity.getString(R.string.btnOk), okListener)
                .setNegativeButton(activity.getString(R.string.btnCancel), cancelListener)
                .create()
                .show();
    }

    private static void showMessageOK2(NiftyDialogBuilder builder, Activity activity, String message, View.OnClickListener okListener) {
        builder.withTitle(activity.getString(R.string.info_capital))
                .withMessage(message)
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .withButton1Text(activity.getString(R.string.btnOk))
                .setButton1Click(okListener);
        builder.show();
    }

    private static void showMessageOK(Activity activity, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(activity.getString(R.string.btnOk), okListener)
                .create()
                .show();
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



    public static void checkInternalStorage(Context context) {
        boolean isFull = false;
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        statFs.restat(Environment.getDataDirectory().getPath());

        String freeSpace;
        double storageAvailable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            long bytesAvailable = statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
            storageAvailable = (double) bytesAvailable / Math.pow(1024, 2);
            freeSpace = Formatter.formatByteSize(bytesAvailable);
            Logger.d("availableMemory", freeSpace);
            if (storageAvailable < 300d) {
                isFull = true;
            }
        } else {
            long bytesAvailable = (long)(statFs.getBlockSize() * statFs.getAvailableBlocks());
            storageAvailable = (double) bytesAvailable / Math.pow(1024, 2);
            freeSpace = Formatter.formatByteSize(bytesAvailable);
            Logger.d("availableMemory", freeSpace);
            if (storageAvailable < 300d) {
                isFull = true;
            }
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference devices = database.getReference("devices");
        devices.child(GlobalData.getSharedGlobalData().getUser().getUuid_user()).setValue("Free Storage: " + storageAvailable + ", Desc: " + freeSpace);

        if (isFull) {
            Intent resultIntent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(NotificationThread.getNotificationIcon());
            builder.setContentTitle(context.getString(R.string.freeup_space));
            builder.setContentText(context.getString(R.string.freeup_space_detail));
            builder.setPriority(Notification.PRIORITY_DEFAULT);
            NotificationCompat.BigTextStyle inboxStyle =
                    new NotificationCompat.BigTextStyle();
            inboxStyle.setBigContentTitle(context.getString(R.string.freeup_space));
            inboxStyle.bigText(context.getString(R.string.freeup_space_detail));

            builder.setDefaults(android.app.Notification.DEFAULT_ALL);
            builder.setStyle(inboxStyle);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(9, builder.build());

        }
    }

    public static void scheduleJob(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ComponentName serviceComponent = new ComponentName(context, MssJobScheduler.class);
            JobInfo.Builder builder = null;
                builder = new JobInfo.Builder(0, serviceComponent);
            builder.setMinimumLatency(1000); // wait at least
            builder.setOverrideDeadline((long)(3 * 1000)); // maximum delay
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());
        }
    }

    public static boolean isRegexCheckValid(String text,String patternStr){
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static int getViewById(Context context, String id){
        return context.getResources().getIdentifier(id, "id", context.getPackageName());
    }

    public static int getDrawableById(Context context, String id){
        return context.getResources().getIdentifier(id, "drawable", context.getPackageName());
    }

    public static void metricStart(HttpMetric metric, String json){
        try {
            metric.start();
            metric.setRequestPayloadSize(json.getBytes().length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void metricStop(HttpMetric metric, HttpConnectionResult serverResult){
        metric.setResponseContentType("Application/json");
        metric.setResponsePayloadSize(serverResult.getResult().getBytes().length);
        metric.setHttpResponseCode(serverResult.getStatusCode());
        metric.stop();
    }

}
