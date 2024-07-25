package com.adins.mss.coll;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.about.activity.AboutActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.NewMenuItem;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.base.todolist.form.TaskListFragment_new;
import com.adins.mss.base.todolist.form.TaskList_Fragment;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.closingtask.models.ClosingTaskListResponse;
import com.adins.mss.coll.closingtask.models.ClosingTaskRequest;
import com.adins.mss.coll.closingtask.senders.ClosingTaskSender;
import com.adins.mss.coll.dashboardcollection.view.DashboardCollectionView;
import com.adins.mss.coll.fragments.CollectionActivityFragment;
import com.adins.mss.coll.fragments.DashBoardFragment;
import com.adins.mss.coll.fragments.DepositReportACFragment;
import com.adins.mss.coll.fragments.DepositReportFragmentNew;
import com.adins.mss.coll.fragments.DepositReportPCFragment;
import com.adins.mss.coll.fragments.GuidelineFaqFragment;
import com.adins.mss.coll.fragments.InstallmentScheduleFragment;
import com.adins.mss.coll.fragments.PaymentHistoryFragment;
import com.adins.mss.coll.fragments.ReceiptHistoryFragment;
import com.adins.mss.coll.fragments.ReportSummaryFragment;
import com.adins.mss.coll.models.DocumentListResponse;
import com.adins.mss.coll.services.MCLocationTrackingService;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Emergency;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.foundation.db.dataaccess.EmergencyDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.MenuDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.services.ForegroundServiceNotification;
import com.services.MainServices;
import com.services.NotificationThread;
import com.services.plantask.ChangePlanService;
import com.services.plantask.ConnectivityChangeReceiver;
import com.services.plantask.StartVisitJob;

import java.util.List;
import java.util.Locale;

public class NewMCMainActivity extends NewMainActivity {

    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        NewMainActivity.setMss(this.getClass());
        /**
         * Nendi | 2019.06.17
         * Update Foreground Service
         * @support Android >= 8.0
         */
        startLocationHistoryService();
        super.onCreate(savedInstanceState);

        screenName = FirebaseAnalytics.getInstance(this);

        try {
            NewMCMainActivity.mnTaskList.setCounter(String.valueOf(ToDoList.getCounterTaskList(this)));
        } catch (Exception e) {
            FireCrash.log(e);
        }

        Tool.installProvider(getApplicationContext());

        if(savedInstanceState ==null) {
            Fragment fragment1 = new NewTimelineFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.add(com.adins.mss.base.R.id.content_frame, fragment1);
            transaction.commit();
        }

