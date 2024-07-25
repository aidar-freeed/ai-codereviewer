package com.adins.mss.coll.models.loyaltymodels;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class LoyaltyPointsRequest extends MssRequestType {
    @SerializedName("MEMBERSHIP_PROGRAM_CODE")
    public String membershipProgramId;
    @SerializedName("LOGIN_ID")
    public String loginId;
    @SerializedName("POINT_GROUP")
    public String pointGroup;
}
