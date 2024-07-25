package com.adins.mss.coll.loyalti.pointacquisitiondaily.contracts;

import com.adins.mss.coll.loyalti.BasePresenter;
import com.adins.mss.coll.loyalti.BaseView;
import com.adins.mss.coll.models.loyaltymodels.GroupPointData;
import com.adins.mss.coll.models.loyaltymodels.LoyaltyPointsRequest;
import com.adins.mss.coll.models.loyaltymodels.PointDetail;
import com.adins.mss.coll.models.loyaltymodels.RankDetail;

import java.util.List;

public interface DailyPointContract {
    interface View extends BaseView<DailyPointContract.Presenter> {
        void onDataReceived(float[][] pointDataSet, RankDetail[][] rankDataSet,List<PointDetail> pointDetailsDataSet);
        void onGetDataFailed(String message);
    }

    interface Presenter extends BasePresenter {
        void getDailyPointsData(LoyaltyPointsRequest reqData);
        float getAvgPoint();
        int getMaxPoint();
        int getTotalPoints();
        int getCurrentDay();
        String[] getDays();
        GroupPointData getPointDataAt(int monthIdx);
        List<PointDetail> getPointDetails();
        List<RankDetail> getRanks();
    }
}
