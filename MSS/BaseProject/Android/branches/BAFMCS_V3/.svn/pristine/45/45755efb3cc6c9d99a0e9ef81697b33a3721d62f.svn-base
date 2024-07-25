package com.adins.mss.coll.models.loyaltymodels;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupPointData extends MssResponseType {

    @SerializedName("GROUP_POINT")
    public String groupPoint;
    @SerializedName("RANK")
    public List<RankDetail> rankDetails;
    @SerializedName("POINT_DETAIL")
    public List<PointDetail> pointDetails;

    private transient String[] groupPointValue;
    private transient boolean isMonth;
    private transient boolean isDay;

    public GroupPointData() { }

    public GroupPointData(String groupPoint, List<RankDetail> rankDetails, List<PointDetail> pointDetails) {
        this.groupPoint = groupPoint;
        this.rankDetails = rankDetails;
        this.pointDetails = pointDetails;

        groupPointValue = groupPoint.trim().split("-");
    }

    public String[] getGroupPointValue() {
        if(groupPoint == null || groupPoint.equals("")){
            return null;
        }
        if(groupPointValue == null || groupPointValue.length == 0)
            groupPointValue = groupPoint.trim().split("-");

        return groupPointValue;
    }

    public boolean isMonth() {
        if(groupPointValue == null || groupPointValue.length == 0)
             getGroupPointValue();

        if(groupPointValue.length == 2){
            isMonth = true;
            isDay = false;
        }

        return isMonth;
    }

    public boolean isDay() {
        if(groupPointValue == null || groupPointValue.length == 0)
            getGroupPointValue();

        if(groupPointValue.length == 3){
            isDay = true;
            isMonth = false;
        }

        return isDay;
    }
}

