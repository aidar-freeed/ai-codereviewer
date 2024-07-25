package com.adins.mss.base.todolist.form;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.dynamicform.newlead.NewLeadFragment;
import com.adins.mss.base.timeline.TimelineImpl;
import com.adins.mss.base.timeline.TimelineInterface;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder_PL;
import com.adins.mss.foundation.formatter.Tool;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by muhammad.aap on 11/12/2018.
 */

public class NewToDoListTabFragment extends Fragment implements OnTaskListClickListener, TasklistListener {
    public static final String uuidSchemeDummy = "schemeDummy";
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static ToDoList toDoList;
    private List<TaskH> listTaskH;
    public static Handler mHandler;
    private static String param;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public static NewToDoListViewAdapter viewAdapter;
    public NewToDoListTabFragment.FormAdapter formAdapter;
    public NewToDoListTabFragment.NewToDoListAdapter newToDoListAdapter;
    private List<Scheme> formListName;
    private static Scheme selectedScheme;
    private TextView textView;
    private RecyclerView recyclerView;
    private RelativeLayout layout;
    private TextView dataNotFound;
    private boolean isNewToDoListOpen = false;
   public static NewToDoListTabFragment.NewToDoListHandler handler;
    private TasklistInterface iTasklist;
    private TimelineInterface iTimeline;

    private static Menu mainMenu;
    public static boolean isMenuClicked = false;

    private BottomNavigationView bottomNav;

    private int formPosition = 0;
    public static int taskPosition = 0;

    @SuppressWarnings("unused")
    public static NewToDoListTabFragment newInstance(int columnCount) {
        NewToDoListTabFragment fragment = new NewToDoListTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, Integer.parseInt(param));
        fragment.setArguments(args);
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
                getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(200, Toolbar.LayoutParams.WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_HIGH:
                getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(300, Toolbar.LayoutParams.WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(470, Toolbar.LayoutParams.WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(710, Toolbar.LayoutParams.WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(950, Toolbar.LayoutParams.WRAP_CONTENT));
                break;
            default:
                break;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mainMenu = menu;
        menu.findItem(R.id.menuMore).setVisible(true);
        setToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(isMenuClicked == false){
            int id = item.getItemId();
            if (id == R.id.menuMore) {
                mainMenu.findItem(R.id.mnViewMap).setVisible(true);
                isMenuClicked=false;
            }
            if (id == R.id.mnViewMap) {
                MapsViewerFragment fragment = new MapsViewerFragment();
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                isMenuClicked=true;
            }
            // olivia : menu View All Header sdh tidak digunakan karena hampir sama dgn Task List
            else if (id == R.id.mnViewAllHeader) {
                AllHeaderViewerFragment viewerFragment = AllHeaderViewerFragment.newInstance(AllHeaderViewerFragment.REQ_PRIORITY_LIST);
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, viewerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                isMenuClicked=true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        isNewToDoListOpen = false;
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
        isNewToDoListOpen = true;
        isMenuClicked = false;
        setToolbar();

        bottomNav.getMenu().findItem(R.id.taskListNav).setEnabled(false);

        listTaskH = iTasklist.getSelectedTaskH(taskPosition);
        viewAdapter = new NewToDoListViewAdapter(getActivity(), listTaskH, NewToDoListTabFragment.this, param);
        recyclerView.setAdapter(viewAdapter);
        viewAdapter.notifyDataSetChanged();
        initBackground(listTaskH);

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
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
        }
        DialogManager.showTimeProviderAlert(getActivity());
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

        param = iTasklist.getParam();
        toDoList = iTasklist.getTodoList();
        listTaskH = new ArrayList<>();

        mHandler = new Handler();

        try {
            listTaskH = iTasklist.getTaskH(null, 0);
            ViewMapActivity.setListTaskH(listTaskH);
        } catch (Exception e) {
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
        viewAdapter = new NewToDoListViewAdapter(getActivity(), listTaskH, this, param);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_task_list, container, false);

        bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottomNav);

        // olivia : untuk trigger dialog search ketika klik layout search di toolbar
        textView = (TextView) getActivity().findViewById(R.id.searchTask);
        textView.setText(getString(R.string.all_form_all_task));
        taskPosition = 0;
        formPosition = 0;
        getActivity().findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog();
            }
        });

