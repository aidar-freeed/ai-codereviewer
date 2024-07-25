package com.adins.mss.svy.assignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Keep;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.svy.R;
import com.adins.mss.svy.reassignment.JsonResponseServer;
import com.adins.mss.svy.reassignment.JsonResponseServer.ResponseServer;
import com.androidquery.AQuery;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.List;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class OrderAssignmentActivity extends Fragment {
	
	protected View view;
	protected AQuery query;
	protected List<TaskH> allObjects;
	protected List<TaskH> objects;
	protected JsonResponseServer results;
	private List<ResponseServer> responseServer;
	private Bundle mArguments;
//	public static boolean isChange = false;
	public static boolean isChange = false;
	public static int selectedPosition = 9999;
	protected OrderAssignmentAdapter adapter;
	private ListView listView;
	private FirebaseAnalytics screenName;
	@Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        isChange = false;
        mArguments = getArguments();
//        getActivity().getActionBar().setTitle(getString(R.string.title_mn_surveyassign));
        results = (JsonResponseServer) mArguments.getSerializable("resultJson");
        responseServer = results.getListResponseServer();

		// olivia : set toolbar
		getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
		getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
		getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_surveyassign));
        
        adapter = new OrderAssignmentAdapter(getActivity(), responseServer,false);
        setHasOptionsMenu(true);
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Utility.freeMemory();
	}

	@Override
	public void onResume(){
		super.onResume();
//		getActivity().getActionBar().setTitle(getString(R.string.title_mn_surveyassign));
		//Set Firebase screen name
		screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_survey_assignment), null);

		// olivia : set toolbar
		getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
		getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
		getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_surveyassign));

		if(isChange){
			if(selectedPosition!=9999){
				responseServer.remove(selectedPosition);
				adapter = new OrderAssignmentAdapter(getActivity(), responseServer,false);
				query.id(android.R.id.list).adapter(adapter);
				isChange=false;
				selectedPosition=9999;
			}
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    	    	
		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
		try {
			view = inflater.inflate(R.layout.new_fragment_survey_assignment, container, false);
			query = new AQuery(view);
			listView = view.findViewById(android.R.id.list);
			query.id(listView).adapter(adapter);
			query.id(listView).itemClicked(this, "itemClick");
			screenName = FirebaseAnalytics.getInstance(getActivity());
			if(responseServer.size()==0){
        		final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
	            builder.withTitle(getString(R.string.info_capital))
	            	.withMessage(getString(R.string.data_not_found))
	            	.withButton1Text(getString(R.string.btnOk))
	            	.isCancelable(false)
	            	.setButton1Click(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {					
							builder.dismiss();
						}
					}).show();
        	}
		} catch (Exception e) {
			FireCrash.log(e);
		}
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				UserHelp.showAllUserHelpWithListView(OrderAssignmentActivity.this.getActivity(),
						OrderAssignmentActivity.this.getClass().getSimpleName(),
						listView,
						0);
			}
		}, SHOW_USERHELP_DELAY_DEFAULT);
        return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.mnGuide){
			if(!Global.BACKPRESS_RESTRICTION) {

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						UserHelp.showAllUserHelpWithListView(OrderAssignmentActivity.this.getActivity(),
								OrderAssignmentActivity.this.getClass().getSimpleName(),
								listView,
								0);
					}
				}, SHOW_USERHELP_DELAY_DEFAULT);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Keep
	public void itemClick(AdapterView<?> parent, View v, int position, long id){
		selectedPosition = position;
		String nomorOrder = responseServer.get(position).getKey();
		String uuid_task_h = responseServer.get(position).getFlag();
		String formName = responseServer.get(position).getFormName();
		gotoDetailData(nomorOrder, uuid_task_h, formName);
	}
	private void gotoDetailData(String nomorOrder,String uuid_task_h,String formName) {
		Intent intent = new Intent(getActivity(), OrderAssignmentResult.class);
		intent.putExtra(Global.BUND_KEY_ORDERNO, nomorOrder);
		intent.putExtra(Global.BUND_KEY_FORM_NAME, formName);
		intent.putExtra(Global.BUND_KEY_TASK_TYPE, Global.TASK_ORDER_ASSIGNMENT);
		intent.putExtra(Global.BUND_KEY_UUID_TASKH, uuid_task_h);
		startActivity(intent);	
	}
}
