package com.adins.mss.odr;

import org.acra.ACRA;

import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.odr.reassignment.OrderReassignmentResult;
import com.adins.mss.odr.update.ResultUpdateActivity;
import com.google.firebase.analytics.FirebaseAnalytics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class OrderFilterActivity extends CheckOrderActivity{
	private int taskType;
	private Spinner cbSearchByStatus;
	private String[] contentStatusFilter;
	private TextView labelSearchByStatus;
	private RelativeLayout layoutFilterByStatus;
	private Bundle mArguments;
	private LinearLayout container;
	private FirebaseAnalytics screenName;
//	private View view;
	public OrderFilterActivity() {
		// TODO Auto-generated constructor stub
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		screenName = FirebaseAnalytics.getInstance(getActivity());
//		if(taskType==Global.TASK_CANCEL_ORDER){
//			addStatusFilter();
//		}

		this.container = view.findViewById(R.id.container);
		this.container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(!UserHelp.isActive)
							UserHelp.showAllUserHelp(OrderFilterActivity.this.getActivity(),OrderFilterActivity.this.getClass().getSimpleName());
					}
				}, SHOW_USERHELP_DELAY_DEFAULT);
			}
		});


		return view;
	}

	private void addStatusFilter() {
		labelSearchByStatus =  (TextView)view.findViewById(R.id.lblSearchByStatus);
		cbSearchByStatus = (Spinner)view.findViewById(R.id.cbSearcrByStatus);
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
		if(taskType==Global.TASK_CANCEL_ORDER){
			intent = new Intent(getActivity(), ResultUpdateActivity.class);
			intent.putExtra(Global.BUND_KEY_TASK_TYPE, taskType);
//			int selectedItemPosition = cbSearchByStatus.getSelectedItemPosition();
//			String keySearchBy = "";
//			if(0==selectedItemPosition){
//				keySearchBy = "ALL";
//			}else if(1==selectedItemPosition){
//				keySearchBy = "OnSurvey";
//			}else  if(2==selectedItemPosition){
//				keySearchBy = "OnCA";
//			}
//			
//			intent.putExtra("status", keySearchBy);	
		}else if(taskType==Global.TASK_ORDER_REASSIGNMENT){
			intent = new Intent(getActivity(), OrderReassignmentResult.class);
			intent.putExtra(Global.BUND_KEY_TASK_TYPE, taskType);
		}
		
		return intent;
	}
	@Override
	public void onResume(){
	    	super.onResume();
//	    	getActivity().getActionBar().removeAllTabs();
	    	if(taskType==Global.TASK_CANCEL_ORDER){
	    		getActivity().setTitle(getString(R.string.title_mn_cancelorder));
				screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_cancel_order), null);
				disableCheckOrderBtn();
	    	}
	    	else if(taskType==Global.TASK_ORDER_REASSIGNMENT){
				//Set Firebase screen name
				screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_order_reassignment), null);
	    		getActivity().setTitle(getString(R.string.title_mn_orderreassign));
	    	}
//	    	getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.mnGuide){
			if(!Global.BACKPRESS_RESTRICTION) {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (!UserHelp.isActive)
							UserHelp.showAllUserHelp(OrderFilterActivity.this.getActivity(), OrderFilterActivity.this.getClass().getSimpleName());
					}
				}, SHOW_USERHELP_DELAY_DEFAULT);
				container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (!UserHelp.isActive)
							UserHelp.showAllUserHelp(OrderFilterActivity.this.getActivity(), OrderFilterActivity.this.getClass().getSimpleName());
					}
				});
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
