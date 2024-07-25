package com.adins.mss.coll;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adins.mss.base.ChangePasswordFragment;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.about.activity.AboutActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.mainmenu.MainMenuHelper;
import com.adins.mss.base.tasklog.TaskLogImpl;
import com.adins.mss.base.timeline.MenuAdapter;
import com.adins.mss.base.timeline.MenuModel;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.TaskListFragment_new;
import com.adins.mss.base.todolist.form.TaskListTask;
import com.adins.mss.base.todolist.form.TaskList_Fragment;
import com.adins.mss.coll.closingtask.models.ClosingTaskListResponse;
import com.adins.mss.coll.closingtask.models.ClosingTaskRequest;
import com.adins.mss.coll.closingtask.senders.ClosingTaskSender;
import com.adins.mss.coll.fragments.CollectionActivityFragment;
import com.adins.mss.coll.fragments.DepositReportFragment;
import com.adins.mss.coll.fragments.InstallmentScheduleFragment;
import com.adins.mss.coll.fragments.PaymentHistoryFragment;
import com.adins.mss.coll.fragments.ReportSummaryFragment;
import com.adins.mss.constant.Global;
import com.services.MainServices;
import com.services.NotificationThread;

import java.util.ArrayList;
import java.util.List;

public class MCMainMenuActivity extends com.adins.mss.base.mainmenu.MainMenuActivity{

    public static MenuModel mnTimeline;
//    public static MenuModel mnTaskList;
//    public static MenuModel mnLog;
	public static MenuModel mnSVYApproval;
	public static MenuModel mnSVYVerify;
    public static MenuModel mnReportSummary;
    public static MenuModel mnDepositReport;
	public static MenuModel mnPaymentHistory;
	public static MenuModel mnInstallmentSchedule;
    public static MenuModel mnAbsentI;
    public static MenuModel mnExit;
    public static MenuModel mnAbout;
    public static MenuModel mnSynchronize;
    public static MenuModel mnChangePassword;
    public static int flag_edit=0;
    public static boolean inAbsent=false;
	Fragment fragment;
	ChangePasswordFragment fragmentChgPwd;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListLeft;


	@Override
	protected ArrayList<MenuModel> getModels() {
		// TODO Auto-generated method stub
		ArrayList<MenuModel> models = new ArrayList<MenuModel>();
		MenuModel titleGroup1 = new MenuModel(getString(R.string.title_mn_main_menu));
		titleGroup1.isGroupHeader();
		MenuModel titleGroup2 = new MenuModel(getString(R.string.title_mn_other));
		titleGroup2.isGroupHeader();
		
		mnTimeline = new MenuModel(R.drawable.ic_home, getString(R.string.title_mn_home),null);
		models.add(titleGroup1);
	    models.add(mnTimeline);
	    List<String> mainMenuTitle = getMainMenuTitle();
	    List<Integer> mainMenuIcon = getMainMenuIcon();
	    for(int i=0; i<mainMenuTitle.size();i++){
	    	if(mainMenuTitle.get(i).equalsIgnoreCase(getString(R.string.title_mn_tasklist))){
	    		mnTaskList = new MenuModel(mainMenuIcon.get(i),mainMenuTitle.get(i),"0");
	    		models.add(mnTaskList);
	    	}else if(getMainMenuTitle().get(i).equalsIgnoreCase(getString(R.string.title_mn_log))){
	    		mnLog = new MenuModel(mainMenuIcon.get(i),mainMenuTitle.get(i),"0");
	    		models.add(mnLog);
	    	}else if(getMainMenuTitle().get(i).equalsIgnoreCase(getString(R.string.title_mn_surveyverification))){
	    		mnSVYVerify = new MenuModel(mainMenuIcon.get(i),mainMenuTitle.get(i),"0");
	    		models.add(mnSVYVerify);
	    	}else if(getMainMenuTitle().get(i).equalsIgnoreCase(getString(R.string.title_mn_surveyapproval))){
	    		mnSVYApproval = new MenuModel(mainMenuIcon.get(i),mainMenuTitle.get(i),"0");
	    		models.add(mnSVYApproval);
	    	}else{
	    		models.add(new MenuModel(mainMenuIcon.get(i),mainMenuTitle.get(i),null));
	    	}
	    }
	    
	    models.add(titleGroup2);
	    List<String>  otherMenuTitle = getOtherMenuTitle();
	    List<Integer> otherMenuIcon = getOtherMenuIcon();
//	    mnChangePassword = new MenuModel(R.drawable.ic_changepassword,getString(R.string.title_mn_changepassword), null);
//	    models.add(mnChangePassword);
	    for(int i=0; i<otherMenuTitle.size();i++){
	    	models.add(new MenuModel(otherMenuIcon.get(i),otherMenuTitle.get(i),null));
	    }
	    
	    
	    for(int i=0 ; i<models.size(); i++){
	    	this.allMenu.add(models.get(i).getTitle());
	    }
	    
	    this.models=models;
	    return models;
	}

