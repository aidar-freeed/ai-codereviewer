package com.adins.mss.odr;

import android.content.Intent;
import android.os.Bundle;

import com.adins.mss.base.SynchronizeActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

public class MOSynchronizeActivity extends SynchronizeActivity{

	private Trace synchronizeOrdTrace;
	private FirebaseAnalytics screenName;

	@Override
	protected Intent getIntentMainMenu() {
		// TODO Auto-generated method stub
		return new Intent(this, NewMOMainActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		screenName = FirebaseAnalytics.getInstance(this);
		synchronizeOrdTrace = FirebasePerformance.getInstance().newTrace(getString(R.string.synchronize_trace_order));
		activity = this;
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		//Set Firebase screen name
		screenName.setCurrentScreen(this,getString(com.adins.mss.base.R.string.screen_name_odr_synchronize),null);
		synchronizeOrdTrace.start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		synchronizeOrdTrace.stop();
		super.onPause();
	}

}
