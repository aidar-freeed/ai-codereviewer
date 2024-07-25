package com.adins.mss.base.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.core.content.FileProvider;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.BuildConfig;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewChangePasswordActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.authentication.Authentication;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.syncfile.FileSyncHelper;
import com.adins.mss.base.update.DownloadUpdate;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.broadcast.SubmitSubscribedTopicTask;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.MssResponseType;
import com.adins.mss.foundation.oauth2.OAuth2Client;
import com.adins.mss.foundation.oauth2.Token;
import com.adins.mss.foundation.oauth2.store.SharedPreferencesTokenStore;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.messaging.FirebaseMessaging;

import org.acra.ACRA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Aditya Purwa on 1/6/2015.
 * Default implementation of login.
 */
public abstract class DefaultLoginModel extends LoginModel implements Authentication.AuthenticationHandler, IShowError {

    public static final String LOGIN_PREFERENCES = "login_preferences";
    public static final String LOGIN_PREFERENCES_USERNAME = "login_preferences.USERNAME";
    public static final String LOGIN_PREFERENCES_PASSWORD = "login_preferences.PASSWORD";
    public static final String LOGIN_PREFERENCES_REMEMBER_ME = "login_preferences.REMEMBER_ME";
    public static final String PWD_EXP = "passwordExpired";
    public static String tenantId;
    public static onImportSuccess importSuccess;
    private final ObscuredSharedPreferences loginPreferences;
    private String username;
    private String password;
    private boolean isRememberMe;
    private ProgressDialog progressDialog;
    private ErrorMessageHandler errorMessageHandler;

