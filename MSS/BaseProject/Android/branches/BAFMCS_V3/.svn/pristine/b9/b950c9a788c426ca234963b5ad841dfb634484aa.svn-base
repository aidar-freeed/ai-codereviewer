package com.adins.mss.foundation.location;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.db.dataaccess.LocationInfoDataAccess;

import java.util.List;


/**
 * @author gigin.ginanjar
 */
public class UserLocationBean extends LocationInfo {

    /**
     * Gets List of Locations from Database
     *
     * @param context
     * @return all Location
     */

    public static List<LocationInfo> getFromDB(Context context) {
        String uuid = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return LocationInfoDataAccess.getAll(context, uuid);
    }

//	public String toSubmitString() {
//		Gson gson = new Gson();
//		return gson.toJson(this);
//	}

    /**
     * Set a location
     *
     * @param locationInfo
     */
    public void setLocation(LocationInfo locationInfo) {
        this.setUuid_location_info(locationInfo.getUuid_location_info());
        this.setLatitude(locationInfo.getLatitude());
        this.setLongitude(locationInfo.getLongitude());
        this.setMcc(locationInfo.getMcc());
        this.setMnc(locationInfo.getMnc());
        this.setLac(locationInfo.getLac());
        this.setCid(locationInfo.getCid());
        this.setHandset_time(locationInfo.getHandset_time());
        this.setMode(locationInfo.getMode());
        this.setAccuracy(locationInfo.getAccuracy());
        this.setGps_time(locationInfo.getGps_time());
        this.setIs_gps_time(locationInfo.getIs_gps_time());
        this.setUsr_crt(locationInfo.getUsr_crt());
        this.setDtm_crt(locationInfo.getDtm_crt());
        this.setUuid_user(locationInfo.getUuid_user());
    }

    public String toAnswerString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLatitude()).append(",")
                .append(getLongitude()).append(",")
                .append(getCid()).append(",")
                .append(getMcc()).append(",")
                .append(getMnc()).append(",")
                .append(getLac());
        return sb.toString();
    }

}