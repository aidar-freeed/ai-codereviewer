package com.adins.mss.svy.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.tasklog.SurveyTaskAdapter;
import com.adins.mss.base.todolist.form.OnTaskListClickListener;
import com.adins.mss.base.todolist.form.TasklistListener;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dummy.userhelp_dummy.Adapter.NewTaskLogDummyAdapter;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.svy.R;
import com.adins.mss.svy.UserHelpSVYDummy;
import com.adins.mss.svy.tool.Constants;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.Calendar;
import java.util.List;

public class SurveyApprovalFragment extends Fragment implements OnTaskListClickListener, TasklistListener {
    public static TaskH selectedApproval;
    private static Menu mainMenu;
    private List<TaskH> objects;
    private SurveyTaskAdapter adapter;
//    private GridView gridView;
    private RecyclerView recyclerView;
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String errMessage = null;
    private SurveyActivityInterface iSurveyActivity;
    private LinearLayoutManager layoutManager;
    private boolean showDummy = true;

    private FirebaseAnalytics screenName;
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        this.context = activity;
        iSurveyActivity = new SurveyActivityImpl(activity);
        setHasOptionsMenu(true);
        try {
            objects = Constants.listOfApprovalTask;
            if (objects == null || objects.size() == 0) {
                objects = TaskHDataAccess.getAllApprovalForUser(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
                Constants.listOfApprovalTask = objects;
            }
            try {
//                MSMainMenuActivity.mnSVYApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(getActivity())));
//                if(MainMenuActivity.menuAdapter!=null)
//                    MainMenuActivity.menuAdapter.notifyDataSetChanged();
//                MainMenuActivity.setDrawerCounter();
                NewMainActivity.setCounter();
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
                ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
            }
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO: handle exception
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(getActivity());
        View view = inflater.inflate(R.layout.new_fragment_survey_verification, container, false);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        this.mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(com.adins.mss.base.R.color.tv_light),
                getResources().getColor(com.adins.mss.base.R.color.tv_normal),
                getResources().getColor(com.adins.mss.base.R.color.tv_dark),
                getResources().getColor(com.adins.mss.base.R.color.tv_darker));
        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                SurveyApprovalFragment.this.initiateRefresh();
            }
        });
