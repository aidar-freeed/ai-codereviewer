package com.adins.mss.base.todolist.form;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.Backup;
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
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrioritySectionFragment extends Fragment implements
        OnItemClickListener,
        Filterable {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    /* STATUS */
    public static final String STATUS_SEND_INIT = "New";
    public static final String STATUS_SEND_DOWNLOAD = "Download";
    /* PRIORITY */
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_REMINDER = "REMINDER";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_NORMAL = "NORMAL";
    public static final String PRIORITY_LOW = "LOW";
    private List<TaskH> listTaskH;
    private static ArrayAdapter<TaskH> priorityListAdapter;
    public ToDoList toDoList;
    public int searchType;
    public String searchContent;
    protected Spinner spinnerSearch;
    LayoutInflater inflater;
   private GridView gridView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView priorityCounter;

    private RefreshBackgroundTask backgroundTask;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = (GridView) view.findViewById(R.id.gridPriority);
        priorityListAdapter = new ImageAdapter(getActivity()
                .getApplicationContext(), listTaskH);

        priorityCounter = (TextView) view.findViewById(R.id.priorityCounter);
        spinnerSearch = (Spinner) view.findViewById(R.id.priorityViewBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.cbPriorityBy, R.layout.spinner_style);

        spinnerSearch.setAdapter(adapter);
        spinnerSearch
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View v,
                                               int position, long id) {
                        long itemSearchBy = spinnerSearch
                                .getItemIdAtPosition(position);
                        if (itemSearchBy == 0) {
                            // by all
                            cancelRefreshTask();
                            listTaskH = toDoList.getListTaskInPriority(
                                    searchType, searchContent);
                            priorityListAdapter = new ImageAdapter(
                                    getActivity().getApplicationContext(),
                                    listTaskH);
                            priorityListAdapter.notifyDataSetChanged();
                            gridView.setAdapter(priorityListAdapter);
                            long counter = listTaskH.size();
                            priorityCounter.setText(getString(R.string.task_count)+ counter);
                        } else if (itemSearchBy == 1) {
                            // by High Priority
                            cancelRefreshTask();
                            listTaskH = toDoList.getListTaskInHighPriority();
                            priorityListAdapter = new ImageAdapter(
                                    getActivity().getApplicationContext(),
                                    listTaskH);
                            priorityListAdapter.notifyDataSetChanged();
                            gridView.setAdapter(priorityListAdapter);
                            long counter = listTaskH.size();
                            priorityCounter.setText(getString(R.string.task_count)+counter);
                        } else if (itemSearchBy == 2) {
                            // by Normal Priority
                            cancelRefreshTask();
                            listTaskH = toDoList.getListTaskInNormalPriority();
                            priorityListAdapter = new ImageAdapter(
                                    getActivity().getApplicationContext(),
                                    listTaskH);
                            priorityListAdapter.notifyDataSetChanged();
                            gridView.setAdapter(priorityListAdapter);
                            long counter = listTaskH.size();
                            priorityCounter.setText(getString(R.string.task_count)+counter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        //EMPTY
                    }

                });

        gridView.setAdapter(priorityListAdapter);

        gridView.setOnItemClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toDoList = new ToDoList(getActivity().getApplicationContext());

        listTaskH = toDoList.getListTaskInPriority(searchType, searchContent);
        ViewMapActivity.setListTaskH(listTaskH);
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.priority_layout, container,
                false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.swiperefresh);

        // BEGIN_INCLUDE (change_colors)
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color
        // resource ids
        mSwipeRefreshLayout.setColorSchemeColors(R.color.tv_light, R.color.tv_normal,
                R.color.tv_dark, R.color.tv_darker);

        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        initiateRefresh();
                    }
                });
        return rootView;
    }

    private void initiateRefresh() {
        cancelRefreshTask();
        backgroundTask = new RefreshBackgroundTask();
        backgroundTask.execute();
    }

    private void cancelRefreshTask() {
        if (backgroundTask != null) {
            backgroundTask.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (toDoList == null) toDoList = new ToDoList(getActivity());
            MainMenuActivity.InitializeGlobalDataIfError(getActivity().getApplicationContext());
            int pos = spinnerSearch.getSelectedItemPosition();

            if (pos == 0) {
                // by all
                listTaskH = toDoList.getListTaskInPriority(searchType,
                        searchContent);
            } else if (pos == 1) {
                // by High Priority
                listTaskH = toDoList.getListTaskInHighPriority();
            } else if (pos == 2) {
                // by Normal Priority
                listTaskH = toDoList.getListTaskInNormalPriority();
            } else {
                listTaskH = toDoList.getListTaskInPriority(searchType,
                        searchContent);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            if (toDoList == null) toDoList = new ToDoList(getActivity());
            listTaskH = toDoList.getListTaskInPriority(searchType,
                    searchContent);
        }
        try {
            priorityListAdapter.clear();
            for (TaskH taskH : listTaskH) {
                priorityListAdapter.add(taskH);
            }
            priorityListAdapter.notifyDataSetChanged();
        } catch (UnsupportedOperationException e) {
            try {
                priorityListAdapter = new ImageAdapter(
                        getActivity().getApplicationContext(),
                        listTaskH);
                priorityListAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                FireCrash.log(e);
            }
        }

        try {
            try {
                MainMenuActivity.setDrawerCounter();
            } catch (Exception e) {
                FireCrash.log(e);
            }
            gridView.setAdapter(priorityListAdapter);
            long counter = listTaskH.size();
            priorityCounter.setText(getString(R.string.task_count) + counter);
        } catch (Exception e) {
            FireCrash.log(e);
        }
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
                                taskHs.addAll(TaskHDataAccess.getAllTaskByStatus(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_SEND_SAVEDRAFT));
                                List<TaskH> needRemoveFromBackup = new ArrayList<>();

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
                                        if(h.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)) {
                                            needRemoveFromBackup.add(h);
                                        }
                                    }
                                }
                                Backup backup = new Backup(activity);
                                backup.removeTask(needRemoveFromBackup);
                            }
                            errMsg   = "noError";
                            return errMsg;
                        } else {
                            errMsg = result;
                            return errMsg;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
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

    private void onRefreshComplete(List<TaskH> result) {
        // Remove all items from the ListAdapter, and then replace them with the
        // new items
        try {
            if (result != null && !result.isEmpty())
                listTaskH = result;
            priorityListAdapter.clear();
            for (TaskH taskH : listTaskH) {
                priorityListAdapter.add(taskH);
            }
            priorityListAdapter.notifyDataSetChanged();
            gridView.setAdapter(priorityListAdapter);
            long counter = listTaskH.size();
            priorityCounter.setText(getString(R.string.task_count) + counter);
        } catch (UnsupportedOperationException e) {
            try {
                priorityListAdapter = new ImageAdapter(
                        getActivity().getApplicationContext(),
                        listTaskH);
                priorityListAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                FireCrash.log(e);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                priorityListAdapter = new ImageAdapter(
                        getActivity().getApplicationContext(),
                        listTaskH);
                priorityListAdapter.notifyDataSetChanged();
            } catch (Exception e2) {
                FireCrash.log(e);
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
                listTaskH = toDoList.getListTaskInPriority(
                        ToDoList.SEARCH_BY_ALL, (String) constraint);
                result.values = listTaskH;
                result.count = listTaskH.size();

                searchType = ToDoList.SEARCH_BY_ALL;
                searchContent = (String) constraint;

                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results.count != 0) {
                    gridView.setAdapter(new ImageAdapter(getActivity(),
                            (List<TaskH>) results.values));
                } else {
                    gridView.setAdapter(new ImageAdapter(getActivity(),
                            new ArrayList<TaskH>()));
                }
            }
        };
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        cancelRefreshTask();
        if (position < listTaskH.size()) {
            TaskH item = listTaskH.get(position);
            try {
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
            } catch (Exception e) {
                FireCrash.log(e);
                String message = getActivity().getString(R.string.task_cant_seen2) + " " + e.getMessage();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("NewApi")
    private class RefreshBackgroundTask extends
            AsyncTask<Void, Void, List<TaskH>> {
        int pos = 0;
        String errMessage = "";

        public RefreshBackgroundTask() {
            try {
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
                errMessage = getTaskListFromServer(getActivity());
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }

            List<TaskH> listTaskH;
            if (pos == 0) {
                // by all
                listTaskH = toDoList.getListTaskInPriority(searchType,
                        searchContent);
            } else if (pos == 1) {
                // by High Priority
                listTaskH = toDoList.getListTaskInHighPriority();
            } else if (pos == 2) {
                // by Normal Priority
                listTaskH = toDoList.getListTaskInNormalPriority();
            } else {
                listTaskH = toDoList.getListTaskInPriority(searchType,
                        searchContent);
            }
            // Return a new random list of cheeses
            return listTaskH;
        }

        @Override
        protected void onPostExecute(List<TaskH> result) {
            super.onPostExecute(result);
            // Tell the Fragment that the refresh has completed
            if (!errMessage.isEmpty() && !errMessage.equals("noError")) {
                Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
            }

            try {
                MainMenuActivity.setDrawerCounter();
            } catch (Exception e) {
                FireCrash.log(e);
            }
            onRefreshComplete(result);
        }

    }

    public class ImageAdapter extends ArrayAdapter<TaskH> {
        private Context mContext;
        private List<TaskH> listTaskH;

        public ImageAdapter(Context c, List<TaskH> listTaskH) {
            super(c, R.layout.priority_item_layout, listTaskH);
            mContext = c;
            this.listTaskH = listTaskH;
        }

        @Override
        public int getCount() {
            return listTaskH.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            v = inflater.inflate(R.layout.priority_item_layout, null);
            LinearLayout layout = (LinearLayout) v
                    .findViewById(R.id.bgGridPriority);
            ImageView imgStatus = (ImageView) v.findViewById(R.id.imgPriority);
            ImageView imgThumb = (ImageView) v
                    .findViewById(R.id.imgStsPriority);
            ImageView imgSLE = (ImageView) v.findViewById(R.id.ImgSLE);
            TextView txtpriority = (TextView) v.findViewById(R.id.txtPriority);
            TextView txtId = (TextView) v.findViewById(R.id.txtTaskID);
            TextView txtName = (TextView) v.findViewById(R.id.txtName);
            TextView txtStatus = (TextView) v.findViewById(R.id.txtStatusTask);
            TextView txtScheme = (TextView) v.findViewById(R.id.txtScheme);
            TextView slaTime = (TextView) v.findViewById(R.id.txtslatime);
            TaskH taskH = listTaskH.get(position);

            txtId.setText(taskH.getTask_id());
            txtName.setText(taskH.getCustomer_name());
            txtStatus.setText(taskH.getStatus());
            txtName.setSelected(true);
            Scheme scheme = taskH.getScheme();
            if (scheme == null) {
                scheme = SchemeDataAccess.getOne(mContext,
                        taskH.getUuid_scheme());
            }
            if (scheme != null)
                txtScheme.setText(scheme.getForm_id());
            String priority = taskH.getPriority();
            if (priority != null) {
                if (PRIORITY_HIGH.equalsIgnoreCase(priority)) {
                    txtpriority.setText("High Priority");
                    layout.setBackgroundResource(R.drawable.highpriority_background);
                    imgStatus.setImageResource(R.drawable.icon_high);
                } else if (PRIORITY_MEDIUM.equalsIgnoreCase(priority)) {
                    txtpriority.setText("Medium Priority");
                    layout.setBackgroundResource(R.drawable.mediumpriority_background);
                    imgStatus.setImageResource(R.drawable.icon_medium);
                } else if (PRIORITY_NORMAL.equalsIgnoreCase(priority)) {
                    txtpriority.setText("Normal Priority");
                    layout.setBackgroundResource(R.drawable.mediumpriority_background);
                    imgStatus.setImageResource(R.drawable.icon_medium);
                } else if (PRIORITY_LOW.equalsIgnoreCase(priority)) {
                    txtpriority.setText("Low Priority");
                    layout.setBackgroundResource(R.drawable.lowpriority_background);
                    imgStatus.setImageResource(R.drawable.icon_low);
                } else if (PRIORITY_REMINDER.equalsIgnoreCase(priority)) {
                    txtpriority.setText("Reminder");
                    layout.setBackgroundResource(R.drawable.highpriority_background);
                    imgStatus.setImageResource(R.drawable.icon_high);
                }
            }

            if (TaskHDataAccess.STATUS_SEND_DOWNLOAD.equals(taskH.getStatus()))
                imgThumb.setImageResource(R.drawable.ic_downloaded);
            else
                imgThumb.setImageResource(R.drawable.ic_undownload);

            int SLA_time = Integer.parseInt(GeneralParameterDataAccess.getOne(
                    mContext,
                    GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                    Global.GS_SLA_TIME).getGs_value());

            java.util.Date assignDate = taskH.getAssignment_date();
            java.util.Date dSlaTime;
            if (assignDate != null) {
                Long assDateMs = assignDate.getTime();

                java.util.Date now = Tool.getSystemDateTime();
                Long nowMs = now.getTime();

                Long SLAMs = SLA_time * Long.valueOf(Global.HOUR);

                Long sla_late = assDateMs + SLAMs;

                dSlaTime = new Date(sla_late);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String slaDate = sdf.format(dSlaTime);
                slaTime.setText(slaDate);
                if (nowMs > sla_late) {
                    imgSLE.setImageResource(R.drawable.light_red);
                    slaTime.setVisibility(View.GONE);
                } else
                    imgSLE.setImageResource(R.drawable.ic_downloaded);
            }

            String application = GlobalData.getSharedGlobalData()
                    .getAuditData().getApplication();
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                imgSLE.setVisibility(View.GONE);
                slaTime.setVisibility(View.GONE);
            }

            return v;
        }
    }
}
