package com.adins.mss.coll.dashboardcollection;

import com.adins.mss.coll.dashboardcollection.model.CollResultDetail;
import com.adins.mss.coll.models.ReportSummaryResponse;

import java.util.List;

public interface IDashboardCollDataSource {
    //sync mode
    int getTotalOutstandingTask();
    double getOutstandingAmount();
    double getTotalAmountCollected();
    double getTotalAmountToCollect();
    List<CollResultDetail> getTaskCollectedDetails();
    List<CollResultDetail> getTaskPTPDetails();
    List<CollResultDetail> getTaskFailedDetails();

    //async mode
    void getTotalOutstandingTaskAsync(DashboardResultListener<Integer> listener);
    void getOutstandingAmountAsync(DashboardResultListener<Double> listener);
    void getTotalAmountCollectedAsync(DashboardResultListener<Double> listener);
    void getTotalAmountToCollectAsync(DashboardResultListener<Double> listener);
    void getTaskCollectedDetailsAsync(DashboardResultListener<List<CollResultDetail>> listener);
    void getTaskPTPDetailsAsync(DashboardResultListener<List<CollResultDetail>> listener);
    void getTaskFailedDetailsAsync(DashboardResultListener<List<CollResultDetail>> listener);

    //remote data
    void requestDashboardData(DashboardResultListener<ReportSummaryResponse> listener);

    interface DashboardResultListener<T>{
        void onResult(T result);
        void onError(Exception e);
    }
}
