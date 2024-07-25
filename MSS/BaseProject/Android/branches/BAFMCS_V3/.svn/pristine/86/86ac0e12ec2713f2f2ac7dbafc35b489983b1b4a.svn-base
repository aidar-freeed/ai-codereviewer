package com.adins.mss.svy.tool;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;

import java.util.List;

public class Constants {
	public static final int START_DATE_DIALOG_ID=0;
	public static final int END_DATE_DIALOG_ID=1;
	public static final String KEY_START_DATE="start date";
	public static final String KEY_END_DATE="end date";
	public static final String KEY_DATE="date";
	public static List<TaskH> listOfVerifiedTask;
	public static List<TaskH> listOfApprovalTask;
	
	public static long getCounterVerificationTask(Context context){
		long counter=0;		
		try {
			counter = TaskHDataAccess.getVerificationTaskCounterByUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
		} catch (Exception e) {
			FireCrash.log(e);
			// TODO: handle exception
		}
		return counter;
	}
	
	public static long getCounterApprovalTask(Context context){
		long counter=0;		
		try {
			counter = TaskHDataAccess.getApprovalTaskCounterByUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
		} catch (Exception e) {
			FireCrash.log(e);
			// TODO: handle exception
		}
		return counter;
	}
	
	public static long getCounterVerificationTaskByBranch(Context context){
		long counter=0;		
		try {
			counter = TaskHDataAccess.getVerificationTaskCounterByBranch(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
		} catch (Exception e) {
			FireCrash.log(e);
			// TODO: handle exception
		}
		return counter;
	}
	
	public static long getCounterApprovalTaskByBranch(Context context){
		long counter=0;		
		try {
			counter = TaskHDataAccess.getApprovalTaskCounterByBranch(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
		} catch (Exception e) {
			FireCrash.log(e);
			// TODO: handle exception
		}
		return counter;
	}
}
