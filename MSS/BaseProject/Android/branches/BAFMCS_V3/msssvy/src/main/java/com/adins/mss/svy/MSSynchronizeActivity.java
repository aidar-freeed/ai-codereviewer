package com.adins.mss.svy;

import android.content.Intent;
import android.os.Bundle;

import com.adins.mss.base.SynchronizeActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

public class MSSynchronizeActivity extends SynchronizeActivity{

	private Trace synchronizeSvyTrace;
	private FirebaseAnalytics screenName;

	@Override
	protected Intent getIntentMainMenu() {
		// TODO Auto-generated method stub
		return new Intent(this,NewMSMainActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		screenName = FirebaseAnalytics.getInstance(this);
		synchronizeSvyTrace = FirebasePerformance.getInstance().newTrace(getString(R.string.synchronize_trace_survey));
		activity = this;
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		//Set Firebase screen name
		screenName.setCurrentScreen(this,getString(com.adins.mss.base.R.string.screen_name_svy_synchronize),null);
		synchronizeSvyTrace.start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		synchronizeSvyTrace.stop();
		super.onPause();
	}
	
}
