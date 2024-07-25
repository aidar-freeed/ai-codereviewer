package com.adins.mss.base.todolist.form;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskHSequence;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHSequenceDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnTaskListClickListener}
 * interface.
 */
public class PriorityTabFragmentBackup extends Fragment implements OnTaskListClickListener {

    public static final String uuidSchemeDummy = "schemeDummy";
    private static final String ARG_COLUMN_COUNT = "column-count";
    //ineedthisfor coh
    private static final String TASK_OBJ_KEY_CONN = "task_object_key_connection";
    private static final String TASK_OBJ_KEY_URL = GlobalData.getSharedGlobalData().getURL_UPDATE_CASH_ON_HAND();
    private static final String TASK_OBJ_KEY_JSON = "task_object_key_json";
    private static final String TASK_OBJ_KEY_DELEGATE = "task_object_key_delegate";
    private static final String TASK_OBJ_KEY_CONTEXT = "task_object_key_context";
    private static final String TASK_OBJ_KEY_RESULT = "task_object_key_result";
    private static final String TASK_OBJ_KEY_BUILD_NUMBER = "task_object_key_build_number";
    private static final String TASK_OBJ_KEY_LOGIN_ID = "task_object_key_login_id";
    public static PriorityHandler handler;
    private static String param;
    public ToDoList toDoList;
    public List<TaskH> listTaskH;
    public List<TaskH> listTaskHSequenceJoin;
    protected Spinner spinnerSearch;
    protected Spinner spinnerForm;
    double limit = 0;
    double cashOnHand = 0;
    private TextView cashCounter;
    private int mColumnCount = 3;
    private GeneralParameter columnList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PriorityViewAdapter viewAdapter;
    private TextView priorityCounter;
    private String[] isiSearchBy;
    private FormAdapter formAdapter;
    private List<Scheme> formListName;
    private Scheme selectedScheme;
    private RefreshBackgroundTask backgroundTask;
    private RecyclerView recyclerView;
    private boolean isPriorityOpen = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PriorityTabFragmentBackup() {
        //EMPTY
    }

    @SuppressWarnings("unused")
    public static PriorityTabFragmentBackup newInstance(int columnCount) {
        PriorityTabFragmentBackup fragment = new PriorityTabFragmentBackup();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, Integer.parseInt(param));
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isCOHAktif() {
        String parameter = GeneralParameterDataAccess.getOne(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                Global.GS_CASHONHAND).getGs_value();
        return parameter != null && parameter.equals(Global.TRUE_STRING);
    }

