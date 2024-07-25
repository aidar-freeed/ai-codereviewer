package com.adins.mss.foundation.services;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.foundation.camerainapp.helper.Logger;

/**
 * @author glen.iglesias
 *         <p>
 *         Object to store interval and event to trigger, used by AutoSendThread
 *         <p>
 *         example from:
 *         http://stackoverflow.com/questions/6776327/how-to-pause-resume-thread-in-android
 */
public class ScheduledItem extends Thread {


    String scheduleId;
    /**
     * Interval for which event will be triggered each time
     */
    int interval;
    ScheduledItemHandler handler;
    private Object mPauseLock = new Object();
    private boolean mPaused = false;
    public ScheduledItem(String id, int interval, ScheduledItemHandler handler) {
        super();
        this.scheduleId = id;
        this.interval = interval;
        this.handler = handler;
    }

    public ScheduledItem(String id, int interval) {
        super();
        this.scheduleId = id;
        this.interval = interval;
    }

    //=== Thread Method ===//
    @Override
    public void run() {
        while (true) {

            try {
                handler.onEventTrigger(this);
            } catch (Exception e) {
                FireCrash.log(e);
                Logger.e("ScheduleItem " + scheduleId, "Exception occured");
                e.printStackTrace();
            }

            synchronized (mPauseLock) {

                //sleep by interval
                try {
                    mPauseLock.wait(interval);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    Thread.currentThread().interrupt();
                }

                //sleep by pause, if paused
                while (mPaused) {
                    try {
                        mPauseLock.wait();
                    } catch (InterruptedException e) {
                        FireCrash.log(e);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    /**
     * Call this on pause.
     */
    public void pauseSchedule() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }

    /**
     * Call this on resume.
     */
    public void resumeSchedule() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    //=== Getter and Setter ===//

    public ScheduledItemHandler getHandler() {
        return handler;
    }

    public void setHandler(ScheduledItemHandler handler) {
        this.handler = handler;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    enum State {
        PAUSED,
        ACTIVE,
        INACTIVE
    }

    /**
     * @author glen.iglesias
     *         <p>
     *         A Interface for handler to handle event trigger when interval counter reaches zero or below
     */
    public interface ScheduledItemHandler {
        /**
         * Callback when ScheduledItem first started or after a set of interval.
         *
         * @param schItem
         * @return true if should ignore interval and trigger another event just after the previous is done,
         * or false if next trigger should wait until next interval
         */
        public boolean onEventTrigger(ScheduledItem schItem);
    }

}
