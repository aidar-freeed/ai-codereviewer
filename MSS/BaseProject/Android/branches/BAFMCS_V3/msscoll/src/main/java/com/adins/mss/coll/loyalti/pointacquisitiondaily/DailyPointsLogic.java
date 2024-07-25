package com.adins.mss.coll.loyalti.pointacquisitiondaily;

import com.adins.mss.coll.loyalti.pointacquisitionmonthly.contracts.ILoyaltyPointsDataSource;
import com.adins.mss.coll.models.loyaltymodels.GroupPointData;
import com.adins.mss.coll.models.loyaltymodels.LoyaltyPointsRequest;
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
        int _sum = 0;
        for(int i=0; i<totalPerIdx.length; i++){
            _sum = 0;
            for (int stack=0; stack<dataSet.get(i).pointDetails.size(); stack++){
                int point = Integer.valueOf(dataSet.get(i).pointDetails.get(stack).point);
                _sum += point;
            }
            totalPerIdx[i] = _sum;
            totalPoints += totalPerIdx[i];
        }

        return totalPoints;
    }

    public float getAvgPoint(List<GroupPointData> dataSet){
        if(dataSet == null)
            return 0;
        float totalPoints = 0;
        int avg = 0;
        int[] totalPerIdx = new int[dataSet.size()];
        int _sum = 0;
        for(int i=0; i<totalPerIdx.length; i++){
            _sum = 0;
            for (int stack=0; stack<dataSet.get(i).pointDetails.size(); stack++){
                int point = Integer.valueOf(dataSet.get(i).pointDetails.get(stack).point);
                _sum += point;
            }
            totalPerIdx[i] = _sum;
            totalPoints += totalPerIdx[i];
        }

        int divideDays = getTotalDaysMonth();
        avg = Math.round(totalPoints/divideDays);
        return avg;
    }

    private int getTotalDaysMonth(){
        Calendar calendar = Calendar.getInstance();

        //current date
        calendar.setTime(new Date());
        int currMonth = calendar.get(Calendar.MONTH);
        int currYear = calendar.get(Calendar.YEAR);
        int currDay = calendar.get(Calendar.DAY_OF_MONTH);

        int maxDays = 0;
        //tentukan tanggal maksimum
        if(currMonth == displayMonth && currYear == displayYear){
            //bulan berjalan
            maxDays = currDay - 1;
        }else {
            //sudah lewat
            calendar.set(Calendar.MONTH,displayMonth);
            calendar.set(Calendar.YEAR,displayYear);
            maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        //competition start date
        DateFormat dateFormat = new SimpleDateFormat(Global.DATE_STR_FORMAT1);
        Date startCompDate = null;
        try {
            startCompDate = dateFormat.parse(competitionStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(startCompDate == null)
            return 1;

        calendar.setTime(startCompDate);
        int cYear = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH);
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);

        //hitung tanggal maksimum berdasarkan tanggal kompetisi mulai
        if(displayYear == cYear && displayMonth == cMonth){
            maxDays = (maxDays - cDay);
        }

        return maxDays;
    }

    public int getMaxPoint(List<GroupPointData> dataSet){
        if(dataSet == null)
            return 0;

        int max = 0;
        int[] totalPerIdx = new int[dataSet.size()];
        int _sum = 0;
        for(int i=0; i<totalPerIdx.length; i++){
            _sum = 0;
            for (int stack=0; stack<dataSet.get(i).pointDetails.size(); stack++){
                int point = Integer.valueOf(dataSet.get(i).pointDetails.get(stack).point);
                _sum += point;
            }
            totalPerIdx[i] = _sum;
        }

        Arrays.sort(totalPerIdx);
        max = totalPerIdx[totalPerIdx.length -1];
        return max;
    }

}
