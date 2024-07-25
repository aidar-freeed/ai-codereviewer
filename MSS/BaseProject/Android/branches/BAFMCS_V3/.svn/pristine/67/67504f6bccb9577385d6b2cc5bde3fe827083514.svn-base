package com.adins.mss.coll.dashboardcollection.model;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CollResultDetail {

    public static final int PTP_TYPE = 1;
    public static final int COLLECTED_TYPE = 0;
    public static final int FAILED_TYPE = 2;

    private int type;
    private String agrNo;
    private String custName;
    private String result;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat dateParser;

    public CollResultDetail(int type,String agrNo, String custName, String result) {
        dateParser = new SimpleDateFormat(Global.DATE_STR_FORMAT1);
        dateFormatter = new SimpleDateFormat(Global.DATE_STR_FORMAT3);

        this.type = type;
        this.agrNo = agrNo;
        this.custName = custName;
        this.result = result;
    }

    public CollResultDetail() {
        dateParser = new SimpleDateFormat(Global.DATE_STR_FORMAT1);
        dateFormatter = new SimpleDateFormat(Global.DATE_STR_FORMAT3);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAgrNo() {
        return agrNo;
    }

    public void setAgrNo(String agrNo) {
        this.agrNo = agrNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getResult() {
        if(type == COLLECTED_TYPE){
            try{
                double doubleVal = Double.valueOf(result);
                return Tool.formatToCurrency(doubleVal);
            } catch (NumberFormatException e){
                e.printStackTrace();
                FireCrash.log(e);
                return "0";
            }
        }
        else if(type == PTP_TYPE){
            Date date = null;
            try {
                date = dateParser.parse(result);
            } catch (ParseException e) {
                e.printStackTrace();
                FireCrash.log(e);
                return "Invalid Date";
            }

            return dateFormatter.format(date);
        }
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
