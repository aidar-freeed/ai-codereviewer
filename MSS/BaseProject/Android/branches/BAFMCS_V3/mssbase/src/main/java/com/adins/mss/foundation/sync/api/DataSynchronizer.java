package com.adins.mss.foundation.sync.api;

import android.content.Context;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.common.LinkedHashMapToObject;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Holiday;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.Sync;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.HolidayDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.SyncDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.foundation.sync.api.model.SynchronizeCOHRequestModel;
import com.adins.mss.foundation.sync.api.model.SynchronizeRequestModel;
import com.adins.mss.foundation.sync.api.model.SynchronizeResponse;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adityapurwa on 11/03/15.
 * Automatically do a synchronization with the server.
 */
public class DataSynchronizer {
    public static final String IS_SYNCHRONIZE_SYNC = "IS_SYNCHRONIZE_SYNC";
    public static final String IS_SYNCHRONIZE_LOOKUP = "IS_SYNCHRONIZE_LOOKUP";
    public static final String IS_SYNCHRONIZE_HOLIDAY = "IS_SYNCHRONIZE_HOLIDAY";
    public static final String IS_SYNCHRONIZE_COH = "IS_SYNCHRONIZE_COH";
    public static final String IS_SYNCHRONIZE_USER= "IS_SYNCHRONIZE_USER";
    private static final String SYNCHRONIZATION_PREFERENCE = "com.adins.mss.base.SynchronizationPreference";
    private static final String DTM_UPD_KEY = "com.adins.mss.base.SynchronizationPreference";
    private final Context context;

    /**
     * New data synchronization instance.
     *
     * @param context The context of synchronization.
     */
    public DataSynchronizer(Context context) {
        this.context = context;
    }

    /**
     * Reflect server database to local database
     *
     * @param <T>                     Dao class type.
     * @param <TDataAccess>           Dao data access type.
     * @param tableName               Name of the table to reflect.
     * @param daoClass                The dao class of the table.
     * @param dataAccessClass         Data access class of the table. The class must have add(Context, List&lt;daoClass&gt;)
     *                                static method on it to be able to handle the reflection.
     * @param synchronizationCallback Callback called during synchronization event, may be null.
     * @param init
     * @param generateUuid
     * @throws IOException Bubbled from http request to the server.
     */
    public <T, TDataAccess> void reflect(
            String tableName,
            final Class<T> daoClass,
            final Class<TDataAccess> dataAccessClass,
            List parameters,
            final SynchronizationCallback synchronizationCallback,
            int init, final boolean generateUuid, String flag) throws IOException {

        // Get last dtm_upd.
        final ObscuredSharedPreferences synchronizationPreference =
                ObscuredSharedPreferences.getPrefs(context, SYNCHRONIZATION_PREFERENCE + "." + tableName, Context.MODE_PRIVATE);

        Date date = null;
        if (synchronizationPreference.contains(DTM_UPD_KEY)) {
            date = new Date(Long.parseLong(synchronizationPreference.getString(DTM_UPD_KEY, "")));
        }

        SynchronizeRequestModel request = new SynchronizeRequestModel();
        request.setInit(init);
        if (parameters != null)
            request.setList(parameters);
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        request.setTableName(tableName);
        if (date != null) {
            request.setDtm_upd(date);
        }

        String jsonRequest = GsonHelper.toJson(request);
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult serverResult = null;
        String url = GlobalData.getSharedGlobalData().getURL_SYNCPARAM();

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, jsonRequest);