    /**
     * Initialize a new instance of context model.
     *
     * @param context The context for the model. Must be an activity.
     */
    public DefaultLoginModel(Context context) {
        super(context);
        loginPreferences = ObscuredSharedPreferences.getPrefs(context, LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        errorMessageHandler = new ErrorMessageHandler();
        errorMessageHandler.setiShowError(this);
    }

    public static void showGPSAlert(final Activity activity) {
        try {
            LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            if (lm.getProvider(LocationManager.GPS_PROVIDER) != null) {
                final boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gpsEnabled) {
                    final NiftyDialogBuilder ndb = NiftyDialogBuilder.getInstance(activity);
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
    }

    boolean isRememberMe() {
        return isRememberMe;
    }

    public void setRememberMe(boolean isRememberMe) {
        this.isRememberMe = isRememberMe;
    }

    String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTenantId(String username) {
        String[] idNtenant = Tool.split(username, "@");
        if (idNtenant.length > 1)
            tenantId = idNtenant[1];
    }

    @SuppressLint("NewApi")
    @Override
    public boolean login() {

        if (!ValidateInput()) {
            Toast.makeText(getContext(), getContext().getString(R.string.login_mandatory), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (GlobalData.getSharedGlobalData().isRequiresAccessToken()) {
            getAccessTokenAndLogin(getContext());
        } else {
            doLogin();
        }
        return false;
    }

    private void getAccessTokenAndLogin(final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.please_wait), true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                String urlService = GlobalData.getSharedGlobalData().getUrlMain();
                int idx = urlService.indexOf("/services");
                String urlMain = urlService.substring(0, idx);
                OAuth2Client client = new OAuth2Client(getUsername(), getPassword(), GlobalData.getSharedGlobalData().getClientId(), null, urlMain);
                GlobalData.getSharedGlobalData().setoAuth2Client(client);
                try {
                    Token token = client.getAccessToken(context);
                    result = "success";
                    if (result.equals("success")) {
                        SharedPreferencesTokenStore tokenStore = new SharedPreferencesTokenStore(context);
                        tokenStore.store(GlobalData.getSharedGlobalData().getoAuth2Client().getUsername(), token);
                        GlobalData.getSharedGlobalData().setToken(token);
                    }
                } catch (RuntimeException ex) {
                    result = ex.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = "Token Failed to store in Shared Preferences";
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                closeProgress();
                if (result.equals("success")) {
                    doLogin();
                } else {
                    //intishar: change error message mechanism
                    if(errorMessageHandler != null)
                        errorMessageHandler.processError("",result, ErrorMessageHandler.TOAST_TYPE);
                }
            }
        }.execute();
    }

    protected void doLogin() {
        ObscuredSharedPreferences.Editor loginPreferencesEditor = loginPreferences.edit();
        if (isRememberMe()) {
            loginPreferencesEditor.putString(LOGIN_PREFERENCES_USERNAME, getUsername());
            loginPreferencesEditor.putString(LOGIN_PREFERENCES_PASSWORD, getPassword());
            loginPreferencesEditor.putBoolean(LOGIN_PREFERENCES_REMEMBER_ME, isRememberMe());
            loginPreferencesEditor.apply();
        } else {
            loginPreferencesEditor.clear();
            loginPreferencesEditor.apply();
        }
        try {
            if (Tool.isInternetconnected(getContext())) {
                setTenantId(getUsername());
                progressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.please_wait), true);
                progressDialog.show();
                Global.IS_LOGIN = true;
                Authentication.authenticateOnBackground(
                        getContext(),
                        getUsername(),
                        getPassword(),
                        getBuildNumber(),
                        this);
            } else {
                Toast.makeText(getContext(), getContext().getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected abstract int getBuildNumber();

    private boolean ValidateInput() {
        return
                (getUsername() != null && getPassword() != null) &&
                        (getUsername().length() > 0 && getPassword().length() > 0);
    }

    @Override
    public boolean exit() {
        getContextAsActivity().finish();
        return false;
    }

    @Override
    public void onConnectionFail(Authentication authentication, HttpConnectionResult httpConnectionResult) {
        closeProgress();
        //bong 6 may 15 - penjagaan message yang ditampilkan saat login dengan inactive user
        if (authentication == null) {
            //intishar: change error message mechanism
            if(errorMessageHandler != null)
                errorMessageHandler.processError(httpConnectionResult, httpConnectionResult.getResult(), ErrorMessageHandler.TOAST_TYPE);
            return;
        }
        //bong 7 may 15 - penjagaan jika belum login ke wifi
        MssResponseType responseFromServer = null;
        if (httpConnectionResult.isOK()) {
            responseFromServer = GsonHelper.fromJson(httpConnectionResult.getResult(), MssResponseType.class);

            if (responseFromServer.getStatus().getCode() == Global.STATUS_CODE_APPL_CLEANSING) {
                Toast.makeText(
                        getContext(),
                        responseFromServer.getStatus().getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

        }

        //intishar: change error message mechanism
        if(errorMessageHandler != null)
            errorMessageHandler.processError(httpConnectionResult, httpConnectionResult.getResult(), ErrorMessageHandler.TOAST_TYPE);
    }

    @Override
    public void onLoginFail(Authentication authentication, String message) {
        closeProgress();
        if (message.contains("Android ID") || message.contains("android ID")) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserHelp.showUserHelp(getContextAsActivity(), getContextAsActivity().findViewById(R.id.androidId),
                            getContext().getString(R.string.androidIdnotregistered));
                }
            }, 2000);
        } else
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    public void onForceUpdate(Authentication authentication, String message, String otaLink) {
        closeProgress();
        showForceUpdateDialog(otaLink);
    }

    private void showForceUpdateDialog(final String otaLink) {
        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getContext())
                .withTitle(getContext().getString(R.string.server))
                .withMessage(getContext().getString(R.string.critical_update))
                .withButton1Text(getContext().getString(R.string.update)).setButton1Click(
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
            File file = new File(context.getFilesDir(), "app.apk");
            if(file.exists()){
            Uri apkURI = FileProvider.getUriForFile(
                    context, context.getPackageName() + ".provider", file);
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getContextAsActivity().startActivity(intent);
            } else{
                DownloadUpdate downloadUpdate = new DownloadUpdate(context);
                downloadUpdate.execute(otaLink);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onInactiveUser(Authentication authentication) {
        try {
            closeProgress();
            String fullname = GlobalData.getSharedGlobalData().getUser() != null ? GlobalData.getSharedGlobalData().getUser().getFullname() : "";
            if(fullname == null || fullname.equals("")){
                fullname = Authentication.getTemp_loginID();
                if(fullname.contains("@")){
                    fullname = fullname.substring(0,fullname.indexOf('@'));
                }
            }
            String message = getContext().getString(R.string.inactive_user, fullname);
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            DialogManager.uninstallAPK(getContext());
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public void onLoginSuccess(Authentication authentication, String otaLink, boolean needUpdatePassword,
                               boolean pwdExp, boolean needUpdateApplication,
                               String message, User authenticatedUser) {

        //check if activity context has destroyed
        Activity activity = (Activity)context;
        if(activity.isFinishing()){
            return;
        }

        closeProgress();
        importSuccess = new onImportSuccess();
        GlobalData.getSharedGlobalData().setUser(authenticatedUser);

        if (needUpdateApplication) {
            showAskForUpdateDialog(otaLink, needUpdatePassword, pwdExp);
            return;
        }

        if (needUpdatePassword || pwdExp) {
            forceUpdatePassword(pwdExp);
            return;
        }

        FileSyncHelper.senderID = 0;
        FileSyncHelper.startFileSync(getContext());
    }

    private void forceUpdatePassword(boolean pwdExp) {
        loginPreferences.edit().putBoolean("is_expired", true)
                .apply();

        Intent intent = new Intent(getContext(), NewChangePasswordActivity.class);
        if (pwdExp)
            intent.putExtra(PWD_EXP, "1");
        else intent.putExtra(PWD_EXP, "0");

        getContext().startActivity(intent);
    }

    public void closeProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void goToSynchronize() {
        doSubscribe();
        Intent syncIntent = getIntentSynchronize();
        getContext().startActivity(syncIntent);
    }

    protected abstract Intent getIntentSynchronize();

    private void showAskForUpdateDialog(final String otaLink, final boolean needUpdatePassword, final boolean pwdExp) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getContext());
        builder.withTitle(getContext().getString(R.string.server)).isCancelableOnTouchOutside(false).isCancelable(false)
                .withMessage(getContext().getString(R.string.update_available))
                .withButton1Text(getContext().getString(R.string.later))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        if (needUpdatePassword || pwdExp) {
                            forceUpdatePassword(pwdExp);
                        } else {
                            FileSyncHelper.senderID = 0;
                            FileSyncHelper.startFileSync(getContext());
                        }
                    }
                })
                .withButton2Text(getContext().getString(R.string.update))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        openUpdate(otaLink);
                    }
                }).show();
    }

