package com.adins.mss.base.tracking;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.LocationInfoDataAccess;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.services.ScheduledConnectionItem;
import com.adins.mss.foundation.services.ScheduledItem.ScheduledItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Default ScheduledConnectionItem for location tracking. It automatically query locations info from database and try to send it over
 * Internet connection to server
 *
 * @author glen.iglesias
 */
public class LocationTrackingSchedule extends ScheduledConnectionItem implements ScheduledItemHandler {

    Context context;
    List<LocationInfo> processedList;

    /**
     * @param context
     * @param id       of ScheduledItem to be registered to AutoSendService
     * @param interval time delay between trigger
     * @param url      target URL
     * @param encrypt  need of encryption
     * @param decrypt  need of decryption
     */
    public LocationTrackingSchedule(Context context, String id, int interval, String url, boolean encrypt, boolean decrypt) {
        super(id, interval, url, encrypt, decrypt);
        this.context = context;
        this.url = url;
        this.setHandler(this);

        this.processedList = new ArrayList<>();
    }

    @Override
    protected String getData() {
        try {
            GlobalData.getSharedGlobalData().getUser().getUuid_user();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        //add to database
        LocationInfoDataAccess.add(context, Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_TRACKING));
        return "";
    }

    @Override
    protected boolean onSuccess(HttpConnectionResult result) {
        LocationInfoDataAccess.deleteList(context, processedList);
        processedList.clear();
        Logger.d("LocationTracking", "success connection");
        return false;
    }

    @Override
    protected boolean onFail(HttpConnectionResult result) {
        processedList.clear();
        Logger.d("LocationTracking", "fail connection");
        return false;
    }
}
