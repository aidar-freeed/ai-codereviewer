package com.adins.mss.base.tracking;

import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LocationTrackingRequestJson extends MssRequestType {
    @SerializedName("listLocationInfo")
    public List<LocationInfo> listLocationInfo = new ArrayList<LocationInfo>();

    public String precentageBattery;
    public String dataUsage;

    public String getPrecentageBattery() {
        return precentageBattery;
    }

    public void setPrecentageBattery(String precentageBattery) {
        this.precentageBattery = precentageBattery;
    }

    public String getDataUsage() {
        return dataUsage;
    }

    public void setDataUsage(String dataUsage) {
        this.dataUsage = dataUsage;
    }

    //	public LocationTrackingRequestJson(){
//		super();
//		setAudit(GlobalData.getSharedGlobalData().getAuditData());
//		String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
//		getAudit().setCallerId(uuidUser);
//		KeyValue imei = new KeyValue("imei", GlobalData.getSharedGlobalData().getImei());
//		addItemToUnstructured(imei, false);
//	}

    public void addLocationInfoList(List<LocationInfo> listLocationInfo) {
        this.listLocationInfo.addAll(listLocationInfo);
    }

    public List<LocationInfo> getLocationInfoList() {
        return this.listLocationInfo;
    }

    public void clearLocationInfoList() {
        listLocationInfo.clear();
    }
}