    @Override
    public void onPause() {
        super.onPause();
        isPriorityOpen = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPriorityOpen = true;
        if (viewAdapter != null && listTaskH != null && selectedScheme != null) {
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                try {
                    setCashOnHandUI();
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorOnResume", Tool.getSystemDateTime().toLocaleString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Set Cash UI"));
                }
            }

            getSelectedTaskH();
            viewAdapter = new PriorityViewAdapter(getActivity(), listTaskH, PriorityTabFragmentBackup.this, param);
            recyclerView.setAdapter(viewAdapter);
            long counter = listTaskH.size();
            priorityCounter.setText(getString(R.string.task_count) + String.valueOf(counter));
        }
        try {
            MainMenuActivity.setDrawerCounter();
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
        }
        DialogManager.showTimeProviderAlert(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (Global.APPLICATION_SURVEY.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                param = GeneralParameterDataAccess.getOne(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_TASK_LAYOUT_MS).getGs_value();
            } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                param = GeneralParameterDataAccess.getOne(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_TASK_LAYOUT_MC).getGs_value();
            } else if (Global.APPLICATION_ORDER.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                param = GeneralParameterDataAccess.getOne(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_TASK_LAYOUT_MO).getGs_value();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            ACRA.getErrorReporter().putCustomData("ErrorPriorityTabFragment", e.getMessage());
            ACRA.getErrorReporter().handleSilentException(new Exception("Error get param Priority Tab Fragment" + e.getMessage()));
        }

        if (null == param) {
            param = "1";
        }

        toDoList = new ToDoList(getActivity().getApplicationContext());
        listTaskH = new ArrayList<>();
        try {
            listTaskH = toDoList.getListTaskInPriority(ToDoList.SEARCH_BY_ALL, "", uuidSchemeDummy);
            List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());
            List<TaskH> taskHLists = new ArrayList<>();
            TaskHSequenceDataAccess.clean(getContext());
            TaskHSequenceDataAccess.insertAllNewTaskHSeq(getContext(), listTaskH);
            taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());
            for (int i = 0; i < taskHSequences.size(); i++) {
                taskHLists.add(taskHSequences.get(i).getTaskH());
            }
            listTaskH = taskHLists;
            ViewMapActivity.setListTaskH(listTaskH);
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("ErrorOnCreate", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorOnCreate", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set List TaskH"));
            if (Global.IS_DEV)
                e.printStackTrace();
        }
        formListName = new ArrayList<>();
        formListName.clear();
        setAllschemeSpinner();
        formListName.addAll(SchemeDataAccess.getAllActivePriorityScheme(getActivity()));
        viewAdapter = new PriorityViewAdapter(getActivity(), listTaskH, this, param);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        if (param.equalsIgnoreCase("3")) {
            view = inflater.inflate(R.layout.fragment__priority_layout_list, container, false);
        } else if (param.equalsIgnoreCase("1")) {
            view = inflater.inflate(R.layout.fragment__priority_layout, container, false);
        }

        // Set the adapter
        if (view instanceof RelativeLayout) {
            handler = new PriorityHandler();
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.listPriority);
            if (param.equalsIgnoreCase("3")) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else if (param.equalsIgnoreCase("1")) {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(viewAdapter);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
            mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.tv_light),
                    getResources().getColor(R.color.tv_normal),
                    getResources().getColor(R.color.tv_dark),
                    getResources().getColor(R.color.tv_darker));
            mSwipeRefreshLayout
                    .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {

                            initiateRefresh(true);
                        }
                    });
            cashCounter = (TextView) view.findViewById(R.id.cashCounter);
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                cashCounter.setVisibility(View.VISIBLE);
                try {
                    setCashOnHandUI();
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorOnResume", Tool.getSystemDateTime().toLocaleString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Set Cash UI"));
                    e.printStackTrace();
                }
            } else {
                cashCounter.setVisibility(View.GONE);
            }
            priorityCounter = (TextView) view.findViewById(R.id.priorityCounter);
            spinnerSearch = (Spinner) view.findViewById(R.id.priorityViewBy);
            isiSearchBy = this.getResources().getStringArray(R.array.cbPriorityBy);
            PriorityAdapter priorityAdapter = new PriorityAdapter(getActivity(), R.layout.spinner_style, isiSearchBy);
            spinnerSearch.setAdapter(priorityAdapter);

            spinnerForm = (Spinner) view.findViewById(R.id.priorityViewByForm);
            formAdapter = new FormAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, formListName);
            formAdapter.setDropDownViewResource(R.layout.spinner_style);

            spinnerForm.setAdapter(formAdapter);
            spinnerForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedScheme = formAdapter.getItem(position);
                    cancelRefreshTask();
                    getSelectedTaskH();
                    viewAdapter = new PriorityViewAdapter(getActivity(), listTaskH, PriorityTabFragmentBackup.this, param);
                    recyclerView.setAdapter(viewAdapter);
                    long counter = listTaskH.size();
                    priorityCounter.setText(getString(R.string.task_count)
                            + String.valueOf(counter));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //EMPTY
                }
            });

            spinnerSearch
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View v,
                                                   int position, long id) {
                            cancelRefreshTask();
                            listTaskH = getSelectedTaskH();

                            viewAdapter = new PriorityViewAdapter(getActivity(), listTaskH, PriorityTabFragmentBackup.this, param);
                            recyclerView.setAdapter(viewAdapter);

                            long counter = listTaskH.size();
                            priorityCounter.setText(getString(R.string.task_count)
                                    + counter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                          //EMPTY
                        }
                    });
        }
        return view;
    }

    private void setCashOnHandUI() {
        String cashLimit = GlobalData.getSharedGlobalData().getUser().getCash_limit();
        limit = cashLimit != null ? Double.parseDouble(cashLimit) : 0.0;
        String coh = GlobalData.getSharedGlobalData().getUser().getCash_on_hand();
        cashOnHand = coh != null ? Double.parseDouble(coh) : 0.0;
        String sLimit = Tool.separateThousand(limit);
        String sCOH = Tool.separateThousand(cashOnHand);
        if (limit > 0 && isCOHAktif())
            cashCounter.setText(getActivity().getString(R.string.lblCashOnHand, sLimit, sCOH));
        else
            cashCounter.setText(getActivity().getString(R.string.lblCashOnHand_wo_limit, sCOH));
        cashCounter.setVisibility(View.GONE);
    }

    private List<TaskH> getSelectedTaskH() {
        listTaskH.clear();
        if (spinnerSearch.getSelectedItemPosition() == 0) {
            listTaskH.addAll(toDoList.getListTaskInPriority(ToDoList.SEARCH_BY_ALL, "", selectedScheme.getUuid_scheme()));
            if (listTaskH.isEmpty()) {
                return listTaskH;
            }
            List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());
            List<TaskH> taskHLists = new ArrayList<>();
            if (taskHSequences.isEmpty() || taskHSequences.size() != listTaskH.size()) {
                TaskHSequenceDataAccess.clean(getContext());
                TaskHSequenceDataAccess.insertAllNewTaskHSeq(getContext(), listTaskH);
                taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());
            }
            for (int i = 0; i < taskHSequences.size(); i++) {
                taskHLists.add(taskHSequences.get(i).getTaskH());
            }
            listTaskH = taskHLists;
        }


        if (spinnerSearch.getSelectedItemPosition() == 1) {
            listTaskH.addAll(toDoList.getListTaskInHighPriority(selectedScheme.getUuid_scheme()));
            if (listTaskH.isEmpty()) {
                return listTaskH;
            }
            List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());
            List<TaskH> taskHLists = new ArrayList<>();
            if (taskHSequences.isEmpty()) {
                TaskHSequenceDataAccess.clean(getContext());
                TaskHSequenceDataAccess.insertAllNewTaskHSeq(getContext(), listTaskH);
            } else {
                for (int i = 0; i < listTaskH.size(); i++) {
                    TaskHSequence taskHSequence = TaskHSequenceDataAccess.getTaskHSeqByUUIDTaskH(getContext(), listTaskH.get(i).getUuid_task_h());
                    taskHLists.add(taskHSequence.getTaskH());
                }
            }
            listTaskH = taskHLists;
        }


        if (spinnerSearch.getSelectedItemPosition() == 2) {
            listTaskH.addAll(toDoList.getListTaskInNormalPriority(selectedScheme.getUuid_scheme()));
            if (listTaskH.isEmpty()) {
                return listTaskH;
            }
            List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());
            List<TaskH> taskHLists = new ArrayList<>();
            if (taskHSequences.isEmpty()) {
                TaskHSequenceDataAccess.clean(getContext());
                TaskHSequenceDataAccess.insertAllNewTaskHSeq(getContext(), listTaskH);
            } else {
                for (int i = 0; i < listTaskH.size(); i++) {
                    TaskHSequence taskHSequence = TaskHSequenceDataAccess.getTaskHSeqByUUIDTaskH(getContext(), listTaskH.get(i).getUuid_task_h());
                    taskHLists.add(taskHSequence.getTaskH());
                }
            }
            listTaskH = taskHLists;
        }

        if (spinnerSearch.getSelectedItemPosition() == 3) {
            listTaskH.addAll(toDoList.getListTaskInLowPriority(selectedScheme.getUuid_scheme()));
            if (listTaskH.isEmpty()) {
                return listTaskH;
            }
            List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());
            List<TaskH> taskHLists = new ArrayList<>();
            if (taskHSequences.isEmpty()) {
                TaskHSequenceDataAccess.clean(getContext());
                TaskHSequenceDataAccess.insertAllNewTaskHSeq(getContext(), listTaskH);
            } else {
                for (int i = 0; i < listTaskH.size(); i++) {
                    TaskHSequence taskHSequence = TaskHSequenceDataAccess.getTaskHSeqByUUIDTaskH(getContext(), listTaskH.get(i).getUuid_task_h());
                    taskHLists.add(taskHSequence.getTaskH());
                }
            }
            listTaskH = taskHLists;
        }

        return listTaskH;
    }

    private void setAllschemeSpinner() {
        Scheme schemeDummy = new Scheme();
        schemeDummy.setUuid_scheme(uuidSchemeDummy);
        schemeDummy.setScheme_description("All");
        schemeDummy.setForm_id("schemeDummy");

        formListName.add(0, schemeDummy);
    }

    private void initiateRefresh(boolean getDataFromServer) {
        cancelRefreshTask();
        backgroundTask = new RefreshBackgroundTask(getDataFromServer);
        backgroundTask.execute();
    }

    private void cancelRefreshTask() {
        if (backgroundTask != null) {
            backgroundTask.cancel(true);
            backgroundTask = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelRefreshTask();
        Utility.freeMemory();
    }

    @Override
    public void onItemClickListener(TaskH item, int position) {
        try {
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) && isCOHAktif() && limit > 0 && cashOnHand >= limit) {
                DialogManager.showAlertNotif(getActivity(), getActivity().getString(R.string.limit_coh), "Cash On Hand");
            } else {
                Scheme scheme = null;
                scheme = item.getScheme();
                if (scheme == null && item.getUuid_scheme() != null) {
                    scheme = SchemeDataAccess.getOne(getActivity(),
                            item.getUuid_scheme());
                    if (scheme != null)
                        item.setScheme(scheme);
                }

                if (scheme == null) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.task_cant_seen),
                            Toast.LENGTH_SHORT).show();
                } else {
                    SurveyHeaderBean header = new SurveyHeaderBean(item);
                    CustomerFragment fragment = CustomerFragment.create(header);
                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorClickListener", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorClickListener", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat click item"));
            String message = getActivity().getString(R.string.task_cant_seen2);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemLongClickListener(TaskH item, int position) {
        //EMPTY
    }

    private void onRefreshComplete() {
        try {
            viewAdapter = new PriorityViewAdapter(getActivity(), listTaskH, PriorityTabFragmentBackup.this, param);
            recyclerView.setAdapter(viewAdapter);
            viewAdapter.notifyDataSetChanged();
            long counter = listTaskH.size();
            priorityCounter.setText(getString(R.string.task_count) + String.valueOf(counter));
        } catch (UnsupportedOperationException e) {
            try {
                viewAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", e.getMessage());
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Notify Data Set Changed"));
            }
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                viewAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", e.getMessage());
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Notify Data Set Changed"));
            }
        }
        // Stop the refreshing indicator
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    private String getTaskListFromServer(Context activity) {
        String errMsg = "";
        if (Tool.isInternetconnected(activity)) {
            String result;
            User user = GlobalData.getSharedGlobalData().getUser();
            MssRequestType requestType = new MssRequestType();
            requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
            requestType.addImeiAndroidIdToUnstructured();

            String json = GsonHelper.toJson(requestType);
            String url = GlobalData.getSharedGlobalData().getURL_GET_TASKLIST();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;

            //Firebase Performance Trace HTTP Request
            HttpMetric networkMetric =
                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
            Utility.metricStart(networkMetric, json);

            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                Utility.metricStop(networkMetric, serverResult);
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorRequestToServer", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorRequestToServer", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat request to server"));
                e.printStackTrace();
                errMsg = getActivity().getString(R.string.jsonParseFailed);
                return errMsg;
            }


            List<String> listUuidTaskH = new ArrayList<>();

            if (serverResult != null) {
                if (serverResult.isOK()) {
                    try {
                        result = serverResult.getResult();
                        JsonResponseTaskList taskList = GsonHelper.fromJson(result, JsonResponseTaskList.class);
                        if (taskList.getStatus().getCode() == 0) {
                            List<TaskH> listTaskH = taskList.getListTaskList();
                            if (listTaskH != null && !listTaskH.isEmpty()) {
                                String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                                TaskHDataAccess.deleteTaskHByStatus(activity, uuidUser, TaskHDataAccess.STATUS_SEND_INIT);

                                for (TaskH taskH : listTaskH) {
                                    taskH.setUser(user);
                                    taskH.setIs_verification(Global.TRUE_STRING);

                                    String uuid_scheme = taskH.getUuid_scheme();
                                    listUuidTaskH.add(taskH.getUuid_task_h());
                                    Scheme scheme = SchemeDataAccess.getOne(activity, uuid_scheme);
                                    if (scheme != null) {
                                        taskH.setScheme(scheme);
                                        TaskH h = TaskHDataAccess.getOneHeader(activity, taskH.getUuid_task_h());
                                        String uuid_timelineType = TimelineTypeDataAccess.getTimelineTypebyType(activity, Global.TIMELINE_TYPE_TASK).getUuid_timeline_type();
                                        boolean wasInTimeline = TimelineDataAccess.getOneTimelineByTaskH(activity, user.getUuid_user(), taskH.getUuid_task_h(), uuid_timelineType) != null;
                                        if (h != null && h.getStatus() != null) {
                                            if (!ToDoList.isOldTask(h)) {
                                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
                                                TaskHDataAccess.addOrReplace(activity, taskH);
                                                if (!wasInTimeline)
                                                    TimelineManager.insertTimeline(activity, taskH);
                                            } else {
                                                if (taskH.getPts_date() != null) {
                                                    h.setPts_date(taskH.getPts_date());
                                                    TaskHDataAccess.addOrReplace(activity, h);
                                                }
                                            }
                                        } else {
                                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
                                            TaskHDataAccess.addOrReplace(activity, taskH);
                                            if (!wasInTimeline)
                                                TimelineManager.insertTimeline(activity, taskH);
                                        }
                                    }
                                }
                                List<TaskH> taskHs = TaskHDataAccess.getAllTaskByStatus(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_SEND_DOWNLOAD);

                                for (TaskH h : taskHs) {
                                    String uuid_task_h = h.getUuid_task_h();
                                    boolean isSame = false;
                                    for (String uuid_from_server : listUuidTaskH) {
                                        if (uuid_task_h.equals(uuid_from_server)) {
                                            isSame = true;
                                            break;
                                        }
                                    }
                                    if (!isSame) {
                                        TaskHDataAccess.deleteWithRelation(activity, h);
                                    }
                                }
                            }
                            errMsg = "noError";
                            return errMsg;
                        } else {
                            errMsg = result;
                            return errMsg;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", e.getMessage());
                        ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", Tool.getSystemDateTime().toLocaleString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert json dari Server"));
                        errMsg = getActivity().getString(R.string.jsonParseFailed);
                        return errMsg;
                    }
                } else {
                    errMsg = serverResult.getResult();
                    return errMsg;
                }
            }
            return errMsg;
        } else {
            return errMsg;
        }
    }

    public class PriorityHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            try {
                if (isPriorityOpen) {
                    initiateRefresh(false);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorRefresh", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorRefresh", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat initiate Refresh"));
            }
        }
    }

    private class RefreshBackgroundTask extends AsyncTask<Void, Void, List<TaskH>> {
        static final int TASK_DURATION = 2 * 1000; // 2 seconds
        int pos = 0;
        String errMessage = "";
        boolean isGetFromServer;

        public RefreshBackgroundTask(boolean isGetFromServer) {
            try {
                this.isGetFromServer = isGetFromServer;
                pos = spinnerSearch.getSelectedItemPosition();
            } catch (Exception e) {
                FireCrash.log(e);

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected List<TaskH> doInBackground(Void... params) {
            // Sleep for a small amount of time to simulate a background-task
            try {
                if (isGetFromServer)
                    errMessage = getTaskListFromServer(getActivity());
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorGetErrorMessage", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetErrorMessage", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert taskList in error message"));
                e.printStackTrace();
            }
            // Return a new random list of cheeses
            return getSelectedTaskH();
        }

        @Override
        protected void onPostExecute(List<TaskH> result) {
            super.onPostExecute(result);
            if (!errMessage.isEmpty() && !errMessage.equals("noError")) {
                Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
            }
            try {
                MainMenuActivity.setDrawerCounter();
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
                ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
            }
            onRefreshComplete();
        }

    }

    public class FormAdapter extends ArrayAdapter<Scheme> {
        private Context context;
        private List<Scheme> values;

        public FormAdapter(Context context, int resource, int textViewResourceId, List<Scheme> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
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
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText("Form : " + values.get(position).getScheme_description());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getScheme_description());
            return label;
        }
    }

    public class PriorityAdapter extends ArrayAdapter<String> {
        private Context context;
        private String[] values;

        public PriorityAdapter(Context context, int resource, String[] objects) {
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
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText("Priority : " + values[position]);
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
}