//        getActivity().getActionBar().setTitle(getString(com.adins.mss.svy.R.string.title_mn_surveyapproval));

        // olivia : set toolbar
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_surveyapproval));

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(1, 500);
//        initiateRefresh();
        adapter = new SurveyTaskAdapter(getActivity(), objects, SurveyApprovalFragment.this);
        recyclerView.setAdapter(adapter);
        if(Global.ENABLE_USER_HELP &&
                showDummy &&
                Global.userHelpDummyGuide.get(SurveyApprovalFragment.this.getClass().getSimpleName()) != null &&
                Global.userHelpDummyGuide.get(SurveyApprovalFragment.this.getClass().getSimpleName()).size()>0) {
            NewTaskLogDummyAdapter dummyAdapter = new NewTaskLogDummyAdapter();
            recyclerView.setAdapter(dummyAdapter);
            UserHelpSVYDummy userHelpSVYDummy = new UserHelpSVYDummy();
            userHelpSVYDummy.showDummyVerif(SurveyApprovalFragment.this.getActivity(),SurveyApprovalFragment.this.getClass().getSimpleName(), recyclerView,adapter);
            showDummy = false;
        }
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnGuide){
            if(!Global.BACKPRESS_RESTRICTION) {
                if(!Global.BACKPRESS_RESTRICTION) {
                    NewTaskLogDummyAdapter dummyAdapter = new NewTaskLogDummyAdapter();
                    recyclerView.setAdapter(dummyAdapter);
                    UserHelpSVYDummy userHelpSVYDummy = new UserHelpSVYDummy();
                    userHelpSVYDummy.showDummyVerif(SurveyApprovalFragment.this.getActivity(), SurveyApprovalFragment.this.getClass().getSimpleName(), recyclerView, adapter);
                    showDummy = false;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiateRefresh() {
//        RefreshBackgroundTask task = new RefreshBackgroundTask();
//        task.execute();
        iSurveyActivity.getBackgroundTask(SurveyApprovalFragment.this, false, false);
    }

    private void onRefreshComplete(List<TaskH> result) {
        this.mSwipeRefreshLayout.setRefreshing(false);
        NiftyDialogBuilder fragment;
        objects = result;

        try {
//            MSMainMenuActivity.mnSVYApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(getActivity())));
//            if(MainMenuActivity.menuAdapter!=null)
//                MainMenuActivity.menuAdapter.notifyDataSetChanged();
//            MainMenuActivity.setDrawerCounter();
            NewMainActivity.setCounter();
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
        }

//        if (this.errMessage != null) {
//            fragment = NiftyDialogBuilder.getInstance(this.context);
//            fragment.withTitle(context.getString(com.adins.mss.base.R.string.error_capital)).withMessage(this.errMessage).show();
//        } else
        if (result != null && result.size() != 0) {
            try {
                if(objects==null || objects.size()==0){
                    Constants.listOfApprovalTask = objects;
                    adapter = new SurveyTaskAdapter(getActivity(), objects, SurveyApprovalFragment.this);
                    recyclerView.setAdapter(adapter);
//                  MSMainMenuActivity.mnSVYApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(getActivity())));
//                  MSMainMenuActivity.menuAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                FireCrash.log(e);
                e.getMessage();
            }
        }
//        else {
//            Constants.listOfApprovalTask = objects;
//            adapter = new TaskLogArrayAdapter(getActivity(), objects, false);
//            listView.setAdapter(adapter);
//            //MainMenuActivity.mnSVYApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(context)));
//            fragment = NiftyDialogBuilder.getInstance(this.context);
//            fragment.withTitle(context.getString(com.adins.mss.base.R.string.info_capital)).withMessage(getString(com.adins.mss.base.R.string.msgNoApproval)).show();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        adapter = null;
        Constants.listOfApprovalTask = null;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().getActionBar().removeAllTabs();
//        getActivity().getActionBar().setTitle(getString(com.adins.mss.svy.R.string.title_mn_surveyapproval));
//        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_survey_approval), null);

        // olivia : set toolbar
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_surveyapproval));

        try {
//            MSMainMenuActivity.mnSVYApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(getActivity())));
//            if(MainMenuActivity.menuAdapter!=null)
//                MainMenuActivity.menuAdapter.notifyDataSetChanged();
//            MainMenuActivity.setDrawerCounter();
            NewMainActivity.setCounter();
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
        }

//		if(objects!=null && objects.size()>0){
        objects = TaskHDataAccess.getAllApprovalForUser(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
        Constants.listOfApprovalTask = objects;
//		}
    }

    @Override
    public void onItemClickListener(TaskH item, int position) {
        selectedApproval = item;
        SurveyHeaderBean header = new SurveyHeaderBean(selectedApproval);
        // TODO Action Lempar ke Customer ACtivity
        Bundle bundle = new Bundle();
        bundle.putSerializable(CustomerFragment.SURVEY_HEADER, header);
        bundle.putInt(CustomerFragment.SURVEY_MODE, Global.MODE_VIEW_SENT_SURVEY);
        Fragment fragment = com.adins.mss.base.dynamicform.CustomerFragment.create(header);

        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onItemLongClickListener(TaskH item, int position) {

    }

    @Override
    public void onRefreshBackgroundCancelled(boolean value) {

    }

    @Override
    public void onRefreshBackgroundComplete(List<TaskH> result) {
        onRefreshComplete(result);
    }


//    @SuppressLint({"NewApi"})
//    private class RefreshBackgroundTask extends AsyncTask<Void, Void, List<TaskH>> {
//        static final int TASK_DURATION = 2000;
//
//        private RefreshBackgroundTask() {
//        }
//
//        protected List<TaskH> doInBackground(Void... params) {
//            List<TaskH> result = null;
//            User user = GlobalData.getSharedGlobalData().getUser();
//
//            if (Tool.isInternetconnected(context)) {
//                MssRequestType requestType = new MssRequestType();
//                requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
//                requestType.addImeiAndroidIdToUnstructured();
//
//                String json = GsonHelper.toJson(requestType);
//                String url = GlobalData.getSharedGlobalData().getURL_GET_LIST_APPROVAL();
//                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
//                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
//                HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
//                HttpConnectionResult serverResult = null;
//                try {
//                    result = TaskHDataAccess.getAllApprovalForUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
//                    if (result != null && result.size() > 0)
//                        serverResult = httpConn.requestToServer(url, json, Global.SORTCONNECTIONTIMEOUT);
//                    else
//                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
//                } catch (Exception e) {             FireCrash.log(e);
//                    e.printStackTrace();
//                    errMessage = e.getMessage();
//                }
//
//                String stringResult = serverResult.getResult();
//
//                try {
//                    JsonResponseTaskList taskList = GsonHelper.fromJson(stringResult, JsonResponseTaskList.class);
//                    if (taskList.getStatus().getCode() == 0) {
//                        List<TaskH> listTaskH = taskList.getListTaskList();
//                        if (listTaskH != null && listTaskH.size() > 0) {
//                            String uuid_timelineType = TimelineTypeDataAccess.getTimelineTypebyType(getActivity(), Global.TIMELINE_TYPE_APPROVAL).getUuid_timeline_type();
//                            for (TaskH taskHLocal : result) {
//                                boolean wasDeleted = true;
//                                for (TaskH taskH : listTaskH) {
//                                    if (taskH.getUuid_task_h().equals(taskHLocal.getUuid_task_h()))
//                                        wasDeleted = false;
//                                }
//                                if (wasDeleted) {
//                                    TaskHDataAccess.delete(getActivity(), taskHLocal);
//                                    /*Timeline timeline = TimelineDataAccess.getOneTimelineByTaskH(getActivity(), user.getUuid_user(), taskHLocal.getUuid_task_h(), uuid_timelineType);
//                                    if(timeline != null)
//                                        TimelineDataAccess.delete(getActivity(), timeline);*/
//                                }
//                            }
//
//                            for (TaskH taskH : listTaskH) {
//                                taskH.setUser(user);
//                                taskH.setIs_verification(Global.TRUE_STRING);
//
//                                String uuid_scheme = taskH.getUuid_scheme();
//                                Scheme scheme = SchemeDataAccess.getOne(getActivity(), uuid_scheme);
//                                if (scheme != null) {
//                                    taskH.setScheme(scheme);
//
//                                    TaskH h = TaskHDataAccess.getOneHeader(getActivity(), taskH.getUuid_task_h());
//                                    boolean wasInTimeline = TimelineDataAccess.getOneTimelineByTaskH(getActivity(), user.getUuid_user(), taskH.getUuid_task_h(), uuid_timelineType) != null;
//                                    if (h != null && h.getStatus() != null) {
//                                        if (!ToDoList.isOldTask(h)) {
//                                            TaskHDataAccess.addOrReplace(getActivity(), taskH);
//                                            if (!wasInTimeline)
//                                                TimelineManager.insertTimeline(getActivity(), taskH);
//                                        }
//                                    } else {
//                                        TaskHDataAccess.addOrReplace(getActivity(), taskH);
//                                        if (!wasInTimeline)
//                                            TimelineManager.insertTimeline(getActivity(), taskH);
//                                    }
//                                } else {
//                                    errMessage = context.getString(com.adins.mss.base.R.string.scheme_not_found);
//                                }
//                            }
//                        }
//                    } else {
//                        errMessage = stringResult;
//                    }
//                } catch (Exception e) {             FireCrash.log(e);
//                    errMessage = e.getMessage();
//                }
//            }
//            try {
////                if (context != null) {
////                    result = TaskHDataAccess.getAllApprovalForUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
////                } else if (getActivity() != null) {
////                    result = TaskHDataAccess.getAllApprovalForUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
////                } else {
////                    result = TaskHDataAccess.getAllApprovalForUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
////                }
//            } catch (Exception var6) {
//                var6.printStackTrace();
//                errMessage = var6.getMessage();
//            }
//
//            return result;
//        }
//
//        protected void onPostExecute(List<TaskH> result) {
//            super.onPostExecute(result);
//            SurveyApprovalFragment.this.onRefreshComplete(result);
//        }
//    }
}