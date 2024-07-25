package com.adins.mss.base.dynamicform.newlead;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.FormBean;
import com.adins.mss.base.dynamicform.JsonRequestOpenStartTask;
import com.adins.mss.base.dynamicform.QuestionSetTask;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.todo.form.JsonRequestScheme;
import com.adins.mss.base.todo.form.JsonResponseScheme;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Account;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.AccountDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.DaoException;

/**
 * Created by olivia.dg on 11/22/2017.
 */

public class NewLeadFragment extends Fragment {

    public static final String SURVEY_MODE = "com.adins.mss.base.dynamicform.newlead.SURVEY_MODE";
    public static final String SURVEY_HEADER = "com.adins.mss.base.dynamicform.newlead.SURVEY_HEADER";
    public static final String SURVEY_UUID = "com.adins.mss.base.dynamicform.newlead.SURVEY_UUID";
    public static final String CUSTOMER_NOTES = "com.adins.mss.base.dynamicform.newlead.CUSTOMER_NOTES";
    public static final String UUID_ACCOUNT = "com.adins.mss.base.dynamicform.newlead.UUID_ACCOUNT";

    private AppCompatSpinner spinner;
    private TextView txtNotes;
    private Button btnStart;
    private Button btnView;

    private List<Account> accountList;
    public static SurveyHeaderBean header;
    private Bundle bundleParams;
    private Context context;
    Scheme lastUpdateScheme;
    private CheckScheme checkScheme;

    public static Boolean isEditable=false;
    public List<TaskH> listTaskH;
    public ToDoList toDoList;
    private RefreshBackgroundTask backgroundTask;
    public static boolean viewTask = false;
    private FirebaseAnalytics screenName;

    public static NewLeadFragment create(Bundle data) {
        NewLeadFragment fragment = new NewLeadFragment();
        fragment.setArguments(data);
        Global.IS_NEWLEAD = data.getBoolean(Global.BUND_KEY_IS_NEWLEAD);
        return fragment;
    }

    public static NewLeadFragment create(SurveyHeaderBean header) {
        NewLeadFragment.header = null;
        Bundle bundle = new Bundle();

        bundle.putString(UUID_ACCOUNT, header.getUuid_account());
        bundle.putString(CUSTOMER_NOTES, header.getNotes());
        String status = header.getStatus();
        int mode = 0;
        if(status.equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)||
                status.equals(TaskHDataAccess.STATUS_SEND_DOWNLOAD)||
                status.equals(TaskHDataAccess.STATUS_SEND_PENDING)||
                status.equals(TaskHDataAccess.STATUS_SEND_UPLOADING)||
                status.equals(TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD)||
                status.equals(TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD))
            mode = Global.MODE_SURVEY_TASK;
        else if(status.equals(TaskHDataAccess.STATUS_SEND_SENT)||
                status.equals(TaskHDataAccess.STATUS_SEND_REJECTED)){
            mode = Global.MODE_VIEW_SENT_SURVEY;
        }else if (status.equals(TaskHDataAccess.STATUS_TASK_VERIFICATION)||
                status.equals(TaskHDataAccess.STATUS_TASK_APPROVAL)) {
            mode = Global.MODE_SURVEY_TASK;
        }

        else{
            if(header.getStatus().equals(TaskHDataAccess.STATUS_SEND_INIT)&&
                    header.getPriority()!=null){
                mode = Global.MODE_SURVEY_TASK;
            }else mode = Global.MODE_NEW_SURVEY;
        }

        bundle.putInt(SURVEY_MODE, mode);
        bundle.putSerializable(SURVEY_HEADER, header);
        bundle.putString(SURVEY_UUID, header.getUuid_scheme());
        bundle.putBoolean(Global.BUND_KEY_IS_NEWLEAD, false);

        return create(bundle);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        setHasOptionsMenu(true);
        Utility.freeMemory();
        bundleParams = getArguments();
        context = activity;
        header = (SurveyHeaderBean) bundleParams.getSerializable(SURVEY_HEADER);
        if ((bundleParams.getInt(SURVEY_MODE) != Global.MODE_VIEW_SENT_SURVEY)) {
            checkScheme = new CheckScheme();
            checkScheme.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else{
            Global.getSharedGlobal().setSchemeIsChange(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_lead, container, false);
        screenName = FirebaseAnalytics.getInstance(getActivity());

        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_newlead));

