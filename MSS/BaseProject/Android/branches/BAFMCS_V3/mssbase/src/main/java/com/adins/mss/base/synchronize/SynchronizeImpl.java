package com.adins.mss.base.synchronize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.JsonRequestQuestionSet;
import com.adins.mss.base.dynamicform.JsonResponseQuestionSet;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.models.SyncParamSuccessRequest;
import com.adins.mss.base.models.SyncParamSuccessResponse;
import com.adins.mss.base.todo.form.JsonRequestScheme;
import com.adins.mss.base.todo.form.JsonResponseScheme;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Holiday;
import com.adins.mss.dao.LastSync;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.PaymentChannel;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.Sync;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.HolidayDataAccess;
import com.adins.mss.foundation.db.dataaccess.LastSyncDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.PaymentChannelDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.SyncDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnection;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.print.paymentchannel.SyncChannelResponse;
import com.adins.mss.foundation.print.paymentchannel.SyncChennelRequest;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.foundation.sync.Synchronize;
import com.adins.mss.foundation.sync.api.DataSynchronizer;
import com.adins.mss.foundation.sync.api.FakeSynchronizationCallback;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.gson.JsonParseException;

import org.acra.ACRA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 26/07/2017.
 */

public class SynchronizeImpl implements SynchronizeInterface, IShowError {

    public static final String SYNCHRONIZATION_PREFERENCE = "com.adins.mss.base.SynchronizationPreference";
    public static Intent IntentMainMenu;
    public Activity activity;
    public int syncMax = 0;
    public boolean isFinish = false, isSyncScheme = false,
            isSyncQuestionSet = false, isSyncLookup = false,
            isSyncHoliday = false, isHoliday = false, isSyncPaymentChannel;
    public ProgressListener progressListener;
    Synchronize sync;
    private int syncCounter = 0;
    private FakeSynchronizationCallback<Sync> syncFakeCallback;
    private boolean init;
    private DataSynchronizer datasync;
    private FakeSynchronizationCallback<Lookup> lookupFakeCallback;
    private FakeSynchronizationCallback<Holiday> holidayFakeCallback;
    private FakeSynchronizationCallback<User> cashFakeCallBack;
    private FakeSynchronizationCallback<User> userFakeCallBack;
    private Sync finalSavedSync;
    private boolean forceStop;
    private SynchronizeAll synchronizeAll;
    private HashMap<String, String> datasuccess = null;
    private HashMap<String, Object> datafailed = null;
    private List dataHardSync = null;
    long req_time;
    HashMap<String, Integer> lookupList = new HashMap<>();
    ErrorMessageHandler errorMessageHandler;
    private List<Lookup> afterInsert;

    public SynchronizeImpl(Activity activity, Intent intentMainMenu, ProgressListener progressListener) {
        this.activity = activity;
        this.progressListener = progressListener;
        IntentMainMenu = intentMainMenu;
        errorMessageHandler = new ErrorMessageHandler(this);
    }

    @Override
    public void publish() {
        // Synchronize Tasks
        doSyncFakeCallback();
        doHolidayFakeCallback();
        doLookupFakeCallback();
        doCashFakeCallback();
        doUserFakeCallback();

        synchronize();
    }

    @Override
    public void synchronize() {
        datasync = new DataSynchronizer(activity);
        if (synchronizeAll != null) synchronizeAll.cancel(true);
        synchronizeAll = new SynchronizeAll();
        synchronizeAll.execute();
    }