        //new
        try {
            Global.FEATURE_REVISIT_COLLECTION = MenuDataAccess.isHaveReVisitMenu(getApplicationContext());
        } catch (Exception e) {
            FireCrash.log(e);
        }
        Global.installmentSchIntent = new Intent(getApplicationContext(), InstallmentScheduleFragment.class);
        Global.paymentHisIntent = new Intent(getApplicationContext(), PaymentHistoryFragment.class);
        Global.collectionActIntent = new Intent(getApplicationContext(), CollectionActivityFragment.class);
        Global.receiptHistoryIntent = new Intent(getApplicationContext(), ReceiptHistoryFragment.class);
    }

    private void checkPlanTaskMode() {
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        GeneralParameter planTaskGP = GeneralParameterDataAccess.getOne(this,uuid_user,Global.GS_PLAN_TASK);
        if(planTaskGP != null && planTaskGP.getGs_value().equals("1")){
            Global.PLAN_TASK_ENABLED = true;
        }else {
            Global.PLAN_TASK_ENABLED = false;
        }

        TodayPlanRepository todayPlanRepository = GlobalData.getSharedGlobalData().getTodayPlanRepo();
        if(todayPlanRepository == null && Global.PLAN_TASK_ENABLED){
            todayPlanRepository = new TodayPlanRepository(getApplicationContext());
            GlobalData.getSharedGlobalData().setTodayPlanRepo(todayPlanRepository);
        }else if(!Global.PLAN_TASK_ENABLED && todayPlanRepository != null){
            todayPlanRepository = null;//clear
        }

        if(todayPlanRepository != null && Global.PLAN_TASK_ENABLED){
            todayPlanRepository.checkPlanAfterSync();
        }
    }

    @Override
    public void registerNetworkCallbacks() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nCallback = new ConnectivityChangeReceiver(new ConnectivityChangeReceiver.ConnectivityListener() {
                @Override
                public void onOnline() {
                    //do task when online
                    String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                    GeneralParameter planTaskGP = GeneralParameterDataAccess
                            .getOne(getApplicationContext(),uuid_user,Global.GS_PLAN_TASK);
                    if(planTaskGP != null && planTaskGP.getGs_value().equals("1")){
                        Global.PLAN_TASK_ENABLED = true;
                    }else {
                        Global.PLAN_TASK_ENABLED = false;
                    }

                    if(Global.PLAN_TASK_ENABLED){
                        //sync start visit
                        TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                        if(todayPlanRepo == null){
                            todayPlanRepo = new TodayPlanRepository(getApplicationContext());
                        }
                        if(todayPlanRepo.isNeedSync()){
                            Intent startVisitServ = new Intent(getApplicationContext(), StartVisitJob.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(startVisitServ);
                            }
                            else
                                startService(startVisitServ);
                        }

                        //sync change plan
                        String[] lastOffChangePlan = todayPlanRepo.getLastOffChangePlanInfo();
                        if(lastOffChangePlan != null && lastOffChangePlan.length > 0){
                            Intent changePlanServ = new Intent(getApplicationContext(), ChangePlanService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(changePlanServ);
                            }
                            else
                                startService(changePlanServ);
                        }
                    }
                }

                @Override
                public void onOffline() {
                    //do task when offline

                }
            });
        }
        super.registerNetworkCallbacks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    @Override
    public Intent getLocationServiceIntent() {
        return new Intent(this, MCLocationTrackingService.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        actionFromNotif(intent);
    }

    protected void actionFromNotif(Intent intent){
        try {
            String action = intent.getAction();
            if(action!=null && action.equals(NotificationThread.TASKLIST_NOTIFICATION_KEY)){
                if(Global.PLAN_TASK_ENABLED){
                    logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.mn_plantask));
                    TaskListFragment_new tasklistFragment = new TaskListFragment_new();
                    Bundle bundle = new Bundle();
                    bundle.putInt(TaskList_Fragment.BUND_KEY_PAGE,1);
                    tasklistFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = NewMainActivity.fragmentManager.beginTransaction();
                    fragmentTransaction.replace(com.adins.mss.base.R.id.content_frame,tasklistFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return;
                }
                logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_tasklist));
                Fragment fragment1 = new PriorityTabFragment();
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale, com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
                transaction.replace(com.adins.mss.base.R.id.content_frame, fragment1);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this,getString(R.string.screen_name_coll_main_menu),null);

        checkPlanTaskMode();
        if (Global.REDIRECT_TIMELINE.equals(Global.getREDIRECT())){
            super.openTimeline();
            Global.setREDIRECT(null);
            return;
        }

        Global.syncIntent = new Intent(getApplicationContext(), MCSynchronizeActivity.class);
        if(NewMainActivity.getMss()==null) {
            NewMainActivity.setMss(this.getClass());
            MainServices.mainClass = mss;
        }
        NewMainActivity.setMainMenuClass(NewMCMainActivity.class);
        try {
            NewMCMainActivity.mnTaskList.setCounter(String.valueOf(ToDoList.getCounterTaskList(this)));
        } catch (Exception e) {
            FireCrash.log(e);

        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && !ForegroundServiceNotification.isServiceAvailable(this, MCLocationTrackingService.class)) {
            startLocationHistoryService();
        }
        if(null != GlobalData.getSharedGlobalData().getUser().getIs_emergency() &&
                (GlobalData.getSharedGlobalData().getUser().getIs_emergency().equalsIgnoreCase(Global.EMERGENCY_SEND_SUCCESS)
                        ||GlobalData.getSharedGlobalData().getUser().getIs_emergency().equalsIgnoreCase(Global.EMERGENCY_SEND_PENDING))){
            Emergency emergency = new Emergency();
            emergency.setUser(GlobalData.getSharedGlobalData().getUser());
            EmergencyDataAccess.addOrReplace(getApplicationContext(),emergency);
            Intent intent = new Intent(this,EmergencyLockActivity.class);
            startActivity(intent);
        }else{
            List<Emergency> emergencies = EmergencyDataAccess.getByUser(getApplicationContext(),GlobalData.getSharedGlobalData().getUser().getUuid_user());
            if(!emergencies.isEmpty()){
                Intent intent = new Intent(this,EmergencyLockActivity.class);
                startActivity(intent);

            }
        }
    }

    private void startLocationHistoryService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(getLocationServiceIntent());
        } else {
            startService(getLocationServiceIntent());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected Intent getIntentSynchronize() {
        return new Intent(this, MCSynchronizeActivity.class);
    }

    @Override
    protected void ApplicationMenu(NewMenuItem menuItem) {
        super.ApplicationMenu(menuItem);

        if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_reportsummary))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_reportsummary));
            gotoReportSummary();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_depositreport))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_depositreport));
            gotoDepositReport();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_depositreportAC))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_depositreportAC));
            gotoDepositReportAC();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_depositreportPC))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_depositreportPC));
            gotoDepositReportPC();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_closing_task))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_closing_task));
            gotoClosingTask();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_dashboard))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_dashboard));
            goToDashBoard();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_dashboard_collection))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_dashboard_collection));
            goToDashboardCollection();
        } else if(menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_guideline_faq)))  {
            logEventCountMenuAccessed(getString(R.string.title_mn_guideline_faq));
            goToGuidelineFaq();
        }
    }

    protected void goToGuidelineFaq(){
        new GetDocumentList(getApplicationContext()).execute();
    }

    public class GetDocumentList extends AsyncTask<Void, Void, String>{
        private ProgressDialog progressDialog;
        private Context context;

        public GetDocumentList(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(NewMCMainActivity.this, "", getString(R.string.progressWait), true);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                if(Tool.isInternetconnected(NewMCMainActivity.this)){
                    String result = null;
                    MssRequestType requestType = new MssRequestType();
                    requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                    String json = GsonHelper.toJson(requestType);
                    String url = GlobalData.getSharedGlobalData().getURL_GET_DOCUMENT_LIST();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new  HttpCryptedConnection(NewMCMainActivity.this, encrypt, decrypt);
                    HttpConnectionResult serverResult = null;
                    // Firebase Performance Trace Network Request
                    HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(
                            url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);

                    if(serverResult != null){
                        if(serverResult.isOK()){
                            try{
                                result = serverResult.getResult();
                            }catch (Exception e){
                                FireCrash.log(e);
                                e.printStackTrace();
                            }
                        }
                    }
                    return result;
                }
            }catch (Exception e){
                FireCrash.log(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            DocumentListResponse documentListResponse = new DocumentListResponse();
            if(result != null){
                documentListResponse = GsonHelper.fromJson(result, DocumentListResponse.class);

                GuidelineFaqFragment guidelineFaqFragment = GuidelineFaqFragment.newInstance(documentListResponse.getDocumentList());
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, guidelineFaqFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

    }

    @Override
    public void gotoTimeline() {
        super.gotoTimeline();
    }

    @Override
    protected void gotoAbout() {
        AboutActivity.setChangeLog(ChangeLog.getChangeLog(this), 0);
        super.gotoAbout();
    }

    protected void gotoReportSummary() {
        fragment = new ReportSummaryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoDepositReport() {
        fragment = new DepositReportFragmentNew();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    protected void gotoDepositReportAC() {
        fragment = new DepositReportACFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    protected void gotoDepositReportPC() {
        fragment = new DepositReportPCFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void goToDashBoard() {
        fragment = new DashBoardFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void goToDashboardCollection(){
        DashboardCollectionView fragment = new DashboardCollectionView();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoClosingTask() {
        ClosingTaskRequest request = new ClosingTaskRequest();
        request.setFlag(ClosingTaskRequest.CLOSING_TASK_LIST);
        ClosingTaskSender<ClosingTaskListResponse> sender = new ClosingTaskSender<>(
                this, request, ClosingTaskListResponse.class);
        sender.execute();
    }

}
