package com.adins.mss.base.todolist.todayplanrepository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.PlanTaskDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.ArrayList;
import java.util.List;

public class PlanTaskDataSource implements IPlanTaskDataSource {

    private Context appContext;
    private Handler handler;
    private final String START_VISIT_STATUS = "startVisitStatus";
    private final String NEED_SYNC_STATUS = "needSyncStatus";
    private final String PlanTaskPrefName = "PlanTaskSharedPref";

    public PlanTaskDataSource(Context appContext) {
        this.appContext = appContext;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void loadPlans(Result<List<PlanTask>> resultCallback) {
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        List<PlanTask> result = PlanTaskDataAccess.getAllPlan(appContext,uuidUser);
        List<PlanTask> finalResult = new ArrayList<>();
        boolean hasDeletedPlan = false;
        for(PlanTask planTask:result){
            TaskH taskH = planTask.getTaskH();
            if(taskH == null){
                hasDeletedPlan = true;
                continue;
            }

            if(taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_DELETED)){
                PlanTaskDataAccess.removePlan(appContext,planTask);
            }
            else {
                finalResult.add(planTask);
            }
        }
        GlobalData.getSharedGlobalData().getTodayPlanRepo().setHasDeletedPlanTask(hasDeletedPlan);

        if(finalResult.size() == 0){//reset current plan task if no plans data
            Global.setCurrentPlanTask(null);
        }

        if(resultCallback != null){
            resultCallback.onResult(finalResult);
        }
    }

    @Override
    public void updatePlanStatus(List<PlanTask> planTasks, Result<List<PlanTask>> resultCallback) {
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        PlanTaskDataAccess.addUpdatePlans(appContext,planTasks);
        List<PlanTask> newPlans = PlanTaskDataAccess.getAllPlan(appContext,uuidUser);//get new plans
        if(resultCallback != null){
            resultCallback.onResult(newPlans);
        }
    }

    @Override
    public void updatePlanStatus(List<PlanTask> planTasks) {
        PlanTaskDataAccess.addUpdatePlans(appContext,planTasks);
    }

    @Override
    public int getTotalPlanFromStart() {
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return PlanTaskDataAccess.totalAllPlanFromStart(appContext,uuidUser);
    }

    @Override
    public int getLastSequenceNo() {
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return PlanTaskDataAccess.getPlanLastSequenceNo(appContext,uuidUser);
    }

    @Override
    public boolean getStartVisitOnlineStatus() {
        SharedPreferences sharedPref = appContext.getSharedPreferences(PlanTaskPrefName,Context.MODE_PRIVATE);
        return sharedPref.getBoolean(START_VISIT_STATUS,false);
    }

    @Override
    public void saveStartVisitOnlineStatus(boolean status) {
        SharedPreferences sharedPref = appContext.getSharedPreferences(PlanTaskPrefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(START_VISIT_STATUS,status);
        editor.apply();
    }

    @Override
    public boolean getNeedSyncStatus() {
        SharedPreferences sharedPref = appContext.getSharedPreferences(PlanTaskPrefName,Context.MODE_PRIVATE);
        return sharedPref.getBoolean(NEED_SYNC_STATUS,false);
    }

    @Override
    public void saveNeedSyncStatus(boolean status) {
        SharedPreferences sharedPref = appContext.getSharedPreferences(PlanTaskPrefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(NEED_SYNC_STATUS,status);
        editor.apply();
    }

    @Override
    public String[] getLastOfflineChangePlan() {
        SharedPreferences sharedPref = appContext.getSharedPreferences(PlanTaskPrefName,Context.MODE_PRIVATE);
        String oldPlan = sharedPref.getString("CPlanOld",null);
        String newPlan = sharedPref.getString("CPlanNew",null);
        if(oldPlan == null || newPlan == null){
            return new String[]{};
        }
        return new String[]{oldPlan,newPlan};
    }

    @Override
    public void saveLastChangePlanOffline(String oldPlanTask,String newPlanTask) {
        SharedPreferences sharedPref = appContext.getSharedPreferences(PlanTaskPrefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(oldPlanTask == null || newPlanTask == null){
            editor.remove("CPlanOld");
            editor.remove("CPlanNew");
            editor.apply();
            return;
        }

        editor.putString("CPlanOld",oldPlanTask);
        editor.putString("CPlanNew",newPlanTask);
        editor.apply();
    }

    @Override
    public void startVisit(final RequestStartVisit request, final Result<ResponseStartVisit> result) {
        if(!Tool.isInternetconnected(appContext)){
            if(result != null){
                result.onError("Offline");
            }
            return;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(appContext, encrypt, decrypt);
                String data = GsonHelper.toJson(request);
                String url =  GlobalData.getSharedGlobalData().getURL_START_VISIT_PLAN();

                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, data);

                try {
                    final HttpConnectionResult serverresult = httpConn.requestToServer(
                           url,
                            data, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverresult);

                    if(serverresult.getStatusCode() != 200){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                result.onError(serverresult.getResult());
                            }
                        });
                        return;
                    }

                    final ResponseStartVisit responseStartVisit = GsonHelper.fromJson(serverresult.getResult(),ResponseStartVisit.class);
                    if(responseStartVisit.getStatus().getCode() != 0){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                result.onError(responseStartVisit.getStatus().getMessage());
                            }
                        });
                        return;
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            result.onResult(responseStartVisit);
                        }
                    });
                }catch (final Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            result.onError(e.getMessage());
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void changePlan(final RequestChangePlan request, final Result<ResponseChangePlan> result) {
        if(!Tool.isInternetconnected(appContext)){
            if(result != null){
                result.onError("Offline");
            }
            return;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(appContext, encrypt, decrypt);
                String data = GsonHelper.toJson(request);
                String url =  GlobalData.getSharedGlobalData().getURL_CHANGE_PLAN();

                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, data);

                try {
                    final HttpConnectionResult serverresult = httpConn.requestToServer(
                            url,
                            data, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverresult);

                    if(serverresult.getStatusCode() != 200){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                result.onError(serverresult.getResult());
                            }
                        });
                        return;
                    }

                    final ResponseChangePlan respChangePlan = GsonHelper.fromJson(serverresult.getResult(),ResponseChangePlan.class);
                    if(respChangePlan.getStatus().getCode() != 0){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                result.onError(respChangePlan.getStatus().getMessage());
                            }
                        });
                        return;
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            result.onResult(respChangePlan);
                        }
                    });
                }catch (final Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            result.onError(e.getMessage());
                        }
                    });
                }
            }
        });
        thread.start();
    }
}
