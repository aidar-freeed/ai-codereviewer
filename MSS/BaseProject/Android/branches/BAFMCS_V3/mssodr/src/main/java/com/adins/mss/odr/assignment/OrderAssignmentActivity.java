package com.adins.mss.odr.assignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Keep;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.odr.R;
import com.adins.mss.odr.model.JsonResponseServer;
import com.adins.mss.odr.model.JsonResponseServer.ResponseServer;
import com.androidquery.AQuery;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.List;

public class OrderAssignmentActivity extends Fragment {
	
	protected View view;
	protected AQuery query;
	protected List<TaskH> allObjects;
	protected List<TaskH> objects;
	protected JsonResponseServer results;
	private List<ResponseServer> responseServer;
	private Bundle mArguments;
	protected OrderAssignmentAdapter adapter;
	private FirebaseAnalytics screenName;
	@Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        mArguments = getArguments();
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_orderassign));
        results = (JsonResponseServer) mArguments.getSerializable("resultJson");
        responseServer = results.getListResponseServer();
        adapter = new OrderAssignmentAdapter(getActivity(), responseServer,false);
    }
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Utility.freeMemory();
	}
	@Override
	public void onResume(){
		super.onResume();
		//Set Firebase screen name
		screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_order_assignment), null);
		getActivity().getActionBar().setTitle(getString(R.string.title_mn_orderassign));
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		screenName = FirebaseAnalytics.getInstance(getActivity());
		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
		try {
			view = inflater.inflate(R.layout.newtask_layout, container, false);
			query = new AQuery(view);
			query.id(android.R.id.list).adapter(adapter);
			query.id(android.R.id.list).itemClicked(this, "itemClick");
		} catch (Exception e) {
		}
        return view;
	}
	@Keep
	public void itemClick(AdapterView<?> parent, View v, int position, long id){
		String nomorOrder = responseServer.get(position).getKey();
		String uuid_task_h = responseServer.get(position).getFlag();
		gotoDetailData(nomorOrder, uuid_task_h);
	}
	private void gotoDetailData(String nomorOrder,String uuid_task_h) {
		Intent intent = new Intent(getActivity(), OrderAssignmentResult.class);
		intent.putExtra(Global.BUND_KEY_ORDERNO, nomorOrder);
		intent.putExtra(Global.BUND_KEY_TASK_TYPE, Global.TASK_ORDER_ASSIGNMENT);
		intent.putExtra(Global.BUND_KEY_UUID_TASKH, uuid_task_h);
		startActivity(intent);	
	}
}
