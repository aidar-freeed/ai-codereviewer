package com.adins.mss.svy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.about.activity.AboutActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.loyalti.mypointdashboard.DashboardMyPoint;
import com.adins.mss.base.mainmenu.NewMenuItem;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.todo.form.GetSchemeTask;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.MenuDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.svy.assignment.OrderAssignmentTask;
import com.adins.mss.svy.fragments.SurveyApprovalByBranchFragment;
import com.adins.mss.svy.fragments.SurveyApprovalFragment;
import com.adins.mss.svy.fragments.SurveyPerformanceFragment;
import com.adins.mss.svy.fragments.SurveyVerificationByBranchFragment;
import com.adins.mss.svy.fragments.SurveyVerificationFragment;
import com.adins.mss.svy.reassignment.OrderFilterActivity;
import com.adins.mss.svy.tool.Constants;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.services.ForegroundServiceNotification;
import com.services.MSLocationTrackingService;
import com.services.MainServices;
import com.adins.mss.base.R;
import com.services.NotificationThread;
import com.services.RestartAutoSendLocationReceiver;
import com.services.SurveyApprovalService;
import com.services.SurveyApprovalThread;
import com.services.SurveyAssignmentThread;
import com.services.SurveyVerificationService;
import com.services.SurveyVerificationThread;

import java.util.Locale;

public class NewMSMainActivity extends NewMainActivity {

    public static SurveyVerificationThread verificationThread;
    public static SurveyApprovalThread approvalThread;
    private User user = null;
    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        NewMainActivity.setMss(this.getClass());

        screenName = FirebaseAnalytics.getInstance(this);