        try {
            serverResult = httpConn.requestToServer(url, jsonRequest, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            FireCrash.log(e);
        }
        String body = null;
        if (serverResult != null && serverResult.isOK()) {
            body = serverResult.getResult();
        } else {
            reportFailure(synchronizationCallback);
            return;
        }
        if (Global.IS_DEV)
            Log.i("DataSynchronizer", body);
        // This may not work, the requested entity is typed but gson
        // give us entity with linked hash map as the sync entries.
        // BLAME THE DAMN TYPE ERASURE OF JAVA !!!
        // HACK use an object mapper.


        SynchronizeResponseSync entitySync = null;
        SynchronizeResponseLookup entityLookup = null;
        SynchronizeResponseHoliday entityHoliday = null;
        SynchronizeResponseCOH entityCOH = null;
        SynchronizeResponse<LinkedHashMap> entity = null;
        boolean dataWasHere = false;

        if (flag.equals(IS_SYNCHRONIZE_SYNC)) {
            entitySync = GsonHelper.fromJson(body, SynchronizeResponseSync.class);
            if (entitySync != null && entitySync.getListSync() != null && !entitySync.getListSync().isEmpty())
                dataWasHere = true;
        } else if (flag.equals(IS_SYNCHRONIZE_LOOKUP)) {
            entityLookup = GsonHelper.fromJson(body, SynchronizeResponseLookup.class);
            if (entityLookup != null && entityLookup.getListSync() != null && !entityLookup.getListSync().isEmpty())
                dataWasHere = true;
        } else if (flag.equals(IS_SYNCHRONIZE_HOLIDAY)) {
            entityHoliday = GsonHelper.fromJson(body, SynchronizeResponseHoliday.class);
            if (entityHoliday != null && entityHoliday.getListSync() != null && !entityHoliday.getListSync().isEmpty())
                dataWasHere = true;
        } else if (flag.equals(IS_SYNCHRONIZE_COH)) {
            entityCOH = GsonHelper.fromJson(body, SynchronizeResponseCOH.class);
            if (entityCOH != null && entityCOH.getListSync() != null && !entityCOH.getListSync().isEmpty())
                dataWasHere = true;
        }
        else {
            entity = GsonHelper.fromJson(body, SynchronizeResponse.class);
            if (entity != null && entity.getListSync() != null && !entity.getListSync().isEmpty())
                dataWasHere = true;
        }

        if (dataWasHere) {
            saveNewDtmUpd(synchronizationPreference);

            try {
                Method add = dataAccessClass.getMethod("addOrUpdateAll", Context.class, List.class);
                List<Sync> entitiesSync;
                List<Lookup> entitiesLookup;
                List<Holiday> entitiesHoliday;
                List<User> entitiesCOH;
                if (flag.equals(IS_SYNCHRONIZE_SYNC)) {
                    if (generateUuid) {
                        entitiesSync = new ArrayList<>();
                        for (Sync sch : entitySync.getListSync()) {
                            sch.setUuid_sync(Tool.getUUID());
                            entitiesSync.add(sch);
                        }
                    } else {
                        entitiesSync = entitySync.getListSync();
                    }
                    SyncDataAccess.addOrReplaceAll(context, entitiesSync);
                } else if (flag.equals(IS_SYNCHRONIZE_LOOKUP)) {
                    if (generateUuid) {
                        entitiesLookup = new ArrayList<>();
                        for (Lookup sch : entityLookup.getListSync()) {
                            sch.setUuid_lookup(Tool.getUUID());
                            entitiesLookup.add(sch);
                        }
                    } else {
                        entitiesLookup = entityLookup.getListSync();
                    }
                    LookupDataAccess.addOrUpdateAll(context, entitiesLookup);
                } else if (flag.equals(IS_SYNCHRONIZE_HOLIDAY)) {
                    if (generateUuid) {
                        entitiesHoliday = new ArrayList<>();
                        for (Holiday sch : entityHoliday.getListSync()) {
                            sch.setUuid_holiday(Tool.getUUID());
                            entitiesHoliday.add(sch);
                        }
                    } else {
                        entitiesHoliday = entityHoliday.getListSync();
                    }
                    HolidayDataAccess.addOrReplace(context, entitiesHoliday);
                } else if (flag.equals(IS_SYNCHRONIZE_COH)) {
                    entitiesCOH = entityCOH.getListSync();
                    UserDataAccess.addOrReplace(context, entitiesCOH);
                }
                else {
                    // Gson return a linked hash map. Change it to entities.
                    List<T> entities = LinkedHashMapToObject.convert(entity.getListSync(), daoClass, generateUuid);
                    // Invoke the static method add.
                    add.invoke(null, context, entities);
                }

            } catch (Exception e) {
                FireCrash.log(e);
                reportFailure(synchronizationCallback);
                e.printStackTrace();
                return;
            }
        }

        reportSuccess(synchronizationCallback);

    }