    public void doSubscribe() {
        try {
            List<String> topicList = new ArrayList<>();
            User user = GlobalData.getSharedGlobalData().getUser();
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            String buildConfig = BuildConfig.FLAVOR.toUpperCase();
            if (buildConfig.length() == 0) {
                buildConfig = Global.FLAVORS;
            }
            String usedTenant = "";
            String userLogin = user.getLogin_id().split("@")[0];
            if (user.getLogin_id().contains("@")) {
                String tenant = user.getLogin_id().split("@")[1];
                if (null != tenant && !"".equalsIgnoreCase(tenant)) {
                    usedTenant = "-" + tenant.toUpperCase();
                }
            }

            String topicIndividu = "INDIVIDU-" + userLogin.toUpperCase() + "-" + buildConfig + usedTenant;
            FirebaseMessaging.getInstance().subscribeToTopic(topicIndividu);
            topicList.add(topicIndividu);
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                String topicAllMC = "ALL-MC-" + buildConfig + usedTenant;
                FirebaseMessaging.getInstance().subscribeToTopic("ALL-MC-" + buildConfig + usedTenant);
                topicList.add(topicAllMC);
                if (null != user.getUuid_branch()) {
                    String branchMC = "BRANCH-" + user.getUuid_branch() + "-" + buildConfig + usedTenant;
                    FirebaseMessaging.getInstance().subscribeToTopic(branchMC);
                    topicList.add(branchMC);
                }
                if (null != user.getUuid_group()) {
                    String[] listGroup = user.getUuid_group().split(";");
                    for (int i = 0; i < listGroup.length; i++) {
                        String groupMC = "GROUP-" + listGroup[i] + "-" + buildConfig + usedTenant;
                        FirebaseMessaging.getInstance().subscribeToTopic("GROUP-" + listGroup[i] + "-" + buildConfig + usedTenant);
                        topicList.add(groupMC);
                    }
                }
            } else if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                FirebaseMessaging.getInstance().subscribeToTopic("ALL-MO-" + buildConfig + usedTenant);
                if (null != user.getUuid_group()) {
                    String[] listGroup = user.getUuid_group().split(";");
                    for (int i = 0; i < listGroup.length; i++) {
                        FirebaseMessaging.getInstance().subscribeToTopic("GROUP-" + listGroup[i] + "-" + buildConfig + usedTenant);
                    }
                }
                if (null != user.getUuid_branch()) {
                    FirebaseMessaging.getInstance().subscribeToTopic("BRANCH-" + user.getUuid_branch() + "-" + buildConfig + usedTenant);
                }
                if (null != user.getUuid_dealer()) {
                    FirebaseMessaging.getInstance().subscribeToTopic("DEALER-" + user.getUuid_dealer() + "-" + buildConfig + usedTenant);
                }
            } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                FirebaseMessaging.getInstance().subscribeToTopic("ALL-MS-" + buildConfig + usedTenant);
                if (null != user.getUuid_branch()) {
                    FirebaseMessaging.getInstance().subscribeToTopic("BRANCH-" + user.getUuid_branch() + "-" + buildConfig + usedTenant);
                }
                if (null != user.getUuid_group()) {
                    String[] listGroup = user.getUuid_group().split(";");
                    for (int i = 0; i < listGroup.length; i++) {
                        FirebaseMessaging.getInstance().subscribeToTopic("GROUP-" + listGroup[i] + "-" + buildConfig + usedTenant);
                    }
                }
            }

            /*SubmitSubscribedTopicTask submitSubscribedTopicTask = new SubmitSubscribedTopicTask(getContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), topicList);
            submitSubscribedTopicTask.execute();*/
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorToSubcribe", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorToSubcribe", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Subcribe Topic"));
        }
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(errorSubject != null && !errorSubject.equals(""))
            Toast.makeText(getContext(), errorSubject+":"+errorMsg, Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    public class onImportSuccess extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            boolean success = data.getBoolean("importSuccess", false);
            if (success) {
                getContextAsActivity().finish();
                goToSynchronize();
                importSuccess = null;
            }
        }
    }
}