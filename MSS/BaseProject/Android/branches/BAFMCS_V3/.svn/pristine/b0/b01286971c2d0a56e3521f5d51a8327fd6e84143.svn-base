package com.adins.mss.odr;

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
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.mainmenu.MainMenuHelper;
import com.adins.mss.base.tasklog.TaskLogImpl;
import com.adins.mss.base.timeline.MenuAdapter;
import com.adins.mss.base.timeline.MenuModel;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.StatusTabFragment;
import com.adins.mss.constant.Global;
import com.adins.mss.odr.assignment.OrderAssignmentTask;
import com.adins.mss.odr.news.NewsHeaderTask;
import com.adins.mss.odr.simulasi.CreditSimulationActivity;
import com.services.MainServices;
import com.services.NotificationThread;

import java.util.ArrayList;
import java.util.List;

public class MOMainMenuActivity extends MainMenuActivity {

    public static MenuModel mnTimeline;
    public static MenuModel mnNewOdr;
    public static MenuModel mnCheckOdr;
    public static MenuModel mnCreditSimulation;
    public static MenuModel mnOdrAssignment;
    public static MenuModel mnOdrReAssignment;
    public static MenuModel mnUpdateOdr;
    public static MenuModel mnAbsentI;
    public static MenuModel mnAbsentO;
    public static MenuModel mnNews;
    public static MenuModel mnExit;
    public static MenuModel mnAbout;
    public static MenuModel mnSynchronize;
    public static MenuModel mnStgProfile;
    public static MenuModel mnChangePassword;
    public static int flag_edit = 0;
    public static boolean inAbsent = false;
    public Intent RunNotificationNewsService;
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
        for (int i = 0; i < mainMenuTitle.size(); i++) {
            if (mainMenuTitle.get(i).equalsIgnoreCase(getString(R.string.title_mn_tasklist))) {
                mnTaskList = new MenuModel(mainMenuIcon.get(i), mainMenuTitle.get(i), "0");
                models.add(mnTaskList);
            } else if (getMainMenuTitle().get(i).equalsIgnoreCase(getString(R.string.title_mn_log))) {
                mnLog = new MenuModel(mainMenuIcon.get(i), mainMenuTitle.get(i), "0");
                models.add(mnLog);
            } else {
                models.add(new MenuModel(mainMenuIcon.get(i), mainMenuTitle.get(i), null));
            }
        }

        models.add(titleGroup2);
        List<String> otherMenuTitle = getOtherMenuTitle();
        List<Integer> otherMenuIcon = getOtherMenuIcon();
//	    mnChangePassword = new MenuModel(R.drawable.ic_changepassword,getString(R.string.title_mn_changepassword), null);
//	    models.add(mnChangePassword);
        for (int i = 0; i < otherMenuTitle.size(); i++) {
            models.add(new MenuModel(otherMenuIcon.get(i), otherMenuTitle.get(i), null));
        }


        for (int i = 0; i < models.size(); i++) {
            this.allMenu.add(models.get(i).getTitle());
        }

        this.models = models;

