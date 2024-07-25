package com.adins.mss.base.todolist.form;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.acra.ACRA;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class StatusSectionFragment extends Fragment implements
        Filterable,
        OnItemClickListener, OnItemLongClickListener {

    protected List<TaskH> listTaskH;
    public static ListHandler handler;
    private static boolean isStatusOpen = false;
    public ToDoList toDoList;
    public int searchType;
    public String searchContent;
    public ArrayAdapter<TaskH> statusListAdapter;
    LayoutInflater inflater;
    private GridView gridView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public StatusSectionFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        gridView = (GridView) view
                .findViewById(R.id.gridStatus);
        List<SurveyHeaderBean> list = null;
        if (ToDoList.getListOfSurveyStatus() == null) {
            try {
                list = new ArrayList<SurveyHeaderBean>();
                for (TaskH h : listTaskH) {
                    list.add(new SurveyHeaderBean(h));
                }
                ToDoList.setListOfSurveyStatus(list);
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV) {
                    e.printStackTrace();
                    System.out.println("list failed");
                }
                ACRA.getErrorReporter().putCustomData("errorOnViewCreated", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorOnViewCreated", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat getList"));
                String[] msg = {getActivity().getString(R.string.msgConnectionFailed)};
                String alert = Tool.implode(msg, "\n");
                Toast.makeText(getActivity(), alert, Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        } else {
            list = ToDoList.getListOfSurveyStatus();
        }
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

        statusListAdapter = new StatusArrayAdapter(getActivity().getApplicationContext(), listTaskH);
        gridView.setAdapter(statusListAdapter);

        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainMenuActivity.setDrawerCounter();
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("errorOnViewCreated", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorOnViewCreated", Tool.getSystemDateTime().toLocaleString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer"));
                }
            }
        });
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
    public void onResume() {
        super.onResume();
        isStatusOpen = true;
        if (Global.APPLICATION_ORDER.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
            getActivity().getActionBar().setTitle(getActivity().getString(R.string.title_mn_tasklist));
        } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
            if (MainMenuActivity.mnTaskList == null) {
                getActivity().getActionBar().setTitle("Status Task");
            }
        }
        if (toDoList == null) toDoList = new ToDoList(getActivity());
        listTaskH = toDoList.getListTaskInStatus(searchType, searchContent);
        List<SurveyHeaderBean> list = new ArrayList<SurveyHeaderBean>();
        for (TaskH h : listTaskH) {
            list.add(new SurveyHeaderBean(h));
        }
        ToDoList.setListOfSurveyStatus(list);
        statusListAdapter = new StatusArrayAdapter(getActivity().getApplicationContext(), listTaskH);
        statusListAdapter.notifyDataSetChanged();
        try {
            try {
                MainMenuActivity.setDrawerCounter();
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
                ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorOnResume", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer"));
            }
            gridView.setAdapter(statusListAdapter);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnResume", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toDoList = new ToDoList(getActivity().getApplicationContext());
        listTaskH = toDoList.getListTaskInStatus(searchType, searchContent);

        this.inflater = inflater;
        handler = new ListHandler();
        View rootView = inflater.inflate(
                R.layout.status_layout, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        // BEGIN_INCLUDE (change_colors)
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.tv_light),
                getResources().getColor(R.color.tv_normal),
                getResources().getColor(R.color.tv_gray),
                getResources().getColor(R.color.tv_darker));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                initiateRefresh();
            }
        });
        return rootView;
    }

    private void initiateRefresh() {
        new RefreshBackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void onRefreshComplete(List<TaskH> result) {
        // Remove all items from the ListAdapter, and then replace them with the new items
        try {
            statusListAdapter.clear();
            for (TaskH taskH : result) {
                statusListAdapter.add(taskH);
            }
            statusListAdapter.notifyDataSetChanged();
        } catch (UnsupportedOperationException e) {
            try {
                statusListAdapter = new StatusArrayAdapter(getActivity().getApplicationContext(), listTaskH);
                statusListAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                e.printStackTrace();
                ACRA.getErrorReporter().putCustomData("errorOnRefreshComplete", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorOnRefreshComplete", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat notify Data Set Changed"));
            }
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                statusListAdapter = new StatusArrayAdapter(getActivity().getApplicationContext(), listTaskH);
                statusListAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                e.printStackTrace();
                ACRA.getErrorReporter().putCustomData("errorOnRefreshComplete", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorOnRefreshComplete", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat notify Data Set Changed"));
            }
        }
        // Stop the refreshing indicator
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                if (constraint.length() == 0) {
                    result.count = 0;
                    result.values = null;
                    return result;
                }
                listTaskH = toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, (String) constraint);
                result.values = listTaskH;
                result.count = listTaskH.size();
                searchType = ToDoList.SEARCH_BY_ALL;
                searchContent = (String) constraint;
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count != 0) {
                    List<SurveyHeaderBean> list = new ArrayList<SurveyHeaderBean>();
                    for (TaskH h : ((List<TaskH>) results.values)) {
                        list.add(new SurveyHeaderBean(h));
                    }
                    statusListAdapter = new StatusArrayAdapter(getActivity(), listTaskH);
                    gridView.setAdapter(statusListAdapter);
                } else {
                    gridView.setAdapter(statusListAdapter);
                }
                statusListAdapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isStatusOpen = false;
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            ACRA.getErrorReporter().putCustomData("errorOnDetach", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnDetach", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set FragmentDeclared, NoSuchFieldExcp"));
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            ACRA.getErrorReporter().putCustomData("errorOnDetach", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnDetach", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set FragmentDeclared, IllegalAccess"));
            throw new RuntimeException(e);
        }
    }

    public void updateListItem() {
        try {
            if (statusListAdapter != null) {
                statusListAdapter.clear();
                if (ToDoList.getListOfSurveyStatus() != null &&
                        ToDoList.getListOfSurveyStatus().size() > 0) {
                    for (SurveyHeaderBean bean : ToDoList.getListOfSurveyStatus()) {
                        statusListAdapter.add(bean);
                    }
                }
                if (gridView != null) {
                    statusListAdapter.notifyDataSetChanged();
                    gridView.setAdapter(statusListAdapter);
                    initiateRefresh();
                }

            }
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                statusListAdapter.notifyDataSetChanged();
                gridView.setAdapter(statusListAdapter);
            } catch (Exception e2) {
                e.printStackTrace();
                ACRA.getErrorReporter().putCustomData("errorUpdateListItem", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorUpdateListItem", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat notify data set changed"));
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (position < listTaskH.size()) {
            TaskH item = listTaskH.get(position);

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
                            getActivity().getString(R.string.task_cant_seen),
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
                e.printStackTrace();
                ACRA.getErrorReporter().putCustomData("errorOnItemClick", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorOnItemClick", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat event on Click, check Scheme"));
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v,
                                   int position, long id) {
        try {
            TaskH item = ToDoList.getListOfSurveyStatus().get(position);
        } catch (IndexOutOfBoundsException e) {
            listTaskH = toDoList.getListTaskInStatus(searchType, searchContent);
            List<SurveyHeaderBean> list = new ArrayList<SurveyHeaderBean>();
            for (TaskH h : listTaskH) {
                list.add(new SurveyHeaderBean(h));
            }
            ToDoList.setListOfSurveyStatus(null);
            ToDoList.setListOfSurveyStatus(list);
            e.printStackTrace();
            ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat event onLongClick"));
        }
        try {
            final TaskH item = ToDoList.getListOfSurveyStatus().get(position);
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
                                                e.printStackTrace();
                                                ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", e.getMessage());
                                                ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", Tool.getSystemDateTime().toLocaleString());
                                                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat event onLongClick"));
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                    }
                                    if (StatusSectionFragment.handler != null)
                                        StatusSectionFragment.handler.sendEmptyMessage(0);
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
                    ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", Tool.getSystemDateTime().toLocaleString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat event onLongClick"));
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

                                                    try {
                                                        MainMenuActivity.setDrawerCounter();
                                                    } catch (Exception e) {
                                                        FireCrash.log(e);
                                                        e.printStackTrace();
                                                        ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", e.getMessage());
                                                        ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", Tool.getSystemDateTime().toLocaleString());
                                                        ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set DrawerCounter"));
                                                    }
                                                    listTaskH = toDoList.getListTaskInStatus(searchType, searchContent);
                                                    statusListAdapter = new StatusArrayAdapter(getActivity(), listTaskH);
                                                    gridView.setAdapter(statusListAdapter);
                                                    statusListAdapter.notifyDataSetChanged();
                                                } catch (Exception e) {
                                                    FireCrash.log(e);
                                                    e.printStackTrace();
                                                    ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", e.getMessage());
                                                    ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", Tool.getSystemDateTime().toLocaleString());
                                                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat notifyDataSetChanged"));
                                                }
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
                                if (StatusSectionFragment.handler != null)
                                    StatusSectionFragment.handler.sendEmptyMessage(0);
                                listTaskH = toDoList.getListTaskInStatus(searchType, searchContent);
                                statusListAdapter = new StatusArrayAdapter(getActivity(), listTaskH);
                                gridView.setAdapter(statusListAdapter);
                                statusListAdapter.notifyDataSetChanged();
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        try {
                                            MainMenuActivity.setDrawerCounter();
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                            e.printStackTrace();
                                            ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", e.getMessage());
                                            ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", Tool.getSystemDateTime().toLocaleString());
                                            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set DrawerCounter"));
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
                                if (StatusSectionFragment.handler != null)
                                    StatusSectionFragment.handler.sendEmptyMessage(0);
                                listTaskH = toDoList.getListTaskInStatus(searchType, searchContent);
                                statusListAdapter = new StatusArrayAdapter(getActivity(), listTaskH);
                                gridView.setAdapter(statusListAdapter);
                                statusListAdapter.notifyDataSetChanged();
                                dialogBuilder.dismiss();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            MainMenuActivity.setDrawerCounter();
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                            e.printStackTrace();
                                            ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", e.getMessage());
                                            ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", Tool.getSystemDateTime().toLocaleString());
                                            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set DrawerCounter"));
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
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnItemLongClick", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat get item"));
        }

        return true;
    }

    private class RefreshBackgroundTask extends AsyncTask<Void, Void, List<TaskH>> {

        static final int TASK_DURATION = 2 * 1000; // 2 seconds

        @Override
        protected List<TaskH> doInBackground(Void... params) {
            // Sleep for a small amount of time to simulate a background-task
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
                ACRA.getErrorReporter().putCustomData("errorOnRefresh", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorOnRefresh", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Thread.sleep"));
            }
            listTaskH = toDoList.getListTaskInStatus(searchType, searchContent);
            List<SurveyHeaderBean> list = new ArrayList<SurveyHeaderBean>();
            for (TaskH h : listTaskH) {
                list.add(new SurveyHeaderBean(h));
            }
            ToDoList.setListOfSurveyStatus(null);
            ToDoList.setListOfSurveyStatus(list);

            // Return a new random list of cheeses
            return listTaskH;
        }

        @Override
        protected void onPostExecute(List<TaskH> result) {
            super.onPostExecute(result);
            try {
                MainMenuActivity.setDrawerCounter();
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
                ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorOnResume", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer"));
            }
            onRefreshComplete(result);
        }

    }

    public class StatusArrayAdapter extends ArrayAdapter<TaskH> {


        private Context mContext;
        private List<TaskH> list;

        // references to our images
        public StatusArrayAdapter(Context context,
                                  List<TaskH> list) {
            super(context, R.layout.status_item_layout, list);
            mContext = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public SurveyHeaderBean getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) v = inflater.inflate(R.layout.status_item_layout, null);
            try {
                ImageView imgStatus = (ImageView) v.findViewById(R.id.imgStatus);
                TextView txtId = (TextView) v.findViewById(R.id.txtTaskID);
                TextView txtName = (TextView) v.findViewById(R.id.txtName);
                TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
                TextView txtStatus = (TextView) v.findViewById(R.id.txtStatusTask);
                TextView txtScheme = (TextView) v.findViewById(R.id.txtScheme);
                TaskH bean = list.get(position);
                if (TaskHDataAccess.STATUS_SEND_PENDING.equals(bean.getStatus())) {
                    imgStatus.setImageResource(R.drawable.ic_pending);
                } else if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(bean.getStatus())) {
                    imgStatus.setImageResource(R.drawable.ic_save_status);
                } else if (TaskHDataAccess.STATUS_SEND_UPLOADING.equals(bean.getStatus())) {
                    imgStatus.setImageResource(R.drawable.ic_uploading);
                }
                txtId.setText(bean.getTask_id());
                txtName.setText(bean.getCustomer_name());
                txtStatus.setText(bean.getStatus());
                if (bean.getScheme() != null)
                    txtScheme.setText(bean.getScheme().getForm_id());
                // bong 6 apr 15 - add txtDate below txtname
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy - HH:mm");
                String draftDate = sdf.format(bean.getDraft_date());
                txtDate.setText(draftDate);
                txtDate.setTextSize(10);
                txtId.setSelected(true);
                txtName.setSelected(true);

            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
                ACRA.getErrorReporter().putCustomData("errorGetView", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetView", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat binding data to view"));
            }
            return v;
        }
    }

    public class ListHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (isStatusOpen) {
                updateListItem();
            }
        }
    }
}
