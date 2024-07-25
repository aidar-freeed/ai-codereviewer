package com.adins.mss.base.todolist.form.todaysplan;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.timeline.TimelineImpl;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.AllHeaderViewerFragment;
import com.adins.mss.base.todolist.form.MapsViewerFragment;
import com.adins.mss.base.todolist.form.OnTaskListClickListener;
import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.base.todolist.form.TaskListTabInteractor;
import com.adins.mss.base.todolist.form.TasklistImpl;
import com.adins.mss.base.todolist.form.TasklistInterface;
import com.adins.mss.base.todolist.form.TasklistListener;
import com.adins.mss.base.todolist.form.TasklistView;
import com.adins.mss.base.todolist.form.helper.TaskFilterParam;
import com.adins.mss.base.todolist.form.helper.TaskPlanFilterObservable;
import com.adins.mss.base.todolist.form.helper.TaskPlanFilterObserver;
import com.adins.mss.base.todolist.form.todaysplan.dummytaskplan.DummyPlan;
import com.adins.mss.base.todolist.todayplanrepository.IPlanTaskDataSource;
import com.adins.mss.base.todolist.todayplanrepository.ResponseStartVisit;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpView;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.PlanTaskDataAccess;
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
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayPlanFragment extends Fragment implements OnTaskListClickListener
        ,TodayPlanHandler, View.OnClickListener,
        TasklistListener,
        TaskListTabInteractor.TabPage,
        TodayPlanAdapter.OnPlanDeletedListener, TodayPlanRepository.PlanTaskRepoListener
        , TaskPlanFilterObserver<TaskFilterParam> {

    //android view widget
    private RecyclerView taskListRecycler;
    private LinearLayout itemControlPanel;
    private LinearLayout confirmPlanBtnCont;
    private ConstraintLayout tasklistPlanContent;
    private ConstraintLayout noContentInfo;
    private TextView planSelectedInfo;
    private LinearLayoutManager linearLayoutManager;

    private TodayPlanAdapter todayPlanAdapter;
    private int selectedPlanIdx = -1;
    private String selectedTaskH;
    private boolean inControlMode = false;
    private boolean planInfoHideScheduled = false;
    private int totalTaskList;
    private boolean filterActive;

    private TaskListTabInteractor tabInteractor;
    private List<PlanTask> plannedTasks = new ArrayList<>();

    private Menu mainMenu;
    private TasklistInterface iTasklist;
    private NiftyDialogBuilder dialogBuilder;
    private TimelineImpl iTimeline;
    private TodayPlanRepository todayPlanRepo;

    private Handler handler;
    private TaskPlanFilterObservable<TaskFilterParam> filterObservable;
    private TaskFilterParam lastFilterParam;

    private static final String TASK_LIST_TAB_USERHELP = "PlanTaskList";

    public TodayPlanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View todayPlanView = inflater.inflate(R.layout.fragment_today_plan, container, false);
        noContentInfo = todayPlanView.findViewById(R.id.noPlanTaskContent);
        Button goToTasklistBtn = todayPlanView.findViewById(R.id.goToTasklistBtn);
        goToTasklistBtn.setOnClickListener(this);
        confirmPlanBtnCont = todayPlanView.findViewById(R.id.confirmPlanBtnCont);
        Button confirmButton = todayPlanView.findViewById(R.id.confirmPlanBtn);
        confirmButton.setOnClickListener(this);
        tasklistPlanContent = todayPlanView.findViewById(R.id.tasklistPlanContent);
        taskListRecycler = todayPlanView.findViewById(R.id.planList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        taskListRecycler.setLayoutManager(linearLayoutManager);
        setScrollListener();
        planSelectedInfo = todayPlanView.findViewById(R.id.planSelectedInfo);
        ImageView topBtn = todayPlanView.findViewById(R.id.toTopBtn);
        topBtn.setOnClickListener(this);

        ImageView botBtn = todayPlanView.findViewById(R.id.toBottomBtn);
        botBtn.setOnClickListener(this);

        ImageView downBtn = todayPlanView.findViewById(R.id.downBtn);
        downBtn.setOnClickListener(this);

        ImageView upBtn = todayPlanView.findViewById(R.id.upBtn);
        upBtn.setOnClickListener(this);

        ImageView doneMoveBtn = todayPlanView.findViewById(R.id.doneMoveBtn);
        doneMoveBtn.setOnClickListener(this);

        itemControlPanel = todayPlanView.findViewById(R.id.itemControlPanel);
        itemControlPanel.setVisibility(View.GONE);

        return todayPlanView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        iTasklist = new TasklistImpl(this, this);
        iTimeline = new TimelineImpl(getActivity());
        iTimeline.setCashOnHand();
        todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filterObservable = tabInteractor.getFilterObservable();
        if(filterObservable != null)
            filterObservable.subscribeEvent(this);
        todayPlanRepo.addListener(this);

        if (Global.ENABLE_USER_HELP && needShowTabUserHelp()) {
            showTaskPlanUserHelp();
        }
        else {
            loadPlanTasks();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mainMenu = menu;
        menu.findItem(R.id.menuMore).setVisible(true);
        if (checkUserHelpAvailability()) {
            menu.findItem(com.adins.mss.base.R.id.mnGuide).setVisible(true);
        }
        setToolbar();
    }

    private void showPlanInfo(boolean autoHide){
        planSelectedInfo.setVisibility(View.VISIBLE);
        if(!autoHide){
            planInfoHideScheduled = false;
            return;
        }

        if(planInfoHideScheduled)
            return;

        if(handler == null){
            handler = new Handler();
        }
        planInfoHideScheduled = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //check if schedule hide is canceled
                if(!planInfoHideScheduled)
                    return;

                planSelectedInfo.setVisibility(View.GONE);
                planInfoHideScheduled = false;
            }
        },2000);
    }

    private void setScrollListener(){
        taskListRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        showPlanInfo(true);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int firstVisPos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if(firstVisPos == 0){//got to top
                    //permanent visible plan info
                    showPlanInfo(false);
                }
            }
        });
    }

    private void setToolbar() {
        getActivity().findViewById(R.id.search).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);

        // olivia : set tampilan toolbar untuk masing" density
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_MEDIUM:
                getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(200, WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_HIGH:
                if(NewMainActivity.ismnGuideEnabled)
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(240, WRAP_CONTENT));
                else
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(300, WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                if(NewMainActivity.ismnGuideEnabled)
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(370, WRAP_CONTENT));
                else
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(470, WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                if(NewMainActivity.ismnGuideEnabled)
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(560, WRAP_CONTENT));
                else
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(710, WRAP_CONTENT));
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                if(NewMainActivity.ismnGuideEnabled)
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(750, WRAP_CONTENT));
                else
                    getActivity().findViewById(R.id.search).setLayoutParams(new Toolbar.LayoutParams(950, WRAP_CONTENT));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION) {
            UserHelp.reloadUserHelp(getActivity(), TodayPlanFragment.class.getSimpleName());
            if (needShowTabUserHelp()) {
                showTaskPlanUserHelp();
            }
        }
        if (!PriorityTabFragment.isIsMenuClicked()) {
            int id = item.getItemId();
            if (id == R.id.menuMore) {
                mainMenu.findItem(R.id.mnViewMap).setVisible(true);
                PriorityTabFragment.setIsMenuClicked(false);
            }

            if (id == R.id.mnViewMap) {
                MapsViewerFragment fragment = new MapsViewerFragment();
                fragment.setTodayPlanHandler(this);
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                PriorityTabFragment.setIsMenuClicked(true);
            }
            // olivia : menu View All Header sdh tidak digunakan karena hampir sama dgn Task List
            else if (id == R.id.mnViewAllHeader) {
                AllHeaderViewerFragment viewerFragment = AllHeaderViewerFragment.newInstance(AllHeaderViewerFragment.REQ_PRIORITY_LIST);
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, viewerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                PriorityTabFragment.setIsMenuClicked(true);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void setTabInteractor(TaskListTabInteractor tabInteractor) {
        this.tabInteractor = tabInteractor;
    }

    private void processItemClicked(TaskH item){
        try {
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) && iTimeline.isCOHAktif() && iTimeline.getLimit() > 0 && iTimeline.getCashOnHand() >= iTimeline.getLimit()) {
                DialogManager.showAlertNotif(getActivity(), getActivity().getString(R.string.limit_coh), "Cash On Hand");
                return;
            }

            Scheme scheme = null;
            scheme = item.getScheme();
            if (scheme == null) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.task_cant_seen),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (item.getUuid_scheme() == null){
                return;
            }

            scheme = SchemeDataAccess.getOne(getActivity(), item.getUuid_scheme());
            if (scheme == null)
                return;

            item.setScheme(scheme);
            if (Boolean.FALSE.equals(GlobalData.getSharedGlobalData().getDoingTask())) {
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
    public void onItemClickListener(TaskH item, int position) {
        if(!Global.isPlanStarted()){
            if(inControlMode){
                return;
            }
            Toast.makeText(getActivity(), getString(R.string.pls_start_visit), Toast.LENGTH_SHORT).show();
            return;
        }

        if (Global.isLockTask() || Global.BACKPRESS_RESTRICTION) return;
        processItemClicked(item);
    }

    @Override
    public void onItemLongClickListener(TaskH item, int position) {
        if(Global.isPlanStarted()){
            onItemLongClickAfterStart(item,position);
            return;
        }

        if(inControlMode)//cannot select another or same item while control mode active
            return;

        //hide confirm button
        confirmPlanBtnCont.setVisibility(View.GONE);
        //show move tools
        itemControlPanel.setVisibility(View.VISIBLE);
        selectedPlanIdx = position;
        selectedTaskH = item.getUuid_task_h();
        inControlMode = true;
    }

    private void handleLongClickUploading(final TaskH item){
        try {
            dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
            dialogBuilder.withTitle(getActivity().getString(R.string.info_capital))
                    .withMessage(getActivity().getString(R.string.confirm_upload) + " " + item.getCustomer_name() + " ?")
                    .withButton1Text(getActivity().getString(R.string.btnYes))
                    .withButton2Text(getActivity().getString(R.string.btnCancel))
                    .setButton1Click(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            dialogBuilder.dismiss();
                            if (!Tool.isInternetconnected(getActivity())){
                                Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (Global.isIsUploading() || Global.isIsManualUploading()) {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.upload_on_queue), Toast.LENGTH_SHORT).show();
                                return;
                            }

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

    private void forceSendVerifTask(TaskH item){
        if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
            new TaskManager.ApproveTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK, false, item.getVerification_notes()).execute();
        else if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
            new TaskManager.RejectWithReSurveyTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.VERIFICATION_FLAG).execute();
        else
            new TaskManager.ForceSendTaskOnBackground(getActivity(), item.getTask_id()).execute();
    }

    private void forceSendApprovalTask(TaskH item) {
        if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
            new TaskManager.ApproveTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK, true, item.getVerification_notes()).execute();
        else if (item.getFlag_survey() != null && item.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
            new TaskManager.RejectWithReSurveyTaskOnBackground(getActivity(), item, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.APPROVAL_FLAG).execute();
        else
            new TaskManager.ApproveTaskOnBackground(getActivity(), item, Global.FLAG_FOR_APPROVALTASK, true, item.getVerification_notes()).execute();
    }

    private void handleLongClickPending(final TaskH item, final int position){
        String btnText1 = null;
        if (item.getIs_prepocessed() == null){
            btnText1 = getActivity().getString(R.string.btnSend);
        }
        else if (item.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION))
            btnText1 = getActivity().getString(R.string.verifyBtn);
        else if (item.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL))
            btnText1 = getActivity().getString(R.string.approveBtn);

        final NiftyDialogBuilder_PL dialogBuilderNoTitle = NiftyDialogBuilder_PL.getInstance(getActivity());
        dialogBuilderNoTitle.withNoTitle()
                .withNoMessage()
                .withButton1Text(btnText1)
                .withButton2Text(getActivity().getString(R.string.btnDelete))
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialogBuilderNoTitle.dismiss();
                        if (!Tool.isInternetconnected(getActivity())){
                            Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (item.getTask_id() == null) {
                            return;
                        }

                        if (item.getIs_prepocessed() == null){
                            new TaskManager.ForceSendTaskOnBackground(getActivity(), item.getUuid_task_h()).execute();
                        }
                        else if (item.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                            forceSendVerifTask(item);
                        } else if (item.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL)) {
                            forceSendApprovalTask(item);
                        }

                        for (int i = 1; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++)
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                })
                .setButton2Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        todayPlanAdapter.delete(position);
                        TaskHDataAccess.deleteWithRelation(getActivity(), item);
                        if (item.getTask_id() != null)
                            ToDoList.removeSurveyFromList(item.getTask_id());
                        dialogBuilderNoTitle.dismiss();
                    }
                }).show();
    }

    private void handleLongClickDraft(final TaskH item, final int position){
        dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder.withTitle(getActivity().getString(R.string.info_capital))
                .withMessage(getActivity().getString(R.string.confirm_delete) + " " + item.getCustomer_name() + " ?")
                .withButton1Text(getActivity().getString(R.string.btnYes))
                .withButton2Text(getActivity().getString(R.string.btnCancel))
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        todayPlanAdapter.delete(position);
                        TaskHDataAccess.deleteWithRelation(getActivity(), item);
                        ToDoList.removeSurveyFromList(item.getTask_id());
                        if(Global.PLAN_TASK_ENABLED){
                            plannedTasks.remove(position);
                            planSelectedInfo.setText(getString(R.string.planned_info,todayPlanAdapter.getListTaskh().size(),totalTaskList));
                            todayPlanRepo.setHasDeletedPlanTask(true);
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

    private void processItemLongClicked(final TaskH item, final int position){
        if (item.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
            handleLongClickUploading(item);
        } else if (item.getStatus().equals(TaskHDataAccess.STATUS_SEND_PENDING)) {
            handleLongClickPending(item,position);
        } else if (item.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)) {
            handleLongClickDraft(item,position);
        }
    }

    private void onItemLongClickAfterStart(TaskH item,int position){
        try {
            processItemLongClicked(item,position);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addToPlan(List<TaskH> planTasks) {
        noContentInfo.setVisibility(View.GONE);
        tasklistPlanContent.setVisibility(View.VISIBLE);

        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        for(int i=0; i<planTasks.size(); i++){
            TaskH taskH = planTasks.get(i);
            if(taskH == null)
                continue;
            todayPlanAdapter.addQueueItem(taskH);
            PlanTask planTask = new PlanTask();
            planTask.setUuid_plan_task(Tool.getUUID());
            planTask.setUuid_user(uuidUser);
            planTask.setPlan_crt_date(new Date());
            planTask.setUuid_task_h(taskH.getUuid_task_h());
            plannedTasks.add(planTask);

            if(Global.isPlanStarted()){
                if(i == 0 && Global.getCurrentPlanTask() == null){
                    planTask.setPlan_status(PlanTaskDataAccess.STATUS_STARTED);
                    Global.setCurrentPlanTask(planTask.getUuid_task_h());
                }
                else {
                    planTask.setPlan_status(PlanTaskDataAccess.STATUS_PLANNED);
                }
                planTask.setSequence(todayPlanRepo.getLastPlanSequenceNo() + i + 1);
                planTask.setPlan_start_date(new Date());
            }
            else {
                planTask.setSequence(todayPlanRepo.getLastPlansCount() + i + 1);
                planTask.setPlan_status(PlanTaskDataAccess.STATUS_DRAFTED);
            }
            planTask.setView_sequence(planTask.getSequence());
        }
        processAddPlan();
    }

    private void processAddPlan(){
        planSelectedInfo.setText(getString(R.string.planned_info,todayPlanAdapter.getItemCount(),totalTaskList));
        PlanTaskDataAccess.addUpdatePlans(getActivity(),plannedTasks);
        if(handler == null){
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                todayPlanRepo.updateLastPlanSequenceNo();//update last seq no plans
                todayPlanRepo.updateLastPlansCount();//update last plans count
            }
        },500);

        if(!Global.isPlanStarted() && !inControlMode){
            confirmPlanBtnCont.setVisibility(View.VISIBLE);
        }
        else {//if plan has been started, call start visit again
            startVisit();
        }
        Toast.makeText(getActivity(), getActivity().getString(R.string.added_todays_plan), Toast.LENGTH_SHORT).show();
        //reset filter
        if(filterObservable != null){
            filterActive = false;
            filterObservable.setSearchFilterText(0,0);
        }
    }

    @Override
    public List<PlanTask> getCurrentPlans() {
        return plannedTasks;
    }

    @Override
    public int getAllPlansCount() {
        int planCount = 0;
        if(Global.isPlanStarted()){
            planCount = todayPlanRepo.getLastPlanSequenceNo();
        }
        else {
            planCount = todayPlanRepo.getLastPlansCount();
        }
        return planCount;
    }

    //task item move navigation
    private void moveTop(){
        if(selectedPlanIdx == -1)
            return;
        selectedPlanIdx = todayPlanAdapter.moveItemToTop(selectedPlanIdx);
        linearLayoutManager.scrollToPositionWithOffset(selectedPlanIdx,1);
    }

    private void moveBottom(){
        if(selectedPlanIdx == -1)
            return;
        selectedPlanIdx = todayPlanAdapter.moveItemToBottom(selectedPlanIdx);
        linearLayoutManager.scrollToPositionWithOffset(selectedPlanIdx,1);
    }

    private void moveUp(){
        if(selectedPlanIdx == -1)
            return;
        int topIdx = selectedPlanIdx - 1;
        selectedPlanIdx = todayPlanAdapter.moveItemFromTo(selectedPlanIdx,topIdx);
        linearLayoutManager.scrollToPositionWithOffset(selectedPlanIdx,1);
    }

    private void moveDown(){
        if(selectedPlanIdx == -1)
            return;
        int downIdx = selectedPlanIdx + 1;
        selectedPlanIdx = todayPlanAdapter.moveItemFromTo(selectedPlanIdx,downIdx);
        linearLayoutManager.scrollToPositionWithOffset(selectedPlanIdx,1);
    }

    private void doneMoveControl(){
        inControlMode = false;
        todayPlanAdapter.deselectItem(selectedTaskH,selectedPlanIdx);

        //save new sequence state
        for(int i=0; i<todayPlanAdapter.getListTaskh().size(); i++){
            TaskH taskH = todayPlanAdapter.getListTaskh().get(i);
            PlanTask planTask = getPlanTaskByTaskH(taskH.getUuid_task_h());
            if(planTask == null)
                continue;
            planTask.setSequence(i+1);
            planTask.setView_sequence(planTask.getSequence());
        }
        PlanTaskDataAccess.addUpdatePlans(getActivity(),plannedTasks);

        selectedPlanIdx = -1;
        selectedTaskH = null;
        itemControlPanel.setVisibility(View.GONE);
        confirmPlanBtnCont.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.upBtn){
            moveUp();
        }
        else if(v.getId() == R.id.downBtn){
            moveDown();
        }
        else if(v.getId() == R.id.toBottomBtn){
            moveBottom();
        }
        else if(v.getId() == R.id.toTopBtn){
            moveTop();
        }
        else if(v.getId() == R.id.doneMoveBtn){
            doneMoveControl();
        }
        else if(v.getId() == R.id.goToTasklistBtn){
            if(tabInteractor != null){
                tabInteractor.goToTab(0);
            }
        }
        else if(v.getId() == R.id.confirmPlanBtn){
            if(userHelpMode)
                return;

            startVisit();
        }
    }

    private PlanTask getPlanTaskByTaskH(String uuidTaskh){
        PlanTask result = null;
        for(PlanTask planTask : plannedTasks){
            if(planTask.getUuid_task_h().equals(uuidTaskh)){
                result = planTask;
                break;
            }
        }
        return result;
    }

    private void loadPlanTasks(){
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        //get total tasklist for user
        totalTaskList = TaskHDataAccess.getCountAll(getActivity(),uuidUser);
        //get cached from repo, because plan data has been loaded before when first enter after sync
        todayPlanRepo.loadPlans();
    }

    private void startVisit(){
        if(plannedTasks.isEmpty())
            return;

        if(!Global.isPlanStarted()){
            //set new sequence position
            for(int i=0; i<todayPlanAdapter.getListTaskh().size(); i++){
                TaskH taskH = todayPlanAdapter.getListTaskh().get(i);
                PlanTask planTask = getPlanTaskByTaskH(taskH.getUuid_task_h());
                if(planTask == null)
                    continue;

                if(i == 0){//first plan set to true
                    planTask.setPlan_status(PlanTaskDataAccess.STATUS_STARTED);
                }
                else{
                    planTask.setPlan_status(PlanTaskDataAccess.STATUS_PLANNED);
                }
                planTask.setPlan_start_date(new Date());
                planTask.setSequence(i+1);
                planTask.setView_sequence(planTask.getSequence());
            }
        }

        todayPlanRepo.startVisit(plannedTasks, new IPlanTaskDataSource.Result<ResponseStartVisit>() {
            @Override
            public void onResult(ResponseStartVisit result) {
                onStartVisitSuccess(result);
            }

            @Override
            public void onError(String error) {
                onStartVisitError(error);
            }
        });
    }

    private void onStartVisitSuccess(ResponseStartVisit result){
        if(result != null){//sukses
            todayPlanRepo.setStartVisit(true);//has started visit online
            todayPlanRepo.setNeedSync(false);//no need to sync
            confirmPlanBtnCont.setVisibility(View.GONE);
            //show dialog
            if (!Global.isPlanStarted()) {
                if (dialogBuilder == null) {
                    dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                }
                dialogBuilder.withTitle(getString(R.string.success)).withMessage(getString(R.string.plan_started)).show();
            }

            //reset filter
            if(filterActive && filterObservable != null){
                filterActive = false;
                filterObservable.setSearchFilterText(0,0);
            }
            //update plan on local db
            todayPlanRepo.updatePlan(plannedTasks);
            Global.setPlanStarted(true);
        }
    }

    private void onStartVisitError(String error){
        if(GlobalData.isRequireRelogin()){
            DialogManager.showForceExitAlert(getActivity(),getString(R.string.msgLogout));
            return;
        }
        if(error.equals("Offline")){//offline mode
            confirmPlanBtnCont.setVisibility(View.GONE);

            if(!todayPlanRepo.isStartVisit())//if has not been start visit online before, set to false
                todayPlanRepo.setStartVisit(false);

            todayPlanRepo.setNeedSync(true);//need sync when online

            //show dialog
            if (!Global.isPlanStarted()) {
                if (dialogBuilder == null) {
                    dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                }
                dialogBuilder.withTitle(getString(R.string.success)).withMessage(getString(R.string.plan_started)).show();
            }

            //update plan on local db
            todayPlanRepo.updatePlan(plannedTasks);
            Global.setPlanStarted(true);
            return;
        }
        //error
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefreshBackgroundCancelled(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onRefreshBackgroundComplete(List<TaskH> result) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getTabPageName() {
        return TasklistView.TODAYSPLAN_TAB_PAGE_TAG;
    }

    @Override
    public void onEnterPage() {
        if(Global.PLAN_TASK_ENABLED){
            if(plannedTasks.isEmpty()) {
                confirmPlanBtnCont.setVisibility(View.GONE);
                tasklistPlanContent.setVisibility(View.GONE);
                noContentInfo.setVisibility(View.VISIBLE);
            }
            if(Global.ENABLE_USER_HELP && needShowTabUserHelp()) {
                showTaskPlanUserHelp();
            }
        }
    }

    @Override
    public void onLeavePage() {
        if(selectedTaskH == null || selectedTaskH.equals(""))
            return;
        if(selectedPlanIdx == -1)
            return;
        //remove control before leave today plan page
        doneMoveControl();
    }

    @Override
    public void onDestroyView() {
        onLeavePage();
        //unsubscribe filter event when view destroyed
        if(filterObservable != null){
            if(filterActive){
                filterActive = false;
                filterObservable.setSearchFilterText(0,0);
            }
            //unsubscribe observer
            filterObservable.unsubscribeEvent(this);
        }

        todayPlanRepo.removeListener(this);
        super.onDestroyView();
    }

    @Override
    public void onPlanDeleted(final TaskH taskH, final int position) {
        dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder.withTitle(getActivity().getString(R.string.info_capital))
                .withMessage(getActivity().getString(R.string.confirm_delete) + " " + taskH.getCustomer_name() + " " + getString(R.string.from_todays_plan))
                .withButton1Text(getActivity().getString(R.string.btnYes))
                .withButton2Text(getActivity().getString(R.string.btnCancel))
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        PlanTask removedPlan = plannedTasks.remove(position);
                        if(removedPlan == null)
                            return;

                        PlanTaskDataAccess.removePlan(getActivity(),removedPlan);
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                todayPlanRepo.updateLastPlansCount();//update only last count of plans because delete only happen before start visit
                            }
                        },300);

                        todayPlanAdapter.delete(position);
                        if(plannedTasks.isEmpty()){
                            confirmPlanBtnCont.setVisibility(View.GONE);
                            tasklistPlanContent.setVisibility(View.GONE);
                            noContentInfo.setVisibility(View.VISIBLE);
                        }
                        planSelectedInfo.setText(getString(R.string.planned_info,plannedTasks.size(),totalTaskList));
                        Toast.makeText(getActivity(), getActivity().getString(R.string.successfully_removed_task, taskH.getCustomer_name()), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRepoChange(List<PlanTask> plans) {
        if(UserHelp.isActive || inControlMode)
            return;

        List<TaskH> taskhDataset = new ArrayList<>();
        plannedTasks = plans;//update plan dataset from repo

        for(PlanTask planTask : plannedTasks){
            if(planTask != null){
                TaskH taskH = TaskHDataAccess.getOneUnsentTaskHeader(getActivity(),planTask.getUuid_task_h());
                if(taskH == null)
                    continue;
                taskhDataset.add(taskH);
            }
        }

        if(filterActive){
            filterPlan(lastFilterParam);
        }
        else if(todayPlanAdapter == null) {
            todayPlanAdapter = new TodayPlanAdapter(getActivity()
                    ,taskhDataset
                    ,this,this,"");
        }
        else{
            todayPlanAdapter.changeDataset(taskhDataset);
        }
        linearLayoutManager = new LinearLayoutManager(getActivity());
        taskListRecycler.setLayoutManager(linearLayoutManager);
        taskListRecycler.setAdapter(todayPlanAdapter);

        planSelectedInfo.setText(getString(R.string.planned_info,plannedTasks.size(),totalTaskList));
        showPlanInfo(false);
        if(Global.isPlanStarted()){
            confirmPlanBtnCont.setVisibility(View.GONE);
        }
        else {
            confirmPlanBtnCont.setVisibility(View.VISIBLE);
        }

        if(taskhDataset.isEmpty()){
            tasklistPlanContent.setVisibility(View.GONE);
            noContentInfo.setVisibility(View.VISIBLE);
            confirmPlanBtnCont.setVisibility(View.GONE);
            todayPlanRepo.setStartVisit(false);
        }
    }

    @Override
    public void onError(String errMsg) {
        throw new UnsupportedOperationException();
    }

    private boolean userHelpMode = false;
    public static boolean needShowTabUserHelp() {
        List<UserHelpView> userHelpViews = Global.userHelpGuide.get(TodayPlanFragment.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpViews != null && !userHelpViews.isEmpty();
    }

    private UserHelp.OnSequenceShowed userHelpShowCallback = new UserHelp.OnSequenceShowed() {
        @Override
        public void onSequenceShowed(String prevUserHelp, String currentShowUserHelp, int index) {
            if(currentShowUserHelp.equals("slidingTabLayout")){
                tasklistPlanContent.setVisibility(View.GONE);
                confirmPlanBtnCont.setVisibility(View.GONE);
            }
            else if(currentShowUserHelp.equals("goToTasklistBtn")){
                tasklistPlanContent.setVisibility(View.VISIBLE);
                confirmPlanBtnCont.setVisibility(View.VISIBLE);
                noContentInfo.setVisibility(View.VISIBLE);
                List<DummyPlan> dummyPlans = TodayPlanDummyAdapter.createDefaultDummyDataset(1,true);
                planDummyAdapter.setDummyDataset(dummyPlans);
            }
            else if(currentShowUserHelp.equals("dummyDeletePlanBtn")){
                noContentInfo.setVisibility(View.GONE);
            }
            else if(currentShowUserHelp.equals("confirmPlanBtn")){
                //hide delete button on dummy plan item
                List<DummyPlan> dummyPlans = TodayPlanDummyAdapter.createDefaultDummyDataset(2,false);
                planDummyAdapter.setDummyDataset(dummyPlans);
                planSelectedInfo.setVisibility(View.GONE);
            }
        }
    };

    private UserHelp.OnShowSequenceFinish userHelpFinishCallback = new UserHelp.OnShowSequenceFinish() {
        @Override
        public void onSequenceFinish() {
            userHelpMode = false;
            tasklistPlanContent.setVisibility(View.VISIBLE);
            noContentInfo.setVisibility(View.GONE);
            confirmPlanBtnCont.setVisibility(View.GONE);
            loadPlanTasks();//load plan tasks
        }
    };

    TodayPlanDummyAdapter planDummyAdapter;
    public void showTaskPlanUserHelp(){
        userHelpMode = true;
        //create dummy adapter
        List<DummyPlan> dummyPlans = TodayPlanDummyAdapter.createDefaultDummyDataset(2,false);
        planDummyAdapter = new TodayPlanDummyAdapter(getActivity(),dummyPlans);
        taskListRecycler.setAdapter(planDummyAdapter);
        tasklistPlanContent.setVisibility(View.VISIBLE);
        confirmPlanBtnCont.setVisibility(View.VISIBLE);
        noContentInfo.setVisibility(View.VISIBLE);
        planSelectedInfo.setVisibility(View.GONE);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //start user help sequences
                UserHelp.showAllUserHelp(getActivity(), TodayPlanFragment.class.getSimpleName(),taskListRecycler,userHelpShowCallback,userHelpFinishCallback);
            }
        },200);
    }

    @Override
    public void onFilterApplied(TaskFilterParam filterData) {
        if(filterData == null)
            return;

        filterActive = true;
        //hide selected plan info
        planSelectedInfo.setVisibility(View.GONE);
        lastFilterParam = filterData;
        iTasklist.setSelectedScheme(filterData.getScheme());
        iTasklist.setSelectedTask(filterData.getSearchType());
        filterPlan(filterData);
    }

    private void filterPlan(TaskFilterParam filterParam){
        //check if filter is reset
        if(filterParam.getScheme().getUuid_scheme().equals(PriorityTabFragment.uuidSchemeDummy)
        && filterParam.getSearchType() <= 0 && filterParam.getCustomerName().isEmpty()
        && filterParam.getPtp() <= 0 && filterParam.getOsFrom().isEmpty()
        && filterParam.getTenorFrom().isEmpty()){//if reset, load plan as default
            filterActive = false;
            planSelectedInfo.setVisibility(View.VISIBLE);
            todayPlanRepo.loadPlans();
            return;
        }

        List<TaskH> filteredPlan;
        filteredPlan = TaskHDataAccess.getTaskhPlanFilter(
                getActivity().getApplicationContext()
                ,filterParam.getScheme().getUuid_scheme()
                ,filterParam.getSearchType(), filterParam.getPtp()
                , filterParam.getTenorFrom(), filterParam.getTenorTo()
                , filterParam.getOsFrom(), filterParam.getOsTo()
                , filterParam.getCustomerName());

        List<PlanTask> todayPlan = PlanTaskDataAccess.getAllPlan(getActivity().getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
        for (PlanTask planTask : todayPlan){
            String planTaskH = planTask.getUuid_task_h();
        }

        if(todayPlanAdapter != null){//update adapter
            todayPlanAdapter.changeDataset(filteredPlan);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            taskListRecycler.setLayoutManager(linearLayoutManager);
            taskListRecycler.setAdapter(todayPlanAdapter);
        }
    }

    private boolean checkUserHelpAvailability() {
        List<UserHelpView> userHelpViews = Global.userHelpGuide.get(TodayPlanFragment.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpViews != null;
    }
}