	@Override
	protected String getTitleGroup() {
		// TODO Auto-generated method stub
		return "Collection";
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        MainMenuActivity.setMss(this.getClass());
        super.onCreate(savedInstanceState);
        AboutActivity.setChangeLog(ChangeLog.getChangeLog(getApplicationContext()), 0);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerListLeft = (ListView) findViewById(R.id.left_drawer);
		
//		TimelineModel model = new TimelineModel()
//		.setContentFrame(R.id.content_frame)
//		.setCustomerFragment(new com.adins.mss.base.dynamicform.CustomerFragment())
//		.setLogFragment(new LogResultActivity())
//		.setMainFragment(this)
//		.setTaskListFragment(new TaskList_Fragment())
//		.withColor(getResources().getColor(R.color.tv_normal));
//	
		menuAdapter = new MenuAdapter(this, getModels(), R.drawable.activated_background_indicator);
		mDrawerListLeft.setAdapter(menuAdapter);
	        
	    mDrawerListLeft.setOnItemClickListener(this);
	    
	    try {
			MCMainMenuActivity.mnLog.setCounter(String.valueOf(TaskLogImpl.getCounterLog(this)));
			MCMainMenuActivity.mnTaskList.setCounter(String.valueOf(ToDoList.getCounterTaskList(this)));
		} catch (Exception e) {             FireCrash.log(e);
			// TODO: handle exception
		}
	    if (savedInstanceState == null) {
        	goTimeline(1);
        	Global.positionStack.push(1);        
        }
	    Global.installmentSchIntent = new Intent(getApplicationContext(), InstallmentScheduleFragment.class);
	    Global.paymentHisIntent = new Intent(getApplicationContext(), PaymentHistoryFragment.class);
	    Global.collectionActIntent = new Intent(getApplicationContext(), CollectionActivityFragment.class);
		try {
			actionFromNotif(getIntent());
		} catch (Exception e) {
			FireCrash.log(e);
		}
	}

