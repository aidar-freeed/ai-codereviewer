package com.adins.mss.coll.loyalti.pointacquisitionmonthly.contracts;

import com.adins.mss.coll.models.loyaltymodels.LoyaltyPointsRequest;
import com.adins.mss.coll.models.loyaltymodels.LoyaltyPointsResponse;
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