        return models;
    }

    @Override
    protected String getTitleGroup() {
        // TODO Auto-generated method stub
        return "Order";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        MainMenuActivity.setMss(this.getClass());
        super.onCreate(savedInstanceState);
        AboutActivity.setChangeLog(ChangeLog.getChangeLog(getApplicationContext()), 1);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerListLeft = findViewById(R.id.left_drawer);
        menuAdapter = new MenuAdapter(this, getModels(), R.drawable.activated_background_indicator);
        mDrawerListLeft.setAdapter(menuAdapter);

        mDrawerListLeft.setOnItemClickListener(this);

        //setcounter
        try {
            long newsCounter = News.getCounterNews(this);
            long logCounter = TaskLogImpl.getCounterLog(this);
            long taskListCounter = ToDoList.getCounterTaskList(this);

//			MOMainMenuActivity.mnNews.setCounter(String.valueOf(newsCounter));
            mnLog.setCounter(String.valueOf(logCounter));
            mnTaskList.setCounter(String.valueOf(taskListCounter));
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (savedInstanceState == null) {
            goTimeline(1);
            Global.positionStack.push(1);
        }

		/*System.out.println("Before start RunNotificationNewsService");
		RunNotificationNewsService = new Intent(this, NotificationNewsService.class);
		startService(RunNotificationNewsService);
		System.out.println("Start RunNotificationNewsService");*/
        try {
            actionFromNotif(getIntent());
        } catch (Exception e) {
        }
    }

    protected void actionFromNotif(Intent intent) {
        try {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(NotificationThread.TASKLIST_NOTIFICATION_KEY)) {
                    gotoTaskList(0);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        actionFromNotif(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Global.syncIntent = new Intent(getApplicationContext(), MOSynchronizeActivity.class);
        if (MainMenuActivity.getMss() == null) {
            MainMenuActivity.setMss(this.getClass());
//			NotificationService.mss = mss;
            MainServices.mainClass = mss;
        }
        MainMenuActivity.setMainMenuClass(MOMainMenuActivity.class);
        try {
            final long logCounter = TaskLogImpl.getCounterLog(this);
            final long taskListCounter = ToDoList.getCounterTaskList(this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mnLog.setCounter(String.valueOf(logCounter));
                    mnTaskList.setCounter(String.valueOf(taskListCounter));
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }
        getActionBar().removeAllTabs();
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position,
                            long id) {
        // TODO Auto-generated method stub
        super.onItemClick(parent, view, position, id);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getString(R.string.title_mn_changepassword).equalsIgnoreCase(allMenu.get(position))) {
                    gotoChangePassword();
                }
            }
        }, 250);
    }

    @Override
    protected Intent getIntentSynchronize() {
        // TODO Auto-generated method stub
        return new Intent(this, MOSynchronizeActivity.class);
    }

    //bong 10 apr 15 - menjaga saat change password tidak dapat lihat menu
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (fragmentChgPwd != null) {
                if (fragmentChgPwd.isVisible()) {
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
        if (fragmentChgPwd != null) {
            if (fragmentChgPwd.isVisible()) {
                if (GlobalData.getSharedGlobalData().getUser().getChg_pwd().equals("1")) {
                    ChangePasswordFragment.onBackPressed();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    private void gotoChangePassword() {
        fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putBoolean(ChangePasswordFragment.AS_ACTIVITY, false);
        fragment.setArguments(args);
        if (GlobalData.getSharedGlobalData().getUser().getChg_pwd().equals("1") ||
                GlobalData.getSharedGlobalData().getUser().getPwd_exp().equals("1")) {
            fragmentChgPwd = (ChangePasswordFragment) fragment;
            fragmentChgPwd.setArguments(args);
        }
        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected Fragment getChgPassFragment() {
        // TODO Auto-generated method stub
        return fragmentChgPwd;
    }

    @Override
    protected void goTimeline(int position) {
//    	TimelineModel.status_fragment=new StatusSectionFragment();
        super.goTimeline(position);
    }

    @Override
    protected void gotoNewTask(int position) {
//		GetSchemeTask task = new GetSchemeTask(this, new MONewTaskActivity(), true);
//		task.execute();
    }

    @Override
    protected void gotoCheckOrder(int position) {
        fragment = new CheckOrderActivity();
        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void gotoCreditSimulation(int position) {
        fragment = new CreditSimulationActivity();
        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void gotoCancelOrder(int position) {
        fragment = new OrderFilterActivity();
        Bundle args = new Bundle();
        args.putInt(Global.BUND_KEY_TASK_TYPE, Global.TASK_CANCEL_ORDER);
        fragment.setArguments(args);

        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void gotoTaskList(int position) {
        fragment = new StatusTabFragment();
        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void gotoPromo(int position) {
        NewsHeaderTask task = new NewsHeaderTask(this, getString(R.string.progressWait),
                getString(R.string.msgNoList), R.id.content_frame);
        task.execute();
    }

    @Override
    protected void gotoNews(int position) {
        NewsHeaderTask task = new NewsHeaderTask(this, getString(R.string.progressWait),
                getString(R.string.msgNoList), R.id.content_frame);
        task.execute();
    }

    @Override
    protected void gotoAbout() {
        AboutActivity.setChangeLog(ChangeLog.getChangeLog(getApplicationContext()), 1);
        super.gotoAbout();
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
    protected void gotoOrderAssignment(int position) {
        OrderAssignmentTask task = new OrderAssignmentTask(this, getString(R.string.progressWait),
                getString(R.string.msgNoList), R.id.content_frame);
        task.execute();
    }

    @Override
    protected void gotoOrderReassignment(int position) {
        fragment = new OrderFilterActivity();
        Bundle args = new Bundle();
        args.putInt(Global.BUND_KEY_TASK_TYPE, Global.TASK_ORDER_REASSIGNMENT);
        fragment.setArguments(args);

        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    public void showNotAvailableMenu(int position) {
        MainMenuHelper.showNotAvailableMenuDialog(MOMainMenuActivity.this, allMenu.get(position));
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

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
