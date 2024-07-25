package com.adins.mss.base.tasklog;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.log.Log;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;

import java.util.List;

public class TaskLog {
	private static Context context;
	private static List<TaskH> listTask;
	//private static String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
	
	public TaskLog(Context context){
		this.context = context;		
	}
	
	public List<TaskH> getListTaskLog(){
		Log log = new Log(this.context);
		//listTask = TaskHDataAccess.getAllSentTask(context, uuidUser);
		listTask = log.getAllSentTaskWithLimited();
		Global.listOfSentTask = listTask;
		return listTask;
	}
	
	public static long getCounterLog(Context context){
		long counter=0;		
		try {
			String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
			counter = TaskHDataAccess.getSentTaskCounter(context, uuidUser);
		} catch (Exception e) {
			// TODO: handle exception
		}
		int MAXIMUM_DATA_KEEP = GlobalData.getSharedGlobalData().getMaxDataInLog();
		if(counter>MAXIMUM_DATA_KEEP && MAXIMUM_DATA_KEEP!=0) counter = MAXIMUM_DATA_KEEP;
		return counter;
	}

	public static long getCounterLogAllUser(Context context){
		long counter=0;
		try {
			counter = TaskHDataAccess.getSentTaskCounterAllUser(context);
		} catch (Exception e) {
			// TODO: handle exception
		}
		int MAXIMUM_DATA_KEEP = GlobalData.getSharedGlobalData().getMaxDataInLog();
		if(counter>MAXIMUM_DATA_KEEP && MAXIMUM_DATA_KEEP!=0) counter = MAXIMUM_DATA_KEEP;
		return counter;
	}

	public List<TaskH> doRefresh(){
		return getListTaskLog();
	}
}
