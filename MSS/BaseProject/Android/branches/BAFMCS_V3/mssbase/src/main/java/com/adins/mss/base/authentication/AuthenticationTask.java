package com.adins.mss.base.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.syncfile.FileSyncHelper;
import com.adins.mss.base.update.DownloadUpdate;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.MssResponseType;
import com.adins.mss.foundation.notification.Notification;

import java.io.File;

public class AuthenticationTask implements Authentication.AuthenticationHandler {
    public static final String LOGIN_PREFERENES = "login_preferences";
    public static onImportSuccess importSuccess;
    private ProgressDialog progressDialog;
    private FragmentActivity mContext;
    private Activity activity;

    public AuthenticationTask(FragmentActivity context) {
        this.mContext = context;
        Notification.getSharedNotification().clearNotifAll(mContext);
        progressDialog = ProgressDialog.show(
                mContext, "",
                context.getString(R.string.authentication_user), true);
        String username = GlobalData.getSharedGlobalData().getUser().getLogin_id();
        String password = GlobalData.getSharedGlobalData().getUser().getPassword();

        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            if (Global.IS_DEV)
                e.printStackTrace();
        }
        int buildNumber = 0;

        if(pInfo != null) {
            Global.BUILD_VERSION = pInfo.versionCode;
            Global.APP_VERSION = pInfo.versionName;
            buildNumber = Global.BUILD_VERSION;
        }

        Authentication.authenticateOnBackground(
                mContext,
                username,
                password,
                buildNumber,
                this);
    }

    public AuthenticationTask(Activity context) {
        this.activity = context;
        Notification.getSharedNotification().clearNotifAll(mContext);
        progressDialog = ProgressDialog.show(
                mContext, "",
                context.getString(R.string.authentication_user), true);
        String username = GlobalData.getSharedGlobalData().getUser().getLogin_id();
        String password = GlobalData.getSharedGlobalData().getUser().getPassword();

        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            if (Global.IS_DEV)
                e.printStackTrace();
        }

        int buildNumber = 0;
        if(pInfo != null){
            Global.BUILD_VERSION = pInfo.versionCode;
            Global.APP_VERSION = pInfo.versionName;
            buildNumber = Global.BUILD_VERSION;
        }

        Authentication.authenticateOnBackground(
                mContext,
                username,
                password,
                buildNumber,
                this);
    }

    @Override
    public void onConnectionFail(Authentication authentication,
                                 HttpConnectionResult httpConnectionResult) {
        closeProgress();

        //bong 6 may 15 - penjagaan message yang ditampilkan saat login dengan inactive user
        if (authentication == null) {
            if (httpConnectionResult != null) {
                Toast.makeText(
                        mContext,
                        httpConnectionResult.getResult(),
                        Toast.LENGTH_LONG
                ).show();
            } else {
                Toast.makeText(
                        mContext,
                        mContext.getString(R.string.connection_failed),
                        Toast.LENGTH_LONG
                ).show();
            }
            return;
        }
        //bong 7 may 15 - penjagaan jika belum login ke wifi
        MssResponseType responseFromServer = null;
        if (httpConnectionResult.isOK()) {
            responseFromServer = GsonHelper.fromJson(httpConnectionResult.getResult(), MssResponseType.class);

            if (responseFromServer.getStatus().getCode() == Global.STATUS_CODE_APPL_CLEANSING) {
                Toast.makeText(
                        mContext,
                        responseFromServer.getStatus().getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

        }

        Toast.makeText(
                mContext,
                mContext.getString(R.string.connection_failed_http) + httpConnectionResult.getStatusCode()
                        + mContext.getString(R.string.divider_vertical_bar) + httpConnectionResult.getResult(),
                Toast.LENGTH_LONG
        ).show();

    }

    @Override
    public void onLoginFail(Authentication auth, String message) {
        closeProgress();
        if (message.contains("Password") || message.contains("password") || message.contains("IMEI") || message.contains("imei")) {
            DialogManager.showForceExitAlert(mContext, mContext.getString(R.string.password_not_match));
        } else
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onForceUpdate(Authentication auth, String message,
                              String otaLink) {
        closeProgress();
        showForceUpdateDialog(otaLink);
    }

    private void showForceUpdateDialog(final String otaLink) {
        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(mContext)
                .withTitle(mContext.getString(R.string.server))
                .withMessage(mContext.getString(R.string.critical_update))
                .withButton1Text(mContext.getString(R.string.update)).setButton1Click(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                openUpdate(otaLink);
                            }
                        }
                ).isCancelable(false)
                .isCancelableOnTouchOutside(false);
        builder.show();

    }
    private void openUpdate(String otaLink) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File file = new File(mContext.getApplicationContext().getFilesDir(), "app.apk");
            if (file.exists()) {
                Uri apkURI = FileProvider.getUriForFile(
                        mContext.getApplicationContext(), mContext.getPackageName() + ".provider", file);
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(intent);
            } else {
                DownloadUpdate downloadUpdate = new DownloadUpdate(mContext);
                downloadUpdate.execute(otaLink);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onInactiveUser(Authentication auth) {
        closeProgress();
        DialogManager.UninstallerHandler(mContext);
    }

    @Override
    public void onLoginSuccess(Authentication authentication, String otaLink,
                               boolean needUpdatePassword, boolean pwdExp,
                               boolean needUpdateApplication, String message,
                               User authenticatedUser) {
        closeProgress();
        importSuccess = new onImportSuccess();
        if (needUpdateApplication) {
            showAskForUpdateDialog(otaLink);
            return;
        }

        GlobalData.getSharedGlobalData().setUser(authenticatedUser);
        FileSyncHelper.senderID = 1;
        FileSyncHelper.startFileSync(mContext);
    }

    private void goToSynchronize() {
        Utility.stopAllServices(mContext);
        Intent syncIntent = Global.syncIntent;
        mContext.startActivity(syncIntent);
        mContext.finish();
    }

    private void showAskForUpdateDialog(final String otaLink) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(mContext);
        builder.withTitle(mContext.getString(R.string.server)).isCancelableOnTouchOutside(false).isCancelable(false)
                .withMessage(mContext.getString(R.string.update_available))
                .withButton1Text(mContext.getString(R.string.later))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        FileSyncHelper.senderID = 1;
                        FileSyncHelper.startFileSync(mContext);
                    }
                })
                .withButton2Text(mContext.getString(R.string.update))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        openUpdate(otaLink);
                    }
                }).show();
    }

    private void closeProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public class onImportSuccess extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            boolean success = data.getBoolean("importSuccess", false);
            if (success) {
                goToSynchronize();
            }
        }
    }
}
