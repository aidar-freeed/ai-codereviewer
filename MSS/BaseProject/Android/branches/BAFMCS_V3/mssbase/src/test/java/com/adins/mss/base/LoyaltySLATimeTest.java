package com.adins.mss.base;

import com.adins.mss.base.loyalti.sla.LoyaltiSlaTimeHandler;
import com.adins.mss.constant.Global;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoyaltySLATimeTest {

    private int slaTime = 3;
    private int[] trackingDays;
    private int[] startTimeDigit;
    private int[] endTimeDigit;
    private boolean enableTracking;

    private LoyaltiSlaTimeHandler generateHandler(){
        LoyaltiSlaTimeHandler slaTimeHandler = new LoyaltiSlaTimeHandler();//use default constructor
        slaTimeHandler.enableTracking(enableTracking);
        slaTimeHandler.setSlaTime((long) slaTime * Global.HOUR);
        slaTimeHandler.setTrackingDays(trackingDays);
        slaTimeHandler.setStartTimeDigit(startTimeDigit);
        slaTimeHandler.setEndTimeDigit(endTimeDigit);
        return slaTimeHandler;
    }

    private Date parseDate(String strDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat(Global.DATE_TIME_STR_FORMAT, Locale.getDefault());
        Date result = null;
        try {
            result = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @Test
    public void testScenario1(){
        slaTime = 3;
        enableTracking = true;
        trackingDays = new int[]{0,1,2,3,4,5,6};
        startTimeDigit = new int[]{9,0};
        endTimeDigit = new int[]{22,0};
        String assignDateStr = "14/10/2020 10:30";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 1 assertion: ","14/10/2020 13:30",slaTimeResult);
    }

    @Test
    public void testScenario2(){
        slaTime = 3;
        enableTracking = true;
        trackingDays = new int[]{1,2,3,4,5};
        startTimeDigit = new int[]{9,0};
        endTimeDigit = new int[]{22,0};
        String assignDateStr = "17/10/2020 10:30";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 2 assertion: ","19/10/2020 12:00",slaTimeResult);
    }

    @Test
    public void testScenario3(){
        slaTime = 3;
        enableTracking = true;
        trackingDays = new int[]{1,2,3,4,5};
        startTimeDigit = new int[]{9,0};
        endTimeDigit = new int[]{22,0};
        String assignDateStr = "16/10/2020 21:45";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 3 assertion: ","19/10/2020 11:45",slaTimeResult);
    }

    @Test
    public void testScenario4(){
        slaTime = 3;
        enableTracking = true;
        trackingDays = new int[]{2,3,4,5};
        startTimeDigit = new int[]{9,0};
        endTimeDigit = new int[]{17,0};
        String assignDateStr = "16/10/2020 17:30";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 4 assertion: ","20/10/2020 12:00",slaTimeResult);
    }

    @Test
    public void testScenario5(){
        slaTime = 3;
        enableTracking = true;
        trackingDays = new int[]{2,3,4,5};
        startTimeDigit = new int[]{9,0};
        endTimeDigit = new int[]{17,0};
        String assignDateStr = "17/10/2020 12:53";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 5 assertion: ","20/10/2020 12:00",slaTimeResult);
    }

    @Test
    public void testScenario6(){
        slaTime = 3;
        enableTracking = true;
        trackingDays = new int[]{2,3,4,5,6};
        startTimeDigit = new int[]{8,0};
        endTimeDigit = new int[]{21,0};
        String assignDateStr = "17/10/2020 20:00";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 6 assertion: ","20/10/2020 10:00",slaTimeResult);
    }

    @Test
    public void testScenario7(){
        slaTime = 3;
        enableTracking = false;
        trackingDays = new int[]{2,3,4,5};
        startTimeDigit = new int[]{9,0};
        endTimeDigit = new int[]{17,0};
        String assignDateStr = "17/10/2020 12:53";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 7 assertion: ","17/10/2020 15:53",slaTimeResult);
    }

    @Test
    public void testScenario8(){
        slaTime = 3;
        enableTracking = false;
        trackingDays = new int[]{0,1,2,3,6};
        startTimeDigit = new int[]{16,0};
        endTimeDigit = new int[]{23,59};
        String assignDateStr = "26/10/2020 12:53";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 8 assertion: ","26/10/2020 19:00",slaTimeResult);
    }

    @Test
    public void testScenario9(){
        slaTime = 3;
        enableTracking = true;
        trackingDays = new int[]{0,1,2,3,6};
        startTimeDigit = new int[]{16,0};
        endTimeDigit = new int[]{23,59};
        String assignDateStr = "26/10/2020 23:59";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 9 assertion: ","27/10/2020 19:00",slaTimeResult);
    }

    @Test
    public void testScenario10(){
        slaTime = 3;
        enableTracking = true;
        trackingDays = new int[]{2,3};
        startTimeDigit = new int[]{16,0};
        endTimeDigit = new int[]{23,59};
        String assignDateStr = "01/11/2020 12:59";

        LoyaltiSlaTimeHandler slaTimeHandler = generateHandler();
        Date assignDate = parseDate(assignDateStr);
        if(assignDate == null)
            throw new NullPointerException();
        String slaTimeResult = slaTimeHandler.calculateSLATime(assignDate,Global.DATE_TIME_STR_FORMAT);
        Assert.assertEquals("Scenario 9 assertion: ","03/11/2020 19:00",slaTimeResult);
    }

}