        // Set the adapter
        if (view instanceof RelativeLayout) {
            handler = new NewToDoListTabFragment.NewToDoListHandler();
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.listTask);
            layout = (RelativeLayout) view.findViewById(R.id.layout);
            dataNotFound = (TextView) view.findViewById(R.id.txv_data_not_found);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.getRecycledViewPool().setMaxRecycledViews(1, 500);
            recyclerView.setHasFixedSize(true);

            initBackground(listTaskH);
           recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(viewAdapter);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshTimeline);
            mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.tv_light),
                    getResources().getColor(R.color.tv_normal),
                    getResources().getColor(R.color.tv_dark),
                    getResources().getColor(R.color.tv_darker));
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
                    ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorOnResume", Tool.getSystemDateTime().toLocaleString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Set Cash UI"));
                    e.printStackTrace();
                }
            }
        }
        return view;
    }

    public void initBackground(List<TaskH> taskHList) {
        if (taskHList.size() == 0) {
            layout.setBackgroundResource(R.drawable.bg_notfound);
        } else {
            layout.setBackgroundResource(R.color.bgColor);
        }
    }

    // olivia : melakukan search sesuai value di dropdown
    public void doSearch() {
        selectedScheme = formAdapter.getItem(formPosition);
        iTasklist.setSelectedScheme(selectedScheme);
        iTasklist.cancelRefreshTask();
        iTasklist.setSelectedTask(taskPosition);
        listTaskH = iTasklist.getSelectedTaskH(taskPosition);

        viewAdapter = new NewToDoListViewAdapter(getActivity(), listTaskH, NewToDoListTabFragment.this, param);
        recyclerView.setAdapter(viewAdapter);
        viewAdapter.notifyDataSetChanged();
        initBackground(listTaskH);
    }

    public void setAdapter(String[] listTask) {
        formListName = new ArrayList<>();
        formListName.clear();
        setAllschemeSpinner();
        formListName.addAll(SchemeDataAccess.getAllActivePriorityScheme(getActivity()));
        newToDoListAdapter = new NewToDoListTabFragment.NewToDoListAdapter(getActivity(), R.layout.spinner_style2, listTask);
        newToDoListAdapter.setDropDownViewResource(R.layout.spinner_style);
        formAdapter = new NewToDoListTabFragment.FormAdapter(getActivity(), R.layout.spinner_style2, R.id.text_spin, formListName);
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
        initBackground(listTaskH);
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
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        iTasklist.cancelRefreshTask();
        Utility.freeMemory();
        try {
            mainMenu.findItem(R.id.menuMore).setVisible(false);
        } catch (Exception e) {
        }
        bottomNav.getMenu().findItem(R.id.taskListNav).setEnabled(true);
    }

    @Override
    public void onItemClickListener(TaskH item, int position) {
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
                        Fragment fragment = null;
                        if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_ORDER))
                            fragment = NewLeadFragment.create(header);
                        else
                            fragment = CustomerFragment.create(header);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                        transaction.replace(R.id.content_frame, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }
        } catch (Exception e) {
            ACRA.getErrorReporter().putCustomData("errorClickListener", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorClickListener", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat click item"));
            String message = e.getMessage();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemLongClickListener(final TaskH item, int position) {
        try {
            if(item.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)){
                try {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                    dialogBuilder.withTitle(getActivity().getString(R.string.info_capital))
                            .withMessage(getActivity().getString(R.string.confirm_upload)+" "+item.getCustomer_name()+" ?")
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
                    e.printStackTrace();
                }
            }else if(item.getStatus().equals(TaskHDataAccess.STATUS_SEND_PENDING)){
                String btnText1 = getActivity().getString(R.string.btnSend);
                if(item.getIs_prepocessed()!= null && item.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION))
                    btnText1 = getActivity().getString(R.string.verifyBtn);
                else if(item.getIs_prepocessed()!= null && item.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL))
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
                                    if(item.getTask_id()!=null) {
                                        if(item.getIs_prepocessed() != null && item.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                            if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                                                new TaskManager().sendApprovalTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK, false, item.getVerification_notes());
                                            else if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
                                                new TaskManager().sendRejectedWithReSurveyTask(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.VERIFICATION_FLAG);
                                            else
                                                new TaskManager.ForceSendTaskOnBackground(getActivity(), item.getTask_id()).execute();
                                        } else if(item.getIs_prepocessed() != null && item.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL)) {
                                            if (item.getFlag_survey() != null &&  item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                                                new TaskManager().sendApprovalTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK, true, item.getVerification_notes());
                                            else if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
                                                new TaskManager().sendRejectedWithReSurveyTask(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.APPROVAL_FLAG);
                                            else
                                                new TaskManager().sendApprovalTaskOnBackground(getActivity(), item, Global.FLAG_FOR_APPROVALTASK, true, item.getVerification_notes());
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
                                } else{
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                TaskHDataAccess.deleteWithRelation(getActivity(), item);
                                if(item.getTask_id()!=null)
                                    ToDoList.removeSurveyFromList(item.getTask_id());
                                iTasklist.setSelectedScheme(selectedScheme);
                                iTasklist.setSelectedTask(taskPosition);
                                iTasklist.initiateRefresh(true);
                                dialogBuilder.dismiss();
                            }
                        }).show();
            }else if (item.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)){
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                dialogBuilder.withTitle(getActivity().getString(R.string.info_capital))
                        .withMessage(getActivity().getString(R.string.confirm_delete)+" "+item.getCustomer_name()+" ?")
                        .withButton1Text(getActivity().getString(R.string.btnYes))
                        .withButton2Text(getActivity().getString(R.string.btnCancel))
                        .setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                TaskHDataAccess.deleteWithRelation(getActivity(), item);
                                ToDoList.removeSurveyFromList(item.getTask_id());
                                iTasklist.setSelectedScheme(selectedScheme);
                                iTasklist.setSelectedTask(taskPosition);
                                iTasklist.initiateRefresh(true);
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
            FireCrash.log(ex);
        }
    }

    private void onRefreshComplete(List<TaskH> result) {
        try {
            listTaskH.clear();
            listTaskH   = result;
            viewAdapter = new NewToDoListViewAdapter(getActivity(), listTaskH, NewToDoListTabFragment.this, param);
            recyclerView.setAdapter(viewAdapter);

            viewAdapter.notifyDataSetChanged();
        } catch (UnsupportedOperationException e) {
            try {
                viewAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", e.getMessage());
                ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Notify Data Set Changed"));
            }
        } catch (Exception e) {
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

    public void searchDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.Dialog_NoTitle)
                .setView(R.layout.new_dialog_search)
                .create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        wmlp.windowAnimations = R.style.DialogAnimation;
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final AppCompatSpinner searchForm = (AppCompatSpinner) dialog.findViewById(R.id.spinnerForm);
        final AppCompatSpinner searchTask = (AppCompatSpinner) dialog.findViewById(R.id.spinnerTask);
        Button search = (Button) dialog.findViewById(R.id.btnSearch);

        String[] listTask = getActivity().getResources().getStringArray(R.array.taskSearch);

        newToDoListAdapter = new NewToDoListTabFragment.NewToDoListAdapter(getActivity(), R.layout.spinner_style2, listTask);
        newToDoListAdapter.setDropDownViewResource(R.layout.spinner_style);
        formAdapter = new NewToDoListTabFragment.FormAdapter(getActivity(), R.layout.spinner_style2, R.id.text_spin, formListName);
        formAdapter.setDropDownViewResource(R.layout.spinner_style);

        searchForm.setAdapter(formAdapter);
        searchTask.setAdapter(newToDoListAdapter);

        searchForm.setSelection(formPosition);
        searchTask.setSelection(taskPosition);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskPosition = searchTask.getSelectedItemPosition();
                formPosition = searchForm.getSelectedItemPosition();

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

        public int getCount() {
            return values.size();
        }

        public Scheme getItem(int position) {
            return values.get(position);
        }

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

    public class NewToDoListAdapter extends ArrayAdapter<String> {
        private Activity activity;
        private String[] values;

        public NewToDoListAdapter(Activity activity, int resource, String[] objects) {
            super(activity, resource, objects);
            this.activity = activity;
            this.values = objects;
        }

        public int getCount() {
            return values.length;
        }

        public String getItem(int position) {
            return values[position];
        }

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

    public static NewToDoListTabFragment.NewToDoListHandler getHandler() {
        return handler;
    }

    public static void setHandler(NewToDoListTabFragment.NewToDoListHandler handler) {
        NewToDoListTabFragment.handler = handler;
    }

    public class NewToDoListHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            try {
                if (isNewToDoListOpen) {
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
}