    @Override
    public void syncLookup(final Activity activity) throws IOException {
        List<Scheme> schemeList = SchemeDataAccess.getAll(activity);
        List<Sync> syncs = SyncDataAccess.getAll(activity);
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
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressListener.onUpdatedValue(100);
                }
            });
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
                        //Your code goes here
                        datasync.fakeReflect("MS_HOLIDAY_D", Holiday.class,
                                null, holidayFakeCallback, 1,
                                false, DataSynchronizer.IS_SYNCHRONIZE_HOLIDAY);

                        datasync.fakeReflect("MS_USER", User.class,
                                null, userFakeCallBack, 1,
                                false, DataSynchronizer.IS_SYNCHRONIZE_USER);

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
                                progressListener.onUpdatedValue(100);
                                isSyncHoliday = true;
                                progressListener.onSyncHoliday(isSyncHoliday);
                                publishCounter(activity);
                            }

                        });
                    }
                }
            });

            thread.start();
        }
    }

    private void getPaymentChannel() {
        String errorMessage = null;
        SyncChennelRequest request = new SyncChennelRequest();
        request.setLastDtmUpd(PaymentChannelDataAccess.getLastDateChannel(activity));
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity,
                GlobalData.getSharedGlobalData().isEncrypt(), GlobalData.getSharedGlobalData().isDecrypt());
        HttpConnectionResult serverResult = null;
        if(Tool.isInternetconnected(activity)) {
            try
            {
                String json = GsonHelper.toJson(request);
                serverResult = httpConn.requestToServer(GlobalData.getSharedGlobalData().getURL_SYNC_PAYMENTCHANNEL()
                        , json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e)
            {
                e.printStackTrace();
                errorMessage = activity.getString(R.string.failed_send_data);
            }
        } else {
            errorMessage = activity.getString(R.string.no_internet_connection);
        }

        SyncChannelResponse resultBean = new SyncChannelResponse();
        if(serverResult != null) {
            if(serverResult.isOK()) {
                try {
                    resultBean = GsonHelper.fromJson(serverResult.getResult(), SyncChannelResponse.class);
//                        if(resultBean.getListReceiptVoucher() == null || resultBean.getListReceiptVoucher().size() == 0){
//                            resultBean.setErrorMessage(context.getString(R.string.lookup_not_available_try_again, Global.TAG_RV_NUMBER));
//                        }
                } catch (JsonParseException e) {
                    errorMessage = activity.getString(R.string.msgErrorParsingJson);
                }
            } else {
                errorMessage = serverResult.getResult();
            }
        }

        if(errorMessage != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorDialog();
                }
            });
        }else{
            List<PaymentChannel> channels = resultBean.getListPaymentChannel();
            if (channels != null && channels.size() > 0) {
                syncMax = channels.size();
                syncCounter = 0;
                try {
                    for (int i = 0; i < channels.size(); i++) {
                        PaymentChannel channel = channels.get(i);
                        PaymentChannelDataAccess.addOrUpdate(activity, channel);
                        syncCounter++;
                        publishCounter(activity);
                    }
                    isSyncPaymentChannel = false;
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.onUpdatedValue(100);
                        progressListener.onSyncPaymentChannel(true);
                    }
                });
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressListener.onSyncPaymentChannel(true);
                    progressListener.onUpdatedValue(1);
                }
            });
        }
    }

    @Override
    public void publishCounter(Activity activity) {
        float progress = (float) (syncCounter) / (syncMax - 1) * 100;
        final float finalProgress = progress > 100 ? 100 : progress;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressListener.onUpdatedValue(finalProgress);
            }
        });
    }

    @Override
    public void run() {
        sync.startSynchronize();
    }

    @Override
    public void getQuestionSet() {
        List<Scheme> schemeList = SchemeDataAccess.getAll(activity);
        syncMax = schemeList.size();
        syncCounter = 0;
        int x = 1;
        for (Scheme scheme : schemeList) {
            List<QuestionSet> qs = new ArrayList<>();
            try {
                qs = QuestionSetDataAccess.getAllByFormVersion(activity, scheme.getUuid_scheme(), scheme.getForm_version());
            } catch (Exception e) {
                FireCrash.log(e);
                Logger.e("SynchronizeActivity", e);
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
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.getApplicationContext(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;

                    //Firebase Performance Trace HTTP Request
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
                                if (scheme2 != null) {
                                    Integer new_last_update = Integer.valueOf(scheme2.getForm_version());
                                    Integer temp_last_update = (Global.getSharedGlobal().getTempSchemeVersion().get(scheme.getUuid_scheme()) != null) ? Global.getSharedGlobal().getTempSchemeVersion().get(scheme.getUuid_scheme()) : 0;
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
                publishCounter(activity);
                JsonRequestQuestionSet request = new JsonRequestQuestionSet();
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.setForm_version(scheme.getForm_version());
                request.setUuid_scheme(scheme.getUuid_scheme());

                String json = GsonHelper.toJson(request);
                String url = GlobalData.getSharedGlobalData().getURL_GET_QUESTIONSET();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.getApplicationContext(), encrypt, decrypt);
                HttpConnectionResult serverResult = null;

                //Firebase Performance Trace HTTP Request
                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, json);

                if (Tool.isInternetconnected(activity)) {
                    try {
                        serverResult = httpConn.requestToServer(url, json,
                                Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV)
                            e.printStackTrace();
                    }
                    if (serverResult != null) {
                        if (serverResult.isOK()) {
                            try {
                                String result = serverResult.getResult();
                                JsonResponseQuestionSet response = GsonHelper.fromJson(
                                        result, JsonResponseQuestionSet.class);
                                if (response.getStatus().getCode() == 0) {
                                    List<QuestionSet> questionSets = response
                                            .getListQuestionSet();
                                    List<String> listVersionScheme = TaskHDataAccess.getAllVersionSchemeTaskByUuidScheme(activity, scheme.getUuid_scheme());
                                    listVersionScheme.add(scheme.getForm_version());

                                    QuestionSetDataAccess.deleteBySchemeVersion(activity,
                                            scheme.getUuid_scheme(), listVersionScheme);

                                    List<QuestionSet> newquestionSets = new ArrayList<>();
                                    for (QuestionSet questionSet : questionSets) {
                                        questionSet.setUuid_question_set(Tool.getUUID());
                                        questionSet.setScheme(scheme);
                                        newquestionSets.add(questionSet);
                                    }

                                    try { // 2019.09.26 | Delete QuestionSet if has been inserted on DB
                                        QuestionSetDataAccess.delete(activity, request.getUuid_scheme(),
                                                request.getForm_version());
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                    QuestionSetDataAccess.addOrReplace(activity,
                                            scheme.getUuid_scheme(), newquestionSets);
                                } else {
                                    if (Global.IS_DEV)
                                        System.out.println(result);
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (Global.IS_DEV)
                                    System.out.println(e.getMessage());
                            }
                        }
                    }
                }
            } else {
                syncMax = 2;
                syncCounter = 1;
                publishCounter(activity);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.onSyncQuestion(isSyncQuestionSet);
                        progressListener.onUpdatedValue(100);
                    }
                });
            }
        }
        if (schemeList.isEmpty()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressListener.onUpdatedValue(100);
                    isSyncQuestionSet = true;
                    progressListener.onSyncQuestion(isSyncQuestionSet);
                }
            });
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isSyncQuestionSet = true;
                progressListener.onUpdatedValue(1);
                progressListener.onSyncQuestion(isSyncQuestionSet);
            }
        });
    }

    private String getScheme() {
        String resultScheme = Global.FALSE_STRING;
        List<Scheme> listSchemes = SchemeDataAccess.getAll(activity);
        if (Global.getSharedGlobal().getTempSchemeVersion() != null)
            Global.getSharedGlobal().setTempSchemeVersion(null);
        Global.getSharedGlobal().setTempSchemeVersion(new HashMap<String, Integer>());

        for (Scheme scheme : listSchemes) {
            Global.getSharedGlobal().getTempSchemeVersion().put(scheme.getUuid_scheme(), Integer.valueOf(scheme.getForm_version()));
        }

        try {
            if (GlobalData.getSharedGlobalData().getUser() == null)
                NewMainActivity.InitializeGlobalDataIfError(activity);
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
            ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(activity,
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
        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
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

                    SchemeDataAccess.clean(activity);
                    PrintItemDataAccess.clean(activity);
                    for (Scheme scheme : schemes) {
                        syncCounter++;
                        publishCounter(activity);
                        SchemeDataAccess.addOrReplace(activity, scheme);
                    }

                    for (PrintItem printItem : printItems) {
                        syncCounter++;
                        publishCounter(activity);
                        Scheme scheme = SchemeDataAccess.getOne(activity,
                                printItem.getUuid_scheme());
                        printItem.setScheme(scheme);
                        PrintItemDataAccess.addOrReplace(activity, printItem);
                    }

                    if (schemes.isEmpty() || printItems.isEmpty()) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressListener.onUpdatedValue(100);
                                isSyncScheme = true;
                                progressListener.onSyncScheme(isSyncScheme);
                            }
                        });
                    }
                } else {
                    resultScheme = responseScheme.getStatus().getMessage();
                }

            } catch (JsonParseException e) {
                if (Global.IS_DEV)
                    e.printStackTrace();
                resultScheme = activity.getString(R.string.jsonParseFailed);
            } catch (IllegalStateException e) {
                if (Global.IS_DEV)
                    e.printStackTrace();
                resultScheme = activity.getString(R.string.jsonParseFailed);
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
                resultScheme = activity.getString(R.string.jsonParseFailed);
            }
        } else {
            try {
                resultScheme = serverResult.getResult();
            } catch (NullPointerException e) {
                resultScheme = activity.getString(R.string.jsonParseFailed);
            } catch (Exception e) {
                FireCrash.log(e);
                resultScheme = activity.getString(R.string.jsonParseFailed);
            }
        }

        return resultScheme;
    }

    private void showErrorDialog() {
        saveLastSyncToDB();
        if (synchronizeAll != null)
            synchronizeAll.cancel(true);
        if (!activity.isFinishing()) {
            final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
            builder.withTitle(activity.getString(R.string.error)).withMessage(activity.getString(R.string.sync_error))
                    .withButton1Text(activity.getString(R.string.try_again)).withButton2Text(activity.getString(R.string.later))
                    .isCancelable(false)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                            if (Build.VERSION.SDK_INT >= 11) {
                                activity.recreate();
                            } else {
                                Intent intent = activity.getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                activity.finish();
                                activity.overridePendingTransition(0, 0);

                                activity.startActivity(intent);
                                activity.overridePendingTransition(0, 0);
                            }
                        }
                    }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    Intent timelineIntent = new Intent(
                            activity.getApplicationContext(), NewMainActivity.getMainMenuClass());
                    timelineIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.getApplicationContext().startActivity(timelineIntent);
                    activity.finish();
                }
            }).show();
        }
    }

    private void showErrorDialog(String message) {
        if (synchronizeAll != null)
            synchronizeAll.cancel(true);
        if (!activity.isFinishing()) {
            final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
            builder.withTitle(activity.getString(R.string.error)).withMessage(message)
                    .withButton1Text(activity.getString(R.string.try_again)).withButton2Text(activity.getString(R.string.later))
                    .isCancelable(false)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                            if (Build.VERSION.SDK_INT >= 11) {
                                activity.recreate();
                            } else {
                                Intent intent = activity.getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                activity.finish();
                                activity.overridePendingTransition(0, 0);

                                activity.startActivity(intent);
                                activity.overridePendingTransition(0, 0);
                            }
                        }
                    }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    Intent timelineIntent = new Intent(
                            activity.getApplicationContext(), NewMainActivity.getMainMenuClass());
                    timelineIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.getApplicationContext().startActivity(timelineIntent);
                    activity.finish();
                }
            }).show();
        }
    }

    public void sendErrorDataToACRA(String message) {
        ACRA.getErrorReporter().putCustomData("errorSynchronize", message);
        ACRA.getErrorReporter().handleSilentException(new Exception("Error: Synchronize Error " + message));
    }

    /**
     * Method for Sync Fake Callback Instance
     */
    public void doSyncFakeCallback() {
        datasuccess = new HashMap<>();
        datafailed = new HashMap<>();
        dataHardSync = new ArrayList<String>();
        syncFakeCallback = new FakeSynchronizationCallback<Sync>() {
            @Override
            public void onSuccess(List<Sync> entities) {
                syncMax = entities.size() * 2;
                syncCounter = 0;
                try {
                    if (!entities.isEmpty()) {
                        for (Sync sync : entities) {
                            syncCounter++;
                            publishCounter(activity);
                            if (init) {
                                /**
                                 * Cara Embed Table lookup
                                 * 1. Ambil file msmdb menggunakan Android Device Manager (ADM)
                                 * 2. pilih file explorer -> data -> data -> nama package -> database -> msmdb -> save ke drive
                                 * 3. buka Sqlite Manager open file msmdb kemudian Import file .csv ke table MS_LOOKUP (jika belum ada datanya)
                                 * 4. isi table sync (jika belum ada isinya) sesuai lov_group yang mau di embed
                                 *    jangan lupa tambahkan 1 second untuk dtm_upd masing2 item yang mau di embed, untuk dtm_upd bisa minta ke bagian web
                                 *    edit table sync (jika pernah melewati langkah 7), kemudian hapus setiap record untuk lov_group yang TIDAK di embedd
                                 * 5. (jika kesulitan untuk mengisi tabel sync, lewati langkah 4 langsung ke langkah 6)
                                 * 6. Copy-paste file msmdb yg telah diubah tadi ke folder assets di masing2 project nya.
                                 * 7. (jika langkah 4 di lewati, jalankan aplikasi kemudian login sampai masuk ke halaman timeline, kemudian ulangi dari langkah 1)
                                 **/
                                //untuk LOV_GROUP yang di embed (sample :KOTA, KELURAHAN, KECAMATAN, ASSETMASTER, KODE POS

                                List<HashMap<String, String>> lookupArgs = new ArrayList<>();
                                HashMap<String, String> forms = new HashMap<>();
                                //buat catat jumlah LOV_GROUP pada DB
                                try {
                                    lookupList.put(sync.getLov_group(), LookupDataAccess.getAllByLovGroup(activity, sync.getLov_group()).size());
                                } catch (NullPointerException ex) {
                                    lookupList.put(sync.getLov_group(), 0);
                                }
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

                                } catch (final IOException e) {
                                    sendErrorDataToACRA(e.getMessage());
                                    if (Global.IS_DEV)
                                        e.printStackTrace();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            errorMessageHandler.processError(e, ErrorMessageHandler.DIALOG_TYPE);
                                        }
                                    });
                                    return;
                                }
                            } else {
                                finalSavedSync = SyncDataAccess
                                        .getOneByLovGroupName(
                                                activity,
                                                sync.getLov_group());
                                Lookup lookups = LookupDataAccess.getOneByLovGroup(activity, sync.getLov_group());
                                boolean isHaveLookupData;
                                isHaveLookupData = lookups != null;
                                if (null != finalSavedSync) {
                                    try {
                                        if (null == finalSavedSync.getFlag_hardsync() || "".equals(finalSavedSync.getFlag_hardsync()))
                                            finalSavedSync.setFlag_hardsync("0");
                                    } catch (NullPointerException ex) {
                                        finalSavedSync.setFlag_hardsync("0");
                                    }
                                    if (null != finalSavedSync && isHaveLookupData && !sync.getFlag_hardsync().equalsIgnoreCase("1")) {
                                        if (finalSavedSync.getDtm_upd().getTime() >= sync
                                                .getDtm_upd().getTime()) {
                                            afterInsert = LookupDataAccess.getAllByLovGroup(activity, sync.getLov_group());
                                            datasuccess.put(sync.getLov_group(), "N:0" + ",U:0" + ",T:" + afterInsert.size());
                                            syncCounter++;
                                            publishCounter(activity);
                                            continue;
                                        }
                                    } else {
                                        finalSavedSync = sync;
                                    }
                                } else {
                                    finalSavedSync = sync;
                                }

                                List<HashMap<String, Object>> lookupArgs = new ArrayList<>();
                                HashMap<String, Object> forms = new HashMap<>();
                                try {
                                    lookupList.put(sync.getLov_group(), LookupDataAccess.getAllByLovGroup(activity, sync.getLov_group()).size());
                                } catch (NullPointerException ex) {
                                    lookupList.put(sync.getLov_group(), 0);
                                }
                                forms.put("lov_group", sync.getLov_group());

                                if (finalSavedSync != null) {/*SyncDataAccess.getOne(activity, finalSavedSync.getUuid_sync()) != null*/
                                    forms.put("dtm_upd", finalSavedSync.getDtm_upd());
                                    //update dtm_upd finalSavedSync
                                    finalSavedSync.setDtm_upd(sync.getDtm_upd());
                                }
                                lookupArgs.add(forms);

                                try {
                                    finalSavedSync = sync;
                                    datasync.fakeReflect("MS_LOV", Lookup.class,
                                            lookupArgs, lookupFakeCallback, 0,
                                            false, DataSynchronizer.IS_SYNCHRONIZE_LOOKUP);
                                    if (forceStop) {
                                        return;
                                    }
                                } catch (final IOException e) {
                                    if (Global.IS_DEV)
                                        e.printStackTrace();
                                    sendErrorDataToACRA(e.getMessage());
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            errorMessageHandler.processError(e, ErrorMessageHandler.DIALOG_TYPE);
                                        }
                                    });
                                    return;
                                }
                            }
                            syncCounter++;
                            publishCounter(activity);
                        }
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressListener.onUpdatedValue(100);
                                isSyncLookup = true;
                                progressListener.onSyncLookup(isSyncLookup);
                            }
                        });
                    }

                    publishCounter(activity);
                    String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                    if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
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
                            progressListener.onSyncHoliday(isSyncHoliday);
                            publishCounter(activity);
                        }
                    }
                    //sync Holiday
                    try {
                        datasync.fakeReflect("MS_HOLIDAY_D", Holiday.class,
                                null, holidayFakeCallback, 1,
                                false, DataSynchronizer.IS_SYNCHRONIZE_HOLIDAY);
                        if (forceStop) {
                            return;
                        }
                        datasync.fakeReflect("MS_USER", User.class,
                                null, userFakeCallBack, 1,
                                false, DataSynchronizer.IS_SYNCHRONIZE_USER);
                        if (forceStop) {
                            return;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        isSyncHoliday = true;
                        progressListener.onSyncHoliday(isSyncHoliday);
                        publishCounter(activity);
                    }

                } catch (final Exception ex) {
                    if (Global.IS_DEV)
                        ex.printStackTrace();
                    sendErrorDataToACRA(ex.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorMessageHandler.processError(ex, ErrorMessageHandler.DIALOG_TYPE);
                        }
                    });
                    return;
                }
            }

            @Override
            public void onFailed(final String errMessage) {
                sendErrorDataToACRA(errMessage);
                datafailed.put("ERROR SYNC Data", errMessage);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorMessageHandler.processError("", errMessage, ErrorMessageHandler.DIALOG_TYPE);
                    }
                });
            }
        };
    }

    /**
     * Method for Holiday Fake Callback Instance
     */
    public void doHolidayFakeCallback() {
        holidayFakeCallback = new FakeSynchronizationCallback<Holiday>() {

            @Override
            public void onSuccess(List<Holiday> entities) {
                try {
                    syncMax = entities.size() * 2;
                    syncCounter = 0;
                    isHoliday = true;
                    progressListener.onSyncHoliday(isHoliday);
                    for (Holiday holiday : entities) {
                        syncCounter++;
                        publishCounter(activity);
                        HolidayDataAccess.addOrReplace(activity.getApplicationContext(), holiday);
                        syncCounter++;
                        publishCounter(activity);
                    }
                    publishCounter(activity);
                } catch (final Exception ex) {
                    sendErrorDataToACRA(ex.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorMessageHandler.processError(ex, ErrorMessageHandler.DIALOG_TYPE);
                        }
                    });
                    forceStop = true;
                    if (Global.IS_DEV)
                        ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(final String errMessage) {
                sendErrorDataToACRA(errMessage);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Global.IS_DEV && errMessage != null && !errMessage.isEmpty())
                            showErrorDialog(errMessage);
                        else
                            showErrorDialog();
                        forceStop = true;
                    }
                });
            }
        };
    }

    /**
     * Method for Lookup Fake Callback Instance
     */
    private void doLookupFakeCallback() {
        req_time = System.currentTimeMillis();
        lookupFakeCallback = new FakeSynchronizationCallback<Lookup>() {
            @Override
            public void onSuccess(List<Lookup> entities) {
                try {
                    LookupDataAccess.addOrUpdateAll(activity, entities);
                    SyncDataAccess.addOrReplace(activity, finalSavedSync);
                    if (isHardSync(entities)) {
                        dataHardSync.add(entities.get(0).getLov_group().trim());
                        //digunakan untuk push data lov group apa saja yang sukses di sync tapi ini untuk hardsync data
                        datasuccess.put(entities.get(0).getLov_group(), "N:" + entities.size() + ",U:" + countingUpdate(entities) + ",T:" + afterInsert.size());
                    } else {
                        //digunakan untuk push data lov group apa saja yang sukses di sync gunakan ini jika tanpa hardsync
                        datasuccess.put(finalSavedSync.getLov_group(), "N:" + entities.size() + ",U:" + countingUpdate(entities) + ",T:" + afterInsert.size());
                    }
                } catch (final Exception ex) {
                    sendErrorDataToACRA(ex.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorMessageHandler.processError(ex, ErrorMessageHandler.DIALOG_TYPE);
                        }
                    });
                    forceStop = true;
                    if (Global.IS_DEV)
                        ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(final String errMessage) {
                sendErrorDataToACRA(errMessage);
                datafailed.put("Error Sync LOV", errMessage);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorMessageHandler.processError("", errMessage, ErrorMessageHandler.DIALOG_TYPE);
                        forceStop = true;
                    }
                });
            }
        };
    }

    /**
     * Method for Cash Fake Callback Instance
     */
    private void doCashFakeCallback() {
        cashFakeCallBack = new FakeSynchronizationCallback<User>() {
            @Override
            public void onSuccess(List<User> entities) {
                if (!entities.isEmpty()) {
                    User ucash = GlobalData.getSharedGlobalData().getUser();
                    ucash.setCash_limit(entities.get(0).getCash_limit());
                    ucash.setCash_on_hand(entities.get(0).getCash_on_hand());
                    UserDataAccess.addOrReplace(activity.getApplicationContext(), ucash);
                }
            }

            @Override
            public void onFailed(final String errMessage) {
                sendErrorDataToACRA(errMessage);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorMessageHandler.processError("", errMessage, ErrorMessageHandler.DIALOG_TYPE);
                        forceStop = true;
                    }
                });
            }
        };
    }

    private void doUserFakeCallback() {
        userFakeCallBack = new FakeSynchronizationCallback<User>() {
            @Override
            public void onSuccess(List<User> entities) {
                try {
                    List<String> uuidUserServer = new ArrayList<>();
                    if (GlobalData.getSharedGlobalData().getUser() != null && GlobalData.getSharedGlobalData().getUser()
                            .getUuid_user() != null) {
                        uuidUserServer.add(GlobalData.getSharedGlobalData().getUser()
                                .getUuid_user());
                    }

                    for(int i = 0; i<entities.size(); i++) {
                        uuidUserServer.add(entities.get(i).getUuid_user());
                    }
                    UserDataAccess.addOrReplace(activity, entities);
                    List<User> userInDb = UserDataAccess.getAll(activity);
                    for(int i = 0; i<userInDb.size(); i++){
                        if(!uuidUserServer.contains(userInDb.get(i).getUuid_user())){
                            userInDb.get(i).setFacebook_id("0");
                        }else{
                            userInDb.get(i).setFacebook_id("1");
                        }
                    }
                    UserDataAccess.addOrReplace(activity, userInDb);


                } catch (Exception ex) {
                    sendErrorDataToACRA(ex.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog();
                        }
                    });
                    forceStop = true;
                    if(Global.IS_DEV)
                        ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(final String errMessage) {
                sendErrorDataToACRA(errMessage);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(Global.IS_DEV && errMessage!=null && !errMessage.isEmpty())
                            showErrorDialog(errMessage);
                        else
                            showErrorDialog();
                        forceStop = true;
                    }
                });
            }
        };
    }

    @Override
    public Synchronize getSync() {
        return sync;
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if (Global.IS_DEV && errorMsg != null && !errorMsg.isEmpty())
            showErrorDialog(errorMsg);
        else
            showErrorDialog();
    }

    public class SynchronizeAll extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                final String res1 = getScheme();
                if (res1.equals(Global.TRUE_STRING)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isSyncScheme = true;
                            progressListener.onSyncScheme(isSyncScheme);
                            progressListener.onUpdatedValue(1);
                        }
                    });
                    getQuestionSet();
                    getPaymentChannel();
                    syncLookup(activity);
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (GlobalData.isRequireRelogin()) {
                                DialogManager.showForceExitAlert(activity, activity.getString(com.adins.mss.base.R.string.msgLogout));
                            }
                            else if (Global.FALSE_STRING.equals(res1)) {
                                showErrorDialog();
                            } else if ("Data has gone".equals(res1)) {
                                String message = activity.getString(R.string.data_corrupt);
                                DialogManager.showForceExitAlert(activity, message);
                            } else if (HttpConnection.ERROR_STATUSCODE_FROM_SERVER.equals(res1)) {
                                if (Global.IS_DEV)
                                    System.out.print(HttpConnection.ERROR_STATUSCODE_FROM_SERVER);
                            } else {
                                showErrorDialog(res1);
                            }
                        }
                    });
                }
            } catch (final IOException e) {
                if (Global.IS_DEV)
                    e.printStackTrace();
                sendErrorDataToACRA(e.getMessage());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (GlobalData.isRequireRelogin()) {
                            DialogManager.showForceExitAlert(activity, activity.getString(com.adins.mss.base.R.string.msgLogout));
                            return;
                        }
                        errorMessageHandler.processError(e, ErrorMessageHandler.DIALOG_TYPE);
                    }
                });
            }
            return null;
        }
    }

    //PENGECEKAN JUMLAH DATA YANG SUDAH DI INSERT
    private int countingUpdate(List<Lookup> entities) {
        int countUpdate = 0;
        afterInsert = LookupDataAccess.getAllByLovGroup(activity, finalSavedSync.getLov_group());
        //Penghitungan data terupdate
        List<Lookup> entitiesWs = new ArrayList<>();
        entitiesWs.addAll(entities);
        if (!afterInsert.isEmpty()) {
            for (Lookup lookupDb : afterInsert) {
                if (!entitiesWs.isEmpty()) {
                    for (int i = 0; i < entitiesWs.size(); i++) {
                        if (entitiesWs.get(i).getUuid_lookup().equalsIgnoreCase(lookupDb.getUuid_lookup())) {
                            countUpdate++;
                            entitiesWs.remove(i);
                            break;
                        }
                    }
                }
            }
            for (int i = 0; i < entitiesWs.size(); i++) {
                if (entitiesWs.get(i).getIs_active().equals("0")) {
                    countUpdate++;
                }
            }
        }
        return countUpdate;
    }

    //CHECKING APAKAH ADA DATA FLAG HARDSYNC DARI SETIAP LIST LOOKUP
    private boolean isHardSync(List<Lookup> entities) {
        boolean hardsync = false;
        if (!entities.isEmpty()) {
            Sync sync = SyncDataAccess.getOneByLovGroupName(activity, entities.get(0).getLov_group());
            if (sync.getFlag_hardsync().equalsIgnoreCase("1")) hardsync = true;
        }
        if (!hardsync) {
            for (Lookup lookup : entities) {
                try {
                    if (lookup.getFlag_hardsync() != null) {
                        if (lookup.getFlag_hardsync().equalsIgnoreCase("1")) {
                            hardsync = true;
                            break;
                        }
                    }
                } catch (NullPointerException ex) {
                    continue;
                }
            }
        }
        return hardsync;
    }

    public void saveLastSyncToDB() {
        String uuid = Tool.getUUID();
        String failedData;
        int flagError; // No Error = 1, Error = 0
        if (datasuccess.size() > 0) {
            LastSync lastSync = new LastSync();
            if (datafailed.size() > 0) {
                if (!dataHardSync.isEmpty()) {
                    //INSERT TO DB LASTSYNC WHEN THERE IS ERROR, HARDSYNC DATA, NORMALSYNC DATA
                    String data = "NS:" + datasuccess + ",HS:" + dataHardSync + ",ER:" + datafailed;
                    flagError = 0;
                    lastSync.setUuid_last_sync(uuid);
                    lastSync.setDtm_req(String.valueOf(req_time));
                    lastSync.setFlag(String.valueOf(flagError));
                    lastSync.setListOfLOV(dataHardSync.toString());
                    lastSync.setData(data);
                    lastSync.setIs_send(0);
                } else {
                    //INSERT TO DB LASTSYNC WHEN THERE IS ERROR, NORMALSYNC DATA
                    failedData = datafailed.toString();
                    flagError = 0;
                    String Data = "NS:" + datasuccess + ",ER:" + failedData;
                    lastSync.setUuid_last_sync(uuid);
                    lastSync.setDtm_req(String.valueOf(req_time));
                    lastSync.setFlag(String.valueOf(flagError));
                    lastSync.setData(Data);
                    lastSync.setIs_send(0);
                }
            } else {
                if (!dataHardSync.isEmpty()) {
                    //INSERT TO DB LASTSYNC WHEN THERE IS HARDSYNC DATA, NORMALSYNC DATA
                    String data = datasuccess.toString();
                    flagError = 1;
                    lastSync.setUuid_last_sync(uuid);
                    lastSync.setDtm_req(String.valueOf(req_time));
                    lastSync.setFlag(String.valueOf(flagError));
                    lastSync.setListOfLOV(dataHardSync.toString());
                    lastSync.setData(data);
                    lastSync.setIs_send(0);
                } else {
                    //INSERT TO DB LASTSYNC WHEN THERE NORMALSYNC DATA ONLY
                    String data = "NS:" + datasuccess;
                    flagError = 1;
                    lastSync.setDtm_req(String.valueOf(req_time));
                    lastSync.setUuid_last_sync(uuid);
                    lastSync.setFlag(String.valueOf(flagError));
                    lastSync.setData(data);
                    lastSync.setIs_send(0);
                }
            }
            LastSyncDataAccess.addOrReplace(activity, lastSync);
        }
    }

    public class SubmitSyncParamSuccess extends AsyncTask<Void, Void, String> {
        private final Context mContext;
        private final String uuidUser;
        private final String dtmSuccess;

        public SubmitSyncParamSuccess(Context mContext, String uuidUser, String dtmSuccess) {
            this.mContext = mContext;
            this.uuidUser = uuidUser;
            this.dtmSuccess = dtmSuccess;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String errorMessage = null;
            if (Tool.isInternetconnected(mContext)) {
                SyncParamSuccessRequest request = new SyncParamSuccessRequest();
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.addImeiAndroidIdToUnstructured();
                request.setUuidUser(uuidUser);
                request.setDtmSuccess(dtmSuccess);

                String json = GsonHelper.toJson(request);
                String url = GlobalData.getSharedGlobalData().getURL_SYNC_PARAM_SUCCESS();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(mContext, encrypt, decrypt);
                // Firebase Performance Trace Network Request
                HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(
                        url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, json);

                try {
                    HttpConnectionResult serverResult = httpConn.requestToServer(url, json);
                    Utility.metricStop(networkMetric, serverResult);
                    String result = serverResult.getResult();
                    SyncParamSuccessResponse response = GsonHelper.fromJson(result, SyncParamSuccessResponse.class);
                    if (response.getStatus().getCode() == 0) {
                        Log.i("INFO SYNC SUCCESS", "Submit Sync Param Success");
                    }
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                }
            } else {
                errorMessage = mContext.getString(R.string.no_internet_connection);
            }
            return errorMessage;
        }

        @Override
        protected void onPostExecute(final String message) {
            super.onPostExecute(message);

            if (null != message && !"".equals(message)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

}
