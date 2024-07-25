package com.adins.mss.foundation.sync;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.authentication.Authentication;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.JsonRequestQuestionSet;
import com.adins.mss.base.dynamicform.JsonResponseQuestionSet;
import com.adins.mss.base.todo.form.JsonRequestScheme;
import com.adins.mss.base.todo.form.JsonResponseScheme;
import com.adins.mss.base.update.DownloadUpdate;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Holiday;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.Sync;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.HolidayDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.SyncDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnection;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.foundation.sync.api.DataSynchronizer;
import com.adins.mss.foundation.sync.api.FakeSynchronizationCallback;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by olivia.dg on 5/2/2017.
 */

public class BackgroundServiceSynchronize implements BackgroundSynchronize.SynchronizeListener, Authentication.AuthenticationHandler {
    private static final String SYNCHRONIZATION_PREFERENCE = "com.adins.mss.base.SynchronizationPreference";
    public static NotificationManager mNotifyManager;
    public static Notification.Builder mBuilder;
    public static Notification.BigTextStyle inboxStyle;
    public Context context;
    BackgroundSynchronize backgroundSynchronize;
    private int syncCounter = 0;
    private int syncMax = 0;
    private FakeSynchronizationCallback<Sync> syncFakeCallback;
    private boolean init;
    private DataSynchronizer datasync;
    private FakeSynchronizationCallback<Lookup> lookupFakeCallback;
    private FakeSynchronizationCallback<Holiday> holidayFakeCallback;
    private FakeSynchronizationCallback<User> cashFakeCallBack;
    private Sync finalSavedSync;
    private boolean forceStop;
    private boolean isFinish = false;
    private boolean isSyncScheme = false;
    private boolean isSyncQuestionSet = false;
    private boolean isSyncLookup = false;
    private boolean isSyncHoliday = false;
    private boolean isHoliday = false;
    private SynchronizeAll synchronizeAll;
    public static final String NOTIFICATION_CHANNEL_ID = "10002";

    public BackgroundServiceSynchronize(Context context) {
        this.context = context;
    }

