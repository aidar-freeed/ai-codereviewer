package com.adins.mss.svy.reassignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.svy.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class OrderFilterActivity extends CheckOrderActivity{
	private int taskType;
	private Spinner cbSearchByStatus;
	private TextView labelSearchByStatus;
	private RelativeLayout layoutFilterByStatus;
	private Bundle mArguments;
	private FirebaseAnalytics screenName;
	public OrderFilterActivity() {
	}
	
	@Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        mArguments = getArguments();        
        taskType = mArguments.getInt(Global.BUND_KEY_TASK_TYPE, 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.mnGuide){
			if(!Global.BACKPRESS_RESTRICTION) {

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						UserHelp.showAllUserHelp(OrderFilterActivity.this.getActivity(), OrderFilterActivity.this.getClass().getSimpleName());
					}
				}, SHOW_USERHELP_DELAY_DEFAULT);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		screenName = FirebaseAnalytics.getInstance(getActivity());
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				UserHelp.showAllUserHelp(OrderFilterActivity.this.getActivity(),OrderFilterActivity.this.getClass().getSimpleName());
			}
		}, SHOW_USERHELP_DELAY_DEFAULT);
		return view;
	}

	private void addStatusFilter() {
		labelSearchByStatus =  (TextView)view.findViewById(R.id.lblSearchByStatus);
		cbSearchByStatus = (Spinner)view.findViewById(R.id.cbSearchByStatus);
		layoutFilterByStatus = (RelativeLayout)view.findViewById(R.id.filterByStatus);
		labelSearchByStatus.setVisibility(View.VISIBLE);
		layoutFilterByStatus.setVisibility(View.VISIBLE);
		ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource
				(getActivity(), R.array.cbSearchByStatus,R.layout.spinner_style); 
		cbSearchByStatus.setAdapter(adapter);
	}
	
	@Override
	protected Intent getNextActivityIntent() {
		Intent intent =null;
		if(taskType==Global.TASK_ORDER_REASSIGNMENT){
			intent = new Intent(getActivity(), OrderReassignmentResult.class);
			intent.putExtra(Global.BUND_KEY_TASK_TYPE, taskType);
		}
		
		return intent;
	}
	@Override
	public void onResume(){
	    	super.onResume();
//	    	getActivity().getActionBar().removeAllTabs();
		//Set Firebase screen name
		screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_survey_reassignment), null);
	    	if(taskType==Global.TASK_CANCEL_ORDER){
//	    		getActivity().getActionBar().setTitle(getString(R.string.title_mn_cancelorder));
	    	}
	    	else if(taskType==Global.TASK_ORDER_REASSIGNMENT){
//	    		getActivity().getActionBar().setTitle(getString(R.string.title_mn_surveyreassign));

				// olivia : set toolbar
				getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
				getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
				getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_surveyreassign));
	    	}
//	    	getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
}