        spinner = (AppCompatSpinner) view.findViewById(R.id.spinnerAccount);
        txtNotes = (TextView) view.findViewById(R.id.txtNotes);
        btnStart = (Button) view.findViewById(R.id.btnStart);
        btnView = (Button) view.findViewById(R.id.btnViewTask);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditable = false;
                GlobalData.getSharedGlobalData().setDoingTask(true);
                gotoNextDynamicForm();
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.getSharedGlobal().setIsViewer(true);
                viewTask = true;
                header = (SurveyHeaderBean) bundleParams.getSerializable(SURVEY_HEADER);
                cancelCheckScheme();
                Bundle extras = new Bundle();
                extras.putInt(Global.BUND_KEY_MODE_SURVEY, bundleParams.getInt(SURVEY_MODE));
                extras.putSerializable(Global.BUND_KEY_SURVEY_BEAN, bundleParams.getSerializable(SURVEY_HEADER));
                QuestionSetTask task =new QuestionSetTask(getActivity(), extras);
                task.execute();
            }
        });

        header = (SurveyHeaderBean) bundleParams.getSerializable(SURVEY_HEADER);

        if (bundleParams.getInt(SURVEY_MODE) == Global.MODE_SURVEY_TASK ||
                bundleParams.getInt(SURVEY_MODE) == Global.MODE_VIEW_SENT_SURVEY) {
            if(!(TaskHDataAccess.STATUS_SEND_INIT.equals(header.getIs_prepocessed()) &&
                    TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(header.getStatus()))) {
                txtNotes.setText(header.getNotes());
                spinner.setEnabled(false);
                txtNotes.setEnabled(false);
            }
        }

        if(TaskHDataAccess.STATUS_SEND_PENDING.equals(header.getStatus())||
                TaskHDataAccess.STATUS_SEND_UPLOADING.equals(header.getStatus())){
            btnStart.setVisibility(View.GONE);
        }

        if(header.getPriority()!=null&&header.getPriority().length()>0){
            if(header.getOpen_date()==null){
                header.setOpen_date(Tool.getSystemDateTime());
                new SendOpenReadTaskH(getActivity(), header).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

        toDoList = new ToDoList(getActivity());
        listTaskH = toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, "");

        isEditable = false;

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountList = new ArrayList<>();
        accountList.clear();
        List<Account> listAcc = AccountDataAccess.getAll(getActivity());
        if (listAcc != null)
            accountList.addAll(listAcc);
        AccountAdapter accountSpinner = new AccountAdapter(getContext(), R.layout.spinner_style2, accountList);
        spinner.setAdapter(accountSpinner);

        if (bundleParams.getInt(SURVEY_MODE) == Global.MODE_SURVEY_TASK ||
                bundleParams.getInt(SURVEY_MODE) == Global.MODE_VIEW_SENT_SURVEY) {
            if(!(TaskHDataAccess.STATUS_SEND_INIT.equals(header.getIs_prepocessed()) &&
                    TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(header.getStatus()))) {
                Account account = AccountDataAccess.getOne(getActivity(), header.getUuid_account());
                spinner.setSelection(accountList.indexOf(account));
                txtNotes.setText(header.getNotes());
            }
        }

        if (bundleParams.getInt(SURVEY_MODE) == Global.MODE_VIEW_SENT_SURVEY) {
            btnStart.setVisibility(View.GONE);
            btnView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_new_lead), null);

        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_newlead));

        if (listTaskH != null) {
            initiateRefresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelCheckScheme();
    }

    private void gotoNextDynamicForm() {
        Global.getSharedGlobal().setIsViewer(false);
        if(bundleParams.getInt(SURVEY_MODE) != Global.MODE_VIEW_SENT_SURVEY || (bundleParams.getInt(SURVEY_MODE) == Global.MODE_VIEW_SENT_SURVEY && isEditable)) {
            cancelCheckScheme();
            Scheme scheme = null;
            try {
                String uuidScheme = header.getUuid_scheme();
                scheme = SchemeDataAccess.getOne(getActivity(), uuidScheme);
            } catch (DaoException e) {
                ACRA.getErrorReporter().putCustomData("errorGetScheme", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetSchemeTime", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Get Scheme"));
            } catch (Exception e) {
                ACRA.getErrorReporter().putCustomData("errorGetScheme", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetSchemeTime", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Get Scheme"));
            }

            if(scheme!=null) {
                header.setScheme(scheme);
                FormBean formBean = new FormBean(scheme);
                if(lastUpdateScheme!=null){
                    formBean = new FormBean(lastUpdateScheme);
                }
                Account account = (Account) spinner.getSelectedItem();
                String uuidAccount = account.getUuid_account();
                header.setUuid_account(uuidAccount);
                header.setNotes(txtNotes.getText().toString());
                if (header.getStart_date() == null) {
                    if (header.getPriority() != null && header.getPriority().length() > 0) {
                    } else {
                        header.setStart_date(Tool.getSystemDateTime());
                    }
                }
                header.setForm(formBean);
                header.setIs_preview_server(formBean.getIs_preview_server());

                Bundle extras = new Bundle();
                if(isEditable){
                    bundleParams.putInt(SURVEY_MODE, Global.MODE_SURVEY_TASK);
                    header.setStatus(TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                }
                extras.putInt(Global.BUND_KEY_MODE_SURVEY, bundleParams.getInt(SURVEY_MODE));
                extras.putString(Global.BUND_KEY_UUID_TASKH, header.getUuid_task_h());
                extras.putSerializable(Global.BUND_KEY_SURVEY_BEAN, header);
                extras.putSerializable(Global.BUND_KEY_FORM_BEAN, formBean);

                QuestionSetTask task = new QuestionSetTask(getActivity(), extras);
                task.execute();
                onDetach();
            }else{
                Toast.makeText(getActivity(), getActivity().getString(R.string.request_error),
                        Toast.LENGTH_SHORT).show();
                doBack();
            }
        }
    }

    private void initiateRefresh() {
        cancelRefreshTask();
        backgroundTask = new RefreshBackgroundTask();
        backgroundTask.execute();
    }

    private void cancelRefreshTask(){
        if(backgroundTask!=null){
            backgroundTask.cancel(true);
            backgroundTask=null;
        }
    }

    public class CheckScheme extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String uuidScheme = header.getUuid_scheme();
                Scheme schema = SchemeDataAccess.getOne(getActivity(), uuidScheme);
                if(schema!=null){
                    JsonRequestScheme requestScheme = new JsonRequestScheme();
                    requestScheme.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    requestScheme.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    requestScheme.setUuid_scheme(schema.getUuid_scheme());
                    requestScheme.setTask(Global.TASK_GETONE);

                    String json = GsonHelper.toJson(requestScheme);
                    String url = GlobalData.getSharedGlobalData().getURL_GET_SCHEME();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;

                    //Firebase Performance Trace HTTP Request
                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    try {
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", e.getMessage());
                        ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", Tool.getSystemDateTime().toLocaleString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat request to Server"));
                        if(Global.IS_DEV)
                            e.printStackTrace();
                    }
                    if(serverResult!=null && serverResult.isOK()){
                        try {
                            String result = serverResult.getResult();
                            JsonResponseScheme responseScheme = GsonHelper.fromJson(result, JsonResponseScheme.class);
                            List<Scheme> schemes = responseScheme.getListScheme();

                            Scheme scheme= schemes.get(0);
                            try {
                                Date new_last_update = scheme.getScheme_last_update();
                                Date temp_last_update = Global.TempScheme.get(scheme.getUuid_scheme());
                                lastUpdateScheme = scheme;
                                Global.getSharedGlobal().setSchemeIsChange(new_last_update.after(temp_last_update));
                            } catch (Exception e) {
                                FireCrash.log(e);
                            }

                        } catch (Exception e) {
                            ACRA.getErrorReporter().putCustomData("errorGetScheme", e.getMessage());
                            ACRA.getErrorReporter().putCustomData("errorGetSchemeTime", Tool.getSystemDateTime().toLocaleString());
                            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Get Scheme"));
                            Global.getSharedGlobal().setSchemeIsChange(false);
                        }
                    }
                    else{
                        Global.getSharedGlobal().setSchemeIsChange(false);
                    }
                }
            } catch (Exception e) {
                ACRA.getErrorReporter().putCustomData("errorGetScheme", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetSchemeTime", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Get Scheme"));
                Global.getSharedGlobal().setSchemeIsChange(false);
            }
            return true;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Global.getSharedGlobal().setSchemeIsChange(false);
        }
    }

    private class RefreshBackgroundTask extends AsyncTask<Void, Void, List<TaskH>> {
        static final int TASK_DURATION = 2 * 1000;

        @Override
        protected List<TaskH> doInBackground(Void... params) {
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            listTaskH.clear();
            listTaskH.addAll(toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, ""));
            ToDoList.setListOfSurveyStatus(null);
            List<SurveyHeaderBean> list = new ArrayList<>();
            for(TaskH h:listTaskH){
                list.add(new SurveyHeaderBean(h));
            }
            ToDoList.setListOfSurveyStatus(list);

            return listTaskH;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(List<TaskH> result) {
            super.onPostExecute(result);
        }

    }

    public static class SendOpenReadTaskH extends AsyncTask<Void, Void, Void>{
        private TaskH taskH;
        private SurveyHeaderBean bean;
        private Context context;
        private Activity activity;
        public SendOpenReadTaskH(Activity activity, SurveyHeaderBean bean){
            this.bean=bean;
            this.context=activity;
            this.activity =activity;
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            if(Tool.isInternetconnected(context)) {
                taskH = bean.getTaskH();

                try {
                    if(null==taskH.getFlag()){
                        taskH.setFlag("");
                    }
                } catch (NullPointerException e){
                    taskH.setFlag("");
                }

                if(taskH.getFlag().equalsIgnoreCase("")) {
                    JsonRequestOpenStartTask task = new JsonRequestOpenStartTask();
                    task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    task.addImeiAndroidIdToUnstructured();
                    task.setTaskH(taskH);

                    String json = GsonHelper.toJson(task);

                    String url = GlobalData.getSharedGlobalData().getURL_SUBMITOPENREADTASK();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                    HttpConnectionResult serverResult = null;

                    //Firebase Performance Trace HTTP Request
                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    try {
                        serverResult = httpConn.requestToServer(url, json);
                        Utility.metricStop(networkMetric, serverResult);
                        String response = serverResult.getResult();
                        MssResponseType responseType = GsonHelper.fromJson(response, MssResponseType.class);
                        if (responseType.getStatus().getCode() == 0) {
                            try {
                                TaskHDataAccess.addOrReplace(context, taskH);
                            } catch (Exception e) {
                                ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", e.getMessage());
                                ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", Tool.getSystemDateTime().toLocaleString());
                                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert json dari Server dan addOrReplace taskH"));
                            }
                        }
                    } catch (Exception e) {
                        ACRA.getErrorReporter().putCustomData("errorRequestToServer", e.getMessage());
                        ACRA.getErrorReporter().putCustomData("errorRequestToServer", Tool.getSystemDateTime().toLocaleString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat request to server"));
                        if (Global.IS_DEV)
                            e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    public class AccountAdapter extends ArrayAdapter<Account> {
        private Context context;
        private List<Account> values;

        public AccountAdapter(Context context, int resource, List<Account> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values = objects;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Account getItem(int position) {
            return values.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style2, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getAccount_name());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getAccount_name());
            return label;
        }
    }

    public static void doBack(){
        try {
            if(NewMainActivity.fragmentManager.getBackStackEntryCount()>0) {
                NewMainActivity.fragmentManager.popBackStack();
                GlobalData.getSharedGlobalData().setDoingTask(false);
            }
        } catch (Exception e) {
            ACRA.getErrorReporter().putCustomData("errordDoBack", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errordDoBack", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception do back in Customer Fragment"));
        }

    }

    public void cancelCheckScheme(){
        try {
            if (checkScheme != null) {
                checkScheme.cancel(true);
                checkScheme = null;
            }
        }catch (Exception e){
            ACRA.getErrorReporter().putCustomData("errorGetScheme", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorGetSchemeTime", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Get Scheme"));
            e.printStackTrace();
        }
    }
}
