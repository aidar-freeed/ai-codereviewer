package com.adins.mss.base.loyalti.monthlypointacquisition;


import com.adins.mss.base.loyalti.model.GroupPointData;
import com.adins.mss.base.loyalti.model.LoyaltyPointsRequest;
import com.adins.mss.base.loyalti.model.LoyaltyPointsResponse;
import com.adins.mss.base.loyalti.model.PointDetail;
import com.adins.mss.base.loyalti.model.RankDetail;
import com.adins.mss.base.loyalti.monthlypointacquisition.contract.ILoyaltyPointsDataSource;
import com.adins.mss.base.loyalti.monthlypointacquisition.contract.MonthlyPointContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthlyPointsPresenter implements MonthlyPointContract.Presenter, ILoyaltyPointsDataSource.ReqPointsListener {

    private MonthlyPointContract.View view;
    private MonthlyPointsLogic monthlyPointsLogic;

    //data hold
    private LoyaltyPointsResponse pointsData;

    public MonthlyPointsPresenter(MonthlyPointContract.View view, MonthlyPointsLogic logic) {
        this.view = view;
        this.monthlyPointsLogic = logic;
    }

    @Override
    public void init() {
        //no operation on init
    }

    @Override
    public void getMonthlyPointsData(LoyaltyPointsRequest reqData) {
        monthlyPointsLogic.getMonthlyPointsData(reqData,this);
    }

    @Override
    public float getAvgPoint() {
        return monthlyPointsLogic.getAvgPoint(pointsData.dataDetail);
    }

    @Override
    public int getMaxPoint() {
        return monthlyPointsLogic.getMaxPoint(pointsData.dataDetail);
    }

    @Override
    public int getTotalPoints() {
        return monthlyPointsLogic.getTotalPoints(pointsData.dataDetail);
    }

    @Override
    public int getCurrentMonth() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int currMonth = calendar.get(Calendar.MONTH);
        if(currMonth < 0)
            return 0;//default use january when error
        else if(currMonth >= 12)
            currMonth = 11;

        for (int i=0; i<pointsData.dataDetail.size(); i++) {
            GroupPointData group = pointsData.dataDetail.get(i);
            if(group == null)
                continue;

            int month = Integer.parseInt(group.getGroupPointValue()[0]);
            if(month == currMonth+1){
                currMonth = i;
                break;
            }
        }

        if(currMonth >= pointsData.dataDetail.size()){
            currMonth = pointsData.dataDetail.size() - 1;
        }

        return currMonth;
    }

    @Override
    public String[] getMonths() {
        if(pointsData == null || pointsData.dataDetail.isEmpty())
            return new String[]{};

        String[] monthNames = new String[pointsData.dataDetail.size()];
        Calendar calendar = Calendar.getInstance();
        List<GroupPointData> groups = pointsData.dataDetail;
        int monthIdx = 0;

        for(int i=0; i<groups.size(); i++){
            GroupPointData groupData = groups.get(i);
            if(groupData.isMonth()){
                monthIdx = Integer.valueOf(groupData.getGroupPointValue()[0]) - 1;
                calendar.set(Calendar.MONTH,monthIdx);
                monthNames[i] = calendar.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault());
            }
        }

        return monthNames;
    }

    @Override
    public GroupPointData getPointDataAt(int monthIdx) {
        if(pointsData == null || pointsData.dataDetail.isEmpty())
            return null;

        if(monthIdx >= pointsData.dataDetail.size()){
            return null;
        }

        return pointsData.dataDetail.get(monthIdx);
    }

    @Override
    public List<PointDetail> getPointDetails() {
        if(pointsData == null)
            return new ArrayList<>();

        List<PointDetail> pointCategories = new ArrayList<>();
        for (GroupPointData groupPoint:pointsData.dataDetail) {
            if(groupPoint == null)
                continue;
            if(groupPoint.pointDetails == null || groupPoint.pointDetails.isEmpty())
                continue;

            for (PointDetail pointDetail:groupPoint.pointDetails) {
                if(pointDetail == null)
                    continue;
                if(isContainsPointDetails(pointCategories,pointDetail.rewardProgram)){
                    continue;
                }
                pointCategories.add(pointDetail);
            }
        }

        return pointCategories;
    }

    private boolean isContainsPointDetails(List<PointDetail> list,String rewardProgram){
        boolean result = false;
        for (PointDetail pointDetail:list){
            if(pointDetail.rewardProgram.equals(rewardProgram)){
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public List<RankDetail> getRanks() {
        if(pointsData == null || pointsData.dataDetail.isEmpty())
            return new ArrayList<>();

        return pointsData.dataDetail.get(0).rankDetails;
    }

    @Override
    public void onSuccess(LoyaltyPointsResponse pointsData) {
        if(pointsData == null || pointsData.dataDetail.isEmpty()){
            view.onGetDataFailed("No data detail found");
            return;
        }

        this.pointsData = pointsData;
        //convert point and rank data to 2d array to satisfy view
        float[][] pointDataSet = new float[pointsData.dataDetail.size()][];
        RankDetail[][] rankDataSet = new RankDetail[pointsData.dataDetail.size()][pointsData.dataDetail.get(0).rankDetails.size()];
        List<PointDetail> pointDetailsDataSet = new ArrayList<>();

        for(int i=0; i<pointDataSet.length; i++){
            float[] yVals = new float[pointsData.dataDetail.get(i).pointDetails.size()];
            for (int j=0; j<yVals.length; j++){
                yVals[j] = Integer.parseInt(pointsData.dataDetail.get(i).pointDetails.get(j).point);
                pointDetailsDataSet.add(pointsData.dataDetail.get(i).pointDetails.get(j));
            }
            pointDataSet[i] = yVals;
        }

        for(int i=0; i<rankDataSet.length; i++){
            for (int j=0; j<rankDataSet[i].length; j++){
                rankDataSet[i][j] = pointsData.dataDetail.get(i).rankDetails.get(j);
            }
        }

        view.onDataReceived(pointDataSet,rankDataSet,pointDetailsDataSet);
    }

    @Override
    public void onFailed(String message) {
        view.onGetDataFailed(message);
    }
}

