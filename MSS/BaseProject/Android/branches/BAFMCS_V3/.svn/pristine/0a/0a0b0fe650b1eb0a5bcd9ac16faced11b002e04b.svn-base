package com.adins.mss.base.timeline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.adins.mss.base.models.SendUpdateNotificationRequest;
import com.adins.mss.base.models.SendUpdateNotificationResponse;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpView;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.tasklog.LogResultActivity;
import com.adins.mss.base.timeline.activity.Timeline_Activity;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.base.todolist.form.TaskListFragment_new;
import com.adins.mss.base.todolist.form.TaskList_Fragment;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.User;
import com.adins.mss.dummy.userhelp_dummy.Adapter.TimelineDummyAdapter;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PlanTaskDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder_PL;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class NewTimelineFragment extends Fragment implements TimelineListener, ThemeLoader.ColorSetLoaderCallback, IShowError {

    public static TimelineHandler timelineHandler;
    public static boolean haveDoubleMenuVerify = true;
    public static boolean haveDoubleMenuApproval = true;
    private static Context context;
    private static boolean isTimelineOpen = false;
    private ErrorMessageHandler errorMessageHandler;
    TimelineManager manager;
    View view;
    private List<Timeline> objects = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TimelineImpl timelineImpl;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private NewTimelineAdapter adapter;
    private BottomNavigationView bottomNav;

    private String[] searchBy;
    private int range;
    private int position = -1;
    private int searchPosition = 0;

    private FirebaseAnalytics screenName;

    public NewTimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineHandler getTimelineHandler() {
        return timelineHandler;
    }

    public static void setTimelineHandler(TimelineHandler timelineHandler) {
        NewTimelineFragment.timelineHandler = timelineHandler;
    }

    public static NewTimelineFragment newInstance(String param1, String param2) {
        NewTimelineFragment fragment = new NewTimelineFragment();
        return fragment;
    }

    private void setToolbar() {
        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.VISIBLE);
        getActivity().setTitle("");

        // olivia : set tampilan toolbar untuk masing" density
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_MEDIUM:
                getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(200, WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_HIGH:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(300, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(370, WRAP_CONTENT));
                }
                break;
            case DisplayMetrics.DENSITY_260:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(420, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(490, WRAP_CONTENT));
                }
                break;
            case DisplayMetrics.DENSITY_280:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(450, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(530, WRAP_CONTENT));
                }
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(470, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(570, WRAP_CONTENT));
                }
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(710, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(860, WRAP_CONTENT));
                }
                break;
            case android.util.DisplayMetrics.DENSITY_560:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(950, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(1150, WRAP_CONTENT));
                }
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(950, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.spinner).setLayoutParams(new Toolbar.LayoutParams(1150, WRAP_CONTENT));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        errorMessageHandler = new ErrorMessageHandler(this);
        screenName = FirebaseAnalytics.getInstance(getActivity());

        boolean haveMnVerificationlist = NewMainActivity.mnSurveyVerif != null;
        boolean haveMnApprovallist = NewMainActivity.mnSurveyApproval != null;
        boolean haveMnVerificationlistByBranch = NewMainActivity.mnVerifByBranch != null;
        boolean haveMnApprovallistByBranch = NewMainActivity.mnApprovalByBranch != null;
        if (haveMnApprovallistByBranch && !haveMnApprovallist) {
            haveDoubleMenuApproval = false;
        }
        if (haveMnVerificationlistByBranch && !haveMnVerificationlist) {
            haveDoubleMenuVerify = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.new_fragment_timeline, container,
                false);

        bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottomNav);
        if (Global.userHelpDummyGuide.get(NewTimelineFragment.this.getClass().getSimpleName()) !=null &&
                Global.userHelpDummyGuide.size()>0 &&
                Global.userHelpDummyGuide.get(NewTimelineFragment.this.getClass().getSimpleName()).size()>0 &&
                Global.ENABLE_USER_HELP) {
            recyclerView = (RecyclerView) view.findViewById(R.id.listTimeline);
            TimelineDummyAdapter adapter = new TimelineDummyAdapter(this, recyclerView);
            recyclerView.setAdapter(adapter);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
        } else{
            inflateRealTimelineTask();
        }

        String temp_uuid_user;
        try {
            temp_uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            NewMainActivity.InitializeGlobalDataIfError(getActivity().getApplicationContext());
            try {
                temp_uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            } catch (Exception e2) {
                ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat get UUID User"));
                ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getActivity(),
                        "GlobalData", Context.MODE_PRIVATE);
                temp_uuid_user = sharedPref.getString("UUID_USER", "");
            }

        }
        User user = UserDataAccess.getOne(getContext(), temp_uuid_user);
        if (user == null) {
            user = GlobalData.getSharedGlobalData().getUser();
            if (user == null) {
                String message = getActivity().getString(R.string.data_corrupt);
                DialogManager.showForceExitAlert(getActivity(), message);
            }
        }

        setCashOnHandUI();

        if (user != null) {
            if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_ORDER)) {
                searchBy = this.getResources().getStringArray(R.array.dropdownSearchOrder);
            } else if ("Job MH".equalsIgnoreCase(user.getFlag_job())) {
                searchBy = this.getResources().getStringArray(R.array.dropdownSearchTaskSPV);
            } else {
                searchBy = this.getResources().getStringArray(R.array.dropdownSearchTask);
            }
        }

        AppCompatSpinner searchSpinner = (AppCompatSpinner) getActivity().findViewById(R.id.spinnerSearch);
        SearchAdapter searchAdapter = new SearchAdapter(getContext(), R.layout.spinner_style2, searchBy);
        searchAdapter.setDropDownViewResource(R.layout.spinner_style);
        searchSpinner.setAdapter(searchAdapter);

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!Global.ENABLE_USER_HELP || Global.userHelpDummyGuide.size() == 0 ||
                (null != Global.userHelpDummyGuide &&
                                Global.userHelpDummyGuide.size()>0 &&
                                null != Global.userHelpDummyGuide.get(NewTimelineFragment.this.getClass().getSimpleName()) &&
                                Global.userHelpDummyGuide.get(NewTimelineFragment.this.getClass().getSimpleName()).size()==0)) {
                    objects = getTimeline(position, range);
                    if (adapter == null) {
                        adapter = new NewTimelineAdapter(getActivity(), objects, itemListener);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.setObjects(objects);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
                    searchPosition = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //EMPTY
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

        NewTimelineFragment.context = context;
        loadSavedTheme();
        timelineImpl = new TimelineImpl(getActivity(), this);
        timelineImpl.setContext(context);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (checkUserHelpAvailability()) {
            menu.findItem(R.id.mnGuide).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION) {
            TimelineDummyAdapter adapter = new TimelineDummyAdapter(this, recyclerView);
            recyclerView.setAdapter(adapter);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        isTimelineOpen = false;
        bottomNav.getMenu().findItem(R.id.timelineNav).setEnabled(true);
        context.unregisterReceiver(dynamicFormReceiver);

        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            isTimelineOpen = false;
            bottomNav.getMenu().findItem(R.id.timelineNav).setEnabled(true);
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorOnStop", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnStop", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set isTimelineOpen"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Set Firebase screen name
        screenName.setCurrentScreen(NewTimelineFragment.this.getActivity(), getString(R.string.screen_name_timeline), null);

        isTimelineOpen = true;
        bottomNav.getMenu().findItem(R.id.timelineNav).setEnabled(false);
        setToolbar();
        Utility.freeMemory();
        setCashOnHandUI();
        NewMainActivity.tempPosition = 0;
        /**
         * Nendi | 2019.06.21
         * Register Question Form Notification
         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.intent_action_dynamic_question));
        intentFilter.addAction(context.getString(R.string.intent_action_exit_dynamic_form));
        context.registerReceiver(dynamicFormReceiver, intentFilter);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(GlobalData.isRequireRelogin()){
                    DialogManager.showForceExitAlert(getContext(), getContext().getString(R.string.msgLogout));
                }
            }
        }, 2000);
    }

    private void setCashOnHandUI() {
        if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
            timelineImpl.setCashOnHand();
        }
    }

    @Override
    public void onSuccessBackgroundTask(List<Timeline> timelines) {
        onRefreshComplete(timelines);
    }

    @Override
    public void onSuccessImageBitmap(Bitmap bitmap, int imageViewId, int defaultImage) {
        //EMPTY
    }

    public List<Timeline> getTimeline(int position, int range) {
        User user = GlobalData.getSharedGlobalData().getUser();
        GeneralParameter gp_jobspv = new GeneralParameter();
        if(GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_SURVEY)) {
             gp_jobspv = GeneralParameterDataAccess.getOne(getContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_MS_JOBSPV);
        }
        if (position == 0)
            objects = TimelineManager.getAllTimelineWithLimitedDay(getActivity(), range);
        else if (position == 1) {
            if (user != null) {
               if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_ORDER) || user.getFlag_job().equalsIgnoreCase(gp_jobspv.getGs_value())) {
                    objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_SUBMITTED);
                }
                else {
                    objects = TimelineManager.getAllTimelineTaskWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_TASK, TaskHDataAccess.PRIORITY_HIGH);
                }
            }
        }
        else if (position == 2) {
            if (user != null) {
                if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_ORDER) || user.getFlag_job().equalsIgnoreCase(gp_jobspv.getGs_value())) {
                    objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_PENDING);
                }
                else {
                    objects = TimelineManager.getAllTimelineTaskWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_TASK, TaskHDataAccess.PRIORITY_NORMAL);
                }
            }
        }
        else if (position == 3) {
            if (user != null) {
                if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_ORDER) || user.getFlag_job().equalsIgnoreCase(gp_jobspv.getGs_value())) {
                    objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_UPLOADING);
                }
                else {
                    objects = TimelineManager.getAllTimelineTaskWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_TASK, TaskHDataAccess.PRIORITY_LOW);
                }
            }
        }
        else if (position == 4) {
            if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_ORDER) || user.getFlag_job().equalsIgnoreCase(gp_jobspv.getGs_value())) {
                objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_SAVEDRAFT);
            } else {
                objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_SUBMITTED);
            }
        }
        else if (position == 5) {
            if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_ORDER) || user.getFlag_job().equalsIgnoreCase(gp_jobspv.getGs_value())) {
                objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_FAILEDDRAFT);
            } else {
                objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_PENDING);
            }
        }
        else if (position == 6) {
            objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_UPLOADING);
        }
        else if (position == 7) {
            objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_SAVEDRAFT);
        }
        else if (position == 8) {
            objects = TimelineManager.getAllTimelineByTypeWithLimitedDay(getActivity(), range, Global.TIMELINE_TYPE_FAILEDDRAFT);
        }
        return objects;
    }

    private void initiateRefresh() {
        try {
            if(!GlobalData.isRequireRelogin()) {
                timelineImpl.setPosition(searchPosition);
                timelineImpl.refreshBackgroundTask().execute();
            } else{
                DialogManager.showForceExitAlert(getContext(), getContext().getString(R.string.msgLogout));
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorRefresh", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorRefresh", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat initiate Refresh"));
        }
    }

    private NewTimelineAdapter.OnItemListener itemListener = new NewTimelineAdapter.OnItemListener() {
        @Override
        public void onItemClick(Timeline timeline, int index) {
            position = index;
            if (!Global.isLockTask() && !Global.BACKPRESS_RESTRICTION)
                headerClick(timeline);
        }

        @Override
        public void onAttendanceClick(Timeline timeline, int index) {
            position = index;
            if (!Global.isLockTask() && !Global.BACKPRESS_RESTRICTION) attendanceClick(timeline);
        }

        @Override
        public void onStatusClick(Timeline timeline, int index) {
            position = index;
            if (!Global.isLockTask() && !Global.BACKPRESS_RESTRICTION) statusClick(timeline);
        }

        @Override
        public void onStatusLongClick(Timeline timeline, int index) {
            position = index;
            statusLongClick(timeline);
        }
    };

    private void onRefreshComplete(List<Timeline> result) {
        final List<Timeline> newResult = result;

        try {
            //Selagi tidak di dalam penggunaan user help, refresh tampilan timeline
            if (!(Global.userHelpDummyGuide != null && !Global.userHelpDummyGuide.isEmpty() && !Global.userHelpDummyGuide.get(NewTimelineFragment.this.getClass().getSimpleName())
                    .isEmpty())) {
                if (isAdded() && adapter == null) {
                    adapter = new NewTimelineAdapter(getActivity(), newResult, itemListener);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.setObjects(newResult);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
                //To Fix RecyclerView went blank on some device
                recyclerView.smoothScrollToPosition(0);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
        }

        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                try {
                    setCashOnHandUI();
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorOnResume", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Set Cash UI"));
                }
            }
        });
    }

    private void loadSavedTheme(){
        ThemeLoader themeLoader = new ThemeLoader(context);
        themeLoader.loadSavedColorSet(this);
    }

    private void applyTheme(DynamicTheme dynamicTheme){
       //EMPTY
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme) {
        if(dynamicTheme != null && !dynamicTheme.getThemeItemList().isEmpty()){
            applyTheme(dynamicTheme);
        }
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {
        //EMPTY
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(notifType == ErrorMessageHandler.DIALOG_TYPE){
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withTitle(errorSubject)
                    .withMessage(errorMsg)
                    .isCancelableOnTouchOutside(true)
                    .show();
        }
    }

    public class TimelineHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            try {
                if (isTimelineOpen) {
                    initiateRefresh();
                    bottomNav.getMenu().findItem(R.id.timelineNav).setEnabled(false);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorTimelineHandler", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorTimelineHandler", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat initiate Refresh"));
            }
        }
    }

    public class SearchAdapter extends ArrayAdapter<String> {
        private Context context;
        private String[] values;

        public SearchAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            this.context = context;
            this.values = objects;
        }

        @Override
        public int getCount() {
            return values.length;
        }

        @Override
        public String getItem(int position) {
            return values[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_layout, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values[position]);
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values[position]);
            return label;
        }
    }

    private void attendanceClick(Timeline timeline) {
        String timeline_type = timeline.getTimelineType().getTimeline_type();
        if (NewMainActivity.Force_Uninstall) {
            DialogManager.UninstallerHandler((Activity) context);
        } else {
            if (Global.TIMELINE_TYPE_CHECKIN.equals(timeline_type) || Global.TIMELINE_TYPE_CHECKOUT.equals(timeline_type)) {
                Intent intent = new Intent(context, MapsViewer.class);
                intent.putExtra("latitude", timeline.getLatitude());
                intent.putExtra("longitude", timeline.getLongitude());
                context.startActivity(intent);
            }
            else if (Global.TIMELINE_TYPE_PUSH_NOTIFICATION.equals(timeline_type)) {
                String description = timeline.getDescription();
                String[] message = description.split("\\|");
                if (message.length > 1) {
                    String uuidNotification = message[0];
                    String desc = message[1];

                    String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                    SendUpdateNotification sendUpdateNotif = new SendUpdateNotification(context, uuidUser, uuidNotification);
                    sendUpdateNotif.execute();

                    String urlRegex = "((http|https)://)(www\\.)?[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)";
                    Pattern pattern = Pattern.compile(urlRegex);
                    Matcher matcher = pattern.matcher(desc);
                    if (matcher.find()) {
                        String url = matcher.group();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    }
                }
            }
        }
    }

    private void statusClick(Timeline timeline) {
        String timeline_type = timeline.getTimelineType().getTimeline_type();
        if (NewMainActivity.Force_Uninstall) {
            DialogManager.UninstallerHandler((Activity) context);
        } else {
            TaskH taskH = timeline.getTaskH();
            if (Global.TIMELINE_TYPE_SUBMITTED.equals(timeline_type) || Global.TIMELINE_TYPE_APPROVED.equals(timeline_type) ||
                    Global.TIMELINE_TYPE_REJECTED.equals(timeline_type) || Global.TIMELINE_TYPE_VERIFIED.equals(timeline_type)) {
                Fragment fragment = new LogResultActivity();
                try {
                    FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            } else if (Global.TIMELINE_TYPE_VERIFICATION.equals(timeline_type)) {
                try {
                    if (Timeline_Activity.haveDoubleMenuVerify) {
                        Fragment fragment1 = NewMainActivity.getVerificationFragment();
                        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale);
                        transaction.replace(R.id.content_frame, fragment1);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Fragment fragment1 = NewMainActivity.getVerificationFragmentByBranch();
                        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale);
                        transaction.replace(R.id.content_frame, fragment1);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    Fragment fragment1 = NewMainActivity.getVerificationFragment();
                    FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale);
                    transaction.replace(R.id.content_frame, fragment1);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            } else if (Global.TIMELINE_TYPE_APPROVAL.equals(timeline_type)) {
                try {
                    if (Timeline_Activity.haveDoubleMenuApproval) {
                        Fragment fragment1 = NewMainActivity.getApprovalFragment();
                        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale);
                        transaction.replace(R.id.content_frame, fragment1);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Fragment fragment1 = NewMainActivity.getApprovalFragmentByBranch();
                        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale);
                        transaction.replace(R.id.content_frame, fragment1);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    Fragment fragment1 = NewMainActivity.getApprovalFragment();
                    FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale);
                    transaction.replace(R.id.content_frame, fragment1);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            } else if((Global.TIMELINE_TYPE_SAVEDRAFT.equals(timeline_type)||Global.TIMELINE_TYPE_FAILEDDRAFT.equals(timeline_type))
                    && taskH != null && taskH.getScheme().getForm_type().equals("KTP")){
                if(Global.PLAN_TASK_ENABLED){
                    planTaskEnableHeaderClick(taskH);
                    return;
                }
                SurveyHeaderBean header = new SurveyHeaderBean(taskH);
                CustomerFragment.setHeader(header);
                CustomerFragment fragment = CustomerFragment.create(header);
                fragment.gotoNextDynamicForm(this.getActivity());
            } else {
                if(taskH == null){
                    errorMessageHandler.processError(context.getString(R.string.warning_capital),
                            context.getString(R.string.task_not_available2),
                            ErrorMessageHandler.DIALOG_TYPE);
                    return;
                }
                if(Global.PLAN_TASK_ENABLED){
                    planTaskEnableHeaderClick(taskH);
                    return;
                }
                Fragment fragment = new PriorityTabFragment();
                try {
                    FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
        }
    }

    private void statusLongClick(final Timeline timeline) {
        String timeline_type = timeline.getTimelineType().getTimeline_type();
        final String uuid_task_h = timeline.getUuid_task_h();
        final TaskH taskH = TaskHDataAccess.getOneHeader(context, timeline.getUuid_task_h());
        if (MainMenuActivity.Force_Uninstall) {
            DialogManager.UninstallerHandler((Activity) context);
        } else {
            if (Global.TIMELINE_TYPE_UPLOADING.equalsIgnoreCase(timeline_type)) {
                if(taskH!=null) {
                    if (TaskHDataAccess.STATUS_SEND_UPLOADING.equalsIgnoreCase(taskH.getStatus())) {
                        try {
                            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                            dialogBuilder.withTitle(context.getString(R.string.info_capital)).withMessage(context.getString(R.string.confirm_upload) + " " + timeline.getName() + " ?")
                                    .withButton1Text(context.getString(R.string.btnYes)).withButton2Text(context.getString(R.string.btnCancel))
                                    .setButton1Click(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            dialogBuilder.dismiss();
                                            if (Tool.isInternetconnected(context)) {
                                                if (Global.isIsUploading() || Global.isIsManualUploading()) {
                                                    Toast.makeText(context, context.getString(R.string.upload_on_queue), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    try {
                                                        List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(context, timeline.getUuid_user(), uuid_task_h);
                                                        TaskManager.ManualUploadImage((Activity) context, taskd);
                                                    } catch (Exception e) {
                                                        FireCrash.log(e);
                                                        Toast.makeText(context, context.getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setButton2Click(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            dialogBuilder.dismiss();
                                        }
                                    })
                                    .show();
                        } catch (Exception e) {
                            FireCrash.log(e);
                            e.printStackTrace();
                        }
                    }
                }
            } else if (Global.TIMELINE_TYPE_PENDING.equalsIgnoreCase(timeline_type)) {
                if(taskH!=null) {
                    if (TaskHDataAccess.STATUS_SEND_FAILED.equalsIgnoreCase(taskH.getStatus())) {
                        String btnText1 = context.getString(R.string.btnSend);

                        if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION))
                            btnText1 = context.getString(R.string.verifyBtn);
                        else if (taskH.getIs_prepocessed() != null && Global.FORM_TYPE_APPROVAL.equals(taskH.getIs_prepocessed()))
                            btnText1 = context.getString(R.string.approveBtn);

                        final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
                        dialogBuilder.withNoTitle().withNoMessage().withButton1Text(btnText1).withButton2Text(context.getString(R.string.btnDelete))
                                .setButton1Click(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        dialogBuilder.dismiss();
                                        if (Tool.isInternetconnected(context)) {
                                            if (uuid_task_h != null) {
                                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                                    if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                                                        new TaskManager.ApproveTaskOnBackground((Activity) context, taskH, Global.FLAG_FOR_REJECTEDTASK, false, taskH.getVerification_notes()).execute();
                                                    else if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
                                                        new TaskManager.RejectWithReSurveyTaskOnBackground((Activity) context, taskH, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.VERIFICATION_FLAG).execute();
                                                    else
                                                        new TaskManager.ForceSendTaskOnBackground((Activity) context, taskH.getTask_id()).execute();
                                                } else if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL)) {
                                                    if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                                                        new TaskManager.ApproveTaskOnBackground((Activity) context, taskH, Global.FLAG_FOR_REJECTEDTASK, true, taskH.getVerification_notes()).execute();
                                                    else if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
                                                        new TaskManager.RejectWithReSurveyTaskOnBackground((Activity) context, taskH, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.APPROVAL_FLAG).execute();
                                                    else
                                                        new TaskManager.ApproveTaskOnBackground((Activity) context, taskH, Global.FLAG_FOR_APPROVALTASK, true, taskH.getVerification_notes()).execute();
                                                } else
                                                    new TaskManager.ForceSendTaskOnBackground((Activity) context, taskH.getTask_id()).execute();
                                            }
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setButton2Click(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        TaskHDataAccess.deleteWithRelation(context, taskH);
                                        TimelineDataAccess.delete(context, timeline);
                                        adapter.deleteTimeline(position);
                                        adapter.notifyDataSetChanged();
                                        if (uuid_task_h != null)
                                            ToDoList.removeSurveyFromList(taskH.getTask_id());
                                        if (timelineHandler != null) {
                                            timelineHandler.sendEmptyMessage(0);
                                        }
                                        dialogBuilder.dismiss();
                                    }
                                }).show();
                    }
                }
            } else if (Global.TIMELINE_TYPE_SAVEDRAFT.equalsIgnoreCase(timeline_type)||Global.TIMELINE_TYPE_FAILEDDRAFT.equalsIgnoreCase(timeline_type)) {
                if(taskH!=null) {
                    if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equalsIgnoreCase(taskH.getStatus())) {
                        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                        dialogBuilder.withTitle(context.getString(R.string.info_capital)).withMessage(context.getString(R.string.confirm_delete) + " " + timeline.getName() + " ?")
                                .withButton1Text(context.getString(R.string.btnYes)).withButton2Text(context.getString(R.string.btnCancel))
                                .setButton1Click(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        try {
                                            TaskHDataAccess.deleteWithRelation(context, taskH);
                                            ToDoList.removeSurveyFromList(taskH.getTask_id());
                                            TimelineDataAccess.delete(context, timeline);
                                            adapter.deleteTimeline(position);
                                            adapter.notifyDataSetChanged();
                                            if (timelineHandler != null) {
                                                timelineHandler.sendEmptyMessage(0);
                                            }
                                        } catch (Exception ex) {
                                            Toast.makeText(context, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                                        }
                                        dialogBuilder.dismiss();
                                    }
                                })
                                .setButton2Click(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            }
        }
    }

    public void headerClick(Timeline timeline) {
        String timeline_type = timeline.getTimelineType().getTimeline_type();
        if (NewMainActivity.Force_Uninstall) {
            DialogManager.UninstallerHandler((Activity) context);
        } else if(GlobalData.isRequireRelogin()){
            DialogManager.showForceExitAlert(getActivity(), context.getString(R.string.msgLogout));
        }
        else {
            if (Global.TIMELINE_TYPE_TASK.equals(timeline_type) ||
                    Global.TIMELINE_TYPE_VERIFICATION.equals(timeline_type) ||
                    Global.TIMELINE_TYPE_APPROVAL.equals(timeline_type)) {
                TaskH taskH = timeline.getTaskH();
                try {
                    String taskId = taskH.getTask_id();

                    TaskH nTaskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
                    if (Global.TIMELINE_TYPE_APPROVAL.equals(timeline_type))
                        nTaskH = TaskHDataAccess.getOneHeader(context, timeline.getUuid_task_h());

                    String appl = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                    if (Global.TIMELINE_TYPE_TASK.equals(timeline_type) && Global.APPLICATION_COLLECTION.equalsIgnoreCase(appl)) {
                        String cashLimit = GlobalData.getSharedGlobalData().getUser().getCash_limit();
                        double limit = cashLimit != null ? Double.parseDouble(cashLimit) : 0.0;
                        String coh = GlobalData.getSharedGlobalData().getUser().getCash_on_hand();
                        double cashOnHand = coh != null ? Double.parseDouble(coh) : 0.0;
                        if (isCOHAktif() && limit > 0 && cashOnHand > limit) {
                            DialogManager.showAlertNotif(context, context.getString(R.string.limit_coh), "Cash On Hand");
                            return;
                        }
                    }

                    if (nTaskH != null) {
                        if(Global.PLAN_TASK_ENABLED){
                            planTaskEnableHeaderClick(nTaskH);
                            return;
                        }
                        if (nTaskH.getScheme() != null) {
                            SurveyHeaderBean header = new SurveyHeaderBean(nTaskH);
                            Fragment fragment = CustomerFragment.create(header);
                            FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            NewMainActivity.tempPosition = 0;
                        } else {
                            errorMessageHandler.processError(context.getString(R.string.info_capital),
                                    context.getString(R.string.task_cant_seen),ErrorMessageHandler.DIALOG_TYPE);
                        }
                    } else {
                        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                        if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                            errorMessageHandler.processError(context.getString(R.string.warning_capital),
                                    context.getString(R.string.task_reassign_svy),
                                    ErrorMessageHandler.DIALOG_TYPE);
                        } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                            errorMessageHandler.processError(context.getString(R.string.warning_capital),
                                    context.getString(R.string.task_reassign_col),
                                    ErrorMessageHandler.DIALOG_TYPE);
                        } else {
                            errorMessageHandler.processError(context.getString(R.string.warning_capital),
                                    context.getString(R.string.task_not_available2),
                                    ErrorMessageHandler.DIALOG_TYPE);
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (NewMainActivity.mnSurveyApproval != null) {
                        if (Global.TIMELINE_TYPE_VERIFICATION.equals(timeline_type)) {
                            errorMessageHandler.processError(context.getString(R.string.info_capital),
                                    context.getString(R.string.task_verified),
                                    ErrorMessageHandler.DIALOG_TYPE);
                        }
                    } else {
                        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                        if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                            errorMessageHandler.processError(context.getString(R.string.warning_capital),
                                    context.getString(R.string.task_reassign_svy),
                                    ErrorMessageHandler.DIALOG_TYPE);
                        } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                            errorMessageHandler.processError(context.getString(R.string.warning_capital),
                                    context.getString(R.string.task_reassign_col),
                                    ErrorMessageHandler.DIALOG_TYPE);
                        } else {
                            errorMessageHandler.processError(context.getString(R.string.warning_capital),
                                    context.getString(R.string.task_not_available2),
                                    ErrorMessageHandler.DIALOG_TYPE);
                        }
                    }
                }
            }
            else {
                TaskH taskH = timeline.getTaskH();
                if (taskH != null) {
                    if(Global.PLAN_TASK_ENABLED ){
                        planTaskEnableHeaderClick(taskH);
                        return;
                    }
                    try {
                        String taskId = taskH.getUuid_task_h();
                        taskH = TaskHDataAccess.getOneHeader(context, taskId);
                        if (taskH.getScheme() != null) {
                            if(taskH.getScheme().getForm_type().equals("KTP")){
                                SurveyHeaderBean header = new SurveyHeaderBean(taskH);
                                CustomerFragment.setHeader(header);
                                CustomerFragment fragment = CustomerFragment.create(header);
                                fragment.gotoNextDynamicForm(this.getActivity());
                                if (header.getPriority() != null && header.getPriority().length() > 0) {
                                    if (header.getOpen_date() == null) {
                                        header.setOpen_date(Tool.getSystemDateTime());
                                    }
                                }
                            } else {
                                SurveyHeaderBean header = new SurveyHeaderBean(taskH);
                                Fragment fragment = CustomerFragment.create(header);
                                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                transaction.replace(R.id.content_frame, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        } else {
                            errorMessageHandler.processError(context.getString(R.string.info_capital),
                                    context.getString(R.string.task_cant_seen),
                                    ErrorMessageHandler.DIALOG_TYPE);
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        errorMessageHandler.processError(context.getString(R.string.info_capital),
                                context.getString(R.string.task_not_available),
                                ErrorMessageHandler.DIALOG_TYPE);
                    }
                } else {
                    errorMessageHandler.processError(context.getString(R.string.info_capital),
                            context.getString(R.string.task_not_available),
                            ErrorMessageHandler.DIALOG_TYPE);
                }
            }
        }
    }

    private void planTaskEnableHeaderClick(TaskH taskH){
        List<PlanTask> result = PlanTaskDataAccess.findPlanByTaskH(context,taskH.getTask_id());
        int tabId = 0;
        if(!result.isEmpty()){
            if(result.get(0) != null && result.get(0).getPlan_status().equals(PlanTaskDataAccess.STATUS_FINISH)){
                //task finish then show customer header
                SurveyHeaderBean header = new SurveyHeaderBean(taskH);
                Fragment fragment = CustomerFragment.create(header);
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return;
            }

            //go to today plan tab
            tabId = 1;
        }

        if(taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)){
            SurveyHeaderBean header = new SurveyHeaderBean(taskH);
            Fragment fragment = CustomerFragment.create(header);
            FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
            transaction.replace(R.id.content_frame, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return;
        }

        TaskListFragment_new tasklistFragment = new TaskListFragment_new();
        Bundle bundle = new Bundle();
        bundle.putInt(TaskList_Fragment.BUND_KEY_PAGE,tabId);
        tasklistFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = NewMainActivity.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame,tasklistFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private boolean isCOHAktif() {
        String parameter = GeneralParameterDataAccess.getOne(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                Global.GS_CASHONHAND).getGs_value();
        return parameter != null && parameter.equals(Global.TRUE_STRING);
    }

    private BroadcastReceiver dynamicFormReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Global.setLockTask(!Global.isLockTask());
        }
    };
    public void inflateRealTimelineTask(){

        NewTimelineFragment.setTimelineHandler(new TimelineHandler());
        try {
            manager = new TimelineManager(getActivity());
            range = GlobalData.getSharedGlobalData().getKeepTimelineInDays();
            objects = TimelineManager.getAllTimelineWithLimitedDay(getActivity(), range);

            recyclerView = (RecyclerView) view.findViewById(R.id.listTimeline);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            adapter = new NewTimelineAdapter(getActivity(), objects, itemListener);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorOnCreateView", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnCreateView", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshTimeline);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSwipeRefreshLayout.setColorSchemeColors(
                        getResources().getColor(R.color.tv_gray, getContext().getTheme()),
                        getResources().getColor(R.color.tv_light, getContext().getTheme()),
                        getResources().getColor(R.color.tv_normal, getContext().getTheme()),
                        getResources().getColor(R.color.tv_darker, getContext().getTheme()));
            } else {
                mSwipeRefreshLayout.setColorSchemeColors(
                        getResources().getColor(R.color.tv_gray),
                        getResources().getColor(R.color.tv_light),
                        getResources().getColor(R.color.tv_normal),
                        getResources().getColor(R.color.tv_darker));
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorsetColorScheme", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorsetColorScheme", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Color Scheme"));
            if (Global.IS_DEV)
                e.printStackTrace();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateRefresh();
            }
        });

        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                initiateRefresh();
            }
        }, 5000);
    }

    private boolean checkUserHelpAvailability() {
        List<UserHelpView> userHelpViews = Global.userHelpGuide.get(NewTimelineFragment.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpViews != null;
    }

    public static class SendUpdateNotification extends AsyncTask<Void, Void, String> {
        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final String uuidUser;
        private final String uuidNotification;

        public SendUpdateNotification(Context mContext, String uuidUser, String uuidNotification) {
            this.mContext = mContext;
            this.uuidUser = uuidUser;
            this.uuidNotification = uuidNotification;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String errorMessage = null;
            if (Tool.isInternetconnected(mContext)) {
                SendUpdateNotificationRequest request = new SendUpdateNotificationRequest();
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.addImeiAndroidIdToUnstructured();
                request.setUuidUser(uuidUser);
                request.setFlag(Global.TRUE_STRING);
                request.setUuidNotification(uuidNotification);

                String json = GsonHelper.toJson(request);
                String url = GlobalData.getSharedGlobalData().getURL_SEND_UPDATE_NOTIFICATION();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(mContext, encrypt, decrypt);

                HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, json);

                try {
                    HttpConnectionResult serverResult = httpConn.requestToServer(url, json);
                    Utility.metricStop(networkMetric, serverResult);
                    String result = serverResult.getResult();
                    SendUpdateNotificationResponse response = GsonHelper.fromJson(result, SendUpdateNotificationResponse.class);
                    if (response.getStatus().getCode() == 0) {
                        Log.i("INFO NOTIFICATION", "Update Notification Success");
                    }
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                }
            } else {
                errorMessage = mContext.getString(R.string.no_internet_connection);
            }
            return errorMessage;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);

            if (null != message && !"".equals(message)) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
