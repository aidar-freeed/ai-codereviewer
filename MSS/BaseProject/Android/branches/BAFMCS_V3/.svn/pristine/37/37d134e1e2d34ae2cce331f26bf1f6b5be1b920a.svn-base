package com.adins.mss.coll.loyalti.pointacquisitionmonthly;

import com.adins.mss.coll.loyalti.pointacquisitionmonthly.contracts.ILoyaltyPointsDataSource;
import com.adins.mss.coll.models.loyaltymodels.GroupPointData;
import com.adins.mss.coll.models.loyaltymodels.LoyaltyPointsRequest;
import com.adins.mss.coll.models.loyaltymodels.RankDetail;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthlyPointsLogic {
    private ILoyaltyPointsDataSource dataSource;
    private String competitionStartDate;
    private int displayYear;

    public MonthlyPointsLogic(ILoyaltyPointsDataSource dataSource, String competitionStartDate, int displayYear) {
        this.dataSource = dataSource;
        this.competitionStartDate = competitionStartDate;
        this.displayYear = displayYear;
    }

    public MonthlyPointsLogic(ILoyaltyPointsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void getMonthlyPointsData(LoyaltyPointsRequest reqData, ILoyaltyPointsDataSource.ReqPointsListener listener){
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

        int divideMonths = getTotalMonthsYear();
        avg = Math.round(totalPoints/divideMonths);
        return avg;
    }

    private int getTotalMonthsYear(){
        Calendar calendar = Calendar.getInstance();

        //current date
        calendar.setTime(new Date());
        int currMonth = calendar.get(Calendar.MONTH);
        int currYear = calendar.get(Calendar.YEAR);

        int maxMonths = 0;
        //tentukan bulan maksimum
        if(currYear == displayYear){
            //tahun berjalan
            maxMonths = currMonth;
        }else {
            //sudah lewat
            calendar.set(Calendar.YEAR,displayYear);
            maxMonths = calendar.getActualMaximum(Calendar.MONTH);
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

        //hitung bulan maksimum berdasarkan tanggal kompetisi mulai
        if(displayYear == cYear){
            maxMonths = (maxMonths - cMonth) + 1;
        }

        return maxMonths;
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

    public List<RankDetail> updateJobName(List<RankDetail> rankDetails){
        if(rankDetails == null || rankDetails.size() == 0)
            return rankDetails;

        List<String> jobNames = new ArrayList<>();
        for (RankDetail rank:rankDetails) {
            if(rank == null)
                continue;
            jobNames.add(rank.level);
        }
        List<GeneralParameter> jobParams = dataSource.getJobsGenParam(jobNames);
        if(jobParams.size() == 0)
            return rankDetails;

        for (RankDetail rank:rankDetails) {
            if(rank == null)
                continue;
            for (GeneralParameter jobParam:jobParams){
                if(rank.level.equalsIgnoreCase(jobParam.getGs_value())){
                    String jobParamCode = jobParam.getGs_code();
                    if(jobParamCode.equalsIgnoreCase("MC_JOBNATIONAL")){
                        rank.level = "National";
                    }
                    else if(jobParamCode.equalsIgnoreCase("MC_JOBBM")){
                        rank.level = "Branch";
                    }
                    else if(jobParamCode.equalsIgnoreCase("MC_JOBSPV")){
                        rank.level = "SPV";
                    }
                }
            }
        }
        return rankDetails;
    }

}
