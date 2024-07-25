package com.adins.mss.base.tasklog;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.todolist.form.OnTaskListClickListener;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dummy.userhelp_dummy.Adapter.NewTaskLogDummyAdapter;
import com.adins.mss.dummy.userhelp_dummy.UserHelpGeneralDummy;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

public class LogResultActivity extends Fragment implements OnTaskListClickListener {
    public static TaskH selectedLog;
    private static Menu mainMenu;
    private List<TaskH> objects;
    private NewTaskLogAdapter adapter;

    private RelativeLayout layoutView;
    private RecyclerView recyclerView;
    private TextView dataNotFound;
    private TaskLogInterface iTaskLog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private static boolean showDummy = true;
    private FirebaseAnalytics screenName;

    TaskListener myListener = new TaskListener() {
        @Override
        public void onCompleteTask(Object result) {
            adapter.setObjects((List<TaskH>) result);
            objects = adapter.getObjects();
            Global.setListOfSentTask(objects);

            if (objects == null || objects.isEmpty()) {
                dataNotFound.setVisibility(View.VISIBLE);
                layoutView.setBackgroundResource(R.drawable.bg_notfound);
            } else {
                layoutView.setBackgroundResource(R.color.bgColor);
                dataNotFound.setVisibility(View.GONE);
            }

            adapter.notifyDataSetChanged();

            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onCancelTask(boolean value) {
            //EMPTY
        }

        @Override
        public void onLocalData(Object result) {
            //EMPTY
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //EMPTY
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        iTaskLog = new TaskLogImpl(activity);

        try {
            objects = Global.getListOfSentTask();
            if (objects == null || objects.isEmpty()) {
                updateList();
            }

            adapter = new NewTaskLogAdapter(activity, objects, true, LogResultActivity.this);

        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public void updateList() {
        objects = iTaskLog.getListTaskLog();
        if (adapter != null) {
            adapter.setObjects(objects);
            objects = adapter.getObjects();
            Global.setListOfSentTask(objects);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        iTaskLog.cancelOnlineLog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(getActivity());
        View view = inflater.inflate(R.layout.new_fragment_log, container, false);

        /**
         * 2017-09-07
         * Kusnendi
         * Add title on toolbar
         */
        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_log));

        recyclerView = (RecyclerView) view.findViewById(R.id.listLog);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(1, 500);

        dataNotFound = (TextView) view.findViewById(R.id.txv_data_not_found);
        if (adapter == null) {
            try {
                if (objects == null || objects.isEmpty()) {
                    objects = iTaskLog.getListTaskLog();
                    Global.setListOfSentTask(objects);
                }

                adapter = new NewTaskLogAdapter(getActivity(), objects, true, LogResultActivity.this);
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }

        if (Global.ENABLE_USER_HELP &&
                showDummy &&
                Global.userHelpDummyGuide.get(LogResultActivity.this.getClass().getSimpleName()) != null &&
                !Global.userHelpDummyGuide.get(LogResultActivity.this.getClass().getSimpleName()).isEmpty()) {
            NewTaskLogDummyAdapter dummyAdapter = new NewTaskLogDummyAdapter();
            recyclerView.setAdapter(dummyAdapter);
            UserHelpGeneralDummy userHelpGeneralDummy = new UserHelpGeneralDummy();
            userHelpGeneralDummy.showDummyLog(LogResultActivity.this.getActivity(),LogResultActivity.this.getClass().getSimpleName(),recyclerView,this,adapter);
            showDummy = false;
        }else {
            recyclerView.setAdapter(adapter);
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshTimeline);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                iTaskLog.callOnlineLog(myListener);
            }
        });

        layoutView = (RelativeLayout) view.findViewById(R.id.layout);
        if (objects == null || objects.isEmpty()) {
            dataNotFound.setVisibility(View.VISIBLE);
            layoutView.setBackgroundResource(R.drawable.bg_notfound);
        }

        if (!GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
            mSwipeRefreshLayout.setEnabled(false);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        adapter = null;
        Global.setListOfSentTask(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_log), null);
        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(R.string.title_mn_log);
        try {
            updateList();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            NewTaskLogDummyAdapter dummyAdapter = new NewTaskLogDummyAdapter();
            recyclerView.setAdapter(dummyAdapter);
            UserHelpGeneralDummy userHelpGeneralDummy = new UserHelpGeneralDummy();
            userHelpGeneralDummy.showDummyLog(LogResultActivity.this.getActivity(), LogResultActivity.this.getClass().getSimpleName(), recyclerView, this, adapter);
            showDummy = false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClickListener(TaskH item, int position) {
        if (Boolean.FALSE.equals(GlobalData.getSharedGlobalData().getDoingTask())) {
            selectedLog = item;
            try {
                Scheme scheme = null;
                scheme = selectedLog.getScheme();
                if (scheme == null && selectedLog.getUuid_scheme() != null) {
                    scheme = SchemeDataAccess.getOne(getActivity(), selectedLog.getUuid_scheme());
                    if (scheme != null)
                        selectedLog.setScheme(scheme);
                }

                if (scheme == null) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.task_cant_seen),
                            Toast.LENGTH_SHORT).show();
                } else {
                    SurveyHeaderBean header = new SurveyHeaderBean(selectedLog);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CustomerFragment.SURVEY_HEADER, header);
                    bundle.putInt(CustomerFragment.SURVEY_MODE, Global.MODE_VIEW_SENT_SURVEY);
                    Fragment fragment = com.adins.mss.base.dynamicform.CustomerFragment.create(header);

                    FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            } catch (Exception e) {
                FireCrash.log(e);
                Toast.makeText(getActivity(), getActivity().getString(R.string.scheme_not_found_sync),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemLongClickListener(TaskH item, int position) {
        //EMPTY
    }
}