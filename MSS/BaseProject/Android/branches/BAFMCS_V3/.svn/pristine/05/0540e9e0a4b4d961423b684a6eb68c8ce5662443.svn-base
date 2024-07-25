package com.adins.mss.svy;

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
import com.adins.mss.base.R;
import com.adins.mss.base.about.activity.AboutActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.mainmenu.MainMenuHelper;
import com.adins.mss.base.tasklog.TaskLogImpl;
import com.adins.mss.base.timeline.MenuAdapter;
import com.adins.mss.base.timeline.MenuModel;
import com.adins.mss.base.todo.form.GetSchemeTask;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.StatusTabFragment;
import com.adins.mss.base.todolist.form.TaskListFragment_new;
import com.adins.mss.base.todolist.form.TaskListTask;
import com.adins.mss.base.todolist.form.TaskList_Fragment;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.MenuDataAccess;
import com.adins.mss.svy.assignment.OrderAssignmentActivity;
import com.adins.mss.svy.assignment.OrderAssignmentTask;
import com.adins.mss.svy.fragments.SurveyApprovalByBranchFragment;
import com.adins.mss.svy.fragments.SurveyApprovalFragment;
import com.adins.mss.svy.fragments.SurveyPerformanceFragment;
import com.adins.mss.svy.fragments.SurveyVerificationByBranchFragment;
import com.adins.mss.svy.fragments.SurveyVerificationFragment;
import com.adins.mss.svy.reassignment.OrderFilterActivity;
import com.adins.mss.svy.tool.Constants;
import com.services.FirebaseMessagingService;
import com.services.NotificationThread;
import com.services.SurveyApprovalService;
import com.services.SurveyApprovalThread;
import com.services.SurveyAssignmentThread;
import com.services.SurveyVerificationService;
import com.services.SurveyVerificationThread;

import java.util.ArrayList;
import java.util.List;

public class MSMainMenuActivity extends com.adins.mss.base.mainmenu.MainMenuActivity{

	public static MenuModel mnTimeline;
	public static MenuModel mnNewTask;
//	public static MenuModel mnTaskList;
//	public static MenuModel mnLog;
	public static MenuModel mnSVYPerformance;
//	public static MenuModel mnSVYVerifyByBranch;
//	public static MenuModel mnSVYApprovalByBranch;
	public static MenuModel mnAbsentI;
	public static MenuModel mnOrderAssignment;
	public static MenuModel mnOrderReassignment;
	public static MenuModel mnExit;
	public static MenuModel mnAbout;
	public static MenuModel mnSynchronize;
	public static MenuModel mnChangePassword;
	public static int flag_edit=0;
	public static boolean inAbsent=false;
    public static SurveyVerificationThread verificationThread;
    public static SurveyApprovalThread approvalThread;
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

        mnTimeline = new MenuModel(R.drawable.ic_home, getString(R.string.title_mn_home), null);
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
	    	}else if(getMainMenuTitle().get(i).equalsIgnoreCase(getString(R.string.title_mn_verification_bybranch))){
	    		mnSVYVerifyByBranch = new MenuModel(mainMenuIcon.get(i),mainMenuTitle.get(i),"0");
	    		models.add(mnSVYVerifyByBranch);
	    	}else if(getMainMenuTitle().get(i).equalsIgnoreCase(getString(R.string.title_mn_approval_bybranch))){
	    		mnSVYApprovalByBranch = new MenuModel(mainMenuIcon.get(i),mainMenuTitle.get(i),"0");
	    		models.add(mnSVYApprovalByBranch);
	    	}else if(getMainMenuTitle().get(i).equalsIgnoreCase(getString(R.string.title_mn_surveyassign))) {
				mnSVYAssignment = new MenuModel(mainMenuIcon.get(i), mainMenuTitle.get(i), "0");
				models.add(mnSVYAssignment);
			}else{
	    		models.add(new MenuModel(mainMenuIcon.get(i),mainMenuTitle.get(i),null));
	    	}
	    }
