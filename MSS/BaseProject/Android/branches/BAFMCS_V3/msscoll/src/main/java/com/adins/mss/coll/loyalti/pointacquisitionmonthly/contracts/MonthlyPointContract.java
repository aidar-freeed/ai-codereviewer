package com.adins.mss.coll.loyalti.pointacquisitionmonthly.contracts;

import com.adins.mss.coll.loyalti.BasePresenter;
import com.adins.mss.coll.loyalti.BaseView;
import com.adins.mss.coll.models.loyaltymodels.GroupPointData;
import com.adins.mss.coll.models.loyaltymodels.LoyaltyPointsRequest;
import com.adins.mss.coll.models.loyaltymodels.PointDetail;
import com.adins.mss.coll.models.loyaltymodels.RankDetail;

import java.util.List;

public interface MonthlyPointContract {
    interface View extends BaseView<MonthlyPointContract.Presenter> {
        void onDataReceived(float[][] pointDataSet,RankDetail[][] rankDataSet,List<PointDetail> pointDetailsDataSet);
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