    /**
     * Fake reflect server database to local database. This does not actually update the database or
     * update the timestamp.
     *
     * @param <T>                     Dao class type.
     * @param tableName               Name of the table to reflect.
     * @param daoClass                The dao class of the table.
     * @param synchronizationCallback Callback called during synchronization event, may be null.
     * @param init
     * @param generateUuid
     * @throws IOException Bubbled from http request to the server.
     */
    public <T, TDataAccess> void fakeReflect(
            String tableName,
            final Class<T> daoClass,
            List parameters,
            final FakeSynchronizationCallback<T> synchronizationCallback,
            int init, final boolean generateUuid, String flag) throws IOException {

        // Get last dtm_upd.
        final ObscuredSharedPreferences synchronizationPreference =
                ObscuredSharedPreferences.getPrefs(context, SYNCHRONIZATION_PREFERENCE + "." + tableName, Context.MODE_PRIVATE);

        Date date = null;
        if (synchronizationPreference.contains(DTM_UPD_KEY)) {
            date = new Date(Long.parseLong(synchronizationPreference.getString(DTM_UPD_KEY, "")));
        }

        SynchronizeRequestModel request = new SynchronizeRequestModel();
        request.setInit(init);
        if (parameters != null){
            //Perbaikan jika setelah embed CSV ada lov group yang belum di insert ke database mobile, set dtm_upd null
            if (flag.equals(IS_SYNCHRONIZE_LOOKUP)) {
                for (int i = 0; i < parameters.size(); i++) {
                    Map lovGroup = (Map) parameters.get(i);
                    Sync sync = SyncDataAccess.getOneByLovGroupName(context, (String) lovGroup.get("lov_group"));
                    if (sync == null) {
                        lovGroup.put("dtm_upd", null);
                        parameters.set(i, lovGroup);
                    }
                }
            }
            //end
            request.setList(parameters);
        }
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        request.setTableName(tableName);
        if (date != null) {
            request.setDtm_upd(date);
        }

        String jsonRequest = GsonHelper.toJson(request);
        if (flag.equals(IS_SYNCHRONIZE_COH)) {
            SynchronizeCOHRequestModel requestModel = new SynchronizeCOHRequestModel(request);
            String loginId = GlobalData.getSharedGlobalData().getUser().getLogin_id();
            int idxOfOpenAt = loginId.indexOf('@');
            if (idxOfOpenAt != -1) {
                loginId = loginId.substring(0, idxOfOpenAt);
            }
            requestModel.setLogin_ID(loginId);
            requestModel.setAudit(GlobalData.getSharedGlobalData().getAuditData());
            jsonRequest = GsonHelper.toJson(requestModel);
        }

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult serverResult = null;
        String url = GlobalData.getSharedGlobalData().getURL_SYNCPARAM();

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, jsonRequest);

