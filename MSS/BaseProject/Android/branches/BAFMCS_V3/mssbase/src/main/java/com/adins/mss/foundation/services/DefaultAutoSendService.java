package com.adins.mss.foundation.services;

import android.app.Service;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.autosend.AutoSubmitForm;
import com.adins.mss.base.autosend.AutoSubmitImage;
import com.adins.mss.base.tracking.LocationTrackingSchedule;

/**
 * A default AutoSendService which will include LocationTracking, AutoSubmitForm, and AutoSubmitImage ScheduledItem
 * as the service is created.
 * It save the hassle of accessing this service and manually register said default ScheduledItem
 * <p>If there is a need to access this service and register more ScheduledItem, might need to check bindService() in http://developer.android.com/guide/components/services.html
 * <p>Register this service to AndroidManifest and call from StartService() to use
 * <br>Make sure both GlobalData.intervalTracking and GlobalData.intervalAutoSend or they will use default value
 *
 * @author glen.iglesias
 * @see Service
 * @see AutoSendTaskService
 * @see GlobalData
 */
public class DefaultAutoSendService extends AutoSendSerivce {

    public static final String SCHED_LOC_TRACKING = "schedule_location_tracking";
    public static final String SCHED_AUTO_SUBMIT = "schedule_auto_submit";
    public static final String SCHED_AUTO_SUBMIT_IMAGE = "schedule_auto_submit_image";

    public DefaultAutoSendService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        GlobalData globData = GlobalData.getSharedGlobalData();
        boolean enc = globData.isEncrypt();
        boolean dec = globData.isDecrypt();

        //TODO replace userid
        //get userId from globalData

        //Cancel fetch from database because the process should be to set all from database to GlobalData after login
        //Fetch from database instead of GlobalData, to enable multi-user saved on database just in case needed
//		List<GeneralParameter> parameters = GeneralParameterDataAccess.getAll(this, "test");

        //Location Tracking
        int locTrackSchedInterval = GlobalData.getSharedGlobalData().getIntervalTracking();
        if (locTrackSchedInterval == -1) locTrackSchedInterval = 10000;
        String urlTracking = globData.getUrlTracking();
        LocationTrackingSchedule locTrackSched = new LocationTrackingSchedule(this, SCHED_LOC_TRACKING, locTrackSchedInterval, urlTracking, enc, dec);
        addScheduledItem(locTrackSched);

        String urlSubmit = globData.getURL_SUBMITTASK();
        boolean isPartial = globData.isPartialSending();

        //Auto Submit Form
        int autoSubmitSchedInterval = GlobalData.getSharedGlobalData().getIntervalAutoSend();
        if (autoSubmitSchedInterval == -1) autoSubmitSchedInterval = 100000;
//		AutoSubmitForm autoSubmitFormSched = new AutoSubmitForm(this, SCHED_AUTO_SUBMIT, autoSubmitSchedInterval, urlSubmit, enc, dec);
        AutoSubmitForm autoSubmitFormSched = new AutoSubmitForm(this, SCHED_AUTO_SUBMIT, autoSubmitSchedInterval, urlSubmit, enc, dec, isPartial);
        addScheduledItem(autoSubmitFormSched);

        //Auto Submit Image
        int autoSubmitImageSchedInterval = GlobalData.getSharedGlobalData().getIntervalAutoSend();
        if (autoSubmitImageSchedInterval == -1) autoSubmitImageSchedInterval = 100000;
//		AutoSubmitImage autoSubmitImageSched = new AutoSubmitImage(this, SCHED_AUTO_SUBMIT_IMAGE, autoSubmitImageSchedInterval, urlSubmit, enc, dec, isPartial);
        AutoSubmitImage autoSubmitImageSched = new AutoSubmitImage(this, SCHED_AUTO_SUBMIT_IMAGE, autoSubmitImageSchedInterval, urlSubmit, enc, dec);
        addScheduledItem(autoSubmitImageSched);

    }

}
