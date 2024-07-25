package com.adins.mss.base.loyalti.sla;

import com.adins.mss.base.GlobalData;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LoyaltiSlaTimeHandler {

    private boolean isTracking;
    private int[] trackingDays;
    private int[] startTimeDigit;
    private int[] endTimeDigit;
    private long slaTime;

    public LoyaltiSlaTimeHandler() {
    }

    public LoyaltiSlaTimeHandler(int slaTime) {
        this.slaTime = (long) slaTime * Global.HOUR;

        //parsing tracking info from user properties
        User user = GlobalData.getSharedGlobalData().getUser();
        if (user == null) {
            return;
        }

        //parse isTracking flag
        isTracking = user.getIs_tracking() != null && user.getIs_tracking().equals("1");

        //parse track days
        String trackDays = user.getTracking_days() != null && !user.getTracking_days().isEmpty() ? user.getTracking_days() : null;
        if (trackDays == null) {
            return;
        }
        trackDays = trackDays.trim();
        String[] trackDaysSplit = trackDays.split(";");
        trackingDays = new int[trackDaysSplit.length];
        for (int i = 0; i < trackDaysSplit.length; i++) {
            trackingDays[i] = Integer.parseInt(trackDaysSplit[i]);
        }

        //parse start time
        String startTimeStr = user.getStart_time();
        startTimeStr = startTimeStr.trim();
        String[] startTimePart = startTimeStr.split(":");
        startTimeDigit = new int[2];
        startTimeDigit[0] = Integer.parseInt(startTimePart[0]);
        startTimeDigit[1] = Integer.parseInt(startTimePart[1]);

        //parse end time
        String endTimeStr = user.getEnd_time();
        endTimeStr = endTimeStr.trim();
        String[] endTimePart = endTimeStr.split(":");
        endTimeDigit = new int[2];
        endTimeDigit[0] = Integer.parseInt(endTimePart[0]);
        endTimeDigit[1] = Integer.parseInt(endTimePart[1]);
    }

    public boolean isTracking() {
        return isTracking;
    }

    public void enableTracking(boolean tracking) {
        isTracking = tracking;
    }

    public int[] getTrackingDays() {
        return trackingDays;
    }

    public void setTrackingDays(int[] trackingDays) {
        this.trackingDays = trackingDays;
    }

    public int[] getStartTimeDigit() {
        return startTimeDigit;
    }

    public void setStartTimeDigit(int[] startTimeDigit) {
        this.startTimeDigit = startTimeDigit;
    }

    public int[] getEndTimeDigit() {
        return endTimeDigit;
    }

    public void setEndTimeDigit(int[] endTimeDigit) {
        this.endTimeDigit = endTimeDigit;
    }

    public long getSlaTime() {
        return slaTime;
    }

    public void setSlaTime(long slaTime) {
        this.slaTime = slaTime;
    }

    public String calculateSLATime(Date assignDate, String outputFormat) {
        Calendar assignTime = Calendar.getInstance();
        assignTime.setTime(assignDate);
        Calendar assignTimeClone = (Calendar) assignTime.clone();
        long totalSla = slaTime;

        SlaTimeResult result = decreaseSLADuration(0, assignTimeClone, totalSla);
        return result.getResult(assignTime, outputFormat);
    }

    public Date calculateSLATime(Date assignDate) {
        Calendar assignTime = Calendar.getInstance();
        assignTime.setTime(assignDate);
        Calendar assignTimeClone = (Calendar) assignTime.clone();
        long totalSla = slaTime;

        SlaTimeResult result = decreaseSLADuration(0, assignTimeClone, totalSla);
        return result.getResult(assignTime);
    }

    private SlaTimeResult decreaseSLADuration(int dayCounter, Calendar date, long slaDuration) {
        long usedDuration = 0;

        int day = date.get(Calendar.DAY_OF_WEEK) - 1;
        if (needProcessSLA(day)) {
            Calendar startTime = (Calendar) date.clone();
            startTime.set(Calendar.HOUR_OF_DAY, startTimeDigit[0]);
            startTime.set(Calendar.MINUTE, startTimeDigit[1]);

            Calendar endTime = (Calendar) date.clone();
            endTime.set(Calendar.HOUR_OF_DAY, endTimeDigit[0]);
            endTime.set(Calendar.MINUTE, endTimeDigit[1]);

            //check is before startTime
            if (date.getTimeInMillis() <= startTime.getTimeInMillis()) {
                usedDuration = endTime.getTimeInMillis() - startTime.getTimeInMillis();
            } else if (date.getTimeInMillis() > startTime.getTimeInMillis() && date.getTimeInMillis() < endTime.getTimeInMillis()) {
                usedDuration = endTime.getTimeInMillis() - date.getTimeInMillis();
            }
            slaDuration -= usedDuration;
            if (slaDuration < 0) {
                usedDuration = slaDuration + usedDuration;
                slaDuration = 0;
            }
        }

        if (slaDuration == 0) {
            return new SlaTimeResult(dayCounter, usedDuration);
        } else {
            dayCounter += 1;
            date.set(Calendar.HOUR_OF_DAY, startTimeDigit[0]);
            date.set(Calendar.MINUTE, startTimeDigit[1]);
            incrementDays(date, 1);
            return decreaseSLADuration(dayCounter, date, slaDuration);
        }
    }

    private void incrementDays(Calendar current, int inc) {
        current.add(Calendar.DATE, inc);
    }

    private boolean needProcessSLA(int dayIdx) {

        if (!isTracking || trackingDays == null || trackingDays.length == 0) {
            return true;
        }

        for (int i = 0; i < trackingDays.length; i++) {
            if (trackingDays[i] == dayIdx) {
                return true;
            }
        }
        return false;
    }

    class SlaTimeResult {
        private int dayCounter;
        private long usedDuration;

        public SlaTimeResult(int dayCounter, long usedDuration) {
            this.dayCounter = dayCounter;
            this.usedDuration = usedDuration;
        }

        public String getResult(Calendar assignDate, String outputFormat) {
            incrementDays(assignDate, dayCounter);
            Calendar startTime = (java.util.Calendar) assignDate.clone();
            startTime.set(Calendar.HOUR_OF_DAY, startTimeDigit[0]);
            startTime.set(Calendar.MINUTE, startTimeDigit[1]);

            if (dayCounter > 0 || (dayCounter == 0 && assignDate.before(startTime))) {
                assignDate.set(Calendar.HOUR_OF_DAY, startTimeDigit[0]);
                assignDate.set(Calendar.MINUTE, startTimeDigit[1]);
            }

            assignDate.add(Calendar.MILLISECOND, (int) usedDuration);
            SimpleDateFormat dateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());
            return dateFormat.format(assignDate.getTime());
        }

        public Date getResult(Calendar assignDate) {
            incrementDays(assignDate, dayCounter);
            if (dayCounter > 0) {
                assignDate.set(Calendar.HOUR_OF_DAY, startTimeDigit[0]);
                assignDate.set(Calendar.MINUTE, startTimeDigit[1]);
            }
            assignDate.add(Calendar.MILLISECOND, (int) usedDuration);
            return assignDate.getTime();
        }
    }

}