        /**
         * Nendi | 2019.06.17
         * Update Foreground Service
         * @support Android >= 8.0
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(getLocationServiceIntent());
        } else {
            startService(getLocationServiceIntent());
        }
        super.onCreate(savedInstanceState);
        AboutActivity.setChangeLog(ChangeLog.getChangeLog(getApplicationContext()), 2);

        NewMainActivity.setApprovalFragment(new SurveyApprovalFragment());
        NewMainActivity.setVerificationFragment(new SurveyVerificationFragment());
        NewMainActivity.setApprovalFragmentByBranch(new SurveyApprovalByBranchFragment());
        NewMainActivity.setVerificationFragmentByBranch(new SurveyVerificationByBranchFragment());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Intent autoRestart = new Intent(this, RestartAutoSendLocationReceiver.class);
            PendingIntent autoRestartPendingIntent = PendingIntent.getBroadcast(this, 2020, autoRestart, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000, 2 * 60 * 1000,
                    autoRestartPendingIntent);
        }

        try {
            if (NewMSMainActivity.mnTaskList != null)
                NewMSMainActivity.mnTaskList.setCounter(String.valueOf(ToDoList.getCounterTaskList(this)));
            if (NewMSMainActivity.mnSurveyAssign != null)
                NewMSMainActivity.mnSurveyAssign.setCounter(String.valueOf(ToDoList.getCounterAssignment(getApplicationContext())));
            if (NewMSMainActivity.mnSurveyVerif != null)
                NewMSMainActivity.mnSurveyVerif.setCounter(String.valueOf(Constants.getCounterVerificationTask(getApplicationContext())));
            if (NewMSMainActivity.mnSurveyApproval!= null)
                NewMSMainActivity.mnSurveyApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(getApplicationContext())));
            if (NewMSMainActivity.mnVerifByBranch != null)
                NewMSMainActivity.mnVerifByBranch.setCounter(String.valueOf(Constants.getCounterVerificationTaskByBranch(getApplicationContext())));
            if (NewMSMainActivity.mnApprovalByBranch != null)
                NewMSMainActivity.mnApprovalByBranch.setCounter(String.valueOf(Constants.getCounterApprovalTaskByBranch(getApplicationContext())));
        } catch (Exception e) {
            FireCrash.log(e);
        }

        try {
            Global.FEATURE_RESCHEDULE_SURVEY = MenuDataAccess.isHaveRescheduleMenu(getApplicationContext());
        } catch (Exception e) {             FireCrash.log(e);
        }
        Global.VERIFICATION_BRANCH = MenuDataAccess.isHaveVerificationBranchMenu(getApplicationContext());
        Global.APPROVAL_BRANCH = MenuDataAccess.isHaveApprovalBranchMenu(getApplicationContext());

        Tool.installProvider(getApplicationContext());

        if(savedInstanceState ==null) {
            Fragment frag = new NewTimelineFragment();
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            trans.add(com.adins.mss.base.R.id.content_frame, frag);
            trans.commit();
        }

        try {
            user = GlobalData.getSharedGlobalData().getUser();
        } catch (Exception e) {
            FireCrash.log(e);
        }

        if (user != null) {
            try{
                if(!GeneralParameterDataAccess.getOne(getApplicationContext(), user.getUuid_user(), "PRM03_ASIN").getGs_value().equals("0")){
                    if (mnSurveyVerif != null || mnVerifByBranch != null) {
                        verificationThread = new SurveyVerificationThread(this);
                        Global.verifyNotivIntent = new Intent(this,SurveyVerificationService.class);
                        startService(Global.verifyNotivIntent);
                    }
                    if (mnSurveyApproval != null || mnApprovalByBranch != null) {
                        approvalThread = new SurveyApprovalThread(this);
                        Global.approvalNotivIntent = new Intent(this,SurveyApprovalService.class);
                        startService(Global.approvalNotivIntent);
                    }
                }
            }
            catch (NullPointerException e){
                DialogManager.showExitAlert(this, getString(R.string.alertExit));
            }
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    NewMainActivity.setCounter();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }

        }, 500);

        try {
            actionFromNotif(getIntent());
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            e.printStackTrace();
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    protected void actionFromNotif(Intent intent){
        try {
            String action = intent.getAction();
            if(action!=null){
                if(action.equals(NotificationThread.TASKLIST_NOTIFICATION_KEY)){
                    if (Global.PLAN_TASK_ENABLED)
                        logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.mn_plantask));
                    else
                        logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_tasklist));

                    Fragment fragment1 = new PriorityTabFragment();
                    FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                    transaction.replace(R.id.content_frame, fragment1);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }
                else if(action.equals(SurveyVerificationThread.VERIFICATIONLIST_NOTIFICATION_KEY)){
                    logEventCountMenuAccessed(getString(R.string.title_mn_verification_list));
                    gotoSurveyVerification();
                }else if(action.equals(SurveyVerificationThread.VERIFICATIONBRANCHLIST_NOTIFICATION_KEY)){
                    logEventCountMenuAccessed(getString(R.string.title_mn_verification_bybranch));
                    gotoSurveyVerificationByBranch();
                }else if(action.equals(SurveyApprovalThread.APPROVALBRANCHLIST_NOTIFICATION_KEY)){
                    logEventCountMenuAccessed(getString(R.string.title_mn_approval_bybranch));
                    gotoSurveyApprovalByBranch();
                }else if(action.equals(SurveyApprovalThread.APPROVALLIST_NOTIFICATION_KEY)){
                    logEventCountMenuAccessed(getString(R.string.title_mn_approval_list));
                    gotoSurveyApproval();
                }else if(action.equals(SurveyAssignmentThread.ASSIGNMENT_NOTIFICATION_KEY)) {
                    logEventCountMenuAccessed(getString(R.string.title_mn_surveyassign));
                    gotoSurveyAssignment();
                } else if (action.equals(Global.MAINMENU_NOTIFICATION_KEY)){
                    gotoTimeline();
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public Intent getLocationServiceIntent() {
        return new Intent(this, MSLocationTrackingService.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        actionFromNotif(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen namef
        screenName.setCurrentScreen(this,getString(R.string.screen_name_svy_main_menu),null);

        if (Global.REDIRECT_TIMELINE.equals(Global.getREDIRECT())) {
            super.openTimeline();
            Global.setREDIRECT(null);
            return;
        }

        if (!ForegroundServiceNotification.isServiceAvailable(this, MSLocationTrackingService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(getLocationServiceIntent());
            } else {
                startService(getLocationServiceIntent());
            }
        }

        if (verificationThread == null || approvalThread == null) {
            try {
                user = GlobalData.getSharedGlobalData().getUser();
            } catch (Exception e) {
                FireCrash.log(e);
            }

            if (user != null) {
                if(!GeneralParameterDataAccess.getOne(getApplicationContext(), user.getUuid_user(), "PRM03_ASIN").getGs_value().equals("0")){
                    if (mnSurveyVerif != null || mnVerifByBranch != null) {
                        verificationThread = new SurveyVerificationThread(this);
                        Global.verifyNotivIntent = new Intent(this,SurveyVerificationService.class);
                        startService(Global.verifyNotivIntent);
                    }
                    if (mnSurveyApproval != null || mnApprovalByBranch != null) {
                        approvalThread = new SurveyApprovalThread(this);
                        Global.approvalNotivIntent = new Intent(this,SurveyApprovalService.class);
                        startService(Global.approvalNotivIntent);
                    }
                }
            }
        }

        Global.syncIntent = new Intent(getApplicationContext(), MSSynchronizeActivity.class);
        if(NewMainActivity.getMss()==null) {
            NewMainActivity.setMss(this.getClass());
            MainServices.mainClass = mss;
        }
        NewMainActivity.setMainMenuClass(NewMSMainActivity.class);
        Global.VerificationActivityClass = SurveyVerificationActionActivity.class;
        try {
            if (NewMSMainActivity.mnSurveyVerif != null)
                NewMSMainActivity.mnSurveyVerif.setCounter(String.valueOf(Constants.getCounterVerificationTask(getApplicationContext())));
            else if(NewMSMainActivity.mnVerifByBranch != null)
                NewMSMainActivity.mnVerifByBranch.setCounter(String.valueOf(Constants.getCounterVerificationTaskByBranch(getApplicationContext())));
            else{
                try {
                    stopService(Global.verifyNotivIntent);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
            if (NewMSMainActivity.mnSurveyApproval!= null)
                NewMSMainActivity.mnSurveyApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(getApplicationContext())));
            else if (NewMSMainActivity.mnApprovalByBranch != null)
                NewMSMainActivity.mnApprovalByBranch.setCounter(String.valueOf(Constants.getCounterApprovalTaskByBranch(getApplicationContext())));
            else{
                try {
                    stopService(Global.approvalNotivIntent);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (verificationThread != null) {
            verificationThread.requestStop();
            verificationThread = null;
        }
        if (approvalThread != null) {
            approvalThread.requestStop();
            approvalThread = null;
        }
        NewMainActivity.mnTaskList = null;
        NewMainActivity.mnSurveyApproval = null;
        NewMainActivity.mnSurveyApproval = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected Intent getIntentSynchronize() {
        return new Intent(this, MSSynchronizeActivity.class);
    }

    @Override
    protected void ApplicationMenu(NewMenuItem menuItem) {
        super.ApplicationMenu(menuItem);

        if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_newtask))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_newtask));
            gotoNewTask();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_surveyperformance))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_surveyperformance));
            gotoSurveyPerformance();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_surveyverification))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_surveyverification));
            gotoSurveyVerification();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_surveyapproval))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_surveyapproval));
            gotoSurveyApproval();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_surveyassign))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_surveyassign));
            gotoSurveyAssignment();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_surveyreassign))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_surveyreassign));
            gotoSurveyReassignment();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_verification_bybranch))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_verification_bybranch));
            gotoSurveyVerificationByBranch();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_approval_bybranch))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_approval_bybranch));
            gotoSurveyApprovalByBranch();
        }else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_dashboard))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_dashboard));
            goToDashboardMyPoint();
        }
    }

    @Override
    public void gotoTimeline() {
        super.gotoTimeline();
    }

    @Override
    protected void gotoAbout() {
        AboutActivity.setChangeLog(ChangeLog.getChangeLog(this), 2);
        super.gotoAbout();
    }

    protected void gotoNewTask() {
        GetSchemeTask task = new GetSchemeTask(this, new MSNewTaskActivity(), true);
        task.execute();
    }

    protected void gotoSurveyPerformance() {
        fragment = new SurveyPerformanceFragment();
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoSurveyVerification() {
        fragment = new SurveyVerificationFragment();
        SurveyVerificationListTask task = new SurveyVerificationListTask(this, getString(R.string.progressWait),
                getString(R.string.msgNoVerification),null);
        task.execute();
    }

    protected void gotoSurveyApproval() {
        fragment = new SurveyApprovalFragment();
        SurveyApprovalListTask task = new SurveyApprovalListTask(this, getString(R.string.progressWait),
                getString(R.string.msgNoApproval),null);
        task.execute();
    }

    protected void gotoSurveyAssignment() {
        OrderAssignmentTask task = new OrderAssignmentTask(this, getString(R.string.progressWait),
                getString(R.string.msgNoList),R.id.content_frame);
        task.execute();
    }

    protected void gotoSurveyReassignment() {
        fragment = new OrderFilterActivity();
        Bundle args = new Bundle();
        args.putInt(Global.BUND_KEY_TASK_TYPE, Global.TASK_ORDER_REASSIGNMENT);
        fragment.setArguments(args);

        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoSurveyVerificationByBranch() {
        fragment = new SurveyVerificationFragment();
        SurveyVerificationListTask task = new SurveyVerificationListTask(this, getString(R.string.progressWait),
                getString(R.string.msgNoVerification), SurveyVerificationListTask.KEY_BRANCH);
        task.execute();
    }

    protected void gotoSurveyApprovalByBranch() {
        fragment = new SurveyApprovalFragment();
        SurveyApprovalListTask task = new SurveyApprovalListTask(this, getString(R.string.progressWait),
                getString(R.string.msgNoApproval), SurveyApprovalListTask.KEY_BRANCH);
        task.execute();
    }

    protected void goToDashboardMyPoint(){
        fragment = new DashboardMyPoint();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


}
