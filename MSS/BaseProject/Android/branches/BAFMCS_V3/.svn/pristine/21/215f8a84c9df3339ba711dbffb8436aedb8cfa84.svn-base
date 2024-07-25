package com.adins.mss.base.loyalti.dailypointacquisition;


import com.adins.mss.base.loyalti.dailypointacquisition.contracts.DailyPointContract;
import com.adins.mss.base.loyalti.model.GroupPointData;
import com.adins.mss.base.loyalti.model.LoyaltyPointsRequest;
import com.adins.mss.base.loyalti.model.LoyaltyPointsResponse;
import com.adins.mss.base.loyalti.model.PointDetail;
import com.adins.mss.base.loyalti.model.RankDetail;
import com.adins.mss.base.loyalti.monthlypointacquisition.contract.ILoyaltyPointsDataSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyPointsPresenter implements DailyPointContract.Presenter, ILoyaltyPointsDataSource.ReqPointsListener {

    private DailyPointContract.View view;
    private DailyPointsLogic dailyPointsLogic;

    //data hold
    private LoyaltyPointsResponse pointsData;

    public DailyPointsPresenter(DailyPointContract.View view, DailyPointsLogic logic) {
        this.view = view;
        this.dailyPointsLogic = logic;
    }

    @Override
    public void init() {
        //no operation on init
    }

    @Override
    public void getDailyPointsData(LoyaltyPointsRequest reqData) {
        dailyPointsLogic.getDailyPointsData(reqData, this);
    }

    @Override
    public float getAvgPoint() {
        return dailyPointsLogic.getAvgPoint(pointsData.dataDetail);
    }

    @Override
    public int getMaxPoint() {
        return dailyPointsLogic.getMaxPoint(pointsData.dataDetail);
    }

    @Override
    public int getTotalPoints() {
        return dailyPointsLogic.getTotalPoints(pointsData.dataDetail);
    }

    @Override
    public int getCurrentDay() {
        //cek bulan yang dipilih
        int currDay = 0;

        if (pointsData == null || pointsData.dataDetail.isEmpty())
            return currDay;

        String[] timevalues;
        if (pointsData.dataDetail.get(0).isDay()) {
            GroupPointData groupPointData = pointsData.dataDetail.get(0);
            timevalues = groupPointData.getGroupPointValue();
        } else {
            return currDay;
        }

        //set date from timevalues
        Calendar calendarNow = Calendar.getInstance();
        int month = Integer.parseInt(timevalues[1]) - 1;

        //check current month
        calendarNow.setTime(new Date());
        int currMonth = calendarNow.get(Calendar.MONTH);
        boolean isPreviousMonth = false;

        if (currMonth > month) {
            //use last day on selected month
            isPreviousMonth = true;
            calendarNow.set(Calendar.MONTH, month);
            calendarNow.set(Calendar.DAY_OF_MONTH, calendarNow.getActualMaximum(Calendar.DAY_OF_MONTH));
            currDay = calendarNow.get(Calendar.DAY_OF_MONTH);
        } else {
            //get current day
            currDay = calendarNow.get(Calendar.DAY_OF_MONTH);
        }

        int pointDataDay;
        boolean dayFound = false;
        for (int i = 0; i < pointsData.dataDetail.size(); i++) {
            GroupPointData group = pointsData.dataDetail.get(i);
            if (group == null)
                continue;

            pointDataDay = Integer.valueOf(group.getGroupPointValue()[0]);
            if (pointDataDay == currDay) {
                dayFound = true;
                currDay = i;
                break;
            }
        }

        if (isPreviousMonth && !dayFound) {
            currDay = currDay - 1;
        }

        if (currDay >= pointsData.dataDetail.size()) {
            currDay = pointsData.dataDetail.size() - 1;
        }

        return currDay;
    }

    @Override
    public String[] getDays() {
        if (pointsData == null || pointsData.dataDetail.isEmpty())
            return new String[]{};

        String[] dayNames = new String[pointsData.dataDetail.size()];
        List<GroupPointData> groups = pointsData.dataDetail;

        for (int i = 0; i < groups.size(); i++) {
            GroupPointData groupData = groups.get(i);
            if (groupData.isDay()) {
                dayNames[i] = groupData.getGroupPointValue()[0];
            }
        }

        return dayNames;
    }

    @Override
    public GroupPointData getPointDataAt(int monthIdx) {
        if (pointsData == null || pointsData.dataDetail.isEmpty())
            return null;
        return pointsData.dataDetail.get(monthIdx);
    }

    @Override
    public List<PointDetail> getPointDetails() {
        if (pointsData == null)
            return new ArrayList<>();
        List<PointDetail> pointCategories = new ArrayList<>();
        for (GroupPointData groupPoint : pointsData.dataDetail) {
            if (groupPoint == null)
                continue;
            if (groupPoint.pointDetails == null || groupPoint.pointDetails.isEmpty())
                continue;

            for (PointDetail pointDetail : groupPoint.pointDetails) {
                if (pointDetail == null)
                    continue;
                if (isContainsPointDetails(pointCategories, pointDetail.rewardProgram)) {
                    continue;
                }
                pointCategories.add(pointDetail);
            }
        }

        return pointCategories;
    }

    private boolean isContainsPointDetails(List<PointDetail> list, String rewardProgram) {
        boolean result = false;
        for (PointDetail pointDetail : list) {
            if (pointDetail.rewardProgram.equals(rewardProgram)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public List<RankDetail> getRanks() {
        if (pointsData == null || pointsData.dataDetail.isEmpty())
            return new ArrayList<>();

        return pointsData.dataDetail.get(0).rankDetails;
    }

    @Override
    public void onSuccess(LoyaltyPointsResponse pointsData) {
        if (pointsData == null || pointsData.dataDetail.isEmpty()) {
            view.onGetDataFailed("No data detail found");
            return;
        }

        this.pointsData = pointsData;
        //convert point and rank data to 2d array to satisfy view
        float[][] pointDataSet = new float[pointsData.dataDetail.size()][];
        RankDetail[][] rankDataSet = new RankDetail[pointsData.dataDetail.size()][pointsData.dataDetail.get(0).rankDetails.size()];
        List<PointDetail> pointDetailsDataSet = new ArrayList<>();

        for (int i = 0; i < pointDataSet.length; i++) {
            float[] yVals = new float[pointsData.dataDetail.get(i).pointDetails.size()];
            for (int j = 0; j < yVals.length; j++) {
                yVals[j] = Integer.valueOf(pointsData.dataDetail.get(i).pointDetails.get(j).point);
                pointDetailsDataSet.add(pointsData.dataDetail.get(i).pointDetails.get(j));
            }
            pointDataSet[i] = yVals;
        }

        for (int i = 0; i < rankDataSet.length; i++) {
            for (int j = 0; j < rankDataSet[i].length; j++) {
                rankDataSet[i][j] = pointsData.dataDetail.get(i).rankDetails.get(j);
            }
        }

        view.onDataReceived(pointDataSet, rankDataSet, pointDetailsDataSet);
    }

    @Override
    public void onFailed(String message) {
        view.onGetDataFailed(message);
    }
}