    public void authentication(FragmentActivity context) {
        com.adins.mss.foundation.notification.Notification.getSharedNotification().clearNotifAll(context);
        String username = GlobalData.getSharedGlobalData().getUser().getLogin_id();
        String password = GlobalData.getSharedGlobalData().getUser().getPassword();

        if (Global.IS_DEV)
            System.out.println("this is uname and password = " + username + "-" + password);
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
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
                context,
                username,
                password,
                buildNumber,
                this);
    }

    public void Sync() {
        start();
    }

    public void start() {
        syncFakeCallback = new FakeSynchronizationCallback<Sync>() {
            @Override
            public void onSuccess(List<Sync> entities) {
                syncMax = entities.size() * 2;
                syncCounter = 0;
                try {
                    if (!entities.isEmpty()) {
                        for (Sync sync : entities) {
                            syncCounter++;
                            publishCounter();
                            if (init) {
                                List<HashMap<String, String>> lookupArgs = new ArrayList<>();
                                HashMap<String, String> forms = new HashMap<>();
                                forms.put("lov_group", sync.getLov_group());
                                lookupArgs.add(forms);

                                try {
                                    finalSavedSync = sync;
                                    datasync.fakeReflect("MS_LOV", Lookup.class,
                                            lookupArgs, lookupFakeCallback, 1,
                                            false, DataSynchronizer.IS_SYNCHRONIZE_LOOKUP);
                                    if (forceStop) {
                                        return;
                                    }

                                } catch (IOException e) {
                                    sendErrorDataToACRA(e.getMessage());
                                    if (Global.IS_DEV)
                                        e.printStackTrace();
                                    error();
                                    return;
                                }
                            } else {
                                finalSavedSync = SyncDataAccess.getOneByLovGroupName(context, sync.getLov_group());
                                Lookup lookups = LookupDataAccess.getOneByLovGroup(context, sync.getLov_group());
                                boolean isHaveLookupData;
                                isHaveLookupData = lookups != null;
                                if (finalSavedSync != null && isHaveLookupData) {
                                    if (finalSavedSync.getDtm_upd().getTime() >= sync
                                            .getDtm_upd().getTime()) {
                                        syncCounter++;
                                        publishCounter();
                                        continue;
                                    }
                                } else {
                                    finalSavedSync = sync;
                                }

                                List<HashMap<String, Object>> lookupArgs = new ArrayList<>();
                                HashMap<String, Object> forms = new HashMap<>();
                                forms.put("lov_group", sync.getLov_group());

                                if (SyncDataAccess.getOneByLovGroupName(context, finalSavedSync.getLov_group()) != null) {/*SyncDataAccess.getOne(activity, finalSavedSync.getUuid_sync()) != null*/
                                    forms.put("dtm_upd", finalSavedSync.getDtm_upd());
                                }

                                lookupArgs.add(forms);

                                try {

                                    datasync.fakeReflect("MS_LOV", Lookup.class,
                                            lookupArgs, lookupFakeCallback, 0,
                                            false, DataSynchronizer.IS_SYNCHRONIZE_LOOKUP);
                                    if (forceStop) {
                                        return;
                                    }
                                } catch (IOException e) {
                                    if (Global.IS_DEV)
                                        e.printStackTrace();
                                    sendErrorDataToACRA(e.getMessage());
                                    error();
                                    return;
                                }
                            }
                            syncCounter++;
                            publishCounter();
                        }
                    } else {
                        progressUpdated(100);
                        isSyncLookup = true;
                    }

                    publishCounter();
                    String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                    try {
                        datasync.fakeReflect("USER_CASH", User.class,
                                null, cashFakeCallBack, 1,
                                false, DataSynchronizer.IS_SYNCHRONIZE_COH);
                        if (forceStop) {
                            return;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        isSyncHoliday = true;
                        publishCounter();
                    }

                    try {
                        datasync.fakeReflect("MS_HOLIDAY_D", Holiday.class,
                                null, holidayFakeCallback, 1,
                                false, DataSynchronizer.IS_SYNCHRONIZE_HOLIDAY);
                        if (forceStop) {
                            return;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        isSyncHoliday = true;
                        publishCounter();
                    }

                } catch (Exception ex) {
                    if (Global.IS_DEV)
                        ex.printStackTrace();
                    sendErrorDataToACRA(ex.getMessage());
                    error();
                    return;
                }
            }

            @Override
            public void onFailed(final String errMessage) {
                sendErrorDataToACRA(errMessage);
                error();
            }
        };

        holidayFakeCallback = new FakeSynchronizationCallback<Holiday>() {

            @Override
            public void onSuccess(List<Holiday> entities) {
                try {
                    syncMax = entities.size() * 2;
                    syncCounter = 0;
                    isHoliday = true;
                    for (Holiday holiday : entities) {
                        syncCounter++;
                        publishCounter();
                        HolidayDataAccess.addOrReplace(context, holiday);
                        syncCounter++;
                        publishCounter();
                    }
                    publishCounter();
                } catch (Exception ex) {
                    sendErrorDataToACRA(ex.getMessage());
                    error();
                    forceStop = true;
                    if (Global.IS_DEV)
                        ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(final String errMessage) {
                sendErrorDataToACRA(errMessage);
                error();
                forceStop = true;
            }
        };

        lookupFakeCallback = new FakeSynchronizationCallback<Lookup>() {
            @Override
            public void onSuccess(List<Lookup> entities) {
                try {
                    LookupDataAccess.addOrUpdateAll(context, entities);
                    SyncDataAccess.addOrReplace(context, finalSavedSync);
                } catch (Exception ex) {
                    sendErrorDataToACRA(ex.getMessage());
                    error();
                    forceStop = true;
                    if (Global.IS_DEV)
                        ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(final String errMessage) {
                sendErrorDataToACRA(errMessage);
                error();
                forceStop = true;
            }
        };

        cashFakeCallBack = new FakeSynchronizationCallback<User>() {
            @Override
            public void onSuccess(List<User> entities) {
                if (!entities.isEmpty()) {
                    User ucash = GlobalData.getSharedGlobalData().getUser();
                    ucash.setCash_limit(entities.get(0).getCash_limit());
                    ucash.setCash_on_hand(entities.get(0).getCash_on_hand());
                    UserDataAccess.addOrReplace(context, ucash);
                }
            }

            @Override
            public void onFailed(final String errMessage) {
                sendErrorDataToACRA(errMessage);
                error();
                forceStop = true;
            }
        };
        synchronize();
    }

    private void synchronize() {
        datasync = new DataSynchronizer(context);
        if (synchronizeAll != null)
            synchronizeAll.cancel(true);
        synchronizeAll = new SynchronizeAll();
        synchronizeAll.execute();
    }

    @Override
    public void onConnectionFail(Authentication auth, HttpConnectionResult result) {
        //EMPTY
    }

    @Override
    public void onLoginFail(Authentication auth, String message) {
        if (message.contains("Password") || message.contains("password") || message.contains("IMEI") || message.contains("imei")) {
            DialogManager.showForceExitAlert((Activity) context, context.getString(R.string.password_not_match));
        } else
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onForceUpdate(Authentication auth, String message, final String otaLink) {
        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(context)
                .withTitle(context.getString(R.string.server))
                .withMessage(context.getString(R.string.critical_update))
                .withButton1Text(context.getString(R.string.update)).setButton1Click(
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

    @Override
    public void onInactiveUser(Authentication auth) {
        DialogManager.UninstallerHandler((FragmentActivity) context);
        return;
    }

    @Override
    public void onLoginSuccess(Authentication auth, String otaLink, boolean needUpdatePassword, boolean pwdExp, boolean needUpdateApplication, String message, User authenticatedUser) {
        Activity activity = (Activity) context;
        if (needUpdateApplication && !activity.isFinishing() && !activity.isDestroyed()) {
            showAskForUpdateDialog(otaLink, needUpdatePassword, pwdExp);
            return;
        }
        GlobalData.getSharedGlobalData().setUser(authenticatedUser);
        Sync();
    }

    private void showAskForUpdateDialog(final String otaLink, final boolean needUpdatePassword, final boolean pwdExp) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(context);
        builder.withTitle(context.getString(R.string.server)).isCancelableOnTouchOutside(false).isCancelable(false)
                .withMessage(context.getString(R.string.update_available))
                .withButton1Text(context.getString(R.string.later))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        Sync();
                    }
                })
                .withButton2Text(context.getString(R.string.update))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        openUpdate(otaLink);
                    }
                }).show();
    }

    private void openUpdate(String otaLink) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File file = new File(context.getFilesDir(), "app.apk");
            if (file.exists()) {
                Uri apkURI = FileProvider.getUriForFile(
                        context, context.getPackageName() + ".provider", file);
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } else {
                DownloadUpdate downloadUpdate = new DownloadUpdate(context);
                downloadUpdate.execute(otaLink);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getScheme() {
        String resultScheme = Global.FALSE_STRING;
        List<Scheme> listSchemes = SchemeDataAccess.getAll(context);
        if (Global.getSharedGlobal().getTempSchemeVersion() != null)
            Global.getSharedGlobal().setTempSchemeVersion(null);
        Global.getSharedGlobal().setTempSchemeVersion(new HashMap<String, Integer>());

        for (Scheme scheme : listSchemes) {
            Global.getSharedGlobal().getTempSchemeVersion().put(scheme.getUuid_scheme(), Integer.valueOf(scheme.getForm_version()));
        }

        try {
            if (GlobalData.getSharedGlobalData().getUser() == null)
                NewMainActivity.InitializeGlobalDataIfError(context);
        } catch (Exception e) {
            FireCrash.log(e);
            resultScheme = "Data has gone";
            return resultScheme;
        }

        JsonRequestScheme requestScheme = new JsonRequestScheme();
        if (GlobalData.getSharedGlobalData().getUser() != null && GlobalData.getSharedGlobalData().getUser()
                .getUuid_user() != null) {
            requestScheme.setUuid_user(GlobalData.getSharedGlobalData().getUser()
                    .getUuid_user());
        } else {
            ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context,
                    "GlobalData", Context.MODE_PRIVATE);
            String uuidUser = sharedPref.getString("UUID_USER", "");
            if (uuidUser.length() > 0) {
                requestScheme.setUuid_user(uuidUser);
            } else {
                resultScheme = "Data has gone";
                return resultScheme;
            }
        }
        requestScheme.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        requestScheme.setTask(Global.TASK_GETLIST);

        String json = GsonHelper.toJson(requestScheme);
        String url = GlobalData.getSharedGlobalData().getURL_GET_SCHEME();
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult serverResult = null;

        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
        }
        if (serverResult != null && serverResult.isOK()) {
            try {
                String result = serverResult.getResult();
                JsonResponseScheme responseScheme = GsonHelper.fromJson(result,
                        JsonResponseScheme.class);
                List<Scheme> schemes = responseScheme.getListScheme();
                List<PrintItem> printItems = responseScheme.getListPrintItems();
                if (schemes != null) {
                    resultScheme = Global.TRUE_STRING;
                    syncMax = schemes.size() + printItems.size();

                    SchemeDataAccess.clean(context);

                    for (Scheme scheme : schemes) {
                        syncCounter++;
                        publishCounter();
                        Scheme scheme2 = null;
                        try {
                            scheme2 = SchemeDataAccess.getOneByLastUpdate(context,
                                    scheme.getUuid_scheme(),
                                    scheme.getScheme_last_update());
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        try {
                            PrintItemDataAccess.delete(context, scheme.getUuid_scheme());
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }

                        if (scheme2 == null) {
                            if (scheme.getUuid_scheme() != null) {
                                SchemeDataAccess.addOrReplace(context, scheme);
                            }
                        } else {
                            if (scheme.getScheme_last_update().after(scheme2.getScheme_last_update())
                                    && scheme.getUuid_scheme() != null) {
                                SchemeDataAccess.addOrReplace(context, scheme);
                            }
                        }
                    }


                    for (PrintItem printItem : printItems) {
                        syncCounter++;
                        publishCounter();
                        Scheme scheme = SchemeDataAccess.getOne(context,
                                printItem.getUuid_scheme());
                        printItem.setScheme(scheme);
                        PrintItemDataAccess.addOrReplace(context, printItem);
                    }

                    if (schemes.isEmpty() || printItems.isEmpty()) {
                        progressUpdated(100);
                        isSyncScheme = true;
                    }
                } else {
                    resultScheme = responseScheme.getStatus().getMessage();
                }
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
            }
        } else {
            try {
                resultScheme = serverResult.getResult();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
        return resultScheme;
    }

    protected void getQuestionSet() {
        List<Scheme> schemeList = SchemeDataAccess.getAll(context);
        syncMax = schemeList.size();
        syncCounter = 0;
        for (Scheme scheme : schemeList) {
            List<QuestionSet> qs = new ArrayList<>();
            try {
                qs = QuestionSetDataAccess.getAllByFormVersion(context, scheme.getUuid_scheme(), scheme.getForm_version());
            } catch (Exception e) {
                FireCrash.log(e);
                Logger.e("BackgroundSynchronize", e);
            }
            boolean isChange = true;
            if (!qs.isEmpty()) {
                try {
                    JsonRequestScheme requestScheme = new JsonRequestScheme();
                    requestScheme.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    requestScheme.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    requestScheme.setUuid_scheme(scheme.getUuid_scheme());
                    requestScheme.setTask(Global.TASK_GETONE);

                    String json = GsonHelper.toJson(requestScheme);
                    String url = GlobalData.getSharedGlobalData().getURL_GET_SCHEME();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
                    HttpConnectionResult serverResult = null;

                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    try {
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV)
                            e.printStackTrace();
                    }
                    if (serverResult != null && serverResult.isOK()) {
                        try {
                            String result = serverResult.getResult();
                            JsonResponseScheme responseScheme = GsonHelper.fromJson(result, JsonResponseScheme.class);
                            List<Scheme> schemes = responseScheme.getListScheme();

                            Scheme scheme2 = schemes.get(0);
                            try {
                                //olivia : pengecekan form version
                                if(scheme2!=null) {
                                    Integer new_last_update = Integer.valueOf(scheme2.getForm_version());
                                    Integer temp_last_update = (Global.getSharedGlobal().getTempSchemeVersion().get(scheme.getUuid_scheme())!= null) ? Global.getSharedGlobal().getTempSchemeVersion().get(scheme.getUuid_scheme()): 0 ;
                                    isChange = new_last_update > temp_last_update;
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                            }

                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
            if (isChange || qs.isEmpty()) {
                syncCounter++;
                publishCounter();
                JsonRequestQuestionSet request = new JsonRequestQuestionSet();
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.setUuid_scheme(scheme.getUuid_scheme());

                String formVersionScheme = null;
                String formVersionTaskH = null;
                try {
                    formVersionScheme = scheme.getForm_version();
                } catch (Exception e) {
                    FireCrash.log(e);

                }
                if (null != formVersionScheme) {
                    request.setForm_version(formVersionScheme);
                } else if (null != CustomerFragment.getHeader() && null != CustomerFragment.getHeader().getForm_version()) {
                    formVersionTaskH = CustomerFragment.getHeader().getForm_version();
                    request.setForm_version(formVersionTaskH);
                }

                String json = GsonHelper.toJson(request);
                String url = GlobalData.getSharedGlobalData().getURL_GET_QUESTIONSET();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
                HttpConnectionResult serverResult = null;

                //Firebase Performance Trace HTTP Request
                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, json);

                try {
                    serverResult = httpConn.requestToServer(url, json,
                            Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV)
                        e.printStackTrace();
                }
                if (serverResult != null && serverResult.isOK()) {
                    try {
                        String result = serverResult.getResult();
                        JsonResponseQuestionSet response = GsonHelper.fromJson(result, JsonResponseQuestionSet.class);
                        if (response.getStatus().getCode() == 0) {
                            List<QuestionSet> questionSets = response.getListQuestionSet();

                            // Nendi: 2019.09.26 | Clean questionSet
                            QuestionSetDataAccess.delete(context, request.getUuid_scheme(), request.getForm_version());

                            List<QuestionSet> newquestionSets = new ArrayList<>();
                            for (QuestionSet questionSet : questionSets) {
                                questionSet.setUuid_question_set(Tool.getUUID());
                                questionSet.setScheme(scheme);
                                newquestionSets.add(questionSet);
                            }
                            QuestionSetDataAccess.addOrReplace(context, scheme.getUuid_scheme(), newquestionSets);
                        } else {
                            if (Global.IS_DEV) {
                                System.out.println(result);
                            }
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            } else {
                syncMax = 2;
                syncCounter = 1;
                publishCounter();
                progressUpdated(100);
            }
        }
        if (schemeList.isEmpty()) {
            progressUpdated(100);
            isSyncQuestionSet = true;
        }
        isSyncQuestionSet = true;
        progressUpdated(1);
    }


    private void syncLookup() throws IOException {
        List<Scheme> schemeList = SchemeDataAccess.getAll(context);
        List<Sync> syncs = SyncDataAccess.getAll(context);
        init = syncs == null || syncs.isEmpty();
        List<HashMap<String, String>> lookupArgs = new ArrayList<>();
        for (Scheme scheme : schemeList) {
            HashMap<String, String> forms = new HashMap<>();
            forms.put("formId", scheme.getForm_id());
            lookupArgs.add(forms);
        }
        if (!schemeList.isEmpty()) {
            datasync.fakeReflect("MS_LOV", Sync.class, lookupArgs,
                    syncFakeCallback, init ? 1 : 0, true, DataSynchronizer.IS_SYNCHRONIZE_SYNC);
        } else {
            progressUpdated(100);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                            datasync.fakeReflect("USER_CASH", User.class,
                                    null, cashFakeCallBack, 1,
                                    false, DataSynchronizer.IS_SYNCHRONIZE_COH);
                        }
                        datasync.fakeReflect("MS_HOLIDAY_D", Holiday.class,
                                null, holidayFakeCallback, 1,
                                false, DataSynchronizer.IS_SYNCHRONIZE_HOLIDAY);

                        if (forceStop) {
                            return;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV)
                            e.printStackTrace();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                progressUpdated(100);
                                isSyncHoliday = true;
                                publishCounter();
                            }

                        });
                    }
                }
            });

            thread.start();
        }
    }

    private void publishCounter() {
        float progress = (float) (syncCounter) / (syncMax - 1) * 100;
        final float finalProgress = progress > 100 ? 100 : progress;
        progressUpdated(finalProgress);
    }

    @Override
    public void progressUpdated(float progress) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new Notification.Builder(context);
                mBuilder.setContentTitle(context.getString(R.string.synchronizing))
                        .setContentText(context.getString(R.string.syncing))
                        .setSmallIcon(R.drawable.ic_synchronize);
                inboxStyle = new Notification.BigTextStyle();
                inboxStyle.setBigContentTitle(context.getString(R.string.synchronizing));
                inboxStyle.bigText(context.getString(R.string.syncing));
                mBuilder.setStyle(inboxStyle);
                mBuilder.setProgress(0, 0, true);
                mBuilder.setOngoing(true);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                    assert mNotifyManager != null;
                    mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                    mNotifyManager.createNotificationChannel(notificationChannel);
                }

                mNotifyManager.notify(0, mBuilder.build());
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }

        if (!isSyncScheme && !isSyncQuestionSet && !isSyncLookup) {

        } else if (isSyncScheme && !isSyncQuestionSet && !isSyncLookup) {

        } else if (isSyncScheme && isSyncQuestionSet && !isSyncLookup) {
            if (progress >= 100) {
                isSyncLookup = true;
            }
        } else if (isSyncScheme && isSyncQuestionSet && isSyncLookup && !isSyncHoliday) {
            if (progress >= 100 && isHoliday) {
                isSyncHoliday = true;
            }
            if (progress == -0.0 && isHoliday) {
                isSyncHoliday = true;
            }
        }
        if (progress > 100)
            progress = 100;

        if (syncMax == 0 || progress >= 100) {
            if (!isFinish && isSyncLookup && isSyncHoliday) {
                isFinish = true;
                mNotifyManager.cancel(0);

                String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();

                String day = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
                String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
                String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

                ObscuredSharedPreferences synchronizationPreference = ObscuredSharedPreferences.getPrefs(context, SYNCHRONIZATION_PREFERENCE, Context.MODE_PRIVATE);
                if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                    synchronizationPreference.edit().putString("MOSyncDate", day + month + year).commit();
                } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                    synchronizationPreference.edit().putString("MSSyncDate", day + month + year).commit();
                } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                    synchronizationPreference.edit().putString("MCSyncDate", day + month + year).commit();
                }
            }
        }
    }

    @Override
    public void synchronizeFailed(SynchronizeItem syncItem, HttpConnectionResult errorResult, int numOfRetries) {
        backgroundSynchronize.resumeSync();
    }

    private void error() {
        if (synchronizeAll != null){
            mNotifyManager.cancel(0);
            synchronizeAll.cancel(true);
        }
    }

    public void sendErrorDataToACRA(String message) {
        ACRA.getErrorReporter().putCustomData("errorSynchronize", message);
        ACRA.getErrorReporter().handleSilentException(new Exception("Error: Synchronize Error " + message));
    }

    public class SynchronizeAll extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                final String res1 = getScheme();
                if (res1.equals(Global.TRUE_STRING)) {
                    isSyncScheme = true;
                    progressUpdated(1);
                    getQuestionSet();
                    syncLookup();
                } else {
                    if (Global.FALSE_STRING.equals(res1)) {
                        error();
                    } else if (HttpConnection.ERROR_STATUSCODE_FROM_SERVER.equals(res1)) {
                        if (Global.IS_DEV)
                            System.out.print(HttpConnection.ERROR_STATUSCODE_FROM_SERVER);
                    } else {
                        error();
                    }
                }
            } catch (final IOException e) {
                if (Global.IS_DEV)
                    e.printStackTrace();
                sendErrorDataToACRA(e.getMessage());
                error();
            }
            return null;
        }
    }
}
