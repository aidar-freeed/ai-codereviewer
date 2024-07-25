package com.adins.mss.foundation.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.authentication.Authentication;
import com.adins.mss.base.commons.CommonImpl;
import com.adins.mss.base.commons.SecondHelper;
import com.adins.mss.base.commons.SubmitResult;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.util.ByteFormatter;
import com.adins.mss.base.util.SecondFormatter;
import com.adins.mss.base.util.UserSession;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Contact;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DialogManager {
    public static final int TYPE_ERROR = 0;
    public static final int TYPE_INFO = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_SUCCESS = 3;
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private static final String OK_LABEL = "OK";
    private static boolean isForceLogoutDialogShowing;
    /**
     * To check gps enabled and create an alert to force user to enable gps
     *
     * @param activity
     * @author bong.rk
     */
    public static NiftyDialogBuilder ndb;
    public static NiftyDialogBuilder ndb2;
    private static String TAG = "DIALOG_MANAGER";
    private static SlideDateTimeListener dtmListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date.getTime());
            cal.set(cal.SECOND, 0);
            SimpleDateFormat mFormatter = new SimpleDateFormat(Global.DATE_TIME_STR_FORMAT);
            String result = mFormatter.format(cal);
            DynamicFormActivity.setTxtInFocusText(result);
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {
            //EMPTY
        }
    };

    /**
     * @param context
     * @param dialogType TYPE_ERROR, TYPE_INFO, TYPE_WARNING
     * @param message
     * @param title,     you can set empty string
     */

    public static void showAlert(Context context, int dialogType, String message, String title) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        String frontText = null;

        switch (dialogType) {
            case TYPE_ERROR:
                frontText = "ERROR";
                break;
            case TYPE_INFO:
                frontText = "INFO";
                break;
            case TYPE_WARNING:
                frontText = "WARNING";
                break;
            case TYPE_SUCCESS:
                frontText = "SUCCESS";
                break;
            default:
                break;
        }

        alertDialog.setTitle(title);
        alertDialog.setMessage(frontText + ": " + message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, OK_LABEL, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // ** dikasih try, soalnya kadang saat lg mau nampilin,
        // orang yang megang hp nya pencet back or home,
        // so kalo dialog nya tampil dy bakal eror karena layar nya berganti.
        // 17 feb 2012
        try {
            alertDialog.show();
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                Log.i(TAG, "Show Dialog " + e);
        }

    }

    public static void showAlertNotif(Context context, String message, String title) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder.withTitle(title)
                .withMessage(message)
                .withButton1Text(OK_LABEL)
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialogBuilder.dismiss();
                    }
                })
                .isCancelable(true)
                .isCancelableOnTouchOutside(true)
                .show();
    }

    public static void showAlert(Context context, int type, String message,
                                 DialogInterface.OnClickListener listener, String title) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        String frontText = null;

        switch (type) {
            case TYPE_ERROR:
                frontText = "ERROR";
                break;
            case TYPE_INFO:
                frontText = "INFO";
                break;
            case TYPE_WARNING:
                frontText = "WARNING";
                break;
            case TYPE_SUCCESS:
                frontText = "SUCCESS";
                break;
            default:
                break;
        }
        alertDialog.setTitle(title);
        alertDialog.setMessage(frontText + ": " + message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, OK_LABEL, listener);
        alertDialog.show();
    }

    public static void showOptimizeDialog(final Context activity) {
        try {
            ndb = NiftyDialogBuilder.getInstance(activity);
            ndb.withTitle(activity.getString(R.string.optimize_battery))
                    .withMessage(activity.getString(R.string.optimize_battery_alert))

                    .withButton1Text(activity.getString(R.string.btnYes))
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ndb.dismiss();
                            Intent settingsIntent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                            activity.startActivity(settingsIntent);
                        }
                    });
            ndb.isCancelable(true);
            ndb.show();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    public static void showImageDialog(Context context, Bitmap image) {
        NiftyDialogBuilder_PL pl = NiftyDialogBuilder_PL.getInstance(context);
        pl.withNoTitle().withNoMessage().withTransparentBackground().withImageView(image).show();
    }

    public static void showExitAlertQuestion(final Activity activity, String message) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
        dialogBuilder.withTitle(activity.getString(R.string.btnExit))
                .withMessage(message)
                .withButton1Text(activity.getString(R.string.btnYes))
                .withButton2Text(activity.getString(R.string.btnNo))
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialogBuilder.dismiss();
                        activity.finish();
                        GlobalData.getSharedGlobalData().setDoingTask(false);
                    }
                })
                .setButton2Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .isCancelable(false)
                .isCancelableOnTouchOutside(true)
                .show();
    }

    public static void showExitAlert(final Activity activity, String message) {
        if (Global.isIsUploading()) {
            //param, jika masih uoloading gak boleh exit
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
            dialogBuilder.withTitle(activity.getString(R.string.btnExit))
                    .withMessage(activity.getString(R.string.msgStillUploading))
                    .withButton1Text(OK_LABEL)
                    .setButton1Click(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else {
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
            dialogBuilder.withTitle(activity.getString(R.string.btnExit))
                    .withMessage(message)
                    .withButton1Text(activity.getString(R.string.btnYes))
                    .withButton2Text(activity.getString(R.string.btnNo))
                    .setButton1Click(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            dialogBuilder.dismiss();
                            if (NewMainActivity.AutoSendLocationHistoryService != null) {
                                activity.stopService(NewMainActivity.AutoSendLocationHistoryService);
                            }
                            if (NewMainActivity.RunNotificationService != null)
                                activity.stopService(NewMainActivity.RunNotificationService);
                            try {
                                Formatter.clearFormatter();
                                CommonImpl.clearFormatter();
                                UserSession.clear();

                                ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(activity,
                                        "GlobalData", Context.MODE_PRIVATE);

                                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                sharedPrefEditor.remove("HAS_LOGGED");
                                sharedPrefEditor.commit();
                                Authentication.setAsNonFreshInstall(LocationTrackingManager.getContextLocation());
                                activity.sendBroadcast(new Intent(activity.getString(R.string.action_user_logout)));
                            } catch (Exception e) {
                                FireCrash.log(e);
                            }
                            activity.finish();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.exit(0);
                                }
                            }, 1000);
                        }
                    })
                    .setButton2Click(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        }
    }

    public static void showForceExitAlert(final Context context, String message) {
        if(!(context instanceof Activity)){
            //cannot show dialog if context is not activity.
            return;
        }

        Activity activity = (Activity)context;
        if(activity.isFinishing() || activity.isDestroyed())
            return;

        if(isForceLogoutDialogShowing){
            return;
        }

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder.withTitle(context.getString(R.string.btnExit))
                .withMessage(message)
                .withButton1Text(context.getString(R.string.btnYes))
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        isForceLogoutDialogShowing = false;
                        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                        if (!Global.APPLICATION_ORDER.equals(application) && NewMainActivity.AutoSendLocationHistoryService != null) {
                            context.stopService(NewMainActivity.AutoSendLocationHistoryService);
                        }
                        if (NewMainActivity.RunNotificationService != null)
                            context.stopService(NewMainActivity.RunNotificationService);
                       try {
                            UserSession.clear();

                            ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context,
                                    "GlobalData", Context.MODE_PRIVATE);

                            ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                            sharedPrefEditor.remove("HAS_LOGGED");
                            sharedPrefEditor.commit();
                            Authentication.setAsNonFreshInstall(context);

                           ((Activity)context).finishAffinity();
                            dialogBuilder.dismiss();
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.exit(0);
                            }
                        }, 1000);
                    }
                })
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .show();
                isForceLogoutDialogShowing = true;
    }

    public static void showForceExitAlert(final Activity activity, String message) {
        if(activity.isFinishing() || activity.isDestroyed())
            return;

        if(isForceLogoutDialogShowing){
            return;
        }

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
        dialogBuilder.withTitle(activity.getString(R.string.btnExit))
                .withMessage(message)
                .withButton1Text(activity.getString(R.string.btnYes))
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialogBuilder.dismiss();
                        isForceLogoutDialogShowing = false;
                        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                        if (!Global.APPLICATION_ORDER.equals(application) && NewMainActivity.AutoSendLocationHistoryService != null) {
                            activity.stopService(NewMainActivity.AutoSendLocationHistoryService);
                        }
                        if (NewMainActivity.RunNotificationService != null)
                            activity.stopService(NewMainActivity.RunNotificationService);
                        try {
                            UserSession.clear();

                            ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(activity,
                                    "GlobalData", Context.MODE_PRIVATE);

                            ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                            sharedPrefEditor.remove("HAS_LOGGED");
                            sharedPrefEditor.commit();
                            Authentication.setAsNonFreshInstall(LocationTrackingManager.getContextLocation());

                            activity.sendBroadcast(new Intent(activity.getString(R.string.action_user_logout)));
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }

                        activity.finishAffinity();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.exit(0);
                            }
                        }, 1000);
                    }
                })
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .show();
                isForceLogoutDialogShowing = true;
    }

    public static void showForceSyncronize(final NewMainActivity activity) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
        dialogBuilder.withTitle(activity.getString(R.string.sync_dialog))
                .withMessage(R.string.sync_dialog_message)
                .withButton1Text(activity.getString(R.string.btnYes))
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialogBuilder.dismiss();
                        activity.gotoSynchronize();
                    }
                })
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .show();
    }

    public static void showDateTimePicker(FragmentActivity activity) {
        new SlideDateTimePicker.Builder(activity.getSupportFragmentManager())
                .setListener(dtmListener)
                .setInitialDate(new Date())
                .setIs24HourTime(true)
                .build()
                .show();
    }

    public static void showGPSAlert(final Context activity) {
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (lm.getProvider(LocationManager.GPS_PROVIDER) != null) {
                final boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gpsEnabled) {
                    closeGPSAlert();
                    ndb = NiftyDialogBuilder.getInstance(activity);
                    ndb.withTitle(activity.getString(R.string.gps_unable))
                            .withMessage(activity.getString(R.string.gps_warning))
                            .withButton1Text(activity.getString(R.string.gps_button))
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ndb.dismiss();
                                    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    activity.startActivity(settingsIntent);

                                }
                            });
                    ndb.isCancelable(false);
                    ndb.show();
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
        try {
           if (lm.getProvider(LocationManager.GPS_PROVIDER) != null) {
                final boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        boolean isMock;
                        if (android.os.Build.VERSION.SDK_INT >= 18) {
                            isMock = location.isFromMockProvider();
                        } else {
                            isMock = Settings.Secure.getString(LocationTrackingManager.getContextLocation().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
                        }
                        showMockDialog(gpsEnabled, isMock, activity);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        //EMPTY
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        //EMPTY
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        //EMPTY
                    }
                };
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    private static void showMockDialog(Boolean gpsEnabled, Boolean isMock, final Context activity) {
        if (gpsEnabled && isMock) {
            closeGPSAlert();
            ndb = NiftyDialogBuilder.getInstance(activity);
            ndb.withTitle(activity.getString(R.string.mock_location))
                    .withMessage(activity.getString(R.string.mock_location_alert))

                    .withButton1Text(activity.getString(R.string.dev_option))
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ndb.dismiss();
                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                            activity.startActivity(settingsIntent);

                        }
                    });
            ndb.isCancelable(false);
            ndb.show();
        }
    }

    public static void showMockDialog(final Context activity) {
        try {
            closeGPSAlert();
            ndb = NiftyDialogBuilder.getInstance(activity);
            ndb.withTitle(activity.getString(R.string.mock_location))
                    .withMessage(activity.getString(R.string.mock_location_alert))

                    .withButton1Text(activity.getString(R.string.dev_option))
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ndb.dismiss();
                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                            activity.startActivity(settingsIntent);

                        }
                    });
            ndb.isCancelable(false);
            ndb.show();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    public static void closeGPSAlert() {
        if (ndb != null)
            ndb.dismiss();
    }

    public static boolean checkPlayServices(Activity activity) {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, status,
                        REQUEST_CODE_RECOVER_PLAY_SERVICES);
                dialog.setCancelable(false);
                dialog.show();
            } else {
                Toast.makeText(activity, activity.getString(R.string.device_not_supported),
                        Toast.LENGTH_LONG).show();
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static void uninstallAPK(Context context) {
        String packageName = context.getPackageName();
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }

    public static void UninstallerHandler(final FragmentActivity activity) {
        String message = activity.getString(R.string.inactive_user, GlobalData.getSharedGlobalData().getUser().getFullname());
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
        dialogBuilder.withTitle(activity.getString(R.string.warning_capital))
                .withMessage(message)
                .withButton1Text("Uninstall")
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialogBuilder.dismiss();
                        String packageName = activity.getPackageName();
                        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        intent.setData(Uri.parse("package:" + packageName));
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                        activity.startActivityForResult(intent, 1);
                        activity.sendBroadcast(new Intent(activity.getString(R.string.action_user_logout)));
                    }
                })
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .show();
    }

    public static void UninstallerHandler(final Activity activity) {
        String message = activity.getString(R.string.inactive_user, GlobalData.getSharedGlobalData().getUser().getFullname());
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
        dialogBuilder.withTitle(activity.getString(R.string.warning_capital))
                .withMessage(message)
                .withButton1Text("Uninstall")
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialogBuilder.dismiss();
                        String packageName = activity.getPackageName();
                        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        intent.setData(Uri.parse("package:" + packageName));
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                        activity.startActivityForResult(intent, 1);
                        activity.sendBroadcast(new Intent(activity.getString(R.string.action_user_logout)));
                    }
                })
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .show();
    }

    public static void showRootAlert(final Activity activity, final Context context) {
        try {
            ndb2 = NiftyDialogBuilder.getInstance(activity);
            ndb2.withTitle(activity.getResources().getString(R.string.device_rooted))
                    .withMessage(activity.getResources().getString(R.string.device_rooted_uninstall))
                    .withButton1Text(activity.getResources().getString(R.string.uninstall_apk))
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ndb2.dismiss();
                            uninstallAPK(context);
                        }
                    });
            ndb2.isCancelable(false);
            ndb2.show();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    public static boolean isTimeAutomatic(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    public static void showTimeProviderAlert(final Activity activity) {
        try {
            if (!isTimeAutomatic(activity)) {
                closeGPSAlert();
                ndb = NiftyDialogBuilder.getInstance(activity);
                ndb.withTitle(activity.getString(R.string.time_unable))
                        .withMessage(activity.getString(R.string.time_warning))
                        .withButton1Text(activity.getString(R.string.time_button))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ndb.dismiss();
                                Intent settingsIntent = new Intent(Settings.ACTION_DATE_SETTINGS);
                                activity.startActivity(settingsIntent);

                            }
                        });
                ndb.isCancelable(false);
                ndb.show();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    public static void showTurnOffDevMode(final Context context) {
        try {
            ndb = NiftyDialogBuilder.getInstance(context);
            ndb.withTitle(context.getString(R.string.title_developer_mode))
                    .withMessage(context.getString(R.string.text_turn_off_dev_mode))
                    .withButton1Text(context.getString(R.string.btnSetting))
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ndb.dismiss();
                            Intent setting = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                            context.startActivity(setting);
                        }
                    });
            ndb.isCancelable(false);
            ndb.isCancelableOnTouchOutside(false);
            ndb.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAskForDownloadDialog(final Activity activity) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
        builder.withTitle(activity.getString(R.string.get_services))
                .withMessage(activity.getString(R.string.get_services_message))
                .withButton1Text(activity.getString(R.string.get_services))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                       Intent download = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.download_services)));
                        activity.startActivity(download);
                    }

                }).show();
    }

    public static void submitResult(final Activity activity, final SubmitResult submitResult) {
        String msgResult = submitResult.getResult();

        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(activity)
                .setView(R.layout.new_dialog_send_result)
                .create();
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.windowAnimations = R.style.DialogAnimation2;
        dialog.show();

        ImageView icon = (ImageView) dialog.findViewById(R.id.imgHeader);
        Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
        Button btnPrint = (Button) dialog.findViewById(R.id.btnPrintPage);
        TextView txtResult = (TextView) dialog.findViewById(R.id.txtResult);
        TextView txtTime = (TextView) dialog.findViewById(R.id.txtTimeSent);
        TextView txtSize = (TextView) dialog.findViewById(R.id.txtDataSize);

        Scheme scheme = SchemeDataAccess.getOne(activity, submitResult.getTaskH().getUuid_scheme());
        if (scheme != null) {
            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            boolean isTaskPaid = TaskDDataAccess.isTaskPaid(activity, uuidUser, submitResult.getTaskH().getUuid_task_h());
            boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(activity, uuidUser);
            if (isRVinFront) {
                btnPrint.setVisibility(View.GONE);
            } else if (!scheme.getIs_printable().equals("1") || !isTaskPaid) {
                btnPrint.setVisibility(View.GONE);
            } else {
                btnPrint.setVisibility(View.VISIBLE);
            }
        }

        if (msgResult != null) {
            if (msgResult.equalsIgnoreCase("Success")) {
                String time = submitResult.getTaskH().getSubmit_duration();
                String size = submitResult.getTaskH().getSubmit_size();

                String seconds = SecondFormatter.secondsToString(Long.parseLong(time));
                String mTime = activity.getString(com.adins.mss.base.R.string.time) + seconds;

                String bytes = ByteFormatter.formatByteSize(Long.parseLong(size));
                String mSize = activity.getString(com.adins.mss.base.R.string.size) + bytes;

                txtTime.setText(mTime);
                txtSize.setText(mSize);
            } else {
                icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_failed));
                txtResult.setText(submitResult.getTaskH().getMessage());
                txtTime.setVisibility(View.GONE);
                txtSize.setVisibility(View.GONE);
                btnPrint.setVisibility(View.GONE);
            }
        } else {
            icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_failed));
            txtResult.setText(activity.getString(R.string.message_sending_failed));
            txtTime.setVisibility(View.GONE);
            txtSize.setVisibility(View.GONE);
            btnPrint.setVisibility(View.GONE);
        }

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt(NewMainActivity.KEY_ACTION, NewMainActivity.ACTION_TIMELINE);
                msg.setData(bundle);

                NewMainActivity.submitHandler.sendMessage(msg);
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TaskH taskH = TaskHDataAccess.getOneTaskHeader(activity, submitResult.getTaskId());
                if (taskH.getRv_number() != null && !taskH.getRv_number().isEmpty()) {
                    Toast.makeText(activity, R.string.cantPrint, Toast.LENGTH_SHORT).show();
                } else {
                    SecondHelper.Companion.doPrint(activity, submitResult.getTaskId(), "log");
                }
            }
        });
    }

    public static void showDetailContact(final Activity activity, final Contact contact) {
        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.Dialog_NoTitle)
                .setView(R.layout.dialog_contact_detail)
                .create();
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        wmlp.windowAnimations = R.style.DialogAnimation;
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView contactName = (TextView) dialog.findViewById(R.id.txtName);
        TextView contactDept = (TextView) dialog.findViewById(R.id.txtDepartment);
        TextView contactPhone = (TextView) dialog.findViewById(R.id.txtPhone);
        TextView contactEmail = (TextView) dialog.findViewById(R.id.txtEmail);
        ImageButton btnCall = (ImageButton) dialog.findViewById(R.id.btnCall);

        contactName.setText(contact.getContact_name());
        contactDept.setText(contact.getContact_dept());
        contactPhone.setText(contact.getContact_phone());
        contactEmail.setText(contact.getContact_email());

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +  contact.getContact_phone()));
                activity.startActivity(intent);
            }
        });
    }
    public static void showTaskRejected(Activity activity, String result){
        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(activity)
                .setView(R.layout.dialog_reject_success)
                .create();
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.windowAnimations = R.style.DialogAnimation2;
        dialog.show();

        TextView textMessage = dialog.findViewById(R.id.txtResult);
        Button btnOK = (Button) dialog.findViewById(R.id.btnOK);

        textMessage.setText(result);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
