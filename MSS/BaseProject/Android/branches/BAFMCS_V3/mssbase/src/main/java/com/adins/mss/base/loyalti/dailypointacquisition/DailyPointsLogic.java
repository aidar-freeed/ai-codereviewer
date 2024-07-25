package com.adins.mss.base.loyalti.dailypointacquisition;

import com.adins.mss.base.loyalti.model.GroupPointData;
import com.adins.mss.base.loyalti.model.LoyaltyPointsRequest;
import com.adins.mss.base.loyalti.monthlypointacquisition.contract.ILoyaltyPointsDataSource;
import com.adins.mss.constant.Global;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyPointsLogic {
    private ILoyaltyPointsDataSource dataSource;
    private String competitionStartDate;
    private int displayMonth;
    private int displayYear;

    public DailyPointsLogic(ILoyaltyPointsDataSource dataSource, String competitionStartDate, int displayMonth, int displayYear) {
        this.dataSource = dataSource;
        this.competitionStartDate = competitionStartDate;
        this.displayMonth = displayMonth;
        this.displayYear = displayYear;
    }

    public DailyPointsLogic(ILoyaltyPointsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void getDailyPointsData(LoyaltyPointsRequest reqData, ILoyaltyPointsDataSource.ReqPointsListener listener){
        dataSource.requestPointsData(reqData,listener);
    }

    public int getTotalPoints(List<GroupPointData> dataSet){
        if(dataSet == null)
            return 0;
        int totalPoints = 0;
        int[] totalPerIdx = new int[dataSet.size()];
        int sum;
        for(int i=0; i<totalPerIdx.length; i++){
            sum = 0;
            for (int stack=0; stack<dataSet.get(i).pointDetails.size(); stack++){
                int point = Integer.parseInt(dataSet.get(i).pointDetails.get(stack).point);
                sum += point;
            }
            totalPerIdx[i] = sum;
            totalPoints += totalPerIdx[i];
        }

        return totalPoints;
    }

    public float getAvgPoint(List<GroupPointData> dataSet){
        float totalPoints = getTotalPoints(dataSet);
        int divideDays = getTotalDaysMonth();
        return Math.round(totalPoints/divideDays);
    }

    private int getTotalDaysMonth(){
        Calendar calendar = Calendar.getInstance();

        //set current date
        calendar.setTime(new Date());
        int currMonth = calendar.get(Calendar.MONTH);
        int currYear = calendar.get(Calendar.YEAR);
        int currDay = calendar.get(Calendar.DAY_OF_MONTH);

        //set competition start date
        DateFormat dateFormat = new SimpleDateFormat(Global.DATE_STR_FORMAT1);
        Date startCompDate = null;
        try {
            startCompDate = dateFormat.parse(competitionStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startCompDate == null) {
            return 1;
        }

        calendar.setTime(startCompDate);
        int compeYear = calendar.get(Calendar.YEAR);
        int compeMonth = calendar.get(Calendar.MONTH);
        int compeDay = calendar.get(Calendar.DAY_OF_MONTH);

        int maxDays;
        if (currMonth == compeMonth && currYear == compeYear) {
            //Bulan sekarang sama dengan bulan mulai kompetisi
            maxDays = currDay - compeDay;
        } else if (currMonth == displayMonth && currYear == displayYear) {
            //Bulan sekarang sama dengan bulan yang dipilih pada grafik
            maxDays = currDay - 1;
        } else {
            //sudah lewat
            calendar.set(Calendar.MONTH, displayMonth);
            calendar.set(Calendar.YEAR, displayYear);
            maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        return maxDays;
    }

    public int getMaxPoint(List<GroupPointData> dataSet){
        if(dataSet == null)
            return 0;

        int max = 0;
        int[] totalPerIdx = new int[dataSet.size()];
        int sum = 0;
        for(int i=0; i<totalPerIdx.length; i++){
            sum = 0;
            for (int stack=0; stack<dataSet.get(i).pointDetails.size(); stack++){
                int point = Integer.parseInt(dataSet.get(i).pointDetails.get(stack).point);
                sum += point;
            }
            totalPerIdx[i] = sum;
        }

        Arrays.sort(totalPerIdx);
        max = totalPerIdx[totalPerIdx.length -1];
        return max;
    }

}

