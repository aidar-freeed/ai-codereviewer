package com.adins.mss.base.loyalti.monthlypointacquisition.contract;

import com.adins.mss.base.loyalti.BasePresenter;
import com.adins.mss.base.loyalti.BaseView;
import com.adins.mss.base.loyalti.model.GroupPointData;
import com.adins.mss.base.loyalti.model.LoyaltyPointsRequest;
import com.adins.mss.base.loyalti.model.PointDetail;
import com.adins.mss.base.loyalti.model.RankDetail;

import java.util.List;

public interface MonthlyPointContract {
    interface View extends BaseView<Presenter> {
        void onDataReceived(float[][] pointDataSet, RankDetail[][] rankDataSet, List<PointDetail> pointDetailsDataSet);
        void onGetDataFailed(String message);
    }

    interface Presenter extends BasePresenter {
        void getMonthlyPointsData(LoyaltyPointsRequest reqData);
        float getAvgPoint();
        int getMaxPoint();
        int getTotalPoints();
        int getCurrentMonth();
        String[] getMonths();
        GroupPointData getPointDataAt(int monthIdx);
        List<PointDetail> getPointDetails();
        List<RankDetail> getRanks();
    }
}

