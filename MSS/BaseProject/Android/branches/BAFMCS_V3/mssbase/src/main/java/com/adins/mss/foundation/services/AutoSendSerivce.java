package com.adins.mss.foundation.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.adins.mss.base.GlobalData;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.services.ForegroundServiceNotification;

import java.util.ArrayList;
import java.util.List;


/**
 * @author glen.iglesias
 *         A Service to hold several Threads which supposed to work on background. Such Thread is actually a subclass of Thread,
 *         named ScheduledItem. This class is tightly coupled to GlobalData, so application could access this object via GlobalData
 *         <p>
 *         Remember to make sure Service is started before accessing from GlobalData, as GlobalData is set when the most recent Service
 *         started
 */
public class AutoSendSerivce extends Service {

    private List<ScheduledItem> scheduledItems;

    public AutoSendSerivce() {
    }

    //=== Method ===//

    /**
     * Add ScheduledItem into AutoSendService, and start the ScheduledItem. If an ScheduledItem with same scheduleId
     * already existed, the new ScheduledItem will be rejected
     *
     * @param item ScheduledItem to be added into list
     */
    public void addScheduledItem(ScheduledItem item) {
        if (scheduledItems == null) {
            scheduledItems = new ArrayList<ScheduledItem>();
        }

        //Check for double id
        ScheduledItem compare = getScheduleItemByName(item.getScheduleId());
        if (compare != null && compare.getScheduleId().equals(item.getScheduleId())) {
            Logger.e("AutoSendService", "ScheduledItem with same id exist");
            return;
        }

        scheduledItems.add(item);
        item.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("AutoSendServce", "onCreateCalled");
        //Tightly coupling self with GlobalData
        GlobalData.getSharedGlobalData().setAutoSendThread(this);

        startForeground(ForegroundServiceNotification.getNotificationId(),
                ForegroundServiceNotification.getNotification(this));

//		for (ScheduledItem schdItem : scheduledItems){
//			schdItem.start();
//		}
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * @param scheduleId ScheduledItem id of which to be retrieved
     * @return ScheduledItem with scheduleId, or null if no scheduleId match
     */
    public ScheduledItem getScheduleItemByName(String scheduleId) {
        for (ScheduledItem item : scheduledItems) {
            if (scheduleId.equals(item.getScheduleId())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Resume a paused ScheduledItem by scheduleId. If the interval has passed before it's resumed,
     * it will immediately trigger scheduled event.
     *
     * @param scheduleId scheduleId of ScheduledItem which to be resumed after being paused
     */
    public void resumeScheduledItem(String scheduleId) {
        ScheduledItem schedItem = getScheduleItemByName(scheduleId);
        schedItem.resumeSchedule();
    }

    /**
     * Pause a ScheduledItem by scheduleId. The interval counter still continue but won't trigger any event
     * if it's paused, and will be triggered on resume
     *
     * @param scheduleId scheduleId of ScheduledItem which to be paused
     */
    public void pauseScheduledItem(String scheduleId) {
        ScheduledItem schedItem = getScheduleItemByName(scheduleId);
        schedItem.pauseSchedule();
    }

}
