package com.adins.mss.coll.tool;

import java.text.DecimalFormat;

import com.google.android.gms.maps.model.LatLng;

public class Constants {
	public static final int START_DATE_DIALOG_ID=0;
	public static final int END_DATE_DIALOG_ID=1;
	public static final String KEY_START_DATE="start date";
	public static final String KEY_END_DATE="end date";
	public static final String KEY_DATE="date";
	public static final String KEY_BUND_BATCHID="BATCHID";
	public static final double EARTH_RADIUS = 6378.1370;
	
	public double GetDistance(LatLng start, LatLng finish){
		double distance=0;
		
		double lat1=start.latitude;
		double lng1=start.longitude;
		double lat2=finish.latitude;
		double lng2=finish.longitude;
		
		double deltaLat=Math.toRadians(lat2-lat1);
		double deltaLng=Math.toRadians(lng2-lng1);
		lat1=Math.toRadians(lat1);
		lat2=Math.toRadians(lat2);
		
		double axis=Math.sin(deltaLat/2)*Math.sin(deltaLat/2)+
				Math.cos(lat1)*Math.cos(lat2)*Math.sin(deltaLng/2)*Math.sin(deltaLng/2);
		
		distance = EARTH_RADIUS* (2*Math.atan2(Math.sqrt(axis), Math.sqrt(1-axis)));
		DecimalFormat df=new DecimalFormat("#.###");		
		
		return distance;
	}
}
