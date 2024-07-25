package com.adins.mss.coll;

import android.content.Intent;
import android.os.Bundle;

import com.adins.mss.base.SynchronizeActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

public class MCSynchronizeActivity extends SynchronizeActivity{

	private Trace synchronizeCollTrace;
	private FirebaseAnalytics screenName;

	@Override
	protected Intent getIntentMainMenu() {
		// TODO Auto-generated method stub
		return new Intent(this, NewMCMainActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		screenName = FirebaseAnalytics.getInstance(this);
		//Firebase custome trace
		synchronizeCollTrace = FirebasePerformance.getInstance().newTrace(getString(R.string.synchronize_trace_coll));
		activity = this;
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		synchronizeCollTrace.start();
		//Set Firebase screen name
		screenName.setCurrentScreen(this, getString(R.string.screen_name_coll_synchronize), null);
		super.onResume();
	}

	@Override
	protected void onPause() {
		synchronizeCollTrace.stop();
		super.onPause();
	}
}
