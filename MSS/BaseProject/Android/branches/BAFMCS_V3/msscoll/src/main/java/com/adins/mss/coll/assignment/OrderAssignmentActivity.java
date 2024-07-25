package com.adins.mss.coll.assignment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.coll.R;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.androidquery.AQuery;

import org.acra.ACRA;

import java.util.List;

public class OrderAssignmentActivity extends Fragment {
	protected View view;
	protected AQuery query;
	protected List<TaskH> objects;
	private User user = GlobalData.getSharedGlobalData().getUser();
	OrderAssignmentAdapter adapter;
	@Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_orderassign));
//        objects = TaskHDataAccess.getall
        adapter = new OrderAssignmentAdapter(getActivity(), objects);
    }
	@Override
	public void onResume(){
		super.onResume();
		getActivity().getActionBar().setTitle(getString(R.string.title_mn_orderassign));
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    	    	
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
			FireCrash.log(e);
		}
        return view;
	}
	public void itemClick(AdapterView<?> parent, View v, int position, long id){
		
	}
}
