package com.adins.mss.base.loyalti.model;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoyaltyPointsResponse extends MssResponseType {

    @SerializedName("MEMBERSHIP_PROGRAM_CODE")
    public String membershipProgramId;
    @SerializedName("LOGIN_ID")
    public String loginId;
    @SerializedName("DATA_DETAIL")
    public List<GroupPointData> dataDetail;
}

