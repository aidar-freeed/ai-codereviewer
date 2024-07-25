package com.adins.mss.base.todolist.form.followup;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.todolist.form.OnTaskListClickListener;
import com.adins.mss.base.todolist.form.TaskListTabInteractor;
import com.adins.mss.base.todolist.form.TasklistView;
import com.adins.mss.base.todolist.form.helper.TaskFilterParam;
import com.adins.mss.base.todolist.form.helper.TaskPlanFilterObservable;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FollowUpTabFragment extends Fragment implements TaskListTabInteractor.TabPage {

    private RecyclerView recyclerView;
    private FollowUpAdapter followUpAdapter;
    private List<TaskH> taskHList;
    private Menu mainMenu;

    public TaskListTabInteractor getTabInteractor() {
        return tabInteractor;
    }

    public void setTabInteractor(TaskListTabInteractor tabInteractor) {
        this.tabInteractor = tabInteractor;
    }

    private TaskListTabInteractor tabInteractor;

    public FollowUpTabFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow_up, container, false);
        recyclerView = view.findViewById(R.id.listTaskRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskHList = new ArrayList<>();
        followUpAdapter = new FollowUpAdapter(getActivity(), taskHList, new OnTaskListClickListener() {
            @Override
            public void onItemClickListener(TaskH taskH, int position) {
            }

            @Override
            public void onItemLongClickListener(TaskH taskH, int position) {
            }
        }, "param");

        recyclerView.setAdapter(followUpAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menuMore).setVisible(false);

        setToolbar();
    }

    private void setToolbar() {
        getActivity().findViewById(R.id.search).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle("Menu");
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
    public String getTabPageName() {
        return TasklistView.FOLLOWUP_TAB_PAGE_TAG;

    }

    @Override
    public void onEnterPage() {

    }

    @Override
    public void onLeavePage() {

    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadTasksAsyncTask().execute();
    }

    private class LoadTasksAsyncTask extends AsyncTask<Void, Void, List<TaskListResponse>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.progressWait), true);
        }

        @Override
        protected List<TaskListResponse> doInBackground(Void... voids) {
            try {
                if(Tool.isInternetconnected(getContext())){
                    String result = "";
                    MssRequestType requestType = new MssRequestType();
                    requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                    String json = GsonHelper.toJson(requestType);
                    String url = GlobalData.getSharedGlobalData().getURL_GET_FOLLOW_UP_LIST();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new  HttpCryptedConnection(getContext(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);

                    if(serverResult != null){
                        if(serverResult .isOK()){
                            try {
                                result = serverResult.getResult();
                                FollowUpTaskListResponse response = GsonHelper.fromJson(result, FollowUpTaskListResponse.class);
                                return response.getListFollowUpTask();
                            }catch (Exception e){
                                FireCrash.log(e);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }catch (Exception e){
                FireCrash.log(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<TaskListResponse> tasks) {
            super.onPostExecute(tasks);
            taskHList.clear();
            List<FollowUpTask> followUpTasks = new ArrayList<>();
            if(tasks != null){
                for(TaskListResponse taskListResponse : tasks){
                    FollowUpTask dummy = new FollowUpTask();
                    TaskH dummyTaskH = new TaskH();

                    dummyTaskH.setUuid_task_h(taskListResponse.getUuidTaskH());
                    dummyTaskH.setCustomer_name(taskListResponse.getCustomerName());
                    dummyTaskH.setAppl_no(taskListResponse.getAgreementNo());
                    dummyTaskH.setCustomer_address(taskListResponse.getCustomerAddress());
                    dummyTaskH.setCustomer_phone(taskListResponse.getCustomerPhone());
                    dummyTaskH.setOd(taskListResponse.getOverdueDays());
                    dummyTaskH.setInst_no(taskListResponse.getInstallmentNo());
                    dummyTaskH.setAmt_due(taskListResponse.getAmountDue());
                    dummyTaskH.setStatus("Password");
                    dummy.setTglJanjiBayar(taskListResponse.getTglJanjiBayar());
                    dummy.setFlagTask(taskListResponse.getFlagTask());

                    dummy.setFollowUpTaskHeader(dummyTaskH);
                    followUpTasks.add(dummy);
                }
            }

            followUpAdapter.setFollowUpTasks(followUpTasks);
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
