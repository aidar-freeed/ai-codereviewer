package com.adins.mss.coll.dashboardcollection;

import com.adins.mss.coll.dashboardcollection.model.CollResultDetail;
import com.adins.mss.coll.dashboardcollection.model.DashboardData;

import java.util.List;

public interface DashboardCollectionContract {
    interface View{
        void onDashboardDataReceived(DashboardData dashboardData);
        void showErrorInfo(String errorMessage);
    }

    interface Presenter{
        void requestDashboardData();
    }
}
