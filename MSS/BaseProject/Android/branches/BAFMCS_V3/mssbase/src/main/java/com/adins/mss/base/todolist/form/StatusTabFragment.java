package com.adins.mss.base.todolist.form;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adins.mss.base.Backup;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder_PL;
import com.adins.mss.foundation.formatter.Tool;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class StatusTabFragment extends Fragment implements OnTaskListClickListener, TasklistListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static ListHandler handler;
    private static boolean isStatusOpen = false;
    public List<TaskH> listTaskH;
    public ToDoList toDoList;
    private int mColumnCount = 3;
    private StatusViewAdapter viewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TasklistInterface iTasklist;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StatusTabFragment() {
    }

    @SuppressWarnings("unused")
    public static StatusTabFragment newInstance(int columnCount) {
        StatusTabFragment fragment = new StatusTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        isStatusOpen = true;
        try {
            if (Global.APPLICATION_ORDER.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
                getActivity().getActionBar().setTitle(getActivity().getString(R.string.title_mn_tasklist));
            } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
                if (MainMenuActivity.mnTaskList == null) {
                    getActivity().getActionBar().setTitle("Status Task");
                }
            }

            try {
                MainMenuActivity.setDrawerCounter();
            } catch (Exception e) {
                FireCrash.log(e);
            }

        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (listTaskH != null) iTasklist.initiateRefresh("status");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iTasklist = new TasklistImpl(this, this);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        toDoList = iTasklist.getTodoList();
        listTaskH = toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, "");
        handler = new ListHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        isStatusOpen = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStatusOpen = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__status_list, container, false);

        // Set the adapter
        if (view instanceof RelativeLayout) {
            Context context = view.getContext();

            View view2 = view.findViewById(R.id.actionbar);
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                view2.setVisibility(View.VISIBLE);
                RelativeLayout mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
                mainLayout.setBackgroundResource(R.drawable.bg_grayscale);
            } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                if (MainMenuActivity.mnTaskList != null)
                    view2.setVisibility(View.GONE);
                else {
                    view2.setVisibility(View.VISIBLE);
                    RelativeLayout mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
                    mainLayout.setBackgroundResource(R.drawable.bg_grayscale);
                }
            } else {
                view2.setVisibility(View.GONE);
            }

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listStatus);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            viewAdapter = new StatusViewAdapter(listTaskH, this);
            recyclerView.setAdapter(viewAdapter);

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

            // BEGIN_INCLUDE (change_colors)
            // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
            mSwipeRefreshLayout.setColorSchemeColors(
                    getResources().getColor(R.color.tv_light), getResources().getColor(R.color.tv_normal),
                    getResources().getColor(R.color.tv_dark), getResources().getColor(R.color.tv_darker));

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    iTasklist.initiateRefresh("status");
                }
            });
        }
        return view;
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
    public void onRefreshBackgroundCancelled(boolean value) {
        dismissSwiperRefresh();
    }

    @Override
    public void onRefreshBackgroundComplete(List<TaskH> result) {
        onRefreshComplete(result);
    }

    @Override
    public void onItemClickListener(TaskH item, int position) {
        try {
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
                Toast.makeText(getActivity(),
                        getActivity().getString(R.string.task_cant_seen2),
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
        } catch (Exception e) {
            FireCrash.log(e);
            Toast.makeText(
                    getActivity(),
                    getActivity().getString(R.string.scheme_gone)
                            + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemLongClickListener(final TaskH item, int position) {
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
                                        String message = null;
                                        List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(getActivity(), item.getUuid_user(), item.getUuid_task_h());

                                        if (Global.isIsUploading() || Global.isIsManualUploading()) {
                                            Toast.makeText(getActivity(), getActivity().getString(R.string.upload_on_queue), Toast.LENGTH_SHORT).show();
                                        } else {
                                            try {
                                                TaskManager.ManualUploadImage(getActivity(), taskd);
                                                for (int i = 1; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++)
                                                    getActivity().getSupportFragmentManager().popBackStack();
                                            } catch (Exception e) {
                                                FireCrash.log(e);
                                                message = getActivity().getString(R.string.request_error);
                                            }
                                            try {
                                                if(taskd.isEmpty()){
                                                    List<TaskD> taskDList = TaskDDataAccess.getAll(getActivity(), item.getUuid_task_h(), TaskDDataAccess.IMAGE_ONLY);
                                                    if(!taskDList.isEmpty()){
                                                        List<TaskD> taskDetail = new ArrayList();
                                                        taskDetail.add(taskDList.get(0));
                                                        TaskManager.ManualUploadImage(getActivity(), taskDetail);
                                                    }
                                                }else{
                                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                                }
                                            }catch (Exception e){
                                                Toast.makeText(getActivity(), getActivity().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                    }
                                    if (StatusTabFragment.handler != null)
                                        StatusTabFragment.handler.sendEmptyMessage(0);
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
                    btnText1 = "Verify";
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
                                        new TaskManager().saveAndSendTaskOnBackground(getActivity(), item.getTask_id(), false, false);

                                        for (int i = 1; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++)
                                            getActivity().getSupportFragmentManager().popBackStack();

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    MainMenuActivity.setDrawerCounter();
                                                } catch (Exception e) {
                                                    FireCrash.log(e);
                                                }

                                                listTaskH.clear();
                                                listTaskH.addAll(toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, ""));
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
                                TaskHDataAccess.deleteWithRelation(getActivity(), item);
                                if (item.getTask_id() != null)
                                    ToDoList.removeSurveyFromList(item.getTask_id());
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                                listTaskH.clear();
                                listTaskH.addAll(toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, ""));
                                viewAdapter.notifyDataSetChanged();
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        try {
                                            MainMenuActivity.setDrawerCounter();
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                        }
                                    }
                                });
                                dialogBuilder.dismiss();
                            }
                        }).show();
            } else {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                dialogBuilder.withTitle(getActivity().getString(R.string.info_capital))
                        .withMessage(getActivity().getString(R.string.confirm_delete) + " " + item.getCustomer_name() + " ?")
                        .withButton1Text(getActivity().getString(R.string.btnYes))
                        .withButton2Text(getActivity().getString(R.string.btnCancel))
                        .setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                TaskHDataAccess.deleteWithRelation(getActivity(), item);
                                ToDoList.removeSurveyFromList(item.getTask_id());
                                Backup backup = new Backup(getContext());
                                List<TaskH> taskHList = new ArrayList<>();
                                taskHList.add(item);
                                backup.removeTask(new ArrayList<TaskH>(taskHList));
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);

                                listTaskH.clear();
                                listTaskH.addAll(toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, ""));
                                viewAdapter.notifyDataSetChanged();
                                dialogBuilder.dismiss();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            MainMenuActivity.setDrawerCounter();
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                        }
                                    }
                                });
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
        //Reset List Item Collection in Adapter
        listTaskH.clear();
        listTaskH.addAll(result);
        viewAdapter.notifyDataSetChanged();

        // Stop the refreshing indicator
        dismissSwiperRefresh();
    }

    private void dismissSwiperRefresh() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        iTasklist.cancelRefreshTask();
        isStatusOpen = false;
    }

    public class ListHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (isStatusOpen) {
                try {
                    if (listTaskH != null) iTasklist.initiateRefresh("status");
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                }
            }
        }
    }

}
