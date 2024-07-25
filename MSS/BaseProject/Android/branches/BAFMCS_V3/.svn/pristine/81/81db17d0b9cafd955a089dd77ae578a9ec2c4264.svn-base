package com.adins.mss.odr.simulasi;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.FormBean;
import com.adins.mss.base.dynamicform.QuestionSetTask;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.todo.form.NewTaskActivity;
import com.adins.mss.base.todo.form.NewTaskAdapter;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.odr.R;

import org.acra.ACRA;

import java.util.List;

public class CreditSimulationActivity extends NewTaskActivity{
	private CreditSimulation simulation;
	private List<Scheme> objects;
	
	@Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_creditsimulation));
    }
	@Override
	public void onResume(){
		super.onResume();
		getActivity().getActionBar().removeAllTabs();
		getActivity().getActionBar().setTitle(getString(R.string.title_mn_creditsimulation));
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
	@Override
	protected NewTaskAdapter getNewTaskAdapter() {
		simulation = new CreditSimulation(getActivity());
        objects = simulation.getSimulationScheme();        
		return new NewTaskAdapter(getActivity(), objects);
	}
	
	@Override
	protected TaskH setNewTaskH(Scheme scheme){
		TaskH taskH = new TaskH();
		taskH.setUuid_task_h(Tool.getUUID());
		taskH.setUser(GlobalData.getSharedGlobalData().getUser());
		taskH.setScheme(scheme);
		taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
		taskH.setIs_verification("0");
		taskH.setCustomer_name("Customer Simulasi");
		taskH.setCustomer_address("Alamat Simulasi");
		taskH.setCustomer_phone("12345678");
		taskH.setZip_code("12345");
		return taskH;	
	}
	
	@Override
	public void itemClick(AdapterView<?> parent, View v, int position, long id){
		Scheme selectedScheme= getNewTaskAdapter().getItem(position);
		TaskH selectedTask = setNewTaskH(selectedScheme);		
		CustomerFragment.setHeader(new SurveyHeaderBean(selectedTask));
		
		FormBean formBean = new FormBean(CustomerFragment.getHeader().getScheme());		
		if(CustomerFragment.getHeader().getStart_date()==null)
			CustomerFragment.getHeader().setStart_date(Tool.getSystemDateTime());
		CustomerFragment.getHeader().setForm(formBean);
		CustomerFragment.getHeader().setIs_preview_server(formBean.getIs_preview_server());

		Bundle extras = new Bundle();
		extras.putInt(Global.BUND_KEY_MODE_SURVEY, Global.MODE_NEW_SURVEY);
		extras.putString(Global.BUND_KEY_UUID_TASKH, CustomerFragment.getHeader().getUuid_task_h());
		extras.putSerializable(Global.BUND_KEY_SURVEY_BEAN, CustomerFragment.getHeader());
		extras.putBoolean(Global.BUND_KEY_MODE_SIMULASI, true);
		QuestionSetTask task =new QuestionSetTask(getActivity(), extras);
		task.execute();
	}
}
