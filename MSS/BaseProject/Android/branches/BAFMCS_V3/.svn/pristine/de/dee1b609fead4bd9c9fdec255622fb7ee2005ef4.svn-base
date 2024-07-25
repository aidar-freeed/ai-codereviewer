package com.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.svy.MSMainMenuActivity;

public class SurveyVerificationService extends Service {
	private SurveyVerificationThread auto;
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSurveyVerificationThread();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		auto = MSMainMenuActivity.verificationThread;
		startSurveyVerificationThread();
	}
	
	public synchronized void startSurveyVerificationThread() {
		if (auto == null) {
			auto = new SurveyVerificationThread(getApplicationContext());
			auto.start();
		}
		else {
			auto.start();
		}
	}
	
    public synchronized void stopSurveyVerificationThread() {
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