        try {
            serverResult = httpConn.requestToServer(url, jsonRequest, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            FireCrash.log(e);
        }
        String body = null;
        if (serverResult != null && serverResult.isOK()) {
            body = serverResult.getResult();
        } else {
            if (serverResult != null)
                reportFakeFailure(synchronizationCallback, serverResult.getResult());
            else
                reportFakeFailure(synchronizationCallback, null);
            return;
        }

        if (Global.IS_DEV)
            Log.i("DataSynchronizer", body);
        // This may not work, the requested entity is typed but gson
        // give us entity with linked hash map as the sync entries.
        // BLAME THE DAMN TYPE ERASURE OF JAVA !!!
        // HACK use an object mapper.
        SynchronizeResponseSync entitySync = null;
        SynchronizeResponseLookup entityLookup = null;
        SynchronizeResponseHoliday entityHoliday = null;
        SynchronizeResponseCOH entityCOH = null;
        SynchronizeResponse<LinkedHashMap> entity = null;
        boolean dataWasHere = false;
        boolean datawasZero = false;


        if (flag.equals(IS_SYNCHRONIZE_SYNC)) {
            entitySync = GsonHelper.fromJson(body, SynchronizeResponseSync.class);
            if (entitySync != null && entitySync.getListSync() != null && !entitySync.getListSync().isEmpty())
                dataWasHere = true;
            else if (entitySync != null && entitySync.getListSync() != null && entitySync.getListSync().isEmpty())
                datawasZero = true;
        } else if (flag.equals(IS_SYNCHRONIZE_LOOKUP)) {
            entityLookup = GsonHelper.fromJson(body, SynchronizeResponseLookup.class);
            if (entityLookup != null && entityLookup.getListSync() != null && !entityLookup.getListSync().isEmpty())
                dataWasHere = true;
            else if (entityLookup != null && entityLookup.getListSync() != null && entityLookup.getListSync().isEmpty())
                datawasZero = true;
        } else if (flag.equals(IS_SYNCHRONIZE_HOLIDAY)) {
            entityHoliday = GsonHelper.fromJson(body, SynchronizeResponseHoliday.class);
            if (entityHoliday != null && entityHoliday.getListSync() != null && !entityHoliday.getListSync().isEmpty())
                dataWasHere = true;
            else if (entityHoliday != null && entityHoliday.getListSync() != null && entityHoliday.getListSync().isEmpty())
                datawasZero = true;
        } else if (flag.equals(IS_SYNCHRONIZE_COH)) {
            entityCOH = GsonHelper.fromJson(body, SynchronizeResponseCOH.class);
            if (entityCOH != null && entityCOH.getListSync() != null && !entityCOH.getListSync().isEmpty()) {
                dataWasHere = true;

            }
        }

        else {
            entity = GsonHelper.fromJson(body, SynchronizeResponse.class);
            if (entity != null && entity.getListSync() != null && !entity.getListSync().isEmpty())
                dataWasHere = true;
            else if (entity != null && entity.getListSync() != null && entity.getListSync().isEmpty())
                datawasZero = true;
        }

        if (dataWasHere) {

            try {
                List<Sync> entitiesSync;
                List<Lookup> entitiesLookup;
                List<Holiday> entitiesHoliday;
                List<User> entitiesCOH;
                if (flag.equals(IS_SYNCHRONIZE_SYNC)) {
                    if (generateUuid) {
                        entitiesSync = new ArrayList<>();
                        for (Sync sch : entitySync.getListSync()) {
                            sch.setUuid_sync(Tool.getUUID());
                            entitiesSync.add(sch);
                        }
                    } else {
                        entitiesSync = entitySync.getListSync();
                    }
                    reportFakeSuccess(synchronizationCallback, entitiesSync);
                } else if (flag.equals(IS_SYNCHRONIZE_LOOKUP)) {
                    if (generateUuid) {
                        entitiesLookup = new ArrayList<>();
                        for (Lookup sch : entityLookup.getListSync()) {
                            sch.setUuid_lookup(Tool.getUUID());
                            entitiesLookup.add(sch);
                        }
                    } else {
                        entitiesLookup = entityLookup.getListSync();
                    }
                    reportFakeSuccess(synchronizationCallback, entitiesLookup);
                } else if (flag.equals(IS_SYNCHRONIZE_HOLIDAY)) {
                    saveNewDtmUpd(synchronizationPreference);
                    if (generateUuid) {
                        entitiesHoliday = new ArrayList<>();
                        for (Holiday sch : entityHoliday.getListSync()) {
                            sch.setUuid_holiday(Tool.getUUID());
                            entitiesHoliday.add(sch);
                        }
                    } else {
                        entitiesHoliday = entityHoliday.getListSync();
                    }
                    reportFakeSuccess(synchronizationCallback, entitiesHoliday);
                } else if (flag.equals(IS_SYNCHRONIZE_COH)) {
                    entitiesCOH = entityCOH.getListSync();
                    User ucash = GlobalData.getSharedGlobalData().getUser();

                    if (entitiesCOH.get(0).getCash_limit() == null)
                        ucash.setCash_limit("0");
                    else
                        ucash.setCash_limit(entitiesCOH.get(0).getCash_limit());

                    ucash.setCash_on_hand(entitiesCOH.get(0).getCash_on_hand());
                    UserDataAccess.addOrReplace(context, ucash);
                    reportFakeSuccess(synchronizationCallback, entitiesCOH);
                }
                else {
                    // Gson return a linked hash map. Change it to entities.
                    List<T> entities = LinkedHashMapToObject.convert(entity.getListSync(), daoClass, generateUuid);
                    reportFakeSuccess(synchronizationCallback, entities);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                reportFakeFailure(synchronizationCallback, e.getMessage());
                e.printStackTrace();
                return;
            }
        } else if (datawasZero) {
            try {
                List<Sync> entitiesSync;
                List<Lookup> entitiesLookup;
                List<Holiday> entitiesHoliday;
                List<User> entitiesCOH;

                if (flag.equals(IS_SYNCHRONIZE_SYNC)) {
                    if (generateUuid) {
                        entitiesSync = new ArrayList<>();
                        for (Sync sch : entitySync.getListSync()) {
                            sch.setUuid_sync(Tool.getUUID());
                            entitiesSync.add(sch);
                        }
                    } else {
                        entitiesSync = entitySync.getListSync();
                    }
                    reportFakeSuccess(synchronizationCallback, entitiesSync);
                } else if (flag.equals(IS_SYNCHRONIZE_LOOKUP)) {
                    if (generateUuid) {
                        entitiesLookup = new ArrayList<>();
                        for (Lookup sch : entityLookup.getListSync()) {
                            sch.setUuid_lookup(Tool.getUUID());
                            entitiesLookup.add(sch);
                        }
                    } else {
                        entitiesLookup = entityLookup.getListSync();
                    }
                    reportFakeSuccess(synchronizationCallback, entitiesLookup);
                } else if (flag.equals(IS_SYNCHRONIZE_HOLIDAY)) {
                    if (generateUuid) {
                        entitiesHoliday = new ArrayList<>();
                        for (Holiday sch : entityHoliday.getListSync()) {
                            sch.setUuid_holiday(Tool.getUUID());
                            entitiesHoliday.add(sch);
                        }
                    } else {
                        entitiesHoliday = entityHoliday.getListSync();
                    }
                    reportFakeSuccess(synchronizationCallback, entitiesHoliday);
                } else if (flag.equals(IS_SYNCHRONIZE_COH)) {
                    entitiesCOH = entityCOH.getListSync();
                    reportFakeSuccess(synchronizationCallback, entitiesCOH);
                }
                else {
                    // Gson return a linked hash map. Change it to entities.
                    List<T> entities = LinkedHashMapToObject.convert(entity.getListSync(), daoClass, generateUuid);
                    reportFakeSuccess(synchronizationCallback, entities);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void reportFakeFailure(FakeSynchronizationCallback synchronizationCallback, String errorMessage) {
        if (synchronizationCallback != null) {
            String errMessage = errorMessage != null ? errorMessage : "";
            synchronizationCallback.onFailed(errMessage);
        }
    }

    private <T> void reportFakeSuccess(FakeSynchronizationCallback synchronizationCallback, List<T> entities) {
        if (synchronizationCallback != null) {
            synchronizationCallback.onSuccess(entities);
        }
    }

    private void saveNewDtmUpd(ObscuredSharedPreferences synchronizationPreference) {
        ObscuredSharedPreferences.Editor editor = synchronizationPreference.edit();
        editor.putString(DTM_UPD_KEY, String.valueOf(Calendar.getInstance().getTime().getTime()));
        editor.apply();
    }

    private void reportSuccess(SynchronizationCallback synchronizationCallback) {
        if (synchronizationCallback != null)
            synchronizationCallback.onSuccess();
    }

    private void reportFailure(SynchronizationCallback synchronizationCallback) {
        if (synchronizationCallback != null)
            synchronizationCallback.onFailed();
    }
}