	protected void actionFromNotif(Intent intent){
		try {
			String action = intent.getAction();
			if(action!=null){
				if(action.equals(NotificationThread.TASKLIST_NOTIFICATION_KEY)){
					Bundle argument = new Bundle();
					argument.putBoolean(TaskList_Fragment.BUND_KEY_ISERROR, false);
					Fragment fragment1 = new TaskListFragment_new();//TaskList_Fragment();
					fragment1.setArguments(argument);
					FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
					transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale, com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
					transaction.replace(com.adins.mss.base.R.id.content_frame, fragment1);
					transaction.addToBackStack(null);
					transaction.commitAllowingStateLoss();
				}
			}
		} catch (Exception e) {
			FireCrash.log(e);
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		actionFromNotif(intent);
	}

	@Override
	public void onResume(){
		super.onResume();
		Global.syncIntent = new Intent(getApplicationContext(), MCSynchronizeActivity.class);
		if(MainMenuActivity.getMss()==null) {
			MainMenuActivity.setMss(this.getClass());
//			NotificationService.mss = mss;
			MainServices.mainClass = mss;
		}
		MainMenuActivity.setMainMenuClass(MCMainMenuActivity.class);
		try {
	    	long logCounter = TaskLogImpl.getCounterLog(this);
	    	long taskListCounter = ToDoList.getCounterTaskList(this);
			mnLog.setCounter(String.valueOf(logCounter));
			mnTaskList.setCounter(String.valueOf(taskListCounter));
			
		} catch (Exception e) {             FireCrash.log(e);
			// TODO: handle exception
		}
//		TimelineModel.taskList_Fragment=new TaskList_Fragment();
		getActionBar().removeAllTabs();        
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position,
			long id) {
		super.onItemClick(parent, view, position, id);
		new Handler().postDelayed(new Runnable() {
	        @Override
	        public void run() {	        	
	        	if(getString(R.string.title_mn_changepassword).equalsIgnoreCase(allMenu.get(position))){
	        		gotoChangePassword();
	        	}
	        }
	    }, 250);
    }
	
	//bong 10 apr 15 - menjaga saat change password tidak dapat lihat menu
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	if(fragmentChgPwd!=null){
        		if(fragmentChgPwd.isVisible()){
        			return true;
        		}
        	}
        	return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event); 
    } 
	
	// 7 apr 15 disabled onBackpressed on fragment
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//    	if(fragmentChgPwd!=null){
//			if(fragmentChgPwd.isVisible()){
//				if(GlobalData.getSharedGlobalData().getUser().getChg_pwd().equals("1")){
//					ChangePasswordFragment.onBackPressed();
//					return;
//				}
//			}
//    	}
    	super.onBackPressed();
	}

	@Override
	protected Intent getIntentSynchronize() {
		// TODO Auto-generated method stub
		return new Intent(this, MCSynchronizeActivity.class);
	}
	
	private void gotoChangePassword() {
        fragment = new ChangePasswordFragment();        
        Bundle args = new Bundle();
        args.putBoolean(ChangePasswordFragment.AS_ACTIVITY, false);
        fragment.setArguments(args);
        if(GlobalData.getSharedGlobalData().getUser().getChg_pwd().equals("1")){
        	fragmentChgPwd = (ChangePasswordFragment) fragment;
        	fragmentChgPwd.setArguments(args); 
        }
        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
	    transaction.replace(R.id.content_frame, fragment);
	    transaction.addToBackStack(null);
        transaction.commit();
    }

	//bong 13 apr 15 - to set chgPassFragment untuk menjaga supaya menu tidak muncul
	@Override
	protected Fragment getChgPassFragment() {
		// TODO Auto-generated method stub
		return fragmentChgPwd;
	}
	@Override
	protected void goTimeline(int position) {
//	   	TimelineModel.taskList_Fragment=new TaskList_Fragment();
	   	super.goTimeline(position);
	}
	
	@Override
	protected void gotoNewTask(int position) {
		fragment = new MCNewTaskActivity();
		FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
	    transaction.replace(R.id.content_frame, fragment);
	    transaction.addToBackStack(null);
	    transaction.commit();
	}

	@Override
	protected void gotoCheckOrder(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoCreditSimulation(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoCancelOrder(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoTaskList(int position) {
		TaskListTask task = new TaskListTask(this, getString(R.string.progressWait),
				getString(R.string.msgNoTaskList), R.id.content_frame);
		task.execute();
	}

	@Override
	protected void gotoPromo(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoNews(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoOrderAssignment(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoOrderReassignment(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoSurveyPerformance(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoSurveyVerification(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoSurveyApproval(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoSurveyAssignment(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoSurveyReassignment(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoReportSummary(int position) {
		fragment = new ReportSummaryFragment();
		FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
	    transaction.replace(R.id.content_frame, fragment);
	    transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	protected void gotoDepositReport(int position) {
		fragment = new DepositReportFragment();
		FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
	    transaction.replace(R.id.content_frame, fragment);
	    transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	protected void gotoAbout() {
		AboutActivity.setChangeLog(ChangeLog.getChangeLog(getApplicationContext()), 0);
		super.gotoAbout();
	}

	@Override
	protected void gotoPaymentHistory(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoInstallmentSchedule(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoClosingTask(int position) {
		ClosingTaskRequest request = new ClosingTaskRequest();
		request.setFlag(ClosingTaskRequest.CLOSING_TASK_LIST);
		ClosingTaskSender<ClosingTaskListResponse> sender = new ClosingTaskSender<>(
				this, request, ClosingTaskListResponse.class);
		sender.execute();
	}

	public void showNotAvailableMenu(int position){
		MainMenuHelper.showNotAvailableMenuDialog(MCMainMenuActivity.this, allMenu.get(position));
		Global.positionStack.pop();
		mDrawerListLeft.setItemChecked(Global.positionStack.lastElement(), true);
		setTitle(models.get(Global.positionStack.lastElement()).getTitle());
	}

	@Override
	protected void gotoSurveyVerificationByBranch(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void gotoSurveyApprovalByBranch(int position) {
		// TODO Auto-generated method stub
		
	}
}
