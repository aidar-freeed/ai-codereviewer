package com.adins.mss.odr.simulasi;

import java.util.List;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.todolist.DoList;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;

public class CreditSimulation {
	private Context mContext;
	private static List<Scheme> simulationScheme;
	private User user = GlobalData.getSharedGlobalData().getUser();
	
	public CreditSimulation(Context context){
		mContext=context;
	}
	
	public void getSchemeFromServer() {
		try {
			DoList doList = new DoList(mContext);
			doList.doRefresh();
		} catch (Exception e) { }
	}

	public List<QuestionSet> getQuestionSet(String uuid_scheme) {	
		return QuestionSetDataAccess.getAll(mContext, uuid_scheme);
	}

	public List<Scheme> getSimulationScheme(){
		try {
			if(simulationScheme == null)
				simulationScheme = SchemeDataAccess.getAllSimulateScheme(mContext);
		} catch (Exception e) {
			getSchemeFromServer();
			simulationScheme = SchemeDataAccess.getAllSimulateScheme(mContext);
		}
		return simulationScheme;
	}
	
	public TaskH getNewTaskH(Scheme scheme){
		TaskH taskH=null;
		taskH = new TaskH();
		
		taskH.setUser(user);
		taskH.setScheme(scheme);
		return taskH;
	}
}