/*	    MenuModel mnSVYVerifybr = new MenuModel(R.drawable.ic_verification,getString(R.string.title_mn_verification_bybranch), null);
	    models.add(mnSVYVerifybr);
		MenuModel mnSVYApprovalbr=new MenuModel(R.drawable.ic_approval,getString(R.string.title_mn_approval_bybranch), null);
		models.add(mnSVYApprovalbr);
		mnSVYApproval = new MenuModel(R.drawable.ic_approval,getString(R.string.title_mn_surveyapproval),"0");
		models.add(mnSVYApproval);*/
		
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
		Global.FEATURE_RESCHEDULE_SURVEY = MenuDataAccess.isHaveRescheduleMenu(getApplicationContext());
		Global.VERIFICATION_BRANCH = MenuDataAccess.isHaveVerificationBranchMenu(getApplicationContext());
		Global.APPROVAL_BRANCH = MenuDataAccess.isHaveApprovalBranchMenu(getApplicationContext());
	    this.models=models;
	    return models;
	}

	@Override
	protected String getTitleGroup() {
		// TODO Auto-generated method stub
		return "Survey";
	}

	@Override
	protected void gotoAbout() {
		AboutActivity.setChangeLog(ChangeLog.getChangeLog(getApplicationContext()), 2);
		super.gotoAbout();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		MainMenuActivity.setMss(this.getClass());
		super.onCreate(savedInstanceState);

		AboutActivity.setChangeLog(ChangeLog.getChangeLog(getApplicationContext()), 2);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerListLeft = (ListView) findViewById(R.id.left_drawer);

		menuAdapter = new MenuAdapter(this, getModels(), R.drawable.activated_background_indicator);
		mDrawerListLeft.setAdapter(menuAdapter);		
		mDrawerListLeft.setOnItemClickListener(this);

		//setcounter
		try {
			if(MSMainMenuActivity.mnLog!=null)
				MSMainMenuActivity.mnLog.setCounter(String.valueOf(TaskLogImpl.getCounterLog(this)));
			if(MSMainMenuActivity.mnTaskList!=null)
				MSMainMenuActivity.mnTaskList.setCounter(String.valueOf(ToDoList.getCounterTaskList(this)));
			if (MSMainMenuActivity.mnSVYVerify != null)
				MSMainMenuActivity.mnSVYVerify.setCounter(String.valueOf(Constants.getCounterVerificationTask(getApplicationContext())));
			if (MSMainMenuActivity.mnSVYApproval!= null)
				MSMainMenuActivity.mnSVYApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(getApplicationContext())));
			if (MSMainMenuActivity.mnSVYVerifyByBranch != null)
				MSMainMenuActivity.mnSVYVerifyByBranch.setCounter(String.valueOf(Constants.getCounterVerificationTaskByBranch(getApplicationContext())));
			if (MSMainMenuActivity.mnSVYApprovalByBranch != null)
				MSMainMenuActivity.mnSVYApprovalByBranch.setCounter(String.valueOf(Constants.getCounterApprovalTaskByBranch(getApplicationContext())));
		} catch (Exception e) {             FireCrash.log(e);
			// TODO: handle exception
		}
		if (savedInstanceState == null) {
			goTimeline(1);
			Global.positionStack.push(1);
		}
		
		if(!GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), "PRM03_ASIN").getGs_value().equals("0")){
			if (mnSVYVerify != null || mnSVYVerifyByBranch!=null) {
				verificationThread = new SurveyVerificationThread(this);
				Global.verifyNotivIntent = new Intent(this,SurveyVerificationService.class);
				startService(Global.verifyNotivIntent);
			}
			if (mnSVYApproval != null || mnSVYApprovalByBranch!=null) {
				approvalThread = new SurveyApprovalThread(this);
				Global.approvalNotivIntent = new Intent(this,SurveyApprovalService.class);
				startService(Global.approvalNotivIntent);
			}
		}

		MainMenuActivity.setApprovalFragment(new SurveyApprovalFragment());
		MainMenuActivity.setVerificationFragment(new SurveyVerificationFragment());
		MainMenuActivity.setApprovalFragmentByBranch(new SurveyApprovalByBranchFragment());
		MainMenuActivity.setVerificationFragmentByBranch(new SurveyVerificationByBranchFragment());
		MainMenuActivity.setAssignmentFragment(new OrderAssignmentActivity());
		MainMenuActivity.setStatusFragment(new StatusTabFragment());
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					if (MSMainMenuActivity.mnSVYVerify != null)
						MSMainMenuActivity.mnSVYVerify.setCounter(String.valueOf(Constants.getCounterVerificationTask(getApplicationContext())));
					if (MSMainMenuActivity.mnSVYApproval!= null)
						MSMainMenuActivity.mnSVYApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(getApplicationContext())));
					if (MSMainMenuActivity.mnSVYVerifyByBranch != null)
						MSMainMenuActivity.mnSVYVerifyByBranch.setCounter(String.valueOf(Constants.getCounterVerificationTaskByBranch(getApplicationContext())));
					if (MSMainMenuActivity.mnSVYApprovalByBranch != null)
						MSMainMenuActivity.mnSVYApprovalByBranch.setCounter(String.valueOf(Constants.getCounterApprovalTaskByBranch(getApplicationContext())));
					if (MainMenuActivity.menuAdapter != null)
						MainMenuActivity.menuAdapter.notifyDataSetChanged();
				} catch (Exception e) {             FireCrash.log(e);
					// TODO: handle exception
				}
			}
			
		}, 500);
		Intent intent = getIntent();

		try {
			String action = intent.getAction().toUpperCase();

			if (action != null) {
				if (action.equalsIgnoreCase("TASKLISTNOTIFICATION")) {
					Bundle argument =  new Bundle();
					argument.putBoolean(TaskList_Fragment.BUND_KEY_ISERROR, false);
					Fragment fragment1 = new TaskListFragment_new();//TaskList_Fragment();
					fragment1.setArguments(argument);
			        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
			        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
			        transaction.replace(R.id.content_frame, fragment1);
			        transaction.addToBackStack(null);
			        try {
			        	transaction.commit();	
					} catch (Exception e) {             FireCrash.log(e);
						transaction.commitAllowingStateLoss();
					}
				}
			} else {
				if(Global.IS_DEV)
					System.out.println("Intent was null");
			}
		} catch (Exception e) {
			FireCrash.log(e);
			e.printStackTrace();
		}
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
					transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
					transaction.replace(R.id.content_frame, fragment1);
					transaction.addToBackStack(null);
					transaction.commitAllowingStateLoss();
				}
				else if(action.equals(SurveyVerificationThread.VERIFICATIONLIST_NOTIFICATION_KEY)){
					gotoSurveyVerification(0);
				}else if(action.equals(SurveyVerificationThread.VERIFICATIONBRANCHLIST_NOTIFICATION_KEY)){
					gotoSurveyVerificationByBranch(0);
				}else if(action.equals(SurveyApprovalThread.APPROVALBRANCHLIST_NOTIFICATION_KEY)){
					gotoSurveyApprovalByBranch(0);
				}else if(action.equals(SurveyApprovalThread.APPROVALLIST_NOTIFICATION_KEY)){
					gotoSurveyApproval(0);
				}else if(action.equals(SurveyAssignmentThread.ASSIGNMENT_NOTIFICATION_KEY)) {
					gotoSurveyAssignment(0);
				} else if (action.equals(Global.MAINMENU_NOTIFICATION_KEY)){
					goTimeline(0);
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
		Global.syncIntent = new Intent(getApplicationContext(), MSSynchronizeActivity.class);
		if(MainMenuActivity.getMss()==null) {
			MainMenuActivity.setMss(this.getClass());
//			NotificationService.mss = mss;

		}
		MainMenuActivity.setMainMenuClass(MSMainMenuActivity.class);
		getActionBar().removeAllTabs();
		Global.VerificationActivityClass = SurveyVerificationActionActivity.class;
//		TimelineModel.taskList_Fragment=new TaskList_Fragment();
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		long taskListCounter = Constants.getCounterVerificationTask(getApplicationContext());
		try {
			if (MSMainMenuActivity.mnSVYVerify != null)
				MSMainMenuActivity.mnSVYVerify.setCounter(String.valueOf(Constants.getCounterVerificationTask(getApplicationContext())));
			else if(MSMainMenuActivity.mnSVYVerifyByBranch != null)
				MSMainMenuActivity.mnSVYVerifyByBranch.setCounter(String.valueOf(Constants.getCounterVerificationTaskByBranch(getApplicationContext())));
			else{
				try {
					stopService(Global.verifyNotivIntent);	
				} catch (Exception e) {             FireCrash.log(e);
					// TODO: handle exception
				}				
			}				
			if (MSMainMenuActivity.mnSVYApproval!= null)
				MSMainMenuActivity.mnSVYApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(getApplicationContext())));
			else if (MSMainMenuActivity.mnSVYApprovalByBranch != null)
				MSMainMenuActivity.mnSVYApprovalByBranch.setCounter(String.valueOf(Constants.getCounterApprovalTaskByBranch(getApplicationContext())));
			else{
				try {
					stopService(Global.approvalNotivIntent);
				} catch (Exception e) {
					FireCrash.log(e);
					// TODO: handle exception
				}				
			}			
							
		} catch (Exception e) {
			FireCrash.log(e);
			// TODO: handle exception
		}
		try {
			if(isFromSetting) {
				menuAdapter = new MenuAdapter(getApplicationContext(), getModels(), R.drawable.activated_background_indicator);
				mDrawerListLeft.setAdapter(menuAdapter);
				mDrawerListLeft.notifyAll();
				isFromSetting=false;
			}
		} catch (Exception e) {
			FireCrash.log(e);
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if (verificationThread != null) {
			verificationThread.requestStop();
			verificationThread = null;
        }
		if (approvalThread != null) {
			approvalThread.requestStop();
			approvalThread = null;
        }
		MainMenuActivity.mnTaskList = null;
		MainMenuActivity.mnSVYApproval = null;
		MainMenuActivity.mnSVYVerify = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position,
							long id) {
		// TODO Auto-generated method stub
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
	
	//bong 7 apr 15 disabled onBackpressed on fragment
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		if(fragmentChgPwd!=null && fragmentChgPwd.isVisible()){
//			if(GlobalData.getSharedGlobalData().getUser().getChg_pwd().equals("1")){
//				fragmentChgPwd.onBackPressed();
//				return;
//			}
//		}
    	super.onBackPressed();
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


	@Override
	protected Intent getIntentSynchronize() {
		// TODO Auto-generated method stub
		return new Intent(this, MSSynchronizeActivity.class);
	}

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
		GetSchemeTask task = new GetSchemeTask(this, new MSNewTaskActivity(), true);
	    task.execute();
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
	protected void gotoSurveyPerformance(int position) {
		fragment = new SurveyPerformanceFragment();
		FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
		transaction.replace(R.id.content_frame, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	protected void gotoSurveyVerification(int position) {
		fragment = new SurveyVerificationFragment();
		SurveyVerificationListTask task = new SurveyVerificationListTask(this, getString(R.string.progressWait),
				getString(R.string.msgNoVerification),null);
		task.execute();
	}

	@Override
	protected void gotoSurveyApproval(int position) {
		fragment = new SurveyApprovalFragment();
		SurveyApprovalListTask task = new SurveyApprovalListTask(this, getString(R.string.progressWait),
				getString(R.string.msgNoApproval),null);
		task.execute();
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
	protected void gotoSurveyAssignment(int position) {
		OrderAssignmentTask task = new OrderAssignmentTask(this, getString(R.string.progressWait),
				getString(R.string.msgNoList),R.id.content_frame);
		task.execute();
	}

	@Override
	protected void gotoSurveyReassignment(int position) {
		fragment = new OrderFilterActivity();
		Bundle args = new Bundle();
        args.putInt(Global.BUND_KEY_TASK_TYPE, Global.TASK_ORDER_REASSIGNMENT);
        fragment.setArguments(args);
        
        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
		transaction.replace(R.id.content_frame, fragment);
		transaction.addToBackStack(null);
	    transaction.commit();
	}

	@Override
	protected void gotoReportSummary(int position) {
		showNotAvailableMenu(position);
	}

	@Override
	protected void gotoDepositReport(int position) {
		showNotAvailableMenu(position);
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

	}

	public void showNotAvailableMenu(int position){
		MainMenuHelper.showNotAvailableMenuDialog(MSMainMenuActivity.this, allMenu.get(position));
		Global.positionStack.pop();
		mDrawerListLeft.setItemChecked(Global.positionStack.lastElement(), true);
		setTitle(models.get(Global.positionStack.lastElement()).getTitle());
	}

	@Override
	protected void gotoSurveyVerificationByBranch(int position) {
		fragment = new SurveyVerificationFragment();
		SurveyVerificationListTask task = new SurveyVerificationListTask(this, getString(R.string.progressWait),
				getString(R.string.msgNoVerification), SurveyVerificationListTask.KEY_BRANCH);
		task.execute();
	}

	@Override
	protected void gotoSurveyApprovalByBranch(int position) {
		fragment = new SurveyApprovalFragment();
		SurveyApprovalListTask task = new SurveyApprovalListTask(this, getString(R.string.progressWait),
				getString(R.string.msgNoApproval), SurveyApprovalListTask.KEY_BRANCH);
		task.execute();
	}
}
