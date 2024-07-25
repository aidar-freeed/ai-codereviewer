package com.adins.mss.base.loyalti.monthlypointacquisition.contract;

import com.adins.mss.base.loyalti.model.LoyaltyPointsRequest;
import com.adins.mss.base.loyalti.model.LoyaltyPointsResponse;
import com.adins.mss.dao.GeneralParameter;

import java.util.List;

public interface ILoyaltyPointsDataSource {
    void requestPointsData(LoyaltyPointsRequest reqData, ReqPointsListener listener);
    List<GeneralParameter> getJobsGenParam(List<String> jobs);
    interface ReqPointsListener {
        void onSuccess(LoyaltyPointsResponse loyaltyPoints);
        void onFailed(String message);
    }

    //add more here...
}

