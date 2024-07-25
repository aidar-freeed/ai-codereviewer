package com.adins.mss.coll;

import android.app.ActionBar;
import android.content.Context;

import com.adins.mss.base.todo.form.NewTaskActivity;
import com.adins.mss.base.todo.form.NewTaskAdapter;
import com.adins.mss.base.todolist.DoList;
import com.adins.mss.dao.Scheme;

import java.util.List;

public class MCNewTaskActivity extends NewTaskActivity {

	private DoList list;
	private List<Scheme> objects;
	
	@Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_newtask));
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }
	
	@Override
	protected NewTaskAdapter getNewTaskAdapter() {
		// TODO Auto-generated method stub
		list = new DoList(getActivity());
		objects = list.getCollListScheme();
		
		return new NewTaskAdapter(getActivity(), objects);
	}

}
