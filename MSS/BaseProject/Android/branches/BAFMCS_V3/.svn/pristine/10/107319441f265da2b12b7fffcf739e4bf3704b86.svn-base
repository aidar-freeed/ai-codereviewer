package com.adins.mss.base.todolist.todayplanrepository;

import com.adins.mss.dao.PlanTask;

import java.util.List;

public interface IPlanTaskDataSource {
    //local data source by sqlite or sharedpref
    void loadPlans(Result<List<PlanTask>> resultCallback);
    void updatePlanStatus(List<PlanTask> planTasks, Result<List<PlanTask>> resultCallback);
    void updatePlanStatus(List<PlanTask> planTasks);
    int getTotalPlanFromStart();
    int getLastSequenceNo();
    boolean getStartVisitOnlineStatus();
    void saveStartVisitOnlineStatus(boolean status);
    boolean getNeedSyncStatus();
    void saveNeedSyncStatus(boolean status);
    String[] getLastOfflineChangePlan();
    void saveLastChangePlanOffline(String oldPlanTask,String newPlanTask);

    //remote data source
    void startVisit(RequestStartVisit request,Result<ResponseStartVisit> result);
    void changePlan(RequestChangePlan request,Result<ResponseChangePlan> result);

    //result callback
    public interface Result<T>{
        void onResult(T result);
        void onError(String error);
    }
}
