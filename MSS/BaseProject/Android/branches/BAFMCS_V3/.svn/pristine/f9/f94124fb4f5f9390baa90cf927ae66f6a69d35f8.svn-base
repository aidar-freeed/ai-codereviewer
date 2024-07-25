package com.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.svy.MSMainMenuActivity;

public class SurveyApprovalService extends Service {
	private SurveyApprovalThread auto;
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSurveyApprovalThread();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		auto = MSMainMenuActivity.approvalThread;
		startSurveyApprovalThread();
	}
	
	public synchronized void startSurveyApprovalThread() {
		if (auto == null) {
			auto = new SurveyApprovalThread(getApplicationContext());
			auto.start();
		}
		else {
			auto.start();
		}
	}
	
    public synchronized void stopSurveyApprovalThread() {
		if (auto != null) {
			auto.requestStop();
			auto = null;
		}
	}


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
