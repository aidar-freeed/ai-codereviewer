package com.adins.mss.base.todolist.form;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.Helper;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.base.timeline.TimelineImpl;
import com.adins.mss.base.timeline.TimelineInterface;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.helper.TaskFilterParam;
import com.adins.mss.base.todolist.form.helper.TaskPlanFilterObservable;
import com.adins.mss.base.todolist.form.helper.TaskPlanFilterObserver;
import com.adins.mss.base.todolist.form.todaysplan.OnTaskCheckedListener;
import com.adins.mss.base.todolist.form.todaysplan.SelectTaskPlanHandler;
import com.adins.mss.base.todolist.form.todaysplan.TodayPlanHandler;
import com.adins.mss.base.todolist.form.todaysplan.UnplanTaskAdapter;
import com.adins.mss.base.todolist.form.todaysplan.UnplanTaskDummyAdapter;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dummy.userhelp_dummy.Adapter.PriorityDummyAdapter;
import com.adins.mss.dummy.userhelp_dummy.UserHelpGeneralDummy;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpView;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder_PL;
import com.adins.mss.foundation.formatter.Tool;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnTaskListClickListener}
 * interface.
 */
public class PriorityTabFragment extends Fragment implements OnTaskListClickListener, TasklistListener, ThemeLoader.ColorSetLoaderCallback
        , TaskListTabInteractor.TabPage
        , OnTaskCheckedListener
        , SelectTaskPlanHandler.OnSelectedChange
        , TaskPlanFilterObservable<TaskFilterParam> {

    public static final String uuidSchemeDummy = "schemeDummy";
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String TASK_LIST_TAB_USERHELP = "PlanTaskList";
    //ineedthisfor coh
    public static ToDoList toDoList;
    private static List<TaskH> listTaskH;
    private View priorityView;
    public static Handler mHandler;
    public static PriorityViewAdapter viewAdapter;
    private TodayPlanHandler todayPlanHandler;
    private TaskListTabInteractor tabInteractor;

    public static PriorityHandler handler;
    private static boolean isMenuClicked = false;
    public static int taskPosition = 0;
    public static int ptpPosition = 0;
    private static String param;
    private static Scheme selectedScheme;
    private static Menu mainMenu;
    public FormAdapter formAdapter;
    public PriorityAdapter priorityAdapter;
    public PtpAdapter ptpAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Scheme> formListName;
    private String[] listTask;
    private String[] listPtp;
    private TextView textView;
    private RecyclerView recyclerView;
    private RelativeLayout layout;
    private TextView dataNotFound;
    private static boolean isPriorityOpen = false;
    private TasklistInterface iTasklist;
    private TimelineInterface iTimeline;
    private BottomNavigationView bottomNav;
    private int formPosition = 0;
    private Context context;
    private ColorStateList dialogBtnColorStateList;

    //FilterEditTextValue
    private String tenorFromValue = "";
    private String tenorToValue = "";
    private String osFromValue = "";
    private String osToValue = "";
    private String custNameValue = "";

    //plan task
    private LinearLayout addToPlanBtnCont;
    private Button addToPlanBtn;
    private SelectTaskPlanHandler selectTaskPlanHandler;
    private UnplanTaskAdapter unplanTaskAdapter;
    private HashMap<String,Integer> selectedTasks = new HashMap<>();
    private ActionMode actionMode;
    private boolean hasSelectAll;
    private Trace taskListTrace;
    private FirebaseAnalytics screenName;

    public static List<TaskH> getListTaskH() {
        return listTaskH;
    }

    public static boolean isIsMenuClicked() {
        return isMenuClicked;
    }

    public static void setIsMenuClicked(boolean isMenuClicked) {
        PriorityTabFragment.isMenuClicked = isMenuClicked;
    }

    @Override
    public String getTabPageName() {
        return TasklistView.TASKLIST_TAB_PAGE_TAG;
    }

    @Override
    public void onEnterPage() {
        if(!Global.PLAN_TASK_ENABLED)
            return;
        else{
            if (Global.ENABLE_USER_HELP) {
                if (needTaskListTabUserHelp()) {
                    showTaskListTabUserHelp();
                    return;
                }
            }
        }

        listTaskH = iTasklist.getTaskH(selectedScheme,taskPosition);
        unplanTaskAdapter.changeDataset(listTaskH);
    }

    @Override
    public void onLeavePage() {
        if(Global.PLAN_TASK_ENABLED){
            if(actionMode != null){
                actionMode.finish();
                actionMode = null;
            }
            if(selectedTasks != null && selectedTasks.size() > 0){
                selectedTasks.clear();
                selectTaskPlanHandler.clearSelections();
            }
        }
    }

    @Override
    public void onTaskChecked(TaskH taskH, int position) {
        if(selectTaskPlanHandler == null)
            return;

        selectTaskPlanHandler.selectTask(taskH);

    }

    @Override
    public void onTaskUnchecked(TaskH taskH, int position) {
        if(selectTaskPlanHandler == null)
            return;

        selectTaskPlanHandler.deselectTask(taskH);
    }

    @Override
    public void onSelectedChange(List<TaskH> newSelected, int startSequence) {
        selectedTasks.clear();//clear all
        //generate new hash map sequence
        for(int i=0; i<newSelected.size(); i++){
            selectedTasks.put(newSelected.get(i).getUuid_task_h(),i+startSequence+1);
        }

        //update selected info
        if(selectedTasks.size() > 0){
            addToPlanBtnCont.setVisibility(View.VISIBLE);
            if(selectedTasks.size() < listTaskH.size()){
                hasSelectAll = false;
            }
            else if(selectedTasks.size() == listTaskH.size()){
                hasSelectAll = true;
            }
            enableActionMode();
            if(actionMode != null){
                actionMode.setTitle(selectedTasks.size() + " of "+ listTaskH.size()+ " " + getString(R.string.selected));
                actionMode.invalidate();
            }
        }
        else {
            addToPlanBtnCont.setVisibility(View.GONE);
            hasSelectAll = false;
            if(actionMode != null){
                actionMode.setTitle(selectedTasks.size() + " of "+listTaskH.size() + " " + getString(R.string.selected));
                actionMode.invalidate();
            }
        }

        if(handler != null){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    unplanTaskAdapter.notifySelectedSequenceChanged(selectedTasks);
                }
            },200);
        }
    }

    private List<TaskPlanFilterObserver<TaskFilterParam>> filterObservers;
    @Override
    public void subscribeEvent(TaskPlanFilterObserver<TaskFilterParam> observer) {
        if(filterObservers == null){
           filterObservers = new ArrayList<>();
        }
        if(filterObservers.contains(observer)){
            return;
        }
        filterObservers.add(observer);
    }

    @Override
    public void unsubscribeEvent(TaskPlanFilterObserver<TaskFilterParam> observer) {
        if(filterObservers == null)
            return;
        filterObservers.remove(observer);
    }

    @Override
    public void emit(TaskFilterParam filterData) {
        if(filterObservers == null)
            return;
        for (TaskPlanFilterObserver<TaskFilterParam> observer:filterObservers) {
            if(observer == null)
                continue;
            observer.onFilterApplied(filterData);
        }
    }

    @java.lang.Override
    public void setSearchFilterText(int schemePos,int priorityPos) {
        taskPosition = priorityPos;
        formPosition = schemePos;
        if(formAdapter == null)
            return;

        selectedScheme = formAdapter.getItem(formPosition);
        if(selectedScheme == null)
            return;

        iTasklist.setSelectedScheme(selectedScheme);
        iTasklist.setSelectedTask(taskPosition);
        listTaskH = iTasklist.getSelectedTaskH(taskPosition);
        if(unplanTaskAdapter != null){
            unplanTaskAdapter.changeDataset(listTaskH);
        }

        textView.setText(selectedScheme.getScheme_description() + " . " + listTask[taskPosition]);
        emit(new TaskFilterParam(selectedScheme,taskPosition, ptpPosition, custNameValue, osFromValue, osToValue, tenorFromValue, tenorToValue));
    }

    public class PriorityHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            try {
                if (isPriorityOpen) {
                    iTasklist.initiateRefresh(false);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorRefresh", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorRefresh", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat initiate Refresh"));
            }
        }
    }

    @SuppressWarnings("unused")
    public static PriorityTabFragment newInstance(int columnCount) {
        PriorityTabFragment fragment = new PriorityTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, Integer.parseInt(param));
        fragment.setArguments(args);
        fragment.getView().setBackgroundColor(fragment.getResources().getColor(R.color.bgColor));
        return fragment;
    }

    private void setToolbar() {
        getActivity().findViewById(R.id.search).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle("");

        // olivia : set tampilan toolbar untuk masing" density
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_MEDIUM:
                getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(200, WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_HIGH:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(240, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(300, WRAP_CONTENT));
                }
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(370, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(470, WRAP_CONTENT));
                }
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(560, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(710, WRAP_CONTENT));
                }
                break;
            case android.util.DisplayMetrics.DENSITY_560:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(650, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(750, WRAP_CONTENT));
                }
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                if (NewMainActivity.ismnGuideEnabled) {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(750, WRAP_CONTENT));
                } else {
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(950, WRAP_CONTENT));
                }
                break;
            default:
                break;
        }
    }

    private void loadSavedTheme(){
        ThemeLoader themeLoader = new ThemeLoader(context);
        themeLoader.loadSavedColorSet(this);
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme) {
        if(dynamicTheme != null){
            applyTheme(dynamicTheme);
        }
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {

    }

    private void applyTheme(DynamicTheme dynamicTheme){
        int color = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"et_bg_normal"));
        //create color state list for button states
        int btnPressedColor = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_pressed"));
        int btnNormalColor = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_normal"));
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed},  // pressed
                new int[] {}  // normal
        };

        int[] colorlist = new int[]{
                btnPressedColor,
                btnNormalColor
        };
        dialogBtnColorStateList = new ColorStateList(states,colorlist);
    }

    public void setTabInteractor(TaskListTabInteractor tabInteractor) {
        this.tabInteractor = tabInteractor;
    }

    public void setTodayPlanHandler(TodayPlanHandler todayPlanHandler) {
        this.todayPlanHandler = todayPlanHandler;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mainMenu = menu;
        menu.findItem(R.id.menuMore).setVisible(true);
        if (checkUserHelpAvailability() || needTaskListTabUserHelp()) {
            menu.findItem(com.adins.mss.base.R.id.mnGuide).setVisible(true);
        }
        setToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION) {

            if (Global.PLAN_TASK_ENABLED) {
                UserHelp.reloadUserHelp(getActivity(), TASK_LIST_TAB_USERHELP);
                UserHelp.reloadUserHelp(getActivity(), PriorityTabFragment.class.getSimpleName());
                if (needTaskListTabUserHelp()) {
                    showTaskListTabUserHelp();
                }
            } else {
                UserHelp.reloadUserHelp(getActivity(), PriorityTabFragment.class.getSimpleName());
                showPriorityUserHelp();
            }

        }

        if (!isMenuClicked) {
            int id = item.getItemId();
            if (id == R.id.menuMore) {
                mainMenu.findItem(R.id.mnViewMap).setVisible(true);
                mainMenu.findItem(R.id.mnViewAllHeader).setVisible(true);
                isMenuClicked = false;
            }
            if (id == R.id.mnViewMap) {
                MapsViewerFragment fragment = new MapsViewerFragment();
                fragment.setTodayPlanHandler(todayPlanHandler);
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                isMenuClicked = true;
            }
            // olivia : menu View All Header sdh tidak digunakan karena hampir sama dgn Task List
            else if (id == R.id.mnViewAllHeader) {
                AllHeaderViewerFragment viewerFragment = AllHeaderViewerFragment.newInstance(AllHeaderViewerFragment.REQ_PRIORITY_LIST);
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, viewerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                isMenuClicked = true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!Global.PLAN_TASK_ENABLED)
            taskListTrace.stop();

        isPriorityOpen = false;
        bottomNav.getMenu().findItem(R.id.taskListNav).setEnabled(true);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Fire base custom trace
        if(!Global.PLAN_TASK_ENABLED){
            taskListTrace.start();
            //Set Firebase screen name
            screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_task_list), null);
        }


        isPriorityOpen = true;
        isMenuClicked = false;
        setToolbar();

        bottomNav.getMenu().findItem(R.id.taskListNav).setEnabled(false);
        if(!Global.PLAN_TASK_ENABLED){
            listTaskH = iTasklist.getSelectedTaskH(taskPosition);
            if(viewAdapter != null){
                viewAdapter.changeDataset(listTaskH);
            }
        }

        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
            try {
                setCashOnHandUI();
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorOnResume", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Set Cash UI"));
            }
        }

        try {
            NewMainActivity.setCounter();
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
        }
        DialogManager.showTimeProviderAlert(getActivity());
        if (Helper.isDevEnabled(getActivity()) && GlobalData.getSharedGlobalData().isDevEnabled()) {
            if (!GlobalData.getSharedGlobalData().isByPassDeveloper()) {
                DialogManager.showTurnOffDevMode(getActivity());
            }
        }
    }

    public static PriorityHandler getHandler() {
        return handler;
    }

    public static void setHandler(PriorityHandler handler) {
        PriorityTabFragment.handler = handler;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment fragment = this;
        iTasklist = new TasklistImpl(fragment, this);
        iTimeline = new TimelineImpl(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenName = FirebaseAnalytics.getInstance(getActivity());
        taskListTrace = FirebasePerformance.getInstance().newTrace(getString(R.string.task_list_trace));
        param = iTasklist.getParam();
        toDoList = iTasklist.getTodoList();
        listTaskH = new ArrayList<>();

        mHandler = new Handler();

        try {
            listTaskH = iTasklist.getTaskH(null, 0);
            ViewMapActivity.setListTaskH(listTaskH);
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("ErrorOnCreate", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorOnCreate", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(e);
            if (Global.IS_DEV)
                e.printStackTrace();
        }

        formListName = new ArrayList<>();
        formListName.clear();
        setAllschemeSpinner();
        formListName.addAll(SchemeDataAccess.getAllActivePriorityScheme(getActivity()));
        loadSavedTheme();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        priorityView = inflater.inflate(R.layout.new_fragment_task_list, container, false);

        bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottomNav);

        // olivia : untuk trigger dialog search ketika klik layout search di toolbar
        textView = (TextView) getActivity().findViewById(R.id.searchTask);
        textView.setText(getString(R.string.all_form_all_task));
        taskPosition = 0;
        formPosition = 0;
        getActivity().findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalData.getSharedGlobalData().getDoingTask()) {
                    searchDialog();
                }
            }
        });
        addToPlanBtnCont = priorityView.findViewById(R.id.addToPlanBtnCont);
        addToPlanBtnCont.setVisibility(View.GONE);
        addToPlanBtn = priorityView.findViewById(R.id.addToPlanBtn);
        addToPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userHelpMode)
                    return;

                addSelectedsToPlan();

            }
        });

        // Set the adapter
        if (priorityView instanceof RelativeLayout) {
            handler = new PriorityHandler();
            Context context = priorityView.getContext();
            recyclerView = (RecyclerView) priorityView.findViewById(R.id.listTask);
            layout = (RelativeLayout) priorityView.findViewById(R.id.layout);
            dataNotFound = (TextView) priorityView.findViewById(R.id.txv_data_not_found);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.getRecycledViewPool().setMaxRecycledViews(1, 500);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(viewAdapter);
            mSwipeRefreshLayout = (SwipeRefreshLayout) priorityView.findViewById(R.id.refreshTimeline);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.tv_light, getContext().getTheme()),
                        getResources().getColor(R.color.tv_normal, getContext().getTheme()),
                        getResources().getColor(R.color.tv_dark, getContext().getTheme()),
                        getResources().getColor(R.color.tv_darker, getContext().getTheme()));
            } else {
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.tv_light),
                        getResources().getColor(R.color.tv_normal),
                        getResources().getColor(R.color.tv_dark),
                        getResources().getColor(R.color.tv_darker));
            }

            mSwipeRefreshLayout
                    .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            iTasklist.setSelectedScheme(selectedScheme);
                            iTasklist.setSelectedTask(taskPosition);
                            iTasklist.initiateRefresh(true);
                        }
                    });
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                try {
                    setCashOnHandUI();
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorOnResume", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Set Cash UI"));
                    e.printStackTrace();
                }
            }
        }
        if(Global.PLAN_TASK_ENABLED){
            selectTaskPlanHandler = new SelectTaskPlanHandler(todayPlanHandler);
            selectTaskPlanHandler.setChangeListener(this);
            unplanTaskAdapter = new UnplanTaskAdapter(getActivity(), listTaskH
                    ,selectedTasks, this, this, param);
            viewAdapter = unplanTaskAdapter;
        }
        else {
            viewAdapter = new PriorityViewAdapter(getActivity(), listTaskH, PriorityTabFragment.this, param);
        }
        //show user help
        if (Global.ENABLE_USER_HELP && !Global.PLAN_TASK_ENABLED
                && Global.userHelpDummyGuide.get(PriorityTabFragment.this.getClass().getSimpleName()) != null
                && !Global.userHelpDummyGuide.get(PriorityTabFragment.this.getClass().getSimpleName()).isEmpty()) {
            showPriorityUserHelp();
        } else {
            recyclerView.setAdapter(viewAdapter);
            viewAdapter.notifyDataSetChanged();
        }
        return priorityView;
    }

    public void enableActionMode(){
        if(actionMode == null){
            actionMode = ((AppCompatActivity)getActivity()).startSupportActionMode (new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    try{
                        actionMode.getMenuInflater().inflate(R.menu.unplan_task_action_menu,menu);
                        return true;
                    }
                    catch (Exception e){
                        return false;
                    }
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    if(hasSelectAll){
                        menu.findItem(R.id.action_deselect_all).setVisible(true);
                        menu.findItem(R.id.action_select_all).setVisible(false);
                    }
                    else {
                        menu.findItem(R.id.action_deselect_all).setVisible(false);
                        menu.findItem(R.id.action_select_all).setVisible(true);
                    }
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    if(userHelpMode)
                        return false;

                    if(menuItem.getItemId() == R.id.action_select_all){
                        selectTaskPlanHandler.selectAllTask(unplanTaskAdapter.getListTaskh());
                        return true;
                    }
                    else if(menuItem.getItemId() == R.id.action_deselect_all){
                        selectTaskPlanHandler.deselectAllTask();
                        return true;
                    }
                    else if(menuItem.getItemId() == R.id.action_done){
                        PriorityTabFragment.this.actionMode.finish();
                        PriorityTabFragment.this.actionMode = null;
                        return true;
                    }
                    else {
                        return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    if(PriorityTabFragment.this.actionMode != null){
                        PriorityTabFragment.this.actionMode.finish();
                        PriorityTabFragment.this.actionMode = null;
                    }
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(Global.PLAN_TASK_ENABLED){
            if(unplanTaskAdapter != null){
                if(unplanTaskAdapter.getSelectedTasks().size() > 0){
                    addToPlanBtnCont.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    // olivia : melakukan search sesuai value di dropdown
    public void doSearch() {
        selectedScheme = formAdapter.getItem(formPosition);
        iTasklist.setSelectedScheme(selectedScheme);
        iTasklist.cancelRefreshTask ();
        iTasklist.setSelectedTask(taskPosition);
        iTasklist.setPtp(ptpPosition);
        iTasklist.setTenorFromValue(tenorFromValue);
        iTasklist.setTenorToValue(tenorToValue);
        iTasklist.setOsFromValue(osFromValue);
        iTasklist.setOsToValue(osToValue);
        iTasklist.setCustName(custNameValue);
        listTaskH = iTasklist.getSelectedTaskH(taskPosition);
        if(Global.PLAN_TASK_ENABLED){
            selectTaskPlanHandler.clearSelections();
            unplanTaskAdapter.changeDataset(listTaskH);
            emit(new TaskFilterParam(selectedScheme,taskPosition, ptpPosition, custNameValue, osFromValue, osToValue, tenorFromValue, tenorToValue)); //emit event to all observers that subscribed event
        }
        else {
            viewAdapter = new PriorityViewAdapter(getActivity(), listTaskH, PriorityTabFragment.this, param);
            recyclerView.setAdapter(viewAdapter);
            viewAdapter.notifyDataSetChanged();
        }
   }

    public void setAdapter(String[] listTask) {
        formListName = new ArrayList<>();
        formListName.clear();
        setAllschemeSpinner();
        formListName.addAll(SchemeDataAccess.getAllActivePriorityScheme(getActivity()));
        priorityAdapter = new PriorityAdapter(getActivity(), R.layout.spinner_style2, listTask);
        priorityAdapter.setDropDownViewResource(R.layout.spinner_style);
        formAdapter = new FormAdapter(getActivity(), R.layout.spinner_style2, R.id.text_spin, formListName);
        formAdapter.setDropDownViewResource(R.layout.spinner_style);
    }

    @Override
    public void onRefreshBackgroundCancelled(boolean value) {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefreshBackgroundComplete(List<TaskH> result) {
        onRefreshComplete(result);
    }

    private void setCashOnHandUI() {
        iTimeline.setCashOnHand();
    }

    private void setAllschemeSpinner() {
        Scheme schemeDummy = new Scheme();
        schemeDummy.setUuid_scheme(uuidSchemeDummy);
        schemeDummy.setScheme_description(getString(R.string.all_form));
        schemeDummy.setForm_id("schemeDummy");

        formListName.add(0, schemeDummy);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        if(Global.PLAN_TASK_ENABLED && actionMode != null){
            actionMode.finish();
            actionMode = null;
        }

        iTasklist.cancelRefreshTask();
        Utility.freeMemory();
        try {
            mainMenu.findItem(R.id.menuMore).setVisible(false);
        } catch (Exception e) {
            FireCrash.log(e);
        }
        bottomNav.getMenu().findItem(R.id.taskListNav).setEnabled(true);
        super.onDestroyView();
    }

    @Override
    public void onItemClickListener(TaskH item, int position) {

        if(Global.PLAN_TASK_ENABLED){
            Toast.makeText(context, getString(R.string.pls_add_task), Toast.LENGTH_SHORT).show();
            return;//disable click on task
        }
        //Nendi: 2019.06.28 | Prevent from swiped task
        if (Global.isLockTask() || Global.BACKPRESS_RESTRICTION) return;

        try {
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) && iTimeline.isCOHAktif() && iTimeline.getLimit() > 0 && iTimeline.getCashOnHand() >= iTimeline.getLimit()) {
                DialogManager.showAlertNotif(getActivity(), getActivity().getString(R.string.limit_coh), "Cash On Hand");
            } else {
                Scheme scheme = null;
                scheme = item.getScheme();
                if (scheme == null) {
                    if (item.getUuid_scheme() != null) {
                        scheme = SchemeDataAccess.getOne(getActivity(),
                                item.getUuid_scheme());
                        if (scheme != null)
                            item.setScheme(scheme);
                    }
                }

                if (scheme == null) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.task_cant_seen),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (!GlobalData.getSharedGlobalData().getDoingTask()) {
                            SurveyHeaderBean header = new SurveyHeaderBean(item);
                            CustomerFragment fragment = CustomerFragment.create(header);
                            if(scheme.getForm_type().equals("KTP")) {
                                CustomerFragment.setHeader(header);
                                fragment.gotoNextDynamicForm(this.getActivity());
                            } else{
                            FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorClickListener", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorClickListener", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat click item"));
            String message = e.getMessage();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemLongClickListener(final TaskH item, final int position) {

        try {
            if (item.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
                try {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                    dialogBuilder.withTitle(getActivity().getString(R.string.info_capital))
                            .withMessage(getActivity().getString(R.string.confirm_upload) + " " + item.getCustomer_name() + " ?")
                            .withButton1Text(getActivity().getString(R.string.btnYes))
                            .withButton2Text(getActivity().getString(R.string.btnCancel))
                            .setButton1Click(new View.OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    dialogBuilder.dismiss();
                                    if (Tool.isInternetconnected(getActivity())) {
                                        if (Global.isIsUploading() || Global.isIsManualUploading()) {
                                            Toast.makeText(getActivity(), getActivity().getString(R.string.upload_on_queue), Toast.LENGTH_SHORT).show();
                                        } else {
                                            try {
                                                List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(getActivity(), item.getUuid_user(), item.getUuid_task_h());
                                                TaskManager.ManualUploadImage(getActivity(), taskd);
                                                for (int i = 1; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++)
                                                    getActivity().getSupportFragmentManager().popBackStack();
                                            } catch (Exception e) {
                                                FireCrash.log(e);
                                                Toast.makeText(getActivity(), getActivity().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
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
            } else if (item.getStatus().equals(TaskHDataAccess.STATUS_SEND_PENDING)) {
                String btnText1 = getActivity().getString(R.string.btnSend);
                if (item.getIs_prepocessed() != null && item.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION))
                    btnText1 = getActivity().getString(R.string.verifyBtn);
                else if (item.getIs_prepocessed() != null && item.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL))
                    btnText1 = getActivity().getString(R.string.approveBtn);
                final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(getActivity());
                dialogBuilder.withNoTitle()
                        .withNoMessage()
                        .withButton1Text(btnText1)
                        .withButton2Text(getActivity().getString(R.string.btnDelete))
                        .setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                dialogBuilder.dismiss();
                                if (Tool.isInternetconnected(getActivity())) {
                                    if (item.getTask_id() != null) {
                                        if (item.getIs_prepocessed() != null && item.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                            if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                                                new TaskManager.ApproveTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK, false, item.getVerification_notes()).execute();
                                            else if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
                                                new TaskManager.RejectWithReSurveyTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.VERIFICATION_FLAG).execute();
                                            else
                                                new TaskManager.ForceSendTaskOnBackground(getActivity(), item.getTask_id()).execute();
                                        } else if (item.getIs_prepocessed() != null && item.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL)) {
                                            if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                                                new TaskManager.ApproveTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK, true, item.getVerification_notes()).execute();
                                            else if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
                                                new TaskManager.RejectWithReSurveyTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.APPROVAL_FLAG).execute();
                                            else
                                                new TaskManager.ApproveTaskOnBackground(getActivity(), item, Global.FLAG_FOR_APPROVALTASK, true, item.getVerification_notes()).execute();
                                        } else
                                            new TaskManager.ForceSendTaskOnBackground(getActivity(), item.getTask_id()).execute();

                                        for (int i = 1; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++)
                                            getActivity().getSupportFragmentManager().popBackStack();

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listTaskH.clear();
                                                listTaskH.addAll(iTasklist.getSelectedTaskH(0));
                                                viewAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                viewAdapter.delete(position);
                                TaskHDataAccess.deleteWithRelation(getActivity(), item);
                                if (item.getTask_id() != null)
                                    ToDoList.removeSurveyFromList(item.getTask_id());
                                dialogBuilder.dismiss();
                            }
                        }).show();
            } else if (item.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                dialogBuilder.withTitle(getActivity().getString(R.string.info_capital))
                        .withMessage(getActivity().getString(R.string.confirm_delete) + " " + item.getCustomer_name() + " ?")
                        .withButton1Text(getActivity().getString(R.string.btnYes))
                        .withButton2Text(getActivity().getString(R.string.btnCancel))
                        .setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                viewAdapter.delete(position);
                                TaskHDataAccess.deleteWithRelation(getActivity(), item);
                                ToDoList.removeSurveyFromList(item.getTask_id());
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
        } catch (Exception ex) {

        }
    }

    private void onRefreshComplete(List<TaskH> result) {
        try {
            listTaskH.clear();
            listTaskH = result;
            if(Global.PLAN_TASK_ENABLED){
                if(unplanTaskAdapter != null){
                    unplanTaskAdapter.changeDataset(listTaskH);
                    unplanTaskAdapter.notifyDataSetChanged();
                }
            }
            else {
                viewAdapter = new PriorityViewAdapter(getActivity(), listTaskH, PriorityTabFragment.this, param);
                recyclerView.setAdapter(viewAdapter);
                viewAdapter.notifyDataSetChanged();
            }
        } catch (UnsupportedOperationException e) {
            try {
                viewAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", e.getMessage());
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Notify Data Set Changed"));
            }
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                viewAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", e.getMessage());
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Notify Data Set Changed"));
            }
        }
        // Stop the refreshing indicator
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    public void searchDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.Dialog_NoTitle)
                .setView(R.layout.new_dialog_search)
                .create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.windowAnimations = R.style.DialogAnimation;
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT);

        LinearLayout transparan = dialog.findViewById(R.id.transparantLayout);
        if (transparan != null) {
            transparan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        final AppCompatSpinner searchForm = (AppCompatSpinner) dialog.findViewById(R.id.spinnerForm);
        final AppCompatSpinner searchTask = (AppCompatSpinner) dialog.findViewById(R.id.spinnerTask);
        final AppCompatSpinner searchPtp = (AppCompatSpinner) dialog.findViewById(R.id.spinnerPTP);
        Button search = (Button) dialog.findViewById(R.id.btnSearch);
        final EditText osPrincipalFrom = (EditText) dialog.findViewById(R.id.osPrincipalFrom);
        final EditText osPrincipalTo = (EditText) dialog.findViewById(R.id.osPrincipalTo);
        final EditText tenorFrom = (EditText) dialog.findViewById(R.id.tenorFrom);
        final EditText tenorTo = (EditText) dialog.findViewById(R.id.tenorTo);
        final EditText custName = (EditText) dialog.findViewById(R.id.customerNameEdtTxt);

        if(dialogBtnColorStateList != null)
            ThemeUtility.setViewBackground(search,dialogBtnColorStateList);

        listTask = getActivity().getResources().getStringArray(R.array.taskSearch);
        listPtp = getActivity().getResources().getStringArray(R.array.taskPtp);

        ptpAdapter = new PtpAdapter(getActivity(), R.layout.spinner_style2, listPtp);
        ptpAdapter.setDropDownViewResource(R.layout.spinner_style);

        priorityAdapter = new PriorityAdapter(getActivity(), R.layout.spinner_style2, listTask);
        priorityAdapter.setDropDownViewResource(R.layout.spinner_style);

        formAdapter = new FormAdapter(getActivity(), R.layout.spinner_style2, R.id.text_spin, formListName);
        formAdapter.setDropDownViewResource(R.layout.spinner_style);

        tenorFrom.setText(tenorFromValue);
        tenorTo.setText(tenorToValue);
        osPrincipalFrom.setText(osFromValue);
        osPrincipalTo.setText(osToValue);
        custName.setText(custNameValue);

        searchForm.setAdapter(formAdapter);
        searchTask.setAdapter(priorityAdapter);
        searchPtp.setAdapter(ptpAdapter);

        searchForm.setSelection(formPosition);
        searchTask.setSelection(taskPosition);
        searchPtp.setSelection(ptpPosition);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskPosition = searchTask.getSelectedItemPosition();
                formPosition = searchForm.getSelectedItemPosition();
                ptpPosition = searchPtp.getSelectedItemPosition();

                tenorFromValue = tenorFrom.getText().toString().trim();
                tenorToValue = tenorTo.getText().toString().trim();
                osFromValue = osPrincipalFrom.getText().toString().trim();
                osToValue = osPrincipalTo.getText().toString().trim();
                custNameValue = custName.getText().toString();

                if((!tenorFromValue.isEmpty() && tenorToValue.isEmpty()) || (tenorFromValue.isEmpty() && !tenorToValue.isEmpty())){
                    Toast.makeText(context, getString(R.string.all_tenor), Toast.LENGTH_SHORT).show();
                    return;
                }
                else if((!osFromValue.isEmpty() && osToValue.isEmpty()) ||(osFromValue.isEmpty() && !osToValue.isEmpty())){
                    Toast.makeText(context, getString(R.string.all_os), Toast.LENGTH_SHORT).show();
                    return;
                }

                Scheme scheme = (Scheme) searchForm.getSelectedItem();

                textView.setText(scheme.getScheme_description() + " . " + searchTask.getSelectedItem().toString());

                doSearch();
                dialog.dismiss();
            }
        });
    }

    public class FormAdapter extends ArrayAdapter<Scheme> {
        private Activity activity;
        private List<Scheme> values;

        public FormAdapter(Activity activity, int resource, int textViewResourceId, List<Scheme> objects) {
            super(activity, resource, textViewResourceId, objects);
            this.activity = activity;
            this.values = objects;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Scheme getItem(int position) {
            return values.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style2, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getScheme_description());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getScheme_description());
            return label;
        }
    }

    public class PtpAdapter extends ArrayAdapter<String>{
        private  Activity activity;
        private String[] values;

        public PtpAdapter(Activity activity, int resource, String[] objects){
            super(activity, resource, objects);
            this.activity = activity;
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
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style2, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values[position]);
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values[position]);
            return label;
        }
    }

    public class PriorityAdapter extends ArrayAdapter<String> {
        private Activity activity;
        private String[] values;

        public PriorityAdapter(Activity activity, int resource, String[] objects) {
            super(activity, resource, objects);
            this.activity = activity;
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
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style2, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values[position]);
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values[position]);
            return label;
        }
    }

    private void addSelectedsToPlan() {
        if(selectTaskPlanHandler == null)
            return;

        if(todayPlanHandler == null)
            return;

        if(actionMode != null){
            actionMode.finish();
            actionMode = null;
        }

        todayPlanHandler.addToPlan(selectTaskPlanHandler.getSelectedTask());
        unplanTaskAdapter.deleteMany(selectTaskPlanHandler.getSelectedTask());
        addToPlanBtnCont.setVisibility(View.GONE);
        selectedTasks.clear();
        selectTaskPlanHandler.clearSelections();
    }

    public static boolean needTaskListTabUserHelp() {
        List<UserHelpView> userHelpTaskListTabView = Global.userHelpGuide.get(TASK_LIST_TAB_USERHELP);
        return Global.ENABLE_USER_HELP && userHelpTaskListTabView != null && !userHelpTaskListTabView.isEmpty() ;
    }

    private boolean userHelpMode = false;
    public void showTaskListTabUserHelp(){
        userHelpMode = true;
        UnplanTaskDummyAdapter unplanTaskDummyAdapter = new UnplanTaskDummyAdapter(priorityView);
        recyclerView.setAdapter(unplanTaskDummyAdapter);
        enableActionMode();
        addToPlanBtnCont.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelpGeneralDummy userHelpGeneralDummy = new UserHelpGeneralDummy();
                userHelpGeneralDummy.showUnplanTasklistUserHelp(getActivity(), TASK_LIST_TAB_USERHELP,PriorityTabFragment.this,recyclerView,unplanTaskAdapter,null, new UserHelp.OnShowSequenceFinish() {
                    @Override
                    public void onSequenceFinish() {
                        userHelpMode = false;
                        if(actionMode != null){
                            actionMode.finish();
                            actionMode = null;
                        }
                        listTaskH = iTasklist.getTaskH(selectedScheme,taskPosition);
                        unplanTaskAdapter.changeDataset(listTaskH);
                        if(selectedTasks.size() == 0)
                            addToPlanBtnCont.setVisibility(View.GONE);
                    }
                });
            }
        },200);
    }

    private void showPriorityUserHelp(){
        PriorityDummyAdapter priorityDummyAdapter = new PriorityDummyAdapter();
        recyclerView.setAdapter(priorityDummyAdapter);
        UserHelpGeneralDummy userHelpGeneralDummy = new UserHelpGeneralDummy();
        userHelpGeneralDummy.showDummyTaskList(PriorityTabFragment.this.getActivity(), PriorityTabFragment.this.getClass().getSimpleName(), recyclerView, this, viewAdapter);
    }

    private boolean checkUserHelpAvailability() {
        List<com.adins.mss.foundation.UserHelp.Bean.UserHelpView> userHelpViews = com.adins.mss.constant.Global.userHelpGuide.get(
                com.adins.mss.base.todolist.form.PriorityTabFragment.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpViews != null;
    }
}
